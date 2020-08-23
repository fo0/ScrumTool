package com.fo0.vaadin.scrumtool.ui.views.components.card;

import java.util.Set;

import com.fo0.vaadin.scrumtool.ui.data.table.TKBCardComment;
import com.fo0.vaadin.scrumtool.ui.views.components.interfaces.IComponent;

public interface ICardTypeTemplate<Update> extends IComponentUpdate<Update>, IComponent {

	public void changeText(String text);

	public void changeButtonCommentsCaption(Set<TKBCardComment> set);

}
