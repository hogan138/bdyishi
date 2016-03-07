package com.fding.activity.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 判断手机号是否正确
 * 
 */
public class PhoneNumber {

	public static boolean isMobile(String mobile) {

		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,0-9])|(17[0,5-9]))\\d{8}$");

		Matcher m = p.matcher(mobile);

		return m.matches();

	}
}
