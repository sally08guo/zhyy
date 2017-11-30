package com.tphy.zhyycs.ui.work.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.tphy.zhyycs.model.Product;
import com.tphy.zhyycs.model.Project;
import com.tphy.zhyycs.model.User;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.StringCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2017\8\25 0025.   任务反馈
 */

public class CreateDemandActivity extends Activity implements View.OnClickListener {


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
    @BindView(R.id.edt_task_project_name)
    EditText edtTaskProjectName;
    @BindView(R.id.edt_task_module)
    EditText edtTaskModule;
    @BindView(R.id.edt_task_content)
    EditText edtTaskContent;
    @BindView(R.id.edt_file_name)
    EditText edtFileName;
    @BindView(R.id.tv_upfile)
    TextView tvUpfile;
    @BindView(R.id.lyt_upfile)
    LinearLayout lytUpfile;
    @BindView(R.id.btn_save)
    Button btnSave;
    private List<User> list_user = new ArrayList<>();
    private List<Project> list_project = new ArrayList<>();
    private List<Product> list_product = new ArrayList<>();
    private File file;
    private SharedPreferences sp;
    OkHttpClient client;
    private ProgressDialog pd;
    private Activity context;
    Common common;
    private String filename = "", str_project_name = "", str_project_code = "", str_product_code = "", title = "";

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("Select_Project_Demand")) {
                edtTaskProjectName.setText(intent.getStringExtra("name"));
                str_project_code = intent.getStringExtra("code");
            } else if (intent.getAction().equals("Select_Product_Demand")) {
                edtTaskModule.setText(intent.getStringExtra("name"));
                str_product_code = intent.getStringExtra("code");
            }
        }
    };

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    common.showSelectP(context, list_project, edtTaskProjectName, "1");
                    break;
                case 2:
                    common.showSelectPd(context, list_product, edtTaskModule, "1");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createdemand);
        ButterKnife.bind(this);
        if (null != getIntent().getStringExtra("project_code")) {
            str_project_code = getIntent().getStringExtra("project_code");
        }
        if (null != getIntent().getStringExtra("project_name")) {
            str_project_name = getIntent().getStringExtra("project_name");
        }
        if (!str_project_code.equals("")) {
            edtTaskProjectName.setEnabled(false);
            edtTaskProjectName.setText(str_project_name);
        }
        initView();

    }

    private void initView() {
        context = CreateDemandActivity.this;
        client = new OkHttpClient();
        common = new Common(context);
        sp = getSharedPreferences("CYT_USERINFO", MODE_PRIVATE);
        pd = new ProgressDialog(this);
        tvTitle.setText("问题反馈");
        tvUpfile.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        IntentFilter itf2 = new IntentFilter("Select_Project_Demand");
        registerReceiver(receiver, itf2);
        IntentFilter itf3 = new IntentFilter("Select_Product_Demand");
        registerReceiver(receiver, itf3);
    }

    @OnClick({R.id.edt_task_project_name, R.id.edt_task_module, R.id.tv_back, R.id.btn_right, R.id.tv_back_img, R.id.btn_save, R.id.lyt_upfile})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
            case R.id.tv_back_img:
                finish();
                break;
            case R.id.btn_save:

                if (str_project_code.equals("")) {
                    Toast.makeText(context, "请选择项目", Toast.LENGTH_SHORT).show();
                } else if (str_product_code.equals("")) {
                    Toast.makeText(context, "请选择模块", Toast.LENGTH_SHORT).show();
                } else if (edtTaskContent.getText().toString().trim().equals("")) {
                    Toast.makeText(context, "请输入任务描述", Toast.LENGTH_SHORT).show();
                } else {
                    CreateTask();
                }

                break;

            // 调用手机应用-拍照
            case R.id.lyt_upfile:
                Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
                intent1.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent1.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent1, 1);
                break;
            case R.id.edt_task_project_name:
                GetProjects();
                break;
            case R.id.edt_task_module:
                GetProduct();
                break;

        }
    }

    private void CreateTask() {
        pd.setMessage(getString(R.string.dialog_tip));
        pd.show();
        HashMap<String, String> params = new HashMap<>();
        String bv = "";
        if (!filename.equals("")) {
            bv = Common.imageToBase64(file.getPath());
        }
        Map<String, String> pa = new HashMap<>();

        pa.put("LoginUserCode", sp.getString("Code", ""));
        pa.put("Content", edtTaskContent.getText().toString().trim());
        pa.put("ProjectCode", str_project_code);
        pa.put("ProductCode", str_product_code);
        pa.put("IsTask", "0");
        pa.put("FileName", filename);
        pa.put("FileBytes", bv);

        params.put("paraJson", Common.MaptoJson(pa));
        String url = DemoApplication.serviceUrl + "/SendTask";

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
                                Toast.makeText(CreateDemandActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                                Intent it = new Intent("new_task_list");
                                sendBroadcast(it);
                                Intent it2 = new Intent("new_information");
                                sendBroadcast(it2);
                                Intent it3 = new Intent("PatientMain");
                                sendBroadcast(it3);
                                finish();
                            } else {
                                Toast.makeText(CreateDemandActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {

                        }
                        finish();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (!Common.isGrantExternalRW(context)) {
                    Toast.makeText(context, "请开启存储权限", Toast.LENGTH_SHORT).show();
                } else {
                    Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
                    String path = Common.getFileAbsolutePath(CreateDemandActivity.this, uri);
                    file = new File(path);
                    filename = path.substring(path.lastIndexOf("/") + 1, path.length());
                    edtFileName.setText(filename);
                    Log.e("文件的路径", file.getPath());
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void GetProjects() {
        list_project.clear();
        pd.setMessage(getString(R.string.loading));
        pd.show();
        Map<String, String> params = new HashMap<>();
        params.put("paraJson", Common.params("LoginUserCode", sp.getString("Code", "")));
        Log.e("传的参数", Common.params("LoginUserCode", sp.getString("Code", "")));
        String url = DemoApplication.serviceUrl + "/GetProjectsByUserCode";
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
                                pd.dismiss();
                                list_project = new Gson().fromJson(result, new TypeToken<List<Project>>() {
                                }.getType());
                                mHandler.sendEmptyMessage(1);
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        } catch (Exception e) {
                        }
                    }
                });
    }


    private void GetProduct() {
        list_product.clear();
        pd.setMessage(getString(R.string.loading));
        pd.show();
        Map<String, String> params = new HashMap<>();
        params.put("paraJson", Common.params("LoginUserCode", sp.getString("Code", "")));
        String url = DemoApplication.serviceUrl + "/GetMyProducts";
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
                                pd.dismiss();
                                List<Product> list = new Gson().fromJson(result, new TypeToken<List<Product>>() {
                                }.getType());

                                //去除非关联的模块
                                for (int i = 0; i < list.size(); i++) {
                                    if (list.get(i).getProjectCode().equals(str_project_code)) {
                                        list_product.add(list.get(i));
                                    }
                                }
                                mHandler.sendEmptyMessage(2);
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        } catch (Exception e) {
                        }
                    }
                });
    }
}
