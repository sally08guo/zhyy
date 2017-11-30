package com.tphy.zhyycs.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tphy.zhyycs.R;
import com.tphy.zhyycs.utils.Common;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017\11\28 0028.    筛选界面
 */

public class FilterActivity extends Activity implements View.OnClickListener {

    @BindView(R.id.tv_date_start)
    TextView tvDateStart;
    @BindView(R.id.tv_date_end)
    TextView tvDateEnd;
    @BindView(R.id.btn_save)
    Button btnSave;
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

    private Common common;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);
        initView();
    }


    private void initView() {
        common = new Common(this);
        tvTitle.setText("筛选");
    }

    @OnClick({R.id.tv_back, R.id.tv_back_img, R.id.btn_save, R.id.tv_date_start, R.id.tv_date_end})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
            case R.id.tv_back_img:
                finish();
                break;
            case R.id.btn_save:
                Intent it = new Intent();
                String str_start_date = tvDateStart.getText().toString().trim();
                String str_end_date = tvDateEnd.getText().toString().trim();
                if (str_start_date.length() < 6) {
                    str_start_date = "";
                }
                if (str_end_date.length() < 6) {
                    str_end_date = "";
                }
                it.putExtra("startDate", str_start_date);
                it.putExtra("endDate", str_end_date);
                setResult(RESULT_OK, it);
                finish();
                break;
            case R.id.tv_date_start:
                common.showDateDialog(this, tvDateStart);
                break;
            case R.id.tv_date_end:
                common.showDateDialog(this, tvDateEnd);
                break;
        }
    }
}
