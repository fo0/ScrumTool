package com.fo0.vaadin.scrumtool.views;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import com.fo0.vaadin.scrumtool.data.repository.ProjectDataRepository;
import com.fo0.vaadin.scrumtool.data.table.ProjectData;
import com.fo0.vaadin.scrumtool.session.SessionUtils;
import com.fo0.vaadin.scrumtool.views.layouts.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

/**
 * The main view is a top-level placeholder for other views.
 */
@Route(value = Strings.EMPTY, layout = MainLayout.class)
public class MainView extends VerticalLayout {

	private static final long serialVersionUID = 8874200985319706829L;

	@Autowired
	private ProjectDataRepository repository;

	private HorizontalLayout root;

	public MainView() {
		super();
		
		setSizeFull();
		setJustifyContentMode(JustifyContentMode.CENTER);
		
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
		Button btn = new Button("Beitreten");
		btn.getStyle().set("border", "1px solid black");
		btn.setWidth("150px");
		btn.setHeight("100px");
		btn.addClickListener(e -> {
			createJoinSessionDialog().open();
		});
		return btn;
	}

	private Button createBtnCreate() {
		Button btn = new Button("Erstellen");
		btn.getStyle().set("border", "1px solid black");
		btn.addClickListener(e -> {
			ProjectData p = repository.save(ProjectData.builder().ownerId(SessionUtils.getSessionId()).build());
			UI.getCurrent().navigate(KanbanView.class, p.getId());
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

	private Dialog createJoinSessionDialog() {
		Dialog d = new Dialog();
		TextField t = new TextField("Session-ID");
		Button b = new Button("Beitreten");

		b.addClickListener(e -> {
			ProjectData p = repository.findById(t.getValue()).get();
			if (p == null) {
				Notification.show("No Board found", 5000, Position.MIDDLE);
				return;
			}

			UI.getCurrent().navigate(KanbanView.class, p.getId());
			d.close();
		});

		HorizontalLayout l = new HorizontalLayout(t, b);
		l.setMargin(true);
		d.add(l);
		return d;
	}

}
