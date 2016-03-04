package com.fding.activity;

import com.wind4app.wind4app2.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 消息详情页面
 *
 */
public class MessageDetailActivity extends Activity {

	// 返回按钮
	private ImageView Ic_back;

	private TextView Title, CreateTime, Content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_detail);

		// 返回上一页
		Ic_back = (ImageView) findViewById(R.id.ic_back);
		Ic_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		Title = (TextView) findViewById(R.id.title);
		CreateTime = (TextView) findViewById(R.id.createtime);
		Content = (TextView) findViewById(R.id.content);

		Intent i = getIntent();
		Title.setText(i.getStringExtra("title"));
		CreateTime.setText(i.getStringExtra("createTime"));
		Content.setText(i.getStringExtra("content"));

	}

}
