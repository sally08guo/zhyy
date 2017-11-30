package com.tphy.zhyycs.ui.work.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.LCB;
import com.tphy.zhyycs.model.Product;
import com.tphy.zhyycs.model.ProductExpand;
import com.tphy.zhyycs.ui.work.adapter.ProductExpandableAdapter;
import com.tphy.zhyycs.ui.work.adapter.SelectLCBAdapter;
import com.tphy.zhyycs.ui.work.adapter.SelectProductAdapter;
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
 * 选择产品模块
 */

public class SelectProductActivity extends Activity implements View.OnClickListener {

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
    ExpandableListView listview;
    @BindView(R.id.btn_save)
    TextView btnSave;
    private ProductExpandableAdapter adapter;
    private List<Product> list_product = new ArrayList<>();
    private List<Product> list_add = new ArrayList<>();
    private ProgressDialog pd;
    private Context context;

    private List<ProductExpand> list_grid = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectproduct);
        ButterKnife.bind(this);
        initView();
        if (null != getIntent().getSerializableExtra("product")) {
            list_add = (List<Product>) getIntent().getSerializableExtra("product");
        }
        GetCodeListByName();
    }


    private void initView() {
        context = SelectProductActivity.this;
        pd = new ProgressDialog(context);
        tvTitle.setText("产品模块");
        //设置 属性 去掉默认向下的箭头
        listview.setGroupIndicator(null);
    }


    private void GetCodeListByName() {
        list_product.clear();
        pd.setMessage("加载数据...");
        pd.show();
        Map<String, String> params = new HashMap<>();
        params.put("paraJson", Common.params("codeTables", "product"));
        String url = DemoApplication.serviceUrl + "/GetCodeListByName";
        Log.e("传递的参数", Common.params("codeTables", "product"));
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
                                list_product = new Gson().fromJson(js.getString("product"), new TypeToken<List<Product>>() {
                                }.getType());

                                for (int i = 0; i < list_product.size(); i++) {
                                    for (int j = 0; j < list_add.size(); j++) {
                                        if (list_product.get(i).getCode().equals(list_add.get(j).getCode())) {
                                            list_product.get(i).setChecked(true);
                                        }
                                    }
                                }

                                List<Product> list_pcoed = new ArrayList<Product>();
                                for (int i = 0; i < list_product.size(); i++) {
                                    if (list_product.get(i).getPCode().equals("0")) {
                                        Product p = new Product();
                                        p.setCode(list_product.get(i).getCode());
                                        p.setName(list_product.get(i).getName());
                                        list_pcoed.add(p);
                                    }
                                }

                                for (int j = 0; j < list_pcoed.size(); j++) {
                                    ProductExpand pe = new ProductExpand();
                                    pe.setPCode(list_pcoed.get(j).getCode());
                                    pe.setPName(list_pcoed.get(j).getName());
                                    List<Product> list = new ArrayList<Product>();

                                    for (int k = 0; k < list_product.size(); k++) {
                                        if (list_pcoed.get(j).getCode().equals(list_product.get(k).getPCode())) {
                                            list.add(list_product.get(k));
                                        }
                                    }
                                    if (list.size() == 0) {
                                        Product p = new Product();
                                        p.setCode(list_pcoed.get(j).getCode());
                                        p.setName(list_pcoed.get(j).getName());
                                        list.add(p);
                                    }
                                    pe.setListProduct(list);
                                    list_grid.add(pe);
                                }
                                adapter = new ProductExpandableAdapter(SelectProductActivity.this, list_grid);
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

                for (int i = 0; i < list_grid.size(); i++) {
                    for (int j = 0; j < list_grid.get(i).getListProduct().size(); j++) {
                        if (list_grid.get(i).getListProduct().get(j).isChecked()) {
                            list_add.add(list_grid.get(i).getListProduct().get(j));
                        }
                    }
                }

                Intent it = new Intent();
                it.putExtra("Add_Product", (Serializable) list_add);
                setResult(RESULT_OK, it);
                finish();

                break;
        }
    }
}
