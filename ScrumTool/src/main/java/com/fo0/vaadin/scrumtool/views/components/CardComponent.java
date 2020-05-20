package com.fo0.vaadin.scrumtool.views.components;

import com.fo0.vaadin.scrumtool.data.table.ProjectDataCard;
import com.fo0.vaadin.scrumtool.session.SessionUtils;
import com.fo0.vaadin.scrumtool.views.KanbanView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import lombok.Getter;

public class CardComponent extends HorizontalLayout {

	private static final long serialVersionUID = -1213748155629932731L;

	@Getter
	private ProjectDataCard card;

	public CardComponent(KanbanView view, String columnId, String id, String text) {
		setId(id);
		getStyle().set("border", "2px solid black");
		setSpacing(true);
		add(new Label(text));

		card = ProjectDataCard.builder().id(id).ownerId(SessionUtils.getSessionId()).text(text).build();

		Button btn = new Button(VaadinIcon.TRASH.create());
		btn.addClickListener(e -> {
			view.removeCard(columnId, id);
		});
		add(btn);
		setAlignItems(Alignment.CENTER);
	}

}
