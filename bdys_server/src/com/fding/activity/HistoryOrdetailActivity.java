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
import com.wind4app.wind4app2.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 历史订单详情
 *
 */
public class HistoryOrdetailActivity extends Activity {
	// 返回按钮
	private ImageView Ic_back;

	private TextView ContactName, PatientName, HospitalName, StartTime, Duration, ServerType, Money, Address;

	private Button CommitInfo;

	private String orderid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history_ordetail);

		// 返回上一页
		Ic_back = (ImageView) findViewById(R.id.ic_back);
		Ic_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// 初始化数据
		info_init();

		// 得到订单id
		Intent intent = getIntent();
		orderid = intent.getStringExtra("orderId");

		// 获取订单详情信息
		getOrderDetail();

	}

	// 初始化数据
	private void info_init() {
		ContactName = (TextView) findViewById(R.id.contactname);
		PatientName = (TextView) findViewById(R.id.patientname);
		HospitalName = (TextView) findViewById(R.id.hospitalname);
		StartTime = (TextView) findViewById(R.id.starttime);
		Duration = (TextView) findViewById(R.id.duration);
		ServerType = (TextView) findViewById(R.id.servertype);
		Money = (TextView) findViewById(R.id.money);
		Address = (TextView) findViewById(R.id.address);

		// 修改就诊资料
		CommitInfo = (Button) findViewById(R.id.btn_commit);
	}

	// 获取订单详情信息
	private void getOrderDetail() {

		JSONObject obj = new JSONObject();
		try {
			obj.put("orderid", orderid);
			obj.put("servicerid", SaveUserInfo.getInstance(getApplicationContext()).getUserInfo("id"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JsonObjectRequest request = new JsonObjectRequest(Method.POST, Constant.URL_GetOrderDetail, obj,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject j) {
						try {
							JSONObject json = new JSONObject(j.toString());
							JSONObject js = new JSONObject(json.getString("result"));
							final JSONObject jo = new JSONObject(js.getString("order"));
							Log.i("detail", "历史订单详情页" + json);

							ContactName.setText(jo.getString("contactsName"));
							PatientName.setText(jo.getString("patientName"));
							HospitalName.setText(jo.getString("hospitalName"));
							StartTime.setText(jo.getString("startTime"));
							Duration.setText(jo.getDouble("duration") + "");
							ServerType.setText(jo.getString("serviceTypeName"));
							Money.setText(jo.getString("money"));

							// 接送地址
							String address = jo.getString("shuttlePlace") + jo.getString("shuttleDetail");
							String cc = ToSBC(address);
							if (address.isEmpty()) {
								Address.setText("无");
							} else {
								Address.setText(cc);
							}

							// 是否补全健康档案
							int refuse = js.getInt("refuse");
							Log.i("detail", refuse + "");
							if (refuse == 0) {
								CommitInfo.setEnabled(false);
								CommitInfo.setBackgroundResource(R.drawable.slct_enable);
							} else if (refuse == 1) {
								CommitInfo.setBackgroundResource(R.drawable.slct_register);
								CommitInfo.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										try {
											Intent intent = new Intent(HistoryOrdetailActivity.this,
													UpdateSeeDocInfoActivity.class);
											intent.putExtra("healthRecord", jo.getInt("healthRecord"));
											intent.putExtra("userId", jo.getInt("userId"));
											intent.putExtra("startTime", jo.getString("startTime"));
											intent.putExtra("patientName", jo.getString("patientName"));
											intent.putExtra("hospitalName", jo.getString("hospitalName"));
											startActivity(intent);
										} catch (JSONException e) {
											e.printStackTrace();
										}

									}
								});
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

		MyApplication.getHttpQueues().add(request);
	}

	@Override
	protected void onResume() {

		// 更新详情页信息
		getOrderDetail();

		super.onResume();
	}

	public static String ToSBC(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == ' ') {
				c[i] = '\u3000';
			} else if (c[i] < '\177') {
				c[i] = (char) (c[i] + 65248);
			}
		}
		return new String(c);
	}
}
