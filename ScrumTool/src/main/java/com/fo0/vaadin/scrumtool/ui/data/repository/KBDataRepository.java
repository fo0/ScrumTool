package com.fo0.vaadin.scrumtool.ui.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fo0.vaadin.scrumtool.ui.data.table.TKBData;

@Repository
public interface KBDataRepository extends CrudRepository<TKBData, String> {

//	@Query("SELECT p FROM TKBData p LEFT JOIN FETCH p.columns c WHERE c.name IN :columns")
//	public List<TKBData> findByColumns(@Param("columns") List<String> columns);

}
