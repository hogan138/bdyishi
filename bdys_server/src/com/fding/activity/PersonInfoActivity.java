package com.fding.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
import com.fding.activity.utils.Tools;
import com.fding.activity.widget.CircleImageView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wind4app.wind4app2.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 个人资料
 *
 */

public class PersonInfoActivity extends Activity implements OnClickListener {

	// 返回按钮
	private ImageView Ic_back;

	private RelativeLayout Update_Phone, RealName, Qualification;
	private CircleImageView Photo;
	private TextView Name, Phone, Name_status, Qf_status;

	// 修改联系电话
	private EditText edPhone;
	private Button upEnter, upCancel;

	// 头像弹出框
	private Button Gallery, Camera, Cancel;// 图库,相机,取消

	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;

	// 上传图片名称
	private String tmpImage = Environment.getExternalStorageDirectory() + "/temp.jpg";

	// 加载网络图片
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_info);

		// 返回上一页
		Ic_back = (ImageView) findViewById(R.id.ic_back);
		Ic_back.setOnClickListener(this);

		Name = (TextView) findViewById(R.id.info_name);
		Phone = (TextView) findViewById(R.id.info_phone);
		Update_Phone = (RelativeLayout) findViewById(R.id.updatePhone);
		Update_Phone.setOnClickListener(this);
		Photo = (CircleImageView) findViewById(R.id.info_photo);
		Photo.setOnClickListener(this);
		RealName = (RelativeLayout) findViewById(R.id.realName);
		RealName.setOnClickListener(this);
		Qualification = (RelativeLayout) findViewById(R.id.qualification);
		Qualification.setOnClickListener(this);
		Name_status = (TextView) findViewById(R.id.name_status);
		Qf_status = (TextView) findViewById(R.id.qf_status);

		// 获取用户信息
		getUserInfo();
	}

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
							Log.i("request", "获取个人信息" + json);
							JSONObject jb = new JSONObject(json.getString("result"));
							JSONObject j = new JSONObject(jb.getString("servicer"));
							Name.setText(j.getString("name"));
							Phone.setText(j.getString("phone"));

							int istatus = jb.getInt("istatus"); // 实名认证状态
							int qstatus = jb.getInt("qstatus"); // 资格认证状态

							// 实名认证状态
							if (istatus == 1) {
								Name_status.setText("未认证");
							} else if (istatus == 2) {
								Name_status.setText("审核中");
								RealName.setClickable(false);
							} else if (istatus == 3) {
								Name_status.setText("已认证");
								RealName.setClickable(false);
							} else if (istatus == 4) {
								Name_status.setText("未通过");
							}

							// 资格认证状态
							if (qstatus == 1) {
								Qf_status.setText("未认证");
							} else if (qstatus == 2) {
								Qf_status.setText("审核中");
								Qualification.setClickable(false);
							} else if (qstatus == 3) {
								Qf_status.setText("已认证");
								Qualification.setClickable(false);
							} else if (qstatus == 4) {
								Qf_status.setText("未通过");
							}

							// 保存用户信息
							saveInfoInstance(j);

						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						Toast.makeText(getApplicationContext(), "请检查您的网络连接！", 0).show();
						Name.setText(SaveUserInfo.getInstance(getApplicationContext()).getUserInfo("name"));
						Phone.setText(SaveUserInfo.getInstance(getApplicationContext()).getUserInfo("phone"));
					}
				});

		MyApplication.getHttpQueues().add(request);
	}

	// 保存用户信息
	private void saveInfoInstance(JSONObject j) {

		try {
			String phone = j.getString("phone");
			String status = j.getString("status");
			String type = j.getString("type");
			String password = j.getString("password");
			String id = j.getString("id");
			String qualification = j.getString("qualification");
			String lastLogin = j.getString("lastLogin");
			String token = j.getString("token");
			String identification = j.getString("identification");
			String name = j.getString("name");
			String account = j.getString("account");
			String longitude = j.getString("longitude");
			String creationTime = j.getString("creationTime");
			String latitude = j.getString("latitude");
			String rating = j.getString("rating");

			SaveUserInfo.getInstance(PersonInfoActivity.this).setUserInfo("phone", phone);
			SaveUserInfo.getInstance(PersonInfoActivity.this).setUserInfo("status", status);
			SaveUserInfo.getInstance(PersonInfoActivity.this).setUserInfo("type", type);
			SaveUserInfo.getInstance(PersonInfoActivity.this).setUserInfo("password", password);
			SaveUserInfo.getInstance(PersonInfoActivity.this).setUserInfo("id", id);
			SaveUserInfo.getInstance(PersonInfoActivity.this).setUserInfo("qualification", qualification);
			SaveUserInfo.getInstance(PersonInfoActivity.this).setUserInfo("lastLogin", lastLogin);
			SaveUserInfo.getInstance(PersonInfoActivity.this).setUserInfo("token", token);
			SaveUserInfo.getInstance(PersonInfoActivity.this).setUserInfo("identification", identification);
			SaveUserInfo.getInstance(PersonInfoActivity.this).setUserInfo("name", name);
			SaveUserInfo.getInstance(PersonInfoActivity.this).setUserInfo("account", account);
			SaveUserInfo.getInstance(PersonInfoActivity.this).setUserInfo("longitude", longitude);
			SaveUserInfo.getInstance(PersonInfoActivity.this).setUserInfo("creationTime", creationTime);
			SaveUserInfo.getInstance(PersonInfoActivity.this).setUserInfo("latitude", latitude);
			SaveUserInfo.getInstance(PersonInfoActivity.this).setUserInfo("rating", rating);

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.info_photo:
			showPhotoDialog();
			break;
		case R.id.ic_back:
			finish();
			break;
		case R.id.updatePhone:
			UpdatePhoneDialog();
			break;
		case R.id.realName:
			startActivity(new Intent(getApplication(), RealNameActivity.class));
			break;
		case R.id.qualification:
			startActivity(new Intent(getApplication(), QualificationActivity.class));
			break;
		}
	}

	// 修改手机号Dialog
	private void UpdatePhoneDialog() {

		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setCancelable(false);
		dialog.show();
		Window window = dialog.getWindow();
		// 设置窗口的内容页面,dialog_order_prompt.xml文件中定义view内容
		window.setContentView(R.layout.dialog_phone);
		// 设置弹出输入框
		dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

		edPhone = (EditText) window.findViewById(R.id.edit_userinfo_phone);

		upCancel = (Button) window.findViewById(R.id.btn_dlog_cancel);// 取消
		upEnter = (Button) window.findViewById(R.id.btn_dlog_ok);// 确定
		upEnter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String myphone = edPhone.getText().toString().trim();

				if (myphone == null || myphone.equals("")) {
					Toast.makeText(PersonInfoActivity.this, "联系电话不能输入为空！", 0).show();
				} else {
					JSONObject obj = new JSONObject();
					try {
						obj.put("servicerid", SaveUserInfo.getInstance(getApplicationContext()).getUserInfo("id"));
						obj.put("phone", myphone);
					} catch (JSONException e) {
						e.printStackTrace();
					}

					JsonObjectRequest Prequest = new JsonObjectRequest(Method.POST, Constant.URL_UpdateUserInfo, obj,
							new Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject j) {
							try {
								JSONObject json = new JSONObject(j.toString());
								JSONObject jb = new JSONObject(json.getString("result"));
								JSONObject je = new JSONObject(jb.getString("servicer"));
								String msg = json.getString("msg");
								Log.i("request", "" + json);
								int status = json.getInt("status");
								if (status == 0) {
									Toast.makeText(getApplicationContext(), msg, 0).show();
								} else if (status == 1) {
									Phone.setText(myphone);
									Toast.makeText(getApplicationContext(), msg, 0).show();
									// 保存手机号到本地
									SaveUserInfo.getInstance(getApplicationContext()).setUserInfo("phone", myphone);
									dialog.dismiss();
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

					MyApplication.getHttpQueues().add(Prequest);

				}

			}
		});

		upCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	// 弹出头像选择框
	private void showPhotoDialog() {
		View view = getLayoutInflater().inflate(R.layout.photo_choose_dialog, null);
		final Dialog dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
		dialog.setContentView(view, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		Window window = dialog.getWindow();
		// 设置显示动画
		window.setWindowAnimations(R.style.main_menu_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.x = 0;
		wl.y = getWindowManager().getDefaultDisplay().getHeight();
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		// 设置显示位置
		dialog.onWindowAttributesChanged(wl);
		// 设置点击外围解散
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();

		// 从图库选择文件
		Gallery = (Button) window.findViewById(R.id.my_gallery);
		Gallery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentFromGallery = new Intent();
				intentFromGallery.setType("image/*"); // 设置文件类型
				intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
				dialog.dismiss();
			}
		});

		// 相机拍摄
		Camera = (Button) window.findViewById(R.id.my_camera);
		Camera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// 判断存储卡是否可以用，可用进行存储
				if (Tools.hasSdcard()) {
					intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(new File(Environment.getExternalStorageDirectory(), tmpImage)));
					startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
					dialog.dismiss();
				}

			}
		});

		// 取消
		Cancel = (Button) window.findViewById(R.id.my_cancel);
		Cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (Tools.hasSdcard()) {
					File tempFile = new File(Environment.getExternalStorageDirectory() + "/" + tmpImage);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(PersonInfoActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_LONG).show();
				}
				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					getImageToView(data);

				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}

	// 将进行剪裁后的图片显示到UI界面上
	private void getImageToView(Intent picdata) {
		Bundle estras = picdata.getExtras();
		if (estras != null) {
			final Bitmap photo = estras.getParcelable("data");
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);

			// 保存图片到sd卡
			savePicToSdcard(photo, tmpImage);

			// 上传图片
			uploadImage();

		}
	}

	private String savePicToSdcard(Bitmap bitmap, String filePath) {
		if (bitmap == null) {
			return filePath;
		} else {
			File destFile = new File(filePath);
			OutputStream os = null;
			try {
				os = new FileOutputStream(destFile);
				// bitmap=getRCB(bitmap, 10);
				bitmap.compress(CompressFormat.JPEG, 100, os);
				os.flush();
				os.close();
			} catch (IOException e) {
				filePath = "";
			}
		}
		return filePath;
	}

	private void uploadImage() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("file", new File(tmpImage));
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

					// 加载网络图片
					ImageLoader.getInstance().displayImage(path, Photo);

					// 修改个人头像
					updatePhoto(path);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		});

	}

	// 修改头像
	private void updatePhoto(final String path) {

		// String p = path;
		JSONObject obj = new JSONObject();
		try {
			obj.put("servicerid", SaveUserInfo.getInstance(getApplicationContext()).getUserInfo("id"));
			obj.put("icon", path);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Log.i("get", "头像参数" + obj);

		JsonObjectRequest request = new JsonObjectRequest(Method.POST, Constant.URL_UpdateUserInfo, obj,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject j) {
						try {
							JSONObject json = new JSONObject(j.toString());
							String msg = json.getString("msg");
							Toast.makeText(getApplicationContext(), msg, 0).show();

							// 保存头像到本地
							SaveUserInfo.getInstance(getApplicationContext()).setUserInfo("icon", path);

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

	@Override
	protected void onResume() {

		// 获取用户信息
		getUserInfo();

		// 加载网络图片
		if (SaveUserInfo.getInstance(getApplicationContext()).getUserInfo("icon").equals("")) {
		} else {
			ImageLoader.getInstance()
					.displayImage(SaveUserInfo.getInstance(getApplicationContext()).getUserInfo("icon"), Photo);
		}

		super.onResume();
	}

}
