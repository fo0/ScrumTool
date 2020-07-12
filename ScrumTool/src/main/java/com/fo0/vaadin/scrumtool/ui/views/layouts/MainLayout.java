package com.fo0.vaadin.scrumtool.ui.views.layouts;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.fo0.vaadin.scrumtool.ui.session.SessionUtils;
import com.fo0.vaadin.scrumtool.ui.utils.UIUtils;
import com.fo0.vaadin.scrumtool.ui.views.components.ThemeToggleButton;
import com.fo0.vaadin.scrumtool.ui.views.data.IThemeToggleButton;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.PendingJavaScriptResult;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

/**
 * 
 * @created 21.05.2020 - 00:27:23
 * @author KaesDingeling
 * @version 0.1
 */
@Push
@Theme(value = Material.class, variant = Material.LIGHT)
@CssImport("./styles/custom-styles.css")
@CssImport(value = "./styles/custom-button-styles.css", themeFor = "vaadin-button")
public class MainLayout extends VerticalLayout implements RouterLayout, IThemeToggleButton {
	private static final long serialVersionUID = 4630537412936320207L;
	
	/**
	 * 
	 */
	public MainLayout() {
		super();
		
		setSizeFull();
		setPadding(false);
		
		SessionUtils.createSessionIdIfNotExistsExists();
		
		checkOSTheme();
	}
	
	/**
	 * 
	 * 
	 * @Created 21.05.2020 - 01:25:47
	 * @author KaesDingeling
	 */
	public synchronized void checkOSTheme() {
		UI ui = UI.getCurrent();
		
		Object value = ui.getSession().getAttribute(UIUtils.THEME_CHECK);
		
		if (value == null) {
			PendingJavaScriptResult jsResult = ui.getPage().executeJs("return (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches);");
			
			jsResult.then(Boolean.class, i -> {
				if (i) {
					UIUtils.setThemeAndUpdateUI(ui, Material.DARK);
				} else {
					UIUtils.setThemeAndUpdateUI(ui, Material.LIGHT);
				}
			});
		} else if (StringUtils.equals(String.valueOf(value), Material.DARK)) {
			UIUtils.setThemeAndUpdateUI(ui, Material.DARK);
		}
	}

	@Override
	public ThemeToggleButton getThemeToggleButton() {
		return getChildren()
				.filter(Objects::nonNull)
				.filter(component -> component instanceof IThemeToggleButton)
				.filter(Objects::nonNull)
				.map(component -> (IThemeToggleButton) component)
				.filter(Objects::nonNull)
				.map(component -> component.getThemeToggleButton())
				.findAny()
				.orElse(null);
	}
}