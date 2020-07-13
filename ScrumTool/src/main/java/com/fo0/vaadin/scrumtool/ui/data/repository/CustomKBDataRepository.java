package com.fo0.vaadin.scrumtool.ui.data.repository;

import com.fo0.vaadin.scrumtool.ui.data.table.TKBColumn;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBData;

public interface CustomKBDataRepository {

	TKBData insertColumnById(String dataId, TKBColumn column);

}