package com.macary.promodoro;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class CircleTimer extends View {

    private int layoutHeight = 0;
    private int layoutWidth = 0;
    private int fullRadius = 100;
    private int circleRadius = 80;
    private int textSize = 18;
    private int barLength = 0;
    private int barWith = 20;
    private int rimWith = 20;
    private int contourSize = 0;

    private int barColor = 0xAA000000;
    private int contourColor = 0x000000;
    private int circleColor = 0xff000000;
    private int rimColor = 0xAADDDDDD;
    private int textColor = 0x00000000;

    private int paddingTop = 5;
    private int paddingBottom = 5;
    private int paddingLeft = 5;
    private int paddingRight = 5;

    private boolean isRunning = false;

    private String text = "";

    private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint rimPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint contourPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private RectF innerCircleBounds = new RectF();
    private RectF circleBounds = new RectF();
    private RectF circleOuterContour = new RectF();
    private RectF circleInnerContour = new RectF();

    public CircleTimer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleTimer, 0, 0);

        try {
            barWith = (int) typedArray.getDimension(R.styleable.CircleTimer_barWidth, barWith);
            rimWith = (int) typedArray.getDimension(R.styleable.CircleTimer_rimWidth, rimWith);
            barLength = (int) typedArray.getDimension(R.styleable.CircleTimer_barLength, barLength);
            textSize = (int) typedArray.getDimension(R.styleable.CircleTimer_textSize, textSize);

            barColor = typedArray.getColor(R.styleable.CircleTimer_barColor, barColor);
            rimColor = typedArray.getColor(R.styleable.CircleTimer_rimColor, rimColor);
            contourColor = typedArray.getColor(R.styleable.CircleTimer_contourColor, contourColor);
            circleColor = typedArray.getColor(R.styleable.CircleTimer_circleColor, circleColor);
            textColor = typedArray.getColor(R.styleable.CircleTimer_textColor, textColor);

            if (typedArray.hasValue(R.styleable.CircleTimer_text)) {
                text = typedArray.getString(R.styleable.CircleTimer_text);
            }
        } finally {
            typedArray.recycle();
        }
    }

    private void setupPaints() {
        barPaint.setColor(barColor);
        barPaint.setStyle(Paint.Style.STROKE);
        barPaint.setStrokeWidth(barWith);

        rimPaint.setColor(rimColor);
        rimPaint.setStyle(Paint.Style.STROKE);
        rimPaint.setStrokeWidth(rimWith);

        circlePaint.setColor(circleColor);
        circlePaint.setStyle(Paint.Style.FILL);

        textPaint.setColor(textColor);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(textSize);

        contourPaint.setColor(contourColor);
        contourPaint.setStyle(Paint.Style.STROKE);
        contourPaint.setStrokeWidth(contourSize);
    }

    private void setupBounds() {
        int minValue = Math.min(layoutWidth, layoutHeight);

        int xOffset = layoutWidth - minValue;
        int yOffset = layoutHeight - minValue;

        paddingTop = getPaddingTop() + (yOffset / 2);
        paddingBottom = getPaddingBottom() + (yOffset / 2);
        paddingLeft = getPaddingLeft() + (xOffset / 2);
        paddingRight = getPaddingRight() + (xOffset / 2);

        int width = getWidth();
        int height = getHeight();

        innerCircleBounds = new RectF(
                paddingLeft + (1.5F * barWith),
                paddingTop + (1.5F * barWith),
                width - paddingRight - (1.5F * barWith),
                height - paddingBottom - (1.5F * barWith)
        );

        circleBounds = new RectF(
                paddingLeft + barWith,
                paddingTop + barWith,
                width - paddingRight - barWith,
                height - paddingBottom - barWith);

        circleInnerContour = new RectF(
                circleBounds.left + (rimWith / 2.0F) + (contourSize / 2.0F),
                circleBounds.top + (rimWith / 2.0F) + (contourSize / 2.0F),
                circleBounds.right - (rimWith / 2.0F) - (contourSize / 2.0F),
                circleBounds.bottom - (rimWith / 2.0F) - (contourSize / 2.0F)
        );

        circleOuterContour = new RectF(
                circleBounds.left - (rimWith / 2.0F) - (contourSize / 2.0F),
                circleBounds.top - (rimWith / 2.0F) - (contourSize / 2.0F),
                circleBounds.right + (rimWith / 2.0F) + (contourSize / 2.0F),
                circleBounds.bottom + (rimWith / 2.0F) + (contourSize / 2.0F)
        );

        fullRadius = (width - paddingRight - barWith) / 2;
        circleRadius = (fullRadius - barWith) + 1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        if (heightMode != MeasureSpec.UNSPECIFIED && widthMode != MeasureSpec.UNSPECIFIED) {
            if (widthWithoutPadding > heightWithoutPadding) {
                size = heightWithoutPadding;
            } else {
                size = widthWithoutPadding;
            }
        } else {
            size = Math.max(heightWithoutPadding, widthWithoutPadding);
        }

        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onSizeChanged(int newWidth, int newHeight, int oldWidth, int oldHeight) {
        super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight);
        layoutWidth = newWidth;
        layoutHeight = newHeight;
        setupBounds();
        setupPaints();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawArc(innerCircleBounds, 360, 360, false, circlePaint);
        canvas.drawArc(circleBounds, 360, 360, false, rimPaint);
        canvas.drawArc(circleOuterContour, 360, 360, false, contourPaint);

        if (isRunning) {
            canvas.drawArc(circleBounds, 0, barLength, false, barPaint);
        } else {
            canvas.drawArc(circleBounds, -90, barLength, false, barPaint);
        }

        float textHeight = textPaint.descent() - textPaint.ascent();
        float verticalTextOffset = (textHeight / 2) - textPaint.descent();
        float horizontalTextOffset = textPaint.measureText(text) / 2;

        canvas.drawText(text, getWidth() / 2 - horizontalTextOffset, getHeight() / 2 + verticalTextOffset, textPaint);
    }

    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
        invalidate();
        requestLayout();
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
        requestLayout();
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        invalidate();
        requestLayout();
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
        requestLayout();
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }
}