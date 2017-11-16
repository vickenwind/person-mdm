package com.forsuntech.prisonmdm.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.forsuntech.prisonmdm.MainActivity;
import com.forsuntech.prisonmdm.policy.ExecPolicy;
import com.forsuntech.prisonmdm.receiver.SimStateReceive;
import com.forsuntech.prisonmdm.utils.ModeManager;
import com.forsuntech.prisonmdm.utils.MyApplication;

import java.lang.reflect.Method;

/**
 * Created by vicken on 2017/10/16.
 */

public class AutoStartService extends Service{
    private final static String TAG=AutoStartService.class.getName();
    SimStateReceive simReceiver=null;
//    WifiThread wifiThread=null;
    private ExecPolicy execPolicy=null;
    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG,"初始化主服务");

        //启动禁止SIM卡换卡功能
        simReceiver=new SimStateReceive();
        simReceiver.register();

//        wifiThread=new WifiThread();
//        wifiThread.start();

        execPolicy=new ExecPolicy();
        execPolicy.exePolicy();

        Intent intent=new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getContextObject().startActivity(intent);
        //初始化工作模式
        ModeManager.initWorking();

        //强制开启WIFI
        enableForceWifi(1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(simReceiver!=null){
            simReceiver.unRegister();
        }

    }

     /* set setWifiForceEnabled
         * mode 传1: force enable wifi         * mode 传0 : dont force enable wifi
       	 */
    private void enableForceWifi(int mode){
        Method method = null;

        try {
            Class c = Class.forName("android.util.AmigoUtil");
            Object r = c.newInstance();
            method = c.getDeclaredMethod("setWifiForceEnabled", Context.class,int.class);
            method.invoke(r, MyApplication.getContextObject(), mode );
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("wanggj", "exception occur");
            Log.e("wanggj", Log.getStackTraceString(e));
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
