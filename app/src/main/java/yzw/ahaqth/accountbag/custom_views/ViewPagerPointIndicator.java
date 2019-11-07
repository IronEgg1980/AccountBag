package yzw.ahaqth.accountbag.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

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
    private int pointsTotalWidth, mWidth, mHeight;
    private int pointRadius = 10;
    private int pointMargin = pointRadius * 2 + 20;
    private int paintStrokeWidth = 2;
    private Paint pointPaint, bgPaint;
    private ViewPager viewPager;
    private boolean isScroll;
    private int count;
    private int x0;
    private int y0;
    private int currentX;
    private int bgX1, bgY1, bgX2, bgY2;

    public ViewPagerPointIndicator setPointColor(int color) {
        pointPaint.setColor(color);
//        currentPaint.setColor(color);
        invalidate();
        return this;
    }

    public ViewPagerPointIndicator setBgColor(int color) {
        bgPaint.setColor(color);
        invalidate();
        return this;
    }

    public void attachToViewPager(final ViewPager viewPager) {
        if (viewPager == null)
            throw new RuntimeException("The ViewPager is NULL !");
        if (viewPager.getAdapter() == null)
            throw new RuntimeException("The PagerAdapter is NULL ! Call ViewPager.setAdapter() first !");
        this.viewPager = viewPager;
        setVisibility(VISIBLE);
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
        createPoint();
    }

    public ViewPagerPointIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setVisibility(GONE);

        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setColor(Color.parseColor("#ffffff"));
        pointPaint.setStrokeWidth(paintStrokeWidth);
        pointPaint.setStyle(Paint.Style.STROKE);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(Color.parseColor("#38ffffff"));
        bgPaint.setStrokeWidth(paintStrokeWidth);
        bgPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);

        if (widthMode == MeasureSpec.UNSPECIFIED || widthMode == MeasureSpec.AT_MOST) {
            mWidth = mWidth > pointsTotalWidth ? mWidth : pointsTotalWidth;
        }
        mHeight = pointRadius * 4;
        createPoint();
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(bgX1, bgY1, bgX2, bgY2, pointMargin / 2, pointMargin / 2, bgPaint);
        if (points != null) {
            pointPaint.setStyle(Paint.Style.STROKE);
            for (int i = 0; i < points.size(); i++) {
                Point p = points.get(i);
                canvas.drawCircle(p.x, p.y, pointRadius, pointPaint);
            }
            pointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawCircle(currentX, y0, pointRadius, pointPaint);
        }
    }

    private void createPoint() {
        bgX1 = (mWidth - pointsTotalWidth) / 2 - pointMargin / 2;
        bgY1 = 0;
        bgX2 = bgX1 + pointsTotalWidth + pointMargin;
        bgY2 = mHeight;

        x0 = (mWidth - pointsTotalWidth) / 2 + pointRadius;
        y0 = mHeight / 2;

        if (points == null)
            points = new ArrayList<>();
        points.clear();
        for (int i = 0; i < count; i++) {
            int x = x0 + (pointMargin + 2 * pointRadius) * i;
            points.add(new Point(x, y0));
            if(i == viewPager.getCurrentItem())
                currentX = x;
        }
    }
}
