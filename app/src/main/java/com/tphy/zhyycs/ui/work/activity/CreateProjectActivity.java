package com.tphy.zhyycs.ui.work.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.adapter.HorizontalListViewAdapter;
import com.tphy.zhyycs.model.LCB;
import com.tphy.zhyycs.model.Product;
import com.tphy.zhyycs.model.Project;
import com.tphy.zhyycs.model.ProjectStatusCode;
import com.tphy.zhyycs.model.Prole;
import com.tphy.zhyycs.model.Role;
import com.tphy.zhyycs.model.User;
import com.tphy.zhyycs.ui.work.adapter.ProductAdapter;
import com.tphy.zhyycs.ui.work.adapter.ProjectRoleAdapter;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.CustomProgressDialog;
import com.tphy.zhyycs.utils.HorizontalListView;
import com.tphy.zhyycs.utils.MyGridView;
import com.tphy.zhyycs.utils.MyListView;
import com.tphy.zhyycs.utils.StringCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONArray;
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
 * Created by Administrator on 2017\8\25 0025.
 */

public class CreateProjectActivity extends Activity implements View.OnClickListener {

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
    @BindView(R.id.edt_project_name)
    EditText edtProjectName;
    @BindView(R.id.tv_date_start)
    TextView tvDateStart;
    @BindView(R.id.edt_project_content)
    EditText edtProjectContent;
    @BindView(R.id.list)
    HorizontalListView list;
    @BindView(R.id.list_user)
    MyListView listUser;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.tv_xing_one)
    TextView tvXingOne;
    @BindView(R.id.tv_xing_two)
    TextView tvXingTwo;
    @BindView(R.id.tv_xing_three)
    TextView tvXingThree;
    @BindView(R.id.tv_xing_four)
    TextView tvXingFour;
    @BindView(R.id.tv_xing_five)
    TextView tvXingFive;
    @BindView(R.id.lyt_addlcb)
    LinearLayout lytAddLcb;
    @BindView(R.id.tv_adduser)
    TextView tvAdduser;
    @BindView(R.id.tv_product)
    TextView tvProduct;
    @BindView(R.id.lyt_product)
    LinearLayout lytProduct;
    @BindView(R.id.listview_product)
    MyGridView listProduct;

    private Project project;
    private Activity context;
    private Dialog pd;
    private SharedPreferences sp;
    HorizontalListViewAdapter mAdapter;
    ProjectRoleAdapter role_adapter;
    ProductAdapter product_adapter;
    List<LCB> list_lcb = new ArrayList<>();
    List<LCB> list_lcb_old = new ArrayList<>();
    List<Role> list_role = new ArrayList<>();
    List<User> list_user = new ArrayList<>();


    List<Product> list_product_old = new ArrayList<>();
    List<Product> list_product = new ArrayList<>();
    Map<String, List<User>> map_user = new HashMap<>();
    Map<String, List<User>> map_user_old = new HashMap<>();


    private String str_rating = "3", project_code = "";

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("DelUser")) {
                String mapcode = intent.getStringExtra("mapcode");
                String listcode = intent.getStringExtra("listcode");
                int p = Integer.valueOf(listcode);
                List<User> list = map_user.get(mapcode);
                list.remove(p);
                map_user.put(mapcode, list);
                role_adapter.notifyDataSetChanged();

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createproject);
        ButterKnife.bind(this);

        if (null != getIntent().getStringExtra("project_code")) {
            project_code = getIntent().getStringExtra("project_code");
        }
        initView();
        if (!project_code.equals("")) {
            GetProjectDetails();   // 修改
            btnSave.setText("修改");
        } else {     // 新建
            btnSave.setText("新建");
            GetLcb();
            GetProjectRole();
        }

        IntentFilter itf = new IntentFilter("DelUser");
        registerReceiver(receiver, itf);
        IntentFilter itf2 = new IntentFilter("Add_LCB");
        registerReceiver(receiver, itf2);
    }

    private void initView() {
        context = CreateProjectActivity.this;
        sp = getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);
        pd = CustomProgressDialog.createLoadingDialog(context, "正在加载模板...");
        tvTitle.setText("新建项目");

        product_adapter = new ProductAdapter(context, list_product);
        listProduct.setAdapter(product_adapter);

    }

    @OnClick({R.id.tv_back, R.id.tv_back_img, R.id.btn_save, R.id.lyt_addlcb, R.id.tv_product,
            R.id.tv_date_start, R.id.tv_xing_one, R.id.tv_xing_two, R.id.tv_xing_three, R.id.tv_xing_four, R.id.tv_xing_five})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
            case R.id.tv_back_img:
                finish();
                break;
            case R.id.btn_save:  // 保存

                if (!project_code.equals("")) {     // 修改项目
                    List<LCB> list_lcb_temp = new ArrayList<>();
                    List<LCB> list_del_lcb = list_lcb_old;
                    List<LCB> list_add_lcb = list_lcb;

                    // 交集
                    for (int i = 0; i < list_lcb_old.size(); i++) {
                        for (int j = 0; j < list_lcb.size(); j++) {
                            if (list_lcb_old.get(i).getCode().equals(list_lcb.get(j).getCode())) {
                                list_lcb_temp.add(list_lcb_old.get(i));
                            }
                        }
                    }
                    // 新增加的里程碑
                    for (int i = 0; i < list_lcb_temp.size(); i++) {
                        for (int j = 0; j < list_lcb.size(); j++) {
                            if (list_lcb_temp.get(i).getCode().equals(list_add_lcb.get(j).getCode())) {
                                list_add_lcb.remove(j);
                            }
                        }
                    }

                    // 删除的里程碑
                    for (int i = 0; i < list_lcb_temp.size(); i++) {
                        for (int j = 0; j < list_lcb_old.size(); j++) {
                            if (list_lcb_temp.get(i).getCode().equals(list_del_lcb.get(j).getCode())) {
                                list_del_lcb.remove(j);
                            }
                        }
                    }

                    List<Product> list_product_temp = new ArrayList<>();
                    List<Product> list_del_product = list_product_old;
                    List<Product> list_add_product = list_product;
                    // 交集
                    for (int i = 0; i < list_product_old.size(); i++) {
                        for (int j = 0; j < list_product.size(); j++) {
                            if (list_product_old.get(i).getCode().equals(list_product.get(j).getCode())) {
                                list_product_temp.add(list_product_old.get(i));
                            }
                        }
                    }
                    // 新增加的产品模块
                    for (int i = 0; i < list_product_temp.size(); i++) {
                        for (int j = 0; j < list_product.size(); j++) {
                            if (list_product_temp.get(i).getCode().equals(list_add_product.get(j).getCode())) {
                                list_add_product.remove(j);
                            }
                        }
                    }
                    // 删除的产品模块
                    for (int i = 0; i < list_product_temp.size(); i++) {
                        for (int j = 0; j < list_product_old.size(); j++) {
                            if (list_product_temp.get(i).getCode().equals(list_del_product.get(j).getCode())) {
                                list_del_product.remove(j);
                            }
                        }
                    }

                    Map<String, List<User>> map_user_temp = new HashMap<>();
                    Map<String, List<User>> map_user_del = map_user_old;
                    Map<String, List<User>> map_user_add = map_user;
                    // 交集
                    for (int i = 0; i < list_role.size(); i++) {
                        Role role = list_role.get(i);
                        List<User> list_temp = new ArrayList<>();
                        for (int j = 0; j < map_user_old.get(role.getCode()).size(); j++) {
                            for (int k = 0; k < map_user.get(role.getCode()).size(); k++) {
                                if (map_user_old.get(role.getCode()).get(j).getCode().equals(map_user.get(role.getCode()).get(k).getCode())) {
                                    list_temp.add(map_user_old.get(role.getCode()).get(j));
                                }
                            }
                        }
                        map_user_temp.put(role.getCode(), list_temp);
                    }
                    // 新增
                    for (int i = 0; i < list_role.size(); i++) {
                        Role role = list_role.get(i);
                        for (int j = 0; j < map_user_temp.get(role.getCode()).size(); j++) {
                            for (int k = 0; k < map_user.get(role.getCode()).size(); k++) {
                                if (map_user_temp.get(role.getCode()).get(j).getCode().equals(map_user_add.get(role.getCode()).get(k).getCode())) {
                                    map_user_add.get(role.getCode()).remove(k);
                                }
                            }
                        }
                    }
                    // 删除
                    for (int i = 0; i < list_role.size(); i++) {
                        Role role = list_role.get(i);
                        for (int j = 0; j < map_user_temp.get(role.getCode()).size(); j++) {
                            for (int k = 0; k < map_user_old.get(role.getCode()).size(); k++) {
                                if (map_user_temp.get(role.getCode()).get(j).getCode().equals(map_user_del.get(role.getCode()).get(k).getCode())) {
                                    map_user_del.get(role.getCode()).remove(k);
                                }
                            }
                        }
                    }
                    UpdateProject(list_add_lcb, list_del_lcb, map_user_add, map_user_del, list_add_product, list_del_product);

                } else {     // 新建项目
                    boolean bl = true;
                    for (int i = 0; i < list_role.size(); i++) {
                        if (map_user.get(list_role.get(i).getCode()).size() != 0) {
                            bl = false;
                        }
                    }
                    if (edtProjectName.getText().toString().trim().equals("")) {
                        Toast.makeText(context, "项目名不能为空", Toast.LENGTH_SHORT).show();
                    } else if (edtProjectContent.getText().toString().trim().equals("")) {
                        Toast.makeText(context, "项目描述不能为空", Toast.LENGTH_SHORT).show();
                    } else if (list_lcb.size() == 0) {
                        Toast.makeText(context, "项目里程碑不能为空", Toast.LENGTH_SHORT).show();
                    } else if (list_product.size() == 0) {
                        Toast.makeText(context, "产品模块不能为空！", Toast.LENGTH_SHORT).show();
                    } else if (bl) {
                        Toast.makeText(context, "项目相关成员不能为空", Toast.LENGTH_SHORT).show();
                    } else if (tvDateStart.getText().toString().trim().length() < 8) {
                        Toast.makeText(context, "请选择计划时效!", Toast.LENGTH_SHORT).show();
                    } else {
                        SendProject();
                    }
                }
                break;
            case R.id.tv_date_start:   // 计划时效
                Common common = new Common(this);
                common.showDateDialog(this, tvDateStart);
                break;
            case R.id.tv_xing_one:
                SelectRat(1);
                break;
            case R.id.tv_xing_two:
                SelectRat(2);
                break;
            case R.id.tv_xing_three:
                SelectRat(3);
                break;
            case R.id.tv_xing_four:
                SelectRat(4);
                break;
            case R.id.tv_xing_five:
                SelectRat(5);
                break;
            case R.id.lyt_addlcb:  // 新增里程碑
                Intent it = new Intent(CreateProjectActivity.this, SelectLCBActivity.class);
                it.putExtra("lcb", (Serializable) list_lcb);
                startActivityForResult(it, 102);
                break;
            case R.id.tv_product:  // 产品模块
                Intent it2 = new Intent(CreateProjectActivity.this, SelectProductActivity.class);
                it2.putExtra("product", (Serializable) list_product);
                startActivityForResult(it2, 103);
                break;
        }
    }

    private void SelectRat(int s) {
        switch (s) {
            case 1:
                tvXingOne.setText("★");
                tvXingTwo.setText("☆");
                tvXingThree.setText("☆");
                tvXingFour.setText("☆");
                tvXingFive.setText("☆");
                str_rating = "1";
                break;
            case 2:
                tvXingOne.setText("★");
                tvXingTwo.setText("★");
                tvXingThree.setText("☆");
                tvXingFour.setText("☆");
                tvXingFive.setText("☆");
                str_rating = "2";
                break;
            case 3:
                tvXingOne.setText("★");
                tvXingTwo.setText("★");
                tvXingThree.setText("★");
                tvXingFour.setText("☆");
                tvXingFive.setText("☆");
                str_rating = "3";
                break;
            case 4:
                tvXingOne.setText("★");
                tvXingTwo.setText("★");
                tvXingThree.setText("★");
                tvXingFour.setText("★");
                tvXingFive.setText("☆");
                str_rating = "4";
                break;
            case 5:
                tvXingOne.setText("★");
                tvXingTwo.setText("★");
                tvXingThree.setText("★");
                tvXingFour.setText("★");
                tvXingFive.setText("★");
                str_rating = "5";
                break;
        }
    }

    /**
     * 获取里程碑
     */
    private void GetLcb() {
        list_lcb.clear();
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
                        Log.e("onError", "aaaa");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String success = "", result = "", msg = "";
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                result = jsonObject.getString("result");
                                JSONObject js = new JSONObject(result);
                                list_lcb = new Gson().fromJson(js.getString("project_status"), new TypeToken<List<LCB>>() {
                                }.getType());
                                mAdapter = new HorizontalListViewAdapter(context, list_lcb);
                                list.setAdapter(mAdapter);
                                pd.dismiss();
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        } catch (Exception e) {
                        }
                    }
                });
    }

    /**
     * 获取项目详情
     */
    private void GetProjectDetails() {
        list_lcb_old.clear();
        list_role.clear();
        pd.show();
        Map<String, String> params = new HashMap<>();
        Map<String, String> pa = new HashMap<>();
        pa.put("LoginUserCode", sp.getString("Code", ""));
        pa.put("ProjectCode", project_code);
        params.put("paraJson", Common.MaptoJson(pa));
        String url = DemoApplication.serviceUrl + "/GetProjectDetails";
        Log.e("传递的参数", Common.MaptoJson(pa));
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
                        pd.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                result = jsonObject.getString("result");
                                JSONObject js = new JSONObject(result);
                                edtProjectName.setText(js.getString("Name")); // 项目名
                                edtProjectContent.setText(js.getString("Content")); // 项目描述
                                SelectRat(Integer.valueOf(js.getString("Star"))); // 项目星级
                                tvDateStart.setText(js.getString("PlanDate"));  // 计划时效

                                JSONArray json = new JSONArray(js.getString("lstRole"));
                                for (int i = 0; i < json.length(); i++) {
                                    JSONObject js1 = json.getJSONObject(i);
                                    Role role = new Role();
                                    role.setName(js1.getString("Name"));
                                    role.setCode(js1.getString("Code"));
                                    list_role.add(role);
                                    List<User> list_o = new Gson().fromJson(js1.getString("lstUser"), new TypeToken<List<User>>() {
                                    }.getType());
                                    List<User> list_o2 = new Gson().fromJson(js1.getString("lstUser"), new TypeToken<List<User>>() {
                                    }.getType());
                                    map_user_old.put(role.getCode(), list_o2);
                                    map_user.put(role.getCode(), list_o);
                                }
                                role_adapter = new ProjectRoleAdapter(context, list_role, map_user);
                                listUser.setAdapter(role_adapter);

                                list_lcb_old = new Gson().fromJson(js.getString("lstMilestone"), new TypeToken<List<LCB>>() {
                                }.getType());
                                list_lcb = list_lcb_old;
                                mAdapter = new HorizontalListViewAdapter(context, list_lcb);
                                list.setAdapter(mAdapter);

                                List<Product> list = new Gson().fromJson(js.getString("lstProduct"), new TypeToken<List<Product>>() {
                                }.getType());
                                for (int i = 0; i < list.size(); i++) {
                                    if (!list.get(i).getPCode().equals("0")) {
                                        list_product_old.add(list.get(i));
                                    }
                                }
                                list_product = list_product_old;

                                product_adapter = new ProductAdapter(context, list_product);
                                listProduct.setAdapter(product_adapter);

                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                        }
                    }
                });
    }

    /**
     * 获取项目成员类型
     */
    private void GetProjectRole() {
        list_role.clear();
        pd.show();
        Map<String, String> params = new HashMap<>();
        params.put("paraJson", Common.params("codeTables", "project_role"));
        String url = DemoApplication.serviceUrl + "/GetCodeListByName";
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("onError", "aaaa");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String success = "", result = "", msg = "";
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                result = jsonObject.getString("result");
                                JSONObject js = new JSONObject(result);
                                JSONArray json = new JSONArray(js.getString("project_role"));
                                for (int i = 0; i < json.length(); i++) {
                                    JSONObject js1 = json.getJSONObject(i);
                                    Role role = new Role();
                                    role.setName(js1.getString("Name"));
                                    role.setCode(js1.getString("Code"));
                                    role.setPYDM(js1.getString("PYDM"));
                                    list_role.add(role);
                                    map_user.put(js1.getString("Code"), list_user);
                                }
                                role_adapter = new ProjectRoleAdapter(context, list_role, map_user);
                                listUser.setAdapter(role_adapter);
                                pd.dismiss();
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        } catch (Exception e) {
                        }
                    }
                });
    }

    /**
     * 更新项目
     */
    private void UpdateProject(List<LCB> list_add_lcb, List<LCB> list_del_lcb, Map<String, List<User>> map_user_add, Map<String, List<User>> map_user_del, List<Product> list_add_product, List<Product> list_del_product) {

        pd = CustomProgressDialog.createLoadingDialog(context, "正在保存...");
        pd.show();
        HashMap<String, String> params = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("Code", project_code);
        param.put("Name", edtProjectName.getText().toString().trim());
        param.put("Content", edtProjectContent.getText().toString().trim());
        param.put("Star", str_rating);
        param.put("PlanDate", tvDateStart.getText().toString().trim());
        List<ProjectStatusCode> p = new ArrayList<>();
        for (int i = 0; i < list_add_lcb.size(); i++) {
            ProjectStatusCode pjsc = new ProjectStatusCode();
            pjsc.setProjectStatusCode(list_add_lcb.get(i).getCode());
            p.add(pjsc);
        }
        param.put("lstAddMilestone", p);
        List<ProjectStatusCode> p2 = new ArrayList<>();
        for (int i = 0; i < list_del_lcb.size(); i++) {
            ProjectStatusCode pjsc = new ProjectStatusCode();
            pjsc.setProjectStatusCode(list_del_lcb.get(i).getCode());
            p2.add(pjsc);
        }
        param.put("lstRemoveMilestone", p2);

        String[] s1 = new String[list_add_product.size()];
        for (int i = 0; i < list_add_product.size(); i++) {
            s1[i] = list_add_product.get(i).getCode();
        }
        param.put("lstAddProduct", s1);

        String[] s2 = new String[list_del_product.size()];
        for (int j = 0; j < list_del_product.size(); j++) {
            s2[j] = list_del_product.get(j).getCode();
        }
        param.put("lstRemoveProduct", s2);

        List<Prole> prole = new ArrayList<>();
        for (int i = 0; i < list_role.size(); i++) {
            Prole pr = new Prole();
            List<String> list_str = new ArrayList<>();
            pr.setProjectRoleCode(list_role.get(i).getCode());
            for (int j = 0; j < map_user_add.get(list_role.get(i).getCode()).size(); j++) {
                list_str.add(map_user_add.get(list_role.get(i).getCode()).get(j).getCode());
            }
            pr.setLstUserCode(list_str);
            prole.add(pr);
        }
        param.put("lstAddRoleUser", prole);

        List<Prole> prole2 = new ArrayList<>();
        for (int i = 0; i < list_role.size(); i++) {
            Prole pr = new Prole();
            List<String> list_str = new ArrayList<>();
            pr.setProjectRoleCode(list_role.get(i).getCode());
            for (int j = 0; j < map_user_del.get(list_role.get(i).getCode()).size(); j++) {
                list_str.add(map_user_del.get(list_role.get(i).getCode()).get(j).getCode());
            }
            pr.setLstUserCode(list_str);
            prole2.add(pr);
        }
        param.put("lstRemoveRoleUser", prole2);
        Gson gson = new Gson();
        Map<String, Object> pa = new HashMap<>();
        pa.put("Project", gson.toJson(param));
        String str = "{\"LoginUserCode\":\"" + sp.getString("Code", "") + "\"," + gson.toJson(pa).substring(1);

        params.put("paraJson", str);
        Log.e("输出的参数", str);

        String url = DemoApplication.serviceUrl + "/UpdateProject";
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
                        pd.dismiss();
                        String success = "", msg = "";
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                Toast.makeText(context, "修改项目成功", Toast.LENGTH_SHORT).show();
                                finish();
                                Intent it = new Intent("PatientMain");
                                sendBroadcast(it);
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {

                        }

                    }
                });
    }


    /**
     * 发起项目
     */
    private void SendProject() {

        pd = CustomProgressDialog.createLoadingDialog(context, "正在保存...");
        pd.show();
        HashMap<String, String> params = new HashMap<>();

        Map<String, Object> param = new HashMap<>();
        param.put("LoginUserCode", sp.getString("Code", ""));
        param.put("Name", edtProjectName.getText().toString().trim());
        param.put("Content", edtProjectContent.getText().toString().trim());
        param.put("Star", str_rating);

        List<ProjectStatusCode> p = new ArrayList<>();
        for (int i = 0; i < list_lcb.size(); i++) {
            ProjectStatusCode pjsc = new ProjectStatusCode();
            pjsc.setProjectStatusCode(list_lcb.get(i).getCode());
            p.add(pjsc);
        }

        String str_product = "";
        String temp = "";
        for (int i = 0; i < list_product.size(); i++) {
            str_product += list_product.get(i).getCode() + ",";
            if (!list_product.get(i).getPCode().equals(temp)) {
                str_product += list_product.get(i).getPCode() + ",";
                temp = list_product.get(i).getPCode();
            }
        }
        param.put("strProduct", str_product);
        param.put("strMilestone", p);
        param.put("PlanDate", tvDateStart.getText().toString().trim());

        List<Prole> prole = new ArrayList<>();
        for (int i = 0; i < list_role.size(); i++) {
            Prole pr = new Prole();
            List<String> list_str = new ArrayList<>();
            pr.setProjectRoleCode(list_role.get(i).getCode());

            for (int j = 0; j < map_user.get(list_role.get(i).getCode()).size(); j++) {
                list_str.add(map_user.get(list_role.get(i).getCode()).get(j).getCode());
            }
            pr.setLstUserCode(list_str);
            prole.add(pr);
        }
        param.put("strProjectRoleUser", prole);
        Gson gson = new Gson();
        params.put("paraJson", gson.toJson(param));
        Log.e("输出的参数", gson.toJson(param));

        String url = DemoApplication.serviceUrl + "/SendProject";
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        pd.dismiss();
                        Log.e("上传文件", "失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        pd.dismiss();
                        String success = "", msg = "";
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            if (success.equals("true")) {
                                Toast.makeText(context, "创建成功", Toast.LENGTH_SHORT).show();
                                finish();
                                Intent it = new Intent("PatientMain");
                                sendBroadcast(it);
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {

                        }

                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 101:  //
                    List<User> list_new = (List<User>) data.getSerializableExtra("new_add_user");
                    String postion = data.getExtras().getString("number");
                    Log.e("选择人员返回的数据" + postion, list_new.size() + "");
                    map_user.put(postion, list_new);
                    role_adapter.notifyDataSetChanged();
                    break;
                case 102:
                    list_lcb = (List<LCB>) data.getSerializableExtra("Add_LCB");
                    mAdapter.update(list_lcb);
                    break;
                case 103:
                    list_product = (List<Product>) data.getSerializableExtra("Add_Product");
                    product_adapter.update(list_product);
                    break;

            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
