package com.fding.activity;

import org.json.JSONException;

import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fding.activity.server.MyApplication;
import com.fding.activity.server.SaveUserInfo;
import com.fding.activity.utils.Constant;
import com.fding.activity.utils.PhoneNumber;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.wind4app.wind4app2.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 注册页面
 *
 */
public class LoginActivity extends Activity {

	private TextView tx_xieyi;
	private Button btn_register, get_sms;
	private EditText et_user, et_password;
	ProgressDialog pdialog;

	private TimeCount time; // 验证码时间

	private String token;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		pdialog = new ProgressDialog(LoginActivity.this);
		et_user = (EditText) findViewById(R.id.et_user);
		et_password = (EditText) findViewById(R.id.et_paword);
		et_user.addTextChangedListener(new TextChange());
		et_password.addTextChangedListener(new TextChange());

		get_sms = (Button) findViewById(R.id.get_sms);

		time = new TimeCount(60000, 1000);// 构造CountDownTimer对象

		tx_xieyi = (TextView) findViewById(R.id.tx_xieyi_web);
		tx_xieyi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), UseAgreementActivity.class));
			}
		});

		// 启动消息推送服务
		startNotifyMessage();

		// 注册和获取验证码
		initRegister();

	}

	private void startNotifyMessage() {
		// 开启logcat输出，方便debug，发布时请关闭
		// XGPushConfig.enableDebug(this, true);
		// 如果需要知道注册是否成功，请使用registerPush(getApplicationContext(),
		// XGIOperateCallback)带callback版本
		// 如果需要绑定账号，请使用registerPush(getApplicationContext(),account)版本
		// 具体可参考详细的开发指南
		// 传递的参数为ApplicationContext
		Context context = getApplicationContext();
		XGPushManager.registerPush(context);

		Log.i("TPush", XGPushConfig.getToken(context));
		token = XGPushConfig.getToken(context);

		XGPushManager.registerPush(context, new XGIOperateCallback() {

			@Override
			public void onSuccess(Object arg0, int arg1) {
				// TODO Auto-generated method stub

				Log.i("TPush", arg0 + "成功");
			}

			@Override
			public void onFail(Object arg0, int arg1, String arg2) {
				// TODO Auto-generated method stub
				Log.i("TPush", arg1 + "错误" + arg2);
			}
		});

		// 其它常用的API：
		// 绑定账号（别名）注册：registerPush(context,account)或registerPush(context,account,
		// XGIOperateCallback)，其中account为APP账号，可以为任意字符串（qq、openid或任意第三方），业务方一定要注意终端与后台保持一致。
		// 取消绑定账号（别名）：registerPush(context,"*")，即account="*"为取消绑定，解绑后，该针对该账号的推送将失效
		// 反注册（不再接收消息）：unregisterPush(context)
		// 设置标签：setTag(context, tagName)
		// 删除标签：deleteTag(context, tagName)
	}

	// 获取验证码
	private void getsms() {

		String phone = et_user.getText().toString().trim();
		// 先判断
		if (PhoneNumber.isMobile(phone)) {
			time.start();
			JSONObject ojson = new JSONObject();

			try {
				ojson.put("phone", phone);
				ojson.put("type", 2);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			Log.i("params", "" + ojson);

			JsonObjectRequest jsonrequest = new JsonObjectRequest(Method.POST, Constant.URL_GetCode, ojson,
					new Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject obj1) {
							try {
								JSONObject obj = new JSONObject(obj1.toString());
								Log.i("request", obj + "");
								String msg = obj.getString("msg"); // 返回消息
								Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError arg0) {
							Toast.makeText(getApplicationContext(), "请检查您的网络连接！", 0).show();
						}
					});

			MyApplication.getHttpQueues().add(jsonrequest);

		} else {
			Toast.makeText(getApplicationContext(), "请输入正确的手机号码!", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 验证和注册
	 */
	private void initRegister() {
		// 发送验证码
		get_sms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getsms();
			}
		});

		// 注册按钮
		btn_register = (Button) findViewById(R.id.btn_register);
		btn_register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				pdialog.setMessage("正在登录...");
				pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pdialog.show();

				String phones = et_user.getText().toString().trim();
				String ver = et_password.getText().toString().trim();

				JSONObject obj = new JSONObject();
				try {
					obj.put("phone", phones);
					obj.put("verifycode", ver);
					obj.put("type", 2);
					obj.put("ptype", 1);
					obj.put("token", token);

				} catch (JSONException e) {
					e.printStackTrace();
				}
				Log.i("ganquan", obj + "");

				JsonObjectRequest jsonobj = new JsonObjectRequest(Method.POST, Constant.URL_Register, obj,
						new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject arg0) {
						try {
							JSONObject obj = new JSONObject(arg0.toString());
							int i = obj.getInt("status");
							String obj2 = obj.getString("msg");
							// Toast.makeText(getApplicationContext(), obj2,
							// Toast.LENGTH_SHORT).show();
							JSONObject jb = new JSONObject(obj.getString("result"));
							JSONObject j = new JSONObject(jb.getString("servicer"));
							Log.i("request", "个人信息" + j);

							if (i == 1) { // 登录成功
								pdialog.dismiss();
								// 成功提示框
								showSucessDialog();
								// 保存登录信息
								saveInfoInstance(j);

							} else if (i == 0) { // 登录失败
								pdialog.dismiss();
								Toast.makeText(getApplicationContext(), obj2, Toast.LENGTH_SHORT).show();
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						Toast.makeText(getApplicationContext(), "请检查您的网络连接！", 0).show();
					}
				});

				MyApplication.getHttpQueues().add(jsonobj);

			}

		});

	}

	// 保存登录信息
	private void saveInfoInstance(JSONObject j) {

		try {
			String icon = j.getString("icon");
			String phone = j.getString("phone");
			String status = j.getString("status");
			String type = j.getString("type");
			String password = j.getString("password");
			String id = j.getString("id");
			String qualification = j.getString("qualification");
			String lastLogin = j.getString("lastLogin");
			String token = j.getString("token");
			String identification = j.getString("identification");
			String name = j.getString("name");
			String account = j.getString("account");
			String longitude = j.getString("longitude");
			String creationTime = j.getString("creationTime");
			String latitude = j.getString("latitude");
			String rating = j.getString("rating");

			SaveUserInfo.getInstance(LoginActivity.this).setUserInfo("icon", icon);
			SaveUserInfo.getInstance(LoginActivity.this).setUserInfo("phone", phone);
			SaveUserInfo.getInstance(LoginActivity.this).setUserInfo("status", status);
			SaveUserInfo.getInstance(LoginActivity.this).setUserInfo("type", type);
			SaveUserInfo.getInstance(LoginActivity.this).setUserInfo("password", password);
			SaveUserInfo.getInstance(LoginActivity.this).setUserInfo("id", id);
			SaveUserInfo.getInstance(LoginActivity.this).setUserInfo("qualification", qualification);
			SaveUserInfo.getInstance(LoginActivity.this).setUserInfo("lastLogin", lastLogin);
			SaveUserInfo.getInstance(LoginActivity.this).setUserInfo("token", token);
			SaveUserInfo.getInstance(LoginActivity.this).setUserInfo("identification", identification);
			SaveUserInfo.getInstance(LoginActivity.this).setUserInfo("name", name);
			SaveUserInfo.getInstance(LoginActivity.this).setUserInfo("account", account);
			SaveUserInfo.getInstance(LoginActivity.this).setUserInfo("longitude", longitude);
			SaveUserInfo.getInstance(LoginActivity.this).setUserInfo("creationTime", creationTime);
			SaveUserInfo.getInstance(LoginActivity.this).setUserInfo("latitude", latitude);
			SaveUserInfo.getInstance(LoginActivity.this).setUserInfo("rating", rating);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		startActivity(new Intent(LoginActivity.this, OrderActivity.class));
		finish();

	}

	// 成功dialog
	private void showSucessDialog() {
		final AlertDialog adlog = new AlertDialog.Builder(this).create();
		adlog.setCancelable(false);
		adlog.show();
		Window window = adlog.getWindow();
		window.setContentView(R.layout.dialog_success);
	}

	// edittext 监听器
	private class TextChange implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

			boolean sing2 = et_user.getText().length() > 0;
			boolean sing3 = et_password.getText().length() > 0;

			if (sing2 & et_user.getText().length() == 11) {
				get_sms.setTextColor(0xFFE77777);
				get_sms.setEnabled(true);
			} else {

				get_sms.setEnabled(false);
			}

			if (sing2 & sing3) {
				btn_register.setTextColor(0xFFFFFFFF);
				btn_register.setEnabled(true);
			} else {
				btn_register.setTextColor(0xFFf2dee2);
				btn_register.setEnabled(false);
			}

		}
	}

	// 验证码按钮 监听器
	private class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		// 计时过程显示
		@Override
		public void onTick(long millisUntilFinished) {
			get_sms.setEnabled(false);
			get_sms.setText(millisUntilFinished / 1000 + "秒重新获取");
		}

		// 计时完毕时触发
		@Override
		public void onFinish() {
			get_sms.setText("获取验证码");
			get_sms.setEnabled(true);
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		MyApplication.getHttpQueues().cancelAll("longin");
		MyApplication.getHttpQueues().cancelAll("Register");
	}
}
