package yzw.ahaqth.accountbag.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import yzw.ahaqth.accountbag.R;
import yzw.ahaqth.accountbag.interfaces.OnGestureViewValidateListener;

public class GestureView extends RelativeLayout {
    private final int[] ids =
            {
                    1000, 309, 5, 1111, 353,
                    1010, 33983, 33291, 412, 1100,
                    410000, 59, 21111, 5789, 5092,
                    21231, 28931, 232, 209893, 31,
                    42, 4872, 5876132, 1110, 49801
            };
    private Handler handler;
    private GesturePoint[] gesturePoints;
    /*  mCount 每条边的个数 */
    private int mCount = 4;
    /*  存储答案 */
    private String mAnswer = "";
    /* 保存选中point的id */
    private List<Integer> mChoosedPoints = new ArrayList<>();
    private Paint mPaint;

    private int paddingBetweenPoints = 30;
    /**
     * GesturePoint的边长 4 * mWidth / ( 5 * mCount + 1 )
     */
    private int mGesturePointWidth;

    /**
     * GesturePoint无手指触摸的状态下的颜色
     */
    private int mColorNotTouched = 0xFF939090;
    /**
     * GesturePoint触摸状态下的颜色
     */
    private int mColorTouched = 0xFFE0DBDB;
    /**
     * GesturePoint验证成功的颜色
     */
    private int mColorSuccess = 0xFF378FC9;
    /**
     * GesturePoint验证失败的颜色
     */
    private int mColorFailed = 0xFFFF0000;

    private int mCurrentColor = mColorNotTouched;

    /**
     * 宽度
     */
    private int mWidth;
    /**
     * 高度
     */
    private int mHeight;

    private Path mPath;
    /* 指引线宽度 */
    private float mLineRatio = 0.15f;
    /**
     * 指引线的开始位置x
     */
    private int mLastPathX;
    /**
     * 指引线的开始位置y
     */
    private int mLastPathY;
    /**
     * 指引线的结束位置
     */
    private Point mTmpTarget = new Point();

    /**
     * 最大尝试次数
     */
    private int maxTimes = 4;

    private boolean flag = false;

    public void setValidateListener(OnGestureViewValidateListener validateListener) {
        this.mValidateListener = validateListener;
    }

    private OnGestureViewValidateListener mValidateListener;

    private void changePointState(GesturePoint.PointState state) {
        for (GesturePoint point : gesturePoints) {
            if (mChoosedPoints.contains(point.getId())) {
                point.setState(state);
                point.postInvalidate();
            }
        }
    }

    private void reset() {
        mChoosedPoints.clear();
        mPath.reset();
        mCurrentColor = mColorNotTouched;
        for (GesturePoint point : gesturePoints) {
            point.setState(GesturePoint.PointState.NOT_TOUCHED);
            point.setArrowDegree(-1);
            point.invalidate();
        }
    }

