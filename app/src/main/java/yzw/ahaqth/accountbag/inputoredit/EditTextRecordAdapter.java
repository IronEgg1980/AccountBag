package yzw.ahaqth.accountbag.inputoredit;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.interfaces.ItemClickListener;
import yzw.ahaqth.accountbag.modules.TextRecord;
import yzw.ahaqth.accountbag.tools.DialogFactory;

public class EditTextRecordAdapter extends RecyclerView.Adapter<EditTextRecordAdapter.TextRecordVH> {
    private List<TextRecord> mList;
    public void setEditClickListener(ItemClickListener editClickListener) {
        this.editClickListener = editClickListener;
    }
    private ItemClickListener editClickListener;
    public EditTextRecordAdapter(List<TextRecord> list){
        this.mList = list;
    }
    @NonNull
    @Override
    public TextRecordVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.edit_extra_text_item,viewGroup,false);
        return new TextRecordVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TextRecordVH textRecordVH, int i) {
        final TextRecord record = mList.get(i);
        textRecordVH.extraTextKeyTV.setText(record.getKey());
        textRecordVH.extraTextKeyTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = textRecordVH.getAdapterPosition();
                new DialogFactory(textRecordVH.extraTextKeyTV.getContext()).showDefaultConfirmDialog("是否立即移除该项？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        record.delete();
                        mList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,getItemCount() - position);
                    }
                });
            }
        });
        textRecordVH.extraTextContentTV.setText(record.getContent());
        textRecordVH.extraTextContentTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editClickListener.click(textRecordVH.getAdapterPosition());
            }
        });
//        textRecordVH.deleIB.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                record.isDeleted = !record.isDeleted;
//                notifyItemChanged(textRecordVH.getAdapterPosition());
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    protected class TextRecordVH extends RecyclerView.ViewHolder{
        private TextView extraTextKeyTV;
        private AppCompatTextView extraTextContentTV;
        public TextRecordVH(@NonNull View itemView) {
            super(itemView);
            extraTextKeyTV = itemView.findViewById(R.id.extra_text_key_TV);
            extraTextContentTV = itemView.findViewById(R.id.extra_text_content_TV);
        }
    }
}
