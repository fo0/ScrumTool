package com.fo0.vaadin.scrumtool.ui.data.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;

@Service
@Transactional
@Log4j2
public class KBDataCustomRepositoryImpl implements KBDataCustomRepository {

	@PersistenceContext
	private EntityManager em;
}
