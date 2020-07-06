package com.fo0.vaadin.scrumtool.ui.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fo0.vaadin.scrumtool.ui.data.table.TKBCardComment;

@Repository
public interface KBCardCommentRepository extends CrudRepository<TKBCardComment, String> {

}


