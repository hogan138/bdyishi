package com.fding.activity;

import com.wind4app.wind4app2.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

/**
 * 用户协议
 *
 */
public class UseAgreementActivity extends Activity {

	// 返回按钮
	private ImageView Ic_back;
	private WebView wView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_use_agreement);

		// 返回上一页
		Ic_back = (ImageView) findViewById(R.id.ic_back);
		Ic_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		wView = (WebView) findViewById(R.id.wView);

		WebSettings wb = wView.getSettings();

		// 设置编码格式
		wb.setDefaultTextEncodingName("utf-8");

		// 找到Html文件，也可以用网络上的文件
		wView.loadUrl("http://121.41.117.5:8080/bdys/html/UserAgreement.html");
	}

}
