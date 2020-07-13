package com.fo0.vaadin.scrumtool.ui.data.table;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import com.fo0.vaadin.scrumtool.ui.data.converter.ListUserDbConverter;
import com.fo0.vaadin.scrumtool.ui.data.utils.IDataId;
import com.fo0.vaadin.scrumtool.ui.model.User;
import com.google.common.collect.Lists;

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
public class TKBUser implements IDataId, Serializable {

	private static final long serialVersionUID = -3903314185609358126L;

	@Id
	@Builder.Default
	private String id = UUID.randomUUID().toString();

	@Lob
	@Convert(converter = ListUserDbConverter.class)
	private List<User> users;

	
	public List<User> getUsers() {
		if(users == null) {
			users = Lists.newArrayList();
		}
		
		return users;
	}
}
