package com.fo0.vaadin.scrumtool.ui.model;

import java.util.List;

import com.fo0.vaadin.scrumtool.ui.data.interfaces.ILike;
import com.fo0.vaadin.scrumtool.ui.data.table.TKBCardLikes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeItem implements ILike {

	private String id;
	private List<TKBCardLikes> likes;

}
