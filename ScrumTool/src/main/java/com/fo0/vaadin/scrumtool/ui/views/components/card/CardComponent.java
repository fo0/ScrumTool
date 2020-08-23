package com.fo0.vaadin.scrumtool.ui.views.components.card;

import java.util.Set;

import com.fo0.vaadin.scrumtool.ui.broadcast.BroadcasterCard;
import com.fo0.vaadin.scrumtool.ui.broadcast.BroadcasterColumn;
import com.fo0.vaadin.scrumtool.ui.config.Config;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBCardRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBColumnRepository;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCardComment;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBColumn;
import com.fo0.vaadin.scrumtool.ui.utils.SpringContext;
import com.fo0.vaadin.scrumtool.ui.views.KanbanView;
import com.fo0.vaadin.scrumtool.ui.views.components.column.ColumnComponent;
import com.fo0.vaadin.scrumtool.ui.views.components.interfaces.IBroadcastRegistry;
import com.fo0.vaadin.scrumtool.ui.views.components.interfaces.IComponent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@CssImport(value = "./styles/card-style.css", themeFor = "vaadin-horizontal-layout")
public class CardComponent extends HorizontalLayout implements IComponent, IBroadcastRegistry {

	private static final long serialVersionUID = -1213748155629932731L;

	private KBCardRepository cardRepository = SpringContext.getBean(KBCardRepository.class);
	private KBColumnRepository columnRepository = SpringContext.getBean(KBColumnRepository.class);

	@Getter
	private TKBCard card;

	@Getter
	private ColumnComponent column;

	@Getter
	private String columnId;

	@Getter
	private String cardId;

	@Getter
	private KanbanView view;

	private ICardTypeTemplate template;

	public CardComponent(KanbanView view, ColumnComponent column, String columnId, TKBCard card) {
		this.card = card;
		this.columnId = columnId;
		this.column = column;
		this.view = view;
		setId(card.getId());
		cardId = getId().get();

		setSpacing(true);
		setPadding(true);
		setMargin(false);

		switch (card.getType()) {
		case TextCard:
			template = new TextCardType(this);
			break;

		case VotingCard:
			template = new VotingCardType(this);
			break;

		default:
			log.error("failed to find template for card type: {}", card.getType());
			break;
		}
	}

	public void deleteCard() {
		log.info("delete card: " + getId().get());
		TKBColumn c = columnRepository.findById(column.getId().get()).get();
		c.removeCardById(getId().get());
		columnRepository.save(c);
		BroadcasterColumn.broadcast(column.getId().get(), "update");
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		UI ui = UI.getCurrent();
		registerBroadcast("card", BroadcasterCard.register(getId().get(), event -> {
			ui.access(() -> {
				if (Config.DEBUG) {
					Notification.show("receiving broadcast for update", Config.NOTIFICATION_DURATION, Position.BOTTOM_END);
				}
				reload();
			});
		}));
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		unRegisterBroadcasters();
	}

	public void changeText(String text) {
		template.changeText(text);
	}

	public void changeButtonCommentsCaption(Set<TKBCardComment> set) {
		template.changeButtonCommentsCaption(set);
	}

	public void reload() {
		template.reload();
	}

}
