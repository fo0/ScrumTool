package com.fo0.vaadin.scrumtool.ui.model;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fo0.vaadin.scrumtool.ui.utils.Utils;
import com.google.common.collect.Lists;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = { "id" })
public class VotingData {

	@Builder.Default
	private String id = Utils.randomId();

	private String text;

	@Builder.Default
	private List<VotingItem> items = Lists.newArrayList();

	public VotingItem getItemById(String id) {
		return items.stream().filter(e -> StringUtils.equals(e.getId(), id)).findFirst().orElse(null);
	}

}
