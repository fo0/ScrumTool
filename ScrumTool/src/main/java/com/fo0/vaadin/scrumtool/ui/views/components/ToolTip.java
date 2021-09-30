package com.fo0.vaadin.scrumtool.ui.views.components;

import static dev.mett.vaadin.tooltip.Tooltips.get;
import static dev.mett.vaadin.tooltip.Tooltips.getCurrent;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import dev.mett.vaadin.tooltip.config.TC_HIDE_ON_CLICK;
import dev.mett.vaadin.tooltip.config.TooltipConfiguration;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class ToolTip {

  public static <T extends Component & HasStyle> boolean remove(T c) {
    if (exists(c)) {
      getCurrent().removeTooltip(c);
      return true;
    } else {
      return false;
    }
  }

  private static <T extends Component & HasStyle> boolean exists(T c) {
    return getCurrent().getConfiguration(c)
                       .isPresent();
  }


  public static <T extends Component & HasStyle> String getTooltip(T c, String nullValue) {
    return exists(c) ? getCurrent().getConfiguration(c)
                                   .get()
                                   .getContent() : nullValue;
  }

  public static <T extends Component & HasStyle> void add(T c, String tooltip) {
    if (StringUtils.isBlank(tooltip)) {
      remove(c);
      return;
    }

    TooltipConfiguration config = new TooltipConfiguration(tooltip);
    config.setArrow(true);
    config.setHideOnClick(TC_HIDE_ON_CLICK.FALSE);

    getCurrent()
        .setTooltip(c, config);
  }

  public static <T extends Component & HasStyle> void addHtmlFormatted(T c, String tooltip) {
    if (StringUtils.isBlank(tooltip)) {
      remove(c);
      return;
    }

    TooltipConfiguration config = new TooltipConfiguration(tooltip);
    config.setArrow(true);
    config.setHideOnClick(TC_HIDE_ON_CLICK.FALSE);
    config.setAllowHTML(true);

    getCurrent().setTooltip(c, config);
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
      remove(c);
      return;
    }

    StringBuilder sb = new StringBuilder(
        "<table border=1px solid black> <tr> <th>  No. </th> <th> Comment </th> </tr>");
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      sb.append(String.format("<tr> <td> %s </td> <td> %s </td> <tr>", i, line));
    }
    sb.append("</tr> </table>");

    TooltipConfiguration config = new TooltipConfiguration(sb.toString());
    config.setArrow(true);
    config.setHideOnClick(TC_HIDE_ON_CLICK.FALSE);
    config.setAllowHTML(true);

    getCurrent()
        .setTooltip(c, config);
  }


}
