package org.springside.examples.showcase.common.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.collect.Lists;

/**
 * 主题贴.
 * 
 * @author calvin
 */
@Entity
//子类标识字段值
@DiscriminatorValue("Subject")
public class Subject extends Post {

	private List<Reply> replyList = Lists.newArrayList();

	//与回帖的一对多关系,在删除主题时cascade删除回帖.
	@OneToMany(mappedBy = "subject", cascade = { CascadeType.REMOVE }, fetch = FetchType.LAZY, orphanRemoval = true)
	//按时间排序回帖
	@OrderBy(value = "modifyTime DESC")
	public List<Reply> getReplyList() {
		return replyList;
	}

	public void setReplyList(List<Reply> replyList) {
		this.replyList = replyList;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
