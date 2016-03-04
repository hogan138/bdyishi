package com.fding.activity.bean;

/**
 * 消息bean
 *
 */
public class MessageBean {

	private int id; // id
	private String title; // 标题
	private String createTime; // 日期
	private String content; // 消息内容

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

}
