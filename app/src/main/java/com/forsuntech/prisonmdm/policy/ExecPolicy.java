package com.forsuntech.prisonmdm.policy;

import android.util.Log;

import com.forsuntech.prisonmdm.policy.util.JsonValidator;
import com.forsuntech.prisonmdm.utils.SharedPreferencesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 *    Created by vicken on 2017/11/9.
*/

public class ExecPolicy {

    private final String policyPath="/sdcard/MDM-NFC/policy.txt";
    private final String TAG=ExecPolicy.class.getName();


    public void exePolicy(){
        try {
           String policyStr = readSDFile(policyPath);
            parserPolicy(policyStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parserPolicy(String policyStr){
        Log.i("TAG", "解析的策略为----" + policyStr);
        if(!new JsonValidator().validate(policyStr)){
            Log.i(TAG,"json格式不正确");
            return;
        }
        try {
            JSONObject jsonPolicy = new JSONObject(policyStr).getJSONObject("getpolicy");
            JSONArray policyArray = jsonPolicy.getJSONArray("policylist");
            for(int i=0;i<jsonPolicy.length();i++){
                JSONObject policy = (JSONObject) policyArray.get(i);
                if (policy.get("policyname").equals("phone")) {
                    Log.i(TAG,"phone:"+policy.get("content").toString());
                    SharedPreferencesUtil.putString(SharedPreferencesUtil.POLICY_PHONE,policy.get("content").toString());
                }else if(policy.get("policyname").equals("message")){
                    Log.i(TAG,"message:"+policy.get("content").toString());
                    SharedPreferencesUtil.putString(SharedPreferencesUtil.POLICY_MESSAGE,policy.get("content").toString());
                }else if(policy.get("policyname").equals("tag")){
                    Log.i(TAG,"tag:"+policy.get("content").toString());
                    SharedPreferencesUtil.putString(SharedPreferencesUtil.POLICY_TAG,policy.get("content").toString());
                }else if(policy.get("policyname").equals("ssid")){
                    Log.i(TAG,"ssid:"+policy.get("content").toString());
                    SharedPreferencesUtil.putString(SharedPreferencesUtil.POLICY_SSID,policy.get("content").toString());
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }




        private String readSDFile(String fileName) throws IOException {

            File file = new File(fileName);

            FileInputStream fis = new FileInputStream(file);

            int length = fis.available();

            byte [] buffer = new byte[length];
            fis.read(buffer);

            String res = new String(buffer, "UTF-8");

            fis.close();
            return res;

    }
}
