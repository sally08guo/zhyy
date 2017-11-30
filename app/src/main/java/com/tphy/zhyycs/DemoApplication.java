package com.tphy.zhyycs;

import android.app.Application;
import android.content.Context;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2017\10\26 0026.
 */

public class DemoApplication extends Application {

    public static Context applicationContext;
    private static DemoApplication instance;

    public static String main_logo = "测试";
    public static String serviceUrl;  //服务地址
    public static String fileUrl;

    @Override
    public void onCreate() {
        super.onCreate();
        //极光推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        if (main_logo.equals("测试")) {
            serviceUrl = "http://182.92.195.138:5012/zhyyforapp.asmx";
            fileUrl = "http://182.92.195.138:5012/";
        } else if (main_logo.equals("正式")) {
            serviceUrl = "http://182.92.195.138:5013/zhyyforapp.asmx";// 服务器地址
            fileUrl = "http://182.92.195.138:5013/";
        }

        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .addInterceptor(new LoggerInterceptor("TAG"))
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .build();
        OkHttpUtils.initClient(okHttpClient);

        applicationContext = this;
        instance = this;

    }

    public static DemoApplication getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
