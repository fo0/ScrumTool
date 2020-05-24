package com.fo0.vaadin.scrumtool.data.table;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
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
public class TKBData implements IDataId, Serializable {

	private static final long serialVersionUID = 3523289407526253761L;

	@Id
	@Builder.Default
	private String id = UUID.randomUUID().toString();

	private String ownerId;

	@Embedded
	private TKBOptions options; 

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@Builder.Default
	private Set<TKBColumn> columns = Sets.newHashSet();

	public TKBColumn getColumnById(@NonNull TKBColumn projectDataColumn) {
		return getColumnById(projectDataColumn.getId());
	}

	public TKBColumn getColumnById(String id) {
		return columns.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
	}

	public TKBData addColumn(@NonNull TKBColumn column) {
		columns.add(column);
		return this;
	}

	public TKBData addCard(String columnId, TKBCard note) {
		TKBColumn pdc = getColumnById(columnId);
		if (pdc == null) {
			return this;
		}

		pdc.addCard(note);
		return this;
	}

	public TKBData removeCardById(String columnId, String cardId) {
		if (CollectionUtils.isEmpty(columns)) {
			return this;
		}

		TKBColumn pdc = getColumnById(columnId);
		if (pdc == null) {
			return this;
		}

		pdc.removeCardById(cardId);
		return this;
	}

	public TKBData likeCard(String columnId, String cardId, String ownerId) {
		if (CollectionUtils.isEmpty(columns)) {
			return this;
		}

		TKBColumn pdc = getColumnById(columnId);
		if (pdc == null) {
			return this;
		}

		pdc.likeCardById(cardId, ownerId);
		return this;
	}

	public boolean removeColumn(TKBColumn column) {
		return columns.remove(column);
	}

	public TKBData removeColumnById(String id) {
		if (CollectionUtils.isEmpty(columns)) {
			return this;
		}

		columns.removeIf(e -> e.getId().equals(id));
		return this;
	}

}
