package com.tphy.zhyycs.server;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.tphy.zhyycs.DemoApplication;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CallWebService {
    private static final String nameSpace = "http://tempuri.org/";
    @SuppressWarnings("unused")
    private DemoApplication myapp;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object callService(String methodName, HashMap<String, Object> params, Context c, String serviceUrl) {
        Object result = null;
        try {
            if (isNetworkAvailable(c)) {// IUSR
                String SOAP_ACTION = nameSpace + methodName;
                SoapObject request;
                request = new SoapObject(nameSpace, methodName);

                // 请求参数
                try {
                    if (params != null && !params.isEmpty()) {
                        for (Iterator it = params.entrySet().iterator(); it.hasNext(); ) {
                            Entry<String, Object> e = (Entry) it.next();
                            request.addProperty(e.getKey().toString(), e.getValue());
                        }
                    }
                } catch (Exception e) {
                    return null;
                }

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.headerOut = new Element[1];
                envelope.headerOut[0] = buildAuthHeader();
                envelope.bodyOut = request;
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(serviceUrl, 20000);

                try {
                    ht.call(SOAP_ACTION, envelope);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                result = (Object) envelope.bodyIn;
            } else {

                return null;
            }
        } catch (Exception e) {

            return null;
        }

        return result;
    }

    public static boolean isNetworkAvailable(Context c) {
        try {
            ConnectivityManager mgr = (ConnectivityManager) c.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] info = mgr.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    private static Element buildAuthHeader() {
        Element hElement = new Element().createElement("http://tempuri.org/", "AuthHead");
        Element passwordElement = new Element().createElement("http://tempuri.org/", "passWord");
        passwordElement.setName("passWord");
        passwordElement.addChild(org.kxml2.kdom.Node.TEXT, "tphy");
        hElement.addChild(org.kxml2.kdom.Node.ELEMENT, passwordElement);
        return hElement;
    }

    public static String callService1(String methodName, HashMap<String, Object> params, Context c, String serviceUrl) {
        String result;
        try {
            if (isNetworkAvailable(c)) {// IUSR
                String SOAP_ACTION = nameSpace + methodName;
                SoapObject request;
                request = new SoapObject(nameSpace, methodName);
                // 请求参数
                if (params != null && !params.isEmpty()) {
                    for (@SuppressWarnings("rawtypes")
                         Iterator it = params.entrySet().iterator(); it.hasNext(); ) {
                        @SuppressWarnings({"unchecked", "rawtypes"})
                        Entry<String, Object> e = (Entry) it.next();
                        request.addProperty(e.getKey().toString(), e.getValue());
                    }
                }
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.headerOut = new Element[1];
                envelope.headerOut[0] = buildAuthHeader();
                envelope.bodyOut = request;
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(serviceUrl, 10000);
                ht.call(SOAP_ACTION, envelope);
                SoapObject strresult = (SoapObject) envelope.bodyIn;
                result = strresult.getProperty(0).toString();
            } else {

                return "-100";
            }
        } catch (Exception e) {

            return "-1";
        }

        return result;
    }

    /**
     * 使用OKhttp调取服务
     *
     * @param methodName
     * @param json
     * @param c
     * @param serviceUrl
     * @return
     */
    public static String callService2(String methodName, String json, Context c, String serviceUrl) {
        String result;
        if (isNetworkAvailable(c)) {// IUSR
            try {
                String url = serviceUrl + "/" + methodName;
                OkHttpClient client = new OkHttpClient();
                RequestBody body = new FormBody.Builder().add("paraJson", json).build();
                Request request = new Request.Builder()
                        .url(url).post(body).build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    result = response.body().string().replaceAll("(<[^>]*>)", "");//使用正则表达式去掉服务返回字段中xml部分，得到json对象;
                } else {
                    result = response.body().string().replaceAll("(<[^>]*>)", "");
                }
            } catch (Exception ex) {
                result = ex.toString();
            }
        } else {
            result = "Network is Unavailable";
        }
        return result;
    }

}
