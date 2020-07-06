package com.fo0.vaadin.scrumtool.ui.data.table;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fo0.vaadin.scrumtool.ui.data.utils.IDataId;

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
public class TKBOptions implements IDataId, Serializable {

	private static final long serialVersionUID = 3951230846648440224L;

	@Id
	@Builder.Default
	private String id = UUID.randomUUID().toString();

	private boolean optionPermissionSystem;
	private int maxColumns;
	private int maxCards;
	private int maxCardTextLength;
	private int maxLikesPerUser;

	@Builder.Default
	private int maxLikesPerUserPerCard = 1;

	// DESC = latest ON TOP | ASC = oldest on top
	@Builder.Default
	private boolean cardSortDirectionDesc = true;

	@Builder.Default
	private long timerInMillis = TimeUnit.MINUTES.toMillis(3);

}
