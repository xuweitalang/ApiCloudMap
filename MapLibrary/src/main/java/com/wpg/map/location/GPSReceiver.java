package com.wpg.map.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * @Author: xuwei
 * @Date: 2021/3/26 17:08
 * @Description:
 */
class GPSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!GPSUtil.isOPen(context)) {
            GPSUtil.openGPS(context);
        } else {
            Toast.makeText(context, "GPS已打开", Toast.LENGTH_SHORT).show();
        }
    }
}
