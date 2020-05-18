package com.fo0.vaadin.scrumtool.data.table;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(of = { "id" })
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDataCard implements Serializable {

	private static final long serialVersionUID = 652620276690725942L;

	@Id
	@Builder.Default
	private String id = UUID.randomUUID().toString();

	private String text;

}
