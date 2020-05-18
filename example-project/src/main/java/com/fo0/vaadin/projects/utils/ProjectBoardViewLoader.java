package com.fo0.vaadin.projects.utils;

import java.util.Set;
import java.util.stream.IntStream;

import org.apache.commons.collections4.CollectionUtils;

import com.fo0.vaadin.projects.data.table.ProjectData;
import com.fo0.vaadin.projects.data.table.ProjectDataCard;
import com.fo0.vaadin.projects.data.table.ProjectDataColumn;
import com.fo0.vaadin.projects.views.ProjectBoardView;
import com.fo0.vaadin.projects.views.components.ColumnComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ProjectBoardViewLoader {

	public static void createMissingCards(ProjectBoardView view, HorizontalLayout currentColumns, Set<ProjectDataColumn> latestColumns) {
		latestColumns.stream().forEachOrdered(latestColumnAtDb -> {
			log.info("[CARD] iterating over lastest columns");
			IntStream.range(0, currentColumns.getComponentCount()).forEachOrdered(currentColumnIdx -> {
				log.info("[CARD] iterating over current column component");
				ColumnComponent ccc = (ColumnComponent) currentColumns.getComponentAt(currentColumnIdx);
				if (ccc.getId().get().equals(latestColumnAtDb.getId())) {
					log.info("[CARD] iterating over cards components");
					latestColumnAtDb.getCards().stream().forEachOrdered(pdc -> {
						ProjectDataCard pdcc = ccc.getCardById(pdc.getId());
						if (pdcc == null) {
							log.info("update card for column: {} - {} - {}", ccc.getId().get(), pdc.getId(), pdc.getText());
							ccc.getProductDataColumn().getCards().forEach(log::info);
							view.addCard(ccc.getId().get(), pdc.getId(), pdc.getText());
						} else {
							log.info("no card update found: " + pdcc.getId());
						}
					});
				}
			});
		});
	}

	public static void createMissingColumns(ProjectBoardView view, HorizontalLayout currentColumns, Set<ProjectDataColumn> latestColumns) {
		latestColumns.stream().forEachOrdered(latestColumnAtDb -> {
			int compIndex = currentColumns.getComponentCount() == 0 ? 0
					: IntStream.range(0, currentColumns.getComponentCount())
							.filter(cc -> !currentColumns.getComponentAt(cc).getId().get().equals(latestColumnAtDb.getId())).findFirst()
							.orElse(-1);
			if (compIndex != -1) {
				log.info("[COLUMN] add missing column: {} - {}", latestColumnAtDb.getId(), latestColumnAtDb.getName());
				view.addColumn(latestColumnAtDb.getId(), latestColumnAtDb.getName());
			} else {
				log.info("[COLUMN] column aleady exists: {} - {}", latestColumnAtDb.getId(), latestColumnAtDb.getName());
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

		createMissingCards(view, view.getColumns(), latestData.getColumns());

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
