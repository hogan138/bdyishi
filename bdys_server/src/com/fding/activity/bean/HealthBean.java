package com.fding.activity.bean;

/**
 * 健康档案
 *
 */
public class HealthBean {

	private int userid; // 对应用户id

	private int serverid; // 上传的服务人员id

	private String patientname; // 患者姓名

	private int hospitalid; // 医院id

	private String hospitalname; // 医院名称

	private String visitingtime; // 就诊时间

	private String advice; // 医嘱

	private String caspic; // 病例照

	private String prepic; // 处方照

	private String drugpic; // 药品照

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public int getServerid() {
		return serverid;
	}

	public void setServerid(int serverid) {
		this.serverid = serverid;
	}

	public String getPatientname() {
		return patientname;
	}

	public void setPatientname(String patientname) {
		this.patientname = patientname;
	}

	public int getHospitalid() {
		return hospitalid;
	}

	public void setHospitalid(int hospitalid) {
		this.hospitalid = hospitalid;
	}

	public String getHospitalname() {
		return hospitalname;
	}

	public void setHospitalname(String hospitalname) {
		this.hospitalname = hospitalname;
	}

	public String getVisitingtime() {
		return visitingtime;
	}

	public void setVisitingtime(String visitingtime) {
		this.visitingtime = visitingtime;
	}

	public String getAdvice() {
		return advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
	}

	public String getCaspic() {
		return caspic;
	}

	public void setCaspic(String caspic) {
		this.caspic = caspic;
	}

	public String getPrepic() {
		return prepic;
	}

	public void setPrepic(String prepic) {
		this.prepic = prepic;
	}

	public String getDrugpic() {
		return drugpic;
	}

	public void setDrugpic(String drugpic) {
		this.drugpic = drugpic;
	}

}
