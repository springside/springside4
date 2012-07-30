package org.springside.examples.showcase.repository.mybatis;

import java.util.List;
import java.util.Map;

import org.springside.examples.showcase.entity.Team;
import org.springside.examples.showcase.entity.User;

public interface AccountDao {

	public Team getTeamWithDetail(Long id);

	public User getUser(Long id);

	public List<User> searchUser(Map<String, Object> parameters);

	public void saveUser(User user);

	public void deleteUser(Long id);

}
