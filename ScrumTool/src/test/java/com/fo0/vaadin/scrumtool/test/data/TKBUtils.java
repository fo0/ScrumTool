package com.fo0.vaadin.scrumtool.test.data;

import com.fo0.vaadin.scrumtool.ui.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCardLikes;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBColumn;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBData;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBOptions;
import com.fo0.vaadin.scrumtool.ui.utils.Utils;
import com.google.common.collect.Sets;

import lombok.Builder;

public class TKBUtils {

	public static TKBData randomTkbData() {
		//@formatter:off
		return TKBData.builder()
				.options(TKBOptions.builder().build())
				.columns(Sets.newHashSet(TKBColumn.builder()
						.name("column1")
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
	
	public static TKBData randomTkbData2() {
		//@formatter:off
		return TKBData.builder()
				.options(TKBOptions.builder().build())
				.columns(Sets.newHashSet(TKBColumn.builder()
						.name("column1")
						.cards(Sets.newHashSet(TKBCard.builder()
								.likes(Sets.newHashSet(
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build(),
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build()
										))
								.build()))
						.build(),
						TKBColumn.builder()
						.name("column2")
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
	
	public static TKBData randomTkbData3() {
		//@formatter:off
		TKBData data =  TKBData.builder().build();
		data.setOptions(TKBOptions.builder().build());
		data.setColumns(Sets.newHashSet(TKBColumn.builder()
						.name("column1")
						.cards(Sets.newHashSet(TKBCard.builder()
								.likes(Sets.newHashSet(
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build(),
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build()
										))
								.build()))
						.build(),
						TKBColumn.builder()
						.name("column2")
						.cards(Sets.newHashSet(TKBCard.builder()
								.likes(Sets.newHashSet(
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build(),
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build()
										))
								.build()))
						.build()));
		return data;
		//@formatter:on
	}
	

	public static TKBData randomTkbData4() {
		//@formatter:off
		TKBData data =  TKBData.builder().build();
		data.setOptions(TKBOptions.builder().build());
		data.setColumns(Sets.newHashSet(TKBColumn.builder()
						.name("column1")
						.cards(Sets.newHashSet(TKBCard.builder()
								.likes(Sets.newHashSet(
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build(),
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build()
										))
								.build()))
						.build(),
						TKBColumn.builder()
						.name("column2")
						.cards(Sets.newHashSet(TKBCard.builder()
								.likes(Sets.newHashSet(
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build(),
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build()
										))
								.build()))
						.build(),
						TKBColumn.builder()
						.name("column3")
						.cards(Sets.newHashSet(TKBCard.builder()
								.likes(Sets.newHashSet(
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build(),
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build()
										))
								.build()))
						.build()));
		return data;
		//@formatter:on
	}


}
