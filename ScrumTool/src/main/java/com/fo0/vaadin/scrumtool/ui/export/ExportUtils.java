package com.fo0.vaadin.scrumtool.ui.export;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fo0.vaadin.scrumtool.ui.data.interfaces.IDataOrder;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCardComment;
import com.fo0.vaadin.scrumtool.ui.model.TextItem;
import com.fo0.vaadin.scrumtool.ui.model.VotingData;
import com.fo0.vaadin.scrumtool.ui.model.VotingItem;
import com.google.common.collect.Lists;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.logging.log4j.util.Strings;

import j2html.TagCreator;
import j2html.tags.ContainerTag;

public class ExportUtils {

	private static final String TABLE_STYLE = "";

	static String getComments(EExportType type, TKBCard card) {
		// @formatter:off
		
		switch (card.getType()) {
		case TextCard:
			if(CollectionUtils.isEmpty(card.getComments())){
				return Strings.EMPTY;
			}

			List<ContainerTag> list = Lists.newArrayList();
			list.add(TagCreator.tr(TagCreator.th().withText("Description")));
			card.getComments()
				.stream()
				.sorted(Comparator.comparing(IDataOrder::getDataOrder))
				.map(TKBCardComment::getText)
				.forEachOrdered(e -> list.add(TagCreator.td().withText(e)));

			return TagCreator.table().with(list).render();

		case VotingCard:
			if(!card.getByType(VotingData.class).isPresent() || CollectionUtils.isEmpty(card.getByType(VotingData.class).get().getItems())){
				return Strings.EMPTY;
			}

			List<ContainerTag> list2 = Lists.newArrayList();
			list2.add(TagCreator.tr(TagCreator.th().withText("Votes"), TagCreator.th().withText("Description")));

			card.getByType(VotingData.class)
				.get().getItems().stream()
				.sorted(Comparator.comparing(VotingItem::countAllLikes).reversed())
				.forEachOrdered(e -> {
					list2.add(TagCreator.td().withText(String.valueOf(e.countAllLikes())));
					list2.add(TagCreator.td().withText(e.getText()));
				});

				Collections.reverse(list2);

				return TagCreator.table().with(list2).render();

		default:
			return null;
		}
		// @formatter:on
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
	Markdown
}
