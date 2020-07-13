package com.fo0.vaadin.scrumtool.ui.views.dialogs;

import java.util.Comparator;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import com.fo0.vaadin.scrumtool.ui.broadcast.BroadcasterCard;
import com.fo0.vaadin.scrumtool.ui.broadcast.BroadcasterCardComment;
import com.fo0.vaadin.scrumtool.ui.config.Config;
import com.fo0.vaadin.scrumtool.ui.data.interfaces.IDataOrder;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBCardCommentRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBCardRepository;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCardComment;
import com.fo0.vaadin.scrumtool.ui.utils.SpringContext;
import com.fo0.vaadin.scrumtool.ui.views.components.CardCommentComponent;
import com.fo0.vaadin.scrumtool.ui.views.utils.KBViewUtils;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CommentDialog extends Dialog {

	private static final long serialVersionUID = -2119496244059224808L;

	private KBCardRepository cardRepository = SpringContext.getBean(KBCardRepository.class);
	private KBCardCommentRepository cardComment = SpringContext.getBean(KBCardCommentRepository.class);

	private Registration broadcasterRegistration;

	private VerticalLayout root;
	private VerticalLayout commentsLayout;

	private String cardId;
	private String cardText;

	private Label title;

	public CommentDialog(String cardId, String cardText) {
		this.cardId = cardId;
		this.cardText = cardText;

		setId(cardId);
		
		root = new VerticalLayout();
		root.setSizeFull();
		add(root);

		HorizontalLayout header = new HorizontalLayout();
		header.setPadding(true);
		header.setAlignItems(Alignment.CENTER);
		header.getStyle().set("border", "1px solid black");
		header.setWidthFull();
		root.add(header);

		title = new Label();
		title.setWidthFull();
		header.add(title);

		Button btn = new Button(VaadinIcon.PLUS.create());
		btn.addClickListener(e -> {
			new TextDialog("Write Comment", Strings.EMPTY, savedText -> {
				TKBCard tmp = cardRepository.findById(getId().get()).get();
				addComment(TKBCardComment.builder().text(savedText).dataOrder(KBViewUtils.calculateNextPosition(tmp.getComments())).build());
			}).open();
		});

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setJustifyContentMode(JustifyContentMode.END);
		btnLayout.add(btn);
		header.add(btnLayout);

		setWidth("500px");
		setHeight("400px");

		commentsLayout = new VerticalLayout();
		commentsLayout.setWidthFull();
		commentsLayout.setMargin(false);
		commentsLayout.setPadding(false);
		root.add(commentsLayout);

		initCards();
	}

	private void addTitle(int comments, String cardText) {
		title.setText(String.format("There are '%s' Comments for Card: %s", comments, StringUtils.abbreviate(cardText, 15)));
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		UI ui = UI.getCurrent();
		broadcasterRegistration = BroadcasterCardComment.register(getId().get(), event -> {
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
		if (broadcasterRegistration != null) {
			broadcasterRegistration.remove();
			broadcasterRegistration = null;
		} else {
			log.info("cannot remove broadcast, because it is null");
		}
	}

	private void initCards() {
		commentsLayout.removeAll();
		TKBCard card = cardRepository.findById(cardId).get();
		addTitle(CollectionUtils.size(card.getComments()), cardText);
		card.getComments().stream().sorted(Comparator.comparing(IDataOrder::getDataOrder).reversed()).forEach(e -> commentsLayout.add(new CardCommentComponent(cardId, e)));
	}

	private void addComment(TKBCardComment cardComment) {
		commentsLayout.addComponentAsFirst(new CardCommentComponent(cardId, cardComment));
		TKBCard card = cardRepository.findById(cardId).get();
		card.getComments().add(cardComment);
		cardRepository.save(card);
		BroadcasterCardComment.broadcast(cardId, "update");
		BroadcasterCard.broadcast(cardId, "update");
	}

	public void reload() {
		initCards();
	}

}
