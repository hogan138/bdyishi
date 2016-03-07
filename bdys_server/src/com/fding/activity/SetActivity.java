package com.fding.activity;

import com.wind4app.wind4app2.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 设置
 *
 */

public class SetActivity extends Activity implements OnClickListener {

	private RelativeLayout About, Suggestion;

	// 返回按钮
	private ImageView Ic_back;

	// 分享功能
	// final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
	// {
	// SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE
	//// SHARE_MEDIA.SINA,SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,SHARE_MEDIA.DOUBAN
	// };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set);

		// 反馈
		About = (RelativeLayout) findViewById(R.id.about);
		About.setOnClickListener(this);
		Suggestion = (RelativeLayout) findViewById(R.id.suggestion);
		Suggestion.setOnClickListener(this);
		// 返回上一页
		Ic_back = (ImageView) findViewById(R.id.ic_back);
		Ic_back.setOnClickListener(this);

	}

	// private UMShareListener umShareListener = new UMShareListener() {
	// @Override
	// public void onResult(SHARE_MEDIA platform) {
	// Toast.makeText(getApplicationContext(), platform + " 分享成功啦",
	// Toast.LENGTH_SHORT).show();
	// }
	//
	// @Override
	// public void onError(SHARE_MEDIA platform, Throwable t) {
	// Toast.makeText(getApplicationContext(),platform + " 分享失败啦",
	// Toast.LENGTH_SHORT).show();
	// }
	//
	// @Override
	// public void onCancel(SHARE_MEDIA platform) {
	// Toast.makeText(getApplicationContext(),platform + " 分享取消了",
	// Toast.LENGTH_SHORT).show();
	// }
	// };
	//
	// private ShareBoardlistener shareBoardlistener = new ShareBoardlistener()
	// {
	//
	// @Override
	// public void onclick(SnsPlatform snsPlatform,SHARE_MEDIA share_media) {
	// new ShareAction((Activity)
	// getApplicationContext()).setPlatform(share_media).setCallback(umShareListener)
	// .withText("多平台分享")
	// .share();
	// }
	// };
	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// super.onActivityResult(requestCode, resultCode, data);
	// /** attention to this below ,must add this**/
	// UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
	//
	// }
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ic_back:
			finish();
			break;
		case R.id.about:
			// UMImage image = new
			// UMImage(getApplicationContext(),BitmapFactory.decodeResource(getResources(),
			// R.drawable.icon_launch));
			// new ShareAction(this).setDisplayList( displaylist )
			//// .withText( "呵呵" )
			//// .withTitle("title")
			//// .withTargetUrl("http://www.baidu.com")
			//// .withMedia(image)
			// .setListenerList(umShareListener,umShareListener)
			// .setShareboardclickCallback(shareBoardlistener)
			// .open();
			//
			// if(displaylist.equals(SHARE_MEDIA.WEIXIN)){ //微信
			// new
			// ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN).setCallback(umShareListener)
			// .withText("hello wx")
			// .withMedia(image)
			// .share();
			// }else if(displaylist.equals(SHARE_MEDIA.WEIXIN_CIRCLE)){ //微信朋友圈
			// new
			// ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE).setCallback(umShareListener)
			// .withText("hello umeng")
			// .withMedia(image)
			// .share();
			// }

			break;
		case R.id.suggestion:
			startActivity(new Intent(getApplicationContext(), SuggestionActivity.class));
			break;
		}
	}

}
