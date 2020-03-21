package yzw.ahaqth.accountbag.inputoredit;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.List;

import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.interfaces.DialogDismissListener;
import yzw.ahaqth.accountbag.modules.ImageRecord;
import yzw.ahaqth.accountbag.operators.ImageOperator;
import yzw.ahaqth.accountbag.tools.DialogFactory;

public class EditImagRecordAdapter extends RecyclerView.Adapter<EditImagRecordAdapter.ImageRcordVH> {
    private List<ImageRecord> mList;
    private FragmentActivity mActivity;
//    private ItemClickListener<ImageRecord> clickListener;
//
//    public void setClickListener(ItemClickListener<ImageRecord> clickListener) {
//        this.clickListener = clickListener;
//    }

    public EditImagRecordAdapter(FragmentActivity activity, List<ImageRecord> list){
        mActivity = activity;
        mList = list;
    }
    @NonNull
    @Override
    public ImageRcordVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.edit_extra_image_item,viewGroup,false);
        return new EditImagRecordAdapter.ImageRcordVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageRcordVH imageRcordVH, int i) {
        final ImageRecord imageRecord = mList.get(i);
//        String imagePath = imageRecord.getPath() + File.separator + imageRecord.getImageFileName();
        Bitmap bitmap = ImageOperator.getRealImage(imageRecord);
        imageRcordVH.imageView.setImageBitmap(bitmap);
//        Glide.with(imageRcordVH.imageView).load(image).into(imageRcordVH.imageView);
        imageRcordVH.deleIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageRecord.isDeleted = !imageRecord.isDeleted;
                notifyItemChanged(imageRcordVH.getAdapterPosition());
            }
        });
        if(imageRecord.isDeleted){
            imageRcordVH.deleIB.setImageResource(R.drawable.undo_black_18dp);
            imageRcordVH.imageView.setAlpha(0.5f);
            imageRcordVH.removeIB.setVisibility(View.VISIBLE);
//            imageRcordVH.deletedIV.setVisibility(View.VISIBLE);
            imageRcordVH.removeIB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = imageRcordVH.getAdapterPosition();
                    DialogFactory.getConfirmDialog("是否立即移除该项？")
                            .setDismissListener(new DialogDismissListener() {
                                @Override
                                public void onDismiss(boolean isConfirm, Object... valus) {
                                    if(isConfirm){
                                        ImageOperator.deleImageFile(imageRecord);
                                        imageRecord.delete();
                                        mList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position,getItemCount() - position);
                                    }
                                }
                            })
                            .show(mActivity.getSupportFragmentManager(),"confirmRemove");
                }
            });
        }else{
            imageRcordVH.deleIB.setImageResource(R.drawable.clear_black_18dp);
            imageRcordVH.imageView.setAlpha(1.0f);
            imageRcordVH.removeIB.setVisibility(View.GONE);
//            imageRcordVH.deletedIV.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    protected class ImageRcordVH extends RecyclerView.ViewHolder{
        ImageView imageView;
        ImageButton deleIB;
//        ImageView deletedIV;
        ImageButton removeIB;
        public ImageRcordVH(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.extra_image_IV);
            deleIB = itemView.findViewById(R.id.dele);
            removeIB = itemView.findViewById(R.id.remove);
//            deletedIV = itemView.findViewById(R.id.deleted_flag_IV);
        }
    }
}
