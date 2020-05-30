package com.fo0.vaadin.scrumtool.views.dialogs;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.vaadin.maxime.MarkdownArea;

import com.fo0.vaadin.scrumtool.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.data.table.TKBColumn;
import com.fo0.vaadin.scrumtool.data.table.TKBData;
import com.google.common.collect.Lists;
import com.vaadin.flow.component.dialog.Dialog;

public class MarkDownDialog extends Dialog {

	private static final long serialVersionUID = -7507633592046504527L;

	private TKBData data;

	public MarkDownDialog(TKBData data) {
		this.data = data;

		init();
	}

	private void init() {
		setWidth("800px");
		setHeight("600px");

		MarkdownArea area = new MarkdownArea();
		area.setValue(generateMarkDown(data).stream().collect(Collectors.joining("\n")));
		add(area);
	}

	public List<String> generateMarkDown(TKBData data) {
		List<String> list = Lists.newArrayList();
		list.add("# Kanban Board");
		list.add("");
		data.getColumns().stream().sorted(Comparator.comparing(TKBColumn::getDataOrder)).forEachOrdered(column -> {
			list.addAll(createColumn(column));
			list.add("");
		});
		return list;
	}

	public List<String> createColumn(TKBColumn column) {
		List<String> list = Lists.newArrayList();
		list.add("### " + column.getName());
		list.add("| No | Likes | Description |");
		list.add("| :---: | :----: | :------ |");
		column.getCards().stream().sorted(Comparator.comparing(TKBCard::getDataOrder)).forEachOrdered(card -> {
			list.add(String.format("| %d | %d | %s |", card.getDataOrder(), card.countAllLikes(), card.getText()));
		});
		return list;
	}

}
