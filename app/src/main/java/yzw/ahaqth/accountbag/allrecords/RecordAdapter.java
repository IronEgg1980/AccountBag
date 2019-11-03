package yzw.ahaqth.accountbag.allrecords;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.util.List;

import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.interfaces.ItemClickListener;
import yzw.ahaqth.accountbag.modules.AccountRecord;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordVH> {
    private List<AccountRecord> mList;
    private ItemClickListener clickListener;
    private ItemClickListener delClick;

    public void setDelClick(ItemClickListener delClick) {
        this.delClick = delClick;
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public RecordAdapter(List<AccountRecord> list) {
        this.mList = list;
    }

    @NonNull
    @Override
    public RecordVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.record_item, viewGroup, false);
        return new RecordVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecordVH recordVH, int i) {
        final AccountRecord record = mList.get(i);
        recordVH.delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                recordVH.swipeMenuLayout.quickClose();
                delClick.click(recordVH.getAdapterPosition(),recordVH);
            }
        });
        recordVH.recordNameTV.setText(record.getRecordName());
        recordVH.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.click(recordVH.getAdapterPosition(), recordVH.recordNameTV, recordVH.root);
            }
        });
        if (record.getSortIndex() > 1) {
            recordVH.favoriteBT.setImageResource(R.drawable.ic_favorite_24dp);
            recordVH.favoriteBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    record.setSortIndex(1);
                    record.save();
                    notifyItemChanged(recordVH.getAdapterPosition());
                }
            });
        } else {
            recordVH.favoriteBT.setImageResource(R.drawable.ic_favorite_border_24dp);
            recordVH.favoriteBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    record.setSortIndex(100);
                    record.save();
                    notifyItemChanged(recordVH.getAdapterPosition());
                }
            });
        }
        if (record.getTotalTextCount() == 0)
            recordVH.textExtraTV.setVisibility(View.GONE);
        else {
            recordVH.textExtraTV.setVisibility(View.VISIBLE);
            String textcount = "  " + record.getTotalTextCount();
            recordVH.textExtraTV.setText(textcount);
        }
//        if(TextUtils.isEmpty(record.getAccountName()))
//            recordVH.linear1.setVisibility(View.GONE);
//        else {
//            recordVH.linear1.setVisibility(View.VISIBLE);
//            recordVH.accountNameTV.setText(record.getAccountName());
//            recordVH.accountNameTV.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ToolUtils.copy(v.getContext(), record.getAccountName());
//                    new ToastFactory(v.getContext()).showCenterToast("用户名已复制");
//                }
//            });
//        }
//        if(TextUtils.isEmpty(record.getAccountPWD()))
//            recordVH.linear2.setVisibility(View.GONE);
//        else {
//            recordVH.linear2.setVisibility(View.VISIBLE);
//            if (record.isShowPWD())
//                recordVH.accountPwdTV.setText(record.getAccountPWD());
//            else
//                recordVH.accountPwdTV.setText("******");
//            recordVH.accountPwdTV.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ToolUtils.copy(v.getContext(), record.getAccountPWD());
//                    new ToastFactory(v.getContext()).showCenterToast("密码已复制");
//                }
//            });
//            recordVH.showPWDTV.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    record.setShowPWD(!record.isShowPWD());
//                    notifyItemChanged(recordVH.getAdapterPosition());
//                }
//            });
//        }
//        if (record.getExtraTextCount() == 0)
//            recordVH.textExtraTV.setVisibility(View.GONE);
//        else {
//            recordVH.textExtraTV.setVisibility(View.VISIBLE);
//            String s1 = "其他信息： " + record.getExtraTextCount();
//            recordVH.textExtraTV.setText(s1);
//        }
        if (record.getExtraImageCount() == 0)
            recordVH.imageExtraTV.setVisibility(View.GONE);
        else {
            recordVH.imageExtraTV.setVisibility(View.VISIBLE);
            String s2 = "  " + record.getExtraImageCount();
            recordVH.imageExtraTV.setText(s2);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    protected class RecordVH extends RecyclerView.ViewHolder {
        private ImageButton favoriteBT;
        private TextView recordNameTV;
        private LinearLayout root;
        private TextView imageExtraTV;
        private TextView textExtraTV;
        private View delButton;
        private SwipeMenuLayout swipeMenuLayout;
//        private TextView accountNameTV;
//        private TextView accountPwdTV;

        //        private TextView showPWDTV;
//        private LinearLayout linear1,linear2;
        public RecordVH(@NonNull View itemView) {
            super(itemView);
            favoriteBT = itemView.findViewById(R.id.favorite);
            root = itemView.findViewById(R.id.root);
            recordNameTV = itemView.findViewById(R.id.record_time_TV);
            imageExtraTV = itemView.findViewById(R.id.image_extra_TV);
            textExtraTV = itemView.findViewById(R.id.text_extra_TV);
            delButton = itemView.findViewById(R.id.delbutton);
            swipeMenuLayout = itemView.findViewById(R.id.swipemenu);
//            accountNameTV = itemView.findViewById(R.id.account_name_TV);
//            accountPwdTV = itemView.findViewById(R.id.account_pwd_TV);
//            showPWDTV = itemView.findViewById(R.id.showPWD);
//            linear1 = itemView.findViewById(R.id.linear1);
//            linear2 = itemView.findViewById(R.id.linear2);

        }

        public void closeMenu(){
            swipeMenuLayout.quickClose();
        }
    }
}
