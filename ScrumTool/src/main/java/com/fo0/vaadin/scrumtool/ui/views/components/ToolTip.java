package com.fo0.vaadin.scrumtool.ui.views.components;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
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
	
	public static <T extends Component & HasStyle> void addHtmlFormatted(T c, String tooltip) {
		if (StringUtils.isBlank(tooltip)) {
			return;
		}

		TooltipConfiguration ttconfig = new TooltipConfiguration(tooltip);
		ttconfig.setArrow(true);
		ttconfig.setHideOnClick(TC_HIDE_ON_CLICK.FALSE);
		ttconfig.setAllowHTML(true);
		
		Tooltips.getCurrent().setTooltip(c, ttconfig);
	}
	
	/**
	 * HTML Formatted Lines
	 * 
	 * @param <T>
	 * @param c
	 * @param tooltip
	 */
	public static <T extends Component & HasStyle> void addLines(T c, List<String> lines) {
		if (CollectionUtils.isEmpty(lines)) {
			return;
		}

		StringBuilder sb = new StringBuilder("<table border=1px solid black> <tr> <th>  No. </th> <th> Comment </th> </tr>");
		for (int i = 0; i < lines.size() ; i++) {
			String line  = lines.get(i);
			sb.append(String.format("<tr> <td> %s </td> <td> %s </td> <tr>", i, line));
		}
		sb.append("</tr> </table>");
		
		TooltipConfiguration ttconfig = new TooltipConfiguration(sb.toString());
		ttconfig.setArrow(true);
		ttconfig.setHideOnClick(TC_HIDE_ON_CLICK.FALSE);
		ttconfig.setAllowHTML(true);
		
		Tooltips.getCurrent().setTooltip(c, ttconfig);
	}


}
