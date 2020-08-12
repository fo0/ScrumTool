package com.fo0.vaadin.scrumtool.ui.views.components.interfaces;

import java.util.List;

import com.google.common.collect.Lists;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasOrderedComponents;

public interface IComponent {

	public default String id() {
		return ((Component) (this)).getId().get();
	}

	public default <T extends Component> List<T> getComponentsByType(HasOrderedComponents<?> layout, Class<T> type) {
		List<T> components = Lists.newArrayList();
		for (int i = 0; i < layout.getComponentCount(); i++) {
			if (layout.getComponentAt(i) instanceof Component) {
				components.add((T) layout.getComponentAt(i));
			}
		}
		return components;
	}

	public default <T extends Component> T getComponentById(HasOrderedComponents<?> layout, Class<T> type, String id) {
		for (int i = 0; i < layout.getComponentCount(); i++) {
			if (layout.getComponentAt(i) instanceof Component) {
				Component card = (Component) layout.getComponentAt(i);
				if (card.getId().get().equals(id)) {
					return (T) card;
				}
			}
		}

		return null;
	}

}
