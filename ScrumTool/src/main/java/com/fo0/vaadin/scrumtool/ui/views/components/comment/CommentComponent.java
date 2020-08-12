package com.fo0.vaadin.scrumtool.ui.views.components.comment;

import com.fo0.vaadin.scrumtool.ui.broadcast.BroadcasterCard;
import com.fo0.vaadin.scrumtool.ui.broadcast.BroadcasterCardComment;
import com.fo0.vaadin.scrumtool.ui.config.Config;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBCardCommentRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBCardRepository;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCardComment;
import com.fo0.vaadin.scrumtool.ui.utils.SpringContext;
import com.fo0.vaadin.scrumtool.ui.views.components.ToolTip;
import com.fo0.vaadin.scrumtool.ui.views.dialogs.TextDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import lombok.extern.log4j.Log4j2;

@Log4j2
@CssImport(value = "./styles/card-style.css", themeFor = "vaadin-horizontal-layout")
public class CommentComponent extends HorizontalLayout {

	private static final long serialVersionUID = -1213748155629932731L;

	private KBCardRepository cardRepository = SpringContext.getBean(KBCardRepository.class);
	private KBCardCommentRepository cardCommentRepository = SpringContext.getBean(KBCardCommentRepository.class);

	private String cardId;
	private TKBCardComment comment;
	private Label label;

	public CommentComponent(String cardId, TKBCardComment comment) {
		this.comment = comment;
		this.cardId = cardId;

		setId(comment.getId());
		setSpacing(true);
		setPadding(true);
		setMargin(false);
		label = new Label();
		label.getStyle().set("word-break", "break-word");
		label.setWidthFull();
		changeText(comment.getText());
		add(label);

		VerticalLayout btnLayout = new VerticalLayout();
		btnLayout.setWidth("unset");
		btnLayout.setSpacing(false);
		btnLayout.setMargin(false);
		btnLayout.setPadding(false);
		add(btnLayout);

		setFlexGrow(1, label);
		setWidthFull();
		getStyle().set("box-shadow", "0.5px solid black");
		getStyle().set("border-radius", "1em");
		getStyle().set("border", "1px solid var(--material-disabled-text-color)");
		addClassName("card-hover");

		Button btnDelete = new Button(VaadinIcon.TRASH.create());
		ToolTip.add(btnDelete, "Delete the comment");
		btnDelete.addClickListener(e -> delete());
		btnLayout.add(btnDelete);

		label.getElement().addEventListener("click", e -> {
			new TextDialog("Edit Text", label.getText(), savedText -> {
				log.info("edit comment: " + getId().get());
				TKBCardComment c = cardCommentRepository.findById(comment.getId()).get();
				c.setText(savedText);
				cardCommentRepository.save(c);
				BroadcasterCardComment.broadcast(cardId, "update");
				BroadcasterCard.broadcast(cardId, "update");
			}).open();
		});

		Icon editIcon = VaadinIcon.EDIT.create();
		add(editIcon);
	}

	public void delete() {
		log.info("delete card comment: {}", getId().get());
		TKBCardComment cc = cardCommentRepository.findById(getId().get()).get();
		TKBCard c = cardRepository.findById(cardId).get();
		c.getComments().remove(cc);
		cardRepository.save(c);
		BroadcasterCardComment.broadcast(cardId, "update");
		BroadcasterCard.broadcast(cardId, "update");
	}

	public void reload() {
		TKBCardComment tmp = cardCommentRepository.findById(getId().get()).get();

		// update layout with new missing data
		changeText(tmp.getText());
	}

	private void changeText(String text) {
		if (!label.getText().equals(text)) {
			label.setText(text);
		}

		if (Config.DEBUG) {
			label.setText(text + " (" + comment.getDataOrder() + ")");
		}
	}

}
