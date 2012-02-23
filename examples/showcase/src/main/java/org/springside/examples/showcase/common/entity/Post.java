package org.springside.examples.showcase.common.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DiscriminatorOptions;

/**
 * 帖子基类.
 */
@Entity
@Table(name = "SS_POST")
//单表继承策略
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//查询对象时强制加入子类标识字段
@DiscriminatorOptions(force = true)
public class Post extends IdEntity {
	protected String title;
	protected String content;
	protected User user;
	protected Date modifyTime;

	@Column(nullable = false)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	//延时加载的Lob字段, 需要运行instrument任务进行bytecode enhancement
	@Lob
	@Basic(fetch = FetchType.LAZY)
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	//与用户的多对一映射
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getAuthorName() {
		return user.getLoginName();
	}

	public void setAuthorName(String authorName) {
		user = new User();
		user.setLoginName(authorName);
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
}
