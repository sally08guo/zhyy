package com.tphy.zhyycs.ui.work.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.LCB;
import com.tphy.zhyycs.ui.work.adapter.SelectLCBAdapter;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.MyListView;
import com.tphy.zhyycs.utils.StringCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 选择里程牌
 */

public class SelectLCBActivity extends Activity implements View.OnClickListener {

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
    MyListView listview;
    @BindView(R.id.btn_save)
    TextView btnSave;
    private SelectLCBAdapter adapter;
    private List<LCB> list_lcb = new ArrayList<>();
    private List<LCB> list_add = new ArrayList<>();
    private ProgressDialog pd;
    private Context context;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectlcb);
        ButterKnife.bind(this);
        initView();
        if (null != getIntent().getSerializableExtra("lcb")) {
            list_add = (List<LCB>) getIntent().getSerializableExtra("lcb");
        }
        GetLcb();
    }


    private void initView() {
        context = SelectLCBActivity.this;
        pd = new ProgressDialog(context);
        tvTitle.setText("选择里程碑");
    }


    private void GetLcb() {
        list_lcb.clear();
        pd.setMessage("加载数据...");
        pd.show();
        Map<String, String> params = new HashMap<>();
        params.put("paraJson", Common.params("codeTables", "project_status"));
        String url = DemoApplication.serviceUrl + "/GetCodeListByName";
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
                                pd.dismiss();
                                result = jsonObject.getString("result");
                                JSONObject js = new JSONObject(result);
                                list_lcb = new Gson().fromJson(js.getString("project_status"), new TypeToken<List<LCB>>() {
                                }.getType());

                                for (int i = 0; i < list_lcb.size(); i++) {
                                    list_lcb.get(i).setIsCurrent("0");
                                    for (int j = 0; j < list_add.size(); j++) {
                                        if (list_lcb.get(i).getCode().equals(list_add.get(j).getCode())) {
                                            list_lcb.get(i).setChecked(true);
                                            list_lcb.get(i).setIsCurrent(list_add.get(j).getIsCurrent());
                                        }
                                    }
                                }
                                adapter = new SelectLCBAdapter(SelectLCBActivity.this, list_lcb);
                                listview.setAdapter(adapter);
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        } catch (Exception e) {
                        }
                    }
                });
    }

    @OnClick({R.id.tv_back, R.id.tv_back_img, R.id.btn_save})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
            case R.id.tv_back_img:
                finish();
                break;
            case R.id.btn_save:
                list_add.clear();
                for (int i = 0; i < list_lcb.size(); i++) {
                    if (list_lcb.get(i).isChecked()) {
                        list_add.add(list_lcb.get(i));
                    }
                }
                Intent it = new Intent();
                it.putExtra("Add_LCB", (Serializable) list_add);
                setResult(RESULT_OK, it);
                finish();

                break;
        }
    }
}
