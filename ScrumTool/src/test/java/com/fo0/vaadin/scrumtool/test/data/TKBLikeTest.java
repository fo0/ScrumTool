package com.fo0.vaadin.scrumtool.test.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.fo0.vaadin.scrumtool.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.data.table.TKBCardLikes;
import com.fo0.vaadin.scrumtool.data.table.TKBColumn;
import com.fo0.vaadin.scrumtool.data.table.TKBData;
import com.google.common.collect.Sets;

@SpringBootTest
public class TKBLikeTest {

	@Test
	public void same_owner_has_2_likes_1_card() {
		//@formatter:off
		TKBData data = TKBData.builder()
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

		assertNotNull(data);

		assertEquals(2, data.cardLikesByOwnerId("owner1"));
		assertEquals(0, data.cardLikesByOwnerId("owner2"));
	}

	@Test
	public void same_owner_has_4_likes_2_columns() {
		//@formatter:off
		TKBData data = TKBData.builder()
				.columns(Sets.newHashSet(TKBColumn.builder()
						.cards(Sets.newHashSet(TKBCard.builder()
								.likes(Sets.newHashSet(
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build(),
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build()
										))
								.build()))
						.build(),
						
						TKBColumn.builder()
						.cards(Sets.newHashSet(TKBCard.builder()
								.likes(Sets.newHashSet(
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build(),
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build()
										))
								.build()))
						.build()))
				.build();
		//@formatter:on

		assertNotNull(data);

		assertEquals(4, data.cardLikesByOwnerId("owner1"));
		assertEquals(0, data.cardLikesByOwnerId("owner2"));
	}
	
	@Test
	public void two_owner_has_2_likes_1_columns() {
		//@formatter:off
		TKBData data = TKBData.builder()
				.columns(Sets.newHashSet(TKBColumn.builder()
						.cards(Sets.newHashSet(TKBCard.builder()
								.likes(Sets.newHashSet(
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build(),
										TKBCardLikes.builder().ownerId("owner2").likeValue(1).build()
										))
								.build()))
						.build(),
						
						TKBColumn.builder()
						.cards(Sets.newHashSet(TKBCard.builder()
								.likes(Sets.newHashSet(
										TKBCardLikes.builder().ownerId("owner1").likeValue(1).build(),
										TKBCardLikes.builder().ownerId("owner2").likeValue(1).build()
										))
								.build()))
						.build()))
				.build();
		//@formatter:on

		assertNotNull(data);

		assertEquals(2, data.cardLikesByOwnerId("owner1"));
		assertEquals(2, data.cardLikesByOwnerId("owner2"));
	}

}
