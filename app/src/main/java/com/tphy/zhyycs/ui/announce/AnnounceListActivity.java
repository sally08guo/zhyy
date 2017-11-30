package com.tphy.zhyycs.ui.announce;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.tphy.zhyycs.R;
import com.tphy.zhyycs.ui.announce.adapter.AnnounceListPagerAdapter;
import com.tphy.zhyycs.ui.announce.fragment.ReadedFragment;
import com.tphy.zhyycs.ui.announce.fragment.UnreadFragment;
import com.tphy.zhyycs.ui.base_wang.BaseAppCompatActivity;
import com.tphy.zhyycs.utils.MyViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.tphy.zhyycs.ui.announce.fragment.UnreadFragment.stateChanged;

public class AnnounceListActivity extends BaseAppCompatActivity {


    @BindView(R.id.al_tablayout)
    TabLayout tabLayout;
    @BindView(R.id.al_viewpager)
    MyViewPager viewPager;
    @BindView(R.id.toolbar_iv_icon)
    ImageView imageView;

    private List<String> mTitle = new ArrayList<>();
    private List<String> mDatas = new ArrayList<>();
    private List<Fragment> fragmentList;
    private AnnounceListPagerAdapter mAdapter;
    private boolean isCan;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_announce_list;
    }

    @OnClick(R.id.toolbar_iv_icon)
    public void click() {
        if (isCan) {
            startActivity(new Intent(AnnounceListActivity.this, AnnoucePushActivity.class));
        } else {
            Toast.makeText(this, "您没有权限发布公告", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void initViewsAndEvents() {
        String permission = preferences.getString("Permission", "");
        isCan = permission.contains("InsertNotice");
        if (isCan) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setBackgroundResource(R.mipmap.ic_modify);
        } else {
            imageView.setVisibility(View.GONE);
            imageView.setBackgroundResource(R.drawable.modify_no_per);
        }
        tv_title.setText(getText(R.string.announce_manager));
        initFragments();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mAdapter = new AnnounceListPagerAdapter(fragmentManager, mTitle, mDatas, fragmentList);
        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this,
                R.drawable.layout_divider_vertical));
        viewPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    if (stateChanged) {
                        ReadedFragment item = (ReadedFragment) mAdapter.getItem(1);
                        item.initListData(0, true);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected int getToolbarType() {
        return IMAGE_TOOLBAR;
    }

    public void initFragments() {
        fragmentList = new ArrayList<>();
        UnreadFragment unreadFragment = new UnreadFragment();
        ReadedFragment readedFragment = new ReadedFragment();
        fragmentList.add(unreadFragment);
        fragmentList.add(readedFragment);
        mTitle.add("未读");
        mTitle.add("已读");

        mDatas.add("未读公告");
        mDatas.add("已读公告");
    }

//    public interface OnRequestNewData {
//        void requestNewData();
//    }

}







