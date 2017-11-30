package com.tphy.zhyycs.ui.personal.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.tphy.zhyycs.LoginActivity;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.utils.MyDialog;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by Administrator on 2017\10\17 0017.
 */

public class SettingActivity extends Activity implements View.OnClickListener {

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
    @BindView(R.id.lyt_xgmm)
    LinearLayout lytXgmm;
    @BindView(R.id.lyt_quit)
    LinearLayout lytQuit;

    private Context context;
    private SharedPreferences sp;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("close_main")) {
                finish();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTitle.setText("设置");
        context = SettingActivity.this;
        sp = context.getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);
        IntentFilter itf = new IntentFilter("close_main");
        registerReceiver(receiver, itf);
    }

    @OnClick({R.id.lyt_quit, R.id.lyt_xgmm, R.id.tv_back, R.id.tv_back_img})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyt_quit:
                View view = LayoutInflater.from(this).inflate(R.layout.dialog_common, null);
                final MyDialog dialog = new MyDialog(this, view, R.style.dialog);
                ImageView iv = (ImageView) view.findViewById(R.id.iv_dismiss);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                TextView tv = (TextView) view.findViewById(R.id.tv_content);
                tv.setText("退出登录");
                Button btn = (Button) view.findViewById(R.id.btn_save);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sp.edit().clear().commit();
                        setAlias();
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            case R.id.lyt_xgmm:   // 修改密码
                Intent it = new Intent(context, UpdatePwdActivity.class);
                startActivity(it);
                break;
            case R.id.tv_back:
            case R.id.tv_back_img:
                finish();
                break;
        }
    }

    /**
     * 退出极光推送
     */
    private void setAlias() {
        String alias = "pmpmpmpm";
        if (TextUtils.isEmpty(alias)) {
            return;
        }
        // 调用 Handler 来异步设置别名
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002:
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 100);
                    break;
                default:
                    break;
            }
        }
    };
    private static final int MSG_SET_ALIAS = 1001;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    // 调用 JPush 接口来设置别名。
                    JPushInterface.setAliasAndTags(context,
                            (String) msg.obj,
                            null,
                            mAliasCallback);
                    Intent it2 = new Intent("drop_out");
                    sendBroadcast(it2);
                    Intent it = new Intent(context, LoginActivity.class);
                    startActivity(it);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }


}
