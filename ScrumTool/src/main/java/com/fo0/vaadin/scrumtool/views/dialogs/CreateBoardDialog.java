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

public class CreateBoardDialog extends Dialog {

	private static final long serialVersionUID = 2274992601002314827L;

	@Autowired
	private KBDataRepository repository = SpringContext.getBean(KBDataRepository.class);

	private Checkbox chkOptPermissionSystem;
	private NumberField nmbColumnsMax;
	private NumberField nmbCardsMax;
	private NumberField nmbCardTextLengthMax;
	private NumberField nmbMaxLikesPerUser;
	private NumberField nmbCardLikesMaxPerUser;

	public CreateBoardDialog() {
		setWidth("400px");
		setHeight("630px");
		
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

		nmbColumnsMax = new NumberField("Max-Columns");
		nmbColumnsMax.setValue(0d);
		nmbColumnsMax.setHasControls(true);
		nmbColumnsMax.setMin(0);
		nmbColumnsMax.setMax(Integer.MAX_VALUE);
		nmbColumnsMax.setWidthFull();
		options.add(nmbColumnsMax);

		nmbCardsMax = new NumberField("Max-Cards");
		nmbCardsMax.setValue(0d);
		nmbCardsMax.setHasControls(true);
		nmbCardsMax.setMin(0);
		nmbCardsMax.setMax(Integer.MAX_VALUE);
		nmbCardsMax.setWidthFull();
		options.add(nmbCardsMax);

		nmbCardTextLengthMax = new NumberField("Max Card Text Length");
		nmbCardTextLengthMax.setValue(0d);
		nmbCardTextLengthMax.setHasControls(true);
		nmbCardTextLengthMax.setMin(0);
		nmbCardTextLengthMax.setMax(Integer.MAX_VALUE);
		nmbCardTextLengthMax.setWidthFull();
		options.add(nmbCardTextLengthMax);

		nmbMaxLikesPerUser = new NumberField("Max-Likes per User");
		nmbMaxLikesPerUser.setValue(0d);
		nmbMaxLikesPerUser.setHasControls(true);
		nmbMaxLikesPerUser.setMin(0);
		nmbMaxLikesPerUser.setMax(Integer.MAX_VALUE);
		nmbMaxLikesPerUser.setWidthFull();
		options.add(nmbMaxLikesPerUser);
		
		nmbCardLikesMaxPerUser = new NumberField("Max Card Likes per User");
		nmbCardLikesMaxPerUser.setValue(1d);
		nmbCardLikesMaxPerUser.setHasControls(true);
		nmbCardLikesMaxPerUser.setMin(0);
		nmbCardLikesMaxPerUser.setMax(Integer.MAX_VALUE);
		nmbCardLikesMaxPerUser.setWidthFull();
		options.add(nmbCardLikesMaxPerUser);

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
							.maxColumns(nmbColumnsMax.getValue().intValue())
							.maxCards(nmbCardsMax.getValue().intValue())
							.maxCardTextLength(nmbCardTextLengthMax.getValue().intValue())
							.maxLikesPerUser(nmbMaxLikesPerUser.getValue().intValue())
							.maxLikesPerUserPerCard(nmbCardLikesMaxPerUser.getValue().intValue())
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
