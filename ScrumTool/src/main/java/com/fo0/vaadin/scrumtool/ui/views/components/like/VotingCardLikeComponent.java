package com.fo0.vaadin.scrumtool.ui.views.components.like;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import com.fo0.vaadin.scrumtool.ui.broadcast.BroadcasterCard;
import com.fo0.vaadin.scrumtool.ui.config.Config;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBCardRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBDataRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBOptionRepository;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCardLikes;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBOptions;
import com.fo0.vaadin.scrumtool.ui.model.VotingItem;
import com.fo0.vaadin.scrumtool.ui.session.SessionUtils;
import com.fo0.vaadin.scrumtool.ui.utils.SpringContext;
import com.fo0.vaadin.scrumtool.ui.views.KanbanView;
import com.fo0.vaadin.scrumtool.ui.views.components.ToolTip;
import com.google.common.collect.Lists;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class VotingCardLikeComponent extends HorizontalLayout {

	private static final long serialVersionUID = -2483871323771596716L;

	private KanbanView view;

	private KBCardRepository repository = SpringContext.getBean(KBCardRepository.class);
	private KBDataRepository repositoryData = SpringContext.getBean(KBDataRepository.class);
	private KBOptionRepository repositoryDataOption = SpringContext.getBean(KBOptionRepository.class);

	private String boardId;
	private String votingId;
	private String cardId;

	private Button btnLike;
	private Button btnRemoveLike;

	public VotingCardLikeComponent(KanbanView view, String boardId, String cardId, String votingId) {
		this.view = view;
		this.boardId = boardId;
		this.votingId = votingId;
		this.cardId = cardId;
		setId(votingId);

		btnLike = new Button(VaadinIcon.THUMBS_UP_O.create());
		ToolTip.add(btnLike, "Like the card");
		btnLike.setText(String.valueOf(getCurrentLikes()));
		btnLike.setWidthFull();
		btnLike.addClickListener(e -> {
			if (islikeLimitAlreadyExistsByOwner()) {
				Notification.show("You already liked the card", Config.NOTIFICATION_DURATION, Position.MIDDLE);
				return;
			}

			if (isLikeLimitReachedByOwner()) {
				Notification.show("You already reached the like limit", Config.NOTIFICATION_DURATION, Position.MIDDLE);
				return;
			}

			addLike();
			BroadcasterCard.broadcast(cardId, "update");
		});
		add(btnLike);

		btnRemoveLike = new Button(VaadinIcon.THUMBS_DOWN_O.create());
		ToolTip.add(btnRemoveLike, "Remove your like");
		btnRemoveLike.setText(String.valueOf(getCurrentLikesByOwner()));
		btnRemoveLike.setWidthFull();
		btnRemoveLike.addClickListener(e -> {
			if (!isLikedByOwner()) {
				Notification.show("You must like the card, bevor remove", Config.NOTIFICATION_DURATION, Position.MIDDLE);
				return;
			}

			removeLike();
			BroadcasterCard.broadcast(cardId, "update");
		});
		add(btnRemoveLike);
		setMargin(false);
		setPadding(false);
		setSpacing(false);
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
	}

	public boolean islikeLimitAlreadyExistsByOwner() {
		if (view.getOptions().getMaxLikesPerUserPerCard() == 0) {
			return false;
		}

		TKBCard tmp = repository.findById(cardId).get();
		return tmp.getByTypeAsList(VotingItem.class).orElseGet(() -> Lists.newArrayList()).stream().map(VotingItem::getLikes)
				.flatMap(Collection::stream).filter(e -> e.getOwnerId().equals(SessionUtils.getSessionId()))
				.count() >= view.getOptions().getMaxLikesPerUserPerCard();
	}

	public boolean isLikeLimitReachedByOwner() {
		TKBOptions data = repositoryDataOption.findById(view.getOptions().getId()).get();
		if (data.getMaxLikesPerUser() == 0) {
			return false;
		}

		TKBCard tmp = repository.findById(cardId).get();
		return tmp.getByType(VotingItem.class).orElseGet(() -> VotingItem.builder().build())
				.cardLikesByOwnerId(SessionUtils.getSessionId()) >= data.getMaxLikesPerUser();
	}

	public int getCurrentLikes() {
		TKBCard tmp = repository.findById(cardId).get();
		VotingItem item = tmp.getByType(VotingItem.class).orElseGet(() -> VotingItem.builder().build());
		return item != null ? item.countAllLikes() : null;
	}

	public int getCurrentLikesByOwner() {
		TKBCard tmp = repository.findById(cardId).get();
		return tmp.getByType(VotingItem.class).orElseGet(() -> VotingItem.builder().build())
				.cardLikesByOwnerId(SessionUtils.getSessionId());
	}

	public void addLike() {
		TKBCard tmp = repository.findById(cardId).get();
		VotingItem item = getCard(tmp);
		item.getLikes().add(TKBCardLikes.builder().ownerId(SessionUtils.getSessionId()).likeValue(1).build());
		tmp.setTextByType(item);
		repository.save(tmp);
	}

	public void removeLike() {
		TKBCard tmp = repository.findById(cardId).get();
		VotingItem item = getCard(tmp);
		item.removeLikeByOwnerId(SessionUtils.getSessionId());
		tmp.setTextByType(item);
		repository.save(tmp);
	}

	public void reload() {
		// update layout with new missing data
		changeText(getCard().countAllLikes());
		changeButtonIconToLiked(getCard().cardLikesByOwnerId(SessionUtils.getSessionId()) != 0);
	}

	private boolean isLikedByOwner() {
		return getCard().cardLikesByOwnerId(SessionUtils.getSessionId()) != 0;
	}

	public VotingItem getCard(TKBCard card) {
		return card.getByTypeAsList(VotingItem.class).orElseGet(() -> Lists.newArrayList()).stream()
				.filter(e -> StringUtils.equals(e.getId(), votingId)).findFirst().orElseGet(() -> VotingItem.builder().build());
	}

	public VotingItem getCard() {
		TKBCard tmp = repository.findById(cardId).get();
		return getCard(tmp);
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
