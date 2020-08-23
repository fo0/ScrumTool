package com.fo0.vaadin.scrumtool.ui.export;

import java.util.Comparator;
import java.util.stream.Collectors;

import com.fo0.vaadin.scrumtool.ui.data.interfaces.IDataOrder;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCardComment;
import com.fo0.vaadin.scrumtool.ui.model.TextItem;
import com.fo0.vaadin.scrumtool.ui.model.VotingData;
import com.fo0.vaadin.scrumtool.ui.model.VotingItem;

public class ExportUtils {

	static String getComments(EExportType type, TKBCard card) {
		// @formatter:off
		switch (card.getType()) {
		case TextCard:
			return card.getComments().stream().sorted(Comparator.comparing(IDataOrder::getDataOrder))
					.map(TKBCardComment::getText).collect(Collectors.joining(getNewLinePattern(type)));

		case VotingCard:
			return card.getByType(VotingData.class).get().getItems().stream()
					.sorted(Comparator.comparing(VotingItem::countAllLikes).reversed())
					.map(e -> String.format("Votes: %s \\| Description: %s", e.countAllLikes(), e.getText()))
					.collect(Collectors.joining(getNewLinePattern(type)));

		default:
			return "Unsupported Type";
		}
		// @formatter:on
	}

	private static String getNewLinePattern(EExportType type) {
		switch (type) {
		case Markdown:
			return " <br/> ";

		case Markup_Confluence:
			return " \\\\ ";

		default:
			return "Unsupported New-Line-Character";
		}
	}

	public static String getText(TKBCard card) {
		// @formatter:off
		switch (card.getType()) {
		case TextCard:
			return card.getByType(TextItem.class).get().getText();

		case VotingCard:
			return card.getByType(VotingData.class).get().getText();

		default:
			return "Unsupported Type";
		}
		// @formatter:on
	}

	public static TextItem getItem(TKBCard card) {
		return card.getByType(TextItem.class).orElseGet(() -> TextItem.builder().text("Error").build());
	}

}

enum EExportType {
	Markdown,

	Markup_Confluence
}
