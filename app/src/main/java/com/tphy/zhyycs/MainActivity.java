package com.tphy.zhyycs;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tphy.zhyycs.server.UpdateThread;
import com.tphy.zhyycs.ui.approval.AttendanceFragment;
import com.tphy.zhyycs.ui.information.InformationFragment;
import com.tphy.zhyycs.ui.personal.ForumFragment;
import com.tphy.zhyycs.ui.work.PatientMainFragment;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.MyDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.btn_conversation)
    LinearLayout btnConversation;
    @BindView(R.id.btn_kaoqin)
    LinearLayout btnKaoqin;
    @BindView(R.id.btn_main)
    LinearLayout btnMain;
    @BindView(R.id.btn_person)
    LinearLayout btnPerson;
    @BindView(R.id.main_bottom)
    LinearLayout mainBottom;
    @BindView(R.id.fragment_container)
    RelativeLayout fragmentContainer;
    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;

    private LinearLayout[] mTabs;
    private Fragment[] fragments;
    private ProgressBar pgBar;
    private TextView tvNumber;
    private MyDialog dialog;

    private int index;
    private int currentTabIndex;
    private int downLoadFileSize;
    private int fileSize;
    int result;
    private long secondTime;
    private long firstTime = 0;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("DownLoad")) {
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_download, null);
                dialog = new MyDialog(context, view, R.style.dialog);
                dialog.show();
                pgBar = (ProgressBar) view.findViewById(R.id.pgBar);
                tvNumber = (TextView) view.findViewById(R.id.tv_number);
                dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 定义一个Handler，用于处理下载线程与UI间通讯
            if (!Thread.currentThread().isInterrupted()) {
                try {
                    switch (msg.what) {
                        case 0:
                            // notification_pb.setMax(fileSize);
                        case 1:
                            downLoadFileSize = msg.getData().getInt("downLoadFileSize");
                            fileSize = msg.getData().getInt("fileSize");
                            if (downLoadFileSize != 0 && fileSize != 0) {
                                result = (int) (((float) downLoadFileSize / fileSize) * 100);
                            }
                            tvNumber.setText(result + "%");
                            pgBar.setProgress(result);
                            break;
                        case 2:
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "文件下载完成", Toast.LENGTH_SHORT).show();
                            fileSize = 0;
                            // 打开文件进行安装
                            Common.installFile(MainActivity.this, msg.getData().getString("fileString"));
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();

        // 检测是否需要更新
        UpdateThread updateThread = new UpdateThread(this, handler, "grzx", "2");
        updateThread.start();

        //这里以ACCESS_COARSE_LOCATION为例
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION")
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_COARSE_LOCATION"},
                    100);//自定义的code
        }
        Common.isGrantExternalRW(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //可在此继续其他操作。
    }

    private void initView() {
        mTabs = new LinearLayout[4];
        mTabs[0] = (LinearLayout) findViewById(R.id.btn_conversation);
        mTabs[1] = (LinearLayout) findViewById(R.id.btn_kaoqin);
        mTabs[2] = (LinearLayout) findViewById(R.id.btn_main);
        mTabs[3] = (LinearLayout) findViewById(R.id.btn_person);
        mTabs[0].setSelected(true);

        // 考勤
        AttendanceFragment attendanceFragment = new AttendanceFragment();
        // 项目
        PatientMainFragment patientFragment = new PatientMainFragment();
        // 个人中心
        ForumFragment personalFragment = new ForumFragment();
        //消息
        InformationFragment informationFragment = new InformationFragment();
        fragments = new Fragment[]{informationFragment, attendanceFragment, patientFragment, personalFragment};

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, informationFragment)
                .add(R.id.fragment_container, attendanceFragment).hide(attendanceFragment).show(informationFragment)
                .commit();

        IntentFilter itf = new IntentFilter("DownLoad");
        registerReceiver(receiver, itf);
    }


    /**
     * on tab clicked
     *
     * @param view
     */
    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_conversation:
                index = 0;
                break;
            case R.id.btn_kaoqin:
                index = 1;
                break;
            case R.id.btn_main:
                index = 2;
                break;
            case R.id.btn_person:
                index = 3;
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        mTabs[currentTabIndex].setSelected(false);
        // set current tab selected
        mTabs[index].setSelected(true);
        currentTabIndex = index;
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) { // 如果两次按键时间间隔大于2秒，则不退出
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;// 更新firstTime
                return true;
            } else { // 两次按键小于2秒时，退出应用
                finish();
            }
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}