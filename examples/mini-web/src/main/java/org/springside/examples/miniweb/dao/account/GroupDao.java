package org.springside.examples.miniweb.dao.account;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springside.examples.miniweb.entity.account.Group;
import org.springside.examples.miniweb.entity.account.User;
import org.springside.modules.orm.hibernate.HibernateDao;

/**
 * 权限组对象的泛型DAO.
 * 
 * @author calvin
 */
@Component
public class GroupDao extends HibernateDao<Group, Long> {

	private static final String QUERY_USER_BY_GROUPID = "select u from User u left join u.groupList g where g.id=?";

	/**
	 * 重载函数, 因为Group中没有建立与User的关联,因此需要以较低效率的方式进行删除User与Group的多对多中间表中的数据.
	 */
	@Override
	public void delete(Long id) {
		Group group = get(id);
		//查询出拥有该权限组的用户,并删除该用户的权限组.
		List<User> users = find(QUERY_USER_BY_GROUPID, group.getId());
		for (User u : users) {
			u.getGroupList().remove(group);
		}
		super.delete(group);
	}
}
