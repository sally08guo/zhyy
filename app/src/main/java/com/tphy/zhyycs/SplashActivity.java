package com.tphy.zhyycs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2017\10\26 0026.
 */

public class SplashActivity extends Activity {

    private SharedPreferences sp;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (sp.getString("Code", "").equals("")) {
                        Intent it = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(it);
                    } else {
                        Intent it = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(it);
                    }
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sp = getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);
        handler.sendEmptyMessageDelayed(1, 1500);
    }
}
