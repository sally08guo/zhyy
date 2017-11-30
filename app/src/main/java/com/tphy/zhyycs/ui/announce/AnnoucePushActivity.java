package com.tphy.zhyycs.ui.announce;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.tphy.zhyycs.DemoApplication;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.User;
import com.tphy.zhyycs.ui.announce.bean.FileBean;
import com.tphy.zhyycs.ui.announce.bean.NewAnnounce;
import com.tphy.zhyycs.ui.attandence.adapter.DialogAdapter;
import com.tphy.zhyycs.ui.base_wang.BaseAppCompatActivity;
import com.tphy.zhyycs.utils.ActivityUtils;
import com.tphy.zhyycs.utils.Common;
import com.tphy.zhyycs.utils.StringCallback;
import com.zhy.http.okhttp.OkHttpUtils;
import com.tphy.zhyycs.ui.work.adapter.AddUserAdpater;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;


public class AnnoucePushActivity extends BaseAppCompatActivity {

    @BindView(R.id.am_et_title)
    EditText et_title;
    @BindView(R.id.am_et_content)
    EditText et_content;
    @BindView(R.id.am_tv_range)
    TextView tv_range;
    @BindView(R.id.ap_grid)
    GridView gridView;
    @BindView(R.id.ap_view_gone)
    View view_Gone;
    @BindView(R.id.am_tv_upfile)
    TextView tv_upfile;
    @BindView(R.id.am_tv_deletefile)
    TextView tv_deleteFile;
    @BindView(R.id.am_et_filename)
    EditText et_filename;
    @BindString(R.string.range_selected)
    String range_selected;
    private String title;
    private String content;
    private static List<User> list_bumens;
    private static List<User> list_xiangmus;
    private int mCheckedItem = -1;
    private boolean isSelect;
    private final String[] items = new String[]{"公司", "部门", "项目"};
    private final String[] types = new String[]{"1", "2", "3"};
    private File file;
    private boolean isFile = false;
    private String fileBites;
    private ActivityUtils activityUtils;
//    private String extensionName;
//    private String finalFileName;
    private String filename;


    @Override

    protected int getContentViewLayoutID() {
        return R.layout.activity_annouce_push;
    }

    @Override
    protected void initViewsAndEvents() {
        list_bumens = new ArrayList<>();
        list_xiangmus = new ArrayList<>();
        tv_title.setText("公告发布");
        tv_button.setText("确认发布");
        activityUtils = new ActivityUtils(this);
    }


    @Override
    protected int getToolbarType() {
        return BUTTON_TOOLBAR;
    }