    private int[] answerToInt() {
        String[] strings = mAnswer.split(",");
        int[] result = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            String s = strings[i];
            if (!TextUtils.isEmpty(s) && TextUtils.isDigitsOnly(s))
                result[i] = Integer.valueOf(s);
        }
        return result;
    }

    private boolean checkAnswer() {
        int[] answer = answerToInt();
        if (answer.length != mChoosedPoints.size())
            return false;

        for (int i = 0; i < answer.length; i++) {
            if (answer[i] != mChoosedPoints.get(i))
                return false;
        }
        return true;
    }

    private boolean checkPositionInChild(View child, int x, int y) {
        //设置了内边距，即x,y必须落入下GesturePoint的内部中间的小区域中，可以通过调整padding使得x,y落入范围不变大，或者不设置padding
        int padding = (int) (mGesturePointWidth * 0.1);
        return x >= child.getLeft() + padding
                && x <= child.getRight() - padding
                && y >= child.getTop() + padding
                && y <= child.getBottom() - padding;
    }

    private GesturePoint getChildIdByPos(int x, int y) {
        for (GesturePoint point : gesturePoints) {
            if (checkPositionInChild(point, x, y)) {
                return point;
            }
        }
        return null;
    }

    public void setAnswer(String answer) {
        this.mAnswer = answer;
    }

    public String getAnswer() {
        if (mChoosedPoints.size() < 4) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mChoosedPoints.size(); i++) {
            builder.append(mChoosedPoints.get(i));
            if (i < mChoosedPoints.size() - 1) {
                builder.append(",");
            }
        }
        return builder.toString();
    }

    /**
     * 设置最大实验次数
     *
     * @param boundary
     */
    public void setUnMatchExceedBoundary(int boundary) {
        this.maxTimes = boundary;
    }

    public int getRemainTime() {
        return maxTimes;
    }

    public GestureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.GestureView, defStyleAttr, 0);
        int n = typedArray.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.GestureView_touched_color:
                    mColorTouched = typedArray.getColor(attr, mColorTouched);
                    break;
                case R.styleable.GestureView_not_touched_color:
                    mColorNotTouched = typedArray.getColor(attr, mColorNotTouched);
                    break;
                case R.styleable.GestureView_validate_success_color:
                    mColorSuccess = typedArray.getColor(attr, mColorSuccess);
                    break;
                case R.styleable.GestureView_validate_failed_color:
                    mColorFailed = typedArray.getColor(attr, mColorFailed);
                    break;
                case R.styleable.GestureView_count:
                    mCount = typedArray.getInt(attr, mCount);
                    if (mCount < 3)
                        mCount = 3;
                    if (mCount > 5)
                        mCount = 5;
                    break;
                case R.styleable.GestureView_max_time:
                    maxTimes = typedArray.getInt(attr, maxTimes);
                    break;
            }
        }
        typedArray.recycle();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPath = new Path();
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                reset();
                invalidate();
                flag = false;
                return true;
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = mWidth = MeasureSpec.getSize(widthMeasureSpec);

