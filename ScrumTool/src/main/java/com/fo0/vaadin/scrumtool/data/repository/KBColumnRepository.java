package com.fo0.vaadin.scrumtool.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.fo0.vaadin.scrumtool.data.table.TKBColumn;

@Service
public interface KBColumnRepository extends CrudRepository<TKBColumn, String> {

}
