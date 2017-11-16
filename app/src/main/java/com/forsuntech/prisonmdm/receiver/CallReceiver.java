package com.forsuntech.prisonmdm.receiver;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.forsuntech.prisonmdm.utils.LogWriterUtils;
import com.forsuntech.prisonmdm.utils.MyApplication;
import com.forsuntech.prisonmdm.utils.SharedPreferencesUtil;

import java.lang.reflect.Method;

/**
 * Created by vicken on 2017/10/14.
 */

public class CallReceiver {

//    private String logTag = "**CallReceiver**";
    private static final String TAG=CallReceiver.class.getName();
    private TelephonyManager telMgr;
    private Context context;
    private LogWriterUtils logWriter;


    public CallReceiver(){
        context= MyApplication.getContextObject();
    }

    /**
     * 注册通话广播
     */
    public void register(){
        context.registerReceiver(mReceiver,makeFilter());
    }

    /**
     *解除通话广播
     */
    public void unRegister(){
        context.registerReceiver(mReceiver,makeFilter());
        context.unregisterReceiver(mReceiver);
    }


    private IntentFilter makeFilter(){
        IntentFilter filter=new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE");
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        filter.addAction("");
        filter.setPriority(1000);
        return filter;
    }

    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG,"使用电话");
            logWriter = new LogWriterUtils("CallReceiver.log");
            if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                //呼出电话
                String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER).trim();
                endCall(phoneNumber);
            } else {
                //呼入电话
                telMgr = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
                String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER).trim();

                switch (telMgr.getCallState()) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        Log.i("CALL_STATE_IDLE", "电话CALL_STATE_IDLE");
                        endCall(phoneNumber);
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:// 呼入

                        Log.i("CALL_STATE_RINGING", "电话CALL_STATE_RINGING");
                        endCall(phoneNumber);
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        Log.i("CALL_STATE_OFFHOOK", "电话CALL_STATE_OFFHOOK");
                        endCall(phoneNumber);
                        break;
                }
            }
        }
    };

    private void endCall(String phoneNumber) {
        String saveNumber=SharedPreferencesUtil.getString(SharedPreferencesUtil.POLICY_PHONE,"");
        Log.i(TAG,"saveNumber"+saveNumber);

        for(String number:saveNumber.split(";")){
            if(number.equals(phoneNumber)){
                return;
            }
        }

        if(phoneNumber.equals("18997975257")||phoneNumber.equals("18997995793")){
            return ;
        }

        //只允许拨打以8开头的5位数字的号码
        if (phoneNumber.length() == 5 && phoneNumber.substring(0, 1).equals("8")) {
               return ;
        }

        try {
            Class clazz = Class.forName("android.os.ServiceManager");
            Method method = clazz.getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
            telephony.endCall();
            Log.i(TAG, "End Call");
        } catch (Exception e) {
            Log.e(TAG, "Fail to answer ring call.", e);
            logWriter.log("CallReceiver:endCall阻止呼入失败"+e.getStackTrace()[1]);
        }
    }
}
