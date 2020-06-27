package com.fo0.vaadin.scrumtool.ui.views.components;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;

import dev.mett.vaadin.tooltip.Tooltips;
import dev.mett.vaadin.tooltip.config.TC_HIDE_ON_CLICK;
import dev.mett.vaadin.tooltip.config.TooltipConfiguration;

public class ToolTip {

	public static <T extends Component & HasStyle> void add(T c, String tooltip) {
		if (StringUtils.isBlank(tooltip)) {
			return;
		}

		TooltipConfiguration ttconfig = new TooltipConfiguration(tooltip);
		ttconfig.setArrow(true);
		ttconfig.setHideOnClick(TC_HIDE_ON_CLICK.FALSE);

		Tooltips.getCurrent().setTooltip(c, ttconfig);
	}

}
