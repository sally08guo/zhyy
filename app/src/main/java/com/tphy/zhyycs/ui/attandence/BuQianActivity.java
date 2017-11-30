package com.tphy.zhyycs.ui.attandence;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.jimmy.common.util.ToastUtils;
import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.User;
import com.tphy.zhyycs.ui.SelectUserOneActivity;
import com.tphy.zhyycs.ui.approval.bean.QRCodeTypes;
import com.tphy.zhyycs.ui.base_wang.BaseAppCompatActivity;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.CustomProgressDialog;
import com.tphy.zhyycs.utils.MyDialog;
import com.tphy.zhyycs.utils.StringCallback;
import com.tphy.zhyycs.widget.circleview.TimePickerDialog;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

public class BuQianActivity extends BaseAppCompatActivity {

    @BindView(R.id.toolbar_tv_title)
    TextView toolbar_tv_title;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.bq_tv_person)
    TextView tv_person;
    @BindView(R.id.bq_tv_time)
    TextView tv_time;
    @BindView(R.id.bq_et_reason)
    EditText et_reason;
    @BindView(R.id.kq_tv_commit)
    TextView kq_tv_commit;
    private boolean isPersonDone;
    private boolean isTimeDone;
    private String useCode;
    private String leibie;
    private String finalDate;
    private Date savedDate;
    private Dialog loadingDialog;
    private List<QRCodeTypes> qrCodeTypesList;
    private Context context;


    @Override

    protected int getContentViewLayoutID() {
        return R.layout.activity_bu_qian;
    }

    @Override
    protected void initViewsAndEvents() {
        qrCodeTypesList = new ArrayList<>();
        context = BuQianActivity.this;
        getQRCodeTypes();
    }

    @Override
    protected int getToolbarType() {
        return BALD_TOOLBAR;
    }


    @OnClick({R.id.kq_tv_commit, R.id.bq_tv_person, R.id.bq_tv_time})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.bq_tv_person:
                Intent intent = new Intent(this, SelectUserOneActivity.class);
                intent.putExtra("buqian", "补签");
                startActivityForResult(intent, 0);
                break;
            case R.id.bq_tv_time:
                showTimePicker();
                break;
            case R.id.kq_tv_commit:
                loadingDialog = CustomProgressDialog.createLoadingDialog(this, "提交中...");
                loadingDialog.show();
                loadingDialog.setCanceledOnTouchOutside(false);
                Log.e("WQ", "类别是==》" + leibie);
               /* boolean isResonDone = (null != et_reason.getText().toString()) && (!et_reason.getText().toString().equals(""));*/
                if (null != leibie) {
                    if (isPersonDone && isTimeDone) {
                        sendBuQianApply();
                    } else {
                        if (!isPersonDone) {
                            ToastUtils.showShortToast(this, "请选择人员");
                        } else if (!isTimeDone) {
                            ToastUtils.showShortToast(this, "请选择时间");
                        }
                    }
                } else {
                    ToastUtils.showShortToast(this, "请选择补签类型");
                }

                break;
        }
    }

    private void showTimePicker() {
        SlideDateTimeListener listener = new SlideDateTimeListener() {
            @Override
            public void onDateTimeSet(Date date) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = mFormatter.format(date);
                finalDate = formatter.format(date);
                tv_time.setText(time);
                tv_time.setCompoundDrawables(null, null, null, null);
                savedDate = date;
                isTimeDone = true;
            }

            @Override
            public void onDateTimeCancel() {
            }
        };
        SlideDateTimePicker.Builder builder = new SlideDateTimePicker.Builder(getSupportFragmentManager());
        builder.setListener(listener);
        builder.setMinDate(new Date());

        if (null != savedDate) {
            builder.setInitialDate(savedDate);
        } else {
            builder.setInitialDate(new Date());
        }
        builder.setIs24HourTime(true)
                .build()
                .show();
    }


    private void sendBuQianApply() {
//        if (null == customProgressDialog) {
//
//        }
        String remark = et_reason.getText().toString();
        HashMap<String, String> params = new HashMap<>();
        Map<String, String> map = new HashMap<>();
        map.put("LoginUserCode", code);
        map.put("UserCode", useCode);
        map.put("LeiBie", leibie);
        map.put("datetime", finalDate);
        map.put("remark", remark);
        params.put("paraJson", Common.MaptoJson(map));
        Log.e("WQ", "params==>" + params);
        String url = DemoApplication.serviceUrl + "/SignedSupplement";
        OkHttpUtils.post().url(url).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    if (null != loadingDialog) {
                        loadingDialog.dismiss();
                    }
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String msg = jsonObject.getString("msg");
                    if (success.equals("true")) {

                        View view = LayoutInflater.from(context).inflate(R.layout.item_buqian, null);
                        final MyDialog dialog = new MyDialog(context, 700, 700, view, R.style.dialog);
                        ImageView iv = (ImageView) view.findViewById(R.id.iv_dismiss);
                        iv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                        TextView tv = (TextView) view.findViewById(R.id.tv_content);
                        tv.setText("补签申请成功");
                        Button btn = (Button) view.findViewById(R.id.btn_save);
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                        dialog.show();
                        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失

                    } else {
                        Toast.makeText(BuQianActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String name = data.getStringExtra("user_name");
                useCode = data.getStringExtra("user_code");
                tv_person.setText(name);
                tv_person.setCompoundDrawables(null, null, null, null);
                isPersonDone = true;
            }
        }
    }

    private void getQRCodeTypes() {
        Map<String, String> params = new HashMap<>();
        params.put("paraJson", "");
        String url = DemoApplication.serviceUrl + "/GetQRCodeType";
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
//                        ToastUtils.showToast(getContext(), e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String success, msg;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            success = jsonObject.getString("success");
                            msg = jsonObject.getString("msg");
                            List<String> list = new ArrayList<>();
                            if (success.equals("true")) {
                                JSONArray result = jsonObject.getJSONArray("result");
                                qrCodeTypesList.clear();
                                for (int i = 0; i < result.length(); i++) {
                                    JSONObject object = result.getJSONObject(i);
                                    String code = object.getString("Code");
                                    String name = object.getString("Name");
                                    String pydm = object.getString("PYDM");
                                    QRCodeTypes qrCodeTypes = new QRCodeTypes();
                                    qrCodeTypes.setCode(code);
                                    qrCodeTypes.setName(name);
                                    qrCodeTypes.setPYDM(pydm);
                                    qrCodeTypesList.add(qrCodeTypes);
                                    list.add(name);
                                }
                                String[] mItems = list.toArray(new String[list.size()]);
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(BuQianActivity.this, android.R.layout.simple_spinner_item, mItems);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                leibie = qrCodeTypesList.get(0).getCode();
                                spinner.setAdapter(adapter);
                                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                                        leibie = qrCodeTypesList.get(pos).getCode();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });
                            } else {
                                ToastUtils.showToast(BuQianActivity.this, msg);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
