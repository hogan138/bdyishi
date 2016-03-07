package com.fding.activity.bean;

/**
 * 我的账户bean
 *
 */
public class EarnBean {

	/**
	 * 历史总收入
	 */
	private Integer earn;

	/**
	 * 月初日期
	 */
	private String firstDay;
	/**
	 * 月末日期
	 */
	private String lastDay;

	/**
	 * 服务时长
	 */
	private Integer servicetime;
	/**
	 * 总收入
	 */
	private Integer allmoney;
	/**
	 * 代缴费
	 */
	private Integer charge;
	/**
	 * 实际收入
	 */
	private Integer realearn;

	public Integer getEarn() {
		return earn;
	}

	public void setEarn(Integer earn) {
		this.earn = earn;
	}

	public String getFirstDay() {
		return firstDay;
	}

	public void setFirstDay(String firstDay) {
		this.firstDay = firstDay;
	}

	public String getLastDay() {
		return lastDay;
	}

	public void setLastDay(String lastDay) {
		this.lastDay = lastDay;
	}

	public Integer getServicetime() {
		return servicetime;
	}

	public void setServicetime(Integer servicetime) {
		this.servicetime = servicetime;
	}

	public Integer getAllmoney() {
		return allmoney;
	}

	public void setAllmoney(Integer allmoney) {
		this.allmoney = allmoney;
	}

	public Integer getCharge() {
		return charge;
	}

	public void setCharge(Integer charge) {
		this.charge = charge;
	}

	public Integer getRealearn() {
		return realearn;
	}

	public void setRealearn(Integer realearn) {
		this.realearn = realearn;
	}

}
