package com.fding.activity.bean;

/**
 * 实名认证
 *
 */
public class IdentityBean {

	private String name; // 真实名字
	private int identity; // 身份证号码
	private String frontal_idcard; // 身份证正面照片
	private String back_idcard; // 身份证反面照片

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIdentity() {
		return identity;
	}

	public void setIdentity(int identity) {
		this.identity = identity;
	}

	public String getFrontal_idcard() {
		return frontal_idcard;
	}

	public void setFrontal_idcard(String frontal_idcard) {
		this.frontal_idcard = frontal_idcard;
	}

	public String getBack_idcard() {
		return back_idcard;
	}

	public void setBack_idcard(String back_idcard) {
		this.back_idcard = back_idcard;
	}

}
