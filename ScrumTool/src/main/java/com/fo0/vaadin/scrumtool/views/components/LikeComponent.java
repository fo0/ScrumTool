package com.fo0.vaadin.scrumtool.views.components;

import com.fo0.vaadin.scrumtool.broadcast.BroadcasterCards;
import com.fo0.vaadin.scrumtool.config.Config;
import com.fo0.vaadin.scrumtool.data.repository.KBCardRepository;
import com.fo0.vaadin.scrumtool.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.data.table.TKBCardLikes;
import com.fo0.vaadin.scrumtool.session.SessionUtils;
import com.fo0.vaadin.scrumtool.utils.SpringContext;
import com.fo0.vaadin.scrumtool.utils.Utils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class LikeComponent extends HorizontalLayout {

	private static final long serialVersionUID = -2483871323771596716L;

	private KBCardRepository repository = SpringContext.getBean(KBCardRepository.class);

	private String cardId;

	private Button btnLike;

	public LikeComponent(String cardId) {
		this.cardId = cardId;

		btnLike = new Button(VaadinIcon.THUMBS_UP.create());
		btnLike.setText(String.valueOf(0));
		btnLike.setWidthFull();
		btnLike.addClickListener(e -> {
			if (!islikeAlreadyExistsByOwner(SessionUtils.getSessionId())) {
				addLike(Utils.randomId(), SessionUtils.getSessionId());
				BroadcasterCards.broadcast(cardId, "update");
//				reload();
			} else {
				Notification.show("You already liked the card", Config.NOTIFICATION_DURATION, Position.MIDDLE);
			}
		});

		add(btnLike);

		setAlignItems(Alignment.END);
		setDefaultVerticalComponentAlignment(Alignment.CENTER);
	}

	public boolean islikeAlreadyExistsByOwner(String ownerId) {
		return repository.findById(cardId).get().getLikes().stream().anyMatch(e -> e.getOwnerId().equals(ownerId));
	}

	public void addLike(String id, String ownerId) {
		TKBCard tmp = repository.findById(cardId).get();
		tmp.getLikes().add(TKBCardLikes.builder().id(id).ownerId(ownerId).likeValue(1).build());
		repository.save(tmp);
	}

	public void changeText(int likes) {
		btnLike.setText(String.valueOf(likes));
	}

	public void reload() {
		TKBCard tmp = repository.findById(cardId).get();

		// update layout with new missing data
		changeText(tmp.countAllLikes());
	}

}
