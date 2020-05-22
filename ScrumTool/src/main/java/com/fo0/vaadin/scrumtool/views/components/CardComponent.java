package com.fo0.vaadin.scrumtool.views.components;

import com.fo0.vaadin.scrumtool.data.repository.ProjectDataCardRepository;
import com.fo0.vaadin.scrumtool.data.repository.ProjectDataColumnRepository;
import com.fo0.vaadin.scrumtool.data.table.ProjectDataCard;
import com.fo0.vaadin.scrumtool.data.table.ProjectDataColumn;
import com.fo0.vaadin.scrumtool.session.SessionUtils;
import com.fo0.vaadin.scrumtool.utils.SpringContext;
import com.fo0.vaadin.scrumtool.views.KanbanView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CardComponent extends HorizontalLayout {

	private static final long serialVersionUID = -1213748155629932731L;

	private ProjectDataCardRepository cardRepository = SpringContext.getBean(ProjectDataCardRepository.class);
	private ProjectDataColumnRepository columnRepository = SpringContext.getBean(ProjectDataColumnRepository.class);

	@Getter
	private ProjectDataCard card;

	private LikeComponent likeComponent;

	private String columnId;

	public CardComponent(KanbanView view, ColumnComponent column, String columnId, ProjectDataCard card) {
		this.card = card;
		this.columnId = columnId;

		setId(card.getId());
		getStyle().set("border", "2px solid black");
		setSpacing(true);
		add(new Label(card.getText()));

		likeComponent = new LikeComponent(card.getId());
		add(likeComponent);

		if (card.getOwnerId().equals(SessionUtils.getSessionId())) {
			Button btnDelete = new Button(VaadinIcon.TRASH.create());
			btnDelete.addClickListener(e -> {
				log.info("delete card: " + getId().get());
				ProjectDataColumn c = columnRepository.findById(columnId).get();
				c.removeCardById(getId().get());
				columnRepository.save(c);
				column.reload();

			});
			add(btnDelete);
		}

		setAlignItems(Alignment.CENTER);
	}

	public void reload() {
		ProjectDataCard tmp = cardRepository.findById(getId().get()).get();
		tmp = cardRepository.save(tmp);

		// update layout with new missing data
		likeComponent.changeText(tmp.countAllLikes());
	}

}
