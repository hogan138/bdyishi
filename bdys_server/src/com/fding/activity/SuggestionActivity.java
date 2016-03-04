package com.fding.activity;

import org.json.JSONArray;
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
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 意见反馈
 *
 */
public class SuggestionActivity extends Activity {

	private ImageView Ic_back;

	private RelativeLayout User_Help, Call;

	private TextView Phone;

	private ImageView Call_phone;

	private EditText Title, Content;

	private Button Btn_Commit;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suggestion);

		// 返回上一页
		Ic_back = (ImageView) findViewById(R.id.ic_back);
		Ic_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		User_Help = (RelativeLayout) findViewById(R.id.rl_help);
		Call = (RelativeLayout) findViewById(R.id.call);
		Phone = (TextView) findViewById(R.id.phone);
		Call_phone = (ImageView) findViewById(R.id.img_phone);
		Title = (EditText) findViewById(R.id.suggestion_title);
		Content = (EditText) findViewById(R.id.suggestion_content);
		Btn_Commit = (Button) findViewById(R.id.btn_commit);

		// 获取客服电话
		getPhone();

		Btn_Commit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String title = Title.getText().toString();
				String content = Content.getText().toString();

				if (title.isEmpty()) {
					Toast.makeText(getApplicationContext(), "反馈主题不能为空!", 0).show();
				} else {
					if (content.isEmpty()) {
						Toast.makeText(getApplicationContext(), "反馈内容不能为空!", 0).show();
					} else {
						JSONObject obj = new JSONObject();
						try {
							obj.put("servicerid", SaveUserInfo.getInstance(SuggestionActivity.this).getUserInfo("id"));
							obj.put("title", title);
							obj.put("content", content);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						JsonObjectRequest request = new JsonObjectRequest(Method.POST, Constant.URL_Suggestion, obj,
								new Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject j) {
								try {
									JSONObject json = new JSONObject(j.toString());
									Log.i("request", "意见反馈" + json);
									String msg = json.getString("msg");
									Toast.makeText(getApplicationContext(), msg, 0).show();
									finish();
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

				}

			}
		});

	}

	// 获取客服电话
	private void getPhone() {

		JsonObjectRequest PhoneRequest = new JsonObjectRequest(Method.GET, Constant.URL_GetPhone, null,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject j) {
						try {
							JSONObject json = new JSONObject(j.toString());
							JSONObject jo = new JSONObject(json.getString("result"));
							JSONArray array = (JSONArray) jo.get("phonelist");
							for (int i = 0; i < array.length(); i++) {
								final JSONObject js = (JSONObject) array.get(i);
								Log.i("request", "客服电话" + js);
								Phone.setText(js.getString("phone"));
								Call_phone.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										try {

											Intent i = new Intent();
											i.setAction(Intent.ACTION_CALL);
											i.setData(Uri.parse("tel:" + js.getString("phone")));
											startActivity(i);

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
		MyApplication.getHttpQueues().add(PhoneRequest);
	}

	/**
	 * 点击空白处隐藏输入法
	 */

	// 获取点击事件
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View view = getCurrentFocus();
			if (isHideInput(view, ev)) {
				HideSoftInput(view.getWindowToken());
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	// 判定是否需要隐藏
	private boolean isHideInput(View v, MotionEvent ev) {
		if (v != null && (v instanceof EditText)) {
			int[] l = { 0, 0 };
			v.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
			if (ev.getX() > left && ev.getX() < right && ev.getY() > top && ev.getY() < bottom) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	// 隐藏软键盘
	private void HideSoftInput(IBinder token) {
		if (token != null) {
			InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			manager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}
