package com.tphy.zhyycs.ui.announce;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.User;
import com.tphy.zhyycs.ui.announce.adapter.ContactsAdapter;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.MyListView;
import com.tphy.zhyycs.utils.StringCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


public class ContactsActivity extends Activity implements View.OnClickListener {

    private TextView tv_title, tv_back, tv_back_img, tv_confirm;
    private Button btn_right;
    private CheckBox checkbox;
    private MyListView listview;
    private ContactsAdapter adapter;

    public static List<User> list_user = new ArrayList<>();
    private List<User> list_add = new ArrayList<>();

    private String mToast = "只能选择一个";
    private ProgressDialog pd;
    private Context context;
    private int position;
    //    private static List<User> list_bumen;
//    private static List<User> list_xiangmu;
    private String type;
    private ProgressDialog progressDialog;
    private SharedPreferences preferences;
    private String code;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectuser);
        initView();
        preferences = getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);
        code = preferences.getString("Code", "");
        type = getIntent().getStringExtra("type");
        progressDialog = new ProgressDialog(this);
        switch (type) {
            case "bumen":
                list_user = (List<User>) getIntent().getSerializableExtra("user");
                getBumen();
                break;
            case "xiangmu":
                list_user = (List<User>) getIntent().getSerializableExtra("user");
                getXiangmu();
                break;
        }

    }

    private void getXiangmu() {
        list_user.clear();
        Map<String, String> params = new HashMap<>();

        params.put("paraJson", Common.params("LoginUserCode", code));
        String url = DemoApplication.serviceUrl + "/GetMyProjects";
        OkHttpUtils.post().url(url).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(ContactsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String msg = jsonObject.getString("msg");
                    JSONArray result = jsonObject.getJSONArray("result");
                    if (success.equals("true")) {
                        Log.e("WQ", "进入了项目信息");
                        progressDialog.dismiss();
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject object = result.getJSONObject(i);
                            if (object.getString("IsCurrent").equals("1")) {
                                Log.e("WQ", "项目是==>" + object.getString("Name"));
                                User user = new User();
                                user.setName(object.getString("Name"));
                                list_user.add(user);
                            }
                        }
                        adapter = new ContactsAdapter(ContactsActivity.this, list_user);
                        listview.setAdapter(adapter);
                    } else {
                        Toast.makeText(ContactsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getBumen() {
        list_user.clear();
        pd.setMessage("加载数据...");
        pd.show();
        Map<String, String> params = new HashMap<>();
        params.put("paraJson", Common.params("codeTables", "dept"));
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
                                list_user = new Gson().fromJson(js.getString("dept"), new TypeToken<List<User>>() {
                                }.getType());

                                for (int i = 0; i < list_user.size(); i++) {
                                    list_user.get(i).setDUTY("天鹏");
                                    for (int j = 0; j < list_add.size(); j++) {
                                        if (list_user.get(i).getCode().equals(list_add.get(j).getCode())) {
                                            list_user.get(i).setChecked(true);
                                            Log.e("选中的", list_user.get(i).getName());
                                        }
                                    }
                                }
                                adapter = new ContactsAdapter(ContactsActivity.this, list_user);
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

    private void initView() {
        context = ContactsActivity.this;
        pd = new ProgressDialog(context);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("选择成员");
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setOnClickListener(this);
        tv_back_img = (TextView) findViewById(R.id.tv_back_img);
        tv_back_img.setOnClickListener(this);
        btn_right = (Button) findViewById(R.id.btn_right);
        tv_confirm = (TextView) findViewById(R.id.btn_save);
        btn_right.setText("确定");
        btn_right.setVisibility(View.GONE);
        btn_right.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        checkbox.setOnClickListener(this);
        listview = (MyListView) findViewById(R.id.listview);
        View viewById = findViewById(R.id.search_bar_view);
        viewById.setVisibility(View.GONE);
//        list_bumen = new ArrayList<>();
//        list_xiangmu = new ArrayList<>();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
            case R.id.tv_back_img:
                finish();
                break;
            case R.id.btn_save:
                list_add.clear();
                for (int i = 0; i < list_user.size(); i++) {
                    if (list_user.get(i).isChecked()) {
                        list_add.add(list_user.get(i));
                    }
                }
                if (!list_add.isEmpty()) {
                    goback();
                } else {
                    Toast.makeText(this, "选择不能为空", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.checkbox:
                if (!list_user.isEmpty()) {
                    if (checkbox.isChecked()) {
                        for (int i = 0; i < list_user.size(); i++) {
                            list_user.get(i).setChecked(true);
                        }
                    } else {
                        for (int i = 0; i < list_user.size(); i++) {
                            list_user.get(i).setChecked(false);
                        }
                    }
                    adapter.update(list_user);
                }
                break;
        }
    }

    private void goback() {
        switch (type) {
            case "bumen":
                Intent bumen = new Intent();
                bumen.putExtra("new_add_user", (Serializable) list_add);
                setResult(1, bumen);
                finish();
                break;
            case "xiangmu":
                Intent xiangmu = new Intent();
                xiangmu.putExtra("new_add_user", (Serializable) list_add);
                setResult(2, xiangmu);
                finish();
                break;
        }
    }


}
