package com.fo0.vaadin.scrumtool.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProjectBoardConfig {

	public static int MAX_COLUMNS;
	public static int MAX_CARDS;
	public static int MAX_CARD_TEXT_LENGTH;

	@Value("${app.projectboard.columns.max: 3}")
	public void setMAX_COLUMNS(int MAX_COLUMNS) {
		ProjectBoardConfig.MAX_COLUMNS = MAX_COLUMNS;
	}

	@Value("${app.projectboard.cards.max: 5}")
	public void setMAX_CARDS(int MAX_CARDS) {
		ProjectBoardConfig.MAX_CARDS = MAX_CARDS;
	}

	@Value("${app.projectboard.cards.text.length.max: 50}")
	public void setMAX_CARD_TEXT_LENGTH(int MAX_CARD_TEXT_LENGTH) {
		ProjectBoardConfig.MAX_CARD_TEXT_LENGTH = MAX_CARD_TEXT_LENGTH;
	}

}
