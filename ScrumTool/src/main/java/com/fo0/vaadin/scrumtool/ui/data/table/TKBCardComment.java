package com.fo0.vaadin.scrumtool.ui.data.table;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fo0.vaadin.scrumtool.ui.data.interfaces.IDataOrder;
import com.fo0.vaadin.scrumtool.ui.utils.Utils;

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
public class TKBCardComment implements IDataOrder{

	@Id
	@Builder.Default
	private String id = Utils.randomId();

	@Builder.Default
	private int dataOrder = -1;
	
	private String ownerId;

	private String text;

}
