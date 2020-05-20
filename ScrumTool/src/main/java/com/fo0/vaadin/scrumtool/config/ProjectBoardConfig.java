package com.fo0.vaadin.scrumtool.config;

import org.springframework.beans.factory.annotation.Value;

public class ProjectBoardConfig {

	@Value("${app.projectboard.columns.max: 3}")
	public static int MAX_COLUMNS;

	@Value("${app.projectboard.cards.max: 5}")
	public static int MAX_CARDS;

	@Value("${app.projectboard.cards.text.length.max: 50}")
	public static int MAX_CARD_TEXT_LENGTH;

}
