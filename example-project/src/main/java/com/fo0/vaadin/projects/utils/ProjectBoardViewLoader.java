package com.fo0.vaadin.projects.utils;

import java.util.Set;
import java.util.stream.IntStream;

import org.apache.commons.collections4.CollectionUtils;

import com.fo0.vaadin.projects.data.table.ProjectData;
import com.fo0.vaadin.projects.data.table.ProjectDataColumn;
import com.fo0.vaadin.projects.views.ProjectBoardView;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ProjectBoardViewLoader {

	public static void createMissingColumns(ProjectBoardView view, HorizontalLayout columns, Set<ProjectDataColumn> list) {
		list.stream().forEachOrdered(pdc -> {
			int compIndex = columns.getComponentCount() == 0 ? 0
					: IntStream.range(0, columns.getComponentCount())
							.filter(cc -> !columns.getComponentAt(cc).getId().get().equals(pdc.getId())).findFirst().orElse(-1);
			if (compIndex != -1) {
				log.info("add missing column: {} - {}", pdc.getId(), pdc.getName());
				view.addColumn(pdc.getId(), pdc.getName());
			} else {
				log.info("column aleady exists: {} - {}", pdc.getId(), pdc.getName());
			}
		});
	}

	public static void loadData(ProjectBoardView view, ProjectData latestData) {
		if (latestData == null) {
			log.info("no data found");
			return;
		}

		if (CollectionUtils.isEmpty(latestData.getColumns())) {
			log.info("no columns found");
			return;
		}

		createMissingColumns(view, view.getColumns(), latestData.getColumns());

		// data.getColumns().stream().forEachOrdered(c -> {
		// IntStream.range(0, columns.getComponentCount()).forEachOrdered(cc -> {
		// Component comp = columns.getComponentAt(cc);
		//
		// });
		// });
		//
		// IntStream.range(0, columns.getComponentCount()).forEachOrdered(e -> {
		// Component c = columns.getComponentAt(e);
		// if (!c.getId().isPresent()) {
		// return;
		// }
		//
		// ProjectDataColumn pdc = data.getById(c.getId().get());
		// });
	}

}
