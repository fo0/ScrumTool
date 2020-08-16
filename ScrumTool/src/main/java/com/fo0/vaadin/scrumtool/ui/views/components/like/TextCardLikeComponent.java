package com.fo0.vaadin.scrumtool.ui.views.components.like;

import java.util.function.UnaryOperator;

import com.fo0.vaadin.scrumtool.ui.broadcast.BroadcasterCard;
import com.fo0.vaadin.scrumtool.ui.config.Config;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBCardRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBDataRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBOptionRepository;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCardLikes;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBOptions;
import com.fo0.vaadin.scrumtool.ui.model.TextItem;
import com.fo0.vaadin.scrumtool.ui.session.SessionUtils;
import com.fo0.vaadin.scrumtool.ui.utils.SpringContext;
import com.fo0.vaadin.scrumtool.ui.views.KanbanView;
import com.fo0.vaadin.scrumtool.ui.views.components.ToolTip;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class TextCardLikeComponent extends VerticalLayout {

	private static final long serialVersionUID = -2483871323771596716L;

	private KanbanView view;

	private KBCardRepository repository = SpringContext.getBean(KBCardRepository.class);
	private KBDataRepository repositoryData = SpringContext.getBean(KBDataRepository.class);
	private KBOptionRepository repositoryDataOption = SpringContext.getBean(KBOptionRepository.class);

	private String boardId;
	private String cardId;

	private Button btnLike;
	private Button btnRemoveLike;

	public TextCardLikeComponent(KanbanView view, String boardId, String cardId) {
		this.view = view;
		this.boardId = boardId;
		this.cardId = cardId;
		setId(cardId);

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

		return repository.findById(cardId).get().getByType(TextItem.class).orElseGet(() -> TextItem.builder().build()).getLikes().stream()
				.filter(e -> e.getOwnerId().equals(SessionUtils.getSessionId())).count() >= view.getOptions().getMaxLikesPerUserPerCard();
	}

	public boolean isLikeLimitReachedByOwner() {
		TKBOptions data = repositoryDataOption.findById(view.getOptions().getId()).get();
		if (data.getMaxLikesPerUser() == 0) {
			return false;
		}

		TKBCard tmp = repository.findById(cardId).get();
		return tmp.getByType(TextItem.class).orElseGet(() -> TextItem.builder().build())
				.cardLikesByOwnerId(SessionUtils.getSessionId()) >= data.getMaxLikesPerUser();
	}

	public int getCurrentLikes() {
		TKBCard tmp = repository.findById(cardId).get();
		return tmp.getByType(TextItem.class).orElseGet(() -> TextItem.builder().build()).countAllLikes();
	}

	public int getCurrentLikesByOwner() {
		TKBCard tmp = repository.findById(cardId).get();
		return tmp.getByType(TextItem.class).orElseGet(() -> TextItem.builder().build()).cardLikesByOwnerId(SessionUtils.getSessionId());
	}

	public void addLike() {
		updateItem(e -> {
			e.getLikes().add(TKBCardLikes.builder().ownerId(SessionUtils.getSessionId()).likeValue(1).build());
			return e;
		});
	}

	public void removeLike() {
		updateItem(e -> {
			e.removeLikeByOwnerId(SessionUtils.getSessionId());
			return e;
		});
	}

	public void updateItem(UnaryOperator<TextItem> update) {
		TKBCard tmp = getCard();
		TextItem item = getItem(tmp);
		tmp.setTextByType(update.apply(item));
		repository.save(tmp);
	}

	public TextItem getItem(TKBCard card) {
		return card.getByType(TextItem.class).get();
	}

	public TextItem getItem() {
		TKBCard tmp = repository.findById(cardId).get();
		return getItem(tmp);
	}

	public TKBCard getCard() {
		return repository.findById(cardId).get();
	}

	public void reload() {
		TKBCard tmp = repository.findById(cardId).get();

		// update layout with new missing data
		changeText(tmp.getByType(TextItem.class).orElseGet(() -> TextItem.builder().build()).countAllLikes());
		changeButtonIconToLiked(tmp.getByType(TextItem.class).orElseGet(() -> TextItem.builder().build())
				.cardLikesByOwnerId(SessionUtils.getSessionId()) != 0);
	}

	private boolean isLikedByOwner() {
		TKBCard tmp = repository.findById(cardId).get();
		return tmp.getByType(TextItem.class).orElseGet(() -> TextItem.builder().build())
				.cardLikesByOwnerId(SessionUtils.getSessionId()) != 0;
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
