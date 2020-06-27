package com.fo0.vaadin.scrumtool.ui.views.components;

import org.apache.commons.lang3.StringUtils;

import com.fo0.vaadin.scrumtool.ui.utils.UIUtils;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.material.Material;

import lombok.Getter;

/**
 * 
 * @created 21.05.2020 - 00:17:19
 * @author KaesDingeling
 * @version 0.1
 */
public class ThemeToggleButton extends HorizontalLayout {
	private static final long serialVersionUID = 4105012739543724626L;
	
	public static final String THEME_BASE = "theme-toggle";
	public static final String THEME_ON_BTN = "theme-toggle-on-btn";
	public static final String THEME_OFF_BTN = "theme-toggle-off-btn";
	
	@Getter
	private Button offBtn;
	@Getter
	private Button onBtn;
	
	/**
	 * 
	 */
	public ThemeToggleButton() {
		this(true);
	}
	
	/**
	 * 
	 * @param withCustomTheme
	 */
	public ThemeToggleButton(boolean withCustomTheme) {
		super();

		setSpacing(false);
		setAlignItems(Alignment.CENTER);
		
		offBtn = new Button("Light", e -> setThemeLight());
		onBtn = new Button("Dark", e -> setThemeDark());
		
		if (withCustomTheme) {
			addClassName(THEME_BASE);
			
			offBtn.addClassName(THEME_OFF_BTN + "-custom");
			onBtn.addClassName(THEME_ON_BTN + "-custom");
		} else {
			offBtn.addClassName(THEME_OFF_BTN);
			onBtn.addClassName(THEME_ON_BTN);
		}
		
		add(offBtn, onBtn);
		
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
			getElement().setAttribute("title", "Switch to light theme");
			
			offBtn.removeThemeVariants(ButtonVariant.MATERIAL_CONTAINED);
			offBtn.addThemeVariants(ButtonVariant.MATERIAL_OUTLINED);
			
			onBtn.removeThemeVariants(ButtonVariant.MATERIAL_OUTLINED);
			onBtn.addThemeVariants(ButtonVariant.MATERIAL_CONTAINED);
		} else {
			getElement().setAttribute("title", "Switch to dark theme");
			
			offBtn.removeThemeVariants(ButtonVariant.MATERIAL_OUTLINED);
			offBtn.addThemeVariants(ButtonVariant.MATERIAL_CONTAINED);
			
			onBtn.removeThemeVariants(ButtonVariant.MATERIAL_CONTAINED);
			onBtn.addThemeVariants(ButtonVariant.MATERIAL_OUTLINED);
		}
	}
	
	/**
	 * 
	 * 
	 * @Created 22.05.2020 - 00:17:53
	 * @author KaesDingeling
	 */
	public void setThemeDark() {
		UI ui = UI.getCurrent();
		
		if (!isDark(ui)) {
			UIUtils.updateThemeForAllSessionUIs(ui, Material.DARK);
		}
	}
	
	/**
	 * 
	 * 
	 * @Created 22.05.2020 - 00:17:50
	 * @author KaesDingeling
	 */
	public void setThemeLight() {
		UI ui = UI.getCurrent();
		
		if (isDark(ui)) {
			UIUtils.updateThemeForAllSessionUIs(ui, Material.LIGHT);
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