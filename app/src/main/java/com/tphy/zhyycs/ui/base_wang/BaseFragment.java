package com.tphy.zhyycs.ui.base_wang;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017\9\7 0007.
 */

public class BaseFragment extends AppFragment {

    protected Unbinder unbinder;
    protected SharedPreferences preferences;
    protected String code;
    protected Context context;


//    public T newInstance(int type) {
//        Bundle bundle = new Bundle();
//        bundle.putInt(ARG_TYPE,type);
//        T t = new T();
//        t.setArguments(bundle);
//        return
//    }

    @Override
    protected int getLayoutID() {
        return 0;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);

    }


    @Override
    protected void initData(Bundle arguments) {
        preferences = getActivity().getSharedPreferences("CYT_USERINFO", Context.MODE_PRIVATE);
        code = preferences.getString("Code", "");
        context = getContext();
        BaseAppCompatActivity holdingActivity = getHoldingActivity();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
