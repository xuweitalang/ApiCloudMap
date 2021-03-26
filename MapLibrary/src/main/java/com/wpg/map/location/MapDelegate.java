package com.wpg.map.location;

import android.content.Context;
import android.util.Log;

import com.uzmap.pkg.uzcore.uzmodule.AppInfo;
import com.uzmap.pkg.uzcore.uzmodule.ApplicationDelegate;
import com.xdandroid.hellodaemon.DaemonEnv;

/**
 * @Author: xuwei
 * @Date: 2021/3/24 10:23
 * @Description:
 */
class MapDelegate extends ApplicationDelegate {
    private static final String TAG = "MapDelegate";

    @Override
    public void onApplicationCreate(Context context, AppInfo info) {
        super.onApplicationCreate(context, info);
        Log.d(TAG, "onApplicationCreate: ");

//        GPSReceiver receiver = new GPSReceiver();
//        context.registerReceiver(receiver,);

        //需要在 Application 的 onCreate() 中调用一次 DaemonEnv.initialize()
        DaemonEnv.initialize(context, GPSService.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
        GPSService.sShouldStopService = false;
        DaemonEnv.startServiceMayBind(GPSService.class);
    }
}
