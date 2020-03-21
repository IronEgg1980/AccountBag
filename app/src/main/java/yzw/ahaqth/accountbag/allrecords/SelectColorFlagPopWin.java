package yzw.ahaqth.accountbag.allrecords;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.interfaces.ItemClickListener;
import yzw.ahaqth.accountbag.tools.ToolUtils;

public class SelectColorFlagPopWin extends PopupWindow {
    private List<ImageView> imageViewList;
    private List<Drawable> drawables;
    private String[] colors = new String[8];
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public SelectColorFlagPopWin(Context context){
        View contentView = LayoutInflater.from(context).inflate(R.layout.group_colorflag_popwin_layout,null);
        imageViewList = new ArrayList<>();
        drawables = new ArrayList<>();
        for(int i = 0;i<8;i++){
            colors[i] = ToolUtils.getRandomColor(0xFF);
            int[] imageId = {R.id.imageView1, R.id.imageView2, R.id.imageView3, R.id.imageView4,
                    R.id.imageView5, R.id.imageView6, R.id.imageView7, R.id.imageView8};
            ImageView imageView = contentView.findViewById(imageId[i]);
            imageViewList.add(imageView);
            Drawable origin = context.getDrawable(R.drawable.ic_class_24dp);
            Drawable drawable = origin.mutate();
            drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
            drawable.setColorFilter(Color.parseColor(colors[i]), PorterDuff.Mode.MULTIPLY);
            drawables.add(drawable);
        }
        for(int i = 0;i<8;i++){
            final String color = colors[i];
            imageViewList.get(i).setImageDrawable(drawables.get(i));
            imageViewList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.click(-1,color);
                    dismiss();
                }
            });
        }
        contentView.findViewById(R.id.textview1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.click(R.id.textview1,"custom");
                dismiss();
            }
        });
        setContentView(contentView);
        setOutsideTouchable(true);
        setFocusable(true);
        setTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setWidth(600);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
