package com.fo0.vaadin.scrumtool.data.table;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.google.common.collect.Sets;

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

	private String ownerId;

	private String text;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@Builder.Default
	private Set<Likes> likes = Sets.newHashSet();

	/**
	 * 
	 * @return new like value
	 */
	public int doLike(String ownerId) {
		if (likes.stream().anyMatch(e -> e.getId().equals(ownerId))) {
			return countAllLikes();
		}

		likes.add(Likes.builder().id(ownerId).build());

		return countAllLikes();
	}

	public int countAllLikes() {
		return likes.stream().mapToInt(Likes::getLikeValue).sum();
	}
}
