package com.fo0.vaadin.scrumtool.ui.views.components.card;

import java.util.Set;

import com.fo0.vaadin.scrumtool.ui.data.repository.KBCardRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBColumnRepository;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCardComment;
import com.fo0.vaadin.scrumtool.ui.model.VotingData;
import com.fo0.vaadin.scrumtool.ui.model.VotingItem;
import com.fo0.vaadin.scrumtool.ui.utils.SpringContext;
import com.fo0.vaadin.scrumtool.ui.views.KanbanView;
import com.fo0.vaadin.scrumtool.ui.views.components.ToolTip;
import com.fo0.vaadin.scrumtool.ui.views.components.column.ColumnComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import lombok.Getter;

public class VotingCardType implements ICardTypeTemplate {

	private KBCardRepository cardRepository = SpringContext.getBean(KBCardRepository.class);
	private KBColumnRepository columnRepository = SpringContext.getBean(KBColumnRepository.class);

	private CardComponent root;

	@Getter
	private TKBCard card;

	private ColumnComponent column;
	private String columnId;
	private String cardId;
	private Label label;

	private Button btnComment;
	private KanbanView view;

	private VotingData data;

	public VotingCardType(CardComponent root) {
		this.root = root;
		this.card = root.getCard();
		this.view = root.getView();
		this.column = root.getColumn();
		this.data = card.getByType(VotingData.class);

		VerticalLayout rootLayout = new VerticalLayout();
		addStyles(root, rootLayout);

		label = new Label(data.getText());
		HorizontalLayout layoutTitle = new HorizontalLayout(label);
		layoutTitle.setPadding(true);
		layoutTitle.getStyle().set("box-shadow", "black 0px 2px 10px 3px");
		layoutTitle.setWidthFull();
		rootLayout.add(layoutTitle);

		data.getItems().stream().forEachOrdered(e -> {
			rootLayout.add(addItem(e));
		});
	}

	public VerticalLayout addItem(VotingItem item) {
		VerticalLayout layout = new VerticalLayout();
		layout.getStyle().set("border", "0.5px solid black");
		layout.setWidthFull();
		layout.add(new Label(item.getText()));
		layout.setPadding(false);
		layout.setMargin(false);

		HorizontalLayout l = new HorizontalLayout();
		l.setWidthFull();

		Button btnLike = new Button(VaadinIcon.THUMBS_UP_O.create());
		ToolTip.add(btnLike, "Like the card");
		btnLike.setText(String.valueOf(-1));
		btnLike.setWidthFull();
		btnLike.addClickListener(e -> {

		});
		l.add(btnLike);

		Button btnRemoveLike = new Button(VaadinIcon.THUMBS_DOWN_O.create());
		ToolTip.add(btnRemoveLike, "Remove your like");
		btnRemoveLike.setText(String.valueOf(-1));
		btnRemoveLike.setWidthFull();
		btnRemoveLike.addClickListener(e -> {

		});
		l.add(btnRemoveLike);

		layout.add(l);

		return layout;
	}

	private void addStyles(CardComponent root, VerticalLayout rootLayout) {
		rootLayout.setWidthFull();
		rootLayout.setSpacing(true);
		rootLayout.setMargin(false);
		rootLayout.setPadding(false);
		root.add(rootLayout);

		root.setWidthFull();
		root.getStyle().set("box-shadow", "0.5px solid black");
		root.getStyle().set("border-radius", "1em");
		root.getStyle().set("border", "1px solid var(--material-disabled-text-color)");
		root.addClassName("card-hover");
	}

	@Override
	public void reload() {
		card = cardRepository.findById(root.id()).get();
		changeText(card.getText());
		changeButtonCommentsCaption(card.getComments());
	}

	@Override
	public void changeText(String text) {
		// TODO Auto-generated method stub

	}

	@Override
	public void changeButtonCommentsCaption(Set<TKBCardComment> set) {
		// TODO Auto-generated method stub

	}

}
