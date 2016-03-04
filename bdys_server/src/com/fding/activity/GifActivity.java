package com.fding.activity;

import com.fding.activity.server.SaveUserInfo;
import com.wind4app.wind4app2.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/**
 * gif启动页
 *
 */

public class GifActivity extends Activity {
	private final int SPLASH_DISPLAY_LENGHT = 3500;
	private GifView gif1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gif);

		gif1 = (GifView) findViewById(R.id.gif1);
		gif1.setMovieResource(R.raw.splash);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				String account = SaveUserInfo.getInstance(getApplicationContext()).getUserInfo("account");
				String phone = SaveUserInfo.getInstance(getApplicationContext()).getUserInfo("phone");
				Log.i("ganquan", "登录号是" + account + "手机号是：" + phone);

				if (account != null && account.length() != 0 && phone != null && phone.length() != 0) {
					Intent Intent = new Intent(GifActivity.this, OrderActivity.class);
					GifActivity.this.startActivity(Intent);
					GifActivity.this.finish();
				} else {
					Intent Intent = new Intent(GifActivity.this, LoginActivity.class);
					GifActivity.this.startActivity(Intent);
					GifActivity.this.finish();
				}

			}
		}, SPLASH_DISPLAY_LENGHT);
	}

}
