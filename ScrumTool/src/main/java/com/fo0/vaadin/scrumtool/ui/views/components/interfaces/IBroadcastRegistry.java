package com.fo0.vaadin.scrumtool.ui.views.components.interfaces;

import java.util.Map;

import com.google.common.collect.Maps;
import com.vaadin.flow.shared.Registration;

public interface IBroadcastRegistry {

	public Map<String, Registration> broadcast = Maps.newConcurrentMap();

	public default void registerBroadcast(String name, Registration register) {
		broadcast.put(name, register);
	};
	
	public default Registration getBroadcaster(String name) {
		return broadcast.get(name);
	}
	
	public default void unRegisterBroadcasters() {
		broadcast.entrySet().stream().forEach(e -> e.getValue().remove());
	}
	
}
