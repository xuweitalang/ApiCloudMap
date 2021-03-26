package com.wpg.map.location;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import com.amap.api.location.AMapLocation;

/**
 * @Author: xuwei
 * @Date: 2021/3/26 19:39
 * @Description:
 */
class MyIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MyIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //子线程中执行
        Log.i("MyIntentService", "onHandleIntent");
        String extra = intent.getStringExtra("msg");
        new Thread(new Runnable() {
            @Override
            public void run() {
                LocationUtil.getInstance(getApplicationContext()).startLocation(LocationUtil.NULL, new LocationUtil.OnLocationBack() {
                    @Override
                    public void back(AMapLocation aMapLocation, String backString) {
                        Log.e("定位結果", aMapLocation.getAddress() + "");
                    }
                });
            }
        }).start();

        Log.i("MyIntentService", "onHandleIntent:" + extra);
        //调用completeWakefulIntent来释放唤醒锁。
//       WLWakefulReceiver.completeWakefulIntent(intent);
    }
}

