package com.fo0.vaadin.scrumtool.ui.views.components.card;

import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBCardRepository;
import com.fo0.vaadin.scrumtool.ui.data.repository.KBColumnRepository;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCard;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCardComment;
import com.fo0.vaadin.scrumtool.ui.model.VotingData;
import com.fo0.vaadin.scrumtool.ui.model.VotingItem;
import com.fo0.vaadin.scrumtool.ui.utils.SpringContext;
import com.fo0.vaadin.scrumtool.ui.views.KanbanView;
import com.fo0.vaadin.scrumtool.ui.views.components.column.ColumnComponent;
import com.fo0.vaadin.scrumtool.ui.views.components.like.VotingCardLikeComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
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
	private VotingCardLikeComponent likeComponent;

	public VotingCardType(CardComponent root) {
		this.root = root;
		this.card = root.getCard();
		this.cardId = card.getId();
		this.view = root.getView();
		this.column = root.getColumn();
		this.data = card.getByType(VotingData.class).get();

		VerticalLayout rootLayout = new VerticalLayout();
		addStyles(root, rootLayout);

		label = new Label(data.getText());
		Icon icn = FontAwesome.Solid.QUESTION.create();
		HorizontalLayout layoutTitle = new HorizontalLayout(icn, label);
		layoutTitle.setJustifyContentMode(JustifyContentMode.START);
		layoutTitle.setPadding(true);
		layoutTitle.getStyle().set("box-shadow", "black 0px 2px 10px 3px");
		layoutTitle.setWidthFull();
		rootLayout.add(layoutTitle);

		if (CollectionUtils.isNotEmpty(data.getItems())) {
			data.getItems().stream().forEachOrdered(e -> {
				rootLayout.add(addItem(e));
			});
		}
	}

	public VerticalLayout addItem(VotingItem item) {
		VerticalLayout layout = new VerticalLayout();
		layout.getStyle().set("border", "0.5px solid black");
		layout.setWidthFull();
		layout.add(new Label(item.getText()));
		layout.setPadding(true);
		layout.setSpacing(false);
		layout.setMargin(false);

		likeComponent = new VotingCardLikeComponent(view, view.getId().get(), card.getId(), item.getId());
		likeComponent.setWidthFull();
		layout.add(likeComponent);

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
		if (likeComponent != null) {
			likeComponent.reload();
		}
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
