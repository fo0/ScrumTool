package com.fo0.vaadin.scrumtool.ui.export;

import java.util.Comparator;
import java.util.List;

import com.fo0.vaadin.scrumtool.ui.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBColumn;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBData;
import com.google.common.collect.Lists;

import net.steppschuh.markdowngenerator.table.Table;

public class VanillaMarkDown {

	public static List<String> create(TKBData data) {
		List<String> list = Lists.newArrayList();

		list.add("# Kanban Board");
		list.add("");

		data.getColumns().stream().sorted(Comparator.comparing(TKBColumn::getDataOrder)).forEachOrdered(column -> {
			list.add("# " + column.getName());
			list.add(VanillaMarkDown.createColumn2(column));
			list.add("");
		});

		return list;
	}

	private static String createColumn2(TKBColumn column) {
		Table.Builder tableBuilder = new Table.Builder();

		tableBuilder.addRow("No", "Likes", "Type", "Description", "Comments");

		column.getCards().stream().sorted(Comparator.comparing(TKBCard::getDataOrder)).forEachOrdered(card -> {
			// @formatter:off
			tableBuilder.addRow(
				card.getDataOrder(), 
				ExportUtils.getItem(card).countAllLikes(),
				card.getType(), 
				ExportUtils.getText(card), 
				ExportUtils.getComments(EExportType.Markdown, card));
			// @formatter:on
		});

		return tableBuilder.build().toString();
	}

	private static List<String> createColumn(TKBColumn column) {
		List<String> list = Lists.newArrayList();

		list.add("### " + column.getName());
		list.add("| No | Likes | Type | Description | Comments |");
		list.add("| :---: | :----: | :----: | :------ | :------");

		column.getCards().stream().sorted(Comparator.comparing(TKBCard::getDataOrder)).forEachOrdered(card -> {
			// @formatter:off
			list.add(String.format("| %d | %d | %s | %s | %s |", 
					card.getDataOrder(), 
					ExportUtils.getItem(card).countAllLikes(),
					card.getType(), 
					ExportUtils.getText(card), 
					ExportUtils.getComments(EExportType.Markdown, card)));
			// @formatter:on
		});

		return list;
	}

}
