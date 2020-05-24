package com.fo0.vaadin.scrumtool.broadcast;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.fo0.vaadin.scrumtool.config.Config;
import com.fo0.vaadin.scrumtool.utils.StreamUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vaadin.flow.shared.Registration;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class BroadcasterBoard {

	static Executor executor = Executors.newSingleThreadExecutor();

	private static final Map<String, List<Consumer<String>>> listeners = Maps.newLinkedHashMap();

	public static synchronized Registration register(String id, Consumer<String> listener) {
		if (Config.DEBUG) {
			log.info("registering board consumer for: " + id);
		}

		if (BroadcasterBoard.listeners.get(id) == null) {
			BroadcasterBoard.listeners.put(id, Lists.newLinkedList());
		}

		BroadcasterBoard.listeners.get(id).add(listener);

		return () -> {
			synchronized (BroadcasterBoard.class) {
				BroadcasterBoard.listeners.remove(id);
			}
		};
	}

	public static synchronized void broadcast(String id, String message) {
		StreamUtils.parallelStream(BroadcasterBoard.listeners.get(id)).forEach(e -> {
			if (Config.DEBUG) {
				log.info("broadcast message '{}' to id '{}'", message, id);
			}
			executor.execute(() -> e.accept(message));
		});
	}

}
