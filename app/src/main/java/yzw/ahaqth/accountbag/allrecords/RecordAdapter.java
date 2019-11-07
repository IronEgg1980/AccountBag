package yzw.ahaqth.accountbag.allrecords;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
                delClick.click(recordVH.getAdapterPosition(),recordVH);
            }
        });
        if(record.getSortIndex() > 1){
            recordVH.favorite.setVisibility(View.VISIBLE);
            ((TextView)recordVH.favoriteButton).setText("取消收藏");
            recordVH.favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    record.setSortIndex(1);
                    record.save();
                    notifyItemChanged(recordVH.getAdapterPosition());
                }
            });
        }else{
            recordVH.favorite.setVisibility(View.INVISIBLE);
            ((TextView)recordVH.favoriteButton).setText("加入收藏");
            recordVH.favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    record.setSortIndex(100);
                    record.save();
                    notifyItemChanged(recordVH.getAdapterPosition());
                }
            });
        }
        recordVH.recordNameTV.setText(record.getRecordName());
        recordVH.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.click(recordVH.getAdapterPosition(), recordVH.recordNameTV, recordVH.root);
            }
        });
        recordVH.textExtraTV.setVisibility(View.VISIBLE);
        String textcount = "\t" + record.getTotalTextCount();
        recordVH.textExtraTV.setText(textcount);
        recordVH.imageExtraTV.setVisibility(View.VISIBLE);
        String s2 = "\t" + record.getExtraImageCount();
        recordVH.imageExtraTV.setText(s2);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    protected class RecordVH extends RecyclerView.ViewHolder {
        private ImageView favorite;
        private TextView recordNameTV;
        private LinearLayout root;
        private TextView imageExtraTV;
        private TextView textExtraTV;
        private View delButton,favoriteButton;
        private SwipeMenuLayout swipeMenuLayout;
        public RecordVH(@NonNull View itemView) {
            super(itemView);
            favorite = itemView.findViewById(R.id.favoriteIV);
            root = itemView.findViewById(R.id.root);
            recordNameTV = itemView.findViewById(R.id.record_time_TV);
            imageExtraTV = itemView.findViewById(R.id.image_extra_TV);
            textExtraTV = itemView.findViewById(R.id.text_extra_TV);
            delButton = itemView.findViewById(R.id.delbutton);
            favoriteButton = itemView.findViewById(R.id.favorite_BT);
            swipeMenuLayout = itemView.findViewById(R.id.swipemenu);
        }

        public void closeMenu(){
            swipeMenuLayout.smoothClose();
        }
    }
}
