package com.forsuntech.prisonmdm;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.forsuntech.prisonmdm.policy.ExecPolicy;
import com.forsuntech.prisonmdm.service.AutoStartService;
import com.forsuntech.prisonmdm.utils.ModeManager;
import com.forsuntech.prisonmdm.utils.SharedPreferencesUtil;
import com.newabelhce.AccountStorage;
import com.newabelhce.CardService;
import com.newabelhce.MainDES;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class MainActivity extends Activity {

    /**
     * 初始化NFC标签ID
     */
    String initTags="04:B4:04:32:CB:52:84;6EB48C35;";

    private NfcAdapter mNfcAdapter=null;
    private PendingIntent pi=null;
    private TextView tvShowMode;
    private Button btnWork;
    private Button btnNomal;
    private Context context;
    private TextView tvDeviceSnShow;
    private final String TAG=MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        context=this;
        //启动服务
        Intent inentAutoService=new Intent(this,AutoStartService.class);
        startService(inentAutoService);
        tvShowMode=findViewById(R.id.tv_show_mode);
        tvDeviceSnShow=findViewById(R.id.tv_device_sn);
        btnWork=(Button) findViewById(R.id.btn_switch_work);
        btnNomal=(Button) findViewById(R.id.btn_switch_nomal);

        // 在第一次的時候，設置Card uid
        if (AccountStorage.GetAccount(this).equals("00000000")) {
            TelephonyManager mtm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
            String mimei = mtm.getDeviceId();
            String mandroidid = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            if(mandroidid.length() < 16) {
                for(int i = 0; i < 16 - mandroidid.length(); i++)
                    mandroidid = mandroidid + "0";
            }
            if (mimei == null || mimei.length() < 15) {
                //Toast.makeText(this, "没有IMEI", Toast.LENGTH_SHORT).show();
                mimei = mandroidid;
                //System.out.println("手机android ID 号码：mandroidid");
                //Toast.makeText(this, "使用Android ID代替", Toast.LENGTH_SHORT).show();
            }
            byte[] cardid = MainDES.imei_id(mimei, mandroidid);
            AccountStorage.SetAccount(this, CardService.ByteArrayToHexString(cardid));
        }

        //切换到生活模式
        btnNomal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //切换到生活模式
                ModeManager.enableWorking(false);
                tvShowMode.setText(R.string.nomal_mode);
                Toast.makeText(context,"进入生活模式！",Toast.LENGTH_SHORT).show();
            }
        });

        //切换到工作模式
        btnWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ModeManager.enableWorking(true);
                tvShowMode.setText(R.string.working_mode);
                Toast.makeText(context,"进入工作模式！",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if(this==null){
                return ;
            }
            if (mNfcAdapter == null) {
                //初始化NfcAdapter
                mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
                // 初始化PendingIntent，当有NFC设备连接上的时候，就交给当前Activity处理
            }
            if (pi == null) {
                pi = PendingIntent.getActivity(this, 0, new Intent(this, getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            }
            mNfcAdapter.enableForegroundDispatch(this, pi, null, null); //启动
            if (SharedPreferencesUtil.getBoolean(SharedPreferencesUtil.IS_WORK_MODE, false)) {
                tvShowMode.setText(R.string.working_mode);
            } else {
                tvShowMode.setText(R.string.nomal_mode);
            }
            tvDeviceSnShow.setText("SN: " + AccountStorage.GetAccount(this));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        ExecPolicy execPolicy=new ExecPolicy();
        execPolicy.exePolicy();

        // 当前app正在前端界面运行，这个时候有intent发送过来，那么系统就会调用onNewIntent回调方法，将intent传送过来
        // 我们只需要在这里检验这个intent是否是NFC相关的intent，如果是，就调用处理方法
        Tag tag = intent.getParcelableExtra(mNfcAdapter.EXTRA_TAG);
        if(tag==null){
            return ;
        }
//        String currId=processIntent(intent);
//        String policyTags=SharedPreferencesUtil.getString(SharedPreferencesUtil.POLICY_TAG,"");
//        Log.i(TAG,"policyTags:"+policyTags);
//        if(initTags.indexOf(currId)==-1){
//            if(policyTags.indexOf(currId)==-1){
//                return ;
//            }
//        }
        String[] techList = tag.getTechList();
        boolean haveMifareUltralight = false;
                for (String tech : techList) {
                    if (tech.indexOf("MifareUltralight") >= 0) {
                        haveMifareUltralight = true;
                        break;
            }
        }
        if (!haveMifareUltralight) {
            return;
        }
        String mode=readTag(tag).trim();
        if(TextUtils.isEmpty(mode)){
            return;
        }
        if(mode.contains("010666666010")){
            //切换到工作模式
            ModeManager.enableWorking(true);
            tvShowMode.setText(R.string.working_mode);
            Toast.makeText(this,"进入工作模式！",Toast.LENGTH_SHORT).show();
        }else if(mode.contains("010888888010")){
            //切换到生活模式
            ModeManager.enableWorking(false);
            tvShowMode.setText(R.string.nomal_mode);
            Toast.makeText(this,"进入生活模式！",Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    private String processIntent(Intent intent) {
        //取出封装在intent中的TAG
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String CardId =ByteArrayToHexString(tagFromIntent.getId());
        Log.i(TAG,"CardId:"+CardId);
        return CardId;
    }
//    private String ByteArrayToHexString(byte[] inarray) {
//        int i, j, in;
//        String[] hex = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
//                "B", "C", "D", "E", "F" };
//        String out = "";
//        for (j = 0; j < inarray.length; ++j) {
//            in = (int) inarray[j] & 0xff;
//            i = (in >> 4) & 0x0f;
//            out += hex[i];
//            i = in & 0x0f;
//            out += hex[i];
//        }
//        return out;
//    }

    private String ByteArrayToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();

        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.toUpperCase(Character.forDigit(
                    (src[i] >>> 4) & 0x0F, 16));
            buffer[1] = Character.toUpperCase(Character.forDigit(src[i] & 0x0F,
                    16));
//            System.out.println(buffer);
            stringBuilder.append(buffer);
            stringBuilder.append(":");
        }
        return stringBuilder.toString().substring(0,stringBuilder.lastIndexOf(":"));
    }

    /**
     * 读nfc数据
     */
    public static String readFromTag(Intent intent) throws UnsupportedEncodingException {
        Parcelable[] rawArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawArray != null) {
            NdefMessage mNdefMsg = (NdefMessage) rawArray[0];
            NdefRecord mNdefRecord = mNdefMsg.getRecords()[0];
            if (mNdefRecord != null) {
                String readResult = new String(mNdefRecord.getPayload(), "UTF-8");
                return readResult;
            }
        }
        return "";
    }

    private String readTag(Tag tag) {
        MifareUltralight light = MifareUltralight.get(tag);
        try {
            light.connect();
            String metaInfo="";
            byte[] data = light.readPages(6);
            metaInfo +=new String(data, Charset.forName("utf-8"));
            return  metaInfo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 拦截back键
     */
    @Override
    public void onBackPressed() {
        //实现Home键效果
        //super.onBackPressed();这句话一定要注掉,不然又去调用默认的back处理方式
        Intent intent= new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
