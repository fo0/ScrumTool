package com.fo0.vaadin.scrumtool.ui.views.dialogs;

import java.util.stream.Collectors;

import com.fo0.vaadin.scrumtool.ui.data.table.TKBData;
import com.fo0.vaadin.scrumtool.ui.export.VanillaMarkDown;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;

import lombok.Getter;

public class MarkDownDialog extends Dialog {
	private static final long serialVersionUID = -7507633592046504527L;

	@Getter
	private TKBData data;

	private TextArea area;

	public MarkDownDialog(TKBData data) {
		super();

		this.data = data;

		init();
	}

	private void init() {
		setWidth("800px");
		setHeight("600px");

		VerticalLayout root = new VerticalLayout();

		HorizontalLayout header = new HorizontalLayout();
		header.setPadding(true);
		header.getStyle().set("border", "2px solid black");
		header.setWidthFull();

		header.add(createBtnCommonMarkdown());

		area = new TextArea();
		area.setSizeFull();

		VerticalLayout body = new VerticalLayout();
		body.setWidthFull();
		body.getStyle().set("border", "1px solid black");
		body.add(area);
		body.setFlexGrow(1, area);

		root.add(header, body);
		root.setSizeFull();
		root.setFlexGrow(1, body);

		add(root);
	}

	private Button createBtnCommonMarkdown() {
		Button btnCommonMarkdown = new Button("Create Markdown");
		btnCommonMarkdown.addClickListener(e -> {
			area.setValue(VanillaMarkDown.create(data).stream().collect(Collectors.joining("\n")));
		});
		return btnCommonMarkdown;
	}

}
