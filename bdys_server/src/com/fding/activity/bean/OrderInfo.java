package com.fding.activity.bean;

import java.util.Date;

/**
 * 订单
 *
 */
public class OrderInfo {

	private String id;

	private Integer userId;

	private Integer serviceUser;

	private Integer serviceType; // 1.vip 2.普通 3.专业

	private String serviceContent;

	private String healthRecord;

	private Integer hospitalId;

	private String hospitalName;

	private String contactsName;

	private String contactsPhone;

	private String patientName;

	private String creationTime;

	private String startTime;

	private Double duration;

	private Integer endType;

	private Date endtime;

	private Date replyTime;

	private Date payTime;

	private Integer status;

	private String money;

	private Integer userRating;

	private Integer serviceRating;

	private Integer orderLevel;

	private String description;

	private Integer userDel;

	private Integer serviceDel;

	private String serviceTypeName;

	private Integer red;

	private Integer green;

	private Integer blue;

	private Integer shuttle;

	private String shuttleDetail;

	public Integer getRed() {
		return red;
	}

	public void setRed(Integer red) {
		this.red = red;
	}

	public Integer getGreen() {
		return green;
	}

	public void setGreen(Integer green) {
		this.green = green;
	}

	public Integer getBlue() {
		return blue;
	}

	public void setBlue(Integer blue) {
		this.blue = blue;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getServiceUser() {
		return serviceUser;
	}

	public void setServiceUser(Integer serviceUser) {
		this.serviceUser = serviceUser;
	}

	public Integer getServiceType() {
		return serviceType;
	}

	public void setServiceType(Integer serviceType) {
		this.serviceType = serviceType;
	}

	public String getServiceContent() {
		return serviceContent;
	}

	public void setServiceContent(String serviceContent) {
		this.serviceContent = serviceContent;
	}

	public String getHealthRecord() {
		return healthRecord;
	}

	public void setHealthRecord(String healthRecord) {
		this.healthRecord = healthRecord;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getContactsName() {
		return contactsName;
	}

	public void setContactsName(String contactsName) {
		this.contactsName = contactsName;
	}

	public String getContactsPhone() {
		return contactsPhone;
	}

	public void setContactsPhone(String contactsPhone) {
		this.contactsPhone = contactsPhone;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public Double getDuration() {
		return duration;
	}

	public void setDuration(Double duration) {
		this.duration = duration;
	}

	public Integer getEndType() {
		return endType;
	}

	public void setEndType(Integer endType) {
		this.endType = endType;
	}

	public Date getEndtime() {
		return endtime;
	}

	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}

	public Date getReplyTime() {
		return replyTime;
	}

	public void setReplyTime(Date replyTime) {
		this.replyTime = replyTime;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public Integer getUserRating() {
		return userRating;
	}

	public void setUserRating(Integer userRating) {
		this.userRating = userRating;
	}

	public Integer getServiceRating() {
		return serviceRating;
	}

	public void setServiceRating(Integer serviceRating) {
		this.serviceRating = serviceRating;
	}

	public Integer getOrderLevel() {
		return orderLevel;
	}

	public void setOrderLevel(Integer orderLevel) {
		this.orderLevel = orderLevel;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getUserDel() {
		return userDel;
	}

	public void setUserDel(Integer userDel) {
		this.userDel = userDel;
	}

	public Integer getServiceDel() {
		return serviceDel;
	}

	public void setServiceDel(Integer serviceDel) {
		this.serviceDel = serviceDel;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	public String getServiceTypeName() {
		return serviceTypeName;
	}

	public void setServiceTypeName(String serviceTypeName) {
		this.serviceTypeName = serviceTypeName;
	}

	public Integer getShuttle() {
		return shuttle;
	}

	public void setShuttle(Integer shuttle) {
		this.shuttle = shuttle;
	}

	public String getShuttleDetail() {
		return shuttleDetail;
	}

	public void setShuttleDetail(String shuttleDetail) {
		this.shuttleDetail = shuttleDetail;
	}

}