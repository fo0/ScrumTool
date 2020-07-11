package com.fo0.vaadin.scrumtool.test.data;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.fo0.vaadin.scrumtool.ui.config.Profiles;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBDataRepository;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBColumn;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBData;

@SpringBootTest(classes = PersistenceConfig.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles(Profiles.H2_DRIVER)
public class TKBDataAddColumnTest {

	@Autowired
	private KBDataRepository dataRepository;

	@Transactional
	@Test
	public void findAllColumnsByRepo() {
		TKBData data = TKBData.builder().build();
		dataRepository.save(data);

		data = dataRepository.insertColumnById(data.getId(), TKBColumn.builder().name("Lazy").build());

		assertEquals(1, dataRepository.findByIdFetched(data.getId()).getColumns().size());
	}

}
