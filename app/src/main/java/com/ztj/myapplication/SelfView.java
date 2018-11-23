package com.ztj.myapplication;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by zhoutianjie on 2018/11/22.
 */

public class SelfView extends View {

    private Paint mPaint;
    private Path mBackPath;//背景path

    private Path mMovingPath;

    private Bitmap circleStart;//初始小圆圈

    private float pointx = 200f;//控件初始位置，后面调整
    private float pointy = 100f;

    private float moveingX = 0;
    private float moveingY = pointy;

    private int rate = 4;//匡高倍数

    private float circleWidth = 0;
    private float circleHeight = 0;

    private int radiusx = 0;
    private int radiusy = 0;

    private boolean flag = false;//是否处于置灰状态

    private String toast = "删除全部白板";



    public SelfView(Context context) {
        super(context);
        init();
    }

    public SelfView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SelfView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setLayerType(LAYER_TYPE_SOFTWARE,null);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        circleStart = BitmapFactory.decodeResource(getResources(),R.mipmap.startcircle);
        circleWidth = circleStart.getWidth();
        circleHeight = circleStart.getHeight();

        radiusx = (int) (circleWidth/2);
        radiusy = (int) (circleHeight/2);

        mBackPath = new Path();
        mBackPath.moveTo(pointx+radiusx,pointy);
        mBackPath.lineTo(pointx+radiusx+(rate-1)*circleWidth,pointy);

        RectF rectF = new RectF(pointx+(rate-1)*circleWidth,pointy,pointx+rate*circleWidth,pointy+circleHeight);
        mBackPath.arcTo(rectF,-90,180);
        mBackPath.lineTo(pointx+radiusx,pointy+circleHeight);

        RectF rectF1 = new RectF(pointx,pointy,pointx+circleWidth,pointy+circleHeight);
        mBackPath.arcTo(rectF1,90,180);

        mBackPath.close();

        mMovingPath =new Path();


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //限定手指的滑动范围
        float x = event.getX();
        float y = event.getY();
        //手指不在操作区域 或者处于置灰状态不能滑动
//        if(!isInOperateRegion(x,y) || flag){
//            return super.onTouchEvent(event);
//        }
        if(flag){
            return super.onTouchEvent(event);
        }


        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                circleStart = BitmapFactory.decodeResource(getResources(),R.mipmap.select);
                invalidate();
                return true;

            case MotionEvent.ACTION_MOVE:
                if(x<pointx+rate*circleWidth-radiusx && x>pointx+radiusx){
                    moveingX = x-pointx-radiusx;
                    Log.e("TAFG","movingx"+moveingX);
                    invalidate();
                }

                break;

            case MotionEvent.ACTION_UP:
                //回弹或者置灰，回弹用动画实现，插值器用加速插值器
                //在操作区域里面
                if(x<pointx+rate*circleWidth-radiusx && x>pointx+radiusx){
                    final float a = x-pointx-radiusx;
                    final float b = (rate-1)*circleWidth;

                    if(a>9.0f*(rate-1)*circleWidth/10){
                        Log.e("TTT",""+a);
                        //直接滑动到终点
                        ValueAnimator animator = ValueAnimator.ofFloat(0f,1f);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float curValue = (float) animation.getAnimatedValue();
                                moveingX = a+curValue*(b-a);
                                invalidate();
                            }
                        });
                        animator.setDuration(200);
                        animator.setInterpolator(new AccelerateInterpolator());
                        animator.start();

                        animator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                //走动画的处理
                                    moveingX = 0;
                                    flag = true;
                                    invalidate();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });

                    }else {
                        //直接回到原点
                        Log.e("TTTT",""+a);
                        ValueAnimator animator = ValueAnimator.ofFloat(1f,0f);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float curValue = (float) animation.getAnimatedValue();
                                moveingX = a*curValue;
                                invalidate();
                            }
                        });
                        animator.setDuration(600);
                        animator.setInterpolator(new AccelerateInterpolator());
                        animator.start();
                        animator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                circleStart = BitmapFactory.decodeResource(getResources(),R.mipmap.startcircle);
                                invalidate();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                    }
                }

                //不在操作区域里面,右划直接滑动到终点
                //左划直接互动到原点
                //也加一个动画实现
                //可以思考一下 只通过action move 如何实现
               else if(x>=pointx+rate*circleWidth-radiusx ){
                    moveingX = (rate-1)*circleWidth;
                    invalidate();

                    moveingX = 0;
                    flag = true;
                    invalidate();


                }

                else {
                   moveingX = 0;
                   invalidate();
                }


                break;
                default:
                    break;
        }

        return super.onTouchEvent(event);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
//        pointx = getWidth()-circleWidth/2;
//        pointy = getHeight() - circleHeight/2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e("TAG","ok");

        //画边框
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        mPaint.setColor(Color.WHITE);
        canvas.drawPath(mBackPath,mPaint);

        //画初始背景
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#FF494949"));
        canvas.drawPath(mBackPath,mPaint);


        //画提示语
        mPaint.setTextSize(18);
        if(!flag){
            mPaint.setColor(Color.WHITE);
        }else {
            mPaint.setColor(Color.parseColor("#6B6B6B"));
        }

        canvas.drawText(toast,pointx+1.2f*circleWidth,pointy+1.2f*circleHeight/2,mPaint);

        mPaint.setColor(Color.parseColor("#FF008CCD"));
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(getMovingPath(moveingX),mPaint);

        canvas.drawBitmap(circleStart,pointx+moveingX,pointy,mPaint);//只改变pointx
    }

    //是否在操作范围内
    private boolean isInOperateRegion(float x,float y){
        if(y>=pointy-2*circleHeight && y<=pointy+3*circleHeight){
            Log.e("TAG","ok1");
            return true;
        }else {
            Log.e("TAG","ok2");
            return false;
        }
    }


    private Path getMovingPath(float moveingX){
        mMovingPath.reset();
        if(!flag){
            mMovingPath.moveTo(pointx+radiusx,pointy);
            mMovingPath.lineTo(pointx+radiusx+moveingX,pointy);
            RectF rectF2 = new RectF(pointx+moveingX,pointy,pointx+circleWidth+moveingX,pointy+circleHeight);
            mMovingPath.arcTo(rectF2,-90,180);

            RectF rectF3 = new RectF(pointx,pointy,pointx+circleWidth,pointy+circleHeight);
            mMovingPath.arcTo(rectF3,90,180);
            mMovingPath.close();
        }else {
            circleStart = BitmapFactory.decodeResource(getResources(),R.mipmap.disable);
        }

        return mMovingPath;
    }


    public void reset(){
        flag = false;
        circleStart = BitmapFactory.decodeResource(getResources(),R.mipmap.startcircle);
        invalidate();
    }

}
