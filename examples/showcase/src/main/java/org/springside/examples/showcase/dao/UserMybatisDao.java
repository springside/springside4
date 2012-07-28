package org.springside.examples.showcase.dao;

import java.util.List;
import java.util.Map;

import org.springside.examples.showcase.entity.Project;
import org.springside.examples.showcase.entity.User;

public interface UserMybatisDao {

	public Project getProjectDetail(Long id);

	public List<User> searchUser(Map<String, Object> parameters);

	public Long saveUser(User user);

	public void deleteUser(Long id);

}
