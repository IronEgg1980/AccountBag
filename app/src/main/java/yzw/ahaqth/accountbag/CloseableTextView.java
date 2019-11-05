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
    private int padding;
    private int closeButtonSize;
    private int line1X, line1Y, line2X, line2Y;
    int width;

    public CloseableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(getCurrentTextColor());
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.STROKE);
        int textSize = (int) getTextSize();
        if (textSize > 30)
            textSize = 30;
        if (textSize < 16)
            textSize = 16;
        closeButtonSize = textSize;
        padding = textSize / 2 + 5;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        line1X = width - closeButtonSize - padding;
        line1Y = padding;
        line2X = line1X;
        line2Y = line1Y + closeButtonSize;
        int i = padding + closeButtonSize;
        setPadding(padding, padding, i, padding);
        canvas.drawLine(line1X, line1Y, (line1X + closeButtonSize), (line1Y + closeButtonSize), mPaint);
        canvas.drawLine(line2X, line2Y, line2X + closeButtonSize, line2Y - closeButtonSize, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (event.getX() >= line1X && event.getY() <= line1Y + closeButtonSize) {
                    setVisibility(GONE);
                }
                break;
        }
        return true;
    }
}
