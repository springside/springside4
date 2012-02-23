package org.springside.examples.showcase.common.dao;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.springframework.stereotype.Component;
import org.springside.examples.showcase.common.entity.User;
import org.springside.modules.orm.hibernate.HibernateDao;

/**
 * 用户对象的泛型Hibernate Dao.
 * 
 * @author calvin
 */
@Component
public class UserHibernateDao extends HibernateDao<User, Long> {

	private static final String QUERY_USER_WITH_ROLE = "select u from User u left join fetch u.roleList order by u.id";
	private static final String COUNT_USERS = "select count(u) from User u";
	private static final String DISABLE_USERS = "update User u set u.status='disabled' where id in(:ids)";

	/**
	 * 批量修改用户状态.
	 */
	public void disableUsers(List<Long> ids) {
		Map<String, List<Long>> map = Collections.singletonMap("ids", ids);
		batchExecute(UserHibernateDao.DISABLE_USERS, map);
	}

	/**
	 * 使用 HQL 预加载lazy init的List<Role>,用DISTINCE_ROOT_ENTITY排除重复数据.
	 */

	public List<User> getAllUserWithRoleByDistinctHql() {
		Query query = createQuery(QUERY_USER_WITH_ROLE);
		return distinct(query).list();
	}

	/**
	 * 使用Criteria 预加载lazy init的List<Role>, 用DISTINCE_ROOT_ENTITY排除重复数据.
	 */

	public List<User> getAllUserWithRolesByDistinctCriteria() {
		Criteria criteria = createCriteria().setFetchMode("roleList", FetchMode.JOIN);
		return distinct(criteria).list();
	}

	/**
	 * 统计用户数.
	 */
	public Long getUserCount() {
		return findUnique(UserHibernateDao.COUNT_USERS);
	}

	/**
	 * 初始化User的延迟加载关联roleList.
	 */
	public void initUser(User user) {
		initProxyObject(user.getRoleList());
	}
}
