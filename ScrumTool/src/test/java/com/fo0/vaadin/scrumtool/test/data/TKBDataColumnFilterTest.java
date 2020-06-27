package com.fo0.vaadin.scrumtool.test.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import com.fo0.vaadin.scrumtool.ui.data.repository.KBDataRepository;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBData;

@SpringBootTest(classes = PersistenceConfig.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test-db")
public class TKBDataColumnFilterTest {

	@Autowired
	private KBDataRepository dataRepository;

	@Test
	public void save() {
		TKBData data = TKBUtils.randomTkbData2();
		dataRepository.save(data);

		TKBData result = dataRepository.filterByColumn("column1");

		if (result != null)
			result.getColumns().forEach(e -> System.out.println("id: " + e.getId() + ", name:" + e.getName()));

		
		assertNotNull(result);
		assertEquals(1, CollectionUtils.size(result.getColumns()));
	}

}
