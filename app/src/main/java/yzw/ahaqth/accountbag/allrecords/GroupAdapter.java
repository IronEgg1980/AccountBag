package yzw.ahaqth.accountbag.allrecords;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.interfaces.ItemClickListener;
import yzw.ahaqth.accountbag.modules.RecordGroup;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ItemVH> {
    private List<RecordGroup> mList;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private ItemClickListener itemClickListener;
    public GroupAdapter(List<RecordGroup> list){
        this.mList = list;
    }
    @NonNull
    @Override
    public ItemVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.group_item,viewGroup,false);
        return new ItemVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemVH itemVH, int i) {
        RecordGroup recordGroup = mList.get(i);
        itemVH.textView.setText(recordGroup.getGroupName());
        itemVH.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.click(itemVH.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    protected class ItemVH extends RecyclerView.ViewHolder{
        TextView textView;
        public ItemVH(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textview1);
        }
    }
}
