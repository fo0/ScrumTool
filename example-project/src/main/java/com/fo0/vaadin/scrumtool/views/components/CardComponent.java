package com.fo0.vaadin.scrumtool.views.components;

import com.fo0.vaadin.scrumtool.data.table.ProjectDataCard;
import com.vaadin.flow.component.html.Label;

import lombok.Getter;

public class CardComponent extends Label {

	private static final long serialVersionUID = -1213748155629932731L;

	@Getter
	private ProjectDataCard card;

	public CardComponent(String id, String text) {
		setText(text);
		setId(id);
		getStyle().set("border", "2px solid black");
		card = ProjectDataCard.builder().id(id).text(text).build();
	}

}
