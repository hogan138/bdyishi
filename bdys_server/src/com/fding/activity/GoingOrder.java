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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 进行中的订单
 *
 */
public class GoingOrder extends Fragment {

	private ListView Going_lv;
	private List<OrderInfo> mAppList;
	private GoingAdapter mAdapter;
	private SwipeRefreshLayout id_swipe_health;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.order_going, container, false);
		Going_lv = (ListView) v.findViewById(R.id.listView_going);
		mAppList = new ArrayList<OrderInfo>();
		mAdapter = new GoingAdapter();

		View view = getActivity().findViewById(R.id.status);
		view.setVisibility(View.GONE);

		// 获取进行中的订单列表
		getGoingOrder();

		id_swipe_health = (SwipeRefreshLayout) v.findViewById(R.id.id_swipe_health);
		id_swipe_health.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
				android.R.color.holo_orange_light, android.R.color.holo_red_light);
		id_swipe_health.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				mAppList.clear();
				getGoingOrder();
				mHandler.sendEmptyMessageDelayed(1, 3000);
			}
		});
		return v;
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Going_lv.setVisibility(View.GONE);
				mAdapter.notifyDataSetChanged();
				Going_lv.setVisibility(View.VISIBLE);
				id_swipe_health.setRefreshing(false);
				Toast.makeText(getActivity(), "刷新成功", 0).show();
				break;

			}
		};
	};

	// 获取进行中订单列表
	private void getGoingOrder() {

		JSONObject obj = new JSONObject();
		try {
			obj.put("servicerid", SaveUserInfo.getInstance(getActivity()).getUserInfo("id"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JsonObjectRequest request = new JsonObjectRequest(Method.POST, Constant.URL_GetGoing, obj,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject j) {
						try {
							mAppList.clear();
							JSONObject json = new JSONObject(j.toString());
							// String msg = json.getString("msg");
							// Toast.makeText(getActivity(), msg, 1).show();
							JSONObject jo = new JSONObject(json.getString("result"));
							Log.i("request", "进行中的订单" + json);
							JSONArray array = (JSONArray) jo.get("orderlist");

							for (int i = 0; i < array.length(); i++) {
								JSONObject obj = (JSONObject) array.get(i);
								OrderInfo info = new OrderInfo();
								info.setId(obj.getString("id"));
								info.setMoney(obj.getString("money"));
								info.setContactsPhone("contactsPhone");
								info.setShuttle(obj.getInt("shuttle"));
								info.setContactsName(obj.getString("contactsName"));
								info.setServiceTypeName(obj.getString("serviceTypeName"));
								info.setStartTime(obj.getString("startTime"));
								info.setServiceType(obj.getInt("serviceType"));
								info.setHospitalName(obj.getString("hospitalName"));
								info.setDuration(obj.getDouble("duration"));
								info.setStatus(obj.getInt("status"));
								info.setRed(obj.getInt("red"));
								info.setGreen(obj.getInt("green"));
								info.setBlue(obj.getInt("blue"));
								mAppList.add(info);
							}
							Going_lv.setAdapter(mAdapter);
							Going_lv.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
									String orderId = mAppList.get(position).getId().toString();
									Intent intent = new Intent(getActivity(), GoingOrdetailActivity.class);
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

		MyApplication.getHttpQueues().add(request);
	}

	class GoingAdapter extends BaseAdapter {

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
				convertView = View.inflate(getActivity(), R.layout.item_going, null);
				// 服务类型
				holder.serverTypeName = (Button) convertView.findViewById(R.id.servertype);
				holder.isreceive = (Button) convertView.findViewById(R.id.isReceive);
				holder.money = (TextView) convertView.findViewById(R.id.money);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.hospital = (TextView) convertView.findViewById(R.id.hospital);
				holder.startTime = (TextView) convertView.findViewById(R.id.startTime);
				holder.serverTime = (TextView) convertView.findViewById(R.id.serverTime);
				holder.orderstatus = (TextView) convertView.findViewById(R.id.orderstatus);
				holder.call = (ImageView) convertView.findViewById(R.id.call);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final OrderInfo item = getItem(position);
			// 是否接送
			if (item.getShuttle() == 0) {
			} else if (item.getShuttle() == 1) {
				holder.isreceive.setVisibility(View.VISIBLE);
			}
			holder.serverTypeName.setText(item.getServiceTypeName());
			holder.serverTypeName.setBackgroundColor(Color.argb(255, item.getRed(), item.getGreen(), item.getBlue()));
			holder.money.setText(item.getMoney());
			holder.name.setText(item.getContactsName());
			holder.hospital.setText(item.getHospitalName());
			holder.startTime.setText(item.getStartTime());
			holder.serverTime.setText(item.getDuration() + "");

			// 订单状态显示
			if (item.getStatus() == 2) {
				holder.orderstatus.setText("已接单");
			} else if (item.getStatus() == 3) {
				holder.orderstatus.setText("已付款");
			} else if (item.getStatus() == 4) {
				holder.orderstatus.setText("服务开始");
			} else if (item.getStatus() == 5) {
				holder.orderstatus.setText("服务结束");
			} else if (item.getStatus() == 6) {
				holder.orderstatus.setText("等待结账");
			} else if (item.getStatus() == 7) {
				holder.orderstatus.setText("等待评价");
			}

			holder.call.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent();
					i.setAction(Intent.ACTION_CALL);
					i.setData(Uri.parse("tel:" + item.getContactsPhone()));
					startActivity(i);
				}
			});
			return convertView;
		}
	}

	class ViewHolder {
		// 服务类型
		Button serverTypeName;
		Button isreceive;
		TextView money;
		TextView name;
		TextView hospital;
		TextView startTime;
		TextView serverTime;
		TextView orderstatus;

		ImageView call;
	}

	public void onResume() {
		View view = getActivity().findViewById(R.id.status);
		view.setVisibility(View.GONE);
		mAppList = new ArrayList<OrderInfo>();
		mAppList.clear();
		getGoingOrder();
		mAdapter.notifyDataSetChanged();
		super.onResume();
	}

	// //成功dialog
	// private void showSucessDialog() {
	// final AlertDialog adlog = new AlertDialog.Builder(getContext()).create();
	// adlog.setCancelable(false);
	// adlog.show();
	// Window window = adlog.getWindow();
	// window.setContentView(R.layout.dialog_refresh_success);
	// }

}
