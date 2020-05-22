package com.fo0.vaadin.scrumtool.views.components;

import java.util.List;
import java.util.stream.Collectors;

import com.fo0.vaadin.scrumtool.config.KanbanConfig;
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
import com.google.common.collect.Lists;
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

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ColumnComponent extends VerticalLayout {

	private static final long serialVersionUID = 8415434953831247614L;

	private KBDataRepository dataRepository = SpringContext.getBean(KBDataRepository.class);
	private KBColumnRepository repository = SpringContext.getBean(KBColumnRepository.class);

	private KanbanView view;

	@Getter
	private TKBColumn productDataColumn;

	@Getter
	private String name;

	private TextArea area;

	public ColumnComponent(KanbanView view, String id, String ownerId, String name) {
		this.name = name;
		this.view = view;
		setId(id);
		H3 h3 = new H3(name);
		h3.getStyle().set("text-align", "center");
		h3.setWidthFull();
		Button btn = new Button(VaadinIcon.TRASH.create());
		btn.addClickListener(e -> {
			log.info("delete column: " + getId().get());
			Notification.show("Deleting Column: " + name, 3000, Position.MIDDLE);
			TKBData c = dataRepository.findById(view.getBoardId()).get();
			c.removeColumnById(getId().get());
			dataRepository.save(c);
			view.reload();
		});
		HorizontalLayout captionLayout = new HorizontalLayout(h3, btn);
		captionLayout.setWidthFull();
		captionLayout.setVerticalComponentAlignment(Alignment.CENTER, h3);
		captionLayout.setVerticalComponentAlignment(Alignment.CENTER, btn);
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
			reload();
			area.clear();
			area.focus();
		});

		Button btnCancel = new Button("Clear", VaadinIcon.TRASH.create());
		btnCancel.setWidthFull();
		btnCancel.addClickListener(e -> {
			area.clear();
		});

		HorizontalLayout btnLayout = new HorizontalLayout(btnAdd, btnCancel);
		btnLayout.setWidthFull();
		layoutHeader.add(btnLayout);
		setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, h3);

		productDataColumn = TKBColumn.builder().id(id).ownerId(ownerId).name(name).build();
	}

	private void addCard(String randomId, String sessionId, String value) {
		TKBColumn tmp = repository.findById(getId().get()).get();
		TKBCard card = TKBCard.builder().id(randomId).ownerId(sessionId).text(value).build();
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
		TKBColumn tmp = repository.findById(productDataColumn.getId()).get();

		// update layout with new missing data
		tmp.getCards().stream().forEachOrdered(pdc -> {
			CardComponent card = getCardById(pdc.getId());
			if (card == null) {
				// add card as new card
				card = addCardLayout(pdc);
			}

			card.reload();
		});

		// remove old
		getCardComponents().stream().filter(e -> tmp.getCards().stream().noneMatch(x -> x.getId().equals(e.getId().get())))
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
