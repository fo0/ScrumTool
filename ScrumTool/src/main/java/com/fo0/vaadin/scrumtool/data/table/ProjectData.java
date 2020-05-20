package com.fo0.vaadin.scrumtool.data.table;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.apache.commons.collections4.CollectionUtils;

import com.fo0.vaadin.scrumtool.data.utils.IDataId;
import com.google.common.collect.Sets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@EqualsAndHashCode(of = { "id" })
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ProjectData implements IDataId, Serializable {

	private static final long serialVersionUID = 3523289407526253761L;

	@Id
	@Builder.Default
	private String id = UUID.randomUUID().toString();

	private String ownerId;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@Builder.Default
	private Set<ProjectDataColumn> columns = Sets.newHashSet();

	public ProjectDataColumn getColumnById(@NonNull ProjectDataColumn projectDataColumn) {
		return getColumnById(projectDataColumn.getId());
	}

	public ProjectDataColumn getColumnById(String id) {
		return columns.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
	}

	public ProjectData addColumn(@NonNull ProjectDataColumn column) {
		columns.add(column);
		return this;
	}

	public ProjectData addCard(String columnId, ProjectDataCard note) {
		ProjectDataColumn pdc = getColumnById(columnId);
		if (pdc == null) {
			return this;
		}

		pdc.addCard(note);
		return this;
	}

	public ProjectData removeCardById(String columnId, String cardId) {
		if (CollectionUtils.isEmpty(columns)) {
			return this;
		}

		ProjectDataColumn pdc = getColumnById(columnId);
		if (pdc == null) {
			return this;
		}

		pdc.removeCardById(cardId);
		return this;
	}

	public boolean removeColumn(ProjectDataColumn column) {
		return columns.remove(column);
	}

	public ProjectData removeColumnById(String id) {
		if (CollectionUtils.isEmpty(columns)) {
			return this;
		}

		columns.removeIf(e -> e.getId().equals(id));
		return this;
	}

}
