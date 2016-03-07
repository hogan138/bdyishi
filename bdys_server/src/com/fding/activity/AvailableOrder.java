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
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchListener;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfo;
import com.baidu.mapapi.radar.RadarUploadInfoCallback;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.fding.activity.bean.OrderInfo;
import com.fding.activity.server.MyApplication;
import com.fding.activity.server.SaveUserInfo;
import com.fding.activity.utils.Constant;
import com.wind4app.wind4app2.R;

import android.app.AlertDialog;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 可接订单
 */

public class AvailableOrder extends Fragment implements RadarSearchListener, RadarUploadInfoCallback {

	private ListView Lv_avaiable;
	private List<OrderInfo> mAppList;
	private AvailableAdapter mAdapter;
	private RelativeLayout Work_status;
	private ImageView To_work;

	// 弹窗提示
	private Button Enter, Cancel;

	// 下拉刷新
	private SwipeRefreshLayout id_swipe_health;

	// 对顶部时间排序进行声明
	// private RadioGroup genderGroup;
	// private RadioButton creationTime;
	// private RadioButton startTime;

	// 定位
	private GeoCoder geoCoder;
	private LocationClient mLocationClient = null;
	private RadarSearchManager mManager; // 雷达

	private bdListener listener = new bdListener();
	private boolean isFirstLoc = true;
	private LatLng pt;
	private String City;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		SDKInitializer.initialize(getActivity().getApplicationContext());
		View view = inflater.inflate(R.layout.order_available, container, false);
		// 通过控件的ID来得到代表控件的对象
		// genderGroup = (RadioGroup) view.findViewById(R.id.tabs_rg);
		// creationTime = (RadioButton) view.findViewById(R.id.tab_createtime);
		// startTime = (RadioButton) view.findViewById(R.id.tab_starttime);

		Lv_avaiable = (ListView) view.findViewById(R.id.lv_available);
		Work_status = (RelativeLayout) view.findViewById(R.id.work_status);
		To_work = (ImageView) view.findViewById(R.id.to_work);
		mAppList = new ArrayList<OrderInfo>();
		mAdapter = new AvailableAdapter();

		// 获取用户信息
		getUserInfo();

