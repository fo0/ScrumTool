package com.fo0.vaadin.scrumtool.ui.views.components;

import org.springframework.expression.spel.ast.OpAnd;

import com.fo0.vaadin.scrumtool.ui.broadcast.BroadcasterCardLike;
import com.fo0.vaadin.scrumtool.ui.config.Config;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBCardLikesRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBCardRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBDataRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBOptionRepository;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCardLikes;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBData;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBOptions;
import com.fo0.vaadin.scrumtool.ui.session.SessionUtils;
import com.fo0.vaadin.scrumtool.ui.utils.SpringContext;
import com.fo0.vaadin.scrumtool.ui.utils.Utils;
import com.fo0.vaadin.scrumtool.ui.views.KanbanView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class LikeComponent extends VerticalLayout {

	private static final long serialVersionUID = -2483871323771596716L;

	private KanbanView view;

	private KBCardRepository repository = SpringContext.getBean(KBCardRepository.class);
	private KBDataRepository repositoryData = SpringContext.getBean(KBDataRepository.class);
	private KBOptionRepository repositoryDataOption = SpringContext.getBean(KBOptionRepository.class);
	private KBCardLikesRepository repositoryCardLike = SpringContext.getBean(KBCardLikesRepository.class);

	private String boardId;
	private String cardId;

	private Button btnLike;
	private Button btnRemoveLike;

	private Registration broadcasterRegistration;

	public LikeComponent(KanbanView view, String boardId, String cardId, int likes) {
		this.view = view;
		this.boardId = boardId;
		this.cardId = cardId;
		setId(cardId);

		btnLike = new Button(VaadinIcon.THUMBS_UP_O.create());
		ToolTip.add(btnLike, "Like the card");
		btnLike.setText(String.valueOf(likes));
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
		setMargin(false);
		setPadding(false);
		setSpacing(false);
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
		if (view.getOptions().getMaxLikesPerUserPerCard() == 0) {
			return false;
		}

		return repository.findById(cardId).get().getLikes().stream().filter(e -> e.getOwnerId().equals(ownerId)).count() >= view
				.getOptions().getMaxLikesPerUserPerCard();
	}

	public boolean isLikeLimitReachedByOwner(String ownerId) {
		TKBOptions data = repositoryDataOption.findById(view.getOptions().getId()).get();
		if (data.getMaxLikesPerUser() == 0) {
			return false;
		}

		return repositoryCardLike.countLikesInDataByOwner(boardId, ownerId) >= data.getMaxLikesPerUser();
	}

	public int getCurrentLikesByOwner() {
		TKBCard tmp = repository.findById(cardId).get();
		return tmp.cardLikesByOwnerId(SessionUtils.getSessionId());
	}
	
	public void addLike(String id, String ownerId) {
		TKBCard tmp = repository.findById(cardId).get();
		tmp.getLikes().add(TKBCardLikes.builder().id(id).ownerId(ownerId).likeValue(1).build());
		repository.save(tmp);
	}

	public void removeLike(String ownerId) {
		TKBCard tmp = repository.findById(cardId).get();
		tmp.removeLikeByOwnerId(ownerId);
		repository.save(tmp);
	}

	public void reload() {
		TKBCard tmp = repository.findById(cardId).get();

		// update layout with new missing data
		changeText(tmp.countAllLikes());
		changeButtonIconToLiked(tmp.cardLikesByOwnerId(SessionUtils.getSessionId()) != 0);
	}

	private boolean isLikedByOwner(String ownerId) {
		TKBCard tmp = repository.findById(cardId).get();
		return tmp.cardLikesByOwnerId(SessionUtils.getSessionId()) != 0;
	}

	public void changeText(int likes) {
		if (!btnLike.getText().equals(String.valueOf(likes))) {
			btnLike.setText(String.valueOf(likes));
		}
		
		btnRemoveLike.setText(String.valueOf(getCurrentLikesByOwner()));
	}

	private void changeButtonIconToLiked(boolean liked) {
		if (liked) {
			btnLike.setIcon(VaadinIcon.THUMBS_UP.create());
		} else {
			btnLike.setIcon(VaadinIcon.THUMBS_UP_O.create());
		}
	}

	
}
