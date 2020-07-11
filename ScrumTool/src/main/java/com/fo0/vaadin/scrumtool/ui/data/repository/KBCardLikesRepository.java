package com.fo0.vaadin.scrumtool.ui.data.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fo0.vaadin.scrumtool.ui.data.table.TKBCardLikes;

@Repository
public interface KBCardLikesRepository extends CrudRepository<TKBCardLikes, String> {

	@Query("SELECT COALESCE(SUM(l.likeValue), 0) FROM TKBData d RIGHT JOIN d.columns c RIGHT JOIN c.cards a RIGHT JOIN a.likes l WHERE d.id = :dataId AND l.ownerId = :ownerId")
	int countLikesInDataByOwner(@Param("dataId") String dataId, @Param("ownerId") String ownerId);
	
}


