package com.fo0.vaadin.scrumtool.ui.views.components.voting;

import com.fo0.vaadin.scrumtool.ui.broadcast.BroadcasterCard;
import com.fo0.vaadin.scrumtool.ui.config.Config;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBCardRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBColumnRepository;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.ui.utils.SpringContext;
import com.fo0.vaadin.scrumtool.ui.views.KanbanView;
import com.fo0.vaadin.scrumtool.ui.views.components.column.ColumnComponent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@CssImport(value = "./styles/card-style.css", themeFor = "vaadin-horizontal-layout")
public class VotingCardComponent extends HorizontalLayout {

	private static final long serialVersionUID = -7851628346339445219L;

	private KBCardRepository cardRepository = SpringContext.getBean(KBCardRepository.class);
	private KBColumnRepository columnRepository = SpringContext.getBean(KBColumnRepository.class);

	@Getter
	private TKBCard card;
	private ColumnComponent column;
	private VotingItemComponent likeComponent;
	private String columnId;
	private String cardId;
	private Registration broadcasterRegistration;

	public VotingCardComponent(KanbanView view, ColumnComponent column, String columnId, TKBCard card) {
		this.card = card;
		this.columnId = columnId;
		this.column = column;

		setId(card.getId());
		cardId = getId().get();
		setSpacing(true);
		setPadding(true);
		setMargin(false);

		VerticalLayout btnLayout = new VerticalLayout();
		btnLayout.setWidth("unset");
		btnLayout.setSpacing(false);
		btnLayout.setMargin(false);
		btnLayout.setPadding(false);
		add(btnLayout);

		setWidthFull();
		getStyle().set("box-shadow", "0.5px solid black");
		getStyle().set("border-radius", "1em");
		getStyle().set("border", "1px solid var(--material-disabled-text-color)");
		addClassName("card-hover");
	}

	public void deleteCard() {
		log.info("delete card: " + getId().get());
		TKBCard c = cardRepository.findById(column.getId().get()).get();
		 //c.getItems().add(getId().get()); columnRepository.save(c);
		 //BroadcasterColumn.broadcast(column.getId().get(), "update");
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
//		super.onAttach(attachEvent);
		UI ui = UI.getCurrent();
		broadcasterRegistration = BroadcasterCard.register(getId().get(), event -> {
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

	public void reload() {
		//card = cardRepository.findById(getId().get()).get();
	}

}
