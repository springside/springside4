package org.springside.examples.showcase.common.dao;

import org.springframework.stereotype.Component;
import org.springside.examples.showcase.common.entity.Role;
import org.springside.modules.orm.hibernate.HibernateDao;

/**
 * 角色对象的泛型Hibernate Dao.
 * 
 * @author calvin
 */
@Component
public class RoleHibernateDao extends HibernateDao<Role, String> {
}
