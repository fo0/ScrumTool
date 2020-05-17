package com.fo0.vaadin.projects.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.fo0.vaadin.projects.data.table.ProjectDataColumn;

@Service
public interface ProjectDataColumnRepository extends CrudRepository<ProjectDataColumn, String> {

}
