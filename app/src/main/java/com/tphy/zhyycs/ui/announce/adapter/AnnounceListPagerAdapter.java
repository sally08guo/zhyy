package com.tphy.zhyycs.ui.announce.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import java.util.List;

public class AnnounceListPagerAdapter extends FragmentPagerAdapter {

    private List<String> titles;
    private List<String> datas;
    private Context context;
    private List<Fragment> fragments;

    public AnnounceListPagerAdapter(FragmentManager fm, List<String> titles, List<String> datas, List<Fragment> fragments) {
        super(fm);
        this.titles = titles;
        this.datas = datas;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return titles.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return super.isViewFromObject(view, object);
    }
}
