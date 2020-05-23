package com.fo0.vaadin.scrumtool.data.repository;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class KBDataListener {

	@PreRemove
	@PreUpdate
	@PrePersist
	public void onSave(Object o) {
		log.info("interceptor: " + o.toString());
	}

}
