package yzw.ahaqth.accountbag;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class EmptyVH extends RecyclerView.ViewHolder {
    public ImageView imageView;
    public TextView textView;
    public EmptyVH(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.empty_imageview);
        textView = itemView.findViewById(R.id.empty_textview);
    }
}
