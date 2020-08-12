package com.fo0.vaadin.scrumtool.ui.views.components.card;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.fo0.vaadin.scrumtool.ui.broadcast.BroadcasterCard;
import com.fo0.vaadin.scrumtool.ui.broadcast.BroadcasterColumn;
import com.fo0.vaadin.scrumtool.ui.config.Config;
import com.fo0.vaadin.scrumtool.ui.data.interfaces.IDataOrder;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBCardRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBColumnRepository;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCardComment;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBColumn;
import com.fo0.vaadin.scrumtool.ui.utils.SpringContext;
import com.fo0.vaadin.scrumtool.ui.views.KanbanView;
import com.fo0.vaadin.scrumtool.ui.views.components.ToolTip;
import com.fo0.vaadin.scrumtool.ui.views.components.column.ColumnComponent;
import com.fo0.vaadin.scrumtool.ui.views.components.interfaces.IComponent;
import com.fo0.vaadin.scrumtool.ui.views.components.like.LikeComponent;
import com.fo0.vaadin.scrumtool.ui.views.dialogs.CommentDialog;
import com.fo0.vaadin.scrumtool.ui.views.dialogs.TextDialog;
import com.fo0.vaadin.scrumtool.ui.views.utils.KBViewUtils;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@CssImport(value = "./styles/card-style.css", themeFor = "vaadin-horizontal-layout")
public class CardComponent extends HorizontalLayout implements IComponent {

	private static final long serialVersionUID = -1213748155629932731L;

	private KBCardRepository cardRepository = SpringContext.getBean(KBCardRepository.class);
	private KBColumnRepository columnRepository = SpringContext.getBean(KBColumnRepository.class);
	@Getter
	private TKBCard card;
	private ColumnComponent column;
	private LikeComponent likeComponent;
	private String columnId;
	private String cardId;
	private Label label;
	private Registration broadcasterRegistration;

	private Button btnComment;

	public CardComponent(KanbanView view, ColumnComponent column, String columnId, TKBCard card) {
		this.card = card;
		this.columnId = columnId;
		this.column = column;

		setId(card.getId());
		cardId = getId().get();
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

		btnComment = new Button(VaadinIcon.COMMENT_O.create());
		changeButtonCommentsCaption(card.getComments());
		btnComment.addClickListener(e -> {
			new CommentDialog(cardId, label.getText()).open();
		});
		btnLayout.add(btnComment);

		if (KBViewUtils.isAllowed(view.getOptions(), card.getOwnerId())) {
			Button btnDelete = new Button(VaadinIcon.TRASH.create());
			ToolTip.add(btnDelete, "Delete the card");
			btnDelete.addClickListener(e -> deleteCard());
			btnLayout.add(btnDelete);
		}

		setFlexGrow(1, label);
		setWidthFull();
		getStyle().set("box-shadow", "0.5px solid black");
		getStyle().set("border-radius", "1em");
		getStyle().set("border", "1px solid var(--material-disabled-text-color)");
		addClassName("card-hover");

		label.getElement().addEventListener("click", e -> {
			new TextDialog("Edit Text", label.getText(), savedText -> {
				log.info("edit card: " + getId().get());
				TKBCard c = cardRepository.findById(cardId).get();
				c.setText(savedText);
				cardRepository.save(c);
				BroadcasterCard.broadcast(cardId, "update");
			}).open();
		});

		Icon editIcon = VaadinIcon.EDIT.create();
		add(editIcon);
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

	private void changeText(String text) {
		if (!label.getText().equals(text)) {
			label.setText(text);
		}

		if (Config.DEBUG) {
			label.setText(text + " (" + card.getDataOrder() + ")");
		}
	}

	public void reload() {
		card = cardRepository.findById(getId().get()).get();

		changeText(card.getText());

		likeComponent.reload();
		
		changeButtonCommentsCaption(card.getComments());
	}

	public void changeButtonCommentsCaption(Set<TKBCardComment> set) {
		if (CollectionUtils.size(set) > 0) {
			btnComment.setText(String.valueOf(set.size()));
			btnComment.setIcon(VaadinIcon.COMMENT.create());
			ToolTip.addLines(btnComment, set.stream().sorted(Comparator.comparing(IDataOrder::getDataOrder).reversed()).map(TKBCardComment::getText).collect(Collectors.toList()));
		} else {
			btnComment.setIcon(VaadinIcon.COMMENT_O.create());
			ToolTip.add(btnComment, "Comment the Card");
		}
	}

}
