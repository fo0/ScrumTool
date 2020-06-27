package com.fo0.vaadin.scrumtool.ui.data.table;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class TKBOptions implements Serializable {

	private static final long serialVersionUID = 3951230846648440224L;
	
	private boolean optionPermissionSystem;
	private int maxColumns;
	private int maxCards;
	private int maxCardTextLength;
	private int maxLikesPerUser;
	
	@Builder.Default
	private int maxLikesPerUserPerCard = 1;
	
	// DESC = latest ON TOP | ASC = oldest on top
	public boolean cardSortDirectionDesc = true;
	
}

