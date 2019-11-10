package yzw.ahaqth.accountbag.custom_views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.interfaces.ItemClickListener;

public class DrawMenuPopwin extends PopupWindow {
    protected class DrawMenuAdapter extends RecyclerView.Adapter<DrawMenuAdapter.VH> {
        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_item, viewGroup, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VH vh, int i) {
            final int position = vh.getAdapterPosition();

            if (mMode == 1) { // 分组下拉列表
                if (position == 0) {
                    vh.textView.setText("所有分组");
                    vh.textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((TextView) anchorView).setText("所有分组");
                            itemClickListener.click(position);
                            dismiss();
                        }
                    });
                } else {
                    final Object o = mDataList.get(position - 1);
                    vh.textView.setText(o.toString());
                    vh.textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((TextView) anchorView).setText(o.toString());
                            itemClickListener.click(position, o);
                            dismiss();
                        }
                    });
                }
            } else {
                final Object o = mDataList.get(position);
                vh.textView.setText(o.toString());
                vh.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((TextView) anchorView).setText(o.toString());
                        itemClickListener.click(position, o);
                        dismiss();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            if(mMode == 1)
                return mDataList.size() + 1;
            return mDataList.size();
        }

        class VH extends RecyclerView.ViewHolder {
            TextView textView;

            VH(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.textview1);
            }
        }
    }

    public void setmDataList(List<Object> dataList) {
        mDataList = dataList;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private List<Object> mDataList;
    private View anchorView;
    private int mMode;
    private ItemClickListener itemClickListener;

    public DrawMenuPopwin(View anchorView, int mode) {
        this.mMode = mode;
        this.anchorView = anchorView;
        this.setTouchable(true);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
        initialContentView();
    }

    private void initialContentView() {
        View contentView = LayoutInflater.from(anchorView.getContext()).inflate(R.layout.pop_win_layout, null);
        RecyclerView recyclerView = contentView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(anchorView.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(anchorView.getContext(),DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(new DrawMenuAdapter());
        TextView textView = contentView.findViewById(R.id.bottom_textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开分组设置页面

                dismiss();
            }
        });
        textView.setVisibility(mMode == 1 ? View.VISIBLE : View.GONE);
        this.setContentView(contentView);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
