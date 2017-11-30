package com.tphy.zhyycs.ui.announce.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.AnnounceMentResult;
import com.tphy.zhyycs.ui.announce.AnnouceDetailsActivity;
import com.tphy.zhyycs.ui.announce.adapter.AnnouceListAdapter;
import com.tphy.zhyycs.ui.base_wang.BaseFragment;
import com.tphy.zhyycs.utils.ActivityUtils;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.StringCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;

public class ReadedFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, AnnouceListAdapter.OnItemClickListener {


    @BindView(R.id.al_swipe_fresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.announce_list)
    RecyclerView listView;
    @BindView(R.id.al_iv_nodata)
    LinearLayout iv_nodata;
    //    private String Start;
//    private String Length;
    private AnnouceListAdapter adapter;
    private List<AnnounceMentResult> announceMentResults;
    //    private int FIRST_REFRESH = -1;
    private int NORMAL_REFRESH = 0;
    private ActivityUtils activityUtils;

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_announce_list;
    }

    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        announceMentResults = new ArrayList<>();
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        activityUtils = new ActivityUtils(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        adapter = new AnnouceListAdapter(mActivity, announceMentResults);
        listView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        listView.setAdapter(adapter);

    }

    @Override
    public void onRefresh() {
        initListData(NORMAL_REFRESH, true);
    }

    public void initListData(final int order, final boolean refreshing) {
        String isRead = "1";
        if (refreshing) {
            swipeRefreshLayout.setRefreshing(true);
        }
        announceMentResults.clear();
        HashMap<String, String> params = new HashMap<>();
        Map<String, String> map = new HashMap<>();
        map.put("LoginUserCode", code);
        map.put("isRead", isRead);
        map.put("Start", "0");
        map.put("Length", "100");
        params.put("paraJson", Common.MaptoJson(map));
        String url = DemoApplication.serviceUrl + "/GetNoticeByUserCode";
        OkHttpUtils.post().url(url).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (null != swipeRefreshLayout && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                activityUtils.showToast(e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String msg = jsonObject.getString("msg");
                    String result = jsonObject.getString("result");
                    if (success.equals("true")) {
                        announceMentResults = new Gson().fromJson(result, new TypeToken<List<AnnounceMentResult>>() {
                        }.getType());
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        if (announceMentResults.isEmpty()) {
                            iv_nodata.setVisibility(View.VISIBLE);
                        } else {
                            iv_nodata.setVisibility(View.GONE);
                        }
                    } else {
                        activityUtils.showToast(msg);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (order == NORMAL_REFRESH) {
                        adapter.refreshData(announceMentResults);
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initListData(NORMAL_REFRESH, false);
        initItemClick();
//        new AnnounceListActivity().setOnRequestNewData(new AnnounceListActivity.OnRequestNewData() {
//            @Override
//            public void requestNewData() {
//                Log.e("WQ", "刷新嘎嘎嘎");
//                if (stateChanged) {
//                    Log.e("WQ", "刷新嘎嘎嘎");
//                    initListData(NORMAL_REFRESH, false);
//                    stateChanged = false;
//                }
//            }
//        });
    }

    private void initItemClick() {
        adapter.setOnItemClickListener(this);
//        adapter.setDeleteClickListener(this);
//        adapter.setStickClickListener(this);
//        adapter.setOnOpenClickListener(this);
    }

    @Override
    public void itemClickListerner(View view, int position) {
        String code = announceMentResults.get(position).getCode();
        String isMe = announceMentResults.get(position).getIsMe();
        boolean isme = "1".equals(isMe);

//        ProgressDialog progressDialog = new ProgressDialog(context);
//        progressDialog.show();
//        getDetails(progressDialog, code);
        Intent intent = new Intent(context, AnnouceDetailsActivity.class);
        intent.putExtra("NoticeCode", code);
        intent.putExtra("isMe", isme);
        startActivity(intent);
    }

//    @Override
//    public void deleteClickListener(View view, int position) {
//        String code = announceMentResults.get(position).getCode();
//        deleteItem(code, position);
//    }
//
//    @Override
//    public void stickClickListener(View view, int position) {
//        String code = announceMentResults.get(position).getCode();
//        stickItem(code);
//    }
//
//    @Override
//    public void itemOpenListerner(View view, int position) {
//        String isMe = announceMentResults.get(position).getIsMe();
//        if (!isMe.equals("1")) {
//            activityUtils.showToast("没有权限编辑该条公告");
//        }
//    }


//    private void stickItem(String noticeCode) {
//        HashMap<String, String> params = new HashMap<>();
//        params.put("paraJson", Common.params("LoginUserCode", code, "NoticeCode", noticeCode));
//        String url = DemoApplication.serviceUrl + "/StickNotice";
//        OkHttpUtils.post().url(url).params(params).build().execute(new StringCallback() {
//            @Override
//            public void onError(Call call, Exception e, int id) {
//                activityUtils.showToast(e.toString());
//            }
//
//            @Override
//            public void onResponse(String response, int id) {
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    String success = jsonObject.getString("success");
//                    String msg = jsonObject.getString("msg");
//                    if (success.equals("true")) {
//                        activityUtils.showToast("置顶成功");
//                    } else {
//                        activityUtils.showToast(msg);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    private void getDetails(final ProgressDialog pd, String noticeCode) {
//        pd.show();
//        Map<String, String> params = new HashMap<>();
//        params.put("paraJson", Common.params("LoginUserCode", code, "NoticeCode", noticeCode));
//        String url = DemoApplication.serviceUrl + "/GetNoticeInfo";
//        OkHttpUtils.post()
//                .url(url)
//                .params(params)
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        activityUtils.showToast(e.toString());
//                        pd.dismiss();
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        String success, msg;
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            success = jsonObject.getString("success");
//                            msg = jsonObject.getString("msg");
//                            if (success.equals("true")) {
//                                Log.e("WQ", "成功进入详情服务");
//                                pd.dismiss();
//                                JSONArray array = jsonObject.getJSONArray("result");
//                                JSONArray jsonArray = array.getJSONArray(0);
//                                JSONObject infor = jsonArray.getJSONObject(0);
//                                String content = infor.getString("Content");
//                                Log.e("WQ", content);
//                                DetailsDialog detailsDialog = new DetailsDialog(context, content, "公告详情");
//                                detailsDialog.setCancelable(false);
//                                detailsDialog.show();
////                                if (array.length() > 0) {
////                                    JSONArray jsonArray1 = array.getJSONArray(1);
////
////                                    JSONObject jsonObject1 = jsonArray1.getJSONObject(0);
////                                    String fileName = jsonObject1.getString("FileName");
////                                }
//                            } else {
//                                Log.e("WQ", "进入详情服务");
//                                activityUtils.showToast(msg);
//                                pd.dismiss();
//                            }
//                            pd.dismiss();
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//    }

}
