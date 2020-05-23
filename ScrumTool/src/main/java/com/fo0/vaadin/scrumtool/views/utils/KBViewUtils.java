package com.fo0.vaadin.scrumtool.views.utils;

import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;

import com.fo0.vaadin.scrumtool.data.interfaces.IDataOrder;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;

public class KBViewUtils {

	public static HorizontalLayout createColumnLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(true);
		layout.getStyle().set("border", "0.5px solid black");
		layout.setSizeFull();
		return layout;
	}

	public static Label createColumnTextLabel(TextArea area) {
		Label label = new Label(area.getValue());
		label.getStyle().set("border", "2px solid black");
		return label;
	}

	public static VerticalLayout createRootLayout() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		return layout;
	}

	public static int calculateNextPosition(Collection<? extends IDataOrder> items) {
		if (CollectionUtils.isEmpty(items)) {
			return 0;
		}

		return items.stream().map(IDataOrder::getDataOrder).max(Integer::compare).get() + 1;
	}

}
