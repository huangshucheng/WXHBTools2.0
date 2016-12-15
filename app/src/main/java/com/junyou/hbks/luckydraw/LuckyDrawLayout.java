package com.junyou.hbks.luckydraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class LuckyDrawLayout extends View {
    private Context context;

    private Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint backgroundPaintbig = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint backgroundPaintsmall = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint yellowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int radius;
    private int CircleX,CircleY;
    private Canvas canvas;
    private boolean isYellow = false;
    private int delayTime = 500;

    public LuckyDrawLayout(Context context) {
        this(context,null);
    }

    public LuckyDrawLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LuckyDrawLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
//        backgroundPaint.setColor(Color.rgb(255,92,93));
        backgroundPaint.setColor(Color.rgb(204,39,38));
        backgroundPaintbig.setColor(Color.rgb(255,243,233));
        backgroundPaintsmall.setColor(Color.rgb(242,84,86));

        whitePaint.setColor(Color.WHITE);
        yellowPaint.setColor(Color.YELLOW);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if(widthSpecMode == MeasureSpec.AT_MOST  && heightSpecMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(200, 200);
        }else if(widthSpecMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(200, heightSpecSize);
        }else if(heightSpecMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(widthSpecSize, 200);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;

        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();

        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;

        int MinValue = Math.min(width,height);

        radius = MinValue /2;
        CircleX = getWidth() /2;
        CircleY = getHeight() /2;

        canvas.drawCircle(CircleX,CircleY,radius,backgroundPaintbig);//白边
        canvas.drawCircle(CircleX,CircleY,radius-8,backgroundPaint);    //红色底
        drawSmallCircle(isYellow);//小点点
        canvas.drawCircle(CircleX,CircleY,radius-45,backgroundPaintsmall);//最小
    }

    private void drawSmallCircle(boolean FirstYellow){
//        int pointDistance = radius - AngleUtil.dip2px(context,10);
        int pointDistance = radius - AngleUtil.dip2px(context,13);
        for(int i=0;i<=360;i+=20){
            int x = (int) (pointDistance * Math.sin(AngleUtil.change(i))) + CircleX;
            int y = (int) (pointDistance * Math.cos(AngleUtil.change(i))) + CircleY;

            if(FirstYellow)
                canvas.drawCircle(x,y,AngleUtil.dip2px(context,4),yellowPaint);
            else
                canvas.drawCircle(x,y,AngleUtil.dip2px(context,4),whitePaint);
            FirstYellow = !FirstYellow;
        }
    }

    public void startLuckLight(){
        postDelayed(new Runnable() {
            @Override
            public void run() {
                isYellow = !isYellow;
                invalidate();
                postDelayed(this,delayTime);
            }
        },delayTime);
    }

    public void setDelayTime(int delayTime){
        this.delayTime = delayTime;
    }
}
