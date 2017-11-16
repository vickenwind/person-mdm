package com.forsuntech.prisonmdm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.forsuntech.prisonmdm.utils.DeviceManager;
import com.forsuntech.prisonmdm.utils.MyApplication;
import com.forsuntech.prisonmdm.utils.SharedPreferencesUtil;

/**
 * Created by vicken on 2017/11/2.
 */

public class SmsReceiver {
    private static final String TAG=SimStateReceive.class.getName();
    private final static String MDM_FILTER_SMS = "android.intent.action.MDM_FILTER_SMS";
    private Context context;
    private DeviceManager deviceManager;
    public SmsReceiver(){
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
        filter.addAction(MDM_FILTER_SMS);
//        filter.addDataScheme("SIMCHANGE");
        filter.setPriority(1000);
        return filter;
    }
    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MDM_FILTER_SMS.equals(intent.getAction())) {
                String phoneNumber = intent.getStringExtra("number").trim();
                String messageBody = intent.getStringExtra("messageBody").trim();
                Log.i(TAG,phoneNumber+messageBody);
                boolean isSend=false;
                String saveNumber= SharedPreferencesUtil.getString(SharedPreferencesUtil.POLICY_MESSAGE,"");
                Log.i(TAG,"saveNumber"+saveNumber);

                for(String number:saveNumber.split(";")){
                    if(number.equals(phoneNumber)){
                        isSend=true;
                    }
                }

                if(phoneNumber.equals("18997975257")||phoneNumber.equals("18997995793")){
                    isSend=true;
                }


                    //此处做短信过滤判断
                if (phoneNumber.length() == 5 && phoneNumber.substring(0, 1).equals("8")) {
                    isSend=true;
                }

                Log.i(TAG,"是否可以发送："+isSend);
                Intent intentResult = new Intent("android.intent.action.MDM_FILTER_SMS_RESULT");
                intentResult.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                intentResult.putExtra("allow", isSend);// true为允许收发短信，false为禁止收发短信；此处请传入短信过滤的判断结果
                context.sendBroadcast(intentResult);
                return;
            }
        }
    };
}
