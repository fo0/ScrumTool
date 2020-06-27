package com.fo0.vaadin.scrumtool.ui.broadcast;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.fo0.vaadin.scrumtool.ui.config.Config;
import com.fo0.vaadin.scrumtool.ui.utils.StreamUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vaadin.flow.shared.Registration;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class BroadcasterBoard {

	private static final Executor EXECUTOR = Executors.newCachedThreadPool();
	private static final Map<String, List<Consumer<String>>> LISTENERS = Maps.newLinkedHashMap();

	public static synchronized Registration register(String id, Consumer<String> listener) {
		if (Config.DEBUG) {
			log.info("registering board consumer for: " + id);
		}

		LISTENERS.putIfAbsent(id, Lists.newLinkedList());
		LISTENERS.get(id).add(listener);

		return () -> {
			synchronized (BroadcasterBoard.class) {
				BroadcasterBoard.LISTENERS.remove(id);
			}
		};
	}

	public static synchronized void broadcast(String id, String message) {
		StreamUtils.parallelStream(BroadcasterBoard.LISTENERS.get(id)).forEach(e -> {
			if (Config.DEBUG) {
				log.info("broadcast message '{}' to id '{}'", message, id);
			}
			EXECUTOR.execute(() -> e.accept(message));
		});
	}

}
