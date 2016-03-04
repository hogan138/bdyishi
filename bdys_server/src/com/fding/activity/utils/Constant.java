package com.fding.activity.utils;

/**
 * 请求服务器地址
 */

public class Constant {

	// http://121.41.117.5:8081/bdys-app/html/UserAgreement.html

	// 外网IP
	private static String localhost = "http://121.41.117.5:8080/";

	// 内网IP
	// private static String localhost = "http://192.168.31.134:8080/";

	public static final String URL_GetCode = localhost + "bdys/account/verificationcode/getcode"; // 验证码
	public static final String URL_Register = localhost + "bdys/account/auth/login";// 登录及注册
	public static final String URL_location = localhost + "bdys/account/servicer/getlocation"; // 发送当前位置
	public static final String URL_GetUserInfo = localhost + "bdys/account/servicer/getinfo"; // 获取个人信息
	public static final String URL_UpdateUserInfo = localhost + "bdys/account/servicer/updateinfo"; // 更改个人信息
	public static final String URL_Identification = localhost + "bdys/event/identification/update"; // 实名认证
	public static final String URL_Qualification = localhost + "bdys/event/qualification/update"; // 资格认证
	public static final String URL_Account = localhost + "bdys/event/bill/list";// 获取金额
	public static final String URL_Startwork = localhost + "bdys/account/servicer/startwork"; // 开始上班
	public static final String URL_GetAvailable = localhost + "bdys/event/servicerorder/canaccorderlist"; // 获取可接订单列表
	public static final String URL_GetGoing = localhost + "bdys/event/servicerorder/doingorderlist"; // 获取进行中订单列表
	public static final String URL_StartServer = localhost + "bdys/event/servicerorder/start"; // 开始服务
	public static final String URL_StopServer = localhost + "bdys/event/servicerorder/end"; // 结束服务
	public static final String URL_GetHistory = localhost + "bdys/event/servicerorder/overorderlist"; // 历史订单列表
	public static final String URL_GetOrderDetail = localhost + "bdys/event/servicerorder/orderinfo"; // 订单详情
	public static final String URL_ReceiverOrder = localhost + "bdys/event/servicerorder/accpetorder"; // 接单
	public static final String URL_Suggestion = localhost + "bdys/event/feedback/sub"; // 意见反馈
	public static final String URL_GetPhone = localhost + "bdys/info/cservicer/getallphone"; // 获取客服电话
	public static final String URL_ExitLogin = localhost + "bdys/account/auth/logout"; // 退出登录
	public static final String URL_GetMessage = localhost + "bdys/event/pushmessage/getlist"; // 获取推送消息列表
	public static final String URL_Healthrecord = localhost + "bdys/event/healthrecord/update"; // 填写健康档案
	public static final String URL_StopWork = localhost + "bdys/account/servicer/endwork"; // 下班
	public static final String URL_DeleteMessage = localhost + "bdys/event/pushmessage/delmsg"; // 删除消息
	public static final String URL_UploadUrl = localhost + "bdys/event/file/upload"; // 上传图片

}
