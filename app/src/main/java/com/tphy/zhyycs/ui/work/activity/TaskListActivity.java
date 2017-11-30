package com.tphy.zhyycs.ui.work.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
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
 * 项目名称：任务列表
 * 创建人：cdss
 * 创建时间：2017-08-16 14:35
 * 修改人：cdss
 * 修改时间：2017-08-16 14:35
 * 修改备注：
 */

public class TaskListActivity extends Activity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {


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
    @BindView(R.id.query)
    EditText query;
    @BindView(R.id.search_clear)
    ImageButton searchClear;
    @BindView(R.id.top)
    LinearLayout top;
    @BindView(R.id.listview)
    SwipeMenuListView listview;
    @BindView(R.id.al_swipe_fresh)
    SwipeRefreshLayout alSwipeFresh;
    @BindView(R.id.empty)
    LinearLayout empty;
    @BindView(R.id.iv_main_add)
    ImageView ivMainAdd;
    @BindView(R.id.tv_daiban)
    TextView tvDaiban;
    @BindView(R.id.tv_shenhe)
    TextView tvShenhe;
    @BindView(R.id.tv_chaoshi)
    TextView tvChaoshi;
    @BindView(R.id.tv_wc)
    TextView tvWc;
    @BindView(R.id.tv_xm)
    TextView tvXm;
    @BindView(R.id.lyt_buttom)
    LinearLayout lytButtom;
    private List<Task> list_daiban = new ArrayList<>();
    private List<Task> list_shenhe = new ArrayList<>();
    private List<Task> list_chaoshi = new ArrayList<>();
    private List<Task> list_wc = new ArrayList<>();
    private List<Task> list_xm = new ArrayList<>();
    private List<Task> list_task = new ArrayList<>();
    private List<Task> list_all = new ArrayList<>();

