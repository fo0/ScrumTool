package com.fo0.vaadin.scrumtool.ui.export;

import java.util.Comparator;
import java.util.List;

import com.fo0.vaadin.scrumtool.ui.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBColumn;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBData;
import com.fo0.vaadin.scrumtool.ui.model.TextItem;
import com.google.common.collect.Lists;

public class JiraMarkDown {

	public static List<String> create(TKBData data) {
		List<String> list = Lists.newArrayList();

		list.add("h1. Kanban Board");
		list.add("");

		data.getColumns().stream().sorted(Comparator.comparing(TKBColumn::getDataOrder)).forEachOrdered(column -> {
			list.addAll(createColumn(column));
			list.add("");
		});

		return list;
	}

	private static List<String> createColumn(TKBColumn column) {
		List<String> list = Lists.newArrayList();

		list.add("h2. " + column.getName());
		list.add("|| No || Likes || Description || Description || Comments ||");

		column.getCards().stream().sorted(Comparator.comparing(TKBCard::getDataOrder)).forEachOrdered(card -> {
			list.add(String.format("| %d | %d | %s | %s | %s |", 
					card.getDataOrder(),
					ExportUtils.getItem(card).countAllLikes(),
					getItem(card).getText(),
					ExportUtils.getText(card), 
					ExportUtils.getComments(EExportType.Markup_Confluence, card)));
		});

		return list;
	}

	private static TextItem getItem(TKBCard card) {
		return card.getByType(TextItem.class).orElseGet(() -> TextItem.builder().build());
	}

}
