package com.fo0.vaadin.scrumtool.ui.views.dialogs;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;

import com.fo0.vaadin.scrumtool.ui.broadcast.BroadcasterCardComment;
import com.fo0.vaadin.scrumtool.ui.broadcast.BroadcasterColumn;
import com.fo0.vaadin.scrumtool.ui.config.Config;
import com.fo0.vaadin.scrumtool.ui.data.enums.ECardType;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBColumnRepository;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.ui.model.VotingItem;
import com.fo0.vaadin.scrumtool.ui.session.SessionUtils;
import com.fo0.vaadin.scrumtool.ui.utils.SpringContext;
import com.fo0.vaadin.scrumtool.ui.views.KanbanView;
import com.fo0.vaadin.scrumtool.ui.views.components.ToolTip;
import com.fo0.vaadin.scrumtool.ui.views.components.column.ColumnComponent;
import com.fo0.vaadin.scrumtool.ui.views.components.interfaces.IBroadcastRegistry;
import com.fo0.vaadin.scrumtool.ui.views.components.interfaces.IComponent;
import com.fo0.vaadin.scrumtool.ui.views.components.voting.VotingItemComponent;
import com.google.gson.Gson;
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

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CreateVotingCardDialog extends Dialog implements IBroadcastRegistry, IComponent {

	private static final long serialVersionUID = -2119496244059224808L;

	private KBColumnRepository columnRepository = SpringContext.getBean(KBColumnRepository.class);

	private VerticalLayout root;
	private VerticalLayout votingItemLayout;
	private KanbanView view;
	private ColumnComponent column;

	private String columnId;
	private String cardTitle;

	private Label title;

	public CreateVotingCardDialog(KanbanView view, ColumnComponent column, String columnId, String text) {
		this.columnId = columnId;
		this.view = view;
		this.column = column;

		setId(columnId);

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
		ToolTip.add(btn, "Add Voting-Option");
		btn.addClickListener(e -> {
			new TextDialog("Write Comment", Strings.EMPTY, savedText -> {
				addComment(VotingItem.builder().ownerId(SessionUtils.getSessionId()).text(savedText).build());
			}).open();
		});

		Button btnAdd = new Button(VaadinIcon.CHECK.create());
		ToolTip.add(btnAdd, "Save Voting");
		btnAdd.addClickListener(e -> {
			TKBCard card = TKBCard.builder().type(ECardType.VotingCard).text(new Gson().toJson(getVotingItems())).build();
			column.addVotingCardAndSave(card);
			BroadcasterColumn.broadcastAddColumn(columnId, card.getId());
			close();
		});

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setJustifyContentMode(JustifyContentMode.END);
		btnLayout.add(btn);
		btnLayout.add(btnAdd);

		header.add(btnLayout);

		setWidth("500px");
		setHeight("400px");

		votingItemLayout = new VerticalLayout();
		votingItemLayout.setWidthFull();
		votingItemLayout.setMargin(false);
		votingItemLayout.setPadding(false);
		root.add(votingItemLayout);

		addTitle(text);
	}

	private void addTitle(String cardText) {
		title.setText(cardText);
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		UI ui = UI.getCurrent();
		registerBroadcast("card", BroadcasterCardComment.register(getId().get(), event -> {
			ui.access(() -> {
				if (Config.DEBUG) {
					Notification.show("receiving broadcast for update", Config.NOTIFICATION_DURATION, Position.BOTTOM_END);
				}

				reload();
			});
		}));
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		unRegisterBroadcasters();
	}
	
	public List<VotingItem> getVotingItems(){
		return getComponentsByType(votingItemLayout, VotingItemComponent.class).stream().map(VotingItemComponent::getVotingItem).collect(Collectors.toList());
	}

	private void addComment(VotingItem cardComment) {
		VotingItemComponent item = new VotingItemComponent(view, view.getId().get(), getId().get(), cardComment);
		item.setWidthFull();
		votingItemLayout.addComponentAsFirst(item);
	}

	public void reload() {

	}

}
