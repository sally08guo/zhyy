package com.tphy.zhyycs.ui.approval.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.utils.ActivityUtils;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.CustomProgressDialog;
import com.tphy.zhyycs.utils.StringCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


public class AttendanceItemActivity extends Activity implements View.OnClickListener {

    private String mLocation;
    private TextView mTijiaoTvLocation1;
    private ImageView mImageview;
    private TextView mTijiaoTvCommit;
    private String code;
    private String str_file;
    //    private String attendanceCode = "0";
    private Dialog progressDialog;
    private ActivityUtils activityUtils;
    private Intent intent;
    //    private String leiBie = "";
    public LocationClient mLocationClient = null;
    /*权限部分*/
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA
    };

    private static final int PERMISSON_REQUESTCODE = 0;

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;
    private String qrCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendanceitem);
        Intent intent = getIntent();
//        if (null != getIntent().getStringExtra("LeiBie")) {
//            leiBie = intent.getStringExtra("LeiBie");
//        }
        qrCode = intent.getStringExtra("QRCode");
//        attendanceCode = intent.getStringExtra("AttendanceCode");
        initView();
        if (Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23) {
            if (isNeedCheck) {
                checkPermissions(needPermissions);
            }
        } else {
            this.intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(this.intent, 0);
        }
    }


    private void checkPermissions(String... permissions) {
        try {
            if (Build.VERSION.SDK_INT >= 23
                    && getApplicationInfo().targetSdkVersion >= 23) {
                List<String> needRequestPermissonList = findDeniedPermissions(permissions);
                if (null != needRequestPermissonList
                        && needRequestPermissonList.size() > 0) {
                    String[] array = needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
                    Method method = getClass().getMethod("requestPermissions", new Class[]{String[].class,
                            int.class});

                    method.invoke(this, array, PERMISSON_REQUESTCODE);
                } else if (null != needRequestPermissonList && needRequestPermissonList.size() < 1) {
                    this.intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(this.intent, 0);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        if (Build.VERSION.SDK_INT >= 23
                && getApplicationInfo().targetSdkVersion >= 23) {
            try {
                for (String perm : permissions) {
                    Method checkSelfMethod = getClass().getMethod("checkSelfPermission", String.class);
                    Method shouldShowRequestPermissionRationaleMethod = getClass().getMethod("shouldShowRequestPermissionRationale",
                            String.class);
                    if ((Integer) checkSelfMethod.invoke(this, perm) != PackageManager.PERMISSION_GRANTED
                            || (Boolean) shouldShowRequestPermissionRationaleMethod.invoke(this, perm)) {
                        needRequestPermissonList.add(perm);
                    }
                }
            } catch (Throwable e) {

            }
        }
        return needRequestPermissonList;
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
                location.getCountry();    //获取国家信息
                location.getCountryCode();    //获取国家码
                location.getCity();    //获取城市信息
                location.getCityCode();    //获取城市码
                location.getDistrict();    //获取区县信息
                location.getStreet();//获取街道信息
                location.getStreetNumber();//获取街道码
                String locationDescribe = location.getLocationDescribe();//获取当前位置描述信息
                mLocation = addrStr + locationDescribe;
                location.getPoiList();    //获取当前位置周边POI信息
                location.getBuildingID();//室内精准定位下，获取楼宇ID
                location.getBuildingName();//室内精准定位下，获取楼宇名称
                location.getFloor();//室内精准定位下，获取当前位置所处的楼层信息

                if (null != mLocation && !mLocation.contains("null") && null != progressDialog) {
                    progressDialog.dismiss();
                    mTijiaoTvLocation1.setText(addrStr + locationDescribe);
                    if (null != mLocationClient && mLocationClient.isStarted()) {
                        mLocationClient.stop();
                    }
                }
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
                    Log.e("TAG", "网络定位失败");

                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {

                    //当前网络不通
                    Log.e("TAG", "当前网络不通");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {

                    //当前缺少定位依据，可能是用户没有授权，建议弹出提示框让用户开启权限
                    //可进一步参考onLocDiagnosticMessage中的错误返回码
                    Log.e("TAG", "当前缺少定位依据");
                }

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

    private void initView() {
        SharedPreferences sp = getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);
        code = sp.getString("Code", "");
        ImageView mToolbarIvBack = (ImageView) findViewById(R.id.toolbar_iv_back);
        TextView mToolbarTvBack = (TextView) findViewById(R.id.toolbar_tv_back);
        TextView mToolbarTvTitle = (TextView) findViewById(R.id.toolbar_tv_title);
        ImageView mToolbarIvIcon = (ImageView) findViewById(R.id.toolbar_iv_icon);
        ImageView mToolbarIvAddIcon = (ImageView) findViewById(R.id.toolbar_iv_addIcon);
        TextView mToolbarTvButton = (TextView) findViewById(R.id.toolbar_tv_button);
        mTijiaoTvLocation1 = (TextView) findViewById(R.id.tijiao_tv_location1);
        mImageview = (ImageView) findViewById(R.id.tijiao_iv_photo);
        mTijiaoTvCommit = (TextView) findViewById(R.id.tijiao_tv_commit);
        mToolbarIvBack.setOnClickListener(this);
        mToolbarTvBack.setOnClickListener(this);
        mTijiaoTvCommit.setOnClickListener(this);
        mImageview.setOnClickListener(this);
        mToolbarTvTitle.setText("打卡");
        mToolbarIvIcon.setVisibility(View.GONE);
        mToolbarIvAddIcon.setVisibility(View.GONE);
        mToolbarTvButton.setVisibility(View.GONE);
        mToolbarIvBack.setVisibility(View.VISIBLE);
        mToolbarTvBack.setVisibility(View.VISIBLE);
        activityUtils = new ActivityUtils(this);
//        setTypeText();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_iv_back:
                finish();
                break;
            case R.id.toolbar_tv_back:
                finish();
                break;
            case R.id.tijiao_tv_commit:
                updateInfo();
                break;
            case R.id.tijiao_iv_photo:
                if (null != intent) {
                    startActivityForResult(intent, 0);
                } else {
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 0);
                }
                break;
        }

    }

//    private void setTypeText() {
//        switch (leiBie) {
//            case "1":
//                mTijiaoTvType1.setText("上班打卡");
//                break;
//            case "2":
//                mTijiaoTvType1.setText("下班打卡");
//                break;
//            case "3":
//                mTijiaoTvType1.setText("签到打卡");
//                break;
//            case "4":
//                mTijiaoTvType1.setText("签离打卡");
//                break;
//        }
//    }

    private void updateInfo() {
        if (null != mLocation && (!mLocation.equals("")) && (!mLocation.contains("null"))) {
            HashMap<String, String> params = new HashMap<>();
            Map<String, String> map = new HashMap<>();
            long l = System.currentTimeMillis();
            String name = String.valueOf(l);
            map.put("LoginUserCode", code);
            map.put("ImageName", name);
//            map.put("LeiBie", leiBie);
            map.put("Location", mLocation);
            map.put("ImageData", Common.imageToBase64(str_file));
//            map.put("AttendanceCode", attendanceCode);
            map.put("QRCode", qrCode);
            map.put("PhoneType", "A");
            params.put("paraJson", Common.MaptoJson(map));
            Log.e("WQ", "签到json==>" + params);
            String url = DemoApplication.serviceUrl + "/SingInWithQRCode";
            OkHttpUtils.post().url(url).params(params).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {

                }

                @Override
                public void onResponse(String response, int id) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        String msg = jsonObject.getString("msg");
                        if (success.equals("true")) {
                            Toast.makeText(AttendanceItemActivity.this, "打卡成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AttendanceItemActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(this, "未获取您当前位置", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == -1) {

                final Bitmap photo = data.getParcelableExtra("data");
                Drawable drawable = new BitmapDrawable(photo);
                if (mTijiaoTvCommit.getVisibility() == View.GONE) {
                    mTijiaoTvCommit.setVisibility(View.VISIBLE);
                }
                if (photo != null) {
                    mImageview.setImageDrawable(drawable);
                }
                if (null == mLocation || ("".equals(mLocation))) {
                    progressDialog = CustomProgressDialog.createLoadingDialog(this, "获取定位中...");
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                    initMap();
                }
                String name = "kaoqin.jpg";
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
                FileOutputStream b;
                File file = new File("/sdcard/zhyy/img/");

                file.mkdirs();// 创建文件夹
                String fileName = "/sdcard/zhyy/img/" + name;
                try {
                    b = new FileOutputStream(fileName);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 设置位图的压缩格式，质量为100%，并放入字节数组输出流中
                    b.flush();
                    b.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                str_file = fileName;

            } else {
                //此处还有疑问如果用户刚开始退出拍照，定位后再拍照，期间移动了位置？
//                if (null == location || ("".equals(location))) {
//                    initMap();
//                    progressDialog = new ProgressDialog(this);
//                    progressDialog.setTitle("获取定位中...");
//                    progressDialog.setCancelable(false);
//                    progressDialog.show();
//                    mTijiaoTvLocation1.setText(location);
//                }
                if (mTijiaoTvCommit.getVisibility() == View.VISIBLE) {
                    mTijiaoTvCommit.setVisibility(View.GONE);
                }
                activityUtils.showToast("拍照未成功！");
            }
        }
    }

    /**
     * 检测是否所有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                showMissingPermissionDialog();
                isNeedCheck = false;
            } else {
                this.intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(this.intent, 0);
            }
        }
    }

    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.notifyTitle);
        builder.setMessage(R.string.notifyMsg);

        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        builder.setPositiveButton(R.string.setting,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                });

        builder.setCancelable(false);

        builder.show();
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}


