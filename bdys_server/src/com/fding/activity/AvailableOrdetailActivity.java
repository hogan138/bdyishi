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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 可接订单详情页
 *
 */
public class AvailableOrdetailActivity extends Activity {

	// 返回按钮
	private ImageView Ic_back;

	private TextView ContactName, Contactphone, PatientName, HospitalName, StartTime, Duration, ServerType, Money,
			Address;

	private ImageView Call;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_available_ordetail);

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
		String orderid = intent.getStringExtra("orderId");

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
							int severType = jo.getInt("serviceType");
							if (severType == 1) {
								ServerType.setText("VIP");
							} else if (severType == 2) {
								ServerType.setText("普通");
							} else if (severType == 3) {
								ServerType.setText("专业");
							}
							Money.setText(jo.getString("money"));

							// 接送地址
							String address = jo.getString("shuttlePlace") + jo.getString("shuttleDetail");
							String cc = ToSBC(address);
							if (address.isEmpty()) {
								Address.setText("无");
							} else {
								Address.setText(cc);
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

	private void info_init() {

		ContactName = (TextView) findViewById(R.id.contactname);
		Contactphone = (TextView) findViewById(R.id.contactphone);
		PatientName = (TextView) findViewById(R.id.patientname);
		HospitalName = (TextView) findViewById(R.id.hospitalname);
		StartTime = (TextView) findViewById(R.id.starttime);
		Duration = (TextView) findViewById(R.id.duration);
		ServerType = (TextView) findViewById(R.id.servertype);
		Money = (TextView) findViewById(R.id.money);
		Call = (ImageView) findViewById(R.id.call);
		Address = (TextView) findViewById(R.id.address);

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
