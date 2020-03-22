package yzw.ahaqth.accountbag.allrecords;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import yzw.ahaqth.accountbag.EmptyVH;
import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.interfaces.DialogDismissListener;
import yzw.ahaqth.accountbag.interfaces.ItemClickListener;
import yzw.ahaqth.accountbag.modules.AccountRecord;
import yzw.ahaqth.accountbag.operators.RecordOperator;
import yzw.ahaqth.accountbag.tools.DialogFactory;
import yzw.ahaqth.accountbag.tools.ToastFactory;
import yzw.ahaqth.accountbag.tools.ToolUtils;

public class DeleResumeAdapter extends RecyclerView.Adapter {
    private List<AccountRecord> list;
//    private Context mContext;
    private FragmentActivity mActivity;
    private ToastFactory toastFactory;
    private SimpleDateFormat simpleDateFormat;
    private ItemClickListener longClick;
    private int[] ids = {R.mipmap.empty1,R.mipmap.empty2,R.mipmap.empty3,R.mipmap.empty4,R.mipmap.empty5,R.mipmap.empty6,R.mipmap.empty7};

    public void setLongClick(ItemClickListener longClick) {
        this.longClick = longClick;
    }

    public DeleResumeAdapter(FragmentActivity activity, List<AccountRecord> list){
        this.list = list;
        mActivity = activity;
        simpleDateFormat = new SimpleDateFormat("删除时间：yyyy年M月d日 HH:mm:ss", Locale.CHINA);
        toastFactory = new ToastFactory(activity);
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i == 0)
            return new EmptyVH(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty_item, viewGroup, false));
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dele_record_item,viewGroup,false);
        return new VH(view);
    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position).getSortIndex() == 999999){
            return 0;
        }
        return 1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final AccountRecord accountRecord = list.get(i);
        if(accountRecord.getSortIndex() == 999999) {
            EmptyVH vh = (EmptyVH) viewHolder;
            int index = new Random().nextInt(7);
            vh.imageView.setImageResource(ids[index]);
            vh.textView.setText("回收站内貌似很干净啊!");
            return;
        }
        final VH vh = (VH) viewHolder;
        vh.nameTV.setText(accountRecord.getRecordName());
        long deleTime = accountRecord.getDeleTime();
        int remainDay = 30 - (int) ((System.currentTimeMillis() - deleTime) / ToolUtils.ONE_DAY_MILLES);
        String s = remainDay+"天";
        SpannableString spannableString = new SpannableString(s);
        spannableString.setSpan(new RelativeSizeSpan(0.4f),s.length() - 1,s.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        vh.delTimeTV.setText(simpleDateFormat.format(deleTime));
        vh.remainDayTV.setText(spannableString);
        if(accountRecord.isMultiMode){
            vh.checkBox.setVisibility(View.VISIBLE);
            vh.checkBox.setChecked(accountRecord.isSeleted);
            vh.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    accountRecord.isSeleted = !accountRecord.isSeleted;
                    notifyItemChanged(vh.getAdapterPosition());
                }
            });
            vh.swipeMenuLayout.quickClose();
            vh.swipeMenuLayout.setSwipeEnable(false);
            vh.group.setLongClickable(false);
        }else{
            vh.group.setLongClickable(true);
            vh.group.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    for(AccountRecord r:list){
                        r.isMultiMode = true;
                    }
                    accountRecord.isSeleted = true;
                    notifyDataSetChanged();
                    longClick.click(vh.getAdapterPosition());
                    return true;
                }
            });
            vh.resumeBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecordOperator.resumeOne(accountRecord);
                    list.remove(vh.getAdapterPosition());
                    notifyItemRemoved(vh.getAdapterPosition());
                    toastFactory.showCenterToast("恢复操作成功");
                }
            });
            vh.clearBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFactory.getConfirmDialog("是否彻底清除该项目？\n注意：清除后无法恢复！")
                            .setDismissListener(new DialogDismissListener() {
                                @Override
                                public void onDismiss(boolean isConfirm, Object... valus) {
                                    if(isConfirm){
                                        RecordOperator.clear(list.get(vh.getAdapterPosition()));
                                        list.remove(vh.getAdapterPosition());
                                        notifyItemRemoved(vh.getAdapterPosition());
                                        toastFactory.showCenterToast("已清除该项");
                                    }
                                }
                            })
                            .show(mActivity.getSupportFragmentManager(),"confirm");
                }
            });
            vh.checkBox.setVisibility(View.GONE);
            vh.swipeMenuLayout.setSwipeEnable(true);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void resumeAll(){
        for(int i = getItemCount() - 1;i > -1;i--){
            AccountRecord accountRecord = list.get(i);
            if(accountRecord.isSeleted){
                RecordOperator.resumeOne(accountRecord);
                list.remove(i);
            }
        }
        toastFactory.showCenterToast("批量恢复成功");
    }

    public void clearAll(){
        for(int i = getItemCount() - 1;i > -1;i--){
            AccountRecord accountRecord = list.get(i);
            if(accountRecord.isSeleted){
                RecordOperator.clear(accountRecord);
                list.remove(i);
            }
        }
        toastFactory.showCenterToast("批量清除成功");
    }

    public void cancelMultiMode(){
        for(AccountRecord record:list){
            record.isSeleted = false;
            record.isMultiMode = false;
        }
        notifyDataSetChanged();
    }

    protected class VH extends RecyclerView.ViewHolder{
        private SwipeMenuLayout swipeMenuLayout;
        private TextView nameTV;
        private TextView delTimeTV;
        private TextView resumeBT;
        private TextView clearBT;
        private TextView remainDayTV;
        private CheckBox checkBox;
        private LinearLayout group;
        public VH(@NonNull View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.record_name_TV);
            delTimeTV = itemView.findViewById(R.id.record_delTime_TV);
            remainDayTV = itemView.findViewById(R.id.remain_day_TV);
            resumeBT = itemView.findViewById(R.id.resume_button);
            clearBT = itemView.findViewById(R.id.clear_button);
            swipeMenuLayout = itemView.findViewById(R.id.swipemenu);
            checkBox = itemView.findViewById(R.id.checkbox);
            group = itemView.findViewById(R.id.contentGroup);
        }
    }
}
