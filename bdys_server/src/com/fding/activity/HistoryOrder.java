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
import com.fding.activity.bean.OrderInfo;
import com.fding.activity.server.MyApplication;
import com.fding.activity.server.SaveUserInfo;
import com.fding.activity.utils.Constant;
import com.wind4app.wind4app2.R;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 历史订单
 *
 */
public class HistoryOrder extends Fragment {

	private ListView History_lv;
	private List<OrderInfo> mAppList;
	private HistortyAdapter mAdapter;
	private SwipeRefreshLayout id_swipe_health;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.order_history, container, false);
		History_lv = (ListView) view.findViewById(R.id.listView_lv);
		mAppList = new ArrayList<OrderInfo>();
		mAdapter = new HistortyAdapter();

		// 历史订单列表
		getHistoryOrder();

		View view1 = getActivity().findViewById(R.id.status);
		view1.setVisibility(View.GONE);

		id_swipe_health = (SwipeRefreshLayout) view.findViewById(R.id.id_swipe_health);
		id_swipe_health.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
				android.R.color.holo_orange_light, android.R.color.holo_red_light);
		id_swipe_health.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				mAppList.clear();
				getHistoryOrder();
				mHandler.sendEmptyMessageDelayed(1, 3000);
			}
		});

		return view;
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				History_lv.setVisibility(View.GONE);
				mAdapter.notifyDataSetChanged();
				History_lv.setVisibility(View.VISIBLE);
				id_swipe_health.setRefreshing(false);
				Toast.makeText(getActivity(), "刷新成功", 0).show();
				break;

			}
		};
	};

	// 获取历史订单列表
	private void getHistoryOrder() {

		JSONObject obj = new JSONObject();
		try {
			obj.put("servicerid", SaveUserInfo.getInstance(getActivity()).getUserInfo("id"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JsonObjectRequest jor = new JsonObjectRequest(Method.POST, Constant.URL_GetHistory, obj,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject j) {
						try {
							mAppList.clear();
							JSONObject json = new JSONObject(j.toString());
							// String msg = json.getString("msg");
							// Toast.makeText(getContext(), msg, 0).show();
							Log.i("request", "历史订单" + json);
							JSONObject jo = new JSONObject(json.getString("result"));
							JSONArray array = (JSONArray) jo.get("orderlist");

							for (int i = 0; i < array.length(); i++) {

								JSONObject obj = (JSONObject) array.get(i);

								OrderInfo info = new OrderInfo();
								info.setId(obj.getString("id"));
								info.setMoney(obj.getString("money"));
								info.setShuttle(obj.getInt("shuttle"));
								info.setStartTime(obj.getString("startTime"));
								info.setHospitalName(obj.getString("hospitalName"));
								info.setServiceType(obj.getInt("serviceType"));
								info.setServiceTypeName(obj.getString("serviceTypeName"));
								info.setDuration(obj.getDouble("duration"));
								info.setRed(obj.getInt("red"));
								info.setGreen(obj.getInt("green"));
								info.setBlue(obj.getInt("blue"));
								mAppList.add(info);

							}

							History_lv.setAdapter(mAdapter);
							History_lv.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
									String orderId = mAppList.get(position).getId().toString();
									Intent intent = new Intent(getActivity(), HistoryOrdetailActivity.class);
									intent.putExtra("orderId", orderId);
									startActivity(intent);
								}
							});

						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						Toast.makeText(getActivity(), "请检查您的网络连接！", 0).show();
					}
				});

		MyApplication.getHttpQueues().add(jor);
	}

	class HistortyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mAppList.size();
		}

		@Override
		public OrderInfo getItem(int position) {
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
				convertView = View.inflate(getActivity(), R.layout.item_history, null);
				// 服务类型
				holder.serverTypeName = (Button) convertView.findViewById(R.id.servertype);
				holder.isreceive = (Button) convertView.findViewById(R.id.isReceive);
				holder.money = (TextView) convertView.findViewById(R.id.money);
				holder.hospital = (TextView) convertView.findViewById(R.id.hospital);
				holder.startTime = (TextView) convertView.findViewById(R.id.startTime);
				holder.serverTime = (TextView) convertView.findViewById(R.id.serverTime);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			OrderInfo item = getItem(position);
			// 是否接送
			if (item.getShuttle() == 0) {
			} else if (item.getShuttle() == 1) {
				holder.isreceive.setVisibility(View.VISIBLE);
			}
			holder.serverTypeName.setText(item.getServiceTypeName());
			holder.serverTypeName.setBackgroundColor(Color.argb(255, item.getRed(), item.getGreen(), item.getBlue()));
			holder.money.setText(item.getMoney());
			holder.hospital.setText(item.getHospitalName());
			holder.startTime.setText(item.getStartTime());
			holder.serverTime.setText(item.getDuration() + "");
			return convertView;
		}
	}

	class ViewHolder {

		// 服务类型
		Button serverTypeName;
		Button isreceive;
		TextView money;
		TextView hospital;
		TextView startTime;
		TextView serverTime;
	}

	public void onResume() {
		View view = getActivity().findViewById(R.id.status);
		view.setVisibility(View.GONE);
		mAppList = new ArrayList<OrderInfo>();
		mAppList.clear();
		getHistoryOrder();
		mAdapter.notifyDataSetChanged();
		super.onResume();
	}

}
