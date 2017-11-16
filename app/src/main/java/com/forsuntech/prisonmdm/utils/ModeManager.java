package com.forsuntech.prisonmdm.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.forsuntech.prisonmdm.policy.thread.WifiThread;
import com.forsuntech.prisonmdm.receiver.CallReceiver;
import com.forsuntech.prisonmdm.receiver.SmsReceiver;
import com.vivo.api.xjjy.DeviceModeManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vicken on 2017/10/16.
 */

public class ModeManager {

    private static CallReceiver  callReceiver=new CallReceiver();
    private static SmsReceiver smsReceiver=new SmsReceiver();
    private static final String TAG = ModeManager.class.getName();
    private static final DeviceModeManager dmm = new DeviceModeManager(MyApplication.getContextObject());
    private static  DeviceManager deviceManager=new DeviceManager();
    private static LogWriterUtils logWriterUtils=new LogWriterUtils("PackageList.log");
    private static WifiThread wifiThread=new WifiThread();
//    private static List<String> pkgList=null;
    /**
     * 重启手机（该应用有进程保护，如果应用死掉，基本等同于手机重启）
     */
    public static void initWorking(){
        boolean isWorking=SharedPreferencesUtil.getBoolean(SharedPreferencesUtil.IS_WORK_MODE,false);
        if(isWorking){
            switchWorking();
        }
        else{
            wifiThread.start();
        }
    }

    /**
     * 启动工作模式
     * @param enable true:启动工作模式  false：关闭工作模式
     */
    public synchronized static void enableWorking(boolean enable){
        boolean isWorking=SharedPreferencesUtil.getBoolean(SharedPreferencesUtil.IS_WORK_MODE,false);
        if(enable&&!isWorking) {
            switchWorking();
        }else if(!enable&&isWorking){
            switchNomal();
        }
    }

    private synchronized static void switchWorking(){
        Log.i(TAG,"切换到工作模式");
        SharedPreferencesUtil.putBoolean(SharedPreferencesUtil.IS_WORK_MODE,true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //切换到工作模式，传入应用包名列表
                    String brand= Build.BRAND;
                    Log.i(TAG,brand);
                    if(brand.equals("vivo")){
                        deviceManager.enableApk(true,"com.newabelhce");
                        List<String> pkgs = new ArrayList<String>();
                        pkgs.add("com.newabelhce");
                        pkgs.add("com.forsuntech.prisonmdm");
                        pkgs.add("com.android.dialer");
                        pkgs.add("com.android.contacts");
                        dmm.switchToWork(pkgs);
                    }else if(brand.equals("GIONEE")){
                        String[] pkgs={"com.forsuntech.prisonmdm","com.android.contacts","com.android.incallui","com.android.mms"};
                        gioneeWorkMode(1,pkgs);
                        List<PackageInfo> packages = MyApplication.getContextObject().getPackageManager()
                                .getInstalledPackages(0);
                        String pkgList="";
                        for (PackageInfo app : packages) {
                            if ((app.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                                String appPkg = app.packageName;
                                Log.i(TAG,"非系统应用："+appPkg);
                                if(appPkg.equals("com.forsuntech.prisonmdm")){
                                    continue;
                                }
                                if(!deviceManager.getApkStatus(appPkg)){
                                    Log.i(TAG,"禁用应用："+appPkg);
                                    pkgList+=appPkg+";";
                                    deviceManager.enableApk(false,appPkg);
                                    logWriterUtils.log(pkgList);
                                }
                            }
                        }
                        if(!SharedPreferencesUtil.getString(SharedPreferencesUtil.PKG_LIST,"").contains(";")&&!TextUtils.isEmpty(pkgList)){
                            SharedPreferencesUtil.putString(SharedPreferencesUtil.PKG_LIST,pkgList);
                            Log.i(TAG,"禁用应用列表："+pkgList);
                        }

//                        if(!deviceManager.getApkStatus("com.tencent.mm")){
//                            deviceManager.enableApk(false,"com.tencent.mm");
//                        }
//                        if(!deviceManager.getApkStatus("com.tencent.mobileqq")){
//                            deviceManager.enableApk(false,"com.tencent.mobileqq");
//                        }

                        if(!deviceManager.getApkStatus("com.gionee.bluetooth")){
                            deviceManager.enableApk(false,"com.gionee.bluetooth");
                        }
                        if(!deviceManager.getApkStatus("com.android.email")){
                            deviceManager.enableApk(false,"com.android.email");
                        }
                        if(!deviceManager.getApkStatus("com.android.browser")){
                            deviceManager.enableApk(false,"com.android.browser");
                        }


                    }
                    deviceManager.enabelWifi(false);
                    deviceManager.enableBlueTooh(false);

                    deviceManager.enableGps(false);
                    deviceManager.enableCarmer(false);

                    wifiThread.onStop();

                    //如果处于工作模式，则立即切换到工作模式
                    callReceiver.register();
                    smsReceiver.register();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    private synchronized static void switchNomal(){
        SharedPreferencesUtil.putBoolean(SharedPreferencesUtil.IS_WORK_MODE,false);
//        deviceManager.enableApk(false,"com.newabelhce");
//        deviceManager.enableGps(true);

        deviceManager.enableCarmer(true);
        deviceManager.enabelWifi(true);
        wifiThread.start();
        Log.i(TAG,"切换到生活模式");

        String brand= Build.BRAND;
        if(brand.equals("vivo")){
            dmm.switchToNormal();
        }else if(brand.equals("GIONEE")){

            gioneeWorkMode(0,null);

           String pkgList= SharedPreferencesUtil.getString(SharedPreferencesUtil.PKG_LIST,"");
            Log.i(TAG,"启用应用列表："+pkgList);
            if(!TextUtils.isEmpty(pkgList)){
                for(String pkgName:pkgList.split(";")){
                    if(!TextUtils.isEmpty(pkgList)){
                        if(deviceManager.getApkStatus(pkgName)){
                            deviceManager.enableApk(true,pkgName);
                        }
                    }
                }
            }
            SharedPreferencesUtil.putString(SharedPreferencesUtil.PKG_LIST,"");
//        if(deviceManager.getApkStatus("com.tencent.mobileqq")){
//            deviceManager.enableApk(true,"com.tencent.mobileqq");
//        }
//
//        if(deviceManager.getApkStatus("com.tencent.mm")){
//            deviceManager.enableApk(true,"com.tencent.mm");
//        }
        if(deviceManager.getApkStatus("com.android.email")){
            deviceManager.enableApk(true,"com.android.email");
        }
        if(deviceManager.getApkStatus("com.gionee.bluetooth")){
            deviceManager.enableApk(true,"com.gionee.bluetooth");
        }
       if(deviceManager.getApkStatus("com.android.browser")){
           deviceManager.enableApk(true,"com.android.browser");
       }
    }
    //处于工作模式启动电话拦截
        callReceiver.unRegister();
        smsReceiver.unRegister();
    }


    public static void gioneeWorkMode(int mode,String[] packageList) {
        Method method = null;
        try {
            Class c = Class.forName("android.util.AmigoUtil");
            Object r = c.newInstance();
            method = c.getDeclaredMethod("setPhoneMode", Context.class,int.class, String[].class);
            method.invoke(r, MyApplication.getContextObject(), mode , packageList);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("wanggj", "exception occur");
            Log.e("wanggj", Log.getStackTraceString(e));
        }
    }



}
