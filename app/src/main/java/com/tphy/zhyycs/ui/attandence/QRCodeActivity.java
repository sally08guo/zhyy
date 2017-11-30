package com.tphy.zhyycs.ui.attandence;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.jimmy.common.util.ToastUtils;
import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.ui.approval.activity.AttendanceItemActivity;
import com.tphy.zhyycs.ui.approval.bean.SignInInfor;
import com.tphy.zhyycs.ui.attandence.adapter.QrCheckAdapter;
import com.tphy.zhyycs.ui.attandence.zxing.encoding.EncodingHandler;
import com.tphy.zhyycs.ui.base_wang.BaseAppCompatActivity;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.CustomProgressDialog;
import com.tphy.zhyycs.utils.StringCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

public class QRCodeActivity extends BaseAppCompatActivity {

    @BindView(R.id.qr_tv_check)
    TextView qr_tv_check;
    @BindView(R.id.qr_tv_team)
    TextView qr_tv_team;
    @BindView(R.id.qr_recycler)
    RecyclerView qr_recycler;
    @BindView(R.id.iv_qrCode)
    ImageView iv_qrCode;
    @BindView(R.id.qr_tv_summary)
    TextView tv_summary;

    private List<SignInInfor> signInInforList;
    private QrCheckAdapter qrCheckAdapter;
    protected static final int PERMISSON_REQUESTCODE = 57;

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    protected boolean isNeedCheck = true;
    private String qrCodeContent;
    private String userQRCodeCode;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_qrcode;
    }

    @Override
    protected void initViewsAndEvents() {
        signInInforList = new ArrayList<>();
        Intent intent = getIntent();
        if (null != intent.getStringExtra("userQRCodeCode")) {
            userQRCodeCode = intent.getStringExtra("userQRCodeCode");
        }
        getQRCodePunchInfo();
        qr_recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        qrCheckAdapter = new QrCheckAdapter(this, signInInforList);
        qr_recycler.setAdapter(qrCheckAdapter);

    }


    @Override
    protected int getToolbarType() {
        return BALD_TOOLBAR;
    }


    @OnClick({R.id.qr_tv_check, R.id.qr_fab, R.id.toolbar_iv_back, R.id.toolbar_tv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.qr_tv_check:
                Intent intent = new Intent(QRCodeActivity.this, AttendanceItemActivity.class);
                intent.putExtra("QRCode", qrCodeContent);
                startActivity(intent);
                break;
            case R.id.qr_fab:
                getQRCodePunchInfo();
                break;
            case R.id.toolbar_iv_back:
                finish();
                break;
            case R.id.toolbar_tv_back:
                finish();
                break;
        }
    }


    private void initWebView() {
//        WebSettings settings = qr_web.getSettings();
//        settings.setUseWideViewPort(true);
//        settings.setLoadWithOverviewMode(true);
//        qr_web.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                return true;
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                if (null != loadingDialog) {
//                    loadingDialog.dismiss();
//                }
//                super.onPageFinished(view, url);
//            }
//        });
//        qr_web.loadUrl(qrCodeUrl);
//        qr_web.setInitialScale();
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
                    initWebView();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<>();
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
                e.printStackTrace();
            }
        }
        return needRequestPermissonList;
    }

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
                                           @NonNull String[] permissions, @NonNull int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                showMissingPermissionDialog();
                isNeedCheck = false;
            } else {
                initWebView();
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

    public void getQRCodePunchInfo() {
        final Dialog loadingDialog = CustomProgressDialog.createLoadingDialog(this, "");
        loadingDialog.show();
        signInInforList.clear();
        Map<String, String> params = new HashMap<>();
        params.put("paraJson", Common.params("LoginUserCode", code, "UserQRCodeCode", userQRCodeCode));
        Log.e("参数", Common.params("LoginUserCode", code, "UserQRCodeCode", userQRCodeCode));
        String url = DemoApplication.serviceUrl + "/GetQRCodePunchInfo";
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
//                        ToastUtils.showToast(getContext(), e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String success, msg;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                JSONObject result = jsonObject.getJSONObject("result");
                                /* "QRCodeUrl": "UploadFile\\QRCode\\1.jpg",//二维码地址
                                    "DeptName": "数字化医院",
                                    "TypeName": "上班签到",
                                    "QRCode":"二维码扫描出来的内容",*/
                                String deptName = result.getString("DeptName");
                                String typeName = result.getString("TypeName");
                                String time = result.getString("SendTime");
                                qrCodeContent = result.getString("QRCode");
                                JSONArray punchLish = result.getJSONArray("PunchLish");
                                int signinCount = 0;
                                int buqianCount = 0;
                                for (int i = 0; i < punchLish.length(); i++) {
                                    JSONObject object = punchLish.getJSONObject(i);
                                    boolean isScreen = object.getBoolean("IsScreen");
                                    String name = object.getString("Name");
                                    String sendTime = object.getString("SendTime");
                                    String phoneType = object.getString("PhoneType");
                                    SignInInfor signInInfor = new SignInInfor();
                                    signInInfor.setName(name);
                                    signInInfor.setScreen(isScreen);
                                    signInInfor.setPhoneType(phoneType);
                                    signInInfor.setSendTime(sendTime);
                                    signInInforList.add(signInInfor);
                                    /*判断签到和补签的人数*/
                                    if (null != sendTime && (!("").equals(sendTime))) {
                                        signinCount++;
                                    }
                                    if ("B".equals(phoneType)) {
                                        buqianCount++;
                                    }
                                }
                                if (null != qrCheckAdapter) {
                                    qrCheckAdapter.updateList(signInInforList);
                                }

                                String type = "（" + typeName + "）";
                                tv_title.setText(time.substring(0, 10) + type);
                                qr_tv_team.setText(deptName + type);
                                String summary = "共签到：" + signinCount + "人" + "  补签：" + buqianCount
                                        + "人" + "  未签：" + (signInInforList.size() - signinCount) + "人";
                                tv_summary.setText(summary);
                                        /*这里是生成二维码的代码*/
                                try {
                                    Bitmap mBitmap = EncodingHandler.createQRCode(qrCodeContent, mScreenWidth / 2);
                                    if (mBitmap != null) {
                                        iv_qrCode.setImageBitmap(mBitmap);
                                    }
                                } catch (WriterException e) {
                                    e.printStackTrace();
                                }
                                loadingDialog.dismiss();
                            } else {
                                loadingDialog.dismiss();
                                ToastUtils.showToast(QRCodeActivity.this, msg);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
