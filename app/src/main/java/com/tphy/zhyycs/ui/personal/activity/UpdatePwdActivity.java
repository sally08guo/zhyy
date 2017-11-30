package com.tphy.zhyycs.ui.personal.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.LoginActivity;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.CustomProgressDialog;
import com.tphy.zhyycs.utils.StringCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Administrator on 2017\9\16 0016.   修改密码
 */

public class UpdatePwdActivity extends Activity implements View.OnClickListener {

    @BindView(R.id.tv_back_img)
    TextView tvBackImg;
    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.img_right)
    ImageView imgRight;
    @BindView(R.id.btn_right)
    Button btnRight;
    @BindView(R.id.btn_right_2)
    Button btnRight2;
    @BindView(R.id.edt_oldmm)
    EditText edtOldmm;
    @BindView(R.id.edt_newmm)
    EditText edtNewmm;
    @BindView(R.id.edt_againmm)
    EditText edtAgainmm;
    @BindView(R.id.btn_save)
    Button btnSave;
    private Dialog pd;
    private SharedPreferences sp;
    private Context context;
    private String str_oldpwd = "", str_newpwd = "", str_againpwd = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatepwd);
        ButterKnife.bind(this);
        context = UpdatePwdActivity.this;
        initView();
    }

    private void initView() {
        pd = CustomProgressDialog.createLoadingDialog(this, "正在加载中...");
        sp = getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);
        tvTitle.setText("修改密码");
    }

    @OnClick({R.id.tv_back, R.id.tv_back_img, R.id.btn_save})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
            case R.id.tv_back_img:
                finish();
                break;
            case R.id.btn_save:
                str_oldpwd = edtOldmm.getText().toString().trim();
                str_newpwd = edtNewmm.getText().toString().trim();
                str_againpwd = edtAgainmm.getText().toString().trim();
                if (str_newpwd.isEmpty() || str_oldpwd.isEmpty() || str_againpwd.isEmpty()) {
                    Toast.makeText(context, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else if (!str_newpwd.equals(str_againpwd)) {
                    Toast.makeText(context, "新密码两次输入内容不一致", Toast.LENGTH_SHORT).show();
                } else {
                    UpdatePwd(str_newpwd, str_oldpwd);
                }
                break;
        }
    }


    private void UpdatePwd(String str_newpwd, String str_oldpwd) {
        pd.show();
        Map<String, String> params = new HashMap<>();
        Map<String, String> mp = new HashMap<>();
        mp.put("LoginUserCode", sp.getString("Code", ""));
        mp.put("newPwd", str_newpwd);
        mp.put("oldPwd", str_oldpwd);
        params.put("paraJson", Common.MaptoJson(mp));
        String url = DemoApplication.serviceUrl + "/UpdatePwd";
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("onError", "aaaa");
                        pd.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        String success = "", result = "", msg = "";
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                Toast.makeText(context, "修改密码成功!", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                                finish();
                                Intent it = new Intent("close_main");
                                sendBroadcast(it);
                                sp.edit().clear().commit();
                                Intent it2 = new Intent(context, LoginActivity.class);
                                startActivity(it2);
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        } catch (Exception e) {
                        }
                    }
                });
    }
}
