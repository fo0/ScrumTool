package com.fo0.vaadin.scrumtool.ui.broadcast;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.fo0.vaadin.scrumtool.ui.config.Config;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vaadin.flow.shared.Registration;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class BroadcasterColumn {

	public static final String MESSAGE_SORT = "sort";
	public static final String ADD_COLUMN = "addcolumn.";

	private static final Executor EXECUTOR = Executors.newCachedThreadPool();
	private static final Map<String, List<Consumer<String>>> LISTENERS = Maps.newLinkedHashMap();

	public static synchronized Registration register(String id, Consumer<String> listener) {
		if (Config.DEBUG) {
			log.info("registering column consumer for: " + id);
		}

		LISTENERS.putIfAbsent(id, Lists.newLinkedList());
		LISTENERS.get(id).add(listener);

		return () -> {
			BroadcasterUtils.removeBroadcaster(LISTENERS, id);
		};
	}

	public static synchronized void broadcast(String id, String message) {
		BroadcasterUtils.runParallel(EXECUTOR, LISTENERS, id, message);
	}

	public static synchronized void broadcastAddColumn(String id, String message) {
		BroadcasterUtils.runParallel(EXECUTOR, LISTENERS, id, ADD_COLUMN + message);
	}

}
