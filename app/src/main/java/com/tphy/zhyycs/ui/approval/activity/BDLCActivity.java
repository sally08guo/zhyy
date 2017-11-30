package com.tphy.zhyycs.ui.approval.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.tphy.zhyycs.R;

public class BDLCActivity extends AppCompatActivity {
    public LocationClient mLocationClient = null;
    private TextView tv_address,tv_buildingNum,tv_floor;
    private int PERMISSION_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bdlc);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_buildingNum = (TextView) findViewById(R.id.tv_buildingnum);
        tv_floor = (TextView) findViewById(R.id.tv_floor);
        Log.e("WQ", "进入百度定位");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_CODE
            );
        } else {
            initMap();
        }
    }

    private void initMap() {
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(new MyLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                //获取定位结果
                location.getTime();    //获取定位时间
                location.getLocationID();    //获取定位唯一ID，v7.2版本新增，用于排查定位问题
                location.getLocType();    //获取定位类型
                location.getLatitude();//获取纬度信息
                location.getLongitude();//获取经度信息
                location.getRadius();    //获取定位精准度
                String addrStr = location.getAddrStr();//获取地址信息
                tv_address.setText(addrStr);
                location.getCountry();    //获取国家信息
                location.getCountryCode();    //获取国家码
                location.getCity();    //获取城市信息
                location.getCityCode();    //获取城市码
                location.getDistrict();    //获取区县信息
                location.getStreet();//获取街道信息
                location.getStreetNumber();//获取街道码
                String locationDescribe = location.getLocationDescribe();//获取当前位置描述信息
                Log.e("WQ",locationDescribe);
                tv_address.setText("描述信息："+addrStr+locationDescribe);
                location.getPoiList();    //获取当前位置周边POI信息
                location.getBuildingID();//室内精准定位下，获取楼宇ID
                String buildingName = location.getBuildingName();//室内精准定位下，获取楼宇名称
                Log.e("WQ", "楼号是==>" + buildingName);
                tv_buildingNum.setText(buildingName);
                String floor = location.getFloor();//室内精准定位下，获取当前位置所处的楼层信息
                Log.e("WQ", "楼层是===>" + floor);
                tv_floor.setText(floor);
                if (location.getLocType() == BDLocation.TypeGpsLocation) {

                    //当前为GPS定位结果，可获取以下信息
                    location.getSpeed();    //获取当前速度，单位：公里每小时
                    location.getSatelliteNumber();    //获取当前卫星数
                    location.getAltitude();    //获取海拔高度信息，单位米
                    location.getDirection();    //获取方向信息，单位度
//            location.

                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {

                    //当前为网络定位结果，可获取以下信息
                    location.getOperators();    //获取运营商信息

                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {

                    //当前为网络定位结果

                } else if (location.getLocType() == BDLocation.TypeServerError) {

                    //当前网络定位失败
                    //可将定位唯一ID、IMEI、定位失败时间反馈至loc-bugs@baidu.com

                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {

                    //当前网络不通

                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {

                    //当前缺少定位依据，可能是用户没有授权，建议弹出提示框让用户开启权限
                    //可进一步参考onLocDiagnosticMessage中的错误返回码

                }
                mLocationClient.stop();
            }

            @Override
            public void onLocDiagnosticMessage(int locType, int diagnosticType, String diagnosticMessage) {
                if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_BETTER_OPEN_GPS) {

                    //建议打开GPS

                } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_BETTER_OPEN_WIFI) {

                    //建议打开wifi，不必连接，这样有助于提高网络定位精度！

                } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_CHECK_LOC_PERMISSION) {

                    //定位权限受限，建议提示用户授予APP定位权限！

                } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_CHECK_NET) {

                    //网络异常造成定位失败，建议用户确认网络状态是否异常！

                } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_CLOSE_FLYMODE) {

                    //手机飞行模式造成定位失败，建议用户关闭飞行模式后再重试定位！

                } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_INSERT_SIMCARD_OR_OPEN_WIFI) {

                    //无法获取任何定位依据，建议用户打开wifi或者插入sim卡重试！

                } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_OPEN_PHONE_LOC_SWITCH) {

                    //无法获取有效定位依据，建议用户打开手机设置里的定位开关后重试！

                } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_SERVER_FAIL) {

                    //百度定位服务端定位失败
                    //建议反馈location.getLocationID()和大体定位时间到loc-bugs@baidu.com

                } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_FAIL_UNKNOWN) {

                    //无法获取有效定位依据，但无法确定具体原因
                    //建议检查是否有安全软件屏蔽相关定位权限
                    //或调用LocationClient.restart()重新启动后重试！

                }
            }
        });
        initOption();
        mLocationClient.start();

    }

    private void initOption() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        int span = 1000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

//        option.setIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

//        option.setWifiValidTime(5*60*1000);
        //可选，7.2版本新增能力，如果您设置了这个接口，首次启动定位时，会先判断当前WiFi是否超出有效期，超出有效期的话，会先重新扫描WiFi，然后再定位

        mLocationClient.setLocOption(option);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                initMap();
            } else {
                // Permission Denied
                Toast.makeText(this, "访问被拒绝！", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
