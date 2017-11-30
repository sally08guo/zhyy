package com.tphy.zhyycs.ui.base_wang;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2017\9\7 0007.
 */

public abstract class AppFragment extends Fragment {

    protected FragmentActivity mActivity;
    private String wq = "WQ";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutID(), container, false);
        Log.e(wq, getClass().getSimpleName() + "==>OncreateView");
        initData(getArguments());
        initView(view, savedInstanceState);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(wq, getClass().getSimpleName() + "==>OnviewCreated");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releaseView();
    }

    protected void releaseView() {

    }

    protected abstract int getLayoutID();

    protected abstract void initData(Bundle arguments);

    protected abstract void initView(View view, Bundle savedInstanceState);

    protected BaseAppCompatActivity getHoldingActivity() {
        if (getActivity() instanceof BaseAppCompatActivity) {
            return (BaseAppCompatActivity) getActivity();
        } else {
            throw new ClassCastException("activity must extends BaseActivity");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(wq, getClass().getSimpleName() + "==>OnStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(wq, getClass().getSimpleName() + "==>OnResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(wq, getClass().getSimpleName() + "==>Onpause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(wq, getClass().getSimpleName() + "==>onstop");
    }
}
