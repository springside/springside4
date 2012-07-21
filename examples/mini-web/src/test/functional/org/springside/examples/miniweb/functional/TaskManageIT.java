package org.springside.examples.miniweb.functional;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springside.modules.test.category.Smoke;

/**
 * 任务管理的功能测试, 测试页面JavaScript及主要用户故事流程.
 * 
 * @author calvin
 */
public class TaskManageIT extends BaseFunctionalTestCase {

	/**
	 * 查看任务列表.
	 */
	@Test
	@Category(Smoke.class)
	public void viewTaskList() {
		s.open("/task/");
	}
}
