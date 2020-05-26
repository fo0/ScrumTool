package com.fo0.vaadin.scrumtool.views.components;

import com.fo0.vaadin.scrumtool.broadcast.BroadcasterCardLikes;
import com.fo0.vaadin.scrumtool.config.Config;
import com.fo0.vaadin.scrumtool.data.repository.KBCardRepository;
import com.fo0.vaadin.scrumtool.data.repository.KBDataRepository;
import com.fo0.vaadin.scrumtool.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.data.table.TKBCardLikes;
import com.fo0.vaadin.scrumtool.data.table.TKBData;
import com.fo0.vaadin.scrumtool.session.SessionUtils;
import com.fo0.vaadin.scrumtool.utils.SpringContext;
import com.fo0.vaadin.scrumtool.utils.Utils;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.shared.Registration;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class LikeComponent extends HorizontalLayout {

	private static final long serialVersionUID = -2483871323771596716L;

	private KBCardRepository repository = SpringContext.getBean(KBCardRepository.class);
	private KBDataRepository repositoryData = SpringContext.getBean(KBDataRepository.class);

	private String boardId;
	private String cardId;

	private Button btnLike;

	private Registration broadcasterRegistration;

	public LikeComponent(String boardId, String cardId, int likes) {
		this.boardId = boardId;
		this.cardId = cardId;
		setId(cardId);

		btnLike = new Button(VaadinIcon.THUMBS_UP_O.create());
		btnLike.setText(String.valueOf(likes));
		btnLike.setWidthFull();
		btnLike.addClickListener(e -> {
			if (islikeAlreadyExistsByOwner(SessionUtils.getSessionId())) {
				Notification.show("You already liked the card", Config.NOTIFICATION_DURATION, Position.MIDDLE);
				return;
			}

			if (isLikeLimitReachedByOwner(SessionUtils.getSessionId())) {
				Notification.show("You already reached the like limit", Config.NOTIFICATION_DURATION, Position.MIDDLE);
				return;
			}

			addLike(Utils.randomId(), SessionUtils.getSessionId());
			BroadcasterCardLikes.broadcast(cardId, "update");
		});

		add(btnLike);

		setAlignItems(Alignment.END);
		setDefaultVerticalComponentAlignment(Alignment.CENTER);
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		UI ui = UI.getCurrent();
		broadcasterRegistration = BroadcasterCardLikes.register(getId().get(), event -> {
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

	public boolean islikeAlreadyExistsByOwner(String ownerId) {
		return repository.findById(cardId).get().getLikes().stream().anyMatch(e -> e.getOwnerId().equals(ownerId));
	}

	public boolean isLikeLimitReachedByOwner(String ownerId) {
		TKBData data = repositoryData.findById(boardId).get();
		if (data.getOptions().getMaxLikesPerUser() == 0) {
			return false;
		}

		return data.cardLikesByOwnerId(ownerId) >= data.getOptions().getMaxLikesPerUser();
	}

	public void addLike(String id, String ownerId) {
		TKBCard tmp = repository.findById(cardId).get();
		tmp.getLikes().add(TKBCardLikes.builder().id(id).ownerId(ownerId).likeValue(1).build());
		repository.save(tmp);
	}

	public void reload() {
		TKBCard tmp = repository.findById(cardId).get();

		// update layout with new missing data
		changeText(tmp.countAllLikes());
		changeLikeButtonIconToLiked(tmp.cardLikesByOwnerId(SessionUtils.getSessionId()) != 0);
	}

	public void changeText(int likes) {
		btnLike.setText(String.valueOf(likes));
	}

	private void changeLikeButtonIconToLiked(boolean liked) {
		if (liked) {
			btnLike.setIcon(VaadinIcon.THUMBS_UP.create());
		} else {
			btnLike.setIcon(VaadinIcon.THUMBS_UP_O.create());
		}
	}

}