		// 下拉刷新
		id_swipe_health = (SwipeRefreshLayout) view.findViewById(R.id.id_swipe_health);
		id_swipe_health.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
				android.R.color.holo_orange_light, android.R.color.holo_red_light);
		id_swipe_health.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				mAppList.clear();
				getAvailableOrder();
				mHandler.sendEmptyMessageDelayed(1, 3000);
			}
		});

		// 地理编码查询
		geoCoder = GeoCoder.newInstance();
		geoCoder.setOnGetGeoCodeResultListener(geoderListener);
		// 定位到当前位置
		mLocationClient = new LocationClient(getActivity());
		mLocationClient.registerLocationListener(listener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setAddrType("all");
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		return view;
	}

	// 获取用户信息
	private void getUserInfo() {
		JSONObject o = new JSONObject();
		try {
			o.put("servicerid", SaveUserInfo.getInstance(getActivity()).getUserInfo("id"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JsonObjectRequest request = new JsonObjectRequest(Method.POST, Constant.URL_GetUserInfo, o,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject ob) {
						try {
							JSONObject json = new JSONObject(ob.toString());
							JSONObject jb = new JSONObject(json.getString("result"));
							JSONObject j = new JSONObject(jb.getString("servicer"));
							Log.i("request", "个人信息" + j);
							int status = j.getInt("status");

							if (status == 2) {
								Work_status.setVisibility(View.VISIBLE);
								// 上班
								To_work.setOnClickListener(new OnClickListener() {
									public void onClick(View v) {
										// 开始工作
										startwork();
									}
								});
							} else if (status == 1) {

								// 加载订单列表
								id_swipe_health.setVisibility(View.VISIBLE);

								// 获取可接订单列表
								getAvailableOrder();

								// 下班
								isStopWork();

							}

						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						Toast.makeText(getActivity(), "请检查你的网络连接！", 0).show();
					}
				});

		MyApplication.getHttpQueues().add(request);
	}

	@Override
	public void onResume() {
		getUserInfo();
		mAppList = new ArrayList<OrderInfo>();
		mAppList.clear();
		getAvailableOrder();
		mAdapter.notifyDataSetChanged();
		super.onResume();
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				Lv_avaiable.setVisibility(View.GONE);
				mAdapter.notifyDataSetChanged();
				Lv_avaiable.setVisibility(View.VISIBLE);
				id_swipe_health.setRefreshing(false);
				Toast.makeText(getActivity(), "刷新成功", 0).show();
				break;

			}
		};
	};

	// 以下是定位相关
	public void initSearch(LatLng pt) {
		// 获取实例
		mManager = RadarSearchManager.getInstance();
		RadarSearchManager.getInstance().addNearbyInfoListener(this);
		RadarSearchManager.getInstance().setUserID(null);
		mManager.startUploadAuto(this, 5000);

	}

	@Override
	public void onGetClearInfoState(RadarSearchError error) {

		if (error == RadarSearchError.RADAR_NO_ERROR) {
			// 清除成功
			Toast.makeText(getActivity(), "清除位置成功", Toast.LENGTH_LONG).show();
		} else {
			// 清除失败
			Toast.makeText(getActivity(), "清除位置失败", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onGetNearbyInfoList(RadarNearbyResult result, RadarSearchError error) {

	}

	@Override
	public void onGetUploadState(RadarSearchError error) {

		if (error == RadarSearchError.RADAR_NO_ERROR) {
			// 上传成功
			// Toast.makeText(getApplicationContext(), "单次上传位置成功",
			// Toast.LENGTH_LONG).show();
		} else {
			// 上传失败
			// Toast.makeText(getApplicationContext(), "单次上传位置失败",
			// Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public RadarUploadInfo OnUploadInfoCallback() {
		RadarUploadInfo info = new RadarUploadInfo();
		info.comments = SaveUserInfo.getInstance(getActivity()).getUserInfo("id");
		info.pt = pt;
		Log.e("hjtest", "OnUploadInfoCallback");
		return info;
	}

	// 定位到当前位置
	public class bdListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {

			if (location == null) {
				Toast.makeText(getActivity(), "定位失败，请检查网络设置", Toast.LENGTH_SHORT).show();
				return;
			}
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();

			if (isFirstLoc) {
				isFirstLoc = false;
				pt = new LatLng(location.getLatitude(), location.getLongitude());
				Double latitude = location.getLatitude();
				Double longitude = location.getLongitude();

				City = location.getCity().toString();

				initSearch(pt);
				geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(pt));
			}

		}

		public void onReceivePoi(BDLocation poiLocation) {

		}
	}

	OnGetGeoCoderResultListener geoderListener = new OnGetGeoCoderResultListener() {

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(getActivity(), "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
				return;
			}

		}

		@Override
		public void onGetGeoCodeResult(GeoCodeResult result) {

		}
	};

	@Override
	public void onDestroy() {
		// 退出时销毁定位
		mLocationClient.stop();

		// 移除监听
		mManager.removeNearbyInfoListener(this);

		// 清除用户信息
		mManager.clearUserInfo();

		// 释放资源
		mManager.destroy();
		mManager = null;

		super.onDestroy();
	}

	// 是否进行实名认证Dialog
	private void showDialog() {
		final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
		dialog.setCancelable(false);
		dialog.show();
		Window window = dialog.getWindow();
		window.setContentView(R.layout.dialog_work_prompt);

		Cancel = (Button) window.findViewById(R.id.dialog_cancel);// 取消
		Enter = (Button) window.findViewById(R.id.dialog_complain);// 确定
		Enter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), PersonInfoActivity.class));
			}
		});
		Cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	// 开始工作
	private void startwork() {

		JSONObject obj = new JSONObject();

		try {
			obj.put("servicerid", SaveUserInfo.getInstance(getActivity()).getUserInfo("id"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JsonObjectRequest request = new JsonObjectRequest(Method.POST, Constant.URL_Startwork, obj,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject j) {

						try {
							JSONObject json = new JSONObject(j.toString());
							String msg = json.getString("msg");
							Log.i("request", json + "");
							JSONObject obj = new JSONObject(json.getString("result"));
							int istatus = obj.getInt("istatus"); // 实名认证状态
							int qstatus = obj.getInt("qstatus"); // 资格认证状态

							if (istatus == 3 && qstatus == 3) {
								Toast.makeText(getActivity(), msg, 0).show();
								Work_status.setVisibility(View.GONE); // 隐藏按钮
								// genderGroup.setVisibility(View.VISIBLE);
								// //显示顶部栏
								// 加载订单列表
								id_swipe_health.setVisibility(View.VISIBLE);

								// 获取可接订单列表
								getAvailableOrder();

								// 下班
								isStopWork();

							} else {
								// 立即进行实名认证
								showDialog();
							}

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

	// 获取可接订单列表
	private void getAvailableOrder() {

		JSONObject obj = new JSONObject();
		try {
			obj.put("sercityname", City);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Log.i("ganquan", "城市" + obj);

		JsonObjectRequest jor = new JsonObjectRequest(Method.POST, Constant.URL_GetAvailable, obj,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject j) {
						try {
							mAppList.clear();
							JSONObject json = new JSONObject(j.toString());
							// String msg = json.getString("msg");
							// Toast.makeText(getActivity(), msg, 1).show();
							Log.i("request", "可接订单列表" + json);
							JSONObject jo = new JSONObject(json.getString("result"));
							JSONArray array = (JSONArray) jo.get("orderlist");

							for (int i = 0; i < array.length(); i++) {
								JSONObject ob = (JSONObject) array.get(i);
								OrderInfo info = new OrderInfo();
								info.setId(ob.getString("id"));
								info.setMoney(ob.getString("money"));
								info.setShuttle(ob.getInt("shuttle"));
								info.setStartTime(ob.getString("startTime"));
								info.setServiceType(ob.getInt("serviceType"));
								info.setServiceTypeName(ob.getString("serviceTypeName"));
								info.setCreationTime(ob.getString("creationTime"));
								info.setHospitalName(ob.getString("hospitalName"));
								info.setDuration(ob.getDouble("duration"));
								info.setRed(ob.getInt("red"));
								info.setGreen(ob.getInt("green"));
								info.setBlue(ob.getInt("blue"));
								mAppList.add(info);
							}

							// 给RadioGroup设置事件监听
							// genderGroup.setOnCheckedChangeListener(new
							// RadioGroup.OnCheckedChangeListener() {
							// @Override
							// public void onCheckedChanged(RadioGroup group,
							// int checkedId) {
							// if (checkedId == creationTime.getId()) {
							//
							// // 按发布时间排序
							// Collections.sort(mAppList, new
							// Comparator<OrderInfo>() {
							//
							// @SuppressLint("SimpleDateFormat")
							// @Override
							// public int compare(OrderInfo lhs, OrderInfo rhs)
							// {
							// String startTime1 = lhs.getCreationTime();
							// String startTime2 = rhs.getCreationTime();
							//
							// SimpleDateFormat df = new
							// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							// Date s1 = null;
							// Date s2 = null;
							// try {
							// s1 = df.parse(startTime1);
							// s2 = df.parse(startTime2);
							// } catch (ParseException e) {
							// e.printStackTrace();
							// }
							//
							// // 对日期字段进行升序，如果欲降序可采用after方法
							// if (s1.before(s2)) {
							// return 1;
							// }
							// return -1;
							// }
							// });
							//
							// Lv_avaiable.setAdapter(mAdapter);
							//
							// } else if (checkedId == startTime.getId()) {
							//
							// // 按服务时间排序
							// Collections.sort(mAppList, new
							// Comparator<OrderInfo>() {
							//
							// @SuppressLint("SimpleDateFormat")
							// @Override
							// public int compare(OrderInfo lhs, OrderInfo rhs)
							// {
							// String startTime1 = lhs.getStartTime();
							// String startTime2 = rhs.getStartTime();
							//
							// SimpleDateFormat df = new
							// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							// Date s1 = null;
							// Date s2 = null;
							// try {
							// s1 = df.parse(startTime1);
							// s2 = df.parse(startTime2);
							// } catch (ParseException e) {
							// e.printStackTrace();
							// }
							//
							// // 对日期字段进行升序，如果欲降序可采用after方法
							// if (s1.before(s2)) {
							// return 1;
							// }
							// return -1;
							// }
							// });
							//
							// Lv_avaiable.setAdapter(mAdapter);
							// }
							// }
							// });

							// // 默认按发布时间排序
							// Collections.sort(mAppList, new
							// Comparator<OrderInfo>() {
							//
							// @SuppressLint("SimpleDateFormat")
							// @Override
							// public int compare(OrderInfo lhs, OrderInfo rhs)
							// {
							// String startTime1 = lhs.getCreationTime();
							// String startTime2 = rhs.getCreationTime();
							//
							// SimpleDateFormat df = new
							// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							// Date s1 = null;
							// Date s2 = null;
							// try {
							// s1 = df.parse(startTime1);
							// s2 = df.parse(startTime2);
							// } catch (ParseException e) {
							// e.printStackTrace();
							// }
							//
							// // 对日期字段进行升序，如果欲降序可采用after方法
							// if (s1.before(s2)) {
							// return 1;
							// }
							// return -1;
							// }
							// });

							Lv_avaiable.setAdapter(mAdapter);
							Lv_avaiable.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

									String orderId = mAppList.get(position).getId().toString();
									Intent intent = new Intent(getActivity(), AvailableOrdetailActivity.class);
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
						Toast.makeText(getActivity(), "请检查你的网络连接!", 0).show();
					}
				});

		MyApplication.getHttpQueues().add(jor);
	}

	class AvailableAdapter extends BaseAdapter {

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
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				convertView = View.inflate(getActivity(), R.layout.item_available, null);
				holder.serverTypeName = (Button) convertView.findViewById(R.id.servertype);
				holder.isreceive = (Button) convertView.findViewById(R.id.isReceive);
				holder.money = (TextView) convertView.findViewById(R.id.money);
				holder.hospital = (TextView) convertView.findViewById(R.id.hospital);
				holder.startTime = (TextView) convertView.findViewById(R.id.startTime);
				holder.serverTime = (TextView) convertView.findViewById(R.id.serverTime);
				holder.Btn_order = (Button) convertView.findViewById(R.id.available_order);
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
			holder.hospital.setText(item.getHospitalName());
			holder.startTime.setText(item.getStartTime());
			holder.serverTime.setText(item.getDuration() + "");

			// 接单
			holder.Btn_order.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String OrderId = item.getId();
					Log.i("receiver", OrderId);

					JSONObject obj = new JSONObject();
					try {
						obj.put("orderid", OrderId);
						obj.put("servicerid", SaveUserInfo.getInstance(getActivity()).getUserInfo("id"));
					} catch (JSONException e) {

						e.printStackTrace();
					}

					JsonObjectRequest request = new JsonObjectRequest(Method.POST, Constant.URL_ReceiverOrder, obj,
							new Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject j) {
							try {
								JSONObject json = new JSONObject(j.toString());
								String msg = json.getString("msg");
								int status = json.getInt("status");

								if (status == 0) {
									Toast.makeText(getActivity(), msg, 0).show();
								} else if (status == 1) {
									Toast.makeText(getActivity(), "接单成功,快去进行中订单处理！", 1).show();
									mAppList.remove(position);
									Lv_avaiable.setAdapter(mAdapter);

								}

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
			});

			return convertView;
		}
	}

	// 下班
	private void isStopWork() {
		View v = getActivity().findViewById(R.id.status);
		v.setVisibility(View.VISIBLE);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				JSONObject js = new JSONObject();
				try {
					js.put("servicerid", SaveUserInfo.getInstance(getActivity()).getUserInfo("id"));
				} catch (JSONException e) {
					e.printStackTrace();
				}

				JsonObjectRequest jr = new JsonObjectRequest(Method.POST, Constant.URL_StopWork, js,
						new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject j) {
						try {
							JSONObject json = new JSONObject(j.toString());
							int status = json.getInt("status");
							String msg = json.getString("msg");
							if (status == 0) {
								Toast.makeText(getActivity(), msg, 0).show();
							} else if (status == 1) {
								Toast.makeText(getActivity(), msg, 0).show();
								Work_status.setVisibility(View.VISIBLE);// 显示上班页
								// genderGroup.setVisibility(View.INVISIBLE);
								// //隐藏顶部栏
								id_swipe_health.setVisibility(View.INVISIBLE); // 隐藏订单列表
								// 上班
								To_work.setOnClickListener(new OnClickListener() {
									public void onClick(View v) {
										// 开始工作
										startwork();
									}
								});
								View v = getActivity().findViewById(R.id.status);// 隐藏上班按钮
								v.setVisibility(View.INVISIBLE);
							}

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
				MyApplication.getHttpQueues().add(jr);
			}
		});
	}

	class ViewHolder {
		// 服务类型
		Button serverTypeName;
		Button isreceive;
		TextView money;
		TextView hospital;
		TextView startTime;
		TextView serverTime;
		Button Btn_order;
	}

}
