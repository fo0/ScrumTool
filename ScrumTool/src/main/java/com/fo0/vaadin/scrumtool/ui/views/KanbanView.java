package com.fo0.vaadin.scrumtool.ui.views;

import static com.vaadin.flow.component.notification.Notification.Position.BOTTOM_CENTER;
import static com.vaadin.flow.component.notification.Notification.show;

import com.fo0.vaadin.scrumtool.ui.broadcast.BroadcasterBoard;
import com.fo0.vaadin.scrumtool.ui.broadcast.BroadcasterBoardTimer;
import com.fo0.vaadin.scrumtool.ui.broadcast.BroadcasterUsers;
import com.fo0.vaadin.scrumtool.ui.config.Config;
import com.fo0.vaadin.scrumtool.ui.data.interfaces.IDataOrder;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBDataRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBOptionRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBUserRepository;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBColumn;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBData;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBOptions;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBUser;
import com.fo0.vaadin.scrumtool.ui.model.User;
import com.fo0.vaadin.scrumtool.ui.session.SessionUtils;
import com.fo0.vaadin.scrumtool.ui.utils.Utils;
import com.fo0.vaadin.scrumtool.ui.views.components.ThemeToggleButton;
import com.fo0.vaadin.scrumtool.ui.views.components.ToolTip;
import com.fo0.vaadin.scrumtool.ui.views.components.column.ColumnComponent;
import com.fo0.vaadin.scrumtool.ui.views.components.interfaces.IBroadcastRegistry;
import com.fo0.vaadin.scrumtool.ui.views.components.timer.TimerComponent;
import com.fo0.vaadin.scrumtool.ui.views.data.IThemeToggleButton;
import com.fo0.vaadin.scrumtool.ui.views.dialogs.CreateColumnDialog;
import com.fo0.vaadin.scrumtool.ui.views.dialogs.KBConfirmDialog;
import com.fo0.vaadin.scrumtool.ui.views.dialogs.MarkDownDialog;
import com.fo0.vaadin.scrumtool.ui.views.dialogs.ShareLayout;
import com.fo0.vaadin.scrumtool.ui.views.layouts.MainLayout;
import com.fo0.vaadin.scrumtool.ui.views.utils.KBViewUtils;
import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The main view is a top-level placeholder for other views.
 */
