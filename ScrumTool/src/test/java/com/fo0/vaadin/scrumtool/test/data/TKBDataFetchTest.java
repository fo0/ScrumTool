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
import com.fo0.vaadin.scrumtool.ui.data.repository.KBCardRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBColumnRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBDataRepository;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBData;

@SpringBootTest(classes = PersistenceConfig.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles(Profiles.H2_DRIVER)
public class TKBDataFetchTest {

	@Autowired
	private KBDataRepository dataRepository;

	@Autowired
	private KBColumnRepository columnRepository;

	@Autowired
	private KBCardRepository cardRepository;

	@Autowired
	private KBCardLikesRepository cardLikerepository;

	@Test
	public void findAllColumns() {
		// persist data with 2 columns
		TKBData data = TKBUtils.randomTkbData4();
		dataRepository.save(data);

		assertEquals(3, dataRepository.findById(data.getId()).get().getColumns().size());
	}

}