    private ProjectTaskAdapter adapter;
    private String project_name = "", project_code = "", task_shenhe_num = "", str_serach = "0";
    public static String str_task_type = "";
    private Dialog pd;
    private SharedPreferences sp;
    private Activity context;
    Common common;
    private boolean scrollFlag = false;// 标记是否滑动
    private int lastVisibleItemPosition = 0;// 标记上次滑动位置

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("new_task_list")) {
                list_task.clear();
                ShowTaskList();
                str_task_type = "";
                tvTitle.setText("全部任务");
                initData();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasklist);
        ButterKnife.bind(this);
        if (null != getIntent().getStringExtra("project_name")) {
            project_name = getIntent().getStringExtra("project_name");
        }
        if (null != getIntent().getStringExtra("project_code")) {
            project_code = getIntent().getStringExtra("project_code");
        }
        if (null != getIntent().getStringExtra("task_shenhe_num")) {
            task_shenhe_num = getIntent().getStringExtra("task_shenhe_num");
        }
        initView();
        initData();
        alSwipeFresh.setOnRefreshListener(this);
        alSwipeFresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);

        IntentFilter itf = new IntentFilter("new_task_list");
        registerReceiver(receiver, itf);

    }

    private void initData() {
        GetMyTasks();  //待办
    }

    private void initView() {
        context = TaskListActivity.this;
        pd = CustomProgressDialog.createLoadingDialog(context, "正在加载中...");
        common = new Common(context);
        sp = getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);
        tvTitle.setText("全部任务");
        if (!task_shenhe_num.equals("0")) {
            tvShenhe.setVisibility(View.VISIBLE);
        } else {
            tvShenhe.setVisibility(View.GONE);
        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent();
                it.putExtra("LoginUserCode", sp.getString("Code", ""));
                if (str_task_type.equals("2")) {  // 审核
                    if (!task_shenhe_num.equals("0")) {
                        if (str_serach.equals("0")) {
                            it.setClass(TaskListActivity.this, TaskReviewOneActivity.class);
                            if (list_shenhe.get(position).getIsCPCheck().equals("0")) {
                                it.putExtra("TaskCode", list_shenhe.get(position).getCode());
                            } else {
                                it.putExtra("TaskCode", list_shenhe.get(position).getCode());
                                it.putExtra("TaskContent", list_shenhe.get(position).getContent());
                                it.putExtra("SendUser", list_shenhe.get(position).getSendUserName());
                                it.putExtra("ProjectCode", list_shenhe.get(position).getProjectCode());
                                it.putExtra("TaskStatus", list_shenhe.get(position).getStatusName());
                                it.putExtra("CPCheckUserCode", list_shenhe.get(position).getCPCheckUserCode());
                                it.putExtra("CPCheckUserName", list_shenhe.get(position).getCPCheckUserName());
                            }
                        } else {
                            it.setClass(TaskListActivity.this, TaskReviewOneActivity.class);
                            if (list_all.get(position).getIsCPCheck().equals("0")) {
                                it.putExtra("TaskCode", list_all.get(position).getCode());
                            } else {
                                it.putExtra("TaskCode", list_all.get(position).getCode());
                                it.putExtra("TaskContent", list_all.get(position).getContent());
                                it.putExtra("SendUser", list_all.get(position).getSendUserName());
                                it.putExtra("ProjectCode", list_all.get(position).getProjectCode());
                                it.putExtra("TaskStatus", list_all.get(position).getStatusName());
                                it.putExtra("CPCheckUserCode", list_all.get(position).getCPCheckUserCode());
                                it.putExtra("CPCheckUserName", list_all.get(position).getCPCheckUserName());
                            }
                        }
                        startActivity(it);
                    }
                } else if (str_task_type.equals("1")) {   //待办
                    it.setClass(TaskListActivity.this, TaskDetailsActivity.class);
                    if (str_serach.equals("0")) {
                        it.putExtra("task_type", str_task_type);
                        it.putExtra("TaskCode", list_daiban.get(position).getCode());
                        it.putExtra("ProjectName", list_daiban.get(position).getProjectName());
                        it.putExtra("ProductName", list_daiban.get(position).getProductName());
                        it.putExtra("TaskStatus", list_daiban.get(position).getStatusName());
                    } else {
                        it.putExtra("task_type", str_task_type);
                        it.putExtra("TaskCode", list_all.get(position).getCode());
                        it.putExtra("ProjectName", list_all.get(position).getProjectName());
                        it.putExtra("ProductName", list_all.get(position).getProductName());
                        it.putExtra("TaskStatus", list_all.get(position).getStatusName());
                    }
                    startActivity(it);
                } else if (str_task_type.equals("3")) {     // 超时
                    it.setClass(TaskListActivity.this, TaskDetailsActivity.class);
                    if (str_serach.equals("0")) {
                        it.putExtra("TaskCode", list_chaoshi.get(position).getCode());
                        it.putExtra("ProjectName", list_chaoshi.get(position).getProjectName());
                        it.putExtra("ProductName", list_chaoshi.get(position).getProductName());
                        it.putExtra("TaskStatus", list_chaoshi.get(position).getStatusName());
                    } else {
                        it.putExtra("TaskCode", list_all.get(position).getCode());
                        it.putExtra("ProjectName", list_all.get(position).getProjectName());
                        it.putExtra("ProductName", list_all.get(position).getProductName());
                        it.putExtra("TaskStatus", list_all.get(position).getStatusName());
                    }
                    startActivity(it);
                } else if (str_task_type.equals("5")) {
                    it.setClass(TaskListActivity.this, TaskDetailsActivity.class);
                    if (str_serach.equals("0")) {
                        it.putExtra("TaskCode", list_xm.get(position).getCode());
                        it.putExtra("ProjectName", list_xm.get(position).getProjectName());
                        it.putExtra("ProductName", list_xm.get(position).getProductName());
                        it.putExtra("TaskStatus", list_xm.get(position).getStatusName());
                    } else {
                        it.putExtra("TaskCode", list_all.get(position).getCode());
                        it.putExtra("ProjectName", list_all.get(position).getProjectName());
                        it.putExtra("ProductName", list_all.get(position).getProductName());
                        it.putExtra("TaskStatus", list_all.get(position).getStatusName());
                    }
                    startActivity(it);
                } else if (str_task_type.equals("4")) {   // 已完成
                    it.setClass(TaskListActivity.this, TaskDetailsActivity.class);
                    if (str_serach.equals("0")) {
                        it.putExtra("TaskCode", list_wc.get(position).getCode());
                        it.putExtra("ProjectName", list_wc.get(position).getProjectName());
                        it.putExtra("ProductName", list_wc.get(position).getProductName());
                        it.putExtra("TaskStatus", list_wc.get(position).getStatusName());
                    } else {
                        it.putExtra("TaskCode", list_all.get(position).getCode());
                        it.putExtra("ProjectName", list_all.get(position).getProjectName());
                        it.putExtra("ProductName", list_all.get(position).getProductName());
                        it.putExtra("TaskStatus", list_all.get(position).getStatusName());
                    }
                    startActivity(it);
                } else if (str_task_type.equals("")) {
                    if (str_serach.equals("0")) {
                        Task task = list_task.get(position);
                        it.putExtra("ProjectName", list_task.get(position).getProjectName());
                        it.putExtra("ProductName", list_task.get(position).getProductName());
                        it.putExtra("TaskStatus", list_task.get(position).getStatusName());
                        if (task.getTaskType().equals("1")) {
                            it.setClass(TaskListActivity.this, TaskDetailsActivity.class);
                            it.putExtra("task_type", "1");
                            it.putExtra("TaskCode", list_task.get(position).getCode());
                            startActivity(it);
                        } else if (task.getTaskType().equals("2")) {
                            if (task.getIsCPCheck().equals("0")) {
                                it.setClass(TaskListActivity.this, TaskReviewOneActivity.class);
                                it.putExtra("TaskCode", list_shenhe.get(position).getCode());
                            } else {
                                it.setClass(TaskListActivity.this, TaskReviewActivity.class);
                                it.putExtra("TaskCode", list_task.get(position).getCode());
                                it.putExtra("TaskContent", list_task.get(position).getContent());
                                it.putExtra("SendUser", list_task.get(position).getSendUserName());
                                it.putExtra("ProjectCode", list_task.get(position).getProjectCode());
                                it.putExtra("CPCheckUserCode", list_task.get(position).getCPCheckUserCode());
                                it.putExtra("CPCheckUserName", list_task.get(position).getCPCheckUserName());
                            }
                            startActivity(it);
                        } else if (task.getTaskType().equals("3")) {
                            it.setClass(TaskListActivity.this, TaskDetailsActivity.class);
                            it.putExtra("TaskCode", list_task.get(position).getCode());
                            startActivity(it);
                        } else if (task.getTaskType().equals("4")) {
                            it.setClass(TaskListActivity.this, TaskDetailsActivity.class);
                            it.putExtra("TaskCode", list_task.get(position).getCode());
                            startActivity(it);
                        }
                    } else {
                        Task task = list_all.get(position);
                        it.putExtra("ProjectName", list_all.get(position).getProjectName());
                        it.putExtra("ProductName", list_all.get(position).getProductName());
                        it.putExtra("TaskStatus", list_all.get(position).getStatusName());
                        if (task.getTaskType().equals("1")) {
                            it.setClass(TaskListActivity.this, TaskDetailsActivity.class);
                            it.putExtra("task_type", "1");
                            it.putExtra("TaskCode", list_all.get(position).getCode());
                            startActivity(it);
                        } else if (task.getTaskType().equals("2")) {
                            if (task.getIsCPCheck().equals("0")) {
                                it.setClass(TaskListActivity.this, TaskReviewOneActivity.class);
                                it.putExtra("TaskCode", list_all.get(position).getCode());
                            } else {
                                it.setClass(TaskListActivity.this, TaskReviewActivity.class);
                                it.putExtra("TaskCode", list_all.get(position).getCode());
                                it.putExtra("TaskContent", list_all.get(position).getContent());
                                it.putExtra("SendUser", list_all.get(position).getSendUserName());
                                it.putExtra("ProjectCode", list_all.get(position).getProjectCode());
                                it.putExtra("CPCheckUserCode", list_all.get(position).getCPCheckUserCode());
                                it.putExtra("CPCheckUserName", list_all.get(position).getCPCheckUserName());
                            }
                            startActivity(it);
                        } else if (task.getTaskType().equals("3")) {
                            it.setClass(TaskListActivity.this, TaskDetailsActivity.class);
                            it.putExtra("TaskCode", list_all.get(position).getCode());
                            startActivity(it);
                        } else if (task.getTaskType().equals("4")) {
                            it.setClass(TaskListActivity.this, TaskDetailsActivity.class);
                            it.putExtra("TaskCode", list_all.get(position).getCode());
                            startActivity(it);
                        }
                    }

                }
            }
        });

        query.setHint("按编号/任务描述/项目/产品等进行搜索");
        query.addTextChangedListener(watcher);

        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    // 当不滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 是当屏幕停止滚动时
                        scrollFlag = false;
                        // 判断滚动到底部
                        if (listview.getCount() > 8) {
                            if (listview.getLastVisiblePosition() == (listview
                                    .getCount() - 1)) {
                                lytButtom.setVisibility(View.GONE);
                            }
                            // 判断滚动到顶部
                            if (listview.getFirstVisiblePosition() == 0) {
                                lytButtom.setVisibility(View.VISIBLE);
                            }
                        }
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 滚动时
                        scrollFlag = true;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:// 是当用户由于之前划动屏幕并抬起手指，屏幕产生惯性滑动时
                        scrollFlag = false;
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 当开始滑动且ListView底部的Y轴点超出屏幕最大范围时，显示或隐藏顶部按钮
                if (listview.getCount() > 8) {
                    if (firstVisibleItem > lastVisibleItemPosition) {// 上滑
                        lytButtom.setVisibility(View.GONE);
                    } else if (firstVisibleItem < lastVisibleItemPosition) {// 下滑
                        lytButtom.setVisibility(View.VISIBLE);
                    } else {
                        return;
                    }
                    lastVisibleItemPosition = firstVisibleItem;
                }
            }
        });
    }

    /**
     * 待办任务
     */
    private void GetMyTasks() {
        list_daiban.clear();
        list_chaoshi.clear();
        list_wc.clear();
        list_shenhe.clear();
        list_task.clear();
        pd.show();
        Map<String, String> params = new HashMap<>();
        Map<String, String> pa = new HashMap<>();
        pa.put("LoginUserCode", sp.getString("Code", ""));
        pa.put("ProjectCode", project_code);
        params.put("paraJson", Common.MaptoJson(pa));
        String url = DemoApplication.serviceUrl + "/GetMyTasks";
        Log.e("输入的参数", Common.MaptoJson(pa));
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
                                JSONObject js = new JSONObject(result);
                                list_shenhe = new Gson().fromJson(js.getString("WaitCheck"), new TypeToken<List<Task>>() {
                                }.getType());
                                list_daiban = new Gson().fromJson(js.getString("ActionRequired"), new TypeToken<List<Task>>() {
                                }.getType());
                                list_chaoshi = new Gson().fromJson(js.getString("TimeOut"), new TypeToken<List<Task>>() {
                                }.getType());
                                list_wc = new Gson().fromJson(js.getString("Finished"), new TypeToken<List<Task>>() {
                                }.getType());
                                for (int i = 0; i < list_daiban.size(); i++) {
                                    Task task = list_daiban.get(i);
                                    task.setTaskType("1");
                                    list_task.add(task);
                                }
                                for (int i = 0; i < list_shenhe.size(); i++) {
                                    Task task = list_shenhe.get(i);
                                    task.setTaskType("2");
                                    list_task.add(task);
                                }
                                for (int i = 0; i < list_chaoshi.size(); i++) {
                                    Task task = list_chaoshi.get(i);
                                    task.setTaskType("3");
                                    list_task.add(task);
                                }
                                for (int i = 0; i < list_wc.size(); i++) {
                                    Task task = list_wc.get(i);
                                    task.setTaskType("4");
                                    list_task.add(task);
                                }

                                tvDaiban.setText("待办(" + list_daiban.size() + ")");
                                tvShenhe.setText("待审核(" + list_shenhe.size() + ")");
                                tvChaoshi.setText("超时(" + list_chaoshi.size() + ")");
                                tvWc.setText("已完成(" + list_wc.size() + ")");

                                if (str_task_type.equals("")) {  // 全部
                                    adapter = new ProjectTaskAdapter(context, list_task);
                                    if (list_task.size() > 0) {
                                        empty.setVisibility(View.GONE);
                                    } else {
                                        empty.setVisibility(View.VISIBLE);
                                    }
                                    listview.setAdapter(adapter);
                                } else if (str_task_type.equals("1")) {  // 待办任务
                                    adapter = new ProjectTaskAdapter(context, list_daiban);
                                    if (list_daiban.size() > 0) {
                                        empty.setVisibility(View.GONE);
                                    } else {
                                        empty.setVisibility(View.VISIBLE);
                                    }
                                    listview.setAdapter(adapter);
                                } else if (str_task_type.equals("4")) {  // 完成
                                    adapter = new ProjectTaskAdapter(context, list_wc);
                                    if (list_wc.size() > 0) {
                                        empty.setVisibility(View.GONE);
                                    } else {
                                        empty.setVisibility(View.VISIBLE);
                                    }
                                    listview.setAdapter(adapter);
                                } else if (str_task_type.equals("2")) {  // 待审核
                                    adapter = new ProjectTaskAdapter(context, list_shenhe);
                                    if (list_shenhe.size() > 0) {
                                        empty.setVisibility(View.GONE);
                                    } else {
                                        empty.setVisibility(View.VISIBLE);
                                    }
                                    listview.setAdapter(adapter);
                                } else if (str_task_type.equals("3")) {  // 超时任务
                                    adapter = new ProjectTaskAdapter(context, list_chaoshi);
                                    if (list_chaoshi.size() > 0) {
                                        empty.setVisibility(View.GONE);
                                    } else {
                                        empty.setVisibility(View.VISIBLE);
                                    }
                                    listview.setAdapter(adapter);
                                }
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
     * 项目任务
     */
    private void GetTaskByProjectCode() {
        list_xm.clear();
        if (!pd.isShowing()) {
            pd.show();
        }
        Map<String, String> params = new HashMap<>();
        Map<String, String> pa = new HashMap<>();
        pa.put("LoginUserCode", sp.getString("Code", ""));
        pa.put("ProjectCode", project_code);
        params.put("paraJson", Common.MaptoJson(pa));
        String url = DemoApplication.serviceUrl + "/GetTaskByProjectCode";
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
                                list_xm = new Gson().fromJson(result, new TypeToken<List<Task>>() {
                                }.getType());
                                tvXm.setText("项目任务(" + list_xm.size() + ")");
                                adapter = new ProjectTaskAdapter(context, list_xm);
                                if (list_xm.size() > 0) {
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


    @OnClick({R.id.search_clear, R.id.tv_xm, R.id.tv_back, R.id.tv_back_img, R.id.tv_daiban, R.id.tv_shenhe, R.id.tv_chaoshi, R.id.tv_wc, R.id.iv_main_add})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
            case R.id.tv_back_img:
                finish();
                break;
            case R.id.tv_daiban:
                if (tvDaiban.isSelected()) {
                    ShowTaskList();
                    str_task_type = "";
                    tvTitle.setText("全部任务");
                } else {
                    str_task_type = "1";
                    tvTitle.setText("待办任务");
                    tvDaiban.setSelected(true);
                    tvShenhe.setSelected(false);
                    tvChaoshi.setSelected(false);
                    tvWc.setSelected(false);
                    tvXm.setSelected(false);
                    adapter = new ProjectTaskAdapter(context, list_daiban);
                    listview.setAdapter(adapter);
                    if (list_daiban.size() > 0) {
                        empty.setVisibility(View.GONE);
                    } else {
                        empty.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.tv_shenhe:
                if (tvShenhe.isSelected()) {
                    ShowTaskList();
                    str_task_type = "";
                    tvTitle.setText("全部任务");
                } else {
                    str_task_type = "2";
                    tvTitle.setText("待审核任务");
                    tvDaiban.setSelected(false);
                    tvShenhe.setSelected(true);
                    tvChaoshi.setSelected(false);
                    tvWc.setSelected(false);
                    tvXm.setSelected(false);
                    adapter = new ProjectTaskAdapter(context, list_shenhe);
                    listview.setAdapter(adapter);
                    if (list_shenhe.size() > 0) {
                        empty.setVisibility(View.GONE);
                    } else {
                        empty.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.tv_chaoshi:
                if (tvChaoshi.isSelected()) {
                    ShowTaskList();
                    str_task_type = "";
                    tvTitle.setText("全部任务");
                } else {
                    str_task_type = "3";
                    tvTitle.setText("超时任务");
                    tvDaiban.setSelected(false);
                    tvShenhe.setSelected(false);
                    tvChaoshi.setSelected(true);
                    tvWc.setSelected(false);
                    tvXm.setSelected(false);
                    adapter = new ProjectTaskAdapter(context, list_chaoshi);
                    listview.setAdapter(adapter);
                    if (list_chaoshi.size() > 0) {
                        empty.setVisibility(View.GONE);
                    } else {
                        empty.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.tv_wc:
                if (tvWc.isSelected()) {
                    ShowTaskList();
                    str_task_type = "";
                    tvTitle.setText("全部任务");
                } else {
                    str_task_type = "4";
                    tvTitle.setText("已完成任务");
                    tvDaiban.setSelected(false);
                    tvShenhe.setSelected(false);
                    tvChaoshi.setSelected(false);
                    tvWc.setSelected(true);
                    tvXm.setSelected(false);
                    adapter = new ProjectTaskAdapter(context, list_wc);
                    listview.setAdapter(adapter);
                    if (list_wc.size() > 0) {
                        empty.setVisibility(View.GONE);
                    } else {
                        empty.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.tv_xm:
                if (tvXm.isSelected()) {
                    ShowTaskList();
                    str_task_type = "";
                    tvTitle.setText("全部任务");
                } else {
                    str_task_type = "5";
                    tvTitle.setText("项目任务");
                    tvDaiban.setSelected(false);
                    tvShenhe.setSelected(false);
                    tvChaoshi.setSelected(false);
                    tvWc.setSelected(false);
                    tvXm.setSelected(true);
                    GetTaskByProjectCode();
                }
                break;
            case R.id.iv_main_add:
                common.showWindow(context, ivMainAdd, project_code, project_name);
                break;
            case R.id.search_clear:
                query.setText("");
                break;
        }
    }

    private void ShowTaskList() {
        tvDaiban.setSelected(false);
        tvShenhe.setSelected(false);
        tvChaoshi.setSelected(false);
        tvWc.setSelected(false);
        tvXm.setSelected(false);
        tvTitle.setText("全部任务");
        str_task_type = "";
        adapter = new ProjectTaskAdapter(context, list_task);
        listview.setAdapter(adapter);
        if (list_task.size() > 0) {
            empty.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        str_task_type = "";
        unregisterReceiver(receiver);
    }

    @Override
    public void onRefresh() {
        if (str_task_type.equals("5")) {  // 项目任务
            GetTaskByProjectCode();
        } else {
            GetMyTasks();
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
                str_serach = "1";
                list_all.clear();
                searchClear.setVisibility(View.VISIBLE);
                List<Task> list = new ArrayList<>();
                if (str_task_type.equals("")) {
                    list = list_task;
                } else if (str_task_type.equals("1")) {
                    list = list_daiban;
                } else if (str_task_type.equals("2")) {
                    list = list_shenhe;
                } else if (str_task_type.equals("3")) {
                    list = list_chaoshi;
                } else if (str_task_type.equals("4")) {
                    list = list_wc;
                } else if (str_task_type.equals("5")) {
                    list = list_xm;
                }
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getContent().contains(s) || list.get(i).getCode().contains(s) || list.get(i).getProductName().contains(s) || list.get(i).getProjectName().contains(s)) {
                        list_all.add(list.get(i));
                    }
                }

                Log.e(str_task_type + "----" + str_serach, list_all.size() + "");
                adapter = new ProjectTaskAdapter(context, list_all);
                listview.setAdapter(adapter);
            } else {
                str_serach = "0";
                searchClear.setVisibility(View.INVISIBLE);
                if (str_task_type.equals("")) {
                    adapter = new ProjectTaskAdapter(context, list_task);
                } else if (str_task_type.equals("1")) {
                    adapter = new ProjectTaskAdapter(context, list_daiban);
                } else if (str_task_type.equals("2")) {
                    adapter = new ProjectTaskAdapter(context, list_shenhe);
                } else if (str_task_type.equals("3")) {
                    adapter = new ProjectTaskAdapter(context, list_chaoshi);
                } else if (str_task_type.equals("4")) {
                    adapter = new ProjectTaskAdapter(context, list_wc);
                } else if (str_task_type.equals("5")) {
                    adapter = new ProjectTaskAdapter(context, list_xm);
                }
                Log.e(str_task_type + "----" + str_serach, list_all.size() + "");
                listview.setAdapter(adapter);
            }
        }
    };


}
