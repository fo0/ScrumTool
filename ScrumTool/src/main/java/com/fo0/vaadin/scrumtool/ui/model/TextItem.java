package com.fo0.vaadin.scrumtool.ui.model;

import java.util.List;

import com.fo0.vaadin.scrumtool.ui.data.interfaces.ILike;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCardLikes;
import com.fo0.vaadin.scrumtool.ui.utils.Utils;
import com.google.common.collect.Lists;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TextItem implements ILike {

	@Builder.Default
	private String id = Utils.randomId();

	private String text;

	@Builder.Default
	private List<TKBCardLikes> likes = Lists.newArrayList();

}
