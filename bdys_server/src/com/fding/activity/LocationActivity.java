package com.fding.activity;

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
import com.fding.activity.server.MyApplication;
import com.fding.activity.server.SaveUserInfo;
import com.fding.activity.utils.Constant;
import com.wind4app.wind4app2.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 定位当前
 *
 */
public class LocationActivity extends Activity implements RadarSearchListener, RadarUploadInfoCallback {

	private GeoCoder geoCoder;
	private LocationClient mLocationClient = null;
	private RadarSearchManager mManager; // 雷达

	private bdListener listener = new bdListener();
	private boolean isFirstLoc = true;
	private LatLng pt;
	private TextView tx_hospital;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_hospital);

		tx_hospital = (TextView) findViewById(R.id.tx_hospital);

		// 地理编码查询
		geoCoder = GeoCoder.newInstance();
		geoCoder.setOnGetGeoCodeResultListener(geoderListener);

		// 定位到当前位置
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(listener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);
		mLocationClient.start();

	}

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
			Toast.makeText(getApplicationContext(), "清除位置成功", Toast.LENGTH_LONG).show();
		} else {
			// 清除失败
			Toast.makeText(getApplicationContext(), "清除位置失败", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onGetNearbyInfoList(RadarNearbyResult result, RadarSearchError error) {

	}

	@Override
	public void onGetUploadState(RadarSearchError error) {

		if (error == RadarSearchError.RADAR_NO_ERROR) {
			// 上传成功
			Toast.makeText(getApplicationContext(), "单次上传位置成功", Toast.LENGTH_LONG).show();
		} else {
			// 上传失败
			Toast.makeText(getApplicationContext(), "单次上传位置失败", Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public RadarUploadInfo OnUploadInfoCallback() {
		RadarUploadInfo info = new RadarUploadInfo();
		info.comments = "";
		info.pt = pt;
		Log.e("hjtest", "OnUploadInfoCallback");
		return info;
	}

	// 定位到当前位置
	public class bdListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {

			if (location == null) {
				Toast.makeText(getApplicationContext(), "定位失败，请检查网络设置", Toast.LENGTH_SHORT).show();
				return;
			}
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();

			if (isFirstLoc) {
				isFirstLoc = false;
				pt = new LatLng(location.getLatitude(), location.getLongitude());
				Double longitude = location.getLongitude();
				Double latitude = location.getLatitude();

				initSearch(pt);

				JSONObject json = new JSONObject();
				try {
					json.put("servicerid", SaveUserInfo.getInstance(getApplicationContext()).getUserInfo("id"));
					json.put("longitude", longitude);
					json.put("latitude", latitude);

				} catch (JSONException e) {
					e.printStackTrace();
				}

				Log.i("ganquan", "" + json);

				JsonObjectRequest request = new JsonObjectRequest(Method.POST, Constant.URL_location, json,
						new Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject obj) {
								try {
									JSONObject j = new JSONObject(obj.toString());
									Log.i("request", j + "");
									String msg = j.getString("msg");

									Toast.makeText(getApplicationContext(), msg, 1).show();

								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}, new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError arg0) {
								// TODO Auto-generated method stub

							}
						});
				MyApplication.getHttpQueues().add(request);

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
				Toast.makeText(getApplicationContext(), "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
				return;
			}
			tx_hospital.setText("你当前所在位置：" + result.getAddress());

		}

		@Override
		public void onGetGeoCodeResult(GeoCodeResult result) {

		}
	};

	@Override
	protected void onDestroy() {
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

}
