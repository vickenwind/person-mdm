package com.forsuntech.prisonmdm.utils;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application{

	 private static Context context; 
	
	    @Override  
	    public void onCreate() {  
	        //��ȡContext  
	    	super.onCreate();
	        context = getApplicationContext();
	    }
	    //获取全局Context 
	    public static Context getContextObject(){  
	        return context;  
	    } 	    
}
