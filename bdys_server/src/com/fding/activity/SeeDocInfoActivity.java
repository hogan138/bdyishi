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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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
 * 填写就诊资料(结束服务)
 *
 */

public class SeeDocInfoActivity extends Activity implements OnClickListener {

	// 返回按钮
	private ImageView Ic_back;

	// 进入提示框
	private Button Pass;

	// 上传限制按钮
	private ImageView Sel_enter;
	// 弹窗上传照片提示
	private Button Enter, Cancel;

	// 完成
	private Button Btn_finish;

	// 医嘱
	private EditText Advice;

	// 病历照
	private GridView gridView1;
	private final int IMAGE_OPEN1 = 1; // 打开图片标记
	private String pathImage1; // 选择图片路径
	private Bitmap bmp; // 导入临时图片
	private ArrayList<HashMap<String, Object>> imageItem1;
	private SimpleAdapter simpleAdapter1; // 适配器

	// 处方照
	private GridView gridView2;
	private final int IMAGE_OPEN2 = 2; // 打开图片标记
	private String pathImage2; // 选择图片路径
	private ArrayList<HashMap<String, Object>> imageItem2;
	private SimpleAdapter simpleAdapter2; // 适配器

	// 药品照
	private GridView gridView3;
	private final int IMAGE_OPEN3 = 3; // 打开图片标记
	private String pathImage3; // 选择图片路径
	private ArrayList<HashMap<String, Object>> imageItem3;
	private SimpleAdapter simpleAdapter3; // 适配器

	// 上传图片
	private JSONArray ImageArray1 = new JSONArray();
	private JSONArray ImageArray2 = new JSONArray();
	private JSONArray ImageArray3 = new JSONArray();

