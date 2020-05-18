package com.fo0.vaadin.scrumretroboard.views.components;

import org.apache.commons.collections4.CollectionUtils;

import com.fo0.vaadin.scrumretroboard.data.table.ProjectDataCard;
import com.fo0.vaadin.scrumretroboard.data.table.ProjectDataColumn;
import com.fo0.vaadin.scrumretroboard.utils.Utils;
import com.fo0.vaadin.scrumretroboard.views.ProjectBoardView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;

import lombok.Getter;

public class ColumnComponent extends VerticalLayout {

	private static final long serialVersionUID = 8415434953831247614L;

	@Getter
	private ProjectDataColumn productDataColumn;

	@Getter
	private String name;

	private TextArea area;

	public ColumnComponent(ProjectBoardView view, String id, String name) {
		this.name = name;
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
		layoutHeader.add(area);

		Button btnAdd = new Button("Note", VaadinIcon.PLUS.create());
		btnAdd.setWidthFull();
		btnAdd.addClickListener(e -> {
			view.addCard(id, Utils.randomId(), area.getValue());
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

		productDataColumn = ProjectDataColumn.builder().id(id).name(name).build();
	}

	public CardComponent addCard(String id, String message) {
		CardComponent card = new CardComponent(id, message);
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

}
