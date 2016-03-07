package com.fding.activity;

import com.wind4app.wind4app2.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * 欢迎页
 *
 */
public class SplashActivity extends Activity {
	private final int SPLASH_DISPLAY_LENGHT = 2000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent Intent = new Intent(SplashActivity.this, GifActivity.class);
				SplashActivity.this.startActivity(Intent);
				SplashActivity.this.finish();
			}
		}, SPLASH_DISPLAY_LENGHT);
	}
}
