package com.fo0.vaadin.scrumtool.ui.views.components.column;

import static java.lang.String.format;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.fo0.vaadin.scrumtool.ui.broadcast.BroadcasterBoard;
import com.fo0.vaadin.scrumtool.ui.broadcast.BroadcasterColumn;
import com.fo0.vaadin.scrumtool.ui.config.Config;
import com.fo0.vaadin.scrumtool.ui.data.enums.ECardType;
import com.fo0.vaadin.scrumtool.ui.data.interfaces.IDataOrder;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBCardRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBColumnRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBDataRepository;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBColumn;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBData;
import com.fo0.vaadin.scrumtool.ui.model.TextItem;
import com.fo0.vaadin.scrumtool.ui.session.SessionUtils;
import com.fo0.vaadin.scrumtool.ui.styles.STYLES;
import com.fo0.vaadin.scrumtool.ui.utils.SpringContext;
import com.fo0.vaadin.scrumtool.ui.utils.Utils;
import com.fo0.vaadin.scrumtool.ui.views.KanbanView;
import com.fo0.vaadin.scrumtool.ui.views.components.ToolTip;
import com.fo0.vaadin.scrumtool.ui.views.components.card.CardComponent;
import com.fo0.vaadin.scrumtool.ui.views.components.interfaces.IBroadcastRegistry;
import com.fo0.vaadin.scrumtool.ui.views.components.interfaces.IComponent;
import com.fo0.vaadin.scrumtool.ui.views.dialogs.CreateColumnDialog;
import com.fo0.vaadin.scrumtool.ui.views.dialogs.CreateVotingCardDialog;
import com.fo0.vaadin.scrumtool.ui.views.dialogs.KBConfirmDialog;
import com.fo0.vaadin.scrumtool.ui.views.utils.KBViewUtils;
import com.google.common.collect.Sets;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dnd.DropEffect;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

@Log4j2
public class ColumnComponent extends VerticalLayout implements IBroadcastRegistry, IComponent {

  private static final long serialVersionUID = 8415434953831247614L;
  private final HorizontalLayout captionLayout;
  private Icon icon;

  private final KBDataRepository dataRepository = SpringContext.getBean(KBDataRepository.class);
  private final KBCardRepository cardRepository = SpringContext.getBean(KBCardRepository.class);
  private final KBColumnRepository repository = SpringContext.getBean(KBColumnRepository.class);

  @Getter
  private final KanbanView view;

  private TextArea area;
  private H3 h3;
  private VerticalLayout cards;
  private String id;

