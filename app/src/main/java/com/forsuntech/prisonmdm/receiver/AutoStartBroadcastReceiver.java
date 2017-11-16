package com.forsuntech.prisonmdm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.forsuntech.prisonmdm.service.AutoStartService;

/**
 * Created by vicken on 2017/10/16.
 */

public class AutoStartBroadcastReceiver extends BroadcastReceiver{

    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(ACTION)) {
//            Intent newIntent = context.getPackageManager()
//                    .getLaunchIntentForPackage(context.getPackageName());
//            context.startActivity(newIntent);

            Intent i=new Intent(context,AutoStartService.class);
            context.startService(i);
        }
    }
}
