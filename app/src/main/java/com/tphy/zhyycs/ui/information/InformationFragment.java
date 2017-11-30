package com.tphy.zhyycs.ui.information;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.Information;
import com.tphy.zhyycs.ui.announce.AnnouceDetailsActivity;
import com.tphy.zhyycs.ui.attandence.AttendInfoActivity;
import com.tphy.zhyycs.ui.attandence.QRCodeActivity;
import com.tphy.zhyycs.ui.information.adapter.InformationAdapter;
import com.tphy.zhyycs.ui.work.activity.TaskDetailsActivity;
import com.tphy.zhyycs.ui.work.activity.TaskReviewActivity;
import com.tphy.zhyycs.ui.work.activity.TaskReviewOneActivity;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.CustomProgressDialog;
import com.tphy.zhyycs.utils.StringCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONArray;
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

public class InformationFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

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
    @BindView(R.id.rb_task)
    RadioButton rbTask;
    @BindView(R.id.rb_gonggao)
    RadioButton rbGonggao;
    @BindView(R.id.rb_kaoqin)
    RadioButton rbKaoqin;
    @BindView(R.id.rg_task)
    RadioGroup rgTask;
    @BindView(R.id.listview)
    SwipeMenuListView listview;
    @BindView(R.id.al_swipe_fresh)
    SwipeRefreshLayout alSwipeFresh;
    @BindView(R.id.empty)
    LinearLayout empty;
    Unbinder unbinder;
    private Dialog pd;
    private SharedPreferences sp;
    Context context;
    InformationAdapter adapter;
    Common common;
    private List<Information> list_task = new ArrayList<>();
    private List<Information> list_gonggao = new ArrayList<>();
    private List<Information> list_kaoqin = new ArrayList<>();
    private String str_type = "1";

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("new_information")) {
                Log.e("WQ", "收到新消息");
                GetMessageList();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_information, container, false);
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
        GetMessageList();

        alSwipeFresh.setOnRefreshListener(this);
        alSwipeFresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);

        IntentFilter itf = new IntentFilter("new_information");
        getActivity().registerReceiver(receiver, itf);
    }

    private void initView() {
        context = getActivity();
        pd = CustomProgressDialog.createLoadingDialog(context, "正在加载中...");
        common = new Common(getActivity());
        sp = context.getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);
        tvTitle.setText("消息");
        tvBack.setVisibility(View.GONE);
        tvBackImg.setVisibility(View.GONE);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (str_type.equals("1")) {  // 任务
                    Information inf = list_task.get(position);
                    if (inf.getType().equals("1")) {// 待审核
                        if (inf.getIsCPCheck().equals("0")) {   // 测试审核
                            Intent it = new Intent(context, TaskReviewOneActivity.class);
                            it.putExtra("TaskCode", inf.getCode());
                            startActivity(it);
                        } else {
                            Intent it = new Intent(context, TaskReviewActivity.class);
                            it.putExtra("TaskCode", inf.getCode());
                            it.putExtra("ProjectCode", inf.getProjectCode());
                            it.putExtra("TaskStatus", "待审核");
                            it.putExtra("CPCheckUserCode", inf.getCPCheckUserCode());
                            it.putExtra("CPCheckUserName", inf.getCPCheckUserName());
                            startActivity(it);
                        }
                    } else if (inf.getType().equals("2")) {// 待办
                        Intent it = new Intent(context, TaskDetailsActivity.class);
                        it.putExtra("task_type", "1");
                        it.putExtra("TaskCode", inf.getCode());
                        it.putExtra("ProjectName", inf.getProjectName());
                        it.putExtra("ProductName", inf.getProductName());
                        it.putExtra("TaskStatus", "待办");
                        startActivity(it);
                    }
                } else if (str_type.equals("2")) {   //考勤
                    Information inf = list_kaoqin.get(position);
                    Intent it = new Intent(context, QRCodeActivity.class);
                    it.putExtra("userQRCodeCode", inf.getCode());
                    startActivity(it);
                } else if (str_type.equals("3")) {
                    Information inf = list_gonggao.get(position);
                    Intent it = new Intent(context, AnnouceDetailsActivity.class);
                    it.putExtra("NoticeCode", inf.getCode());
                    it.putExtra("from", "information");
                    startActivity(it);
                }
            }
        });

        rgTask.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_task:  // 任务
                        str_type = "1";
                        adapter = new InformationAdapter(getActivity(), list_task);
                        listview.setAdapter(adapter);
                        if (list_task.size() > 0) {
                            empty.setVisibility(View.GONE);
                        } else {
                            empty.setVisibility(View.VISIBLE);
                        }
                        break;
                    case R.id.rb_kaoqin: //考勤
                        str_type = "2";
                        adapter = new InformationAdapter(getActivity(), list_kaoqin);
                        listview.setAdapter(adapter);
                        if (list_kaoqin.size() > 0) {
                            empty.setVisibility(View.GONE);
                        } else {
                            empty.setVisibility(View.VISIBLE);
                        }
                        break;
                    case R.id.rb_gonggao: //公告
                        str_type = "3";
                        adapter = new InformationAdapter(getActivity(), list_gonggao);
                        listview.setAdapter(adapter);
                        if (list_gonggao.size() > 0) {
                            empty.setVisibility(View.GONE);
                        } else {
                            empty.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            }
        });
    }

    /**
     * 获取项目列表
     */
    private void GetMessageList() {
//        list_kaoqin.clear();
        list_task.clear();
        list_gonggao.clear();
        pd.show();
        Map<String, String> params = new HashMap<>();
        params.put("paraJson", Common.params("LoginUserCode", sp.getString("Code", "")));
        Log.e("传的参数", Common.params("LoginUserCode", sp.getString("Code", "")));
        String url = DemoApplication.serviceUrl + "/GetMessageList";
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
                            result = jsonObject.getString("result");
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                JSONObject js = new JSONObject(result);
                                JSONArray jsonArray = new JSONArray(js.getString("WaitCheckTask"));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    Information inf = new Information();
                                    inf.setCode(jsonObject1.getString("Code"));
                                    inf.setContent(jsonObject1.getString("Content"));
                                    inf.setSendTime(jsonObject1.getString("SendTime"));
                                    inf.setProjectName(jsonObject1.getString("ProjectName"));
                                    inf.setProductName(jsonObject1.getString("ProductName"));
                                    inf.setProjectCode(jsonObject1.getString("ProjectCode"));
                                    inf.setIsCPCheck(jsonObject1.getString("IsCPCheck"));
                                    inf.setCPCheckUserCode(jsonObject1.getString("CPCheckUserCode"));
                                    inf.setCPCheckUserName(jsonObject1.getString("CPCheckUserName"));
                                    inf.setStatusName(jsonObject1.getString("StatusName"));
                                    inf.setType("1");
                                    list_task.add(inf);
                                }
                                JSONArray jsonArray1 = new JSONArray(js.getString("ActionRequiredTask"));
                                for (int j = 0; j < jsonArray1.length(); j++) {
                                    JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                                    Information inf = new Information();
                                    inf.setCode(jsonObject1.getString("Code"));
                                    inf.setContent(jsonObject1.getString("Content"));
                                    inf.setSendTime(jsonObject1.getString("SendTime"));
                                    inf.setProjectName(jsonObject1.getString("ProjectName"));
                                    inf.setProductName(jsonObject1.getString("ProductName"));
                                    inf.setStatusName(jsonObject1.getString("StatusName"));
                                    inf.setType("2");
                                    list_task.add(inf);
                                }
                                JSONArray jsonArray3 = new JSONArray(js.getString("WaitReadNotice"));
                                for (int j = 0; j < jsonArray3.length(); j++) {
                                    JSONObject jsonObject1 = jsonArray3.getJSONObject(j);
                                    Information inf = new Information();
                                    inf.setCode(jsonObject1.getString("Code"));
                                    inf.setContent(jsonObject1.getString("Title"));
                                    inf.setSendTime(jsonObject1.getString("SendTime"));
                                    inf.setStatusName(jsonObject1.getString("StatusName"));
                                    inf.setType("4");
                                    list_gonggao.add(inf);
                                }
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        getqrCodeList();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        getActivity().unregisterReceiver(receiver);
    }

    @OnClick({R.id.img_right})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_right:  // 消息历史
                break;
        }
    }

    @Override
    public void onRefresh() {
        GetMessageList();
    }

    private void getqrCodeList() {
        Log.e("WQ", "进入了GetMyQRCode");
        list_kaoqin.clear();
        Map<String, String> params = new HashMap<>();
        params.put("paraJson", Common.params("LoginUserCode", sp.getString("Code", "")));
        String url = DemoApplication.serviceUrl + "/GetMyQRCode";
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

                        String success, msg;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                JSONArray resultArray = jsonObject.getJSONArray("result");
                                for (int i = 0; i < resultArray.length(); i++) {
                                    JSONObject object = resultArray.getJSONObject(i);
                                    String code = object.getString("Code");
                                    String sendTime = object.getString("SendTime");
                                    String name = object.getString("Name");
                                    Information information = new Information();
                                    information.setCode(code);
                                    information.setSendTime(sendTime);
//                                    information.setName(name);
                                    information.setType("3");
                                    information.setContent("考勤打卡");
                                    /*projectname属性，杜撰了这个字符串 ↓*/
                                    information.setProjectName("二维码签到");
                                    /*把签到类型加入了statusname属性中 ↓*/
                                    information.setStatusName(name);
                                    list_kaoqin.add(information);
                                }

                                /*放到这里来了*/
                                if (str_type.equals("1")) {
                                    adapter = new InformationAdapter(getActivity(), list_task);
                                    if (list_task.size() > 0) {
                                        empty.setVisibility(View.GONE);
                                    } else {
                                        empty.setVisibility(View.VISIBLE);
                                    }
                                } else if (str_type.equals("2")) {
                                    adapter = new InformationAdapter(getActivity(), list_kaoqin);
                                    if (list_kaoqin.size() > 0) {
                                        empty.setVisibility(View.GONE);
                                    } else {
                                        empty.setVisibility(View.VISIBLE);
                                    }
                                } else if (str_type.equals("3")) {
                                    if (list_gonggao.size() > 0) {
                                        empty.setVisibility(View.GONE);
                                    } else {
                                        empty.setVisibility(View.VISIBLE);
                                    }
                                    adapter = new InformationAdapter(getActivity(), list_gonggao);
                                }
                                listview.setAdapter(adapter);
                                alSwipeFresh.setRefreshing(false);
                                rbTask.setText("任务(" + list_task.size() + ")");
                                rbGonggao.setText("公告(" + list_gonggao.size() + ")");
                                rbKaoqin.setText("考勤(" + list_kaoqin.size() + ")");
                                pd.dismiss();

                                /*  放到这里来了*/
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
