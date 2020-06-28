package com.fo0.vaadin.scrumtool.ui.views.dialogs;

import java.util.function.Consumer;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;

public class ChangeTextDialog extends Dialog {

	private static final long serialVersionUID = -5714183761044782095L;

	private VerticalLayout layout;

	public ChangeTextDialog(String caption, String initValue, Consumer<String> text) {
		layout = new VerticalLayout();
		add(layout);
		setWidth("400px");
		setMinHeight("300px");

		H3 captionLabel = new H3(caption);
		layout.add(captionLabel);
		layout.setAlignSelf(Alignment.CENTER, captionLabel);

		TextArea area = new TextArea();
		area.setWidthFull();
		layout.add(area);
		area.setValue(initValue);

		Button save = new Button(VaadinIcon.CHECK.create());
		save.setWidthFull();
		save.addClickListener(e -> {
			text.accept(area.getValue());
			close();
		});

		Button close = new Button(VaadinIcon.CLOSE.create());
		close.setWidthFull();
		close.addClickListener(e -> {
			close();
		});
		HorizontalLayout footer = new HorizontalLayout(close, save);
		footer.setWidthFull();
		layout.add(footer);

		area.focus();
	}

}
