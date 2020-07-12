package com.fo0.vaadin.scrumtool.ui.data.repository;

import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.fo0.vaadin.scrumtool.ui.data.table.TKBUser;
import com.fo0.vaadin.scrumtool.ui.model.User;

public class CustomKBUserRepositoryImpl implements CustomKBUserRepository {

	@Autowired
	@Lazy
	private KBUserRepository repository;

	@Override
	public TKBUser insertUserById(String dataUserId, User user) {
		Optional<TKBUser> data = repository.findById(dataUserId);
		if (data.isPresent()) {
			if (!data.get().getUsers().contains(user)) {
				data.get().getUsers().add(user);
				return repository.save(data.get());
			}
		}
		
		return null;
	}

	@Override
	public TKBUser deleteUserById(String dataUserId, String userId) {
		Optional<TKBUser> data = repository.findById(dataUserId);
		if (data.isPresent()) {
			data.get().getUsers().removeIf(e -> StringUtils.equals(e.getId(), userId));
			return repository.save(data.get());
		}

		return null;
	}

	@Override
	public int countByDataIdFetched(String dataId) {
		return CollectionUtils.size(repository.findByDataIdFetched(dataId).getUsers());
	}

}
