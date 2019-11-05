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
    private int padding = 40;
    private int closeButtonSize = 25;
    private int buttonX1,buttonY1;
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
        buttonX1 = getRight() - padding - closeButtonSize;
        buttonY1 = padding / 2;
        setPadding(left,top,right,bottom);
        canvas.drawRect(buttonX1-5,buttonY1-5,(buttonX1+closeButtonSize+5),(buttonY1 + closeButtonSize+5),mPaint);
        canvas.drawLine(buttonX1,buttonY1,(buttonX1+closeButtonSize),(buttonY1 + closeButtonSize),mPaint);
        canvas.drawLine(buttonX1,(buttonY1+closeButtonSize),(buttonX1+closeButtonSize),buttonY1,mPaint);
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
                if (event.getX()>=buttonX1 && event.getY()<=buttonY1 + closeButtonSize){
                    setVisibility(GONE);
                }
                break;
        }
        return true;
    }
}
