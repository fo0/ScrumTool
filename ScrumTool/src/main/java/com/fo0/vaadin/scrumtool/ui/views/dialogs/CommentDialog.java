package com.fo0.vaadin.scrumtool.ui.views.dialogs;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import com.fo0.vaadin.scrumtool.ui.data.repository.KBCardCommentRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBCardRepository;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCardComment;
import com.fo0.vaadin.scrumtool.ui.utils.SpringContext;
import com.fo0.vaadin.scrumtool.ui.views.components.CardCommentComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class CommentDialog extends Dialog {

	private static final long serialVersionUID = -2119496244059224808L;

	private String cardId;
	private String cardText;

	private KBCardRepository cardRepository = SpringContext.getBean(KBCardRepository.class);
	private KBCardCommentRepository cardComment = SpringContext.getBean(KBCardCommentRepository.class);

	private VerticalLayout root;

	public CommentDialog(String cardId, String cardText) {
		this.cardId = cardId;
		this.cardText = cardText;

		root = new VerticalLayout();
		root.setSizeFull();
		add(root);

		HorizontalLayout header = new HorizontalLayout();
		header.setPadding(true);
		header.setAlignItems(Alignment.CENTER);
		header.getStyle().set("border", "1px solid black");
		header.setWidthFull();
		root.add(header);

		Label title = new Label(String.format("Comments for Card '%s'", StringUtils.abbreviate(cardText, 10)));
		title.setWidthFull();
		header.add(title);

		Button btn = new Button(VaadinIcon.PLUS.create());
		btn.addClickListener(e -> {
			new TextDialog("Write Comment", Strings.EMPTY, savedText -> {
				addComment(TKBCardComment.builder().text(savedText).build());
			}).open();
		});

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setJustifyContentMode(JustifyContentMode.END);
		btnLayout.add(btn);
		header.add(btnLayout);

		setWidth("500px");
		setHeight("400px");
		
		initCards();
	}

	private void initCards() {
		TKBCard card = cardRepository.findById(cardId).get();
		card.getComments().forEach(e -> root.add(new CardCommentComponent(cardId, e)));
	}
	
	private void addComment(TKBCardComment cardComment) {
		root.add(new CardCommentComponent(cardId, cardComment));
		TKBCard card = cardRepository.findById(cardId).get();
		card.getComments().add(cardComment);
		cardRepository.save(card);
	}

	public void reload() {

	}

}
