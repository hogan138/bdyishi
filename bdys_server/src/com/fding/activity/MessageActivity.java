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
import com.fding.activity.bean.MessageBean;
import com.fding.activity.server.MyApplication;
import com.fding.activity.server.SaveUserInfo;
import com.fding.activity.utils.Constant;
import com.fding.activity.widget.RefreshLayout;
import com.fding.activity.widget.SwipeMenu;
import com.fding.activity.widget.SwipeMenuCreator;
import com.fding.activity.widget.SwipeMenuItem;
import com.fding.activity.widget.SwipeMenuListView;
import com.fding.activity.widget.SwipeMenuListView.OnMenuItemClickListener;
import com.wind4app.wind4app2.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/*
 *消息 列表
 *
 */

public class MessageActivity extends Activity {

	private SwipeMenuListView ListView;
	private List<MessageBean> mAppList;
	private MessageAdapter mAdapter;

	// 返回按钮
	private ImageView Ic_back;

	private RefreshLayout id_swipe_health;
//	private View footerLayout;
//	private TextView textMore;
//	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);

		// 返回上一页
		Ic_back = (ImageView) findViewById(R.id.ic_back);
		Ic_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ListView = (SwipeMenuListView) findViewById(R.id.listView);
		mAppList = new ArrayList<MessageBean>();
		mAdapter = new MessageAdapter();

		// 获取消息列表
		getMessage();

		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
				// set item background
				// deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
				// 0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(dp2px(80));
				// set a icon
				deleteItem.setIcon(R.drawable.account_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};

		ListView.setMenuCreator(creator);
		ListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public void onMenuItemClick(final int position, SwipeMenu menu, int index) {
				MessageBean item = mAppList.get(position);
				int id = item.getId();
				switch (index) {
				case 0:
					// 删除消息
					JSONObject obj = new JSONObject();
					try {
						obj.put("msgid", id);
					} catch (JSONException e) {
						e.printStackTrace();
					}

					JsonObjectRequest request = new JsonObjectRequest(Method.POST, Constant.URL_DeleteMessage, obj,
							new Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject j) {
							try {
								JSONObject json = new JSONObject(j.toString());
								Log.i("detail", "删除消息" + json);
								String msg = json.getString("msg");
								int status = json.getInt("status");
								if (status == 1) {
									Toast.makeText(getApplicationContext(), msg, 0).show();
									mAppList.remove(position);
									mAdapter.notifyDataSetChanged();
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

					break;
				}
			}
		});

		id_swipe_health = (RefreshLayout) findViewById(R.id.id_swipe_health);
//		footerLayout = getLayoutInflater().inflate(R.layout.listview_footer, null);
//		textMore = (TextView) footerLayout.findViewById(R.id.text_more);
//		progressBar = (ProgressBar) footerLayout.findViewById(R.id.load_progress_bar);
//
//		// 加载更多
//		textMore.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				getMessage();
//			}
//		});
//		ListView.addFooterView(footerLayout);
		
		id_swipe_health.setChildView(ListView);

		id_swipe_health.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
				android.R.color.holo_orange_light, android.R.color.holo_red_light);
		id_swipe_health.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				mAppList.clear();
				getMessage();
				mHandler.sendEmptyMessageDelayed(1, 3000);
			}
		});
//		id_swipe_health.setOnLoadListener(new RefreshLayout.OnLoadListener() {
//
//			@Override
//			public void onLoad() {
//				getMessage();
//			}
//
//		});
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				ListView.setVisibility(View.GONE);
				mAdapter.notifyDataSetChanged();
				ListView.setVisibility(View.VISIBLE);
				id_swipe_health.setRefreshing(false);
				Toast.makeText(getApplicationContext(), "刷新成功", 0).show();
				break;

			}
		};
	};

	// 获取消息列表
	private void getMessage() {

		JSONObject obj = new JSONObject();
		try {
			obj.put("type", 2);
			obj.put("servicerid", SaveUserInfo.getInstance(getApplicationContext()).getUserInfo("id"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JsonObjectRequest request = new JsonObjectRequest(Method.POST, Constant.URL_GetMessage, obj,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject j) {
						try {
							JSONObject json = new JSONObject(j.toString());
							// String m = json.getString("msg");
							// Toast.makeText(getApplicationContext(), m,
							// 0).show();
							JSONObject jso = new JSONObject(j.getString("result"));
							JSONArray array = (JSONArray) jso.get("messagelist");
							for (int i = 0; i < array.length(); i++) {
								JSONObject a = (JSONObject) array.get(i);
								MessageBean msg = new MessageBean();
								msg.setId(a.getInt("id"));
								msg.setTitle(a.getString("title"));
								msg.setCreateTime(a.getString("createTime"));
								msg.setContent(a.getString("content"));
								mAppList.add(msg);
								Log.i("request", "消息" + a);
							}
							ListView.setAdapter(mAdapter);
							ListView.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
									String title = mAppList.get(position).getTitle();
									String createTime = mAppList.get(position).getCreateTime();
									String content = mAppList.get(position).getContent();
									Intent intent = new Intent(getApplicationContext(), MessageDetailActivity.class);
									intent.putExtra("title", title);
									intent.putExtra("createTime", createTime);
									intent.putExtra("content", content);
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
						Toast.makeText(getApplicationContext(), "请检查您的网络连接！", 0).show();
					}
				});

		MyApplication.getHttpQueues().add(request);
	}

	class MessageAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mAppList.size();
		}

		@Override
		public MessageBean getItem(int position) {
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
				convertView = View.inflate(getApplicationContext(), R.layout.item_list_message, null);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.createTime = (TextView) convertView.findViewById(R.id.createtime);
				holder.content = (TextView) convertView.findViewById(R.id.content);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			MessageBean item = getItem(position);
			holder.title.setText(item.getTitle());
			holder.createTime.setText(item.getCreateTime());
			holder.content.setText(item.getContent());
			return convertView;
		}
	}

	class ViewHolder {
		TextView title;
		TextView createTime;
		TextView content;
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
	}
}
