package com.fo0.vaadin.scrumtool.views.dialogs;

import com.fo0.vaadin.scrumtool.data.repository.KBDataRepository;
import com.fo0.vaadin.scrumtool.utils.SpringContext;
import com.fo0.vaadin.scrumtool.views.KanbanView;
import com.fo0.vaadin.scrumtool.views.MainView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class DeleteBoardDialog extends Dialog {

	private static final long serialVersionUID = 3959841920378174696L;

	private KBDataRepository dataRepository = SpringContext.getBean(KBDataRepository.class);
	
	public DeleteBoardDialog(KanbanView view) {
		Label lbl = new Label("Delete the Board");
		
		Button btnDelete = new Button("Delete");
		btnDelete.setIcon(VaadinIcon.CHECK.create());

		btnDelete.addClickShortcut(Key.ENTER);
		btnDelete.addClickListener(e -> {
			UI.getCurrent().navigate(MainView.class);
			dataRepository.deleteById(view.getId().get());
			close();
		});

		Button btnCancel = new Button("Cancel");
		btnCancel.addClickListener(e -> {
			close();
		});

		HorizontalLayout l = new HorizontalLayout(btnCancel, btnDelete);
		l.setMargin(true);
		add(l);
	}

}
