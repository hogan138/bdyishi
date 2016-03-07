package com.fding.activity.server;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 保存个人信息
 *
 */
public class SaveUserInfo {

	public static final String PREFERENCE_NAME = "server_info";
	private static SharedPreferences mSharedPreferences;
	private static SaveUserInfo mPreferenceUtils;
	private static SharedPreferences.Editor editor;

	private SaveUserInfo(Context cxt) {
		mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
	}

	public static SaveUserInfo getInstance(Context cxt) {
		if (mPreferenceUtils == null) {
			mPreferenceUtils = new SaveUserInfo(cxt);
		}
		editor = mSharedPreferences.edit();
		return mPreferenceUtils;
	}

	public void setUserInfo(String str_name, String str_value) {

		editor.putString(str_name, str_value);
		editor.commit();
	}

	public String getUserInfo(String str_name) {

		return mSharedPreferences.getString(str_name, "");

	}

}
