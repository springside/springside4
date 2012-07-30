package org.springside.examples.showcase.repository;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springside.examples.showcase.entity.Team;
import org.springside.examples.showcase.entity.User;

@Component
public class AccountMybatisDaoImpl implements AccountMybatisDao {

	@Override
	public Team getTeamDetail(Long id) {
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
