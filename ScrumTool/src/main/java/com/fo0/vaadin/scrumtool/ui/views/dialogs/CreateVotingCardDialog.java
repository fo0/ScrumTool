package com.fo0.vaadin.scrumtool.ui.views.dialogs;

import org.apache.logging.log4j.util.Strings;

import com.fo0.vaadin.scrumtool.ui.broadcast.BroadcasterCardComment;
import com.fo0.vaadin.scrumtool.ui.config.Config;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBColumnRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBVotingCardRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBVotingItemRepository;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBVotingCard;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBVotingItem;
import com.fo0.vaadin.scrumtool.ui.session.SessionUtils;
import com.fo0.vaadin.scrumtool.ui.utils.SpringContext;
import com.fo0.vaadin.scrumtool.ui.views.KanbanView;
import com.fo0.vaadin.scrumtool.ui.views.components.ToolTip;
import com.fo0.vaadin.scrumtool.ui.views.components.column.ColumnComponent;
import com.fo0.vaadin.scrumtool.ui.views.components.voting.VotingItemComponent;
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
public class CreateVotingCardDialog extends Dialog {

	private static final long serialVersionUID = -2119496244059224808L;

	private KBColumnRepository columnRepository = SpringContext.getBean(KBColumnRepository.class);
	private KBVotingCardRepository votingCardRepository = SpringContext.getBean(KBVotingCardRepository.class);
	private KBVotingItemRepository votingItemRepository = SpringContext.getBean(KBVotingItemRepository.class);

	private Registration broadcasterRegistration;

	private VerticalLayout root;
	private VerticalLayout commentsLayout;
	private KanbanView view;
	private ColumnComponent column;

	private String columnId;
	private String cardTitle;

	private Label title;
	private TKBVotingCard card;

	public CreateVotingCardDialog(KanbanView view, ColumnComponent column, String columnId, String text) {
		this.columnId = columnId;
		this.view = view;
		this.column = column;
		
		this.card = TKBVotingCard.builder().build();
		setId(columnId);

		root = new VerticalLayout();
		root.setSizeFull();
		add(root);

		HorizontalLayout header = new HorizontalLayout();s
		header.setPadding(true);
		header.setAlignItems(Alignment.CENTER);
		header.getStyle().set("border", "1px solid black");
		header.setWidthFull();
		root.add(header);

		title = new Label();
		title.setWidthFull();
		header.add(title);

		Button btn = new Button(VaadinIcon.PLUS.create());
		ToolTip.add(btn, "Add Voting-Option");
		btn.addClickListener(e -> {
			new TextDialog("Write Comment", Strings.EMPTY, savedText -> {
				addComment(TKBVotingItem.builder().ownerId(SessionUtils.getSessionId()).text(savedText).build());
			}).open();
		});

		Button btnAdd = new Button(VaadinIcon.DISC.create());
		ToolTip.add(btnAdd, "Save Voting");
		btnAdd.addClickListener(e -> {
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
		
		addTitle(text);
	}

	private void addTitle(String cardText) {
		title.setText(cardText);
		card.setText(cardText);
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

	private void addComment(TKBVotingItem cardComment) {
		card.getItems().add(cardComment);
		VotingItemComponent item = new VotingItemComponent(view, view.getId().get(), getId().get(), cardComment);
		item.setWidthFull();
		commentsLayout.addComponentAsFirst(item);
	}

	public void reload() {
		
	}

}
