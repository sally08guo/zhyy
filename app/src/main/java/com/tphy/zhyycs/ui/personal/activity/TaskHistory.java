package com.tphy.zhyycs.ui.personal.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.Task;
import com.tphy.zhyycs.ui.FilterActivity;
import com.tphy.zhyycs.ui.work.activity.TaskDetailsActivity;
import com.tphy.zhyycs.ui.work.activity.TaskReviewOneActivity;
import com.tphy.zhyycs.ui.work.adapter.ProjectTaskAdapter;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.CustomProgressDialog;
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
import okhttp3.Call;

/**
 * Created by Administrator on 2017\10\11 0011.   任务历史界面
 */

public class TaskHistory extends Activity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

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
    @BindView(R.id.listview)
    SwipeMenuListView listview;
    @BindView(R.id.al_swipe_fresh)
    SwipeRefreshLayout alSwipeFresh;
    @BindView(R.id.empty)
    LinearLayout empty;
    @BindView(R.id.query)
    EditText query;
    @BindView(R.id.search_clear)
    ImageButton searchClear;

    private Dialog pd;
    private SharedPreferences sp;
    Context context;
    private String str_type = "", str_search = "0";

    private List<Task> list_cy = new ArrayList<>();
    private List<Task> list_all = new ArrayList<>();
    private ProjectTaskAdapter adapter;
    private String str_start_date = "", str_end_date = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskhistory);
        ButterKnife.bind(this);
        if (null != getIntent().getStringExtra("TaskType")) {
            str_type = getIntent().getStringExtra("TaskType");
        }
        initView();
        if (str_type.equals("1")) {
            GetMyCollectByTime(); // 我参与的
            tvTitle.setText("我参与的");
        } else if (str_type.equals("2")) {
            GetMySendByTime();  // 我发起的
            tvTitle.setText("我发起的");
        } else if (str_type.equals("3")) {
            GetMyCheckTaskByTime();  // 我审核的
            tvTitle.setText("我审核的");
        }
        alSwipeFresh.setOnRefreshListener(this);
        alSwipeFresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
    }

    private void initView() {
        context = TaskHistory.this;
        pd = CustomProgressDialog.createLoadingDialog(context, "正在加载中...");
        sp = context.getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);
        btnRight.setVisibility(View.VISIBLE);
        btnRight.setText("筛选");
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (str_type.equals("3")) {
                    if (str_search.equals("0")) {
                        if (list_cy.get(position).getIsCheck().equals("0")) {
                            Intent it = new Intent(context, TaskReviewOneActivity.class);
                            it.putExtra("TaskCode", list_cy.get(position).getCode());
                            it.putExtra("TaskInfo", "TaskInfo");
                            startActivity(it);
                        } else {
                            Intent it = new Intent(context, TaskDetailsActivity.class);
                            it.putExtra("TaskCode", list_cy.get(position).getCode());
                            it.putExtra("ProjectCode", list_cy.get(position).getProjectCode());
                            it.putExtra("ProjectName", list_cy.get(position).getProjectName());
                            it.putExtra("ProductName", list_cy.get(position).getProductName());
                            it.putExtra("StatusName", list_cy.get(position).getStatusName());
                            it.putExtra("modify", "modify");
                            Log.e("项目code", list_cy.get(position).getProductCode());
                            startActivity(it);
                        }
                    } else {
                        if (list_all.get(position).getIsCheck().equals("0")) {
                            Intent it = new Intent(context, TaskReviewOneActivity.class);
                            it.putExtra("TaskCode", list_all.get(position).getCode());
                            it.putExtra("TaskInfo", "TaskInfo");
                            startActivity(it);
                        } else {
                            Intent it = new Intent(context, TaskDetailsActivity.class);
                            it.putExtra("TaskCode", list_all.get(position).getCode());
                            it.putExtra("ProjectCode", list_all.get(position).getProjectCode());
                            it.putExtra("ProjectName", list_all.get(position).getProjectName());
                            it.putExtra("ProductName", list_all.get(position).getProductName());
                            it.putExtra("StatusName", list_all.get(position).getStatusName());
                            it.putExtra("modify", "modify");
                            startActivity(it);
                        }
                    }
                } else {
                    if (str_search.equals("0")) {
                        Intent it = new Intent(context, TaskDetailsActivity.class);
                        it.putExtra("TaskCode", list_cy.get(position).getCode());
                        it.putExtra("ProjectCode", list_cy.get(position).getProjectCode());
                        it.putExtra("ProjectName", list_cy.get(position).getProjectName());
                        it.putExtra("ProductName", list_cy.get(position).getProductName());
                        if (str_type.equals("3")) {
                            it.putExtra("modify", "modify");
                        }
                        startActivity(it);
                    } else {
                        Intent it = new Intent(context, TaskDetailsActivity.class);
                        it.putExtra("TaskCode", list_all.get(position).getCode());
                        it.putExtra("ProjectCode", list_all.get(position).getProjectCode());
                        it.putExtra("ProjectName", list_all.get(position).getProjectName());
                        it.putExtra("ProductName", list_all.get(position).getProductName());
                        if (str_type.equals("3")) {
                            it.putExtra("modify", "modify");
                        }
                        Log.e("项目code", list_all.get(position).getProductCode());
                        startActivity(it);
                    }

                }

            }
        });
        query.setHint("按编号/任务描述/项目/产品等进行搜索");
        query.addTextChangedListener(watcher);

    }


    /**
     * 我参与的任务
     */
    private void GetMyCollectByTime() {
        list_cy.clear();
        pd.show();
        Map<String, String> params = new HashMap<>();
        Map<String, String> pa = new HashMap<>();
        pa.put("LoginUserCode", sp.getString("Code", ""));
        pa.put("StartTime", "");
        pa.put("EndTime", "");
        params.put("paraJson", Common.MaptoJson(pa));
        String url = DemoApplication.serviceUrl + "/GetMyCollectByTime";
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        pd.dismiss();
                        alSwipeFresh.setRefreshing(false);
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
                                list_cy = new Gson().fromJson(result, new TypeToken<List<Task>>() {
                                }.getType());
                                adapter = new ProjectTaskAdapter(context, list_cy);
                                if (list_cy.size() > 0) {
                                    empty.setVisibility(View.GONE);
                                } else {
                                    empty.setVisibility(View.VISIBLE);
                                }
                                listview.setAdapter(adapter);
                                pd.dismiss();
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                            alSwipeFresh.setRefreshing(false);

                        } catch (Exception e) {
                        }
                    }
                });
    }


    /**
     * 我发起的任务
     */
    private void GetMySendByTime() {
        list_cy.clear();
        pd.show();
        Map<String, String> params = new HashMap<>();
        Map<String, String> pa = new HashMap<>();
        pa.put("LoginUserCode", sp.getString("Code", ""));
        pa.put("StartTime", "");
        pa.put("EndTime", "");
        params.put("paraJson", Common.MaptoJson(pa));
        String url = DemoApplication.serviceUrl + "/GetMySendByTime";
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        pd.dismiss();
                        alSwipeFresh.setRefreshing(false);
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
                                list_cy = new Gson().fromJson(result, new TypeToken<List<Task>>() {
                                }.getType());
                                adapter = new ProjectTaskAdapter(context, list_cy);
                                if (list_cy.size() > 0) {
                                    empty.setVisibility(View.GONE);
                                } else {
                                    empty.setVisibility(View.VISIBLE);
                                }
                                listview.setAdapter(adapter);
                                pd.dismiss();
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                            alSwipeFresh.setRefreshing(false);
                        } catch (Exception e) {
                        }
                    }
                });
    }

    /**
     * 我审核的任务
     */
    private void GetMyCheckTaskByTime() {
        list_cy.clear();
        pd.show();
        Map<String, String> params = new HashMap<>();
        Map<String, String> pa = new HashMap<>();
        pa.put("LoginUserCode", sp.getString("Code", ""));
        pa.put("StartTime", str_start_date);
        pa.put("EndTime", str_end_date);
        params.put("paraJson", Common.MaptoJson(pa));
        Log.e("传递的参数", Common.MaptoJson(pa));
        String url = DemoApplication.serviceUrl + "/GetMyCheckTaskByTime";
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        pd.dismiss();
                        alSwipeFresh.setRefreshing(false);
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
                                list_cy = new Gson().fromJson(result, new TypeToken<List<Task>>() {
                                }.getType());
                                adapter = new ProjectTaskAdapter(context, list_cy);
                                if (list_cy.size() > 0) {
                                    empty.setVisibility(View.GONE);
                                } else {
                                    empty.setVisibility(View.VISIBLE);
                                }
                                listview.setAdapter(adapter);
                                pd.dismiss();
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                            alSwipeFresh.setRefreshing(false);
                        } catch (Exception e) {
                        }
                    }
                });
    }

    @OnClick({R.id.tv_back, R.id.tv_back_img, R.id.search_clear, R.id.btn_right})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
            case R.id.tv_back_img:
                finish();
                break;
            case R.id.search_clear:
                query.setText("");
                break;
            case R.id.btn_right:
                Intent it = new Intent(context, FilterActivity.class);
                startActivityForResult(it, 101);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 101:
                    str_start_date = data.getStringExtra("startDate");
                    str_end_date = data.getStringExtra("endDate");
                    GetMyCheckTaskByTime();
                    break;
            }
        }
    }

    /**
     * 文字改变监听
     */
    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                str_search = "1";
                list_all.clear();
                searchClear.setVisibility(View.VISIBLE);
                List<Task> list = list_cy;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getContent().contains(s) || list.get(i).getCode().contains(s) || list.get(i).getProductName().contains(s) || list.get(i).getProjectName().contains(s)) {
                        list_all.add(list.get(i));
                    }
                }
                adapter = new ProjectTaskAdapter(context, list_all);
                listview.setAdapter(adapter);
            } else {
                str_search = "0";
                searchClear.setVisibility(View.INVISIBLE);
                adapter = new ProjectTaskAdapter(context, list_cy);
                listview.setAdapter(adapter);
            }
        }
    };

    @Override
    public void onRefresh() {
        str_start_date = "";
        str_end_date = "";
        if (str_type.equals("1")) {
            GetMyCollectByTime(); // 我参与的
        } else if (str_type.equals("2")) {
            GetMySendByTime();  // 我发起的
        } else if (str_type.equals("3")) {
            GetMyCheckTaskByTime();  // 我审核的
        }
    }
}
