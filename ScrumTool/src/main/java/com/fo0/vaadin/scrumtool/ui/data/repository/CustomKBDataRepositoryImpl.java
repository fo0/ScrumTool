package com.fo0.vaadin.scrumtool.ui.data.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.fo0.vaadin.scrumtool.ui.data.table.TKBColumn;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBData;

public class CustomKBDataRepositoryImpl implements CustomKBDataRepository {

	@Autowired
	private KBDataRepository repository;
	
	@Override
	@Transactional
	public TKBData insertColumnById(String dataId, TKBColumn column) {
		TKBData data = repository.findById(dataId).get();
		data.getColumns().add(column);
		return data;
	}
	
}
