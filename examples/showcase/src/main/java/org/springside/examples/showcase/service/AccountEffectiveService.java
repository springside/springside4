package org.springside.examples.showcase.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springside.examples.showcase.entity.Team;
import org.springside.examples.showcase.entity.User;
import org.springside.examples.showcase.modules.cache.memcached.MemcachedObjectType;
import org.springside.examples.showcase.repository.mybatis.TeamMybatisDao;
import org.springside.examples.showcase.repository.mybatis.UserMybatisDao;
import org.springside.modules.cache.memcached.SpyMemcachedClient;
import org.springside.modules.mapper.JsonMapper;

import com.google.common.collect.Maps;

/**
 * 更高效的AccountService实现，基于MyBatis + Memcached的方案，以JSON格式存储Memcached中的内容。
 * 
 * @author calvin
 */
@Component
@Transactional(readOnly = true)
public class AccountEffectiveService {

	@Autowired
	private UserMybatisDao userDao;
	@Autowired
	private TeamMybatisDao teamDao;

	@Autowired
	private SpyMemcachedClient memcachedClient;

	private final JsonMapper jsonMapper = JsonMapper.nonDefaultMapper();

	public Team getTeamWithDetail(Long id) {
		return teamDao.getTeamWithDetail(id);
	}

	/**
	 * 先访问Memcached, 使用JSON字符串存放对象以节约空间.
	 */
	public User getUser(Long id) {
		String key = MemcachedObjectType.USER.getPrefix() + id;

		User user = null;
		String jsonString = memcachedClient.get(key);

		if (jsonString == null) {
			user = userDao.getUser(id);
			if (user != null) {
				jsonString = jsonMapper.toJson(user);
				memcachedClient.set(key, MemcachedObjectType.USER.getExpiredTime(), jsonString);
			}
		} else {
			user = jsonMapper.fromJson(jsonString, User.class);
		}
		return user;
	}

	public List<User> searchUser(String loginName, String name) {
		Map<String, Object> parameters = Maps.newHashMap();
		parameters.put("loginName", loginName);
		parameters.put("name", name);
		return userDao.searchUser(parameters);
	}

	@Transactional
	public void saveUser(User user) {
		userDao.saveUser(user);
	}

	@Transactional
	public void deleteUser(Long id) {
		userDao.deleteUser(id);
	}
}
