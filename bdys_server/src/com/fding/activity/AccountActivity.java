package com.fding.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fding.activity.bean.EarnBean;
import com.fding.activity.server.MyApplication;
import com.fding.activity.server.SaveUserInfo;
import com.fding.activity.utils.Constant;
import com.wind4app.wind4app2.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 我的账户
 *
 */

public class AccountActivity extends Activity {

	// 返回按钮
	private ImageView Ic_back;

	private ListView Account_lv;
	private List<EarnBean> mAppList;
	private AccountAdapter mAdapter;

	private TextView Money; // 累计盈利
	private SwipeRefreshLayout id_swipe_health;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);

		// 返回上一页
		Ic_back = (ImageView) findViewById(R.id.ic_back);
		Ic_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		id_swipe_health = (SwipeRefreshLayout) findViewById(R.id.id_swipe_health);
		id_swipe_health.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
				android.R.color.holo_orange_light, android.R.color.holo_red_light);
		id_swipe_health.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				mAppList.clear();
				updateMoney();
				mHandler.sendEmptyMessageDelayed(1, 3000);
			}
		});

		Money = (TextView) findViewById(R.id.money);
		Account_lv = (ListView) findViewById(R.id.listView);
		mAppList = new ArrayList<EarnBean>();
		mAdapter = new AccountAdapter();

		// 获取账单信息
		updateMoney();
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				Account_lv.setVisibility(View.GONE);
				mAdapter.notifyDataSetChanged();
				Account_lv.setVisibility(View.VISIBLE);
				id_swipe_health.setRefreshing(false);
				Toast.makeText(getApplicationContext(), "刷新成功", 0).show();
				break;

			}
		};
	};

	private void updateMoney() {

		JSONObject obj = new JSONObject();
		try {
			obj.put("servicerid", SaveUserInfo.getInstance(AccountActivity.this).getUserInfo("id"));

		} catch (JSONException e) {
			e.printStackTrace();
		}

		JsonObjectRequest request = new JsonObjectRequest(Method.POST, Constant.URL_Account, obj,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject j) {

						try {
							JSONObject json = new JSONObject(j.toString());
							Log.i("request", "金额全部信息" + json);
							String msg = json.getString("msg");
							Toast.makeText(getApplicationContext(), msg, 0).show();

							JSONObject jo = new JSONObject(json.getString("result"));

							// 获取总金额信息
							Double allearn = jo.getDouble("earn");
							Money.setText(allearn + "");

							JSONArray array = (JSONArray) jo.get("billlist");
							for (int i = 0; i < array.length(); i++) {

								JSONObject jj = (JSONObject) array.get(i);

								EarnBean earn = new EarnBean();
								earn.setFirstDay(jj.getString("firstDay"));
								earn.setLastDay(jj.getString("lastDay"));
								earn.setServicetime(jj.getInt("serviceTime"));
								earn.setAllmoney(jj.getInt("allmoney"));
								earn.setCharge(jj.getInt("charge"));
								earn.setRealearn(jj.getInt("realEarn"));
								mAppList.add(earn);

							}

							// 设置适配器
							Account_lv.setAdapter(mAdapter);

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
	};

	class AccountAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mAppList.size();
		}

		@Override
		public EarnBean getItem(int position) {
			return mAppList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.item_account, null);
				holder.firstDay = (TextView) convertView.findViewById(R.id.first_date);
				holder.lastDay = (TextView) convertView.findViewById(R.id.last_date);
				holder.serviceTime = (TextView) convertView.findViewById(R.id.servertime);
				holder.allmoney = (TextView) convertView.findViewById(R.id.allmoney);
				holder.charge = (TextView) convertView.findViewById(R.id.charge);
				holder.realEarn = (TextView) convertView.findViewById(R.id.realearn);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			EarnBean item = getItem(position);
			holder.firstDay.setText(item.getFirstDay());
			holder.lastDay.setText(item.getLastDay());
			holder.serviceTime.setText(item.getServicetime() + "");
			holder.allmoney.setText(item.getAllmoney() + "");
			holder.charge.setText(item.getCharge() + "");
			holder.realEarn.setText(item.getRealearn() + "");
			return convertView;
		}
	}

	class ViewHolder {
		TextView firstDay;
		TextView lastDay;
		TextView serviceTime;
		TextView allmoney;
		TextView charge;
		TextView realEarn;
	}

}
