package com.fo0.vaadin.scrumtool.ui.data.repository;

import com.fo0.vaadin.scrumtool.ui.data.table.TKBColumn;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KBColumnRepository extends CrudRepository<TKBColumn, String> {

}
