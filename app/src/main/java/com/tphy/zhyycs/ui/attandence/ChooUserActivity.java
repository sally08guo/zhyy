package com.tphy.zhyycs.ui.attandence;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.adapter.SelectUserAdapter;
import com.tphy.zhyycs.model.User;
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

import static com.tphy.zhyycs.ui.base_wang.BaseAppCompatActivity.BUMEN;
import static com.tphy.zhyycs.ui.base_wang.BaseAppCompatActivity.CHAO_CODE;
import static com.tphy.zhyycs.ui.base_wang.BaseAppCompatActivity.EDITOR_CODE;
import static com.tphy.zhyycs.ui.base_wang.BaseAppCompatActivity.RECORDER_CODE;
import static com.tphy.zhyycs.ui.base_wang.BaseAppCompatActivity.RESELECTE;
import static com.tphy.zhyycs.ui.base_wang.BaseAppCompatActivity.XIANGMU;

/**
 * 选择成员界面
 */

public class ChooUserActivity extends Activity implements View.OnClickListener {

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
    @BindView(R.id.checkbox)
    CheckBox checkbox;
    @BindView(R.id.listview)
    MyListView listview;
    @BindView(R.id.lyt_checkbox)
    LinearLayout lytCheckbox;
    @BindView(R.id.view_checkall_bottom)
    View viewCheckallBottom;
    @BindView(R.id.view_checkall_top)
    View viewCheckallTop;
    @BindView(R.id.btn_save)
    TextView btnSave;
    private SelectUserAdapter adapter;
    private List<User> list_user = new ArrayList<>();
    private List<User> list_add = new ArrayList<>();
    private String type = "", number = "";
    private String mToast = "只能选择一个";
    private ProgressDialog pd;
    private Context context;
    private int position;
    private List<User> list_bumen;
    private List<String> list_xiangmu;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choouser);
        ButterKnife.bind(this);
        initView();
        if (null != getIntent().getStringExtra("number")) {
            number = getIntent().getStringExtra("number");
        }
        if (null != getIntent().getSerializableExtra("user")) {
            list_add = (List<User>) getIntent().getSerializableExtra("user");
            GetAllUser();
        }
        if (null != getIntent().getSerializableExtra("bumen")) {
            list_bumen = (List<User>) getIntent().getSerializableExtra("bumen");
            getBumen();
        }
        if (null != getIntent().getSerializableExtra("xiangmu")) {
            list_xiangmu = (List<String>) getIntent().getSerializableExtra("xiangmu");
            getXiangmu();
        } else {
            GetAllUser();
        }

        if (null != getIntent().getStringExtra("type")) {
            type = getIntent().getStringExtra("type");
        }

        position = getIntent().getIntExtra("position", 2017);
