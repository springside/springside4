package org.springside.examples.quickstart.data;

import org.springside.examples.quickstart.entity.Task;
import org.springside.modules.test.data.RandomData;

/**
 * Task相关实体测试数据生成.
 * 
 * @author calvin
 */
public class TaskData {

	public static Task getRandomTask() {
		Task task = new Task();
		task.setTitle(RandomData.randomName("Task"));

		return task;
	}

}
