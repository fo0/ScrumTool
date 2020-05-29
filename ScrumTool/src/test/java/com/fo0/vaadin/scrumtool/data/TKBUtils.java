package com.fo0.vaadin.scrumtool.data;

import com.fo0.vaadin.scrumtool.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.data.table.TKBCardLikes;
import com.fo0.vaadin.scrumtool.data.table.TKBColumn;
import com.fo0.vaadin.scrumtool.data.table.TKBData;
import com.fo0.vaadin.scrumtool.data.table.TKBOptions;
import com.google.common.collect.Sets;

public class TKBUtils {

	public static TKBData randomTkbData() {
		//@formatter:off
		return TKBData.builder()
				.options(TKBOptions.builder().build())
				.columns(Sets.newHashSet(TKBColumn.builder()
						.cards(Sets.newHashSet(TKBCard.builder()
								.likes(Sets.newHashSet(
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build(),
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build()
										))
								.build()))
						.build()))
				.build();
		//@formatter:on
	}

}