//        GetAllUser();

    }

    private void getXiangmu() {

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
                                adapter = new SelectUserAdapter(ChooUserActivity.this, list_user);
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
        context = ChooUserActivity.this;
        pd = new ProgressDialog(context);
        tvTitle.setText("选择成员");
        listview = (MyListView) findViewById(R.id.listview);
        list_bumen = new ArrayList<>();
        list_xiangmu = new ArrayList<>();
        query.addTextChangedListener(watcher);
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
                                    for (int j = 0; j < list_add.size(); j++) {
                                        if (list_user.get(i).getCode().equals(list_add.get(j).getCode())) {
                                            list_user.get(i).setChecked(true);
                                            Log.e("选中的", list_user.get(i).getName());
                                        }
                                    }
                                }
                                adapter = new SelectUserAdapter(ChooUserActivity.this, list_user);
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

    @OnClick({R.id.tv_back, R.id.tv_back_img, R.id.btn_save, R.id.checkbox, R.id.search_clear})
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
//                if (type.equals("task") && list_add.size() > 1) {
//                    Toast.makeText(SelectUserActivity.this, "任务的负责人只能选择一个", Toast.LENGTH_SHORT).show();
//                } else {
//                    Intent it = new Intent();
//                    it.putExtra("new_add_user", (Serializable) list_add);
//                    setResult(RESULT_OK, it);
//                    finish();
//                }
                getetUser();

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
                }
                adapter.update(list_user);
                break;
            case R.id.search_clear:
                query.setText("");
                break;
        }
    }

    private void getetUser() {
        if (!("".equals(type))) {
            if (type.equals("部门")) {
                list_bumen = list_user;
                if (list_bumen.size() > 0) {
                    Intent bumen = new Intent();
                    bumen.putExtra("new_add_user", (Serializable) list_bumen);
                    setResult(10086, bumen);
                    finish();
                } else {
                    Toast.makeText(ChooUserActivity.this, "请选择部门", Toast.LENGTH_SHORT).show();
                }
            }
            if (list_add.size() > 1) {
                switch (type) {
                    case "审核人":
                        Toast.makeText(ChooUserActivity.this, type + mToast, Toast.LENGTH_SHORT).show();
                        break;
                    case "抄送人":
                        Toast.makeText(ChooUserActivity.this, type + mToast, Toast.LENGTH_SHORT).show();
                        break;
                    case "task":
                        Toast.makeText(ChooUserActivity.this, "任务的负责人只能选择一个", Toast.LENGTH_SHORT).show();
                        break;
                    case "记录人":
                        Toast.makeText(ChooUserActivity.this, type + mToast, Toast.LENGTH_SHORT).show();
                        break;
                }
            } else {
                switch (type) {
                    case "审核人":
                        Intent editor = new Intent();
                        editor.putExtra("new_add_user", (Serializable) list_add);
                        setResult(EDITOR_CODE, editor);
                        finish();
                        break;
                    case "抄送人":
                        if (list_add.size() == 0) {
                            Toast.makeText(context, "请选择人员", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent chao = new Intent();
                            chao.putExtra("new_add_user", (Serializable) list_add);
                            setResult(CHAO_CODE, chao);
                            finish();
                        }
                        break;
                    case "task":
                        Intent task = new Intent();
                        task.putExtra("new_add_user", (Serializable) list_add);
                        setResult(RESULT_OK, task);
                        finish();
                        break;
                    case "记录人":
                        Intent recorder = new Intent();
                        recorder.putExtra("new_add_user", (Serializable) list_add);
                        setResult(RECORDER_CODE, recorder);
                        finish();
                        break;
                    case "重选":
                        Intent reselect = new Intent();
                        reselect.putExtra("new_add_user", (Serializable) list_add);
                        reselect.putExtra("position", position);
                        setResult(RESELECTE, reselect);
                        finish();
                        break;
                    case "部门":

                        break;
                    case "项目":
                        Intent xiangmu = new Intent();
                        xiangmu.putExtra("new_add_user", (Serializable) list_xiangmu);
                        setResult(XIANGMU, xiangmu);
                        finish();
                        break;

                }
            }

        } else {
            Log.e("WQ", "---list_Add_size是====》" + list_add.size());
            if (list_add.size() > 1) {
                Toast.makeText(this, "只能选择一个", Toast.LENGTH_SHORT).show();
            } else {
                Intent it = new Intent();
                it.putExtra("new_add_user", (Serializable) list_add);
                it.putExtra("number", number);
                setResult(RESULT_OK, it);
                finish();
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
                searchClear.setVisibility(View.VISIBLE);
                List<User> list = new ArrayList<>();
                for (int i = 0; i < list_user.size(); i++) {
                    if (list_user.get(i).getName().contains(s) || list_user.get(i).getPYDM().contains(s) || list_user.get(i).getPYDM().toLowerCase().contains(s)) {
                        list.add(list_user.get(i));
                    }
                }
                adapter = new SelectUserAdapter(ChooUserActivity.this, list);
                listview.setAdapter(adapter);
            } else {
                searchClear.setVisibility(View.INVISIBLE);
                adapter = new SelectUserAdapter(ChooUserActivity.this, list_user);
                listview.setAdapter(adapter);
            }
        }
    };

}
