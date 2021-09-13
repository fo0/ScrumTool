package com.fo0.vaadin.scrumtool.ui.views.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import java.util.function.Consumer;
import lombok.Getter;
import org.apache.logging.log4j.util.Strings;

public class TextFieldDialog extends Dialog {

	private static final long serialVersionUID = -5714183761044782095L;

	@Getter
	private VerticalLayout rootLayout;
	@Getter
	private TextField textField;

	public TextFieldDialog(String caption, String initValue, Consumer<String> text) {
		this(caption, Strings.EMPTY, initValue, text);
	}

	public TextFieldDialog(String caption, String placeholder, String initValue, Consumer<String> text) {
		rootLayout = new VerticalLayout();
		add(rootLayout);
		setWidth("400px");
		setMinHeight("200px");

		H3 captionLabel = new H3(caption);
		rootLayout.add(captionLabel);
		rootLayout.setAlignSelf(Alignment.CENTER, captionLabel);

		textField = new TextField();
		textField.setWidthFull();
		rootLayout.add(textField);
		textField.setValue(initValue);
		textField.setPlaceholder(placeholder);

		Button save = new Button(VaadinIcon.CHECK.create());
		save.setWidthFull();
		save.addClickListener(e -> {
			text.accept(textField.getValue());
			close();
		});

		Button close = new Button(VaadinIcon.CLOSE.create());
		close.setWidthFull();
		close.addClickListener(e -> {
			close();
		});
		HorizontalLayout footer = new HorizontalLayout(close, save);
		footer.setWidthFull();
		rootLayout.add(footer);

		textField.focus();
	}

}
