package com.fo0.vaadin.scrumtool.ui.data.table;

import com.fo0.vaadin.scrumtool.ui.utils.Utils;

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
public class TKBCardLikes {

	/**
	 * ID = OwnerId
	 */
	@Builder.Default
	private String id = Utils.randomId();

	private String cardId;
	
	private String ownerId;
	
	private String value;

	@Builder.Default
	private int likeValue = 0;

}
