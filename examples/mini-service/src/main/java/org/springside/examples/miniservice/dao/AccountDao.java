package org.springside.examples.miniservice.dao;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Component;
import org.springside.examples.miniservice.entity.Department;
import org.springside.examples.miniservice.entity.User;

@Component
public class AccountDao extends SqlSessionDaoSupport {

	public Department getDepartmentDetail(Long id) {
		return (Department) getSqlSession().selectOne("Account.getDepartmentDetail", id);
	}

	public User getUser(Long id) {
		return (User) getSqlSession().selectOne("Account.getUser", id);
	}

	public Long saveUser(User user) {
		getSqlSession().insert("Account.saveUser", user);
		return user.getId();
	}

	public List<User> searchUser(Map<String, Object> parameters) {
		return getSqlSession().selectList("Account.searchUser", parameters);
	}
}
