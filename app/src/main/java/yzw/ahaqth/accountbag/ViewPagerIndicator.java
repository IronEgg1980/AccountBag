package yzw.ahaqth.accountbag;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ViewPagerIndicator extends LinearLayout {
    public void setViewPager(ViewPager viewPager) {
        if(viewPager == null)
            throw new RuntimeException("ViewPager is NULL !");
        this.viewPager = viewPager;
        initalPoints();
        changePoint(viewPager.getCurrentItem());
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                changePoint(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        invalidate();
    }

    private void initalPoints(){
        removeAllViews();
        for(int i = 0;i<viewPager.getAdapter().getCount();i++){
            ImageView imageView = new ImageView(getContext());
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(layoutParams);
            layoutParams.setMargins(10,10,10,10);
            imageView.setImageResource(R.drawable.point_normal);
            addView(imageView);
        }
    }
    private void changePoint(int position){
        for(int i = 0;i<getChildCount();i++){
            ImageView imageView = (ImageView) getChildAt(i);
            if(position == i){
                imageView.setImageResource(R.drawable.point_current);
            }else{
                imageView.setImageResource(R.drawable.point_normal);
            }
        }
    }
    private ViewPager viewPager;
    public ViewPagerIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
    }
}
