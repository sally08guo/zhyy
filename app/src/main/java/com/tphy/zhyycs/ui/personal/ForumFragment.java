package com.tphy.zhyycs.ui.personal;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.ui.announce.AnnounceListActivity;
import com.tphy.zhyycs.ui.approval.activity.AttendanceAllActivity;
import com.tphy.zhyycs.ui.approval.activity.AttendanceByDeptActivity;
import com.tphy.zhyycs.ui.approval.activity.AttendanceInfoActivity;
import com.tphy.zhyycs.ui.personal.activity.SettingActivity;
import com.tphy.zhyycs.ui.personal.activity.TaskHistory;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.CustomProgressDialog;
import com.tphy.zhyycs.utils.FitStateUI;
import com.tphy.zhyycs.utils.StringCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;

/**
 * 项目名称：个人中心
 * 创建人：cdss
 * 创建时间：2017-08-14 13:41
 * 修改人：cdss
 * 修改时间：2017-08-14 13:41
 * 修改备注：
 */

public class ForumFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.lyt_setting)
    LinearLayout lytSetting;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_zhiwu)
    TextView tvZhiwu;
    @BindView(R.id.lyt_task_fq)
    LinearLayout lytTaskFq;
    @BindView(R.id.lyt_task_shenhe)
    LinearLayout lytTaskShenhe;
    @BindView(R.id.lyt_task_cy)
    LinearLayout lytTaskCy;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.lyt_gonggao)
    LinearLayout lytGonggao;
    @BindView(R.id.lyt_kq)
    LinearLayout lytKq;
    @BindView(R.id.view_top)
    View viewTop;
    @BindView(R.id.view_bottom)
    View viewBottom;


    Unbinder unbinder;
    private SharedPreferences sp;
    private Dialog pd;
    private String str_numbers;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("drop_out")) {
                getActivity().finish();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_person, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sp = getActivity().getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);
        pd = CustomProgressDialog.createLoadingDialog(getActivity(), "正在加载中...");
        tvName.setText(sp.getString("Name", ""));
        tvZhiwu.setText(sp.getString("DutyName", ""));
        IntentFilter itf = new IntentFilter("drop_out");
        getActivity().registerReceiver(receiver, itf);

        if (sp.getString("Permission", "").contains("MyDeptPunchCollect")) {
            lytKq.setVisibility(View.VISIBLE);
            viewTop.setVisibility(View.VISIBLE);
            viewBottom.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.lyt_kq, R.id.lyt_setting, R.id.lyt_task_fq, R.id.lyt_task_shenhe, R.id.lyt_task_cy, R.id.lyt_gonggao})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyt_gonggao:  // 公告列表  AttendInfoActivity
                Intent it2 = new Intent(getContext(), AnnounceListActivity.class);
                startActivity(it2);
                break;
            case R.id.lyt_setting:
                Intent it = new Intent(getContext(), SettingActivity.class);
                startActivity(it);
                break;
            case R.id.lyt_task_fq:  // 我发起的
                Intent it3 = new Intent(getContext(), TaskHistory.class);
                it3.putExtra("TaskType", "2");
                startActivity(it3);
                break;
            case R.id.lyt_task_shenhe: // 我审核的
                Intent it4 = new Intent(getContext(), TaskHistory.class);
                it4.putExtra("TaskType", "3");
                it4.putExtra("modify", "modify");
                startActivity(it4);
                break;
            case R.id.lyt_task_cy:   // 我参与的
                Intent it5 = new Intent(getContext(), TaskHistory.class);
                it5.putExtra("TaskType", "1");
                startActivity(it5);
                break;
            case R.id.lyt_kq:
//                Intent it6 = new Intent(getContext(), AttendanceAllActivity.class);
//                startActivity(it6);

                Intent it6 = new Intent(getContext(), AttendanceByDeptActivity.class);
                startActivity(it6);

                break;

        }
    }


    /**
     * 获取所有人员信息
     */
    private void GetNoReadCount() {
        pd.show();
        Map<String, String> params = new HashMap<>();
        Map<String, String> pa = new HashMap<>();
        pa.put("LoginUserCode", sp.getString("Code", ""));
        params.put("paraJson", Common.MaptoJson(pa));
        String url = DemoApplication.serviceUrl + "/GetNoReadCount";
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
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
                                str_numbers = jsonObject.getString("result");
                                tvNumber.setText(str_numbers + "条");
                                pd.dismiss();
                            } else {
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        } catch (Exception e) {
                        }
                    }
                });
    }


    @Override
    public void onResume() {
        super.onResume();
        GetNoReadCount();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(receiver);
    }
}
