package com.wpg.map.location;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * @Author: xuwei
 * @Date: 2021/3/26 19:41
 * @Description:
 */
public class LocationUtil implements AMapLocationListener {
    private static LocationUtil locationUtil;
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;
    private OnLocationBack onLocationBack;
    private OnLocationTrain onLocationTrain;
    public static final String NULL = "null";
    private String differenceFlag = "";
    private String latitude, longitude, cityNameString, HotelCityCode;
    private Context context;

    private LocationUtil(Context context) {
        this.context = context;
    }

    public static LocationUtil getInstance(Context context) {
        if (locationUtil == null) {
            synchronized (LocationUtil.class) {
                if (locationUtil == null) {
                    locationUtil = new LocationUtil(context.getApplicationContext());
                }
            }
        }
        return locationUtil;
    }

    private void init() {
        mLocationClient = new AMapLocationClient(context);
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(5000);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //返回最近3s内精度最高的一次定位结果。
        mLocationOption.setOnceLocationLatest(false);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.setLocationListener(this);
    }

    public void startLocation(String differenceFlag, OnLocationBack onLocationBack) {
        init();
        mLocationClient.startLocation();//开始
        this.onLocationBack = onLocationBack;
        this.differenceFlag = differenceFlag;
        Log.e("开始定位", "开始定位");
    }

    public void startLocationTrain(String differenceFlag, OnLocationTrain onLocationTrain) {
        init();
        mLocationClient.startLocation();//开始
        this.onLocationTrain = onLocationTrain;
        this.differenceFlag = differenceFlag;
        Log.e("开始定位", "开始定位");
    }

    public void stopLocation() {
        if (null == mLocationClient) {
            return;
        }
        mLocationClient.unRegisterLocationListener(this);
        mLocationClient.stopLocation();//关闭
        mLocationClient.onDestroy();//销毁
        mLocationClient = null;
        Log.e("开始定位", "开始定位");
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Log.e("定位到当前位置:  ", aMapLocation.getAddress());
        if (aMapLocation == null) {
            onLocationTrain.LocationFail("定位失败");
            return;
        }
        if (null != aMapLocation.getCity()
                && !"null".equals(aMapLocation.getCity())
                && !"".equals(aMapLocation.getCity())
                && 0 != aMapLocation.getLatitude()) {
            cityNameString = aMapLocation.getCity();
            latitude = "" + aMapLocation.getLatitude();
            longitude = "" + aMapLocation.getLongitude();
            saveLocation(aMapLocation);
        } else {
            onLocationTrain.LocationFail("定位失败");
            return;
        }

    }

    public interface OnLocationBack {
        void back(AMapLocation aMapLocation, String backString);
    }

    public interface OnLocationTrain {
        void back(AMapLocation aMapLocation, String backString);

        void LocationFail(String error);
    }

    private void saveLocation(AMapLocation aMapLocation) {
        switch (differenceFlag) {
            case NULL:
                onLocationBack.back(aMapLocation, "返回的是定位到的所有信息");
                break;
        }
    }

}
