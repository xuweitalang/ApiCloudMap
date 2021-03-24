package com.wpg.map.location;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Author: xuwei
 * @Date: 2021/3/24 9:46
 * @Description: 定位工具
 */
public class LocationHelper extends UZModule {
    private static final String TAG = "LocationHelper";
    private static final String TYPE_BD = "'bmap";
    private static final String TYPE_GD = "amap";
    public LocationClient mBDLocationClient = null;
    private BDAbstractLocationListener myBDListener = new MyLocationListener();
    private BDLocation bdLocation;
    private UZModuleContext mJsCallback;

    //声明AMapLocationClient类对象
    private AMapLocationClient mGDLocationClient;
    private AMapLocationClientOption mGDLocationOption;

    public LocationHelper(UZWebView webView) {
        super(webView);
        requestLocationPermission();
    }

    public void jsmethod_startTestActivity(final UZModuleContext moduleContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(context(), TestActivity.class));
            } else {
                Toast.makeText(context(), "权限已开启", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 获取定位权限
     */
    private void requestLocationPermission() {

    }

    public void jsmethod_init(final UZModuleContext moduleContext) {
        //获取前端传过来的地图类型：假设0为百度地图，1为高德地图
        mJsCallback = moduleContext;

        String type = moduleContext.optString("mapType", TYPE_BD);
        if (TYPE_GD.equals(type)) {
            initGDMap(context());
        } else {
            initBDMap(context());
        }
    }

    /**
     * 调用百度定位
     *
     * @param moduleContext
     */
    public void jsmethod_initBD(final UZModuleContext moduleContext) {
        //获取前端传过来的地图类型：假设0为百度地图，1为高德地图
        mJsCallback = moduleContext;
        initBDMap(context());
    }

    /**
     * 调用高德定位
     *
     * @param moduleContext
     */
    public void jsmethod_initGD(final UZModuleContext moduleContext) {
        //获取前端传过来的地图类型：假设0为百度地图，1为高德地图
        mJsCallback = moduleContext;
        initGDMap(context());
    }

    /**
     * 初始化百度地图
     *
     * @param context
     */
    public void initBDMap(Context context) {

        //声明LocationClient类
        mBDLocationClient = new LocationClient(context.getApplicationContext());
        //注册监听函数
        mBDLocationClient.registerLocationListener(myBDListener);
        //开始定位
        mBDLocationClient.start();

        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType("bd09ll");
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        int span = 5000;
        option.setScanSpan(span);
        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);
        //可选，默认false,设置是否使用gps
        option.setOpenGps(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setLocationNotify(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIsNeedLocationPoiList(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIgnoreKillProcess(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        option.setEnableSimulateGps(false);
        mBDLocationClient.setLocOption(option);
    }

    /**
     * 初始化高德地图
     */
    private void initGDMap(Context context) {
        //初始化client
        mGDLocationClient = new AMapLocationClient(context.getApplicationContext());
        mGDLocationOption = getDefaultOption();
        //设置定位参数
        mGDLocationClient.setLocationOption(mGDLocationOption);
        // 设置定位监听
        mGDLocationClient.setLocationListener(mGDLocationListener);
        mGDLocationClient.startLocation();
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }


    /**
     * 百度定位监听
     * 实现定位监听 位置一旦有所改变就会调用这个方法
     * 可以在这个方法里面获取到定位之后获取到的一系列数据
     */
    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //获取定位结果
//            location.getTime();    //获取定位时间
//            location.getLocationID();    //获取定位唯一ID，v7.2版本新增，用于排查定位问题
//            location.getLocType();    //获取定位类型
//            location.getLatitude();    //获取纬度信息
//            location.getLongitude();    //获取经度信息
//            location.getRadius();    //获取定位精准度
//            location.getAddrStr();    //获取地址信息
//            location.getCountry();    //获取国家信息
//            location.getCountryCode();    //获取国家码
//            location.getCity();    //获取城市信息
//            location.getCityCode();    //获取城市码
//            location.getDistrict();    //获取区县信息
//            location.getStreet();    //获取街道信息
//            location.getStreetNumber();    //获取街道码
//            location.getLocationDescribe();    //获取当前位置描述信息
//            location.getPoiList();    //获取当前位置周边POI信息
//
//            location.getBuildingID();    //室内精准定位下，获取楼宇ID
//            location.getBuildingName();    //室内精准定位下，获取楼宇名称
//            location.getFloor();    //室内精准定位下，获取当前位置所处的楼层信息

            bdLocation = location;
            if (mJsCallback != null) {
                JSONObject ret;
                if (bdLocation != null) {
                    try {
                        ret = new JSONObject(new Gson().toJson(bdLocation));
                        mJsCallback.success(ret, true);
                        Log.d(TAG, "mJsCallback: baidu====>" + ret);
                        Toast.makeText(context(), "百度定位成功:" + ret, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        mJsCallback.error(new JSONObject("baidu====>定位失败"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    /**
     * 高德定位监听
     */
    AMapLocationListener mGDLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {

                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    sb.append("定位成功" + "\n");
                    sb.append("定位类型: " + location.getLocationType() + "\n");
                    sb.append("经    度    : " + location.getLongitude() + "\n");
                    sb.append("纬    度    : " + location.getLatitude() + "\n");
                    sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                    sb.append("提供者    : " + location.getProvider() + "\n");

                    sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                    sb.append("角    度    : " + location.getBearing() + "\n");
                    // 获取当前提供定位服务的卫星个数
                    sb.append("星    数    : " + location.getSatellites() + "\n");
                    sb.append("国    家    : " + location.getCountry() + "\n");
                    sb.append("省            : " + location.getProvince() + "\n");
                    sb.append("市            : " + location.getCity() + "\n");
                    sb.append("城市编码 : " + location.getCityCode() + "\n");
                    sb.append("区            : " + location.getDistrict() + "\n");
                    sb.append("区域 码   : " + location.getAdCode() + "\n");
                    sb.append("地    址    : " + location.getAddress() + "\n");
                    sb.append("兴趣点    : " + location.getPoiName() + "\n");
                    //定位完成的时间
                } else {
                    //定位失败
                    sb.append("定位失败" + "\n");
                    sb.append("错误码:" + location.getErrorCode() + "\n");
                    sb.append("错误信息:" + location.getErrorInfo() + "\n");
                    sb.append("错误描述:" + location.getLocationDetail() + "\n");
                }
                sb.append("***定位质量报告***").append("\n");
                sb.append("* WIFI开关：").append(location.getLocationQualityReport().isWifiAble() ? "开启" : "关闭").append("\n");
                sb.append("* GPS星数：").append(location.getLocationQualityReport().getGPSSatellites()).append("\n");
                sb.append("* 网络类型：" + location.getLocationQualityReport().getNetworkType()).append("\n");
                sb.append("* 网络耗时：" + location.getLocationQualityReport().getNetUseTime()).append("\n");
                sb.append("****************").append("\n");

                //解析定位结果
                String result = sb.toString();
                Log.d(TAG, "onLocationChanged:gaode====> " + result);

                if (mJsCallback != null) {
                    JSONObject ret;
                    try {
                        ret = new JSONObject(new Gson().toJson(location));
                        mJsCallback.success(ret, true);
                        Log.d(TAG, "mJsCallback: gaode====>" + ret);
                        Toast.makeText(context(), "高德定位成功:" + ret, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            } else {
                Log.d(TAG, "onLocationChanged: 定位失败，loc is null");
                if (mJsCallback != null) {
                    try {
                        mJsCallback.error(new JSONObject("定位失败"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    };


    @Override
    protected void onClean() {
        if (null != mJsCallback) {
            mJsCallback = null;
        }
    }

}


