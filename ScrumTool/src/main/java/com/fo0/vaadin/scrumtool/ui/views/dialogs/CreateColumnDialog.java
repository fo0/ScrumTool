package com.fo0.vaadin.scrumtool.ui.views.dialogs;

import com.fo0.vaadin.scrumtool.ui.broadcast.BroadcasterBoard;
import com.fo0.vaadin.scrumtool.ui.session.SessionUtils;
import com.fo0.vaadin.scrumtool.ui.utils.Utils;
import com.fo0.vaadin.scrumtool.ui.views.KanbanView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class CreateColumnDialog extends Dialog {

	private static final long serialVersionUID = 3959841920378174696L;

	public CreateColumnDialog(KanbanView view) {
		setWidth("330");
		TextField t = new TextField("Name");
		t.focus();
		Button b = new Button("Create");
		b.addClickShortcut(Key.ENTER);
		b.addClickListener(e -> {
			if(t.isEmpty()) {
				Notification.show("Please enter a column name", 3000, Position.BOTTOM_CENTER);
				return;
			}
			
			view.addColumn(Utils.randomId(), SessionUtils.getSessionId(), t.getValue());
			BroadcasterBoard.broadcast(view.getId().get(), "update");
			close();
		});
		
		HorizontalLayout l = new HorizontalLayout(t, b);
		VerticalLayout layout = new VerticalLayout(l);
		layout.getStyle().set("overflow", "hidden");
		layout.setFlexGrow(1);
		layout.setMargin(false);
		layout.setWidthFull();
		add(layout);
	}

}
