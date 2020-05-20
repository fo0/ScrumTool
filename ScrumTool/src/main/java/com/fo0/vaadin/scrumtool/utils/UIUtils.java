package com.fo0.vaadin.scrumtool.utils;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.PendingJavaScriptResult;
import com.vaadin.flow.theme.material.Material;

/**
 * 
 * @created 20.05.2020 - 22:37:48
 * @author KaesDingeling
 * @version 0.1
 */
public class UIUtils {
	
	public static final String THEME_CHECK = "theme-check";
	
	public static void checkOSTheme(UI ui) {
		Object value = ui.getSession().getAttribute(THEME_CHECK);
		
		if (value == null || value instanceof Boolean) {
			PendingJavaScriptResult jsResult = ui.getPage().executeJs("return (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches);");
			
			jsResult.then(Boolean.class, i -> {
				if (i) {
					ui.getElement().setAttribute("theme", Material.DARK);
					ui.getSession().setAttribute(THEME_CHECK, true);
				} else {
					ui.getSession().setAttribute(THEME_CHECK, false);
				}
			});
		} else if ((Boolean) value) {
			ui.getElement().setAttribute("theme", Material.DARK);
		}
	}
}