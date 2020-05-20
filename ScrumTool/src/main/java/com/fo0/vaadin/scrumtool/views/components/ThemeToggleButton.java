package com.fo0.vaadin.scrumtool.views.components;

import org.apache.commons.lang3.StringUtils;

import com.fo0.vaadin.scrumtool.utils.UIUtils;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.theme.material.Material;

/**
 * 
 * @created 21.05.2020 - 00:17:19
 * @author KaesDingeling
 * @version 0.1
 */
public class ThemeToggleButton extends Button {
	private static final long serialVersionUID = 4105012739543724626L;
	
	public static final String THEME_BASE = "theme-toggle";
	
	/**
	 * 
	 */
	public ThemeToggleButton() {
		super();
		
		addClassName(THEME_BASE);
		addThemeVariants(ButtonVariant.LUMO_ICON);
		addClickListener(e -> toggleTheme());
		
		refresh();
	}
	
	/**
	 * 
	 * 
	 * @Created 21.05.2020 - 00:34:10
	 * @author KaesDingeling
	 */
	public void refresh() {
		if (isDark()) {
			setIcon(VaadinIcon.SUN_O.create());
			getElement().setAttribute("title", "Switch to light theme");
		} else {
			setIcon(VaadinIcon.MOON.create());
			getElement().setAttribute("title", "Switch to dark theme");
		}
	}
	
	/**
	 * 
	 * 
	 * @Created 21.05.2020 - 00:36:53
	 * @author KaesDingeling
	 */
	public void toggleTheme() {
		UI ui = UI.getCurrent();
		
		if (isDark(ui)) {
			UIUtils.updateThemeForAllSessionUIs(ui, Material.LIGHT);
		} else {
			UIUtils.updateThemeForAllSessionUIs(ui, Material.DARK);
		}
	}
	
	/**
	 * 
	 * @return
	 * @Created 21.05.2020 - 00:35:25
	 * @author KaesDingeling
	 */
	public boolean isDark() {
		return isDark(UI.getCurrent());
	}
	
	/**
	 * 
	 * @param ui
	 * @return
	 * @Created 21.05.2020 - 00:35:27
	 * @author KaesDingeling
	 */
	public boolean isDark(UI ui) {
		return StringUtils.equals(String.valueOf(ui.getSession().getAttribute(UIUtils.THEME_CHECK)), Material.DARK);
	}
}