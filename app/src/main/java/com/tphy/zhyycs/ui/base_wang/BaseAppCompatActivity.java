package com.tphy.zhyycs.ui.base_wang;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tphy.zhyycs.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017\8\25 0025.
 */

public abstract class BaseAppCompatActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Screen information
     */
    protected int mScreenWidth = 0;
    protected int mScreenHeight = 0;
    protected float mScreenDensity = 0.0f;

    /**
     * context
     */
    protected Context mContext = null;

    protected Unbinder bind;
    protected Toolbar mToolbar;
    protected ImageView iv_back;
    protected TextView tv_back;
    protected TextView tv_title;
    protected TextView tv_button;
    protected ImageView iv_icon;
    protected ImageView iv_addIcon;
    protected int BUTTON_TOOLBAR = 0;
    protected int IMAGE_TOOLBAR = 1;
    protected int BALD_TOOLBAR = 2;
    protected int ADD_TOOLBAR = 3;
    protected SharedPreferences preferences;
    public static final int EDITOR_CODE = 110;
    public static final int CHAO_CODE = 119;
    public static final int RECORDER_CODE = 120;
    public static final int RESELECTE = 122;
    public static final int BUMEN = 10086;
    public static final int XIANGMU = 10010;
    public static final String wq = "WQ";

    protected String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        super.onCreate(savedInstanceState);

        mContext = this;
        preferences = getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);
        code = preferences.getString("Code", "");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        mScreenDensity = displayMetrics.density;
        mScreenHeight = displayMetrics.heightPixels;
        mScreenWidth = displayMetrics.widthPixels;


        if (getContentViewLayoutID() != 0) {
            setContentView(getContentViewLayoutID());
        } else {
            throw new IllegalArgumentException("You must return a right contentView layout resource Id");
        }

        initViewsAndEvents();
    }

//    @OnClick({R.id.toolbar_iv_back, R.id.toolbar_tv_back})
//    public void backHome(View view) {
//        switch (view.getId()) {
//            case R.id.toolbar_iv_back:
//                finish();
//                break;
//            case R.id.toolbar_tv_back:
//                finish();
//                break;
//        }
//    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        bind = ButterKnife.bind(this);
        mToolbar = ButterKnife.findById(this, R.id.common_toolbar);
        iv_back = ButterKnife.findById(this, R.id.toolbar_iv_back);
        tv_back = ButterKnife.findById(this, R.id.toolbar_tv_back);
        tv_title = ButterKnife.findById(this, R.id.toolbar_tv_title);
        iv_icon = ButterKnife.findById(this, R.id.toolbar_iv_icon);
        tv_button = ButterKnife.findById(this, R.id.toolbar_tv_button);
        iv_addIcon = ButterKnife.findById(this, R.id.toolbar_iv_addIcon);
        tv_back.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        switch (getToolbarType()) {
            case 0:
                setImageGone();
                setButtonVisible();
                break;
            case 1:
                setButtonGone();
                setmodifyVisible();
                setAddGone();
                break;
            case 2:
                setButtonGone();
                setImageGone();
                break;
            case 3:
                setButtonGone();
                setmodifyGone();
                setAddVisible();
                break;
        }
    }

    protected void setImageGone() {
        iv_icon.setVisibility(View.GONE);
        iv_addIcon.setVisibility(View.GONE);
    }

    protected void setButtonGone() {
        tv_button.setVisibility(View.GONE);
    }

    protected void setAddGone() {
        iv_addIcon.setVisibility(View.GONE);
    }

    protected void setmodifyGone() {
        iv_icon.setVisibility(View.GONE);
    }

    protected void setButtonVisible() {
        tv_button.setVisibility(View.VISIBLE);
    }

    protected void setmodifyVisible() {
        iv_icon.setVisibility(View.VISIBLE);
    }

    protected void setAddVisible() {
        iv_addIcon.setVisibility(View.VISIBLE);
    }


    @Override
    public void finish() {
        super.finish();
        bind.unbind();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * bind layout resource file
     *
     * @return id of layout resource
     */
    protected abstract int getContentViewLayoutID();

    /**
     * init all views and add events
     */
    protected abstract void initViewsAndEvents();

    protected abstract int getToolbarType();

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_tv_back:
                finish();
                break;
            case R.id.toolbar_iv_back:
                finish();
                break;
        }
    }

}
