package com.fo0.vaadin.scrumretroboard.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.fo0.vaadin.scrumretroboard.data.table.ProjectDataColumn;

@Service
public interface ProjectDataColumnRepository extends CrudRepository<ProjectDataColumn, String> {

}
