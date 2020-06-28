package com.fo0.vaadin.scrumtool.ui.views.dialogs;

import org.apache.commons.lang3.StringUtils;

import com.fo0.vaadin.scrumtool.ui.data.repository.KBCardCommentRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBCardRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class CommentDialog extends Dialog {

	private static final long serialVersionUID = -2119496244059224808L;

	private String id;
	private String cardText;
	
	private KBCardRepository cardRepository;
	private KBCardCommentRepository cardCommentRepository;

	public CommentDialog(String cardId, String cardText) {
		this.id = cardId;
		this.cardText = cardText;

		VerticalLayout root = new VerticalLayout();
		root.setSizeFull();
		add(root);

		HorizontalLayout header = new HorizontalLayout();
		header.getStyle().set("border", "1px solid black");
		header.setWidthFull();
		root.add(header);

		Label title = new Label(String.format("Comments for Card '%s'", StringUtils.abbreviate(cardText, 10)));
		title.setWidthFull();
		header.add(title);

		Button btn = new Button(VaadinIcon.PLUS.create());
		btn.addClickListener(e -> {
			root.add(new Label("test"));
		});

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setJustifyContentMode(JustifyContentMode.END);
		btnLayout.add(btn);
		header.add(btnLayout);

		setWidth("500px");
		setHeight("400px");
	}

}
