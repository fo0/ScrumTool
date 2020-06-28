package com.fo0.vaadin.scrumtool.ui.data.listeners;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import com.fo0.vaadin.scrumtool.ui.data.table.TKBData;

import lombok.extern.log4j.Log4j2;

/**
 * to audit anything to just need to add <br>
 * @EntityListener(KBDataListener.class) <br>
 * to the related entity
 * 
 * @author max
 *
 */
@Log4j2
public class KBDataListener {

	@PreRemove
	@PreUpdate
	@PrePersist
	public void onSave(TKBData o) {
		log.info("interceptor: " + o);
	}

}
