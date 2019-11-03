package yzw.ahaqth.accountbag.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class GesturePoint extends View {
    /*
     * 点的三种状态
     */
    enum PointState {
        NOT_TOUCHED,
        TOUCHED,
        VALIDATE_FAILED,
        VALIDATE_SUCCESS
    }

    /*
     * 当前点所处的状态
     */
    private PointState mCurrentState = PointState.NOT_TOUCHED;
    private int mWidth;
    private int mHeight;

    /*圆点与外层圆环参数*/
    /*
     * mRadio 圆环半径
     * mInnerRadio 圆点半径
     * mCenterX 与 mCenterY 圆心的坐标
     *
     * */
    private int mRadio;
    private int mInnerRadio;
    private int mCenterX;
    private int mCenterY;

    /*三角形参数*/
    /* mArrowRate （小三角最长边的一半长度 = mArrawRate * mWidth / 2 ）
     *  mArrowDegree 旋转的角度
     *  mArrowPath 画三角形的路径
     */
    private float mArrowRate = 0.25f;
    private int mArrowDegree = -1;
    private Path mArrowPath;

    /**
     * 四个颜色，可由用户自定义，初始化时由GestureView传入
     */
    private int mColorNotTouched;
    private int mColorTouched;
    private int mColorFailed;
    private int mColorSuccess;

    private Paint mPaint;
    private int mStrokeWidth = 4;


    public GesturePoint(Context context, int colorNotTouched, int colorTouched, int colorFailed, int colorSuccess) {
        super(context);
        mColorNotTouched = colorNotTouched;
        mColorTouched = colorTouched;
        mColorFailed = colorFailed;
        mColorSuccess = colorSuccess;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArrowPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        // 取长和宽中的短边
        mWidth = mWidth > mHeight ? mHeight : mWidth;
        mRadio = mCenterX = mCenterY = mWidth / 2;
        mRadio -= mStrokeWidth / 2;
        mInnerRadio =(int)(mRadio * 0.33);
        // 绘制三角形，初始时是个默认箭头朝上的一个等腰三角形，用户绘制结束后，根据两个GeasurePoint决定需要旋转多少度
        float mArrowLenth = mWidth / 2f * mArrowRate;
        int x = mWidth / 2;
        int y = mStrokeWidth +(int)mArrowLenth;
        mArrowPath.moveTo(x,y);
        mArrowPath.lineTo(x - mArrowLenth,y + mArrowLenth);
        mArrowPath.lineTo(x + mArrowLenth,y + mArrowLenth);
        mArrowPath.close();
        mArrowPath.setFillType(Path.FillType.WINDING);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mCurrentState){
            case TOUCHED:
                drawPoint(canvas,mColorTouched);
                break;
            case VALIDATE_SUCCESS:
                drawPoint(canvas,mColorSuccess);
                drawArrow(canvas);
                break;
            case VALIDATE_FAILED:
                drawPoint(canvas,mColorFailed);
                drawArrow(canvas);
                break;
            default:
                drawPoint(canvas,mColorNotTouched);
                break;
        }
    }

    /* 绘制箭头 */
    private void drawArrow(Canvas canvas){
        if(mArrowDegree != -1){
            mPaint.setStyle(Paint.Style.FILL);
            canvas.save();
            canvas.rotate(mArrowDegree,mCenterX,mCenterY);
            canvas.drawPath(mArrowPath,mPaint);
            canvas.restore();
        }
    }

    /* 绘制图案 */
    private void drawPoint(Canvas canvas,int color){
        // 绘制外环
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(color);
        canvas.drawCircle(mCenterX,mCenterY,mRadio,mPaint);

        if(mCurrentState != PointState.NOT_TOUCHED) {
            // 绘制内部原点
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(mCenterX, mCenterY, mInnerRadio, mPaint);
        }
    }

    public void setState(PointState state){
        mCurrentState = state;
//        invalidate();
    }

    public void setArrowDegree(int degree){
        mArrowDegree = degree;
    }

    public int getArrowDegree(){
        return mArrowDegree;
    }
}