@Log4j2
@Route(value = KanbanView.NAME, layout = MainLayout.class)
@CssImport(value = "./styles/custom-menu-bar.css", themeFor = "vaadin-menu-bar")
@CssImport(value = "./styles/custom-menu-bar-button.css", themeFor = "vaadin-menu-bar-button")
public class KanbanView extends Div implements HasUrlParameter<String>, IThemeToggleButton,
    IBroadcastRegistry {

  public static final String NAME = "kanbanboard";

  private static final long serialVersionUID = 8874200985319706829L;
  @Getter
  public HorizontalLayout columns;
  @Autowired
  private KBDataRepository repository;
  @Autowired
  private KBOptionRepository optionRepository;
  @Autowired
  private KBUserRepository userRepository;
  @Getter
  private VerticalLayout root;
  @Getter
  private HorizontalLayout header;
  @Getter
  private ThemeToggleButton themeToggleButton;
  @Getter
  private TKBOptions options;
  private String ownerId;

  @Getter
  private String dataUserId;
  private TimerComponent timer;
  private Button btnUsers;

  private void init() {
    log.info("init");
    setSizeFull();

    root = KBViewUtils.createRootLayout();
    add(root);

    header = createHeaderLayout();
    root.add(header);

    columns = KBViewUtils.createColumnLayout();
    root.add(columns);
  }

  @Override
  public void setParameter(BeforeEvent event, String parameter) {
    setId(parameter);

    if (!repository.findById(getId().get())
                   .isPresent()) {
      createBoardNotFound(this);
      return;
    }

    TKBData tmp = repository.findById(getId().get())
                            .get();
    if (options == null) {
      options = tmp.getOptions();
      if (options == null) {
        options = TKBOptions.builder()
                            .build();
      }

      TKBUser u = userRepository.findByDataIdFetched(getId().get());
      if (u == null) {
        u = TKBUser.builder()
                   .build();
      }
      dataUserId = u.getId();
      ownerId = tmp.getOwnerId();
    }

    userRepository.insertUserById(dataUserId,
                                  User.builder()
                                      .id(SessionUtils.getSessionId())
                                      .active(true)
                                      .build());

    init();
    reload();
  }

  private void createBoardNotFound(HasComponents rootLayout) {
    log.info("could not find any board with ID: {}", getId().get());
    Button b = new Button("No Session found with ID '" + getId().get() + "'");
    b.addClickListener(e -> UI.getCurrent()
                              .navigate(MainView.class));
    setSizeFull();
    VerticalLayout layout = new VerticalLayout();
    layout.setJustifyContentMode(JustifyContentMode.CENTER);
    layout.setSizeFull();
    rootLayout.add(layout);
    layout.add(b);
    layout.setAlignSelf(Alignment.CENTER, b);
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    UI ui = UI.getCurrent();

    registerBroadcast("board", BroadcasterBoard.register(getId().get(), event -> {
      ui.access(() -> {
        if (Config.DEBUG) {
          Notification.show("receiving broadcast for update", Config.NOTIFICATION_DURATION,
                            Position.BOTTOM_END);
        }
        reload();
      });
    }));

    registerBroadcast("users", BroadcasterUsers.register(getId().get(), event -> {
      ui.access(() -> {
        if (Config.DEBUG) {
          Notification.show("receiving broadcast for update", Config.NOTIFICATION_DURATION,
                            Position.BOTTOM_END);
        }

        changeUsersCounter();
      });
    }));

    registerBroadcast("timer", BroadcasterBoardTimer.register(getId().get(), event -> {
      ui.access(() -> {
        if (Config.DEBUG) {
          Notification.show("receiving broadcast for timer", Config.NOTIFICATION_DURATION,
                            Position.BOTTOM_END);
        }

        String[] cmd = event.split("\\.");
        switch (cmd[0]) {
          case "start":
            timer.setTime(Long.valueOf(cmd[1]));
            persistTimer(Long.valueOf(cmd[1]));
            timer.startSilent();
            break;
          case "play":
            timer.playSilent();
            break;
          case "pause":
            timer.pauseSilent();
            break;
          case "stop":
            timer.stopSilent();
            break;
          default:
            break;
        }
      });
    }));

    BroadcasterUsers.broadcast(getId().get(), "update");
    super.onAttach(attachEvent);
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    log.info("detaching ui");
    userRepository.deleteUserById(dataUserId, SessionUtils.getSessionId());
    BroadcasterUsers.broadcast(getId().get(), "delete");

    unRegisterBroadcasters();

    super.onDetach(detachEvent);
  }

  public void reload() {
    log.info("sync & refreshing data: {}", getId().get());
    TKBData tmp = repository.findByIdFetched(getId().get());

    // update layout with new missing data
    tmp.getColumns()
       .stream()
       .sorted(Comparator.comparing(IDataOrder::getDataOrder))
       .forEachOrdered(pdc -> {
         ColumnComponent column = getColumnLayoutById(pdc.getId());
         if (column == null) {
           // add card as new card
           column = addColumnLayout(pdc);
         }

         column.reload();
       });

    // removes deleted columns
    // @formatter:off
		getColumnComponents().stream()
				.filter(e -> tmp.getColumns().stream().noneMatch(x -> x.getId().equals(e.getId().get())))
				.collect(Collectors.toList()).forEach(columns::remove);
		// @formatter:on

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

  public void addColumn(String id, String ownerId, String name, String description) {
    log.info("add column: {} ({})", name, id);
    TKBData tmp = repository.findByIdFetched(getId().get());

    tmp.addColumn(TKBColumn.builder()
                           .id(id)
                           .name(name)
                           .description(description)
                           .ownerId(ownerId)
                           .dataOrder(KBViewUtils.calculateNextPosition(tmp.getColumns()))
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
      if (col.getId()
             .get()
             .equals(columnId)) {
        return col;
      }
    }

    return null;
  }

  public HorizontalLayout createHeaderLayout() {
    HorizontalLayout left = new HorizontalLayout();
    left.setAlignItems(Alignment.CENTER);
    left.setSpacing(false);
    left.setMargin(false);
    left.setWidthFull();

    HorizontalLayout right = new HorizontalLayout();
    right.setJustifyContentMode(JustifyContentMode.END);
    right.setAlignItems(Alignment.CENTER);
    right.setSpacing(false);
    right.setMargin(false);
    right.setWidthFull();

    HorizontalLayout layout = new HorizontalLayout();
    layout.getStyle()
          .set("border", "0.5px solid black");
    layout.setAlignItems(Alignment.CENTER);
    layout.setSpacing(false);
    layout.setMargin(false);
    layout.setWidthFull();
    layout.add(left, right);

    Button b = new Button("Column", VaadinIcon.PLUS.create());
    ToolTip.add(b, "Create new column");

    b.addClickListener(e -> {
      if (options.getMaxColumns() > 0) {
        if (columns.getComponentCount() >= options.getMaxColumns()) {
          Notification.show("Column limit reached", Config.NOTIFICATION_DURATION, Position.MIDDLE);
          return;
        }
      }

      new CreateColumnDialog(Strings.EMPTY, Strings.EMPTY, saveListener -> {
        String name = saveListener[0];
        String description = saveListener[1];

        if (name.isEmpty()) {
          show("Please enter a column name", 3000, BOTTOM_CENTER);
          return;
        }

        addColumn(Utils.randomId(),
                  SessionUtils.getSessionId(),
                  name,
                  description);

        BroadcasterBoard.broadcast(getId().get(), "update");
      }).open();
    });
    left.add(b);

    if (Config.DEBUG) {
      Button btnDebug = new Button("Debug", VaadinIcon.BUG.create());
      btnDebug.addClickListener(e -> {
        Dialog d = new Dialog();
        d.setWidth("500px");
        d.setHeight("500px");
        Label t = new Label(new GsonBuilder().setPrettyPrinting()
                                             .create()
                                             .toJson(repository.findByIdFetched(getId().get())));
        t.getStyle()
         .set("white-space", "pre-wrap");
        t.setSizeFull();
        d.add(t);
        d.open();
      });
      left.add(btnDebug);
    }

    right.add(createTimer2());

    btnUsers = new Button(VaadinIcon.GROUP.create());
    right.add(btnUsers);

    MenuBar menuBar = new MenuBar();
    ToolTip.add(menuBar, "Settings");

    menuBar.getStyle()
           .set("margin-right", "5px");
    menuBar.getStyle()
           .set("margin-left", "1px");
    menuBar.addThemeName("no-overflow-button");

    right.add(menuBar);

    MenuItem menuItem = menuBar.addItem(VaadinIcon.COG.create());

    if (KBViewUtils.isAllowed(options, ownerId)) {
      menuItem.getSubMenu()
              .addItem("Delete Board", e -> {
                KBConfirmDialog
                    .createQuestion()
                    .withCaption("Delete Board")
                    .withMessage(String
                                     .format("This will delete the board and '%s' columns",
                                             columns.getComponentCount()))
                    .withOkButton(() -> {
                      repository.deleteById(getId().get());
                      UI.getCurrent()
                        .navigate(MainView.class);
                    })
                    .withCancelButton()
                    .open();
              });
    }

    menuItem.getSubMenu()
            .addItem("Reset all given Likes", e -> {
              KBConfirmDialog.createQuestion()
                             .withCaption("Reset all given Likes")
                             .withMessage("This will delete every like on any card")
                             .withOkButton(() -> {
                               TKBData data = repository.findByIdFetched(getId().get());
                               data.resetLikes();
                               repository.save(data);
                               BroadcasterBoard.broadcast(getId().get(), "update");
                             })
                             .withCancelButton()
                             .open();
            });

    menuItem.getSubMenu()
            .addItem("Refresh", e -> reload());

    menuItem.getSubMenu()
            .addItem("Export", e -> {
              new MarkDownDialog(repository.findByIdFetched(getId().get())).open();
            });

    menuItem.getSubMenu()
            .addItem("Share with others", e -> {
              new ShareLayout("Share Layout", () -> getId().get(),
                              () -> createCurrentUrl(VaadinService.getCurrentRequest())).open();
            });

    themeToggleButton = new ThemeToggleButton(false);
    menuItem.getSubMenu()
            .addItem(themeToggleButton);

    return layout;
  }

  private TimerComponent createTimer2() {
    timer = new TimerComponent();

    timer.setTime(options.getTimerInMillis());
    timer.addStartListener(e -> BroadcasterBoardTimer.broadcast(getId().get(),
                                                                "start." + timer.getTime()));
    timer.addPauseListener(e -> BroadcasterBoardTimer.broadcast(getId().get(), "pause"));
    timer.addPlayListener(e -> BroadcasterBoardTimer.broadcast(getId().get(), "play"));
    timer.addStopListener(e -> BroadcasterBoardTimer.broadcast(getId().get(), "stop"));

    timer.addTimerEndEvent(e -> {
      Notification.show("Timer ends", 5000, Position.MIDDLE);
    });
    return timer;
  }

  private String createCurrentUrl(VaadinRequest request) {
    try {
      VaadinServletRequest req = (VaadinServletRequest) request; // VaadinService.getCurrentRequest();
      StringBuffer uriString = req.getRequestURL();
      URI uri = new URI(uriString.toString());

      String url = uri.toString();
      if (!url.endsWith(getId().get())) {
        url = uri.toString() + NAME + "/" + getId().get();
      }

      return url;
    } catch (Exception e) {
      log.error("failed to create url share resource", e);
    }

    return "Error creating URL Resource";
  }

  public void changeUsersCounter() {
    getId().ifPresent(e -> {
      if (btnUsers != null) {
        btnUsers.setText(String.valueOf(userRepository.countByDataIdFetched(e)));
      }
    });
  }

  public void persistTimer(long time) {
    options = optionRepository.findById(options.getId())
                              .get();
    options.setTimerInMillis(time);
    options = optionRepository.save(options);
  }

}
