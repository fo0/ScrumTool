package com.fo0.vaadin.scrumtool.ui.data.converter;

import java.util.List;

import javax.persistence.AttributeConverter;

import com.fo0.vaadin.scrumtool.ui.model.User;
import com.google.gson.Gson;
import com.googlecode.gentyref.TypeToken;

public class ListUserDbConverter implements AttributeConverter<List<User>, String> {

	@Override
	public String convertToDatabaseColumn(List<User> obj) {
		return new Gson().toJson(obj); 
	}

	@Override
	public List<User> convertToEntityAttribute(String json) {
		return new Gson().fromJson(json, new TypeToken<List<User>>() {}.getType());
	}

}
