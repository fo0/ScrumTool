package com.fo0.vaadin.scrumtool.ui.views.components.card;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.fo0.vaadin.scrumtool.ui.broadcast.BroadcasterCard;
import com.fo0.vaadin.scrumtool.ui.config.Config;
import com.fo0.vaadin.scrumtool.ui.data.interfaces.IDataOrder;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBCardRepository;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCardComment;
import com.fo0.vaadin.scrumtool.ui.model.TextItem;
import com.fo0.vaadin.scrumtool.ui.utils.SpringContext;
import com.fo0.vaadin.scrumtool.ui.views.KanbanView;
import com.fo0.vaadin.scrumtool.ui.views.components.ToolTip;
import com.fo0.vaadin.scrumtool.ui.views.components.column.ColumnComponent;
import com.fo0.vaadin.scrumtool.ui.views.components.like.TextCardLikeComponent;
import com.fo0.vaadin.scrumtool.ui.views.dialogs.CommentDialog;
import com.fo0.vaadin.scrumtool.ui.views.dialogs.TextAreaDialog;
import com.fo0.vaadin.scrumtool.ui.views.utils.KBViewUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TextCardType implements ICardTypeTemplate<TKBCard> {

	private KBCardRepository cardRepository = SpringContext.getBean(KBCardRepository.class);

	private CardComponent root;

	@Getter
	private TKBCard card;

	private ColumnComponent column;
	private TextCardLikeComponent likeComponent;
	private String columnId;
	private String cardId;
	private Label label;

	private Button btnComment;
	private KanbanView view;

	public TextCardType(CardComponent root) {
		this.root = root;
		this.card = root.getCard();
		this.view = root.getView();
		this.column = root.getColumn();
		this.cardId = root.getCardId();

		label = new Label();
		label.getStyle().set("word-break", "break-word");
		changeText(card.getText());
		label.setWidthFull();
		root.add(label);

		VerticalLayout btnLayout = new VerticalLayout();
		btnLayout.setWidth("unset");
		btnLayout.setSpacing(false);
		btnLayout.setMargin(false);
		btnLayout.setPadding(false);
		root.add(btnLayout);

		likeComponent = new TextCardLikeComponent(view, view.getId().get(), card.getId());
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
			btnDelete.addClickListener(e -> root.deleteCard());
			btnLayout.add(btnDelete);
		}

		root.setFlexGrow(1, label);
		root.setWidthFull();
		root.getStyle().set("box-shadow", "0.5px solid black");
		root.getStyle().set("border-radius", "1em");
		root.getStyle().set("border", "1px solid var(--material-disabled-text-color)");
		root.addClassName("card-hover");

		label.getElement().addEventListener("click", e -> {
			new TextAreaDialog("Edit Text", label.getText(), savedText -> {
				log.info("edit card: " + root.id());
				TKBCard c = cardRepository.findById(cardId).get();
				TextItem item = c.getByType(TextItem.class).get();
				item.setText(savedText);
				c.setTextByType(item);
				cardRepository.save(c);
				BroadcasterCard.broadcast(cardId, "update");
			}).open();
		});

		Icon editIcon = VaadinIcon.EDIT.create();
		root.add(editIcon);
	}

	

	@Override
	public void changeText(String text) {
		if (!label.getText().equals(text)) {
			label.setText(text);
		}

		if (Config.DEBUG) {
			label.setText(text + " (" + card.getDataOrder() + ")");
		}
	}

	@Override
	public void changeButtonCommentsCaption(Set<TKBCardComment> set) {
		if (CollectionUtils.size(set) > 0) {
			btnComment.setText(String.valueOf(set.size()));
			btnComment.setIcon(VaadinIcon.COMMENT.create());

			//@formatter:off
			ToolTip.addLines(btnComment, set.stream()
					.sorted(Comparator.comparing(IDataOrder::getDataOrder).reversed())
					.map(TKBCardComment::getText)
					.collect(Collectors.toList()));
			//@formatter:on
		} else {
			btnComment.setIcon(VaadinIcon.COMMENT_O.create());
			ToolTip.add(btnComment, "Comment the Card");
		}
	}
	
	@Override
	public void reload() {
		card = cardRepository.findById(root.id()).get();
		reload(card);
	}

	@Override
	public void reload(TKBCard data) {
		if (!label.getText().equals(card.getByType(TextItem.class).get().getText()))
			changeText(data.getByType(TextItem.class).get().getText());

		likeComponent.reload();

		changeButtonCommentsCaption(data.getComments());
	}

}
