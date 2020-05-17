package com.fo0.vaadin.projects.views;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;

import com.fo0.vaadin.projects.data.repository.ProjectDataRepository;
import com.fo0.vaadin.projects.data.table.ProjectData;
import com.fo0.vaadin.projects.data.table.ProjectDataCard;
import com.fo0.vaadin.projects.utils.ProjectBoardViewLoader;
import com.google.gson.GsonBuilder;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * The main view is a top-level placeholder for other views.
 */
@Route(ProjectBoardView.NAME)
@Log4j2
@Push
public class ProjectBoardView extends Div implements HasUrlParameter<String> {

	public static final String NAME = "projectboard";

	private static final long serialVersionUID = 8874200985319706829L;

	@Autowired
	private ProjectDataRepository repository;

	@Getter
	private VerticalLayout root;

	@Getter
	private HorizontalLayout header;

	@Getter
	public HorizontalLayout columns;

	private Button btnSession;
	private String sessionId;

	private void init() {
		log.info("init");
		setSizeFull();
		root = ProjectBoardViewUtils.createRootLayout();
		add(root);

		header = createHeaderLayout();
		root.add(header);

		columns = ProjectBoardViewUtils.createColumnLayout();
		root.add(columns);

		root.expand(columns);
	}

	@Override
	public void setParameter(BeforeEvent event, String parameter) {
		sessionId = parameter;
		if (!repository.findById(sessionId).isPresent()) {
			Button b = new Button("No Session Found -> Navigate to Dashbaord");
			b.addClickListener(e -> UI.getCurrent().navigate(MainView.class));
			add(b);
			return;
		}

		init();
		sync();
		setSessionIdAtButton(sessionId);
	}

//	@Scheduled(fixedRate = 1000 * 5)
	public void sync() {
		ProjectData pd = repository.findById(sessionId).orElse(null);
		if (pd == null) {
			log.info("no data in repository found");
			return;
		}

		log.info("sync & refreshing data: {}", pd.getId());
		printProjectData();
		ProjectBoardViewLoader.loadData(this, pd);
	}

	public void printProjectData() {
		System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(getData()));
	}

	public void saveData(Function<ProjectData, ProjectData> save) {
		ProjectData tmp = repository.findById(sessionId).get();
		tmp = save.apply(tmp);
		tmp = repository.save(tmp);
		log.info("save data: {}", tmp.getId());
		printProjectData();
	}

	public ProjectData getData() {
		return repository.findById(sessionId).get();
	}

	public void addColumn(String id, String name) {
		if (getColumnLayoutById(id) != null) {
			log.warn("column already exists: {} - {}", id, name);
			return;
		}

		ColumnComponent col = createColumn(id, name);
		columns.add(col);
		saveData(data -> data.addColumn(col.getProductDataColumn()));
	}

	public ColumnComponent createColumn(String id, String name) {
		return new ColumnComponent(id, name);
	}

	public ColumnComponent getColumnLayoutById(String columnId) {
		for (int i = 0; i < columns.getComponentCount(); i++) {
			ColumnComponent col = (ColumnComponent) columns.getComponentAt(i);
			if (col.getId().get().equals(columnId)) {
				return col;
			}
		}

		return null;
	}

	public ColumnComponent getColumn(String columnId) {
		for (int i = 0; i < columns.getComponentCount(); i++) {
			ColumnComponent col = (ColumnComponent) columns.getComponentAt(i);
			if (col.getId().get().equals(columnId)) {
				return col;
			}
		}

		return null;
	}

	public void addCard(String columnId, String cardId, String message) {
		for (int i = 0; i < columns.getComponentCount(); i++) {
			ColumnComponent col = (ColumnComponent) columns.getComponentAt(i);
			if (col.getId().get().equals(columnId)) {
				col.addCard(cardId, message);
				break;
			}
		}

	}

	public ProjectDataCard getCardLayoutById(String columnId, String cardId) {
		ColumnComponent col = getColumnLayoutById(columnId);
		return col.getCardById(cardId);
	}

	public CardComponent createCard(String id, String name) {
		return new CardComponent(id, name);
	}

	public HorizontalLayout createHeaderLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.getStyle().set("border", "0.5px solid black");
		layout.setWidthFull();

		Button b = new Button("Column", VaadinIcon.PLUS.create());
		b.addClickListener(e -> {
			ProjectBoardViewUtils.createColumnDialog(this).open();
		});
		layout.add(b);

		btnSession = new Button("Session: Unknown", VaadinIcon.GROUP.create());
		layout.add(btnSession);

		Button btnSync = new Button("Refresh", VaadinIcon.REFRESH.create());
		btnSync.addClickListener(e -> {
			sync();
		});

		layout.add(btnSync);
		return layout;
	}

	public void setSessionIdAtButton(String id) {
		btnSession.setText("Session: " + id);
	}
}
