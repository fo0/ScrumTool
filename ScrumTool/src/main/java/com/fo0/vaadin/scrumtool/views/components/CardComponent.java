package com.fo0.vaadin.scrumtool.views.components;

import com.fo0.vaadin.scrumtool.broadcast.BroadcasterCards;
import com.fo0.vaadin.scrumtool.broadcast.BroadcasterColumns;
import com.fo0.vaadin.scrumtool.config.Config;
import com.fo0.vaadin.scrumtool.data.repository.KBCardRepository;
import com.fo0.vaadin.scrumtool.data.repository.KBColumnRepository;
import com.fo0.vaadin.scrumtool.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.data.table.TKBColumn;
import com.fo0.vaadin.scrumtool.utils.SpringContext;
import com.fo0.vaadin.scrumtool.views.KanbanView;
import com.fo0.vaadin.scrumtool.views.dialogs.ChangeTextDialog;
import com.fo0.vaadin.scrumtool.views.utils.KBViewUtils;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CardComponent extends HorizontalLayout {

	private static final long serialVersionUID = -1213748155629932731L;

	private KBCardRepository cardRepository = SpringContext.getBean(KBCardRepository.class);
	private KBColumnRepository columnRepository = SpringContext.getBean(KBColumnRepository.class);
	@Getter
	private TKBCard card;
	private LikeComponent likeComponent;
	private String columnId;
	private Label label;
	private Registration broadcasterRegistration;

	public CardComponent(KanbanView view, ColumnComponent column, String columnId, TKBCard card) {
		this.card = card;
		this.columnId = columnId;

		setId(card.getId());
		getStyle().set("box-shadow", "0.5px solid black");
		setSpacing(true);
		setPadding(true);
		setMargin(false);
		label = new Label();
		label.getStyle().set("word-break", "break-word");
		changeText(card.getText());
		label.setWidthFull();
		add(label);

		VerticalLayout btnLayout = new VerticalLayout();
		btnLayout.setWidth("unset");
		btnLayout.setSpacing(false);
		btnLayout.setMargin(false);
		btnLayout.setPadding(false);
		add(btnLayout);

		likeComponent = new LikeComponent(view, view.getId().get(), card.getId(), card.countAllLikes());
		btnLayout.add(likeComponent);

		if (KBViewUtils.isAllowed(view.getOptions(), card.getOwnerId())) {
			Button btnEdit = new Button(VaadinIcon.EDIT.create());
			btnEdit.addClickListener(e -> {
				new ChangeTextDialog("Edit Text", label.getText(), savedText -> {
					log.info("edit card: " + getId().get());
					TKBCard c = cardRepository.findById(getId().get()).get();
					c.setText(savedText);
					cardRepository.save(c);
					BroadcasterCards.broadcast(getId().get(), "update");
				}).open();
			});
			btnLayout.add(btnEdit);

			Button btnDelete = new Button(VaadinIcon.TRASH.create());
			btnDelete.addClickListener(e -> {
				log.info("delete card: " + getId().get());
				TKBColumn c = columnRepository.findById(columnId).get();
				c.removeCardById(getId().get());
				columnRepository.save(c);
				BroadcasterColumns.broadcast(column.getId().get(), "update");
			});
			btnLayout.add(btnDelete);
		}

		setFlexGrow(1, label);
		setWidthFull();
		getStyle().set("border-radius", "10px");
		getStyle().set("border", "1px solid var(--material-disabled-text-color)");
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
//		super.onAttach(attachEvent);
		UI ui = UI.getCurrent();
		broadcasterRegistration = BroadcasterCards.register(getId().get(), event -> {
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

	private void changeText(String text) {
		if (!label.getText().equals(text)) {
			label.setText(card.getText());
		}

		if (Config.DEBUG) {
			label.setText(card.getText() + " (" + card.getDataOrder() + ")");
		}
	}

	public void reload() {
		card = cardRepository.findById(getId().get()).get();

		changeText(card.getText());

		likeComponent.reload();
	}

}