    @OnClick(R.id.toolbar_tv_button)
    public void click() {
        getTextInput();
        boolean title_filled, content_filled;
        title_filled = !title.equals("");
        content_filled = !content.equals("");
        boolean isChooseRange = mCheckedItem != -1;
        boolean all_filled = title_filled && content_filled&&isChooseRange;
        if (null != fileBites) {
//            String trim = et_filename.getText().toString().trim();
//            if (!trim.equals("")) {
//                finalFileName = trim + extensionName;
                if (all_filled) {
                    sendAnnounce();
                } else {
                    if (!title_filled) Toast.makeText(this, "请填写标题", Toast.LENGTH_SHORT).show();
                    else if (!content_filled) {
                        Toast.makeText(this, "请填写内容", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(this, "请选择发布范围", Toast.LENGTH_SHORT).show();
                    }
                }
//            } else {
//                activityUtils.showToast("附件名不能为空！");
//            }
        } else {
            if (all_filled) {
                sendAnnounce();
            } else {
                if (!title_filled) Toast.makeText(this, "请填写标题", Toast.LENGTH_SHORT).show();
                else if (!content_filled) {
                    Toast.makeText(this, "请填写内容", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "请选择发布范围", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void sendAnnounce() {
//        String bv = "";
//        if (!filename.equals("")) {
//            bv = Common.imageToBase64(file.getPath());
//        }

//        fileBean.setFileName(filename);
        List<FileBean> fileBeanList = new ArrayList<>();
        if (null != fileBites) {
            FileBean fileBean = new FileBean();
            fileBean.setFileBytes(fileBites);
            fileBean.setFileName(filename);
            fileBeanList.add(fileBean);
        }
        NewAnnounce newAnnounce = new NewAnnounce();
        newAnnounce.setLoginUserCode(code);
        newAnnounce.setTitle(title);
        newAnnounce.setContent(content);
        newAnnounce.setType(types[mCheckedItem]);
        newAnnounce.setLstFile(fileBeanList);
        HashMap<String, String> params = new HashMap<>();
        List<String> range_raw = new ArrayList<>();
        if (mCheckedItem == 1) {
            for (int i = 0; i < list_bumens.size(); i++) {
                String code = list_bumens.get(i).getCode();
                range_raw.add(code);
            }
        }
        if (mCheckedItem == 2) {
            for (int i = 0; i < list_xiangmus.size(); i++) {
                String code = list_xiangmus.get(i).getCode();
                range_raw.add(code);
            }
        }
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < range_raw.size(); i++) {
            str.append(range_raw.get(i)).append(",");
        }
        newAnnounce.setRange(str.toString());
        params.put("paraJson", new Gson().toJson(newAnnounce));
        Log.e("WQ", "参数是===>" + params);
        String url = DemoApplication.serviceUrl + "/SendNotice";
        OkHttpUtils.post().url(url).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String msg = jsonObject.getString("msg");
                    if (success.equals("true")) {
                        activityUtils.showToast("公告已推送");
                        finish();
                    } else {
                        activityUtils.showToast(msg);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick({R.id.am_tv_range, R.id.am_tv_upfile,R.id.am_tv_deletefile})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.am_tv_range:
                showDialog();
                break;
            case R.id.am_tv_upfile:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
                break;
            case R.id.am_tv_deletefile:
                et_filename.setText("");
                fileBites = null;
                isFile = false;
                activityUtils.showToast("附件已移除");
                changeToUpload();
                break;
        }
    }


    private void showDialog() {
        DialogAdapter dialogAdapter = new DialogAdapter(this, items, mCheckedItem);
        @SuppressLint("InflateParams") View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_list, null, false);
        ListView listView = (ListView) dialogView.findViewById(R.id.dialog_listview);
        listView.setAdapter(dialogAdapter);
        final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.CustomDialogTheme).create();
        alertDialog.show();
        //noinspection ConstantConditions
        alertDialog.getWindow().setContentView(dialogView);
        dialogAdapter.setOnItemClickListener(new DialogAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                tv_range.setText(String.format(range_selected, items[position]));
                mCheckedItem = position;
                isSelect = true;
                toContacts(position);
                alertDialog.dismiss();
            }
        });
        dialogAdapter.setRadioOnCheckedListener(new DialogAdapter.RadioOnCheckedListener() {
            @Override
            public void onChecked(View view, int position) {
                tv_range.setText(String.format(range_selected, items[position]));
                mCheckedItem = position;
                isSelect = true;
                toContacts(position);
                alertDialog.dismiss();
            }
        });
    }

    private void toContacts(int i) {
        if (i != 0) {
            gridView.setVisibility(View.VISIBLE);
            Intent intent = new Intent(AnnoucePushActivity.this, ContactsActivity.class);
            switch (i) {
                case 1:
                    intent.putExtra("type", "bumen");
                    intent.putExtra("user", (Serializable) list_bumens);
                    break;
                case 2:
                    intent.putExtra("type", "xiangmu");
                    intent.putExtra("user", (Serializable) list_xiangmus);
                    break;
            }
            startActivityForResult(intent, 101);
        } else {
            gridView.setVisibility(View.GONE);
        }
    }

    private void getTextInput() {
        title = et_title.getText().toString();
        content = et_content.getText().toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            switch (resultCode) {
                case 1:
                    list_bumens.clear();
                    //noinspection unchecked
                    list_bumens = (List<User>) data.getSerializableExtra("new_add_user");
                    initGrid(list_bumens);
                    break;
                case 2:
                    list_xiangmus.clear();
                    //noinspection unchecked
                    list_xiangmus = (List<User>) data.getSerializableExtra("new_add_user");
                    initGrid(list_xiangmus);
                    break;
            }
        } else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
                String path = Common.getFileAbsolutePath(AnnoucePushActivity.this, uri);
                assert path != null;
                file = new File(path);
                filename = path.substring(path.lastIndexOf("/") + 1, path.length());
//                String fileNameNoEx = activityUtils.getFileNameNoEx(filename);
//                String extension = activityUtils.getExtensionName(filename);
//                extensionName = "." + extension;
                if (null != filename) {
                    et_filename.setText(filename);
                } else {
                    filename = "未命名";
                    et_filename.setText("未命名");
                }
                isFile = true;
                fileBites = Common.imageToBase64(file.getPath());
                changeToDelete();
            }
        }
    }

    private void changeToDelete() {
        tv_deleteFile.setVisibility(View.VISIBLE);
        tv_upfile.setVisibility(View.GONE);
    }

    private void changeToUpload() {
        tv_deleteFile.setVisibility(View.GONE);
        tv_upfile.setVisibility(View.VISIBLE);
    }

    private void initGrid(List<User> list) {
        if (!list.isEmpty()) {
            AddUserAdpater addUserAdpater = new AddUserAdpater(this, list);
            gridView.setAdapter(addUserAdpater);
            view_Gone.setVisibility(View.VISIBLE);
            isSelect = true;
        }
    }
}
