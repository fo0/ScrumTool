package com.fo0.vaadin.scrumtool.config;

import org.springframework.beans.factory.annotation.Value;

public class Config {

	public static boolean DEBUG = true;

	public static int NOTIFICATION_DURATION = 2000;

	@Value("${app.debug: false}")
	public void setMAX_COLUMNS(boolean DEBUG) {
		Config.DEBUG = DEBUG;
	}
}
