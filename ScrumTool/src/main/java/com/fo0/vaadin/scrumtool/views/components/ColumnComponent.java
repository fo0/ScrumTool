package com.fo0.vaadin.scrumtool.views.components;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fo0.vaadin.scrumtool.broadcast.BroadcasterBoard;
import com.fo0.vaadin.scrumtool.broadcast.BroadcasterColumns;
import com.fo0.vaadin.scrumtool.config.Config;
import com.fo0.vaadin.scrumtool.data.interfaces.IDataOrder;
import com.fo0.vaadin.scrumtool.data.repository.KBCardLikesRepository;
import com.fo0.vaadin.scrumtool.data.repository.KBCardRepository;
import com.fo0.vaadin.scrumtool.data.repository.KBColumnRepository;
import com.fo0.vaadin.scrumtool.data.repository.KBDataRepository;
import com.fo0.vaadin.scrumtool.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.data.table.TKBColumn;
import com.fo0.vaadin.scrumtool.data.table.TKBData;
import com.fo0.vaadin.scrumtool.session.SessionUtils;
import com.fo0.vaadin.scrumtool.styles.STYLES;
import com.fo0.vaadin.scrumtool.utils.SpringContext;
import com.fo0.vaadin.scrumtool.utils.Utils;
import com.fo0.vaadin.scrumtool.views.KanbanView;
import com.fo0.vaadin.scrumtool.views.utils.KBViewUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
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
	private KBCardLikesRepository likeRepository = SpringContext.getBean(KBCardLikesRepository.class);

	@Getter
	private TKBColumn data;
	private KanbanView view;
	private TextArea area;
	private H3 h3;
	private Registration broadcasterRegistration;
	private VerticalLayout cards;

	public ColumnComponent(KanbanView view, TKBColumn column) {
		this.view = view;
		this.data = column;
		setId(column.getId());

		setWidth("400px");
		getStyle().set("box-shadow", "var(--material-shadow-elevation-4dp)");
		setSpacing(true);
		setMargin(false);

		h3 = new H3();
		h3.getStyle().set("margin", "unset");
		changeTitle(column.getName());
		h3.setWidthFull();

		HorizontalLayout captionLayout = new HorizontalLayout(h3);
		captionLayout.setMargin(false);
		captionLayout.setSpacing(false);

		if (KBViewUtils.isAllowed(view.getOptions(), data.getOwnerId())) {
			Button btnShuffle = new Button(VaadinIcon.RANDOM.create());
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
				BroadcasterColumns.broadcast(getId().get(), BroadcasterColumns.MESSAGE_SHUFFLE);
				//@formatter:on
			});
			captionLayout.add(btnShuffle);
			captionLayout.setVerticalComponentAlignment(Alignment.CENTER, btnShuffle);

			Button btnDelete = new Button(VaadinIcon.TRASH.create());
			btnDelete.addClickListener(e -> {
				log.info("delete column: " + getId().get());
				Notification.show("Deleting Column: " + column.getName(), Config.NOTIFICATION_DURATION, Position.MIDDLE);
				TKBData c = dataRepository.findById(view.getId().get()).get();
				c.removeColumnById(getId().get());
				dataRepository.save(c);
				BroadcasterBoard.broadcast(view.getId().get(), "update");
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

		Button btnAdd = new Button("Note", VaadinIcon.PLUS.create());
		btnAdd.setWidthFull();
		btnAdd.addClickListener(e -> {
			if (view.getOptions().getMaxCards() > 0) {
				if (cards.getComponentCount() > view.getOptions().getMaxCards()) {
					Notification.show("Card limit reached", Config.NOTIFICATION_DURATION, Position.MIDDLE);
					return;
				}
			}

			TKBColumn col = addCard(Utils.randomId(), SessionUtils.getSessionId(), area.getValue());
			BroadcasterColumns.broadcast(getId().get(), BroadcasterColumns.ADD_COLUMN + col.getId());
			area.clear();
			area.focus();
		});

		Button btnCancel = new Button("Clear", VaadinIcon.TRASH.create());
		btnCancel.setWidthFull();
		btnCancel.addClickListener(e -> {
			area.clear();
		});

		HorizontalLayout btnLayout = new HorizontalLayout(btnCancel, btnAdd);
		btnLayout.setWidthFull();
		layoutHeader.add(btnLayout);
		setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, h3);

		cards = new VerticalLayout();
		cards.getStyle().set("overflow", "auto");
		cards.setMargin(false);
		cards.setPadding(false);
		cards.setSpacing(true);
		add(cards);

	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
//		super.onAttach(attachEvent);
		UI ui = UI.getCurrent();
		broadcasterRegistration = BroadcasterColumns.register(getId().get(), event -> {
			ui.access(() -> {
				if (Config.DEBUG) {
					Notification.show("receiving broadcast for update", Config.NOTIFICATION_DURATION, Position.BOTTOM_END);
				}

				String[] cmd = event.split("\\.");

				switch (cmd[0]) {
				case BroadcasterColumns.MESSAGE_SHUFFLE:
					ColumnComponent.this.cards.removeAll();
					ColumnComponent.this.reload();
					break;

				case BroadcasterColumns.ADD_COLUMN:
					ColumnComponent.this.reloadAddCard(cmd[1]);
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

	public void changeTitle(String string) {
		if (!h3.getText().equals(string)) {
			h3.setText(string);
		}

		if (Config.DEBUG)
			h3.setText(string + " (" + data.getDataOrder() + ")");
	}

	private TKBColumn addCard(String randomId, String sessionId, String value) {
		TKBColumn tmp = repository.findById(getId().get()).get();
		TKBCard card = TKBCard.builder().id(randomId).ownerId(sessionId).dataOrder(KBViewUtils.calculateNextPosition(tmp.getCards()))
				.text(value).build();
		tmp.addCard(card);
		repository.save(tmp);
		log.info("add card: {}", randomId);
		return tmp;
	}

	private CardComponent addCardLayout(TKBCard card) {
		CardComponent cc = new CardComponent(view, this, getId().get(), card);
		cards.add(cc);
		return cc;
	}

	public void reloadAddCard(String cardId) {
		TKBCard pdc = cardRepository.findById(cardId).get();
		CardComponent card = getCardById(pdc.getId());
		if (card == null) {
			card = addCardLayout(pdc);
		}

		card.reload();
	}

	public void reload() {
		data = repository.findById(getId().get()).get();
		changeTitle(data.getName());

		// update layout with new missing data
		data.getCards().stream().sorted(Comparator.comparing(IDataOrder::getDataOrder)).forEachOrdered(pdc -> {
			CardComponent card = getCardById(pdc.getId());
			if (card == null) {
				// add card as new card
				card = addCardLayout(pdc);
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

}
