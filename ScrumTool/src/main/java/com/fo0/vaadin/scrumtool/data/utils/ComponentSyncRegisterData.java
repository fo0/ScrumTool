package com.fo0.vaadin.scrumtool.data.utils;

import com.vaadin.flow.component.Component;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @created 13.06.2020 - 22:41:19
 * @author KaesDingeling
 * @version 0.1
 */
@Data
@Builder
@EqualsAndHashCode(of = { "syncId", "component" })
public class ComponentSyncRegisterData {

	private String syncId;
	private Component component;
}