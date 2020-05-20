package com.fo0.vaadin.scrumtool.views;

import java.util.function.Function;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.olli.ClipboardHelper;

import com.fo0.vaadin.scrumtool.config.KanbanConfig;
import com.fo0.vaadin.scrumtool.data.repository.ProjectDataRepository;
import com.fo0.vaadin.scrumtool.data.table.ProjectData;
import com.fo0.vaadin.scrumtool.data.table.ProjectDataCard;
import com.fo0.vaadin.scrumtool.session.SessionUtils;
import com.fo0.vaadin.scrumtool.styles.STYLES;
import com.fo0.vaadin.scrumtool.utils.ProjectBoardViewLoader;
import com.fo0.vaadin.scrumtool.views.components.CardComponent;
import com.fo0.vaadin.scrumtool.views.components.ColumnComponent;
import com.fo0.vaadin.scrumtool.views.layouts.MainLayout;
import com.fo0.vaadin.scrumtool.views.utils.ProjectBoardViewUtils;
import com.google.gson.GsonBuilder;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * The main view is a top-level placeholder for other views.
 */
@Log4j2
@Route(value = KanbanView.NAME, layout = MainLayout.class)
public class KanbanView extends Div implements HasUrlParameter<String> {

	public static final String NAME = "kanbanboard";

	private static final long serialVersionUID = 8874200985319706829L;

	@Autowired
	private ProjectDataRepository repository;

	@Getter
	private VerticalLayout root;

	@Getter
	private HorizontalLayout header;

	@Getter
	public HorizontalLayout columns;

	private Button btnBoardId;
	private String boardId;

	private Button btnDelete;
	private ClipboardHelper btnBoardIdClipboard;

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
		SessionUtils.createSessionIdIfExists();

		boardId = parameter;

		if (!repository.findById(boardId).isPresent()) {
			Button b = new Button("No Session Found -> Navigate to Dashbaord");
			b.addClickListener(e -> UI.getCurrent().navigate(MainView.class));
			add(b);
			return;
		}

		init();
		sync();
		setSessionIdAtButton(boardId);
	}

//	@Scheduled(fixedRate = 1000 * 5)
	public void sync() {
		ProjectData pd = repository.findById(boardId).orElse(null);
		if (pd == null) {
			log.info("no data in repository found");
			return;
		}
		
		log.info("sync & refreshing data: {}", pd.getId());
		ProjectBoardViewLoader.loadData(this, pd, SessionUtils.getSessionId());
	}

	public void printProjectData() {
		System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(getData()));
	}

	public void saveData(Function<ProjectData, ProjectData> save) {
		ProjectData tmp = repository.findById(boardId).get();
		tmp = save.apply(tmp);
		tmp = repository.save(tmp);
		log.info("save data: {}", tmp.getId());
//		printProjectData();
	}

	public ProjectData getData() {
		return repository.findById(boardId).get();
	}

	public ColumnComponent addColumn(String id, String ownerId, String name, boolean saveToDb) {
		if (columns.getComponentCount() >= KanbanConfig.MAX_COLUMNS) {
			Notification.show("Column limit reached", 3000, Position.MIDDLE);
			return null;
		}

		if (getColumnLayoutById(id) != null) {
			log.warn("column already exists: {} - {}", id, name);
			return null;
		}

		ColumnComponent col = createColumn(this, id, ownerId, name);
		columns.add(col);
		saveData(data -> data.addColumn(col.getProductDataColumn()));
		return col;
	}

	public ColumnComponent addColumn(String id, String name, boolean saveToDb) {
		return addColumn(id, SessionUtils.getSessionId(), name, saveToDb);
	}

	public ColumnComponent createColumn(KanbanView view, String id, String ownerId, String name) {
		return new ColumnComponent(view, id, ownerId, name);
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

	public void addCard(String columnId, String cardId, String ownerId, String message, boolean saveToDb) {
		ColumnComponent cc = getColumn(columnId);
		if (cc == null) {
			return;
		}

		if (CollectionUtils.size(cc.getProductDataColumn().getCards()) >= KanbanConfig.MAX_CARDS) {
			Notification.show("Card limit reached", 5000, Position.MIDDLE);
			return;
		}

		ProjectDataCard pdc = cc.getCardById(cardId);
		if (pdc != null) {
			log.warn("card already exists: {} - {}", cardId, message);
			return;
		}

		CardComponent ccc = cc.addCard(cardId, ownerId, message);
		saveData(data -> data.addCard(columnId, ccc.getCard()));
	}

	public ProjectDataCard getCardLayoutById(String columnId, String cardId) {
		ColumnComponent col = getColumnLayoutById(columnId);
		return col.getCardById(cardId);
	}

	public CardComponent createCard(String columnId, String cardId, String ownerId, String name) {
		return new CardComponent(this, columnId, cardId, ownerId, name);
	}

	public void removeCard(String columnId, String cardId) {
		ColumnComponent cc = getColumn(columnId);
		if (cc == null) {
			return;
		}
		log.info("[CARD] remove card " + cardId);
		cc.removeCardById(cardId);
		saveData(data -> data.removeCardById(columnId, cardId));
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

		btnBoardId = new Button("Board: Unknown", VaadinIcon.GROUP.create());
		btnBoardId.getStyle().set("vertical-align", "0");
		btnBoardIdClipboard = new ClipboardHelper("", btnBoardId);
		layout.add(btnBoardIdClipboard);

		Button btnSync = new Button("Refresh", VaadinIcon.REFRESH.create());
		btnSync.addClickListener(e -> {
			sync();
		});
		layout.add(btnSync);

		btnDelete = new Button("Delete", VaadinIcon.TRASH.create());
		btnDelete.getStyle().set("color", STYLES.COLOR_RED_500);
		btnDelete.addClickListener(e -> {
			new ConfirmDialog("Delete", null, "Delete", ok -> {
				UI.getCurrent().navigate(MainView.class);
				repository.deleteById(boardId);
			}).open();
		});
		layout.add(btnDelete);

		return layout;
	}

	public void setSessionIdAtButton(String id) {
		btnBoardId.setText("Board: " + id);
		log.info("projectdata ownerId: {} | SessionID: {}", getData().getOwnerId(), SessionUtils.getSessionId());
		if (!getData().getOwnerId().equals(SessionUtils.getSessionId())) {
			btnDelete.setVisible(false);
		}
		btnBoardIdClipboard.setContent(id);
	}
}
