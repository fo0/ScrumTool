package com.fo0.vaadin.scrumtool.test.data;

import org.assertj.core.util.Lists;

import com.fo0.vaadin.scrumtool.ui.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCardLikes;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBColumn;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBData;
import com.fo0.vaadin.scrumtool.ui.model.TextItem;

public class TKBUtils {

	public static TKBData randomTkbData_1_column_2_likes() {
		// @formatter:off
		TKBData data = TKBData.builder().build();
		TKBColumn col1 = TKBColumn.builder().name("column1").build();
		TKBCard card1 = TKBCard.builder().build();
		col1.addCard(card1);
		data.addColumn(col1);
		card1.getByType(TextItem.class).get().getLikes().addAll(Lists.newArrayList(TKBCardLikes.builder().ownerId("owner1").likeValue(1).build(),
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build()));
		return data;
		// @formatter:on
	}

	public static TKBData randomTkbData_2_columns_2_likes() {
		// @formatter:off
		TKBData data = TKBData.builder().build();
		TKBColumn col1 = TKBColumn.builder().name("column1").build();
		TKBCard card1 = TKBCard.builder().build();
		col1.addCard(card1);
		data.addColumn(col1);
		card1.getByType(TextItem.class).get().getLikes().addAll(Lists.newArrayList(TKBCardLikes.builder().ownerId("owner1").likeValue(1).build(),
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build()));
		
		TKBColumn col2 = TKBColumn.builder().name("column2").build();
		TKBCard card2 = TKBCard.builder().build();
		col2.addCard(card2);
		data.addColumn(col2);
		card2.getByType(TextItem.class).get().getLikes().addAll(Lists.newArrayList(TKBCardLikes.builder().ownerId("owner2").likeValue(1).build(),
										TKBCardLikes.builder().ownerId("owner2").likeValue(1).build()));
		return data;
		// @formatter:on
	}

	public static TKBData randomTkbData_2_columns_4_likes() {
		// @formatter:off
		TKBData data = TKBData.builder().build();
		TKBColumn col1 = TKBColumn.builder().name("column1").build();
		TKBCard card1 = TKBCard.builder().build();
		col1.addCard(card1);
		data.addColumn(col1);
		card1.getByType(TextItem.class).get().getLikes().addAll(Lists.newArrayList(TKBCardLikes.builder().ownerId("owner1").likeValue(1).build(),
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build()));
		
		TKBColumn col2 = TKBColumn.builder().name("column2").build();
		TKBCard card2 = TKBCard.builder().build();
		col2.addCard(card2);
		data.addColumn(col2);
		card2.getByType(TextItem.class).get().getLikes().addAll(Lists.newArrayList(TKBCardLikes.builder().ownerId("owner1").likeValue(1).build(),
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build()));
		return data;
		// @formatter:on
	}

	public static TKBData randomTkbData_2_columns_1_2_likes() {
		// @formatter:off
		TKBData data = TKBData.builder().build();
		TKBColumn col1 = TKBColumn.builder().name("column1").build();
		TKBCard card1 = TKBCard.builder().build();
		col1.addCard(card1);
		data.addColumn(col1);
		card1.getByType(TextItem.class).get().getLikes().addAll(Lists.newArrayList(TKBCardLikes.builder().ownerId("owner1").likeValue(1).build(),
										TKBCardLikes.builder().ownerId("owner2").likeValue(1).build()));
		
		TKBColumn col2 = TKBColumn.builder().name("column2").build();
		TKBCard card2 = TKBCard.builder().build();
		col2.addCard(card2);
		data.addColumn(col2);
		card2.getByType(TextItem.class).get().getLikes().addAll(Lists.newArrayList(TKBCardLikes.builder().ownerId("owner1").likeValue(1).build(),
										TKBCardLikes.builder().ownerId("owner2").likeValue(1).build()));
		return data;
		// @formatter:on
	}

	public static TKBData randomTkbData_3_columns_2_likes() {
		// @formatter:off
		TKBData data = TKBData.builder().build();
		TKBColumn col1 = TKBColumn.builder().name("column1").build();
		TKBCard card1 = TKBCard.builder().build();
		col1.addCard(card1);
		data.addColumn(col1);
		card1.getByType(TextItem.class).get().getLikes().addAll(Lists.newArrayList(TKBCardLikes.builder().ownerId("owner1").likeValue(1).build(),
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build()));
		
		TKBColumn col2 = TKBColumn.builder().name("column2").build();
		TKBCard card2 = TKBCard.builder().build();
		col2.addCard(card2);
		data.addColumn(col2);
		card2.getByType(TextItem.class).get().getLikes().addAll(Lists.newArrayList(TKBCardLikes.builder().ownerId("owner1").likeValue(1).build(),
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build()));
		
		TKBColumn col3 = TKBColumn.builder().name("column3").build();
		TKBCard card3 = TKBCard.builder().build();
		col3.addCard(card3);
		data.addColumn(col3);
		card3.getByType(TextItem.class).get().getLikes().addAll(Lists.newArrayList(TKBCardLikes.builder().ownerId("owner1").likeValue(1).build(),
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build()));
		return data;	
		// @formatter:on
	}

}
