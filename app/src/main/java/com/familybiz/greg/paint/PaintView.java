package com.familybiz.greg.paint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

public class PaintView extends View {

    RectF mContentRect;
    float mRadius;
    int mColor = Color.CYAN;
	private Path mPath;
	private boolean mActive = false;

    public interface OnSplotchTouchListener {
        public void onSplotchTouched(PaintView v);
    }

	public void setOnSplotchTouchListener(OnSplotchTouchListener listener) {
		mOnSplotchTouchListener = listener;
	}

	public OnSplotchTouchListener getOnSplotchTouchListener() {
		return mOnSplotchTouchListener;
	}

    OnSplotchTouchListener mOnSplotchTouchListener = null;

    public PaintView(Context context) {
        super(context);
        setMinimumHeight(50);
        setMinimumWidth(50);
    }

	private void initialize() {
		mPath = new Path();

		mContentRect = new RectF();
		mContentRect.left = getPaddingLeft();
		mContentRect.top = getPaddingTop();
		mContentRect.right = getWidth() - getPaddingRight();
		mContentRect.bottom = getHeight() - getPaddingBottom();

		PointF center = new PointF(mContentRect.centerX(), mContentRect.centerY());
		float maxRadius = Math.min(mContentRect.width() * 0.5f, mContentRect.height() * 0.5f);
		float minRadius = 0.25f * maxRadius;
		mRadius = minRadius + (maxRadius - minRadius) * 0.5f;
		int pointCount = 30;

		for (int pointIndex = 0; pointIndex < pointCount; pointIndex += 3) {
			// Control 1
			PointF control1 = new PointF();
			float control1Radius = mRadius + (float)(Math.random() - 0.5) * 2.0f * 8.0f;
			control1.x = center.x + control1Radius *
					(float)Math.cos(((double)pointIndex / (double)pointCount) * 2.0 * Math.PI);
			control1.y = center.y + control1Radius *
					(float)Math.sin(((double)pointIndex / (double)pointCount) * 2.0 * Math.PI);

			// Control 2
			PointF control2 = new PointF();
			float control2Radius = mRadius + (float)(Math.random() - 0.5) * 2.0f * 8.0f;
			control2.x = center.x + control2Radius *
					(float)Math.cos(((double)pointIndex / (double)pointCount) * 2.0 * Math.PI);
			control2.y = center.y + control2Radius *
					(float)Math.sin(((double)pointIndex / (double)pointCount) * 2.0 * Math.PI);

			// Point
			PointF point = new PointF();
			point.x = center.x + mRadius *
					(float)Math.cos(((double)pointIndex / (double)pointCount) * 2.0 * Math.PI);
			point.y = center.y + mRadius *
					(float)Math.sin(((double)pointIndex / (double)pointCount) * 2.0 * Math.PI);

			if(pointIndex == 0)
				mPath.moveTo(point.x, point.y);
			else
				mPath.cubicTo(control1.x, control1.y, control2.x, control2.y, point.x, point.y);
		}
	}

    public int getColor() {
        return mColor;
    }

    public void setColor(int mColor) {
        this.mColor = mColor;
        invalidate();
    }

	public void setActive(boolean active) {
		mActive = active;
		invalidate();
	}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        float circleCenterX = mContentRect.centerX();
        float circleCenterY = mContentRect.centerY();

        float distance = (float) Math.sqrt(Math.pow(circleCenterX - x, 2) + Math.pow(circleCenterY - y, 2));
        if (distance < mRadius) {
			if (mOnSplotchTouchListener != null) {
				setActive(true);
				mOnSplotchTouchListener.onSplotchTouched(this);
			}
		}

        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(mColor);

		if (mPath == null)
			initialize();

		if (mActive) {
			Paint outline = new Paint(Paint.ANTI_ALIAS_FLAG);
			outline.setColor(Color.GREEN);
			outline.setStrokeWidth(3.0f);
			outline.setStyle(Paint.Style.STROKE);

			float cx = mContentRect.width() / 2;
			float cy = mContentRect.height() / 2;
			float radius = mContentRect.width() / 2 - 3.0f;
			canvas.drawCircle(cx, cy, radius, outline);
		}

		canvas.drawPath(mPath, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSpec = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpec = MeasureSpec.getSize(heightMeasureSpec);

        int width = getSuggestedMinimumWidth();
        int height = getSuggestedMinimumHeight();

        if (widthMode == MeasureSpec.AT_MOST) {
            width = widthSpec;
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            height = heightSpec;
        }

        if (widthMode ==  MeasureSpec.EXACTLY) {
            width = widthSpec;
            height = width;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSpec;
            width = height;
        }

        // TODO; RESPECT THE PADDING!
        if (width > height && widthMode != MeasureSpec.EXACTLY) {
            width = height;
        }
        if (height > width && heightMode != MeasureSpec.EXACTLY) {
            height = width;
        }

        setMeasuredDimension(resolveSizeAndState(width, widthMeasureSpec,
                        width < getSuggestedMinimumWidth() ? MEASURED_STATE_TOO_SMALL: 0),
                             resolveSizeAndState(height, heightMeasureSpec,
                        height < getSuggestedMinimumHeight() ? MEASURED_STATE_TOO_SMALL: 0));
    }
}
