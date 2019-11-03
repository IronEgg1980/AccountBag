package yzw.ahaqth.accountbag.allrecords;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.interfaces.ItemClickListener;
import yzw.ahaqth.accountbag.modules.AccountRecord;
import yzw.ahaqth.accountbag.operators.RecordOperator;
import yzw.ahaqth.accountbag.tools.DialogFactory;
import yzw.ahaqth.accountbag.tools.ToastFactory;
import yzw.ahaqth.accountbag.tools.ToolUtils;

public class DeleResumeAdapter extends RecyclerView.Adapter<DeleResumeAdapter.VH> {
    private List<AccountRecord> list;
    private Context mContext;
    private ToastFactory toastFactory;
    private SimpleDateFormat simpleDateFormat;
    private ItemClickListener longClick;

    public void setLongClick(ItemClickListener longClick) {
        this.longClick = longClick;
    }

    public DeleResumeAdapter(Context context){
        this.list = RecordOperator.findAllDeleted();
        this.mContext = context;
        simpleDateFormat = new SimpleDateFormat("删除时间：yyyy年M月d日 HH:mm:ss", Locale.CHINA);
        toastFactory = new ToastFactory(mContext);
    }
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dele_record_item,viewGroup,false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VH vh, int i) {
        final int position = vh.getAdapterPosition();
        final AccountRecord accountRecord = list.get(position);
        vh.nameTV.setText(accountRecord.getRecordName());
        long deleTime = accountRecord.getDeleTime();
        int remainDay = 30 - (int) ((System.currentTimeMillis() - deleTime) / ToolUtils.ONE_DAY_MILLES);
        String s = remainDay+"天后自动清除";
        vh.delTimeTV.setText(simpleDateFormat.format(deleTime));
        vh.remainDayTV.setText(s);
        if(accountRecord.isMultiMode){
            vh.checkBox.setVisibility(View.VISIBLE);
            vh.checkBox.setChecked(accountRecord.isSeleted);
            vh.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    accountRecord.isSeleted = !accountRecord.isSeleted;
                    notifyItemChanged(position);
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
                    longClick.click(position);
                    return true;
                }
            });
            vh.resumeBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecordOperator.resumeOne(accountRecord);
                    list.remove(position);
                    notifyItemRemoved(position);
                    toastFactory.showCenterToast("恢复操作成功");
                }
            });
            vh.clearBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DialogFactory(mContext).showWarningDialog("删除确认", "是否彻底清除该项目？\n注意：清除后无法恢复！",
                            "清除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    RecordOperator.clear(list.get(position));
                                    list.remove(position);
                                    notifyItemRemoved(position);
                                    toastFactory.showCenterToast("已清除该项");
                                }
                            },
                            "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    vh.swipeMenuLayout.smoothClose();
                                }
                            });
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
