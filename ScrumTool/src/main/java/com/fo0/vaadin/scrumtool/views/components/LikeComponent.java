package com.fo0.vaadin.scrumtool.views.components;

import com.fo0.vaadin.scrumtool.data.repository.KBCardRepository;
import com.fo0.vaadin.scrumtool.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.data.table.TKBCardLikes;
import com.fo0.vaadin.scrumtool.session.SessionUtils;
import com.fo0.vaadin.scrumtool.utils.SpringContext;
import com.fo0.vaadin.scrumtool.utils.Utils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class LikeComponent extends HorizontalLayout {

	private static final long serialVersionUID = -2483871323771596716L;

	private KBCardRepository repository = SpringContext.getBean(KBCardRepository.class);

	private String cardId;

	private Button btnLike;
	private Label likesLabel;

	public LikeComponent(String cardId) {
		this.cardId = cardId;

		likesLabel = new Label();
		add(likesLabel);
		changeText(0);

		btnLike = new Button(VaadinIcon.THUMBS_UP.create());
		btnLike.addClickListener(e -> {
			if (!islikeAlreadyExistsByOwner(SessionUtils.getSessionId())) {
				addLike(Utils.randomId(), SessionUtils.getSessionId());
				reload();
			} else {
				Notification.show("You already liked the card", 3000, Position.MIDDLE);
			}
		});

		add(btnLike);

		getStyle().set("border", "1px solid black");
		setAlignItems(Alignment.CENTER);
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
		likesLabel.setText(String.valueOf(likes));
	}

	public void reload() {
		TKBCard tmp = repository.findById(cardId).get();

		// update layout with new missing data
		changeText(tmp.countAllLikes());
	}

}
