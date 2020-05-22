package com.fo0.vaadin.scrumtool.views.utils;

import com.fo0.vaadin.scrumtool.session.SessionUtils;
import com.fo0.vaadin.scrumtool.utils.Utils;
import com.fo0.vaadin.scrumtool.views.KanbanView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

public class ProjectBoardViewUtils {

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

	public static Dialog createColumnDialog(KanbanView view) {
		Dialog d = new Dialog();
		TextField t = new TextField("Name");
		t.focus();
		Button b = new Button("Erstellen");
		b.addClickListener(e -> {
			view.addColumn(Utils.randomId(), SessionUtils.getSessionId(), t.getValue());
			view.reload();
			d.close();
		});

		HorizontalLayout l = new HorizontalLayout(t, b);
		l.setMargin(true);
		d.add(l);
		return d;
	}

}
