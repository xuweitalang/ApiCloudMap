package com.wpg.map;


import android.util.Log;

import com.uzmap.pkg.uzapp.UZApplication;
import com.wpg.map.location.GPSService;
//import com.xdandroid.hellodaemon.DaemonEnv;

/**
 * @Author: xuwei
 * @Date: 2021/3/26 18:24
 * @Description:
 */
public class MyApplication extends UZApplication {
    private static final String TAG = "MyApplication";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        //需要在 Application 的 onCreate() 中调用一次 DaemonEnv.initialize()
//        DaemonEnv.initialize(this, GPSService.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
//        GPSService.sShouldStopService = false;
//        DaemonEnv.startServiceMayBind(GPSService.class);
    }
}
