package yzw.ahaqth.accountbag.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerPointIndicator extends View {
    protected class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    private List<Point> points;
    private int pointsTotalWidth;
    private int pointRadius = 10;
    private int pointMargin = pointRadius * 2 + 20;
    private int paintStrokeWidth = 2;
    private Paint normalPaint, currentPaint;
    private ViewPager viewPager;
    private boolean isScroll;
    private int count;
    private int mWidth, mHeight, x0, y0, currentX;

    public ViewPagerPointIndicator setPointColor(int color){
        normalPaint.setColor(color);
        currentPaint.setColor(color);
        invalidate();
        return this;
    }

    public ViewPagerPointIndicator setViewPager(final ViewPager viewPager) {
        if (viewPager == null)
            throw new RuntimeException("The ViewPager is NULL !");
        if (viewPager.getAdapter() == null)
            throw new RuntimeException("The PagerAdapter is NULL ! Call ViewPager.setAdapter() first !");
        this.viewPager = viewPager;
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                if (isScroll) {
                    currentX = (int) (points.get(i).x + pointMargin * v);
                    invalidate();
                }
            }

            @Override
            public void onPageSelected(int i) {
                if (i < 0 || i > points.size() - 1) {
                    return;
                }
                currentX = points.get(i).x;
                invalidate();
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                if (i == 1) {
                    isScroll = true;
                } else {
                    isScroll = false;
                    currentX = points.get(viewPager.getCurrentItem()).x;
                }
                invalidate();
            }
        });
        count = viewPager.getAdapter().getCount();
        this.pointsTotalWidth = count * (pointMargin + pointRadius * 2) - pointMargin;
        return this;
    }

    public ViewPagerPointIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        normalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        normalPaint.setColor(Color.parseColor("#ffffff"));
        normalPaint.setStrokeWidth(paintStrokeWidth);
        normalPaint.setStyle(Paint.Style.STROKE);

        currentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        currentPaint.setColor(Color.parseColor("#ffffff"));
        currentPaint.setStrokeWidth(paintStrokeWidth);
        currentPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.UNSPECIFIED || widthMode == MeasureSpec.AT_MOST) {
            mWidth = mWidth > pointsTotalWidth ? mWidth : pointsTotalWidth;
        }
        if (heightMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.AT_MOST) {
            int miniHeight = pointRadius *4;
            mHeight = mHeight > miniHeight ? mHeight : miniHeight;
        }

        x0 = (mWidth - pointsTotalWidth) / 2 + pointRadius;
        y0 = mHeight / 2;
        createPoint();
        currentX = points.get(viewPager.getCurrentItem()).x;
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(points !=null) {
            for (int i = 0; i < points.size(); i++) {
                Point p = points.get(i);
                canvas.drawCircle(p.x, p.y, pointRadius, normalPaint);
            }
            canvas.drawCircle(currentX, y0, pointRadius, currentPaint);
        }
    }

    private void createPoint() {
        if (points == null)
            points = new ArrayList<>();
        points.clear();
        for (int i = 0; i < count; i++) {
            int x = x0 + (pointMargin + 2 * pointRadius) * i;
            points.add(new Point(x, y0));
        }
    }
}
