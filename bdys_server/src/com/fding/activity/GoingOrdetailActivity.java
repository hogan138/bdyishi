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
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 正在进行的订单详情
 *
 */
public class GoingOrdetailActivity extends Activity {

	// 返回按钮
	private ImageView Ic_back;

	private TextView ContactName, Contactphone, PatientName, HospitalName, StartTime, Duration, ServerType, Money,
			Address;

	private Button OrderStatus;

	private ImageView Call;

	private String orderid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_going_ordetail);

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
		Log.i("orderId", orderid);

		// 订单详情信息
		getOrderDeatal();

	}

	// 初始化数据
	private void info_init() {

		ContactName = (TextView) findViewById(R.id.contactname);
		Contactphone = (TextView) findViewById(R.id.contactphone);
		PatientName = (TextView) findViewById(R.id.patientname);
		HospitalName = (TextView) findViewById(R.id.hospitalname);
		StartTime = (TextView) findViewById(R.id.starttime);
		Duration = (TextView) findViewById(R.id.duration);
		ServerType = (TextView) findViewById(R.id.servertype);
		Money = (TextView) findViewById(R.id.money);
		OrderStatus = (Button) findViewById(R.id.orderstatus);
		Call = (ImageView) findViewById(R.id.call);
		Address = (TextView) findViewById(R.id.address);

	}

	// 获取订单详情信息
	private void getOrderDeatal() {
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
							Log.i("detail", jo + "");

							ContactName.setText(jo.getString("contactsName"));
							Contactphone.setText(jo.getString("contactsPhone"));
							Call.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									Intent i = new Intent();
									i.setAction(Intent.ACTION_CALL);
									try {
										i.setData(Uri.parse("tel:" + jo.getString("contactsPhone")));
										startActivity(i);
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							});
							PatientName.setText(jo.getString("patientName"));
							HospitalName.setText(jo.getString("hospitalName"));
							StartTime.setText(jo.getString("startTime"));
							Duration.setText(jo.getDouble("duration") + "");
							Money.setText(jo.getString("money"));
							ServerType.setText(jo.getString("serviceTypeName"));

							// 接送地址
							final String address = jo.getString("shuttlePlace") + jo.getString("shuttleDetail");
							String cc = ToSBC(address);
							if (address.isEmpty()) {
								Address.setText("无");
							} else {
								Address.setText(cc);
								Address.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										Intent intent = new Intent(getApplicationContext(),MapAddressActivity.class);
										intent.putExtra("address", address);
										startActivity(intent);
									}
								});
							}

							// 设置订单状态
							int status = jo.getInt("status");
							if (status == 2) {
								OrderStatus.setText("已接单");
								OrderStatus.setBackgroundResource(R.drawable.slct_enable);
							} else if (status == 3) {
								OrderStatus.setText("开始服务");
								OrderStatus.setEnabled(true);
								OrderStatus.setBackgroundResource(R.drawable.slct_register);
								OrderStatus.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										// 开始服务
										startServer();
									}
								});

							} else if (status == 4) {
								OrderStatus.setText("结束服务");
								OrderStatus.setEnabled(true);
								OrderStatus.setBackgroundResource(R.drawable.slct_register);
								OrderStatus.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										try {
											Intent intent = new Intent(GoingOrdetailActivity.this,
													SeeDocInfoActivity.class);
											intent.putExtra("id", jo.getString("id"));
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

							} else if (status == 5) {
								OrderStatus.setText("服务结束");
								OrderStatus.setBackgroundResource(R.drawable.slct_enable);
							} else if (status == 6) {
								OrderStatus.setText("等待结账");
								OrderStatus.setBackgroundResource(R.drawable.slct_enable);
							} else if (status == 7) {
								OrderStatus.setText("等待评价");
								OrderStatus.setBackgroundResource(R.drawable.slct_enable);
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

	// 开始服务
	private void startServer() {
		// 得到订单id
		Intent intent = getIntent();
		String orderid = intent.getStringExtra("orderId");
		Log.i("orderId", orderid);

		JSONObject obj = new JSONObject();
		try {
			obj.put("orderid", orderid);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JsonObjectRequest request = new JsonObjectRequest(Method.POST, Constant.URL_StartServer, obj,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject j) {
						try {
							JSONObject json = new JSONObject(j.toString());
							JSONObject js = new JSONObject(json.getString("result"));
							final JSONObject jo = new JSONObject(js.getString("order"));

							String msg = json.getString("msg");
							int status = json.getInt("status");

							Log.i("request", "开始服务" + jo);

							if (status == 0) {
								Toast.makeText(getApplicationContext(), msg, 0).show();
							} else if (status == 1) {
								Toast.makeText(getApplicationContext(), msg, 0).show();
								OrderStatus.setText("结束服务");
								OrderStatus.setEnabled(true);
								OrderStatus.setBackgroundResource(R.drawable.slct_register);
								OrderStatus.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										try {
											Intent intent = new Intent(GoingOrdetailActivity.this,
													SeeDocInfoActivity.class);
											intent.putExtra("id", jo.getString("id"));
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

		// 刷新订单信息
		getOrderDeatal();

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
