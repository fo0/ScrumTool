package com.fo0.vaadin.scrumtool.ui.views.components.card;

public interface IComponentUpdate<T> {

	void reload();

	void reload(T data);

}