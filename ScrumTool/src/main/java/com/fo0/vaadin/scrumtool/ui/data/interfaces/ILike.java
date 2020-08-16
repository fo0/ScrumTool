package com.fo0.vaadin.scrumtool.ui.data.interfaces;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.fo0.vaadin.scrumtool.ui.data.table.TKBCardLikes;

public interface ILike {

	public String getId();

	public void setId(String id);

	public default void removeLikeByOwnerId(String ownerId) {
		if (CollectionUtils.isEmpty(getLikes())) {
			return;
		}

		getLikes().stream().filter(e -> e.getOwnerId().equals(ownerId)).findFirst().ifPresent(getLikes()::remove);
	};

	public default int cardLikesByOwnerId(String ownerId) {
		if (CollectionUtils.isEmpty(getLikes())) {
			return 0;
		}

		return getLikes().stream().filter(e -> e.getOwnerId().equals(ownerId)).mapToInt(TKBCardLikes::getLikeValue).sum();
	};

	public default int countAllLikes() {
		if (CollectionUtils.isEmpty(getLikes())) {
			return 0;
		}

		return getLikes().stream().mapToInt(TKBCardLikes::getLikeValue).sum();
	};

	public default void clear() {
		if (CollectionUtils.isEmpty(getLikes())) {
			return;
		}

		getLikes().clear();
	}

	public List<TKBCardLikes> getLikes();

}
