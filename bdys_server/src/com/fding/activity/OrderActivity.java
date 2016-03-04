package com.fding.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fding.activity.server.DataCleanManager;
import com.fding.activity.server.MyApplication;
import com.fding.activity.server.SaveUserInfo;
import com.fding.activity.utils.Constant;
import com.fding.activity.widget.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.update.UmengUpdateAgent;
import com.wind4app.wind4app2.R;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 主页面
 *
 */
public class OrderActivity extends FragmentActivity implements OnClickListener {

	private long clickTime = 0; // 记录第一次点击的时间

	private SlidingMenu mMenu;

	// 顶部栏
	private TextView Name, Title, Status;

	// 左侧栏
	private RelativeLayout photo, Account, Set, Message, Exit;

	private CircleImageView MyPhoto;

	// 退出弹窗
	private Button Enter, Cancel;

	private GoingOrder goingorder;
	private AvailableOrder avaiorder;
	private HistoryOrder hisorder;

	/**
	 * 进行中界面布局
	 */
	private RelativeLayout going_Layout;

	private ImageView going_image;

	/**
	 * 可接界面布局
	 */
	private RelativeLayout avai_Layout;

	private ImageView avai_image;

	/**
	 * 历史界面布局
	 */
	private RelativeLayout history_Layout;

	private ImageView history_image;

