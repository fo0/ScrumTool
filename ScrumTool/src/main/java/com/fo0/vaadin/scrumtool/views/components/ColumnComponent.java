package com.fo0.vaadin.scrumtool.views.components;

import org.apache.commons.collections4.CollectionUtils;

import com.fo0.vaadin.scrumtool.config.KanbanConfig;
import com.fo0.vaadin.scrumtool.data.table.ProjectDataCard;
import com.fo0.vaadin.scrumtool.data.table.ProjectDataColumn;
import com.fo0.vaadin.scrumtool.styles.STYLES;
import com.fo0.vaadin.scrumtool.utils.Utils;
import com.fo0.vaadin.scrumtool.views.KanbanView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;

import lombok.Getter;

public class ColumnComponent extends VerticalLayout {

	private static final long serialVersionUID = 8415434953831247614L;

	private KanbanView view;

	@Getter
	private ProjectDataColumn productDataColumn;

	@Getter
	private String name;

	private TextArea area;

	public ColumnComponent(KanbanView view, String id, String ownerId, String name) {
		this.name = name;
		this.view = view;
		setId(id);
		H3 h3 = new H3(name);
		add(h3);
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
			view.addCard(id, Utils.randomId(), ownerId, area.getValue(), true);
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

		productDataColumn = ProjectDataColumn.builder().id(id).ownerId(ownerId).name(name).build();
	}

	public CardComponent addCard(String id, String ownerId, String message) {
		CardComponent card = new CardComponent(view, getId().get(), id, ownerId, message);
		add(card);
		productDataColumn.addCard(card.getCard());
		return card;
	}

	public ProjectDataCard getCardById(String cardId) {
		if (CollectionUtils.isEmpty(productDataColumn.getCards())) {
			return null;
		}

		return productDataColumn.getCardById(cardId);
	}

	public void removeCardById(String cardId) {
		if (CollectionUtils.isEmpty(productDataColumn.getCards())) {
			return;
		}

		productDataColumn.getCards().removeIf(e -> e.getId().equals(cardId));

		for (int i = 0; i < getComponentCount(); i++) {
			if (getComponentAt(i) instanceof CardComponent) {
				CardComponent col = (CardComponent) getComponentAt(i);
				if (col.getId().get().equals(cardId)) {
					remove(col);
					break;
				}
			}
		}
	}

}
