package com.fo0.vaadin.scrumtool.ui.data.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fo0.vaadin.scrumtool.ui.data.table.TKBUser;

@Repository
public interface KBUserRepository extends CrudRepository<TKBUser, String>, CustomKBUserRepository {

	@Query("SELECT u FROM TKBData d RIGHT JOIN d.user u WHERE d.id = :id")
	public TKBUser findByDataIdFetched(@Param("id") String dataId);

}
