package com.fo0.vaadin.scrumtool.test.data;

import static org.junit.Assert.assertEquals;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import com.fo0.vaadin.scrumtool.ui.config.Profiles;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBCardRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBColumnRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBDataRepository;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBData;

@SpringBootTest(classes = PersistenceConfig.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles(Profiles.H2_DRIVER)
public class TKBDataDeleteTest {

	@Autowired
	private KBDataRepository dataRepository;

	@Autowired
	private KBColumnRepository columnRepository;

	@Autowired
	private KBCardRepository cardRepository;

	@Test
	public void save() {
		TKBData data = TKBUtils.randomTkbData_1_column_2_likes();
		dataRepository.save(data);

		assertEquals(1, CollectionUtils.size(dataRepository.findAll()));
		assertEquals(1, CollectionUtils.size(columnRepository.findAll()));
		assertEquals(1, CollectionUtils.size(cardRepository.findAll()));
	}

	@Test
	public void deleteData() {
		TKBData data = TKBUtils.randomTkbData_1_column_2_likes();
		dataRepository.save(data);
		dataRepository.delete(data);

		assertEquals(0, CollectionUtils.size(dataRepository.findAll()));
		assertEquals(0, CollectionUtils.size(columnRepository.findAll()));
		assertEquals(0, CollectionUtils.size(cardRepository.findAll()));
	}

	@Test
	public void deleteColumn() {
		TKBData data = TKBUtils.randomTkbData_1_column_2_likes();
		dataRepository.save(data);
		data.getColumns().clear();
		dataRepository.save(data);

		assertEquals(1, CollectionUtils.size(dataRepository.findAll()));
		assertEquals(0, CollectionUtils.size(columnRepository.findAll()));
		assertEquals(0, CollectionUtils.size(cardRepository.findAll()));
	}

}
