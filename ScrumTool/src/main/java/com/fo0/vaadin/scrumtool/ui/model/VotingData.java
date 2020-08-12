package com.fo0.vaadin.scrumtool.ui.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VotingData {

	private List<VotingItem> items;
	
}
