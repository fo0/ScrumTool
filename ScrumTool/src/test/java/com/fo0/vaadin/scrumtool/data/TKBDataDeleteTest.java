package com.fo0.vaadin.scrumtool.data;

import static org.junit.Assert.assertEquals;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.fo0.vaadin.scrumtool.data.repository.KBCardLikesRepository;
import com.fo0.vaadin.scrumtool.data.repository.KBCardRepository;
import com.fo0.vaadin.scrumtool.data.repository.KBColumnRepository;
import com.fo0.vaadin.scrumtool.data.repository.KBDataRepository;
import com.fo0.vaadin.scrumtool.data.table.TKBData;

@SpringBootTest(classes = PersistenceConfig.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class TKBDataDeleteTest {

	@Autowired
	private KBDataRepository dataRepository;

	@Autowired
	private KBColumnRepository columnRepository;

	@Autowired
	private KBCardRepository cardRepository;

	@Autowired
	private KBCardLikesRepository cardLikerepository;

	@Test
	public void save() {
		TKBData data = TKBUtils.randomTkbData();
		dataRepository.save(data);

		assertEquals(1, CollectionUtils.size(dataRepository.findAll()));
		assertEquals(1, CollectionUtils.size(columnRepository.findAll()));
		assertEquals(1, CollectionUtils.size(cardRepository.findAll()));
		assertEquals(2, CollectionUtils.size(cardLikerepository.findAll()));
	}

	@Test
	public void deleteData() {
		TKBData data = TKBUtils.randomTkbData();
		dataRepository.save(data);
		dataRepository.delete(data);

		assertEquals(0, CollectionUtils.size(dataRepository.findAll()));
		assertEquals(0, CollectionUtils.size(columnRepository.findAll()));
		assertEquals(0, CollectionUtils.size(cardRepository.findAll()));
		assertEquals(0, CollectionUtils.size(cardLikerepository.findAll()));
	}

	@Test
	public void deleteColumn() {
		TKBData data = TKBUtils.randomTkbData();
		dataRepository.save(data);
		data.getColumns().clear();
		dataRepository.save(data);

		assertEquals(1, CollectionUtils.size(dataRepository.findAll()));
		assertEquals(0, CollectionUtils.size(columnRepository.findAll()));
		assertEquals(0, CollectionUtils.size(cardRepository.findAll()));
		assertEquals(0, CollectionUtils.size(cardLikerepository.findAll()));
	}

}
