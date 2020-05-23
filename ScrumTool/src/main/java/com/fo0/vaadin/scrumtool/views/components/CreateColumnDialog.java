package com.fo0.vaadin.scrumtool.views.components;

import com.fo0.vaadin.scrumtool.session.SessionUtils;
import com.fo0.vaadin.scrumtool.utils.Utils;
import com.fo0.vaadin.scrumtool.views.KanbanView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class CreateColumnDialog extends Dialog {

	private static final long serialVersionUID = 3959841920378174696L;

	public CreateColumnDialog(KanbanView view) {
		TextField t = new TextField("Name");
		t.focus();
		Button b = new Button("Create");
		b.addClickListener(e -> {
			view.addColumn(Utils.randomId(), SessionUtils.getSessionId(), t.getValue());
			view.reload();
			close();
		});

		HorizontalLayout l = new HorizontalLayout(t, b);
		l.setMargin(true);
		add(l);
	}

}