  public ColumnComponent(KanbanView view, TKBColumn column) {
    this.view = view;
    setId(column.getId());
    this.id = getId().get();

    setWidth("400px");
    getStyle().set("box-shadow", "var(--material-shadow-elevation-4dp)");
    setSpacing(true);
    setMargin(false);

    h3 = new H3();
    h3.setWidthFull();
    h3.getStyle()
      .set("margin", "unset")
      .set("padding-left", "0.2em");

    icon = VaadinIcon.INFO_CIRCLE_O.create();
    captionLayout = new HorizontalLayout(icon, h3);

    // initialized in changeTitle
    changeTitle(column.getName(), column.getDescription(), column.getDataOrder());

    captionLayout.setAlignItems(Alignment.CENTER);
    captionLayout.setMargin(false);
    captionLayout.setSpacing(true);

    addTitleOptions(view, column, captionLayout);

    captionLayout.setWidthFull();
    captionLayout.setVerticalComponentAlignment(Alignment.CENTER, h3);

    add(captionLayout);

    VerticalLayout layoutHeader = new VerticalLayout();
    layoutHeader.getStyle()
                .set("flex-shrink", "0");
    layoutHeader.getStyle()
                .set("border", "2px solid black");
    layoutHeader.getStyle()
                .set("overflow", "auto");
    layoutHeader.setWidthFull();
    layoutHeader.setHeight("200px");
    add(layoutHeader);

    area = new TextArea();
    area.setWidthFull();
    area.getStyle()
        .set("flex-grow", "1");
    area.setPlaceholder("Enter your text.");

    if (view.getOptions()
            .getMaxCardTextLength() > 0) {
      area.setMaxLength(view.getOptions()
                            .getMaxCardTextLength());
      area.setValueChangeMode(ValueChangeMode.EAGER);
      area.addValueChangeListener(e -> {
        if (e.getSource()
             .getValue()
             .length() > view.getOptions()
                             .getMaxCardTextLength()) {
          layoutHeader.getStyle()
                      .set("border-color", STYLES.COLOR_RED_500);
        } else {
          layoutHeader.getStyle()
                      .remove("border-color");
        }
      });
    }

    VerticalLayout txtLayout = new VerticalLayout(area);
    txtLayout.setWidthFull();
    txtLayout.setPadding(false);
    txtLayout.getStyle()
             .set("overflow-y", "auto");
    txtLayout.getStyle()
             .set("flex-grow", "1");

    layoutHeader.add(txtLayout);

    Button btnAdd = new Button("Card", VaadinIcon.PLUS.create());
    ToolTip.add(btnAdd, "Add a Card");
    btnAdd.setWidthFull();
    btnAdd.addClickListener(e -> {
      if (view.getOptions()
              .getMaxCards() > 0) {
        if (cards.getComponentCount() >= view.getOptions()
                                             .getMaxCards()) {
          Notification.show("Card limit reached", Config.NOTIFICATION_DURATION, Position.MIDDLE);
          return;
        }
      }

      if (StringUtils.isBlank(area.getValue())) {
        Notification.show("Please enter a text", Config.NOTIFICATION_DURATION, Position.MIDDLE);
        return;
      }

      TKBCard card = TKBCard.builder()
                            .type(ECardType.TextCard)
                            .ownerId(SessionUtils.getSessionId())
                            .build();
      card.setTextByType(TextItem.builder()
                                 .text(area.getValue())
                                 .build());
      TKBColumn col = addCardAndSave(card);
      update(col.getId());

      area.clear();
      area.focus();
    });

    Button btnCancel = new Button("Clear", VaadinIcon.TRASH.create());
    ToolTip.add(btnCancel, "Clear the Input");
    btnCancel.setWidthFull();
    btnCancel.addClickListener(e -> {
      area.clear();
    });

    MenuBar cardOptionButton = createCardOptionButton();
    HorizontalLayout btnGroup = new HorizontalLayout(btnAdd, cardOptionButton);
    btnGroup.setSpacing(false);

    HorizontalLayout btnLayout = new HorizontalLayout(btnCancel, btnGroup);
    btnLayout.setWidthFull();

    layoutHeader.add(btnLayout);
    setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, h3);

    cards = new VerticalLayout();
    cards.getStyle()
         .set("overflow", "auto");
    cards.setMargin(false);
    cards.setPadding(false);
    cards.setSpacing(true);
    cards.setHeightFull();

    DropTarget<VerticalLayout> dropTarget = DropTarget.create(cards);
    dropTarget.setDropEffect(DropEffect.MOVE);
    dropTarget.addDropListener(e -> {
      e.getDragSourceComponent()
       .ifPresent(card -> {
         String dragColumnId = e.getDragData()
                                .get()
                                .toString();
         if (hasCardById(dragColumnId)) {
           log.debug("dropping in same layout is not supported");
           return;
         }

         CardComponent droppedCard = (CardComponent) card;

         // update
         log.debug("receive dropped card: " + droppedCard.getId()
                                                         .get());
         droppedCard.getCard()
                    .setId(Utils.randomId());

         TKBColumn col = addCardAndSave(droppedCard.getCard());
         update(col.getId());

         // delete old and update
         droppedCard.deleteCard();
       });
    });

