package com.fo0.vaadin.scrumtool.ui.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fo0.vaadin.scrumtool.ui.data.table.TKBOptions;

@Repository
public interface KBOptionRepository extends CrudRepository<TKBOptions, String> {

}
