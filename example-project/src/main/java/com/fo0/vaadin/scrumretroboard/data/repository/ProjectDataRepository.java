package com.fo0.vaadin.scrumretroboard.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.fo0.vaadin.scrumretroboard.data.table.ProjectData;

@Service
public interface ProjectDataRepository extends CrudRepository<ProjectData, String> {

}
