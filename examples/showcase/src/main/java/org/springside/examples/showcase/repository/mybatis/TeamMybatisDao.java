package org.springside.examples.showcase.repository.mybatis;

import org.springside.examples.showcase.entity.Team;

/**
 * 通过@MapperScannerConfigurer扫描目录中的所有接口, 动态在Spring Context中生成实现.
 * 方法名称必须与Mapper.xml中保持一致.
 * 
 * @author calvin
 */
@MyBatisRepository
public interface TeamMybatisDao {

	Team getWithDetail(Long id);
}
