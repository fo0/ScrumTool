package com.fo0.vaadin.scrumtool.ui.views.dialogs;

import com.fo0.vaadin.scrumtool.ui.data.repository.KBDataRepository;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBData;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBOptions;
import com.fo0.vaadin.scrumtool.ui.session.SessionUtils;
import com.fo0.vaadin.scrumtool.ui.utils.SpringContext;
import com.fo0.vaadin.scrumtool.ui.views.KanbanView;
import com.fo0.vaadin.scrumtool.ui.views.components.CustomNumberField;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import org.springframework.beans.factory.annotation.Autowired;

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
  private Checkbox chkOptLatestCardOnTop;

  public CreateBoardDialog() {
    setWidth("450px");
    setHeight("700px");

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
    title.getStyle()
         .set("text-align", "center");
    title.setWidthFull();
    return title;
  }

  private VerticalLayout createOptionsLayout() {
    VerticalLayout layout = new VerticalLayout();
    layout.setWidthFull();
    layout.setFlexGrow(1);
    add(layout);

    nmbColumnsMax = createNumberField(layout, "Max-Columns", 0, 0, 50);
    nmbCardsMax = createNumberField(layout, "Max-Cards", 0, 0, 99);
    nmbCardTextLengthMax = createNumberField(layout, "Max Card Text Length", 0, 0, 999);
    nmbMaxPerOwner = createNumberField(layout, "Max-Likes per User", 0, 0, Integer.MAX_VALUE);
    nmbCardLikesMaxPerOwner = createNumberField(layout,
                                                "Max Card Likes per User",
                                                0,
                                                0,
                                                Integer.MAX_VALUE);

    chkOptPermissionSystem = new Checkbox("Permissionsystem");
    chkOptPermissionSystem.setWidthFull();
    layout.add(chkOptPermissionSystem);

    chkOptLatestCardOnTop = new Checkbox("Oldest Card in Column on top");
    chkOptLatestCardOnTop.setValue(false);
    chkOptLatestCardOnTop.setWidthFull();
    layout.add(chkOptLatestCardOnTop);

    return layout;
  }

  /**
   * @param title
   * @param defaultValue
   * @param min
   * @param max
   * @param zeroIsInfinite
   * @return
   * @Created 31.05.2020 - 21:10:01
   * @author KaesDingeling
   */
  private NumberField createNumberField(VerticalLayout layout, String title, int defaultValue,
                                        int min, int max) {
    CustomNumberField numberField = new CustomNumberField(title, min, max, defaultValue, true);
    numberField.setWidthFull();

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
      TKBData p = repository.save(
          TKBData.builder()
                 .ownerId(SessionUtils.getSessionId())
                 .options(TKBOptions.builder()
                                    .optionPermissionSystem(chkOptPermissionSystem.getValue())
                                    .maxColumns(getCurrentOrDefaultValue(nmbColumnsMax))
                                    .maxCards(getCurrentOrDefaultValue(nmbCardsMax))
                                    .maxCardTextLength(getCurrentOrDefaultValue(nmbCardTextLengthMax))
                                    .maxLikesPerUser(getCurrentOrDefaultValue(nmbMaxPerOwner))
                                    .maxLikesPerUserPerCard(getCurrentOrDefaultValue(
                                        nmbCardLikesMaxPerOwner))
                                    .cardSortDirectionDesc(!chkOptLatestCardOnTop.getValue())
                                    .build())
                 .build());

      UI.getCurrent()
        .navigate(KanbanView.class, p.getId());

      close();
    });

    HorizontalLayout l = new HorizontalLayout(btnClose, btnOk);
    l.setWidthFull();
    return l;
  }

  private int getCurrentOrDefaultValue(NumberField field) {
    return field.getValue()
                .intValue() == 0 ? (int) field.getMax() : field.getValue()
                                                               .intValue();
  }

}
