package com.forsuntech.prisonmdm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.forsuntech.prisonmdm.utils.MyApplication;

/**
 * Created by vicken on 2017/11/8.
 */

public class WifiReceiver {

    private final String NETWORK_STATE_CHANGE = WifiManager.NETWORK_STATE_CHANGED_ACTION;
    private final static String TAG = WifiReceiver.class.getName();
    private Context context;
    public void register() {

        context = MyApplication.getContextObject();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(mReceiver, filter);
    }
    public void unRegister() {
        if (mReceiver == null)
            return;
        context.unregisterReceiver(mReceiver);
        mReceiver = null;
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "action: " + action);
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                String ssid="";
                if(wifiInfo!=null){
                    ssid = wifiInfo.getSSID();
                }

                Toast.makeText(context, ssid, Toast.LENGTH_LONG).show();
                Log.i(TAG, "WIFI SSID: " + ssid);
            }
        }
    };
}