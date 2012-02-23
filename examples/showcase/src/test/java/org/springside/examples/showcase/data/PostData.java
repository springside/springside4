package org.springside.examples.showcase.data;

import java.util.Date;

import org.springside.examples.showcase.common.entity.Post;
import org.springside.examples.showcase.common.entity.User;

public class PostData {

	public static Post getDefaultPost() {
		Post post = new Post();
		post.setTitle("test post title");
		post.setContent("test post content");

		User user = new User();
		user.setLoginName("calvin");
		post.setUser(user);

		post.setModifyTime(new Date());

		return post;
	}

}
