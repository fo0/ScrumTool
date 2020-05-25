package com.fo0.vaadin.scrumtool.views.components;

import org.springframework.beans.factory.annotation.Autowired;

import com.fo0.vaadin.scrumtool.data.repository.KBDataRepository;
import com.fo0.vaadin.scrumtool.data.table.TKBData;
import com.fo0.vaadin.scrumtool.data.table.TKBOptions;
import com.fo0.vaadin.scrumtool.session.SessionUtils;
import com.fo0.vaadin.scrumtool.utils.SpringContext;
import com.fo0.vaadin.scrumtool.views.KanbanView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class CreateBoardDialog extends Dialog {

	private static final long serialVersionUID = 2274992601002314827L;

	@Autowired
	private KBDataRepository repository = SpringContext.getBean(KBDataRepository.class);

	private Checkbox chkOptPermissionSystem;

	private TextField txtColumns;

	private TextField txtCards;

	private TextField txtCardTextLength;

	public CreateBoardDialog() {
		setWidth("600px");
		setHeight("450px");

		H3 title = new H3("Configure Board");
		title.getStyle().set("text-align", "center");
		title.setWidthFull();
		add(title);

		add(createOptionsLayout());

		add(createBottomLayout());
	}

	private VerticalLayout createOptionsLayout() {
		VerticalLayout options = new VerticalLayout();
		options.setWidthFull();
		add(options);

		txtColumns = new TextField("Max Columns");
		txtColumns.setValue("0");
		txtColumns.setWidthFull();
		options.add(txtColumns);

		txtCards = new TextField("Max Cards");
		txtCards.setValue("0");
		txtCards.setWidthFull();
		options.add(txtCards);

		txtCardTextLength = new TextField("Max Card Text Length");
		txtCardTextLength.setValue("0");
		txtCardTextLength.setWidthFull();
		options.add(txtCardTextLength);

		chkOptPermissionSystem = new Checkbox("Permissionsystem");
		chkOptPermissionSystem.setWidthFull();
		options.add(chkOptPermissionSystem);

		return options;
	}

	private HorizontalLayout createBottomLayout() {
		Button btnClose = new Button(VaadinIcon.CLOSE.create());
		btnClose.setWidthFull();
		btnClose.addClickListener(e -> {
			close();
		});

		Button btnOk = new Button(VaadinIcon.CHECK.create());
		btnOk.setWidthFull();
		btnOk.addClickListener(e -> {
			//@formatter:off
			TKBData p = repository.save(TKBData.builder()
					.ownerId(SessionUtils.getSessionId())
					.options(TKBOptions.builder()
							.optionPermissionSystem(chkOptPermissionSystem.getValue())
							.maxColumns(Integer.valueOf(txtColumns.getValue()))
							.maxCards(Integer.valueOf(txtCards.getValue()))
							.maxCardTextLength(Integer.valueOf(txtCardTextLength.getValue()))
							.build())
					.build());
			UI.getCurrent().navigate(KanbanView.class, p.getId());
			close();
			//@formatter:on
		});

		HorizontalLayout l = new HorizontalLayout(btnClose, btnOk);
		l.setWidthFull();
		return l;
	}

}
