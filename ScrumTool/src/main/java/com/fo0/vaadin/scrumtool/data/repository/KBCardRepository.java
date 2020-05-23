package com.fo0.vaadin.scrumtool.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.fo0.vaadin.scrumtool.data.table.TKBCard;

@Service
public interface KBCardRepository extends CrudRepository<TKBCard, String> {

}


