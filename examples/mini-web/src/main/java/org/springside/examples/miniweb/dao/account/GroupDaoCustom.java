package org.springside.examples.miniweb.dao.account;

import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface GroupDaoCustom {

	/**
	 * 因为Group中没有建立与User的关联,因此需要以较低效率的方式进行删除User与Group的多对多中间表中的数据.
	 */
	void deleteWithReference(Long id);

}
