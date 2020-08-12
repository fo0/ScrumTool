package com.fo0.vaadin.scrumtool.ui.views.components.column;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;

import com.fo0.vaadin.scrumtool.ui.broadcast.BroadcasterBoard;
import com.fo0.vaadin.scrumtool.ui.broadcast.BroadcasterColumn;
import com.fo0.vaadin.scrumtool.ui.config.Config;
import com.fo0.vaadin.scrumtool.ui.data.interfaces.IDataOrder;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBCardRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBColumnRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBDataRepository;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBColumn;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBData;
import com.fo0.vaadin.scrumtool.ui.session.SessionUtils;
import com.fo0.vaadin.scrumtool.ui.styles.STYLES;
import com.fo0.vaadin.scrumtool.ui.utils.SpringContext;
import com.fo0.vaadin.scrumtool.ui.utils.StreamUtils;
import com.fo0.vaadin.scrumtool.ui.utils.Utils;
import com.fo0.vaadin.scrumtool.ui.views.KanbanView;
import com.fo0.vaadin.scrumtool.ui.views.components.ToolTip;
import com.fo0.vaadin.scrumtool.ui.views.components.card.CardComponent;
import com.fo0.vaadin.scrumtool.ui.views.dialogs.CreateVotingCardDialog;
import com.fo0.vaadin.scrumtool.ui.views.dialogs.KBConfirmDialog;
import com.fo0.vaadin.scrumtool.ui.views.dialogs.TextDialog;
import com.fo0.vaadin.scrumtool.ui.views.utils.KBViewUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropEffect;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ColumnComponent extends VerticalLayout {

	private static final long serialVersionUID = 8415434953831247614L;

	private KBDataRepository dataRepository = SpringContext.getBean(KBDataRepository.class);
	private KBCardRepository cardRepository = SpringContext.getBean(KBCardRepository.class);
	private KBColumnRepository repository = SpringContext.getBean(KBColumnRepository.class);

	@Getter
	private KanbanView view;
	private TextArea area;
	private H3 h3;
	private Registration broadcasterRegistration;
	private VerticalLayout cards;
	private String id;

	public ColumnComponent(KanbanView view, TKBColumn column) {
		this.view = view;
		setId(column.getId());
		this.id = getId().get();

		setWidth("400px");
		getStyle().set("box-shadow", "var(--material-shadow-elevation-4dp)");
		setSpacing(true);
		setMargin(false);

		h3 = new H3();
		h3.getStyle().set("margin", "unset");
		changeTitle(column.getName(), column.getDataOrder());
		h3.setWidthFull();

		HorizontalLayout captionLayout = new HorizontalLayout(h3);
		captionLayout.setMargin(false);
		captionLayout.setSpacing(false);

		if (KBViewUtils.isAllowed(view.getOptions(), column.getOwnerId())) {
			Button btnEdit = new Button(VaadinIcon.EDIT.create());
			ToolTip.add(btnEdit, "Edit the column");
			btnEdit.addClickListener(e -> {
				//@formatter:off
				new TextDialog("Change Caption", h3.getText(), savedText -> {
					log.info("Edit column: " + getId().get());
					TKBColumn c = repository.findById(id).get();
					c.setName(savedText);
					repository.save(c);
					BroadcasterColumn.broadcast(id, "update");
				}).open();
				//@formatter:on
			});
			captionLayout.add(btnEdit);
			captionLayout.setVerticalComponentAlignment(Alignment.CENTER, btnEdit);

			Button btnShuffle = new Button(VaadinIcon.RANDOM.create());
			ToolTip.add(btnShuffle, "Shuffle the cards");
			btnShuffle.addClickListener(e -> {
				//@formatter:off
				TKBColumn tmp = repository.findById(getId().get()).get();
				
				List<TKBCard> toShuffle = tmp.getCards()
						.stream()
						.collect(Collectors.toList());
				
				Collections.shuffle(toShuffle);
				
				// fix order
				IntStream.range(0, toShuffle.size()).forEachOrdered(counter -> {
					TKBCard cc = toShuffle.get(counter);
					cc.setDataOrder(counter);
				});
				
				tmp.setCards(Sets.newHashSet(toShuffle));
				tmp = repository.save(tmp);
				BroadcasterColumn.broadcast(getId().get(), BroadcasterColumn.MESSAGE_SHUFFLE);
				//@formatter:on
			});
			captionLayout.add(btnShuffle);
			captionLayout.setVerticalComponentAlignment(Alignment.CENTER, btnShuffle);

			Button btnDelete = new Button(VaadinIcon.TRASH.create());
			ToolTip.add(btnDelete, "Delete the column");
			btnDelete.addClickListener(e -> {
				//@formatter:off
				KBConfirmDialog.createQuestion()
					.withCaption("Deleting Column: " + column.getName())
					.withMessage(String.format("This will remove '%s' cards", cards.getComponentCount()))
					.withOkButton(() -> {
						deleteColumn();
					})
					.withCancelButton()
					.open();	
				//@formatter:on
			});
			captionLayout.add(btnDelete);
			captionLayout.setVerticalComponentAlignment(Alignment.CENTER, btnDelete);
		}

		captionLayout.setWidthFull();
		captionLayout.setVerticalComponentAlignment(Alignment.CENTER, h3);

		add(captionLayout);

		VerticalLayout layoutHeader = new VerticalLayout();
		layoutHeader.getStyle().set("flex-shrink", "0");
		layoutHeader.getStyle().set("border", "2px solid black");
		layoutHeader.getStyle().set("overflow", "auto");
		layoutHeader.setWidthFull();
		layoutHeader.setHeight("200px");
		add(layoutHeader);

		area = new TextArea();
		area.setWidthFull();
		area.getStyle().set("flex-grow", "1");

		if (view.getOptions().getMaxCardTextLength() > 0) {
			area.setMaxLength(view.getOptions().getMaxCardTextLength());
			area.setValueChangeMode(ValueChangeMode.EAGER);
			area.addValueChangeListener(e -> {
				if (e.getSource().getValue().length() > view.getOptions().getMaxCardTextLength()) {
					layoutHeader.getStyle().set("border-color", STYLES.COLOR_RED_500);
				} else {
					layoutHeader.getStyle().remove("border-color");
				}
			});
		}

		VerticalLayout txtLayout = new VerticalLayout(area);
		txtLayout.setWidthFull();
		txtLayout.setPadding(false);
		txtLayout.getStyle().set("overflow-y", "auto");
		txtLayout.getStyle().set("flex-grow", "1");

		layoutHeader.add(txtLayout);

		Button btnAdd = new Button("Card", VaadinIcon.PLUS.create());
		ToolTip.add(btnAdd, "Add a Card");
		btnAdd.setWidthFull();
		btnAdd.addClickListener(e -> {
			if (view.getOptions().getMaxCards() > 0) {
				if (cards.getComponentCount() >= view.getOptions().getMaxCards()) {
					Notification.show("Card limit reached", Config.NOTIFICATION_DURATION, Position.MIDDLE);
					return;
				}
			}

			if (StringUtils.isBlank(area.getValue())) {
				Notification.show("Please enter a text", Config.NOTIFICATION_DURATION, Position.MIDDLE);
				return;
			}

			addCardAndSaveAndBroadcast(Utils.randomId(), SessionUtils.getSessionId(), area.getValue());
			area.clear();
			area.focus();
		});

		Button btnCancel = new Button("Clear", VaadinIcon.TRASH.create());
		ToolTip.add(btnCancel, "Clear the Input");
		btnCancel.setWidthFull();
		btnCancel.addClickListener(e -> {
			area.clear();
		});

		Button btnVoting = new Button(VaadinIcon.CLIPBOARD_CHECK.create());
		ToolTip.add(btnVoting, "Create a Voting-Card");
		btnVoting.addClickListener(e -> {
			new CreateVotingCardDialog(view, this, getId().get(), area.getValue()).open();
		});

		HorizontalLayout btnGroup = new HorizontalLayout(btnAdd, btnVoting);
		btnGroup.setSpacing(false);
		HorizontalLayout btnLayout = new HorizontalLayout(btnCancel, btnGroup);
		btnLayout.setWidthFull();
		layoutHeader.add(btnLayout);
		setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, h3);

		cards = new VerticalLayout();
		cards.getStyle().set("overflow", "auto");
		cards.setMargin(false);
		cards.setPadding(false);
		cards.setSpacing(true);
		cards.setHeightFull();

		DropTarget<VerticalLayout> dropTarget = DropTarget.create(cards);
		dropTarget.setDropEffect(DropEffect.MOVE);
		dropTarget.addDropListener(e -> {
			e.getDragSourceComponent().ifPresent(card -> {
				CardComponent droppedCard = (CardComponent) card;
				log.debug("receive dropped card: " + droppedCard.getId().get());
				TKBColumn col = addCardAndSave(Utils.randomId(), droppedCard.getCard());
				BroadcasterColumn.broadcast(getId().get(), BroadcasterColumn.ADD_COLUMN + col.getId());
			});
		});

		add(cards);

	}

	private void addCardAndSaveAndBroadcast(String id, String owner, String message) {
		TKBColumn col = addCardAndSave(id, owner, message);
		BroadcasterColumn.broadcast(getId().get(), BroadcasterColumn.ADD_COLUMN + col.getId());
	}

	private void deleteColumn() {
		log.info("delete column: " + getId().get());
		Notification.show("Deleting Column: " + h3.getText(), Config.NOTIFICATION_DURATION, Position.MIDDLE);
		TKBData c = dataRepository.findByIdFetched(view.getId().get());
		c.removeColumnById(id);
		dataRepository.save(c);
		BroadcasterBoard.broadcast(view.getId().get(), "update");
	}

	public void changeTitle(String string, int order) {
		if (!h3.getText().equals(string)) {
			h3.setText(string);
		}

		if (Config.DEBUG)
			h3.setText(string + " (" + order + ")");
	}

	private TKBColumn addCardAndSave(String randomId, String sessionId, String value) {
		TKBColumn tmp = repository.findById(getId().get()).get();
		TKBCard card = TKBCard.builder().id(randomId).ownerId(sessionId).dataOrder(KBViewUtils.calculateNextPosition(tmp.getCards()))
				.text(value).build();
		tmp.addCard(card);
		repository.save(tmp);
		log.info("add card: {}", randomId);
		return tmp;
	}

	private TKBColumn addCardAndSave(String randomId, TKBCard card) {
		TKBColumn tmp = repository.findById(getId().get()).get();
		card.setId(randomId);
		StreamUtils.stream(card.getLikes()).forEach(e -> {
			e.setId(Utils.randomId());
		});
		tmp.addCard(card);
		repository.save(tmp);
		log.info("add card: {}", randomId);
		return tmp;
	}

	private CardComponent addCardLayout(TKBCard card, boolean sortOrderDesc) {
		CardComponent cc = new CardComponent(view, this, getId().get(), card);

		// for dnd support
		DragSource<CardComponent> dragConfig = DragSource.create(cc);
		dragConfig.addDragStartListener(e -> {
			if (Config.DEBUG) {
				Notification.show("Start Drag Card: " + e.getComponent().getCard().getText());
			}
		});

		dragConfig.addDragEndListener(e -> {
			if (!e.isSuccessful()) {
				Notification.show("Please move the card to a column", 3000, Position.MIDDLE);
				return;
			}

			if (Config.DEBUG) {
				Notification.show("Stop drag Card: " + e.getComponent().getCard().getText());
			}

			Notification.show("Card moved", 3000, Position.BOTTOM_END);
			e.getComponent().deleteCard();
		});

		if (sortOrderDesc) {
			cards.addComponentAsFirst(cc);
		} else {
			cards.add(cc);
		}
		return cc;
	}

	public void addCardAndReload(String cardId) {
		TKBCard pdc = cardRepository.findById(cardId).get();
		CardComponent card = getCardById(pdc.getId());
		if (card == null) {
			card = addCardLayout(pdc, view.getOptions().isCardSortDirectionDesc());
		}

		card.reload();
	}

	public void reload() {
		TKBColumn data = repository.findById(getId().get()).get();
		changeTitle(data.getName(), data.getDataOrder());

		// update layout with new missing data
		data.getCards().stream().sorted(Comparator.comparing(IDataOrder::getDataOrder)).forEachOrdered(pdc -> {
			CardComponent card = getCardById(pdc.getId());
			if (card == null) {
				// add card as new card
				card = addCardLayout(pdc, view.getOptions().isCardSortDirectionDesc());
			}

			card.reload();
		});

		// remove old
		getCardComponents().stream().filter(e -> data.getCards().stream().noneMatch(x -> x.getId().equals(e.getId().get())))
				.collect(Collectors.toList()).forEach(e -> {
					cards.remove(e);
				});
	}

	public List<CardComponent> getCardComponents() {
		List<CardComponent> components = Lists.newArrayList();
		for (int i = 0; i < cards.getComponentCount(); i++) {
			if (cards.getComponentAt(i) instanceof CardComponent) {
				components.add((CardComponent) cards.getComponentAt(i));
			}
		}
		return components;
	}

	public CardComponent getCardById(String cardId) {
		for (int i = 0; i < cards.getComponentCount(); i++) {
			if (cards.getComponentAt(i) instanceof CardComponent) {
				CardComponent card = (CardComponent) cards.getComponentAt(i);
				if (card.getId().get().equals(cardId)) {
					return card;
				}
			}
		}

		return null;
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		UI ui = UI.getCurrent();
		broadcasterRegistration = BroadcasterColumn.register(getId().get(), event -> {
			ui.access(() -> {
				if (Config.DEBUG) {
					Notification.show("receiving broadcast for update", Config.NOTIFICATION_DURATION, Position.BOTTOM_END);
				}

				String[] cmd = event.split("\\.");

				switch (cmd[0]) {
				case BroadcasterColumn.MESSAGE_SHUFFLE:
					ColumnComponent.this.cards.removeAll();
					ColumnComponent.this.reload();
					break;

				case BroadcasterColumn.ADD_COLUMN:
					ColumnComponent.this.addCardAndReload(cmd[1]);
					break;

				default:
					reload();
					break;
				}

			});
		});
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
//		super.onDetach(detachEvent);
		if (broadcasterRegistration != null) {
			broadcasterRegistration.remove();
			broadcasterRegistration = null;
		} else {
			log.info("cannot remove broadcast, because it is null");
		}
	}

}
