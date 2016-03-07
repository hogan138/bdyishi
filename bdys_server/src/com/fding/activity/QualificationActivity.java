package com.fding.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

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
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.wind4app.wind4app2.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;

/**
 * 资格认证
 *
 */
public class QualificationActivity extends Activity {

	private ImageView Ic_back;

	private EditText Name;

	private Button Btn_commit;

	private Button Enter, Cancel;

	// 学历照
	private GridView gridView1; // 网格显示缩略图
	private final int IMAGE_OPEN1 = 1; // 打开图片标记
	private String pathImage1; // 选择图片路径
	private Bitmap bmp; // 导入临时图片
	private ArrayList<HashMap<String, Object>> imageItem1;
	private SimpleAdapter simpleAdapter1; // 适配器

	// 其他证书
	private GridView gridView2; // 网格显示缩略图
	private final int IMAGE_OPEN2 = 2; // 打开图片标记
	private String pathImage2; // 选择图片路径
	private ArrayList<HashMap<String, Object>> imageItem2;
	private SimpleAdapter simpleAdapter2; // 适配器

	// 图片上传
	private JSONArray ImageArray1 = new JSONArray();
	private JSONArray ImageArray2 = new JSONArray();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qualification);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		Name = (EditText) findViewById(R.id.name);
		Ic_back = (ImageView) findViewById(R.id.ic_back);
		Ic_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// 图片上传
		initPhoto();

		Btn_commit = (Button) findViewById(R.id.btn_commit);
		Btn_commit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				commit();
			}
		});

	}

	private void initPhoto() {

		// 获取控件对象
		gridView1 = (GridView) findViewById(R.id.gridView1);
		gridView2 = (GridView) findViewById(R.id.gridView2);
		/*
		 * 载入默认图片添加图片加号 通过适配器实现 SimpleAdapter参数imageItem为数据源
		 * R.layout.griditem_addpic为布局
		 */
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.add_photo); // 加号
		imageItem1 = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("itemImage", bmp);
		imageItem1.add(map);

		int size = imageItem1.size();

		int length = 80;

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.density;
		int gridviewWidth = (int) (size * (length + 4) * density);
		int itemWidth = (int) (length * density);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gridviewWidth,
				LinearLayout.LayoutParams.FILL_PARENT);
		gridView1.setLayoutParams(params); // 重点
		gridView1.setColumnWidth(itemWidth); // 重点
		gridView1.setHorizontalSpacing(5); // 间距
		gridView1.setStretchMode(GridView.NO_STRETCH);
		gridView1.setNumColumns(size); // 重点

		simpleAdapter1 = new SimpleAdapter(this, imageItem1, R.layout.griditem_addpic, new String[] { "itemImage" },
				new int[] { R.id.imageView1 });
		/*
		 * HashMap载入bmp图片在GridView中不显示,但是如果载入资源ID能显示 如 map.put("itemImage",
		 * R.drawable.img); 解决方法: 1.自定义继承BaseAdapter实现 2.ViewBinder()接口实现 参考
		 * http://blog.csdn.net/admin_/article/details/7257901
		 */
		simpleAdapter1.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data, String textRepresentation) {
				if (view instanceof ImageView && data instanceof Bitmap) {
					ImageView i = (ImageView) view;
					i.setImageBitmap((Bitmap) data);
					return true;
				}
				return false;
			}
		});

		gridView1.setAdapter(simpleAdapter1);

		/*
		 * 监听GridView点击事件 报错:该函数必须抽象方法 故需要手动导入import android.view.View;
		 */
		gridView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				if (imageItem1.size() == 10 & position == 0) { // 第一张为默认图片
					Toast.makeText(getApplicationContext(), "最多上传9张图片", Toast.LENGTH_SHORT).show();
				} else if (position == 0) { // 点击图片位置为+ 0对应0张图片
					Toast.makeText(getApplicationContext(), "请选择图片", Toast.LENGTH_SHORT).show();
					// 选择图片
					Intent intent = new Intent(Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, IMAGE_OPEN1);
					// 通过onResume()刷新数据
				} else {
					dialog(position);
				}

			}
		});

		/*
		 * 载入默认图片添加图片加号 通过适配器实现 SimpleAdapter参数imageItem为数据源
		 * R.layout.griditem_addpic为布局
		 */
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.add_photo); // 加号
		imageItem2 = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map1 = new HashMap<String, Object>();
		map1.put("itemImage", bmp);
		imageItem2.add(map1);

		int size1 = imageItem2.size();

		int length1 = 80;

		DisplayMetrics dm1 = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm1);
		float density1 = dm1.density;
		int gridviewWidth1 = (int) (size1 * (length1 + 4) * density1);
		int itemWidth1 = (int) (length1 * density1);

		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(gridviewWidth1,
				LinearLayout.LayoutParams.FILL_PARENT);
		gridView2.setLayoutParams(params1); // 重点
		gridView2.setColumnWidth(itemWidth1); // 重点
		gridView2.setHorizontalSpacing(5); // 间距
		gridView2.setStretchMode(GridView.NO_STRETCH);
		gridView2.setNumColumns(size1); // 重点

		simpleAdapter2 = new SimpleAdapter(this, imageItem2, R.layout.griditem_addpic, new String[] { "itemImage" },
				new int[] { R.id.imageView1 });
		/*
		 * HashMap载入bmp图片在GridView中不显示,但是如果载入资源ID能显示 如 map.put("itemImage",
		 * R.drawable.img); 解决方法: 1.自定义继承BaseAdapter实现 2.ViewBinder()接口实现 参考
		 * http://blog.csdn.net/admin_/article/details/7257901
		 */
		simpleAdapter2.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data, String textRepresentation) {
				if (view instanceof ImageView && data instanceof Bitmap) {
					ImageView i = (ImageView) view;
					i.setImageBitmap((Bitmap) data);
					return true;
				}
				return false;
			}
		});

		gridView2.setAdapter(simpleAdapter2);

		/*
		 * 监听GridView点击事件 报错:该函数必须抽象方法 故需要手动导入import android.view.View;
		 */
		gridView2.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				if (imageItem2.size() == 10 & position == 0) { // 第一张为默认图片
					Toast.makeText(getApplicationContext(), "最多上传9张图片", Toast.LENGTH_SHORT).show();
				} else if (position == 0) { // 点击图片位置为+ 0对应0张图片
					Toast.makeText(getApplicationContext(), "请选择图片", Toast.LENGTH_SHORT).show();
					// 选择图片
					Intent intent = new Intent(Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, IMAGE_OPEN2);
					// 通过onResume()刷新数据
				} else {
					dialog1(position);
				}

			}
		});

	}

	// 获取图片路径 响应startActivityForResult
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 打开图片
		if (resultCode == RESULT_OK && requestCode == IMAGE_OPEN1) {
			Uri uri = data.getData();
			if (!TextUtils.isEmpty(uri.getAuthority())) {
				// 查询选择图片
				Cursor cursor = getContentResolver().query(uri, new String[] { MediaStore.Images.Media.DATA }, null,
						null, null);
				// 返回 没找到选择图片
				if (null == cursor) {
					return;
				}
				// 光标移动至开头 获取图片路径
				cursor.moveToFirst();
				pathImage1 = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

				// 上传图片到服务器
				RequestParams params = new RequestParams();
				params.addBodyParameter("file", new File(pathImage1));
				HttpUtils _http = new HttpUtils();
				_http.send(HttpMethod.POST, Constant.URL_UploadUrl, params, new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						System.out.println(arg1);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						try {
							JSONObject jsonObject = new JSONObject(arg0.result);
							System.out.println(arg0.result + "");
							JSONObject json = new JSONObject(jsonObject.getString("result"));
							String path = json.getString("path");

							// 获得服务器返回路径
							ImageArray1.put(path);

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

				});
			}
		}

		// 打开图片
		if (resultCode == RESULT_OK && requestCode == IMAGE_OPEN2) {
			Uri uri = data.getData();
			if (!TextUtils.isEmpty(uri.getAuthority())) {
				// 查询选择图片
				Cursor cursor = getContentResolver().query(uri, new String[] { MediaStore.Images.Media.DATA }, null,
						null, null);
				// 返回 没找到选择图片
				if (null == cursor) {
					return;
				}
				// 光标移动至开头 获取图片路径
				cursor.moveToFirst();
				pathImage2 = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

				// 上传图片到服务器
				RequestParams params = new RequestParams();
				params.addBodyParameter("file", new File(pathImage2));
				HttpUtils _http = new HttpUtils();
				_http.send(HttpMethod.POST, Constant.URL_UploadUrl, params, new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						System.out.println(arg1);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						try {
							JSONObject jsonObject = new JSONObject(arg0.result);
							System.out.println(arg0.result + "");
							JSONObject json = new JSONObject(jsonObject.getString("result"));
							String path = json.getString("path");

							// 获得服务器返回路径
							ImageArray2.put(path);

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

				});

			}
		}
	}

	// 刷新图片
	@Override
	protected void onResume() {
		super.onResume();
		if (!TextUtils.isEmpty(pathImage1)) {
			Bitmap addbmp = BitmapFactory.decodeFile(pathImage1);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", addbmp);
			imageItem1.add(map);

			int size = imageItem1.size();

			int length = 80;

			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			float density = dm.density;
			int gridviewWidth = (int) (size * (length + 4) * density);
			int itemWidth = (int) (length * density);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gridviewWidth,
					LinearLayout.LayoutParams.FILL_PARENT);
			gridView1.setLayoutParams(params); // 重点
			gridView1.setColumnWidth(itemWidth); // 重点
			gridView1.setHorizontalSpacing(5); // 间距
			gridView1.setStretchMode(GridView.NO_STRETCH);
			gridView1.setNumColumns(size); // 重点

			simpleAdapter1 = new SimpleAdapter(this, imageItem1, R.layout.griditem_addpic, new String[] { "itemImage" },
					new int[] { R.id.imageView1 });
			simpleAdapter1.setViewBinder(new ViewBinder() {
				@Override
				public boolean setViewValue(View view, Object data, String textRepresentation) {
					// TODO Auto-generated method stub
					if (view instanceof ImageView && data instanceof Bitmap) {
						ImageView i = (ImageView) view;
						i.setImageBitmap((Bitmap) data);
						return true;
					}
					return false;
				}
			});
			gridView1.setAdapter(simpleAdapter1);
			simpleAdapter1.notifyDataSetChanged();
			// 刷新后释放防止手机休眠后自动添加
			pathImage1 = null;
		}

		if (!TextUtils.isEmpty(pathImage2)) {
			Bitmap addbmp = BitmapFactory.decodeFile(pathImage2);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", addbmp);
			imageItem2.add(map);

			int size1 = imageItem2.size();

			int length1 = 80;

			DisplayMetrics dm1 = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm1);
			float density1 = dm1.density;
			int gridviewWidth1 = (int) (size1 * (length1 + 4) * density1);
			int itemWidth1 = (int) (length1 * density1);

			LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(gridviewWidth1,
					LinearLayout.LayoutParams.FILL_PARENT);
			gridView2.setLayoutParams(params1); // 重点
			gridView2.setColumnWidth(itemWidth1); // 重点
			gridView2.setHorizontalSpacing(5); // 间距
			gridView2.setStretchMode(GridView.NO_STRETCH);
			gridView2.setNumColumns(size1); // 重点

			simpleAdapter2 = new SimpleAdapter(this, imageItem2, R.layout.griditem_addpic, new String[] { "itemImage" },
					new int[] { R.id.imageView1 });
			simpleAdapter2.setViewBinder(new ViewBinder() {
				@Override
				public boolean setViewValue(View view, Object data, String textRepresentation) {
					if (view instanceof ImageView && data instanceof Bitmap) {
						ImageView i = (ImageView) view;
						i.setImageBitmap((Bitmap) data);
						return true;
					}
					return false;
				}
			});
			gridView2.setAdapter(simpleAdapter2);
			simpleAdapter2.notifyDataSetChanged();
			// 刷新后释放防止手机休眠后自动添加
			pathImage2 = null;
		}
	}

	/*
	 * Dialog对话框提示用户删除操作 position为删除图片位置
	 */
	private void dialog(final int position) {
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setCancelable(false);
		dialog.show();
		Window window = dialog.getWindow();
		// 设置窗口的内容页面,dialog_order_prompt.xml文件中定义view内容
		window.setContentView(R.layout.dialog_delete_prompt);

		Cancel = (Button) window.findViewById(R.id.cancel);// 取消
		Enter = (Button) window.findViewById(R.id.enter);// 确定
		Enter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				imageItem1.remove(position);
				simpleAdapter1.notifyDataSetChanged();
			}
		});
		Cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	/*
	 * Dialog对话框提示用户删除操作 position为删除图片位置
	 */
	private void dialog1(final int position) {
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setCancelable(false);
		dialog.show();
		Window window = dialog.getWindow();
		// 设置窗口的内容页面,dialog_order_prompt.xml文件中定义view内容
		window.setContentView(R.layout.dialog_delete_prompt);

		Cancel = (Button) window.findViewById(R.id.cancel);// 取消
		Enter = (Button) window.findViewById(R.id.enter);// 确定
		Enter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				imageItem2.remove(position);
				simpleAdapter2.notifyDataSetChanged();
			}
		});
		Cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	private void commit() {

		String name = Name.getText().toString().trim(); // 名字

		if (name.isEmpty()) {
			Toast.makeText(QualificationActivity.this, "姓名不能为空!", 0).show();
		} else {

			if (name.length() < 2 || name.length() > 7) {
				Toast.makeText(QualificationActivity.this, "姓名应在2-6个字符之间!", 0).show();
			} else {
                
				if(imageItem1.size() < 2 || imageItem2.size() < 2){
					Toast.makeText(QualificationActivity.this, "最少上传1张图片", 0).show();
				}else{
					
				JSONObject obj = new JSONObject();
				try {
					obj.put("servicerid", SaveUserInfo.getInstance(QualificationActivity.this).getUserInfo("id"));
					obj.put("name", name);
					obj.put("edupic", ImageArray1);
					obj.put("cerpic", ImageArray2);

				} catch (JSONException e) {
					e.printStackTrace();
				}

				Log.i("ganquan", "上传的参数" + obj);

				JsonObjectRequest request = new JsonObjectRequest(Method.POST, Constant.URL_Qualification, obj,
						new Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject o) {
								try {
									JSONObject json = new JSONObject(o.toString());
									String msg = json.getString("msg");
									Toast.makeText(QualificationActivity.this, msg, 0).show();
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
