package com.fo0.vaadin.scrumtool.ui.data.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fo0.vaadin.scrumtool.ui.data.table.TKBData;

@Repository
public interface KBDataRepository extends CrudRepository<TKBData, String>, CustomKBDataRepository {

//	@Query("SELECT p FROM TKBData p LEFT JOIN FETCH p.columns c WHERE c.name IN :columns")
//	public List<TKBData> findByColumns(@Param("columns") List<String> columns);

	@Query("FROM TKBData d LEFT JOIN FETCH d.columns c LEFT JOIN FETCH d.user WHERE d.id = :id")
	public TKBData findByIdFetched(@Param("id") String id);

}
