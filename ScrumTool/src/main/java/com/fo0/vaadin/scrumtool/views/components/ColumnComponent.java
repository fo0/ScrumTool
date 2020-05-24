package com.fo0.vaadin.scrumtool.views.components;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.fo0.vaadin.scrumtool.broadcast.BroadcasterBoard;
import com.fo0.vaadin.scrumtool.broadcast.BroadcasterColumns;
import com.fo0.vaadin.scrumtool.config.Config;
import com.fo0.vaadin.scrumtool.config.KanbanConfig;
import com.fo0.vaadin.scrumtool.data.interfaces.IDataOrder;
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
	private KBColumnRepository repository = SpringContext.getBean(KBColumnRepository.class);

	private KanbanView view;

	@Getter
	private TKBColumn data;

	private TextArea area;

	private H3 h3;

	private Registration broadcasterRegistration;

	public ColumnComponent(KanbanView view, TKBColumn column) {
		this.view = view;
		this.data = column;
		setId(column.getId());
		h3 = new H3();
		changeTitle(column.getName());
		h3.getStyle().set("text-align", "center");
		h3.setWidthFull();

		HorizontalLayout captionLayout = new HorizontalLayout(h3);
		captionLayout.setMargin(false);
		captionLayout.setSpacing(false);

		if (KBViewUtils.isComponentAllowedToDisplay(view.getOptions(), data.getOwnerId())) {
			Button btnShuffle = new Button(VaadinIcon.RANDOM.create());
			btnShuffle.addClickListener(e -> {
				//@formatter:off
				List<TKBCard> toShuffle = data.getCards()
						.stream()
						.sorted(Comparator.comparing(IDataOrder::getDataOrder))
						.collect(Collectors.toList());
				
				Collections.shuffle(toShuffle);
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
		setMinWidth("400px");
		setWidth("400px");
		getStyle().set("border", "2px solid black");
		setSpacing(true);
		setMargin(true);

		VerticalLayout layoutHeader = new VerticalLayout();
		layoutHeader.getStyle().set("border", "2px solid black");
		layoutHeader.setWidthFull();
		layoutHeader.setHeight("200px");
		add(layoutHeader);

		area = new TextArea();
		area.setSizeFull();
		area.setMaxLength(KanbanConfig.MAX_CARD_TEXT_LENGTH);
		area.setValueChangeMode(ValueChangeMode.EAGER);
		area.addValueChangeListener(e -> {
			if (e.getSource().getValue().length() >= KanbanConfig.MAX_CARD_TEXT_LENGTH) {
				layoutHeader.getStyle().set("border-color", STYLES.COLOR_RED_500);
			} else {
				layoutHeader.getStyle().remove("border-color");
			}
		});
		layoutHeader.add(area);

		Button btnAdd = new Button("Note", VaadinIcon.PLUS.create());
		btnAdd.setWidthFull();
		btnAdd.addClickListener(e -> {
			addCard(Utils.randomId(), SessionUtils.getSessionId(), area.getValue());
			BroadcasterColumns.broadcast(getId().get(), "update");
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
				reload();
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
		if (Config.DEBUG)
			h3.setText(string + " (" + data.getDataOrder() + ")");
		else
			h3.setText(string);
	}

	private void addCard(String randomId, String sessionId, String value) {
		TKBColumn tmp = repository.findById(getId().get()).get();
		TKBCard card = TKBCard.builder().id(randomId).ownerId(sessionId).dataOrder(KBViewUtils.calculateNextPosition(tmp.getCards()))
				.text(value).build();
		tmp.addCard(card);
		repository.save(tmp);
		log.info("add card: {}", randomId);
	}

	private CardComponent addCardLayout(TKBCard card) {
		CardComponent cc = new CardComponent(view, this, getId().get(), card);
		add(cc);
		return cc;
	}

	public void reload() {
		data = repository.findById(data.getId()).get();

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
					remove(e);
				});
	}

	public List<CardComponent> getCardComponents() {
		List<CardComponent> components = Lists.newArrayList();
		for (int i = 0; i < getComponentCount(); i++) {
			if (getComponentAt(i) instanceof CardComponent) {
				components.add((CardComponent) getComponentAt(i));
			}
		}

		return components;
	}

	public CardComponent getCardById(String cardId) {
		for (int i = 0; i < getComponentCount(); i++) {
			if (getComponentAt(i) instanceof CardComponent) {
				CardComponent card = (CardComponent) getComponentAt(i);
				if (card.getId().get().equals(cardId)) {
					return card;
				}
			}
		}

		return null;
	}

}
