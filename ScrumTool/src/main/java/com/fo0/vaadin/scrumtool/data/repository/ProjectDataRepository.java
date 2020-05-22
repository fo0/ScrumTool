package com.fo0.vaadin.scrumtool.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.fo0.vaadin.scrumtool.data.table.ProjectData;

@Service
public interface ProjectDataRepository extends CrudRepository<ProjectData, String> {

//	@Query("SELECT f FROM Foo f WHERE LOWER(f.name) = LOWER(:name)")
//	Foo retrieveByName(@Param("name") String name);
	
}