    add(cards);
  }

  private MenuBar createCardOptionButton() {
    MenuBar menuBar = new MenuBar();
    ToolTip.add(menuBar, "Card-Options");

    menuBar.getStyle()
           .set("margin-right", "1px");
    menuBar.getStyle()
           .set("margin-left", "1px");
    menuBar.addThemeName("no-overflow-button");

    MenuItem menuItem = menuBar.addItem(FontAwesome.Solid.ELLIPSIS_V.create());
    menuItem.getSubMenu()
            .addItem("Voting-Card", e -> {
              if (view.getOptions()
                      .getMaxCards() > 0) {
                if (cards.getComponentCount() >= view.getOptions()
                                                     .getMaxCards()) {
                  Notification.show("Card limit reached",
                                    Config.NOTIFICATION_DURATION,
                                    Position.MIDDLE);
                  return;
                }
              }

              if (StringUtils.isBlank(area.getValue())) {
                Notification.show("Please enter a text",
                                  Config.NOTIFICATION_DURATION,
                                  Position.MIDDLE);
                return;
              }

              new CreateVotingCardDialog(view, this, getId().get(), area.getValue()).open();
              area.clear();
            });

    return menuBar;
  }

  private void addTitleOptions(KanbanView view, TKBColumn column, HorizontalLayout captionLayout) {
    MenuBar menuBar = new MenuBar();
    ToolTip.add(menuBar, "Settings");

    menuBar.getStyle()
           .set("margin-right", "1px");
    menuBar.getStyle()
           .set("margin-left", "1px");
    menuBar.addThemeName("no-overflow-button");

    captionLayout.add(menuBar);

    MenuItem menuItem = menuBar.addItem(FontAwesome.Solid.ELLIPSIS_V.create());
    if (KBViewUtils.isAllowed(view.getOptions(), column.getOwnerId())) {
      menuItem.getSubMenu()
              .addItem("Edit", e -> {
                new CreateColumnDialog(h3.getText(),
                                       ToolTip.getTooltip(icon, Strings.EMPTY),
                                       saveListener -> {
                                         log.info("Edit column: " + getId().get());
                                         TKBColumn c = repository.findById(id)
                                                                 .get();

                                         c.setName(saveListener[0]);
                                         c.setDescription(saveListener[1]);
                                         repository.save(c);
                                         BroadcasterColumn.broadcast(id, "update");
                                       }).open();
              });

      menuItem.getSubMenu()
              .addItem("Shuffle Cards", e -> {
                TKBColumn tmp = repository.findById(getId().get())
                                          .get();

                List<TKBCard> toShuffle = tmp
                    .getCards()
                    .stream()
                    .collect(Collectors.toList());

                Collections.shuffle(toShuffle);

                // fix order
                IntStream.range(0, toShuffle.size())
                         .forEachOrdered(counter -> {
                           TKBCard cc = toShuffle.get(counter);
                           cc.setDataOrder(counter);
                         });

                tmp.setCards(Sets.newHashSet(toShuffle));
                tmp = repository.save(tmp);
                BroadcasterColumn.broadcast(getId().get(), BroadcasterColumn.MESSAGE_SORT);
              });

      menuItem.getSubMenu()
              .addItem("Sort Cards by Votes", e -> {
                TKBColumn tmp = repository.findById(getId().get())
                                          .get();

                List<TKBCard> sortedByVotes = tmp
                    .getCards()
                    .stream()
                    .sorted(Comparator.comparingLong(TKBCard::getLikes))
                    .collect(Collectors.toList());

                // fix order
                IntStream.range(0, sortedByVotes.size())
                         .forEachOrdered(counter -> {
                           TKBCard cc = sortedByVotes.get(counter);
                           cc.setDataOrder(counter);
                         });

                tmp.setCards(Sets.newHashSet(sortedByVotes));
                tmp = repository.save(tmp);
                BroadcasterColumn.broadcast(getId().get(), BroadcasterColumn.MESSAGE_SORT);
              });

      menuItem.getSubMenu()
              .addItem("Delete", e -> {
                KBConfirmDialog.createQuestion()
                               .withCaption("Deleting Column: " + column.getName())
                               .withMessage(format("This will remove '%s' cards",
                                                   cards.getComponentCount()))
                               .withOkButton(this::deleteColumn)
                               .withCancelButton()
                               .open();
              });
    }

  }

  private void update(String columnId) {
    BroadcasterColumn.broadcast(getId().get(), BroadcasterColumn.ADD_COLUMN + columnId);
  }

  private void deleteColumn() {
    log.info("delete column: " + getId().get());
    Notification.show("Deleting Column: " + h3.getText(),
                      Config.NOTIFICATION_DURATION,
                      Position.MIDDLE);
    TKBData c = dataRepository.findByIdFetched(view.getId()
                                                   .get());
    c.removeColumnById(id);
    dataRepository.save(c);
    BroadcasterBoard.broadcast(view.getId()
                                   .get(), "update");
  }

  public TKBColumn addCardAndSave(TKBCard card) {
    TKBColumn tmp = repository.findById(getId().get())
                              .get();
    card.setDataOrder(KBViewUtils.calculateNextPosition(tmp.getCards()));
    tmp.addCard(card);
    repository.save(tmp);
    log.info("add card: {}", card.getId());
    return tmp;
  }

  private CardComponent addCardLayout(TKBCard card) {
    CardComponent cc = new CardComponent(view, this, getId().get(), card);

    if (view.getOptions()
            .isCardSortDirectionDesc()) {
      cards.addComponentAsFirst(cc);
    } else {
      cards.add(cc);
    }
    return cc;
  }

  public void reload() {
    log.info("reloading column: {}", getId().get());
    TKBColumn data = repository.findById(getId().get())
                               .get();

    changeTitle(data.getName(), data.getDescription(), data.getDataOrder());

    // update layout with new missing data
    data.getCards()
        .stream()
        .sorted(Comparator.comparing(IDataOrder::getDataOrder))
        .forEachOrdered(pdc -> {
          CardComponent card = getComponentById(cards, CardComponent.class, pdc.getId());
          if (card == null) {
            // add card as new card
            card = addCardLayout(pdc);
          }

          card.reload();
        });

    // remove old
    getComponentsByType(cards, Component.class)
        .stream()
        .filter(e -> data.getCards()
                         .stream()
                         .noneMatch(x -> x.getId()
                                          .equals(e.getId()
                                                   .get())))
        .collect(Collectors.toList())
        .forEach(e -> {
          cards.remove(e);
        });
  }

  public void changeTitle(String string, String description, int order) {
    if (!h3.getText()
           .equals(string)) {
      h3.setText(string);
    }

    // change only at differences
    String lastTooltip = ToolTip.getTooltip(icon, Strings.EMPTY);
    if (!lastTooltip.equals(description)) {
      log.debug("set column tooltip from/to: {} -> {}", lastTooltip, description);
      Icon iconTmp = null;
      if (StringUtils.isBlank(description)) {
        iconTmp = VaadinIcon.INFO_CIRCLE_O.create();
      } else {
        iconTmp = VaadinIcon.INFO_CIRCLE.create();
      }

      captionLayout.replace(icon, iconTmp);
      icon = iconTmp;

      ToolTip.add(icon, description);
    }

    if (Config.DEBUG) {
      h3.setText(string + " (" + order + ")");
    }
  }

  public boolean hasCardById(String id) {
    return cards.getChildren()
                .anyMatch(e -> e.getId()
                                .get()
                                .equals(id));
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    UI ui = UI.getCurrent();
    registerBroadcast("column", BroadcasterColumn.register(getId().get(), event -> {
      ui.access(() -> {
        if (Config.DEBUG) {
          Notification.show("receiving broadcast for update", Config.NOTIFICATION_DURATION,
                            Position.BOTTOM_END);
        }

        String[] cmd = event.split("\\.");

        switch (cmd[0]) {
          case BroadcasterColumn.MESSAGE_SORT:
            cards.removeAll();
            reload();
            break;

          case BroadcasterColumn.ADD_COLUMN:
            TKBCard pdc = cardRepository.findById(cmd[1])
                                        .get();
            CardComponent card = getComponentById(cards, CardComponent.class, pdc.getId());
            if (card == null) {
              card = addCardLayout(pdc);
            }

            card.reload();
            break;

          default:
            reload();
            break;
        }

      });
    }));
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    unRegisterBroadcasters();
  }

}
