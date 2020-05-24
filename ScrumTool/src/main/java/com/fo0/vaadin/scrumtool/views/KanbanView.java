package com.fo0.vaadin.scrumtool.views;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.olli.ClipboardHelper;

import com.fo0.vaadin.scrumtool.broadcast.BroadcasterBoard;
import com.fo0.vaadin.scrumtool.config.Config;
import com.fo0.vaadin.scrumtool.config.KanbanConfig;
import com.fo0.vaadin.scrumtool.data.interfaces.IDataOrder;
import com.fo0.vaadin.scrumtool.data.repository.KBDataRepository;
import com.fo0.vaadin.scrumtool.data.table.TKBColumn;
import com.fo0.vaadin.scrumtool.data.table.TKBData;
import com.fo0.vaadin.scrumtool.data.table.TKBOptions;
import com.fo0.vaadin.scrumtool.session.SessionUtils;
import com.fo0.vaadin.scrumtool.styles.STYLES;
import com.fo0.vaadin.scrumtool.views.components.ColumnComponent;
import com.fo0.vaadin.scrumtool.views.components.CreateColumnDialog;
import com.fo0.vaadin.scrumtool.views.components.ThemeToggleButton;
import com.fo0.vaadin.scrumtool.views.data.IThemeToggleButton;
import com.fo0.vaadin.scrumtool.views.layouts.MainLayout;
import com.fo0.vaadin.scrumtool.views.utils.KBViewUtils;
import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * The main view is a top-level placeholder for other views.
 */
@Log4j2
@Route(value = KanbanView.NAME, layout = MainLayout.class)
public class KanbanView extends Div implements HasUrlParameter<String>, IThemeToggleButton {

	public static final String NAME = "kanbanboard";

	private static final long serialVersionUID = 8874200985319706829L;

	@Autowired
	private KBDataRepository repository;

	@Getter
	private VerticalLayout root;

	@Getter
	private HorizontalLayout header;

	@Getter
	private ThemeToggleButton themeToggleButton;

	@Getter
	public HorizontalLayout columns;

	private Button btnBoardId;

	private Button btnDelete;
	private ClipboardHelper btnBoardIdClipboard;

	@Getter
	private TKBOptions options;

	private String ownerId;

	private Registration broadcasterRegistration;

	private void init() {
		log.info("init");
		setSizeFull();
		root = KBViewUtils.createRootLayout();
		add(root);

		header = createHeaderLayout();
		root.add(header);

		columns = KBViewUtils.createColumnLayout();
		root.add(columns);

		root.expand(columns);
	}

	@Override
	public void setParameter(BeforeEvent event, String parameter) {
		SessionUtils.createSessionIdIfExists();
		setId(parameter);

		if (!repository.findById(getId().get()).isPresent()) {
			Button b = new Button("No Session Found -> Navigate to Dashbaord");
			b.addClickListener(e -> UI.getCurrent().navigate(MainView.class));
			add(b);
			return;
		}

		TKBData tmp = repository.findById(getId().get()).get();
		if (options == null) {
			options = tmp.getOptions();
			if (options == null) {
				options = new TKBOptions();
			}
			ownerId = tmp.getOwnerId();
		}

		init();
		sync();
		setSessionIdAtButton(getId().get());
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
//		super.onAttach(attachEvent);
		UI ui = UI.getCurrent();
		broadcasterRegistration = BroadcasterBoard.register(getId().get(), event -> {
			ui.access(() -> {
				if (Config.DEBUG) {
					Notification.show("receiving broadcast for update", Config.NOTIFICATION_DURATION, Position.BOTTOM_END);
				}
				reload();
			});
		});
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
//		super.onDetach(detachEvent);
		if (broadcasterRegistration != null) {
			broadcasterRegistration.remove();
			broadcasterRegistration = null;
		} else {
			log.info("cannot remove broadcast, because it is null");
		}
	}

	public void sync() {
		log.info("sync & refreshing data: {}", getId().get());
		reload();
	}

	public void reload() {
		TKBData tmp = repository.findById(getId().get()).get();

		// update layout with new missing data
		tmp.getColumns().stream().sorted(Comparator.comparing(IDataOrder::getDataOrder)).forEachOrdered(pdc -> {
			ColumnComponent column = getColumnLayoutById(pdc.getId());
			if (column == null) {
				// add card as new card
				column = addColumnLayout(pdc);
			}

			column.reload();
		});

		// removes deleted columns
		//@formatter:off
		getColumnComponents().stream()
				.filter(e -> tmp.getColumns().stream().noneMatch(x -> x.getId().equals(e.getId().get())))
				.collect(Collectors.toList()).forEach(e -> {
					log.info("remove column: " + e.getId());
					columns.remove(e);
				});
		//@formatter:on

		// reorder order columns
		// TODO
	}

	public List<ColumnComponent> getColumnComponents() {
		List<ColumnComponent> components = Lists.newArrayList();
		for (int i = 0; i < columns.getComponentCount(); i++) {
			if (columns.getComponentAt(i) instanceof ColumnComponent) {
				components.add((ColumnComponent) columns.getComponentAt(i));
			}
		}

		return components;
	}

	public void addColumn(String id, String ownerId, String name) {
		log.info("add column: {} ({})", name, id);
		TKBData tmp = repository.findById(getId().get()).get();
		tmp.addColumn(TKBColumn.builder().id(id).ownerId(ownerId).dataOrder(KBViewUtils.calculateNextPosition(tmp.getColumns())).name(name)
				.build());
		repository.save(tmp);
	}

	public ColumnComponent addColumnLayout(TKBColumn column) {
		if (getColumnLayoutById(column.getId()) != null) {
			log.warn("column already exists: {} - {}", column.getId(), column.getName());
			return null;
		}

		ColumnComponent col = new ColumnComponent(this, column);
		columns.add(col);
		return col;
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

	public HorizontalLayout createHeaderLayout() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.getStyle().set("border", "0.5px solid black");
		layout.setWidthFull();

		Button b = new Button("Column", VaadinIcon.PLUS.create());
		b.addClickListener(e -> {
			if (columns.getComponentCount() >= KanbanConfig.MAX_COLUMNS) {
				Notification.show("Column limit reached", Config.NOTIFICATION_DURATION, Position.MIDDLE);
				return;
			}

			new CreateColumnDialog(this).open();
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

		if (KBViewUtils.isAllowed(options, ownerId)) {
			btnDelete = new Button("Delete", VaadinIcon.TRASH.create());
			btnDelete.getStyle().set("color", STYLES.COLOR_RED_500);
			btnDelete.addClickListener(e -> {
				new ConfirmDialog("Delete", null, "Delete", ok -> {
					UI.getCurrent().navigate(MainView.class);
					repository.deleteById(getId().get());
				}).open();
			});
			layout.add(btnDelete);
		}

		themeToggleButton = new ThemeToggleButton(false);
		layout.add(themeToggleButton);

		if (Config.DEBUG) {
			Button btnDebug = new Button("Debug", VaadinIcon.INFO.create());
			btnDebug.addClickListener(e -> {
				Dialog d = new Dialog();
				d.setWidth("500px");
				d.setHeight("500px");
				Label t = new Label(new GsonBuilder().setPrettyPrinting().create().toJson(repository.findById(getId().get())));
				t.getStyle().set("white-space", "pre-wrap");
				t.setSizeFull();
				d.add(t);
				d.open();
			});
			layout.add(btnDebug);
		}

		return layout;
	}

	public void setSessionIdAtButton(String id) {
		btnBoardId.setText("Board: " + id);
		btnBoardIdClipboard.setContent(id);
	}
}
