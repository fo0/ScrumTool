package com.fo0.vaadin.scrumtool.ui.views.dialogs;

import com.fo0.vaadin.scrumtool.ui.export.ExportJson;
import com.fo0.vaadin.scrumtool.ui.views.KanbanView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.dom.Style;
import org.apache.logging.log4j.util.Strings;

public class ChooseCreationDialog extends Dialog {

  private static final long serialVersionUID = 2274992601002314827L;

  private HorizontalLayout root;

  public ChooseCreationDialog() {
    setHeight("175px");
    setWidth("500px");

    root = createRootLayout();
    add(root);

    HorizontalLayout centerLayout = new HorizontalLayout();
    root.add(centerLayout);
    Button btnCreate = createBtnCreate();
    Button btnJoin = createBtnJoin();
    centerLayout.add(btnCreate);
    centerLayout.add(btnJoin);

    root.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, centerLayout);
    root.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
  }

  private Button createBtnJoin() {
    Button btn = new Button("Import");
    btn.getStyle()
       .set("border", "1px solid black");
    btn.setWidth("150px");
    btn.setHeight("100px");
    btn.addClickListener(e -> {
      // TODO:
      Checkbox chkNewBoardId = new Checkbox("New Board-ID", false);
      TextFieldDialog dialog = new TextFieldDialog(
          "Import Board Data",
          "Data as Json",
          Strings.EMPTY,
          text -> {
            String id = ExportJson.importAsJson(text, chkNewBoardId.getValue());
            if (id == null) {
              return;
            }

            UI.getCurrent()
              .navigate(KanbanView.class, id);

            close();
          });

      dialog.getRootLayout()
            .addComponentAtIndex(dialog.getRootLayout()
                                       .getComponentCount() - 1, chkNewBoardId);

      Style style = dialog.getTextField()
                          .getStyle();

      style.set("overflow-x", "hidden");

      dialog.open();
    });
    return btn;
  }

  private Button createBtnCreate() {
    Button btn = new Button("New Board");
    btn.getStyle()
       .set("border", "1px solid black");
    btn.addClickListener(e -> {
      new CreateBoardDialog().open();
      close();
    });
    btn.setWidth("150px");
    btn.setHeight("100px");
    return btn;
  }

  private HorizontalLayout createRootLayout() {
    HorizontalLayout layout = new HorizontalLayout();
    layout.setSizeFull();
    return layout;
  }

}
