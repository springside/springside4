package org.springside.examples.showcase.repository;

import java.util.List;
import java.util.Map;

import org.springside.examples.showcase.entity.Team;
import org.springside.examples.showcase.entity.User;

public interface AccountMybatisDao {

	public Team getTeamDetail(Long id);

	public List<User> searchUser(Map<String, Object> parameters);

	public Long saveUser(User user);

	public void deleteUser(Long id);

}
