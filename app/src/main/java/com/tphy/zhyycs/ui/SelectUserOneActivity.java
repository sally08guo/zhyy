package com.tphy.zhyycs.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.adapter.SelectUserOneAdapter;
import com.tphy.zhyycs.model.User;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.MyListView;
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
 * 选择成员界面
 */

public class SelectUserOneActivity extends Activity implements View.OnClickListener {

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
    @BindView(R.id.listview)
    MyListView listview;
    private SelectUserOneAdapter adapter;
    private List<User> list_user = new ArrayList<>();
    private List<User> list;
    private ProgressDialog pd;
    private Context context;
    private String number = "";
    private String type = "1";
    private SharedPreferences sp;
    private String str_project_code = "", str_from_status = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectuser_one);
        ButterKnife.bind(this);
        if (null != getIntent().getStringExtra("number")) {
            number = getIntent().getStringExtra("number");
        }
        if (null != getIntent().getStringExtra("ProjectCode")) {
            str_project_code = getIntent().getStringExtra("ProjectCode");
        }
        if (null != getIntent().getStringExtra("buqian")) {
            str_from_status = getIntent().getStringExtra("buqian");
        }
        initView();
        if (str_from_status.equals("补签")) {
            GetAllUser();
        } else {
            GetUserByProject();
        }
    }

    private void initView() {
        context = SelectUserOneActivity.this;
        pd = new ProgressDialog(context);
        sp = getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);
        tvTitle.setText("选择成员");
        query.addTextChangedListener(watcher);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent();
                if (type.equals("1")) {
                    it.putExtra("user_name", list_user.get(position).getName());
                    it.putExtra("user_code", list_user.get(position).getCode());
                } else if (type.equals("2")) {
                    it.putExtra("user_name", list.get(position).getName());
                    it.putExtra("user_code", list.get(position).getCode());
                }
                it.putExtra("number", number);
                setResult(RESULT_OK, it);
                finish();
            }
        });

    }

    private void GetAllUser() {
        list_user.clear();
        pd.setMessage("加载数据...");
        pd.show();
        Map<String, String> params = new HashMap<>();
        params.put("paraJson", Common.params("codeTables", "user"));
        String url = DemoApplication.serviceUrl + "/GetCodeListByName";
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
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                pd.dismiss();
                                result = jsonObject.getString("result");
                                JSONObject js = new JSONObject(result);
                                list_user = new Gson().fromJson(js.getString("user"), new TypeToken<List<User>>() {
                                }.getType());
                                for (int i = 0; i < list_user.size(); i++) {
                                    list_user.get(i).setDUTY("天鹏恒宇");
                                }
                                adapter = new SelectUserOneAdapter(SelectUserOneActivity.this, list_user);
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


    private void GetUserByProject() {
        list_user.clear();
        pd.setMessage("加载数据...");
        pd.show();
        Map<String, String> params = new HashMap<>();
        Map<String, String> pa = new HashMap<>();
        pa.put("LoginUserCode", sp.getString("Code", ""));
        pa.put("ProjectCode", str_project_code);
        params.put("paraJson", Common.MaptoJson(pa));
        Log.e("选择人员参数", Common.MaptoJson(pa));
        String url = DemoApplication.serviceUrl + "/GetUserByProject";
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
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                pd.dismiss();
                                result = jsonObject.getString("result");
                                list_user = new Gson().fromJson(result, new TypeToken<List<User>>() {
                                }.getType());
                                for (int i = 0; i < list_user.size(); i++) {
                                    list_user.get(i).setDUTY("天鹏恒宇");
                                }
                                adapter = new SelectUserOneAdapter(SelectUserOneActivity.this, list_user);
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

    @OnClick({R.id.tv_back, R.id.tv_back_img, R.id.btn_right, R.id.search_clear})
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
                searchClear.setVisibility(View.VISIBLE);
                list = new ArrayList<>();
                for (int i = 0; i < list_user.size(); i++) {
                    if (list_user.get(i).getName().contains(s) || list_user.get(i).getPYDM().contains(s) || list_user.get(i).getPYDM().toLowerCase().contains(s)) {
                        list.add(list_user.get(i));
                    }
                }
                type = "2";
                adapter = new SelectUserOneAdapter(SelectUserOneActivity.this, list);
                listview.setAdapter(adapter);
            } else {
                searchClear.setVisibility(View.INVISIBLE);
                adapter = new SelectUserOneAdapter(SelectUserOneActivity.this, list_user);
                listview.setAdapter(adapter);
                type = "1";
            }
        }
    };

}
