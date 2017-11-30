package com.tphy.zhyycs.ui.work;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.Project;
import com.tphy.zhyycs.ui.work.activity.TaskListActivity;
import com.tphy.zhyycs.ui.work.adapter.PatientMainAdapter;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.CustomProgressDialog;
import com.tphy.zhyycs.utils.MyGridView;
import com.tphy.zhyycs.utils.StringCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;

/**
 * 项目名称：zhyy
 * 创建人：cdss
 * 创建时间：2017-08-14 13:41
 * 修改人：cdss
 * 修改时间：2017-08-14 13:41
 * 修改备注：
 */

public class PatientMainFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

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
    @BindView(R.id.gridView)
    MyGridView gridView;
    @BindView(R.id.al_swipe_fresh)
    SwipeRefreshLayout alSwipeFresh;
    @BindView(R.id.empty)
    LinearLayout empty;
    @BindView(R.id.iv_main_add)
    ImageView ivMainAdd;
    Unbinder unbinder;
    private List<Project> list = new ArrayList<>();
    private Dialog pd;
    private SharedPreferences sp;
    Context context;
    PatientMainAdapter adapter;
    Common common;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("PatientMain")) {
                GetXmList();
            } else if (intent.getAction().equals("Reflash")) {
                GetXmList();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_patient_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        GetXmList();

        IntentFilter itf = new IntentFilter("PatientMain");
        getActivity().registerReceiver(receiver, itf);
        IntentFilter itf2 = new IntentFilter("Reflash");
        getActivity().registerReceiver(receiver, itf2);

        alSwipeFresh.setOnRefreshListener(this);
        alSwipeFresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);

    }

    private void initView() {
        context = getActivity();
        pd = CustomProgressDialog.createLoadingDialog(context, "正在加载中...");
        common = new Common(getActivity());
        sp = context.getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);
        tvTitle.setText("智慧运营");
        tvBack.setVisibility(View.GONE);
        tvBackImg.setVisibility(View.GONE);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("点击子事件", position + "");
                Intent it = new Intent(context, TaskListActivity.class);
                it.putExtra("project_name", list.get(position).getName());
                it.putExtra("project_code", list.get(position).getCode());
                it.putExtra("task_shenhe_num", list.get(position).getCheckRequiredCount());
                startActivity(it);
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                common.showGridItem(getActivity(), view, position, list.get(position).getCode(), list.get(position).getIsAttention(), list.get(position).getProjectPermission());
                return true;
            }
        });
    }

    /**
     * 获取项目列表
     */
    private void GetXmList() {
        list.clear();
        pd = CustomProgressDialog.createLoadingDialog(context, "正在加载中...");
        pd.show();
        Map<String, String> params = new HashMap<>();
        params.put("paraJson", Common.params("LoginUserCode", sp.getString("Code", "")));
        Log.e("传的参数", Common.params("LoginUserCode", sp.getString("Code", "")));
        String url = DemoApplication.serviceUrl + "/GetProjectsAndCountByUserCode";
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
                            result = jsonObject.getString("result");
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                list = new Gson().fromJson(result, new TypeToken<List<Project>>() {
                                }.getType());
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                        }
                        if (list.size() > 0) {
                            empty.setVisibility(View.GONE);
                        } else {
                            empty.setVisibility(View.VISIBLE);
                        }
                        adapter = new PatientMainAdapter(context, list);
                        gridView.setAdapter(adapter);
                        pd.dismiss();
                        alSwipeFresh.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        getActivity().unregisterReceiver(receiver);
    }

    @OnClick({R.id.iv_main_add})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_main_add:
                common.showPopupWindow(getActivity(), ivMainAdd);
                break;
        }
    }

    @Override
    public void onRefresh() {
        GetXmList();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
