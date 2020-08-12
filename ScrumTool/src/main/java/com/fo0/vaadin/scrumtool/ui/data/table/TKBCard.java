package com.fo0.vaadin.scrumtool.ui.data.table;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import com.fo0.vaadin.scrumtool.ui.data.enums.ECardType;
import com.fo0.vaadin.scrumtool.ui.data.interfaces.IDataOrder;
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
public class TKBCard implements Serializable, IDataOrder {

	private static final long serialVersionUID = 652620276690725942L;

	@Id
	@Builder.Default
	private String id = UUID.randomUUID().toString();

	private String ownerId;

	@Builder.Default
	private int dataOrder = -1;

	@Builder.Default
	private ECardType type = ECardType.TextCard;
	
	private String text;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "cardId")
	@Builder.Default
	private Set<TKBCardLikes> likes = Sets.newHashSet();
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "cardId")
	@Builder.Default
	private Set<TKBCardComment> comments = Sets.newHashSet();

	public void removeLikeByOwnerId(String ownerId) {
		likes.stream().filter(e -> e.getOwnerId().equals(ownerId)).findFirst().ifPresent(likes::remove);
	}

	public int cardLikesByOwnerId(String ownerId) {
		return likes.stream().filter(e -> e.getOwnerId().equals(ownerId)).mapToInt(TKBCardLikes::getLikeValue).sum();
	}
	
	public int countAllLikes() {
		return likes.stream().mapToInt(TKBCardLikes::getLikeValue).sum();
	}

}
