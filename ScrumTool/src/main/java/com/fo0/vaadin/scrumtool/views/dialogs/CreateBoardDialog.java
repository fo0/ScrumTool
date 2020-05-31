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
import com.vaadin.flow.component.textfield.TextFieldVariant;

public class CreateBoardDialog extends Dialog {

	private static final long serialVersionUID = 2274992601002314827L;

	@Autowired
	private KBDataRepository repository = SpringContext.getBean(KBDataRepository.class);

	private Checkbox chkOptPermissionSystem;
	private NumberField nmbColumnsMax;
	private NumberField nmbCardsMax;
	private NumberField nmbCardTextLengthMax;
	private NumberField nmbMaxPerOwner;
	private NumberField nmbCardLikesMaxPerOwner;

	public CreateBoardDialog() {
		setWidth("450px");
		setHeight("650px");

		VerticalLayout layout = new VerticalLayout();
		layout.setWidthFull();
		layout.setFlexGrow(1);
		add(layout);

		layout.add(createTitle());
		layout.add(createOptionsLayout());
		layout.add(createBottomLayout());

	}

	private H3 createTitle() {
		H3 title = new H3("Configure Board");
		title.getStyle().set("text-align", "center");
		title.setWidthFull();
		return title;
	}

	private VerticalLayout createOptionsLayout() {
		VerticalLayout layout = new VerticalLayout();
		layout.setWidthFull();
		layout.setFlexGrow(1);
		add(layout);

		nmbColumnsMax = createNumberField(layout, "Max-Columns", 0, 0, Integer.MAX_VALUE, true);
		nmbCardsMax = createNumberField(layout,"Max-Cards", 0, 0, Integer.MAX_VALUE, true);
		nmbCardTextLengthMax = createNumberField(layout,"Max Card Text Length", 0, 0, Integer.MAX_VALUE, true);
		nmbMaxPerOwner = createNumberField(layout,"Max-Likes per User", 0, 0, Integer.MAX_VALUE, true);
		nmbCardLikesMaxPerOwner = createNumberField(layout,"Max Card Likes per User", 0, 0, Integer.MAX_VALUE, true);
		
		chkOptPermissionSystem = new Checkbox("Permissionsystem");
		chkOptPermissionSystem.setWidthFull();
		layout.add(chkOptPermissionSystem);

		return layout;
	}

	/**
	 * 
	 * @param title
	 * @param defaultValue
	 * @param min
	 * @param max
	 * @param zeroIsInfinite
	 * @return
	 * @Created 31.05.2020 - 21:10:01
	 * @author KaesDingeling
	 */
	private NumberField createNumberField(VerticalLayout layout, String title, int defaultValue, int min, int max, boolean zeroIsInfinite) {
		NumberField numberField = new NumberField("Max Card Likes per User");
		numberField.setHasControls(true);
		numberField.setMin(0);
		numberField.setMax(Integer.MAX_VALUE);
		numberField.setWidthFull();
		numberField.addThemeVariants(TextFieldVariant.MATERIAL_ALWAYS_FLOAT_LABEL);

		if (zeroIsInfinite) {
			numberField.setPlaceholder("∞");
			numberField.setValue(null);
			numberField.addValueChangeListener(e -> {
				if (e.getValue() != null && e.getValue() == 0) {
					e.getSource().setValue(null);
				}
			});
		}

		if (!(defaultValue >= min && defaultValue <= max && min <= max)) {
			throw new IllegalStateException("Invalid parameters: min(" + min + ") max(" + max + ") defaultValue(" + defaultValue + ")");
		}

		layout.add(numberField);
		
		return numberField;
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
							.maxLikesPerUser(nmbMaxPerOwner.getValue().intValue())
							.maxLikesPerUserPerCard(nmbCardLikesMaxPerOwner.getValue().intValue())
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
