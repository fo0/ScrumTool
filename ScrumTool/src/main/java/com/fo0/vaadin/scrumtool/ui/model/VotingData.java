package com.fo0.vaadin.scrumtool.ui.model;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VotingData {

	private String text;
	
	@Builder.Default
	private List<VotingItem> items = Lists.newArrayList();
	
}
