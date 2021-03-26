package com.wpg.map.location;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * @Author: xuwei
 * @Date: 2021/3/26 19:36
 * @Description:
 */
public class WLWakefulReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //
        String extra = intent.getStringExtra("msg");

        Intent serviceIntent = new Intent(context, MyIntentService.class);
        serviceIntent.putExtra("msg", extra);
        startWakefulService(context, serviceIntent);
    }
}
