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
public class BroadcasterBoardTimer {

	private static final Executor EXECUTOR = Executors.newCachedThreadPool();
	private static final Map<String, List<Consumer<String>>> LISTENERS = Maps.newLinkedHashMap();

	public static synchronized Registration register(String id, Consumer<String> listener) {
		if (Config.DEBUG) {
			log.info("registering board timer consumer for: " + id);
		}

		LISTENERS.putIfAbsent(id, Lists.newLinkedList());
		LISTENERS.get(id).add(listener);

		return () -> {
			synchronized (BroadcasterBoardTimer.class) {
				BroadcasterBoardTimer.LISTENERS.remove(id);
			}
		};
	}

	public static synchronized void broadcast(String id, String message) {
		BroadcasterUtils.runParallel(EXECUTOR, LISTENERS, id, message);
	}

}
