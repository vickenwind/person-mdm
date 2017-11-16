package com.forsuntech.prisonmdm.policy.thread;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.forsuntech.prisonmdm.policy.util.WifiAdmin;
import com.forsuntech.prisonmdm.utils.ModeManager;
import com.forsuntech.prisonmdm.utils.MyApplication;
import com.forsuntech.prisonmdm.utils.SharedPreferencesUtil;

/**
 * Created by vicken on 2017/11/9.
 */

public class WifiThread {

    private Context context=null;
    private Thread wifiThread=null;
    private WifiAdmin wifiAdmin=null;
    private final String TAG=WifiThread.class.getName();
    public WifiThread(){
        context= MyApplication.getContextObject();
        wifiAdmin = new WifiAdmin(context);
    }

    public void start(){
        try {
            if (wifiThread == null) {
                wifiThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (!Thread.currentThread().isInterrupted()) {
                            try {
                                if(wifiAdmin==null){
                                    wifiAdmin = new WifiAdmin(context);
                                }
                                wifiAdmin.startScan(context);
                                String currSsids = wifiAdmin.lookUpScan().toString();
                                String policySsids = SharedPreferencesUtil.getString(SharedPreferencesUtil.POLICY_SSID, "");
                                Log.i(TAG, "policySsids:" + policySsids);
                                for (String ssid : currSsids.split(";")) {
                                    if(TextUtils.isEmpty(ssid)){
                                        continue ;
                                    }
                                    if (ssid.equals("XJJYMDM@NFC000")) {
                                        Log.i(TAG, "SSID:" + ssid);
                                        //启动工作模式
                                        ModeManager.enableWorking(true);
                                    }
                                    if (policySsids.indexOf(ssid) > -1) {
                                        for (String savessid : policySsids.split(";")) {
                                            if (savessid.equals(ssid)) {
                                                Log.i(TAG, "SSID:" + ssid);
                                                //启动工作模式
                                                ModeManager.enableWorking(true);
                                            }
                                        }

                                    }
                                }
                                Thread.sleep(1000 * 5);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }

                        }
                    }
                });

            }
            wifiThread.start();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void onStop(){
        if(wifiThread!=null){
//            wifiThread.stop();
//            wifiThread.interrupt();
            wifiThread=null;
        }
    }
}
