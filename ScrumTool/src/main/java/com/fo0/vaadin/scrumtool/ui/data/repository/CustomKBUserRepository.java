package com.fo0.vaadin.scrumtool.ui.data.repository;

import com.fo0.vaadin.scrumtool.ui.data.table.TKBUser;
import com.fo0.vaadin.scrumtool.ui.model.User;

public interface CustomKBUserRepository {

	TKBUser insertUserById(String dataUserId, User user);

	TKBUser deleteUserById(String dataUserId, String userId);
	
	int countByDataIdFetched(String dataId);

}