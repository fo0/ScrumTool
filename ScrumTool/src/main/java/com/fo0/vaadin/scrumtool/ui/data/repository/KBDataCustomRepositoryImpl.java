package com.fo0.vaadin.scrumtool.ui.data.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fo0.vaadin.scrumtool.ui.data.table.TKBData;

import lombok.extern.log4j.Log4j2;

@Service
@Transactional
@Log4j2
public class KBDataCustomRepositoryImpl implements KBDataCustomRepository {

	@PersistenceContext
	private EntityManager em;

	public TKBData data(String name) {
		String q = String.format("select d from TKBData d LEFT JOIN d.columns c WHERE c.name = '%s'", name);
		log.info("Query: " + q);
		return em.createQuery(q, TKBData.class).getSingleResult();
	}
}