	/**
	 * 用于对Fragment进行管理
	 */
	private FragmentManager fragmentManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order);

		// 检查版本更新
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);

		// 初始化组件
		initData();

		// 获取用户信息
		getUserInfo();

		// 加载网络图片（头像）
		if (SaveUserInfo.getInstance(getApplicationContext()).getUserInfo("icon").equals("")) {
		} else {
			ImageLoader.getInstance()
					.displayImage(SaveUserInfo.getInstance(getApplicationContext()).getUserInfo("icon"), MyPhoto);
		}

		// 第一次启动时选中第0个tab
		fragmentManager = getFragmentManager();
		setTabSelection(1);
	}

	public void initData() {

		// 顶部栏
		Title = (TextView) findViewById(R.id.title);
		Status = (TextView) findViewById(R.id.status);

		// 侧滑菜单监听
		mMenu = (SlidingMenu) findViewById(R.id.id_menu);
		photo = (RelativeLayout) findViewById(R.id.photo);
		photo.setOnClickListener(this);
		Name = (TextView) findViewById(R.id.username);
		MyPhoto = (CircleImageView) findViewById(R.id.myphoto);

		Account = (RelativeLayout) findViewById(R.id.account);
		Account.setOnClickListener(this);
		Set = (RelativeLayout) findViewById(R.id.set);
		Set.setOnClickListener(this);
		Message = (RelativeLayout) findViewById(R.id.message);
		Message.setOnClickListener(this);
		Exit = (RelativeLayout) findViewById(R.id.exit);
		Exit.setOnClickListener(this);

		going_Layout = (RelativeLayout) findViewById(R.id.going_layout);
		avai_Layout = (RelativeLayout) findViewById(R.id.avai_layout);
		history_Layout = (RelativeLayout) findViewById(R.id.history_layout);
		going_Layout.setOnClickListener(this);
		avai_Layout.setOnClickListener(this);
		history_Layout.setOnClickListener(this);

		going_image = (ImageView) findViewById(R.id.going_image);
		avai_image = (ImageView) findViewById(R.id.avai_image);
		history_image = (ImageView) findViewById(R.id.history_image);

	}

	// 左侧边栏
	public void leftMenu(View view) {
		mMenu.toggle();
	}

	// 获取用户信息
	private void getUserInfo() {
		JSONObject o = new JSONObject();
		try {
			o.put("servicerid", SaveUserInfo.getInstance(getApplicationContext()).getUserInfo("id"));
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
							Name.setText(j.getString("name"));
							// workstatus = j.getInt("status");
							//
							// //上班状态
							// if(workstatus == 1){
							// Status.setVisibility(View.VISIBLE);
							// }else if(workstatus == 2){
							// Status.setVisibility(View.GONE);
							// }

						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						Toast.makeText(getApplicationContext(), "请检查你的网络连接！", 0).show();
						Name.setText(SaveUserInfo.getInstance(getApplicationContext()).getUserInfo("name"));
					}
				});

		MyApplication.getHttpQueues().add(request);
	}

	// 侧滑菜单监听事件
	@Override
	public void onClick(View v) {

		// initImageBack();

		switch (v.getId()) {
		case R.id.photo:
			startActivity(new Intent(getApplication(), PersonInfoActivity.class));
			break;
		case R.id.account:
			startActivity(new Intent(getApplication(), AccountActivity.class));
			break;
		case R.id.set:
			startActivity(new Intent(getApplication(), SetActivity.class));
			break;
		case R.id.message:
			startActivity(new Intent(getApplication(), MessageActivity.class));
			break;
		case R.id.exit:
			ExitDialog();
			break;
		case R.id.going_layout:
			Title.setText("进行中订单");
			Status.setVisibility(View.GONE);
			setTabSelection(0);
			break;
		case R.id.avai_layout:
			Title.setText("可接订单");
			setTabSelection(1);
			break;
		case R.id.history_layout:
			setTabSelection(2);
			Title.setText("历史订单");
			Status.setVisibility(View.GONE);
			break;
		}
	}

	// 退出登录Dialog
	private void ExitDialog() {
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setCancelable(false);
		dialog.show();
		Window window = dialog.getWindow();
		// 设置窗口的内容页面,dialog_order_prompt.xml文件中定义view内容
		window.setContentView(R.layout.dialog_exit);

		Cancel = (Button) window.findViewById(R.id.dialog_cancel);// 取消
		Enter = (Button) window.findViewById(R.id.dialog_complain);// 确定
		Enter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				exitLogin();
			}
		});
		Cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	// 退出登录
	private void exitLogin() {

		JSONObject obj = new JSONObject();
		try {
			obj.put("type", 2);
			obj.put("servicerid", SaveUserInfo.getInstance(getApplicationContext()).getUserInfo("id"));

		} catch (JSONException e) {
			e.printStackTrace();
		}

		JsonObjectRequest r = new JsonObjectRequest(Method.POST, Constant.URL_ExitLogin, obj,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject j) {
						try {
							JSONObject json = new JSONObject(j.toString());
							Log.i("request", "退出登录" + json);
							int status = json.getInt("status");
							if (status == 0) {
								Toast.makeText(getApplicationContext(), "退出失败[服务器内部错误]", 0).show();
							} else if (status == 1) {
								// 清除本地缓存
								DataCleanManager.cleanSharedPreference(getApplicationContext());
								Toast.makeText(getApplicationContext(), "账户退出成功", 0).show();
								startActivity(new Intent(getApplication(), LoginActivity.class));
								finish();
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {

					}
				});
		MyApplication.getHttpQueues().add(r);

	}

	@Override
	protected void onResume() {
		// 获取用户信息
		getUserInfo();

		// 加载网络图片
		if (SaveUserInfo.getInstance(getApplicationContext()).getUserInfo("icon").equals("")) {
		} else {
			ImageLoader.getInstance()
					.displayImage(SaveUserInfo.getInstance(getApplicationContext()).getUserInfo("icon"), MyPhoto);
		}

		super.onResume();
	}

	// // 初始化 各种图片
	// private void initImageBack() {
	// going_image.setBackgroundResource(R.drawable.going);
	// avai_image.setBackgroundResource(R.drawable.available);
	// history_image.setBackgroundResource(R.drawable.history);
	// }

	private void setTabSelection(int index) {
		// 每次选中之前先清楚掉上次的选中状态
		clearSelection();
		// 开启一个Fragment事务
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
		hideFragments(transaction);
		switch (index) {
		case 0:
			going_Layout.setBackgroundColor(0xffcbc9c9);
			// going_Layout.setBackgroundResource(R.drawable.add_photo);
			// going_image.setBackgroundResource(R.drawable.add_photo);
			if (goingorder == null) {
				// 如果MessageFragment为空，则创建一个并添加到界面上
				goingorder = new GoingOrder();
				transaction.add(R.id.content, goingorder);
			} else {
				// 如果MessageFragment不为空，则直接将它显示出来
				transaction.show(goingorder);
			}
			break;

		case 1:
			// 当点击了联系人tab时，改变控件的图片和文字颜色
			avai_Layout.setBackgroundColor(0xffcbc9c9);
			// avai_Layout.setBackgroundResource(R.drawable.add_photo);
			// avai_image.setBackgroundResource(R.drawable.add_photo);
			if (avaiorder == null) {
				// 如果ContactsFragment为空，则创建一个并添加到界面上
				avaiorder = new AvailableOrder();
				transaction.add(R.id.content, avaiorder);
			} else {
				// 如果ContactsFragment不为空，则直接将它显示出来
				transaction.show(avaiorder);
			}
			break;
		case 2:
			// 当点击了动态tab时，改变控件的图片和文字颜色
			history_Layout.setBackgroundColor(0xffcbc9c9);
			// history_Layout.setBackgroundResource(R.drawable.add_photo);
			// history_image.setBackgroundResource(R.drawable.add_photo);
			if (hisorder == null) {
				// 如果NewsFragment为空，则创建一个并添加到界面上
				hisorder = new HistoryOrder();
				transaction.add(R.id.content, hisorder);
			} else {
				// 如果NewsFragment不为空，则直接将它显示出来
				transaction.show(hisorder);
			}
			break;
		}
		transaction.commit();
	}

	/**
	 * 将所有的Fragment都置为隐藏状态。
	 *
	 * @param transaction
	 *            用于对Fragment执行操作的事务
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (goingorder != null) {
			transaction.hide(goingorder);
			Status.setVisibility(View.GONE);
		}
		if (avaiorder != null) {
			transaction.hide(avaiorder);
		}
		if (hisorder != null) {
			transaction.hide(hisorder);
			Status.setVisibility(View.GONE);
		}
	}

	/**
	 * 清除掉所有的选中状态。
	 */
	private void clearSelection() {
		going_Layout.setBackgroundColor(0xffffffff);
		avai_Layout.setBackgroundColor(0xffffffff);
		history_Layout.setBackgroundColor(0xffffffff);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void exit() {
		if ((System.currentTimeMillis() - clickTime) > 2000) {
			Toast.makeText(getApplicationContext(), "再按一次后退键退出布袋医仕！", Toast.LENGTH_SHORT).show();
			clickTime = System.currentTimeMillis();
		} else {

			this.finish();
		}
	}

}
