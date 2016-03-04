package com.fding.activity.server;

import com.android.volley.RequestQueue;

import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.socialize.PlatformConfig;

import android.app.Application;
import android.content.Context;

/*
 *全局消息队列（Volley） 
 */
public class MyApplication extends Application {

	public static RequestQueue queues;

	@Override
	public void onCreate() {
		super.onCreate();

		initImageLoader(getApplicationContext());
		queues = Volley.newRequestQueue(getApplicationContext());

		// 微信 appid appsecret
		PlatformConfig.setWeixin("wxc4099c4fde744b83", "41bf83c20f394f9c393ba35249339ea2");

	}

	public static RequestQueue getHttpQueues() {
		return queues;

	}

	// 图片加载
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.discCacheFileNameGenerator(new Md5FileNameGenerator());
		config.memoryCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.writeDebugLogs(); // Remove for release app

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config.build());
	}
}
