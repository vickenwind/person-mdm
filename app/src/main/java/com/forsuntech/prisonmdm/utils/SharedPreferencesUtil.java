package com.forsuntech.prisonmdm.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtil {
	private static final String FILE_NAME = "mdm.com";
	private static final int MODE = Context.MODE_PRIVATE;
	public static SharedPreferences preferences=MyApplication.getContextObject().getSharedPreferences(FILE_NAME,MODE);


	public static final String SIM_SIMEI="simei";
	public static final String IS_WORK_MODE="workmode";
	public static final String IS_LOCK="islock";
	public static final String PKG_LIST="pkglist";


	public static final String POLICY_MESSAGE="policymessage";
	public static final String POLICY_PHONE="policyphone";
	public static final String POLICY_TAG="policytag";
	public static final String POLICY_SSID="policyssid";


	public static String getSharedByKey(String key){
//		preferences = MyContext.getContextObject().getSharedPreferences("secphone.com", MyContext.getContextObject().MODE_WORLD_READABLE);
		return preferences.getString(key, "");
	}


	public static String getString(String key, String defValue) {
		return preferences.getString(key, defValue);
	}

	public static boolean getBoolean(String key, Boolean defValue) {
		return preferences.getBoolean(key, defValue);
	}



	public static void putString(String key, String value) {
		Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static void putBoolean(String key, Boolean value) {
		Editor editor = preferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
}
