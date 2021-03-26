package com.wpg.map.location;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.xdandroid.hellodaemon.AbsWorkService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: xuwei
 * @Date: 2021/3/26 18:01
 * @Description:
 */
public class GPSService extends AbsWorkService {
    private static final String TAG = "GPSService";
    //是否 任务完成, 不再需要服务运行?
    public static boolean sShouldStopService;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = amapLocation -> {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //可在其中解析amapLocation获取相应内容。
                Log.d("mapLocation", amapLocation.toString());
                Toast.makeText(this, amapLocation.toString(), Toast.LENGTH_SHORT).show();
                //获取定位时间
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                df.format(date);
//                Log.d(TAG, String.format("经度：%s\t纬度：%s\t地址：%s\n%s\n应上传次数%d\n实上传次数%d", amapLocation.getLongitude(), amapLocation.getLatitude(), amapLocation.getAddress(), df.format(date), shouldCount, actualCount));
//                upload(amapLocation.getLongitude(), amapLocation.getLatitude());
            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        initGps();
    }

    private void initGps() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        /**
         * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
         */
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Sport);

        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(5000);

        mLocationClient.setLocationOption(mLocationOption);

        //启动定位
        mLocationClient.startLocation();
    }


    @Override
    public Boolean shouldStopService(Intent intent, int flags, int startId) {
        return null;
    }

    @Override
    public void startWork(Intent intent, int flags, int startId) {
        if (mLocationClient != null && !mLocationClient.isStarted()) {
            Log.i(TAG, "startLocation");
            mLocationClient.startLocation();
        } else if (mLocationClient == null) {
            initGps();
        }
    }

    @Override
    public void stopWork(Intent intent, int flags, int startId) {
        Log.i(TAG, "stopWork");
        stopService();
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
    }

    public static void stopService() {
        //我们现在不再需要服务运行了, 将标志位置为 true
        sShouldStopService = true;
        //取消 Job / Alarm / Subscription
        cancelJobAlarmSub();
    }

    @Override
    public Boolean isWorkRunning(Intent intent, int flags, int startId) {
        return null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent, Void alwaysNull) {
        return null;
    }

    @Override
    public void onServiceKilled(Intent rootIntent) {

    }
}
