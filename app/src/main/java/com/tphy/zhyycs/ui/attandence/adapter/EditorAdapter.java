package com.tphy.zhyycs.ui.attandence.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.ModelResult;
import com.tphy.zhyycs.model.UserRange;

import java.util.ArrayList;
import java.util.List;


public class EditorAdapter extends RecyclerView.Adapter<EditorAdapter.EditorHolder> {

    private Context context;
    private List<ModelResult> modelResults;

    public EditorAdapter(Context context, List<ModelResult> modelResults) {
        this.context = context;
        this.modelResults = modelResults;
    }

//    public List<ModelResult> getModelResults() {
//        return modelResults;
//    }

    public void setModelResults(List<ModelResult> modelResults) {
        this.modelResults = modelResults;
        notifyDataSetChanged();
    }

    @Override
    public EditorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_editor, parent, false);
        return new EditorHolder(view);
    }

    @Override
    public void onBindViewHolder(final EditorHolder holder, int position) {
        final ModelResult modelResult = modelResults.get(position);
        String name = modelResult.getName();
//        String typeCode = modelResult.getTypeCode();
        List<UserRange> lstUser = modelResult.getLstUser();
        final String[] nameArray = new String[]{};
        for (int i = 0; i < lstUser.size(); i++) {
            String name1 = lstUser.get(i).getName();
            nameArray[i] = name1;
        }
        holder.tv0.setText(name);
        holder.tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editorPickerDialog(holder.tv1, nameArray, modelResult);
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelResults.size();
    }


    public class EditorHolder extends RecyclerView.ViewHolder {
        TextView tv0;
        TextView tv1;

        public EditorHolder(View itemView) {
            super(itemView);
            tv0 = (TextView) itemView.findViewById(R.id.item_editor_tv0);
            tv1 = (TextView) itemView.findViewById(R.id.item_editor_tv1);
        }
    }

    private void editorPickerDialog(final TextView textView, final String[] items, final ModelResult bean) {
        int p = bean.getSavedPosition();
        DialogAdapter dialogAdapter = new DialogAdapter(context, items, p);
        @SuppressLint("InflateParams") View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_list, null, false);
        ListView listView = (ListView) dialogView.findViewById(R.id.dialog_listview);
        listView.setAdapter(dialogAdapter);
        final AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.CustomDialogTheme).create();
        alertDialog.show();
        //noinspection ConstantConditions
        alertDialog.getWindow().setContentView(dialogView);
        dialogAdapter.setOnItemClickListener(new DialogAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                textView.setText(items[position]);
                textView.setCompoundDrawables(null, null, null, null);
                bean.setSavedPosition(position);
                notifyDataSetChanged();
                alertDialog.dismiss();

            }
        });
        dialogAdapter.setRadioOnCheckedListener(new DialogAdapter.RadioOnCheckedListener() {
            @Override
            public void onChecked(View view, int position) {
                textView.setText(items[position]);
                textView.setCompoundDrawables(null, null, null, null);
                bean.setSavedPosition(position);
                notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });
    }
}
