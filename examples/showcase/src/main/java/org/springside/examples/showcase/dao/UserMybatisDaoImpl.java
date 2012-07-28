package org.springside.examples.showcase.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springside.examples.showcase.entity.Project;
import org.springside.examples.showcase.entity.User;

@Component
public class UserMybatisDaoImpl implements UserMybatisDao {

	@Override
	public Project getProjectDetail(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> searchUser(Map<String, Object> parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long saveUser(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteUser(Long id) {
		// TODO Auto-generated method stub

	}

}
