package com.fo0.vaadin.scrumretroboard.data.table;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.apache.commons.collections4.CollectionUtils;

import com.google.common.collect.Sets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(of = { "id" })
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ProjectDataColumn implements Serializable {

	private static final long serialVersionUID = 5307688703528077543L;

	@Id
	@Builder.Default
	private String id = UUID.randomUUID().toString();

	private String name;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@Builder.Default
	private Set<ProjectDataCard> cards = Sets.newHashSet();

	public boolean addCard(ProjectDataCard note) {
		return cards.add(note);
	}

	public boolean removeCard(ProjectDataCard note) {
		return cards.remove(note);
	}

	public ProjectDataCard getCardById(String id) {
		if (CollectionUtils.isEmpty(cards)) {
			return null;
		}

		return cards.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
	}
}
