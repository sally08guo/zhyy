package com.tphy.zhyycs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tphy.zhyycs.model.User;
import com.tphy.zhyycs.server.UpdateThread;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.CustomProgressDialog;
import com.tphy.zhyycs.utils.MyDialog;
import com.tphy.zhyycs.utils.StringCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.Call;

/**
 * Created by Administrator on 2017\10\26 0026.
 */

public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Context context;
    public static SharedPreferences sp;
    private SharedPreferences.Editor editor;
    Dialog pd;
    public static List<User> list_user = new ArrayList<User>();
    private String currentUsername;
    private ImageButton iv_username_del, iv_password_del;

    private int downLoadFileSize;
    private int fileSize;
    int result;
    private ProgressBar pgBar;
    private TextView tvNumber;
    private MyDialog dialog;
    private Button btn_login;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("DownLoad_Login")) {
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_download, null);
                dialog = new MyDialog(context, view, R.style.dialog);
                dialog.show();
                pgBar = (ProgressBar) view.findViewById(R.id.pgBar);
                tvNumber = (TextView) view.findViewById(R.id.tv_number);
                dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initData();
        //这里以ACCESS_COARSE_LOCATION为例
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION")
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_COARSE_LOCATION"},
                    100);//自定义的code
        }
        Common.isGrantExternalRW(this);
    }

    private void initView() {
        usernameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        iv_username_del = (ImageButton) findViewById(R.id.iv_username_del);
        iv_password_del = (ImageButton) findViewById(R.id.iv_password_del);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        iv_username_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameEditText.setText("");
            }
        });
        iv_password_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordEditText.setText("");
            }
        });
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordEditText.setText(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    iv_username_del.setVisibility(View.VISIBLE);
                } else {
                    iv_username_del.setVisibility(View.INVISIBLE);
                }
            }
        });
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    iv_password_del.setVisibility(View.VISIBLE);
                } else {
                    iv_password_del.setVisibility(View.INVISIBLE);
                }
            }
        });
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))) {
                    login();
                    return true;
                } else {
                    return false;
                }
            }
        });
        IntentFilter itf = new IntentFilter("DownLoad_Login");
        registerReceiver(receiver, itf);
    }

    private void initData() {
        context = LoginActivity.this;// 上下文
        pd = CustomProgressDialog.createLoadingDialog(this, "登陆中·");
        sp = getSharedPreferences("CYT_USERINFO", context.MODE_PRIVATE);
        editor = sp.edit();
        // 检测是否需要更新
        UpdateThread updateThread = new UpdateThread(this, handler, "grzx", "1");
        updateThread.start();

    }


    public void login() {
        currentUsername = usernameEditText.getText().toString().trim();
        final String currentPassword = passwordEditText.getText().toString().trim();
        pd.show();
        Map<String, String> params = new HashMap<>();
        params.put("paraJson", Common.params("LoginUserCode", currentUsername, "PassWord", currentPassword));
        String url = DemoApplication.serviceUrl + "/Login";
        Log.e("登录", Common.params("LoginUserCode", currentUsername, "PassWord", currentPassword));
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(LoginActivity.this, "登录失败！", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        sp.edit().clear().commit();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String success = "", result = "", msg = "";
                        pd.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //result = jsonObject.getString("result");
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                JSONArray json = jsonObject.getJSONArray("result");
                                for (int i = 0; i < 1; i++) {  //json.length()
                                    JSONObject js = json.getJSONObject(i);
                                    // 把解析的数据以键值对的形式存入Editor中
                                    editor.putString("Code", js.getString("Code"));// 账号
                                    editor.putString("Name", js.getString("Name"));// 姓名
                                    editor.putString("DeptName", js.getString("DeptName"));// 部门
                                    editor.putString("DutyName", js.getString("DutyName"));// 职务
                                    editor.putString("Permission", js.getString("Permission"));
                                    editor.putString("OriginalCode", js.getString("OriginalCode"));
                                    editor.commit();
                                    setAlias();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                        }
                    }
                });
    }

    // 这是来自 JPush Example 的设置别名的 Activity 里的代码。一般 App 的设置的调用入口，在任何方便的地方调用都可以。
    private void setAlias() {
        String alias = sp.getString("OriginalCode", "");
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
                    logs = "Set tag and alias success";
                    Log.e("logs", "1111111111111");
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.e("logs", "2222222222222222");
                    // 延迟 60 秒来调用 Handler 设置别名
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 100);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e("logs", "3333333333333333");
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
                    JPushInterface.setAliasAndTags(getApplicationContext(),
                            (String) msg.obj,
                            null,
                            mAliasCallback);
                    Intent it = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(it);
                    finish();
                    break;
                default:
                    Log.e("222222", "Unhandled msg - " + msg.what);
            }
        }
    };


    /**
     * 创建一个线程来处理进度
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 定义一个Handler，用于处理下载线程与UI间通讯
            if (!Thread.currentThread().isInterrupted()) {
                try {
                    switch (msg.what) {
                        case 0:
                        case 1:
                            downLoadFileSize = msg.getData().getInt("downLoadFileSize");
                            fileSize = msg.getData().getInt("fileSize");
                            if (downLoadFileSize != 0 && fileSize != 0) {
                                result = (int) (((float) downLoadFileSize / fileSize) * 100);
                            }
                            tvNumber.setText(result + "%");
                            pgBar.setProgress(result);
                            // 设置当前值为count
                            break;
                        case 2:
                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this, "文件下载完成", Toast.LENGTH_SHORT).show();
                            fileSize = 0;
                            // 打开文件进行安装
                            Common.installFile(LoginActivity.this, msg.getData().getString("fileString"));
                            break;
                        case -1:
                            fileSize = 0;
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 发送信息给主ui页面
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
