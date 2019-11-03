package yzw.ahaqth.accountbag.details;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.modules.TextRecord;
import yzw.ahaqth.accountbag.tools.ToastFactory;
import yzw.ahaqth.accountbag.tools.ToolUtils;

public class TextRecordAdapter extends RecyclerView.Adapter<TextRecordAdapter.TextRecordVH> {
    private List<TextRecord> mList;

    public TextRecordAdapter(List<TextRecord> list){
        this.mList = list;
    }
    @NonNull
    @Override
    public TextRecordVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.extra_text_item,viewGroup,false);
        return new TextRecordVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TextRecordVH textRecordVH, int i) {
        final TextRecord record = mList.get(i);
        textRecordVH.extraTextKeyTV.setText(record.getKey());
        textRecordVH.extraTextContentTV.setText(record.getContent());
        textRecordVH.copyIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolUtils.copy(v.getContext(),record.getContent());
                new ToastFactory(v.getContext()).showCenterToast("已复制");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    protected class TextRecordVH extends RecyclerView.ViewHolder{
        private TextView extraTextKeyTV;
        private TextView extraTextContentTV;
        private ImageButton copyIB;

        public TextRecordVH(@NonNull View itemView) {
            super(itemView);
            extraTextKeyTV = itemView.findViewById(R.id.extra_text_key_TV);
            extraTextContentTV = itemView.findViewById(R.id.extra_text_content_TV);
            copyIB = itemView.findViewById(R.id.copy);
        }
    }
}
