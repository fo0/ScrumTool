package com.fo0.vaadin.scrumtool.ui.data.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.fo0.vaadin.scrumtool.ui.data.table.TKBData;

@Service
public interface KBDataRepository extends CrudRepository<TKBData, String>, KBDataCustomRepository {

	@Query("select d from TKBData d LEFT JOIN d.columns c WHERE c.name = :name")
	public TKBData filterByColumn(@Param("name") String name);

}
