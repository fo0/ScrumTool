package com.fo0.vaadin.scrumtool.ui.views.components.interfaces;

import com.vaadin.flow.component.Component;

public interface IComponent {

	public default String id() {
		return ((Component) (this)).getId().get();
	}

}
