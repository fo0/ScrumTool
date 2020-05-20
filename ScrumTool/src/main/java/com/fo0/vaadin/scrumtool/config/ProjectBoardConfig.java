package com.fo0.vaadin.scrumtool.config;

import org.springframework.beans.factory.annotation.Value;

public class ProjectBoardConfig {

	@Value("app.projectboard.columns.max: 3")
	public static int MAX_COLUMNS = 5;

	@Value("app.projectboard.cards.max: 5")
	public static int MAX_CARDS = 50;

	@Value("app.projectboard.cards.text.length.max: 10")
	public static int MAX_CARD_TEXT_LENGTH;

}
