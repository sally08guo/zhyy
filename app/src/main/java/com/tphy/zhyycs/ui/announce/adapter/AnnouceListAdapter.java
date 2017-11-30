package com.tphy.zhyycs.ui.announce.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tphy.zhyycs.R;
import com.tphy.zhyycs.model.AnnounceMentResult;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AnnouceListAdapter extends RecyclerView.Adapter<AnnouceListAdapter.ViewHolder> {

    private List<AnnounceMentResult> list;
    //    private OnDeleteClickListener deleteClickListener;
//    private OnStickClickListener stickClickListener;
    private OnItemClickListener onItemClickListener;
//    private OnOpenClickListener onOpenClickListener;

    public AnnouceListAdapter(Context mContext, List<AnnounceMentResult> list) {
        this.list = list;
    }

//    public void setDeleteClickListener(OnDeleteClickListener deleteClickListener) {
//        this.deleteClickListener = deleteClickListener;
//    }
//
//    public void setStickClickListener(OnStickClickListener stickClickListener) {
//        this.stickClickListener = stickClickListener;
//    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

//    public void setOnOpenClickListener(OnOpenClickListener onOpenClickListener) {
//        this.onOpenClickListener = onOpenClickListener;
//    }

    public void refreshData(List<AnnounceMentResult> list) {
        this.list = list;
        notifyDataSetChanged();
    }

//    public void removeItem(int position) {
//        if (list.size() > 0) {
//            list.remove(position);
//            notifyItemRemoved(position);
//            notifyItemRangeChanged(position, list.size() - position);
////            notifyItemRangeRemoved(position,list.size()-position);
//        }
//
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_announce_listnew, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        AnnounceMentResult announceMentResult = list.get(position);
        String title = announceMentResult.getTitle();
        String startman = announceMentResult.getSendUserName();
        String time = announceMentResult.getSendTime();
        holder.title.setText(title);
        holder.startman.setText(startman);
        holder.time.setText(time);
//        holder.swipeDragLayout.addListener(new SwipeDragLayout.SwipeListener() {
//            @Override
//            public void onUpdate(SwipeDragLayout layout, float offset) {
//
//            }
//
//            @Override
//            public void onOpened(SwipeDragLayout layout) {
//                if (onOpenClickListener != null) {
//                    onOpenClickListener.itemOpenListerner(holder.swipeDragLayout, position);
//                }
//            }
//
//            @Override
//            public void onClosed(SwipeDragLayout layout) {
//
//            }
//
//            @Override
//            public void onClick(SwipeDragLayout layout) {
//                if (onItemClickListener != null) {
//                    onItemClickListener.itemClickListerner(holder.itemView, position);
//                }
//            }
//        });
//        holder.menu_delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (deleteClickListener != null) {
//                    deleteClickListener.deleteClickListener(holder.menu_delete, position);
//                }
//
//
//            }
//        });
//        holder.menu_top.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (stickClickListener != null) {
//                    stickClickListener.stickClickListener(holder.menu_top, position);
//                }
//
//            }
//        });
        holder.item_surface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.itemClickListerner(holder.item_surface,position);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //        @BindView(R.id.item_al_icon)
//        ImageView icon;
        @BindView(R.id.item_al_typename)
        TextView typename;
        @BindView(R.id.item_al_startman)
        TextView startman;
        @BindView(R.id.item_al_title)
        TextView title;
        @BindView(R.id.item_al_time)
        TextView time;
        @BindView(R.id.item_surface)
        RelativeLayout item_surface;
//        @BindView(R.id.swipe_drag)
//        SwipeDragLayout swipeDragLayout;
//        @BindView(R.id.item_al_delete)
//        TextView menu_delete;
//        @BindView(R.id.item_al_top)
//        TextView menu_top;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

//    public interface OnDeleteClickListener {
//        void deleteClickListener(View view, int position);
//
//    }

//    public interface OnStickClickListener {
//        void stickClickListener(View view, int position);
//
//    }

    public interface OnItemClickListener {
        void itemClickListerner(View view, int position);
    }

//    public interface OnOpenClickListener {
//        void itemOpenListerner(View view, int position);
//    }


}
