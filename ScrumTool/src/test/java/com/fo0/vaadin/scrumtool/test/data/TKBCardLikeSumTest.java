package com.fo0.vaadin.scrumtool.test.data;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import com.fo0.vaadin.scrumtool.ui.config.Profiles;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBCardLikesRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBDataRepository;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBData;

@SpringBootTest(classes = PersistenceConfig.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles(Profiles.H2_DRIVER)
public class TKBCardLikeSumTest {

	@Autowired
	private KBDataRepository dataRepository;

	@Autowired
	private KBCardLikesRepository repository;

	@Test
	public void cardLikesInDataByOwnerTest() {
		TKBData data = dataRepository.save(TKBUtils.randomTkbData4());

		assertEquals(6, repository.countLikesInDataByOwner(data.getId(), "owner1"));
	}
	
	@Test
	public void cardLikesInDataByOwnerNullTest() {
		TKBData data = dataRepository.save(TKBData.builder().build());

		assertEquals(0, repository.countLikesInDataByOwner(data.getId(), "owner1"));
	}

}
