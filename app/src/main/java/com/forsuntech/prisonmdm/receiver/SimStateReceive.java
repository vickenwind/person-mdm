package com.forsuntech.prisonmdm.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.forsuntech.prisonmdm.utils.DeviceManager;
import com.forsuntech.prisonmdm.utils.MyApplication;
import com.forsuntech.prisonmdm.utils.SharedPreferencesUtil;

import java.lang.reflect.Method;

/**
 * Created by vicken on 2017/10/15.
 */

public class SimStateReceive  {
    private static final String TAG=SimStateReceive.class.getName();
    private final static String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";
    private Context context;
    private DeviceManager deviceManager;

    public SimStateReceive(){
        context= MyApplication.getContextObject();
        deviceManager=new DeviceManager();
    }

    /**
     * 注册SIM卡广播
     */
    public void register(){
        context.registerReceiver(mReceiver,makeFilter());
    }

    /**
     *解除SIM卡广播
     */
    public void unRegister(){
        context.registerReceiver(mReceiver,makeFilter());
        context.unregisterReceiver(mReceiver);
    }


    private IntentFilter makeFilter(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SIM_STATE_CHANGED");
//        filter.addDataScheme("SIMCHANGE");
        filter.setPriority(1000);
        return filter;
    }

    private String getICCID() {
        try {
            Method method = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
            return (String) method.invoke(null, "ril.iccid.sim1");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) { //大于等于Android 5.1.0 L版本
//
//            SubscriptionManager sub = (SubscriptionManager) MyApplication.getContextObject().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
//            List<SubscriptionInfo> info = sub.getActiveSubscriptionInfoList();
//            int count = sub.getActiveSubscriptionInfoCount();
//            if (count > 0) {
//                if (count > 1) {
//                    String icc1 = info.get(0).getIccId();
//                    String icc2 = info.get(1).getIccId();
//                    return icc1 + "," + icc2;
//                } else {
//                    for (SubscriptionInfo list : info) {
//                        String icc1 = list.getIccId();
//                        return icc1;
//                    }
//                }
//            } else {
//                Log.d("PhoneUtil", "无SIM卡");
//                return "";
//            }
//        }
//        return "";
    }


    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context _context, Intent intent) {
            if (intent.getAction().equals(ACTION_SIM_STATE_CHANGED)) {
//                ModeManager.gioneeWorkMode(0,null);
                Log.i(TAG,"SIM卡状态发生改变");
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
                if(tm==null){
                    Log.i(TAG,"TM为空");
                    return;
                }
                String currSimei ="";
                if(Build.BRAND.equals("GIONEE")){
                    currSimei=getICCID().trim();
                    Log.i(TAG,"ICCID"+currSimei);
                }else{
                    currSimei=tm.getSimSerialNumber();// 获得SIM卡的序号
                    Log.i(TAG,Build.BRAND+currSimei);
                }
                if(TextUtils.isEmpty(currSimei)){
                    return ;
                }
                if(currSimei.length()<5){
                    return ;
                }
                String saveSimei = SharedPreferencesUtil.getString(SharedPreferencesUtil.SIM_SIMEI, "");

                Log.i(TAG,"saveSimei"+saveSimei);
                if (!TextUtils.isEmpty(saveSimei)) {
                    if (!currSimei.equals(saveSimei)) {
                        if(SharedPreferencesUtil.getBoolean(SharedPreferencesUtil.IS_LOCK,false)){
                            Log.i(TAG,"密码已设定，无需重复设定");
                            return ;
                        }
                        //违反换卡策略，执行锁屏
                        Log.i(TAG,"违反换卡策略，执行锁屏");
                        deviceManager.lockPhone(true);
                        SharedPreferencesUtil.putBoolean(SharedPreferencesUtil.IS_LOCK, true);
                    }else{
                        if(SharedPreferencesUtil.getBoolean(SharedPreferencesUtil.IS_LOCK,false)){
                            Log.i(TAG,"解除锁屏");
                            deviceManager.lockPhone(false);
                            SharedPreferencesUtil.putBoolean(SharedPreferencesUtil.IS_LOCK, false);
                        }
                    }
                } else if (!TextUtils.isEmpty(currSimei)) {
                    //第一次记录SIM
                    SharedPreferencesUtil.putString(SharedPreferencesUtil.SIM_SIMEI, currSimei);
                }
            }
        }
    };
}
