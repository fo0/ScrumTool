package com.fo0.vaadin.scrumtool.broadcast;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.fo0.vaadin.scrumtool.config.Config;
import com.fo0.vaadin.scrumtool.data.utils.ComponentSyncData;
import com.fo0.vaadin.scrumtool.data.utils.ComponentSyncRegisterData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.shared.Registration;

import lombok.extern.log4j.Log4j2;

/**
 * 
 * @created 13.06.2020 - 22:51:08
 * @author KaesDingeling
 * @version 0.1
 */
@Log4j2
public class BroadcasterBoardComponent {

	private static final Executor EXECUTOR = Executors.newCachedThreadPool();
	private static final Map<ComponentSyncRegisterData, List<Consumer<ComponentSyncData>>> LISTENERS = Maps.newLinkedHashMap();
	
	/**
	 * 
	 * @param syncId
	 * @param timerComponent
	 * @param listener
	 * @return
	 * @Created 13.06.2020 - 22:51:06
	 * @author KaesDingeling
	 */
	public static synchronized Registration register(String syncId, Component component, Consumer<ComponentSyncData> listener) {
		return register(ComponentSyncRegisterData.builder()
				.syncId(syncId)
				.component(component)
				.build(), listener);
	}

	/**
	 * 
	 * @param registerData
	 * @param listener
	 * @return
	 * @Created 13.06.2020 - 22:51:04
	 * @author KaesDingeling
	 */
	public static synchronized Registration register(ComponentSyncRegisterData registerData, Consumer<ComponentSyncData> listener) {
		if (Config.DEBUG) {
			log.info("registering board timer consumer for: " + registerData.getSyncId());
		}

		LISTENERS.putIfAbsent(registerData, Lists.newLinkedList());
		LISTENERS.get(registerData).add(listener);

		return () -> {
			synchronized (BroadcasterBoardComponent.class) {
				BroadcasterBoardComponent.LISTENERS.remove(registerData);
			}
		};
	}
	
	/**
	 * 
	 * @param syncId
	 * @param timerComponent
	 * @param syncData
	 * @Created 13.06.2020 - 22:49:59
	 * @author KaesDingeling
	 */
	public static synchronized void broadcast(String syncId, Component component, ComponentSyncData syncData) {
		broadcast(ComponentSyncRegisterData.builder()
				.syncId(syncId)
				.component(component)
				.build(), syncData);
	}
	
	/**
	 * 
	 * @param syncId
	 * @param syncData
	 * @Created 13.06.2020 - 23:26:51
	 * @author KaesDingeling
	 */
	public static synchronized void broadcast(String syncId, ComponentSyncData syncData) {
		broadcast(ComponentSyncRegisterData.builder()
				.syncId(syncId)
				.component(syncData.getComponent())
				.build(), syncData);
	}

	/**
	 * 
	 * @param registerData
	 * @param syncData
	 * @Created 13.06.2020 - 22:48:31
	 * @author KaesDingeling
	 */
	public static synchronized void broadcast(ComponentSyncRegisterData registerData, ComponentSyncData syncData) {
		BroadcasterBoardComponent.LISTENERS.entrySet().parallelStream().forEach(entry -> {
			if (entry.getKey().getComponent() != registerData.getComponent()) {
				if (Config.DEBUG) {
					log.info("broadcast message '{}' to id '{}'", syncData.getAction(), registerData.getSyncId());
				}
				
				entry.getValue().parallelStream().forEach(consumer -> EXECUTOR.execute(() -> consumer.accept(syncData)));
			}
		});
	}

}
