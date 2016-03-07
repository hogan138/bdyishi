package com.fding.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.fding.activity.LocationActivity.bdListener;
import com.fding.activity.server.MyApplication;
import com.fding.activity.server.SaveUserInfo;
import com.fding.activity.utils.Constant;
import com.umeng.socialize.utils.Log;
import com.wind4app.wind4app2.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 地址地图页
 *
 */
public class MapAddressActivity extends Activity {

	private MapView mMapView;
    private BaiduMap mBaiduMap;
	private GeoCoder mSearch;
	private LocationClient mLocationClient = null;
	private bdListener listener1 = new bdListener();
	private boolean isFirstLoc = true;
	private LatLng pt;
	
	// 返回按钮
	private ImageView Ic_back;

	private String city,address;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        //注意该方法要再setContentView方法之前实现  
        SDKInitializer.initialize(getApplicationContext());  
		setContentView(R.layout.activity_map_address);
     
		//获取地图控件引用  
        mMapView = (MapView) findViewById(R.id.bmapView);  
		
		// 返回上一页
		Ic_back = (ImageView) findViewById(R.id.ic_back);
		Ic_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
        
		//获取地址
		Intent intent = getIntent();
		address = intent.getStringExtra("address");
		
		
		//检索
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(listener);
		

		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMyLocationEnabled(true);;
		// 定位到当前位置
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(listener1);
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15));
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setAddrType("all");
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);
		mLocationClient.start();
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
				mBaiduMap.setMyLocationData(locData);
				
				if (isFirstLoc) {
					isFirstLoc = false;
					pt = new LatLng(location.getLatitude(), location.getLongitude());
					Double longitude = location.getLongitude();
					Double latitude = location.getLatitude();
					
					MapStatusUpdate mu = MapStatusUpdateFactory.newLatLng(pt);
					mBaiduMap.animateMapStatus(mu);
					
					city = location.getCity();
					mSearch.geocode(new GeoCodeOption().city(city).address(address));
					Toast.makeText(getApplicationContext(), address, 0).show();
				}

			}

			public void onReceivePoi(BDLocation poiLocation) {

			}
		}
	
	OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {  
	    public void onGetGeoCodeResult(GeoCodeResult result) {  
	        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {  
	            //没有检索到结果  
	        }  
	        //获取地理编码结果  
	        Log.i("get",result.getLocation()+"");
	        
	        mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
	        		.icon(BitmapDescriptorFactory.fromResource(R.drawable.location)));
	        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));
	    }  
	 
	    @Override  
	    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {  
	        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {  
	            //没有找到检索结果  
	        }  
	        //获取反向地理编码结果  
	    }  
	};
	
	
	protected void onDestroy() {
		super.onDestroy();
		mSearch.destroy();
	};
	
}
