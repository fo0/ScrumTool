package com.fo0.vaadin.scrumtool.ui.views.components.card;

import java.util.Set;

import com.fo0.vaadin.scrumtool.ui.data.table.TKBCardComment;

public interface ICardTypeTemplate {

	public void reload();
	
	public void changeText(String text);
	
	public void changeButtonCommentsCaption(Set<TKBCardComment> set);
	
}