	private int select;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_see_doc_info);

		// 医嘱
		Advice = (EditText) findViewById(R.id.et_content);

		// 提交
		Btn_finish = (Button) findViewById(R.id.btn_commit);
		Btn_finish.setOnClickListener(this);

		// 选择按钮
		Sel_enter = (ImageView) findViewById(R.id.sel_enter);
		Sel_enter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				chooseDialog();
			}
		});

		// 返回上一页
		Ic_back = (ImageView) findViewById(R.id.ic_back);
		Ic_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ExitDialog();
			}
		});

		// 图片选择
		initPhoto();

	}

	// 初始化图片
	private void initPhoto() {

		// 获取控件对象（病历照）
		gridView1 = (GridView) findViewById(R.id.gridView1);

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

		/*
		 * 监听GridView点击事件 报错:该函数必须抽象方法 故需要手动导入import android.view.View;
		 */
		gridView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				if (imageItem1.size() == 10 & position == 0) { // 第一张为默认图片
					Toast.makeText(getApplicationContext(), "图片数9张已满", Toast.LENGTH_SHORT).show();
				} else if (position == 0) { // 点击图片位置为+ 0对应0张图片
					Toast.makeText(getApplicationContext(), "添加图片", Toast.LENGTH_SHORT).show();
					// 选择图片
					Intent intent = new Intent(Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, IMAGE_OPEN1);
					// 通过onResume()刷新数据
				} else {
					dialog1(position);
					// Toast.makeText(MainActivity.this, "点击第" + (position + 1)
					// + " 号图片",
					// Toast.LENGTH_SHORT).show();
				}

			}
		});

		// 获取控件对象（处方照）
		gridView2 = (GridView) findViewById(R.id.gridView2);
		/*
		 * 载入默认图片添加图片加号 通过适配器实现 SimpleAdapter参数imageItem为数据源
		 * R.layout.griditem_addpic为布局
		 */
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.add_photo); // 加号
		imageItem2 = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map2 = new HashMap<String, Object>();
		map2.put("itemImage", bmp);
		imageItem2.add(map2);

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
				// TODO Auto-generated method stub
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
					Toast.makeText(getApplicationContext(), "图片数9张已满", Toast.LENGTH_SHORT).show();
				} else if (position == 0) { // 点击图片位置为+ 0对应0张图片
					Toast.makeText(getApplicationContext(), "添加图片", Toast.LENGTH_SHORT).show();
					// 选择图片
					Intent intent = new Intent(Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, IMAGE_OPEN2);
					// 通过onResume()刷新数据
				} else {
					dialog2(position);
					// Toast.makeText(MainActivity.this, "点击第" + (position + 1)
					// + " 号图片",
					// Toast.LENGTH_SHORT).show();
				}

			}
		});

		// 获取控件对象（药品照）
		gridView3 = (GridView) findViewById(R.id.gridView3);
		/*
		 * 载入默认图片添加图片加号 通过适配器实现 SimpleAdapter参数imageItem为数据源
		 * R.layout.griditem_addpic为布局
		 */
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.add_photo); // 加号
		imageItem3 = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map3 = new HashMap<String, Object>();
		map3.put("itemImage", bmp);
		imageItem3.add(map3);

		int size2 = imageItem3.size();

		int length2 = 80;

		DisplayMetrics dm2 = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm2);
		float density2 = dm2.density;
		int gridviewWidth2 = (int) (size2 * (length2 + 4) * density2);
		int itemWidth2 = (int) (length2 * density2);

		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(gridviewWidth2,
				LinearLayout.LayoutParams.FILL_PARENT);
		gridView3.setLayoutParams(params2); // 重点
		gridView3.setColumnWidth(itemWidth2); // 重点
		gridView3.setHorizontalSpacing(5); // 间距
		gridView3.setStretchMode(GridView.NO_STRETCH);
		gridView3.setNumColumns(size2); // 重点

		simpleAdapter3 = new SimpleAdapter(this, imageItem3, R.layout.griditem_addpic, new String[] { "itemImage" },
				new int[] { R.id.imageView1 });
		/*
		 * HashMap载入bmp图片在GridView中不显示,但是如果载入资源ID能显示 如 map.put("itemImage",
		 * R.drawable.img); 解决方法: 1.自定义继承BaseAdapter实现 2.ViewBinder()接口实现 参考
		 * http://blog.csdn.net/admin_/article/details/7257901
		 */
		simpleAdapter3.setViewBinder(new ViewBinder() {
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
		gridView3.setAdapter(simpleAdapter3);

		/*
		 * 监听GridView点击事件 报错:该函数必须抽象方法 故需要手动导入import android.view.View;
		 */
		gridView3.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				if (imageItem3.size() == 10 & position == 0) { // 第一张为默认图片
					Toast.makeText(getApplicationContext(), "图片数9张已满", Toast.LENGTH_SHORT).show();
				} else if (position == 0) { // 点击图片位置为+ 0对应0张图片
					Toast.makeText(getApplicationContext(), "添加图片", Toast.LENGTH_SHORT).show();
					// 选择图片
					Intent intent = new Intent(Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, IMAGE_OPEN3);
					// 通过onResume()刷新数据
				} else {
					dialog3(position);
					// Toast.makeText(MainActivity.this, "点击第" + (position + 1)
					// + " 号图片",
					// Toast.LENGTH_SHORT).show();
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
		} // end if 打开图片

		if (resultCode == RESULT_OK && requestCode == IMAGE_OPEN3) {
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
				pathImage3 = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

				// 上传图片到服务器
				RequestParams params = new RequestParams();
				params.addBodyParameter("file", new File(pathImage3));
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
							ImageArray3.put(path);

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

				});

			}
		} // end if 打开图片
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
					// TODO Auto-generated method stub
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

		if (!TextUtils.isEmpty(pathImage3)) {
			Bitmap addbmp = BitmapFactory.decodeFile(pathImage3);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", addbmp);
			imageItem3.add(map);

			int size2 = imageItem3.size();

			int length2 = 80;

			DisplayMetrics dm2 = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm2);
			float density2 = dm2.density;
			int gridviewWidth2 = (int) (size2 * (length2 + 4) * density2);
			int itemWidth2 = (int) (length2 * density2);

			LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(gridviewWidth2,
					LinearLayout.LayoutParams.FILL_PARENT);
			gridView3.setLayoutParams(params2); // 重点
			gridView3.setColumnWidth(itemWidth2); // 重点
			gridView3.setHorizontalSpacing(5); // 间距
			gridView3.setStretchMode(GridView.NO_STRETCH);
			gridView3.setNumColumns(size2); // 重点

			simpleAdapter3 = new SimpleAdapter(this, imageItem3, R.layout.griditem_addpic, new String[] { "itemImage" },
					new int[] { R.id.imageView1 });
			simpleAdapter3.setViewBinder(new ViewBinder() {
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
			gridView3.setAdapter(simpleAdapter3);
			simpleAdapter3.notifyDataSetChanged();
			// 刷新后释放防止手机休眠后自动添加
			pathImage3 = null;
		}
	}

	/*
	 * Dialog对话框提示用户删除操作 position为删除图片位置
	 */
	protected void dialog1(final int position) {
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
	protected void dialog2(final int position) {
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

	/*
	 * Dialog对话框提示用户删除操作 position为删除图片位置
	 */
	protected void dialog3(final int position) {
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
				imageItem3.remove(position);
				simpleAdapter3.notifyDataSetChanged();
			}
		});
		Cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

	}

	// 拒绝用户上传照片Dialog
	private void chooseDialog() {
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setCancelable(false);
		dialog.show();
		Window window = dialog.getWindow();
		// 设置窗口的内容页面,dialog_order_prompt.xml文件中定义view内容
		window.setContentView(R.layout.dialog_photo_prompt);

		Cancel = (Button) window.findViewById(R.id.dialog_cancel);// 取消
		Enter = (Button) window.findViewById(R.id.dialog_complain);// 确定
		Enter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Sel_enter.setBackgroundResource(R.drawable.doc_selected);
				select = 1;
				dialog.dismiss();
			}
		});
		Cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Sel_enter.setBackgroundResource(R.drawable.unselected);
				select = 0;
				dialog.dismiss();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_commit: // 提交
			// 提示框
			promptDialog();
			break;
		}

	}

	// 提示Dialog
	private void promptDialog() {
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setCancelable(false);
		dialog.show();
		Window window = dialog.getWindow();
		// 设置窗口的内容页面,dialog_order_prompt.xml文件中定义view内容
		window.setContentView(R.layout.dialog_prompt);

		Pass = (Button) window.findViewById(R.id.pass);
		Pass.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				commit();
			}
		});

	}

	// 提交
	private void commit() {
		// 获得传来的数据
		Intent intent = getIntent();
		String id = intent.getStringExtra("id");
		int userId = intent.getExtras().getInt("userId");
		String startTime = intent.getStringExtra("startTime");
		String patientName = intent.getStringExtra("patientName");
		String hospitalName = intent.getStringExtra("hospitalName");
		Log.i("get", userId + startTime + patientName + hospitalName + id);

		JSONObject obj = new JSONObject();
		try {
			obj.put("servicerid", SaveUserInfo.getInstance(getApplicationContext()).getUserInfo("id"));
			obj.put("orderid", id);
			obj.put("fromtype", 2);
			obj.put("userid", userId);
			obj.put("visitingtime", startTime);
			obj.put("patientname", patientName);
			obj.put("hospitalname", hospitalName);
			obj.put("advice", Advice.getText().toString());
			obj.put("caspic", ImageArray1);
			obj.put("prepic", ImageArray2);
			obj.put("drugpic", ImageArray3);

			if (select == 1) {
				obj.put("refusesub", 4);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		Log.i("ganquan", "健康档案上传参数" + obj);

		JsonObjectRequest request = new JsonObjectRequest(Method.POST, Constant.URL_Healthrecord, obj,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject j) {
						try {
							JSONObject json = new JSONObject(j.toString());
							String msg = json.getString("msg");
							int status = json.getInt("status");
							if (status == 0) {
								Toast.makeText(getApplicationContext(), msg, 0).show();
							} else if (status == 1) {
								// showSucessDialog();
								Toast.makeText(getApplicationContext(), "健康档案上传成功", 0).show();
								finish();
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

	}

	// 监听返回键
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			ExitDialog();

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// 退出填写健康档案Dialog
	private void ExitDialog() {
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setCancelable(false);
		dialog.show();
		Window window = dialog.getWindow();
		// 设置窗口的内容页面,dialog_order_prompt.xml文件中定义view内容
		window.setContentView(R.layout.dialog_exit2);

		Cancel = (Button) window.findViewById(R.id.dialog_cancel);// 取消
		Enter = (Button) window.findViewById(R.id.dialog_enter);// 确定
		Enter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		Cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	// 成功dialog
	// private void showSucessDialog() {
	// final AlertDialog adlog = new AlertDialog.Builder(this).create();
	// adlog.setCancelable(false);
	// adlog.show();
	// Window window = adlog.getWindow();
	// window.setContentView(R.layout.dialog_seedoc_success);
	// }

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
