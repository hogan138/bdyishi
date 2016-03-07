package com.fding.activity.bean;

/**
 * 资格认证bean
 *
 */
public class QualificationBean {

	private String name; // 真实姓名
	private String edupic; // 学历照片路径
	private String cerpic; // 相关证书路径

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEdupic() {
		return edupic;
	}

	public void setEdupic(String edupic) {
		this.edupic = edupic;
	}

	public String getCerpic() {
		return cerpic;
	}

	public void setCerpic(String cerpic) {
		this.cerpic = cerpic;
	}

}