//        mHeight = MeasureSpec.getSize(heightMeasureSpec);
//        mHeight = mWidth = mWidth < mHeight ? mWidth : mHeight;
        if (gesturePoints == null) {
            gesturePoints = new GesturePoint[mCount * mCount];
            // 计算每个GesturePoint的宽度
            mGesturePointWidth = (int) (2 * mWidth / (3 * mCount + 1));
            //计算每个GesturePoint的间距
            paddingBetweenPoints = (int) (mGesturePointWidth * 0.5);
            // 设置画笔的宽度为GesturePoint的内圆直径稍微小点（不喜欢的话，随便设）
            mPaint.setStrokeWidth(mGesturePointWidth * mLineRatio);
            for (int i = 0; i < gesturePoints.length; i++) {
                gesturePoints[i] = new GesturePoint(getContext(), mColorNotTouched, mColorTouched, mColorFailed, mColorSuccess);
                gesturePoints[i].setId(ids[i]);
                //设置参数，主要是定位GestureLockView间的位置
                RelativeLayout.LayoutParams lockerParams = new RelativeLayout.LayoutParams(mGesturePointWidth, mGesturePointWidth);
                // 不是每行的第一个，则设置位置为前一个的右边
                if (i % mCount != 0) {
                    lockerParams.addRule(RelativeLayout.RIGHT_OF, gesturePoints[i - 1].getId());
                }
                // 从第二行开始，设置为上一行同一位置View的下面
                if (i > mCount - 1) {
                    lockerParams.addRule(RelativeLayout.BELOW, gesturePoints[i - mCount].getId());
                }
                //设置右下左上的边距
                int rightMargin = paddingBetweenPoints;
                int bottomMargin = paddingBetweenPoints;
                int leftMagin = 0;
                int topMargin = 0;

                /*
                 * 每个point都有右外边距和底外边距 第一行的有上外边距 第一列的有左外边距
                 */
                if (i < mCount)// 第一行
                {
                    topMargin = paddingBetweenPoints;
                }
                if (i % mCount == 0)// 第一列
                {
                    leftMagin = paddingBetweenPoints;
                }

                lockerParams.setMargins(leftMagin, topMargin, rightMargin, bottomMargin);
                gesturePoints[i].setState(GesturePoint.PointState.NOT_TOUCHED);
                addView(gesturePoints[i], lockerParams);
            }
        }
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        mPaint.setColor(mCurrentColor);
        mPaint.setAlpha(50);
        //绘制GesturePoint间的连线
        if (mPath != null) {
            canvas.drawPath(mPath, mPaint);
        }
        //绘制指引线
        if (mChoosedPoints.size() > 0) {
            if (mLastPathX != 0 && mLastPathY != 0)
                canvas.drawLine(mLastPathX, mLastPathY, mTmpTarget.x, mTmpTarget.y, mPaint);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (flag)
            return true;
        if (checkMaxTimes())
            return true;
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        // 指引线的终点
        mTmpTarget.x = x;
        mTmpTarget.y = y;
        fingerOnPoint(getChildIdByPos(x, y));
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 重置
                reset();
                if(mChoosedPoints.size() == 1) {
                    // 移到触摸的第一个点
                    mPath.moveTo(mLastPathX, mLastPathY);
                    mCurrentColor = mColorTouched;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(mChoosedPoints.size() == 1) {
                    // 移到触摸的第一个点
                    mPath.moveTo(mLastPathX, mLastPathY);
                    mCurrentColor = mColorTouched;
                }else if(mChoosedPoints.size() > 1){
                    // 非第一个，将两者使用线连上
                    mPath.lineTo(mLastPathX, mLastPathY);
                }
                break;
            case MotionEvent.ACTION_UP:
                fingerUp();
                break;
        }
        invalidate();
        return true;
    }

    private void fingerUp() {
        flag = true;
        if (mChoosedPoints.size() > 3) {
            maxTimes--;
            if (TextUtils.isEmpty(mAnswer) || checkAnswer()) {
                // 改变子元素的状态为UP
                mCurrentColor = mColorSuccess;
                changePointState(GesturePoint.PointState.VALIDATE_SUCCESS);
            } else {
                mCurrentColor = mColorFailed;
                changePointState(GesturePoint.PointState.VALIDATE_FAILED);
            }
        } else {
            mCurrentColor = mColorFailed;
            changePointState(GesturePoint.PointState.VALIDATE_FAILED);
        }
        // 将终点设置位置为起点，即取消指引线
        mTmpTarget.x = mLastPathX;
        mTmpTarget.y = mLastPathY;
        // 计算每个元素中箭头需要旋转的角度
        for (int i = 0; i + 1 < mChoosedPoints.size(); i++) {
            int childId = mChoosedPoints.get(i);
            int nextChildId = mChoosedPoints.get(i + 1);
            GesturePoint startChild = (GesturePoint) findViewById(childId);
            GesturePoint nextChild = (GesturePoint) findViewById(nextChildId);

            int dx = nextChild.getLeft() - startChild.getLeft();
            int dy = nextChild.getTop() - startChild.getTop();
            // 计算角度
            int angle = (int) Math.toDegrees(Math.atan2(dy, dx)) + 90;
            startChild.setArrowDegree(angle);
        }
        // 回调是否成功
        if (mValidateListener != null && mChoosedPoints.size() > 0) {
            mValidateListener.onGestureEvent(checkAnswer());
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(600);
                    handler.sendEmptyMessage(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void fingerOnPoint(GesturePoint touchedPoint) {
        if (touchedPoint != null) {
            int id = touchedPoint.getId();
            if (!mChoosedPoints.contains(id)) {
                mChoosedPoints.add(id);
                touchedPoint.setState(GesturePoint.PointState.TOUCHED);
                touchedPoint.invalidate();
                if (mValidateListener != null)
                    mValidateListener.onBlockSelected(id);
                // 设置指引线的起点
                mLastPathX = touchedPoint.getLeft() / 2 + touchedPoint.getRight() / 2;
                mLastPathY = touchedPoint.getTop() / 2 + touchedPoint.getBottom() / 2;
            }
            mTmpTarget.x = mLastPathX;
            mTmpTarget.y = mLastPathY;
        }
    }

    private boolean checkMaxTimes() {
        if (maxTimes < 1) {
            if (mValidateListener != null) {
                mValidateListener.onUnmatchedExceedBoundary();
            }
            flag = true;
            return true;
        }
        return false;
    }

}
