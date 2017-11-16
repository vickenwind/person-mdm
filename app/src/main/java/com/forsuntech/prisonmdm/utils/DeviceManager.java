package com.forsuntech.prisonmdm.utils;

import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;

import com.forsuntech.prisonmdm.receiver.MyAdminReceiver;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vicken on 2017/10/15.
 */

public class DeviceManager {

    private static Context context ;
    private DevicePolicyManager dpm;
    private ComponentName deviceAdmin;
    private BluetoothAdapter mBluetoothAdapter;
    private static final String TAG=DeviceManager.class.getName();
    private WifiManager mWifiManager;
    public DeviceManager(){
        context=MyApplication.getContextObject();
        dpm = getDevicePolicyManager(context);
        deviceAdmin = getAdminName(context);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mWifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);

//        dpm.clearDeviceOwnerApp(context.getPackageName());

    }

    public void enableCarmer(boolean enable) {

        try {
            if (dpm != null) {
                if (dpm.isAdminActive(deviceAdmin)) {
                    boolean isCameraDisabled = dpm.getCameraDisabled(deviceAdmin);
                    // If the camera isn't already disabled and the user wants
                    // to
                    // disable the camera (disable is true), disable the
                    // device's camera
                    if (!isCameraDisabled && !enable) {
                        dpm.setCameraDisabled(deviceAdmin, !enable);
                    }
                    // If the camera is already disabled and the user wants to
                    // enable
                    // the camera (disable is false), enable the device's camera
                    if (isCameraDisabled && enable) {
                        dpm.setCameraDisabled(deviceAdmin, !enable);
                    }
                }
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enableGps(boolean enable){

        if (dpm.isDeviceOwnerApp(context.getPackageName())) {
            if(enable){
//                dpm.setSecureSetting(deviceAdmin, Settings.Secure.LOCATION_MODE, String.valueOf(Settings.Secure.LOCATION_MODE_SENSORS_ONLY));
//                dpm.setSecureSetting(deviceAdmin, Settings.Secure.LOCATION_MODE, String.valueOf(Settings.Secure.LOCATION_MODE_SENSORS_ONLY));
            }else{
                dpm.setSecureSetting(deviceAdmin, Settings.Secure.LOCATION_MODE, String.valueOf(Settings.Secure.LOCATION_MODE_OFF));
//                dpm.setSecureSetting(deviceAdmin, Settings.Secure.LOCATION_MODE, String.valueOf(Settings.Secure.LOCATION_MODE_SENSORS_ONLY));
            }

        }
        }

    public void enabelWifi(boolean enable){
        if (mWifiManager != null) {
                if (!enable) {
                    if (mWifiManager.isWifiEnabled()) {
                        enableForceWifi(0);
                        mWifiManager.setWifiEnabled(enable);
                        Log.d(TAG, "DisableWifi");
                    }
                }else{
                    enableForceWifi(1);
                    mWifiManager.setWifiEnabled(enable);
            }

            return;
        }
    }

    public void enableBlueTooh(boolean enable){
        if (!enable) {
            try {
                if (mBluetoothAdapter != null) {
                    switch (mBluetoothAdapter.getState()) {
                        case BluetoothAdapter.STATE_ON:
                            mBluetoothAdapter.disable();
                            Log.d(TAG, "DisableBluetooth");
                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            mBluetoothAdapter.disable();
                            Log.d(TAG, "DisableBluetooth");
                            break;
                    }
//					LogWriterUtils.log("OpBluetooth:CloseBluetooth:成功禁用蓝牙");
                    return;
                }

            } catch (Exception e) {
                // TODO: handle exception
//                e.printStackTrace();
                e.printStackTrace();
            }
        }
    }

    public void enableApk(boolean enable,String pkgName){
        if (dpm.isDeviceOwnerApp(context.getPackageName())) {
            dpm.setApplicationHidden(deviceAdmin,pkgName,!enable);
        }
    }

    public boolean getApkStatus(String pkgName){
        boolean apkStatus=true;
        if (dpm.isDeviceOwnerApp(context.getPackageName())) {
            apkStatus=dpm.isApplicationHidden(deviceAdmin,pkgName);
        }
        return apkStatus;
    }

    public void lockPhone(boolean enable){
        if (dpm.isAdminActive(deviceAdmin)) {
            if(enable){

                SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                String[] time=str.split("-");
                String pwd="";
                for(int i=0;i<time.length;i++){
                    if(i!=0){
                        pwd+=time[i];
                    }
                }
                //锁屏
//                dpm.setPasswordMinimumLength(deviceAdmin, 0);//设置密码长度
                dpm.lockNow(); // 锁屏
                dpm.resetPassword("", 0);
                dpm.resetPassword(pwd, 0); // 设置锁屏密码
            }else{
                dpm.lockNow();
                dpm.resetPassword("", 0); // 解锁
            }

        }

    }

    private ComponentName getAdminName(Context context) {
        ComponentName DeviceAdmin = new ComponentName(context, MyAdminReceiver.class);
        return DeviceAdmin;
    }

    private DevicePolicyManager getDevicePolicyManager(Context context) {
        DevicePolicyManager dPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        return dPM;
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
}
