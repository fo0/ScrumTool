package com.fo0.vaadin.scrumtool.ui.views.components.voting;

import com.fo0.vaadin.scrumtool.ui.broadcast.BroadcasterCardLike;
import com.fo0.vaadin.scrumtool.ui.config.Config;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBDataRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBOptionRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBVotingItemRepository;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBVotingItem;
import com.fo0.vaadin.scrumtool.ui.session.SessionUtils;
import com.fo0.vaadin.scrumtool.ui.utils.SpringContext;
import com.fo0.vaadin.scrumtool.ui.utils.Utils;
import com.fo0.vaadin.scrumtool.ui.views.KanbanView;
import com.fo0.vaadin.scrumtool.ui.views.components.ToolTip;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class VotingItemComponent extends VerticalLayout {

	private static final long serialVersionUID = -2483871323771596716L;

	private KanbanView view;

	private KBDataRepository repositoryData = SpringContext.getBean(KBDataRepository.class);
	private KBOptionRepository repositoryDataOption = SpringContext.getBean(KBOptionRepository.class);
	private KBVotingItemRepository votingItemRepository = SpringContext.getBean(KBVotingItemRepository.class);

	private String boardId;
	private String cardId;

	private Button btnLike;
	private Button btnRemoveLike;

	private Registration broadcasterRegistration;

	private Label label;

	public VotingItemComponent(KanbanView view, String boardId, String cardId, TKBVotingItem item) {
		this.view = view;
		this.boardId = boardId;
		this.cardId = cardId;
		setId(cardId);

		btnLike = new Button(VaadinIcon.THUMBS_UP_O.create());
		ToolTip.add(btnLike, "Like the card");
		btnLike.setText(String.valueOf(item.getLikeValue()));
		btnLike.setWidthFull();
		btnLike.addClickListener(e -> {
			if (islikeLimitAlreadyExistsByOwner(SessionUtils.getSessionId())) {
				Notification.show("You already liked the card", Config.NOTIFICATION_DURATION, Position.MIDDLE);
				return;
			}

			if (isLikeLimitReachedByOwner(SessionUtils.getSessionId())) {
				Notification.show("You already reached the like limit", Config.NOTIFICATION_DURATION, Position.MIDDLE);
				return;
			}

			addLike(Utils.randomId(), SessionUtils.getSessionId());
			BroadcasterCardLike.broadcast(cardId, "update");
		});
		add(btnLike);

		btnRemoveLike = new Button(VaadinIcon.THUMBS_DOWN_O.create());
		ToolTip.add(btnRemoveLike, "Remove your like");
		btnRemoveLike.setText(String.valueOf(getCurrentLikesByOwner()));
		btnRemoveLike.setWidthFull();
		btnRemoveLike.addClickListener(e -> {
			if (!isLikedByOwner(SessionUtils.getSessionId())) {
				Notification.show("You must like the card, bevor remove", Config.NOTIFICATION_DURATION, Position.MIDDLE);
				return;
			}

			removeLike(SessionUtils.getSessionId());
			BroadcasterCardLike.broadcast(cardId, "update");
		});
		add(btnRemoveLike);

		label = new Label();
		HorizontalLayout layoutTitle = new HorizontalLayout(label);
		layoutTitle.setMargin(true);
		layoutTitle.setWidthFull();
		add(layoutTitle);
		
		HorizontalLayout l = new HorizontalLayout(btnLike, btnRemoveLike);
		l.setWidthFull();
		add(l);
		changeText(item.getText());

		setMargin(false);
		setPadding(false);
		setSpacing(false);
		
		getStyle().set("box-shadow", "0.5px solid black");
		getStyle().set("border", "1px solid var(--material-disabled-text-color)");
		addClassName("card-hover");
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		UI ui = UI.getCurrent();
		broadcasterRegistration = BroadcasterCardLike.register(getId().get(), event -> {
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

	public boolean islikeLimitAlreadyExistsByOwner(String ownerId) {
		return false;
	}

	public boolean isLikeLimitReachedByOwner(String ownerId) {
		return false;
	}

	public int getCurrentLikesByOwner() {
		return -1;
	}

	public void addLike(String id, String ownerId) {
	}

	public void removeLike(String ownerId) {
	}

	public void reload() {
	}

	private boolean isLikedByOwner(String ownerId) {
		return false;
	}

	public void changeText(String value) {
		label.setText(value);
	}

	private void changeButtonIconToLiked(boolean liked) {
		if (liked) {
			btnLike.setIcon(VaadinIcon.THUMBS_UP.create());
		} else {
			btnLike.setIcon(VaadinIcon.THUMBS_UP_O.create());
		}
	}

}
