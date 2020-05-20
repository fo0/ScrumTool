package com.fo0.vaadin.scrumtool.utils;

import com.fo0.vaadin.scrumtool.views.layouts.MainLayout;
import com.vaadin.flow.component.UI;

/**
 * 
 * @created 20.05.2020 - 22:37:48
 * @author KaesDingeling
 * @version 0.1
 */
public class UIUtils {
	
	public static final String THEME_CHECK = "theme-check";
	
	/**
	 * 
	 * @param ui
	 * @param theme
	 * @Created 21.05.2020 - 00:40:42
	 * @author KaesDingeling
	 */
	public static void updateThemeForAllSessionUIs(UI ui, String theme) {
		ui.getSession().setAttribute(THEME_CHECK, theme);
		
		ui.getSession().getUIs().forEach(browserTab -> {
			browserTab.access(() -> {
				setThemeAndUpdateUI(browserTab, theme);
			});
		});
	}
	
	/**
	 * 
	 * @param ui
	 * @param theme
	 * @Created 21.05.2020 - 00:49:35
	 * @author KaesDingeling
	 */
	public static void setThemeAndUpdateUI(UI ui, String theme) {
		ui.getSession().setAttribute(THEME_CHECK, theme);
		ui.getElement().setAttribute("theme", theme);
		ui.getChildren()
				.filter(view -> view instanceof MainLayout)
				.forEach(view -> ((MainLayout) view).getThemeToggleBtn().refresh());
	}
}