package com.tphy.zhyycs.utils;

import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by zhy on 15/12/14.
 */
public abstract class StringCallback extends Callback<String> {

    @Override
    public String parseNetworkResponse(Response response, int id) throws IOException {
        String string = response.body().string().split("[>]")[2].split("[<]")[0];
        return string;
    }


}
