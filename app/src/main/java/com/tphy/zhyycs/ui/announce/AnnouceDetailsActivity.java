package com.tphy.zhyycs.ui.announce;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.ui.base_wang.BaseAppCompatActivity;
import com.tphy.zhyycs.utils.ActivityUtils;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.StringCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

public class AnnouceDetailsActivity extends BaseAppCompatActivity implements View.OnClickListener {

    @BindView(R.id.ad_tv_sendmen)
    TextView tv_sendmen;
    @BindView(R.id.ad_tv_time)
    TextView tv_time;
    @BindView(R.id.ad_tv_content)
    TextView tv_content;
    @BindView(R.id.toolbar_tv_button)
    TextView toolbar_tv_button;
    @BindView(R.id.ad_tv_attach)
    TextView tv_attach;
    @BindView(R.id.toolbar_tv_title)
    TextView tv_title;
    @BindView(R.id.ad_tv_title)
    TextView ad_tv_title;
    private String NoticeCode;
    private ActivityUtils activityUtils;
    private String attachCode;
    private String fileUrl;

    @Override
    protected int getToolbarType() {
        return BUTTON_TOOLBAR;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_annouce_details;
    }

    @Override
    protected void initViewsAndEvents() {
        tv_title.setText("公告详情");
        activityUtils = new ActivityUtils(this);
        Intent intent = getIntent();
        boolean isMe = intent.getBooleanExtra("isMe", false);
        if (isMe) {
            toolbar_tv_button.setVisibility(View.VISIBLE);
            toolbar_tv_button.setText("删除公告");
            toolbar_tv_button.setTextColor(getResources().getColor(R.color.kq_leave_dark));
        } else {
            toolbar_tv_button.setVisibility(View.GONE);
        }
        String code = intent.getStringExtra("NoticeCode");
        if (!code.equals("")) {
            NoticeCode = code;
            getDetails();
        }
    }

    private void getDetails() {
        SharedPreferences preferences = getSharedPreferences("CYT_USERINFO", MODE_PRIVATE);
        String code = preferences.getString("Code", "");
        Map<String, String> params = new HashMap<>();
        params.put("paraJson", Common.params("LoginUserCode", code, "NoticeCode", NoticeCode));
        String url = DemoApplication.serviceUrl + "/GetNoticeInfo";
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String success, msg;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                JSONArray array = jsonObject.getJSONArray("result");
                                JSONArray jsonArray = array.getJSONArray(0);
                                JSONObject infor = jsonArray.getJSONObject(0);
                                String title = infor.getString("Title");
                                String content = infor.getString("Content");
                                String sendTime = infor.getString("SendTime");
                                String sendUserName = infor.getString("SendUserName");
                                ad_tv_title.setText(title);
                                tv_content.setText(content);
                                tv_time.setText(sendTime);
                                tv_sendmen.setText(sendUserName);
                                JSONArray fileArray = array.getJSONArray(1);
                                if (fileArray.length() > 0) {
                                    JSONObject fileObject = fileArray.getJSONObject(0);
                                    String fileName = fileObject.getString("FileName");
                                    attachCode = fileObject.getString("Code");
                                    tv_attach.setVisibility(View.VISIBLE);
                                    tv_attach.setText(fileName);
                                    getFileURL();
                                } else {
                                    tv_attach.setVisibility(View.GONE);
                                }
                            } else {
                                activityUtils.showToast(msg);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void deleteItem(String noticeCode) {
        HashMap<String, String> params = new HashMap<>();
        params.put("paraJson", Common.params("LoginUserCode", code, "NoticeCode", noticeCode));
        String url = DemoApplication.serviceUrl + "/DeleteNotice";
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        activityUtils.showToast(e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            String msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                activityUtils.showToast("删除成功");
                                finish();
                            } else {
                                activityUtils.showToast(msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    @OnClick({R.id.toolbar_iv_back, R.id.toolbar_tv_back, R.id.toolbar_tv_button, R.id.ad_tv_attach})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_iv_back:
                finish();
                break;
            case R.id.toolbar_tv_back:
                finish();
                break;
            case R.id.toolbar_tv_button:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteItem(NoticeCode);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.setMessage("确定要删除这条公告吗？");
                alertDialog.show();
                break;
            case R.id.ad_tv_attach:
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(DemoApplication.fileUrl + fileUrl);
                intent.setData(content_url);
                startActivity(intent);
                break;
        }
    }

    private void getFileURL() {
        if (null != attachCode) {
            HashMap<String, String> params = new HashMap<>();
            Map<String, String> map = new HashMap<>();
            map.put("LoginUserCode", code);
            map.put("Code", attachCode);
            map.put("Module", "notice");
            params.put("paraJson", Common.MaptoJson(map));
            String url = DemoApplication.serviceUrl + "/GetFileURL";
            OkHttpUtils.post().url(url).params(params).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {

                }

                @Override
                public void onResponse(String response, int id) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        if ("true".equals(success)) {
                            fileUrl = jsonObject.getString("URL");
                        } else {
                            String msg = jsonObject.getString("msg");
                            activityUtils.showToast(msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

    }
}
