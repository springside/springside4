package org.springside.examples.showcase.common.dao;

import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Component;
import org.springside.examples.showcase.common.entity.User;

@Component
public class UserMyBatisDao extends SqlSessionDaoSupport {

	public User getUser(Long id) {
		return (User) getSqlSession().selectOne("Account.getUser", id);
	}

}
