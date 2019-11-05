package yzw.ahaqth.accountbag;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CloseableTextView extends android.support.v7.widget.AppCompatTextView {
    private Paint mPaint;
    private int padding = 30;
    private int closeButtonSize = 25;
    private int line1X,line1Y,line2X,line2Y;
    int left,right,top,bottom;
    public CloseableTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(getCurrentTextColor());
        mPaint.setStrokeWidth(4);
        mPaint.setStyle(Paint.Style.STROKE);
        left = getPaddingLeft() + padding;
        bottom = getPaddingBottom();
        right = getPaddingRight() + padding;
        top = getPaddingTop();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        line1X = getRight() - closeButtonSize - padding;
        line1Y = padding;
        line2X = line1X;
        line2Y = line1Y + closeButtonSize;
        setPadding(left,top,right,bottom);
        canvas.drawRect(line1X-10,line1Y-10,(line1X+closeButtonSize+10),(line1Y + closeButtonSize+10),mPaint);
        canvas.drawLine(line1X,line1Y,(line1X+closeButtonSize),(line1Y + closeButtonSize),mPaint);
        canvas.drawLine(line2X,line2Y,line2X + closeButtonSize,line2Y - closeButtonSize,mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (event.getX()>=line1X && event.getY()<=line1Y + closeButtonSize){
                    setVisibility(GONE);
                }
                break;
        }
        return true;
    }
}
