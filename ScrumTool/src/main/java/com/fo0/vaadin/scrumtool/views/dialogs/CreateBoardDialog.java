package com.fo0.vaadin.scrumtool.views.dialogs;

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
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

public class CreateBoardDialog extends Dialog {

	private static final long serialVersionUID = 2274992601002314827L;

	@Autowired
	private KBDataRepository repository = SpringContext.getBean(KBDataRepository.class);

	private Checkbox chkOptPermissionSystem;
	private TextField txtColumnsMax;
	private TextField txtCardsMax;
	private TextField txtCardTextLengthMax;
	private TextField txtLikeMaxPerOwner;

	private NumberField nmbCardLikesMaxPerOwner;

	public CreateBoardDialog() {
		setWidth("600px");
		setHeight("500px");

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

		txtColumnsMax = new TextField("Max-Columns");
		txtColumnsMax.setValue("0");
		txtColumnsMax.setWidthFull();
		options.add(txtColumnsMax);

		txtCardsMax = new TextField("Max-Cards");
		txtCardsMax.setValue("0");
		txtCardsMax.setWidthFull();
		options.add(txtCardsMax);

		txtCardTextLengthMax = new TextField("Max Card Text Length");
		txtCardTextLengthMax.setValue("0");
		txtCardTextLengthMax.setWidthFull();
		options.add(txtCardTextLengthMax);

		nmbCardLikesMaxPerOwner = new NumberField("Owner-Likes");
		nmbCardLikesMaxPerOwner.setValue(0d);
		nmbCardLikesMaxPerOwner.setHasControls(true);
		nmbCardLikesMaxPerOwner.setMin(0);
		nmbCardLikesMaxPerOwner.setMax(Integer.MAX_VALUE);
		options.add(nmbCardLikesMaxPerOwner);

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
							.maxColumns(Integer.valueOf(txtColumnsMax.getValue()))
							.maxCards(Integer.valueOf(txtCardsMax.getValue()))
							.maxCardTextLength(Integer.valueOf(txtCardTextLengthMax.getValue()))
							.maxLikesPerUser(nmbCardLikesMaxPerOwner.getValue().intValue())
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
