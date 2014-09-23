package com.familybiz.greg.paint;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class PaletteView extends ViewGroup implements PaintView.OnSplotchTouchListener {

	@Override
	public void onSplotchTouched(PaintView v) {
		setCurrentSelectedColor(v.getColor());
		if (mOnColorChangedListener != null)
			mOnColorChangedListener.onColorChanged(this);
	}

	private float mStartX;
	private float mStartY;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		for (int i = 0; i < getChildCount(); i++) {
			PaintView v = (PaintView)getChildAt(i);
			if (v.isActive() && v.isTouched()) {
				switch (event.getActionMasked()) {
					case MotionEvent.ACTION_DOWN:
						mStartX = v.getX();
						mStartY = v.getY();
						break;
					case MotionEvent.ACTION_MOVE:
						v.setX(event.getX() - v.getWidth() / 2);
						v.setY(event.getY() - v.getHeight() / 2);
						break;
					case MotionEvent.ACTION_UP:
						v.setTouched(false);

						// Check if the splotch was dropped off the view
						if (!inPalette(event.getX(), event.getY())) {
							removeView(v);
							mSplotches.remove(v);
							invalidate();
							break;
						}

						// Add a new splotch if a mix occurred
						PaintView splotchDroppedOn = inSplotch(v, event.getX(), event.getY());
						if (splotchDroppedOn != null) {
							v.setX(mStartX);
							v.setY(mStartY);
							PaintView newSplotch = new PaintView(getContext());
							int color = mixColors(splotchDroppedOn.getColor(), v.getColor());
							newSplotch.setColor(color);
							newSplotch.setActive(false);
							newSplotch.setOnSplotchTouchListener(this);
							this.addView(newSplotch);
							mSplotches.add(newSplotch);
							invalidate();
							break;
						}

						// Send splotch back where it belongs
						ObjectAnimator animator = new ObjectAnimator();
						animator.setDuration(200);
						animator.setTarget(v);
						float[] x = {v.getX(), mStartX};
						float[] y = {v.getY(), mStartY};
						animator.setValues(
								PropertyValuesHolder.ofFloat("x", x),
								PropertyValuesHolder.ofFloat("y", y));
						animator.start();
						break;
				}
				break;
			}
		}
		return true;
	}

	private int mixColors(int c1, int c2) {
		int r1 = (c1 >> 16) & 0xFF;
		int r2 = (c2 >> 16) & 0xFF;
		int g1 = (c1 >> 8) & 0xFF;
		int g2 = (c2 >> 8) & 0xFF;
		int b1 = c1 & 0xFF;
		int b2 = c2 & 0xFF;

		int nr = (r1 + r2) / 2;
		int ng = (g1 + g2) / 2;
		int nb = (b1 + b2) / 2;

		return (0xFF << 24) | (nr << 16) | (ng << 8) | nb;
	}

	private PaintView inSplotch(PaintView v, float x, float y) {
		for (int i = 0; i < getChildCount(); i++) {
			PaintView child = (PaintView)getChildAt(i);
			if (child == v)
				continue;
			PointF centerPoint = mStartingPoints.get(child);
			float distance = (float) Math.sqrt(Math.pow(centerPoint.x - x, 2) + Math.pow(centerPoint.y - y, 2));
			float radius = child.mRadius;
			if (distance < radius)
				return child;
		}

		return null;
	}

	public interface OnColorChangedListener {
		public void onColorChanged(PaletteView v);
	}

	OnColorChangedListener mOnColorChangedListener = null;

	public void setOnColorChangedListener(OnColorChangedListener listener) {
		mOnColorChangedListener = listener;
	}

	public OnColorChangedListener getOnColorChangedListener() {
		return mOnColorChangedListener;
	}

	private final int[] startingColors = {
			Color.BLACK,
			Color.WHITE,
			Color.RED,
			Color.YELLOW,
			Color.BLUE,
			Color.GREEN
	};

	public static ArrayList<PaintView> mSplotches;
	private HashMap<PaintView, PointF> mStartingPoints;
	private int mCurrentSelectedColor;

    public PaletteView(Context context) {
		super(context);

		mSplotches = new ArrayList<PaintView>();
		mStartingPoints = new HashMap<PaintView, PointF>();

		for (int splotchIndex = 0; splotchIndex < startingColors.length; splotchIndex++) {
        	PaintView paintView = new PaintView(context);
        	paintView.setColor(startingColors[splotchIndex]);
			if (splotchIndex == 0) {
				paintView.setActive(true);
				setCurrentSelectedColor(paintView.getColor());
			}
			addView(paintView, new LinearLayout.LayoutParams(600, 600));

			mSplotches.add(paintView);
			mStartingPoints.put(paintView, new PointF(0.0f, 0.0f));

			paintView.setOnSplotchTouchListener(this);
		}
	}

	private void setCurrentSelectedColor(int color) {
		mCurrentSelectedColor = color;
	}

	public int getCurrentSelectedColor() {
		return mCurrentSelectedColor;
	}

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpec = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpec = MeasureSpec.getSize(heightMeasureSpec);

        int width = Math.max(widthSpec, getSuggestedMinimumWidth());
        int height = Math.max(heightSpec, getSuggestedMinimumHeight());

        int childState = 0;
        for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {
            View child = getChildAt(childIndex);
            child.measure(MeasureSpec.AT_MOST | 200, MeasureSpec.AT_MOST | 200);
            childState = combineMeasuredStates(childState, child.getMeasuredState());
        }

        setMeasuredDimension(
				resolveSizeAndState(width, widthMeasureSpec, childState),
                resolveSizeAndState(height, heightMeasureSpec, childState));
    }

    @Override
    protected void onLayout(boolean b, int i, int i2, int i3, int i4) {
        int childWidthMax = 0;
        int childHeightMax = 0;
		int childrenNotGone = 0;
        for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {
            View child = getChildAt(childIndex);
            if (child.getVisibility() == GONE)
				continue;
            childHeightMax = Math.max(childWidthMax, child.getMeasuredWidth());
            childWidthMax = Math.max(childHeightMax, child.getMeasuredHeight());
			childrenNotGone++;
        }

		Rect layoutRect = new Rect();
		layoutRect.left = getPaddingLeft() + childWidthMax / 2;
		layoutRect.top = getPaddingTop() + childHeightMax / 2;
		layoutRect.right = getWidth() - getPaddingRight() - childWidthMax / 2;
		layoutRect.bottom = getHeight() - getPaddingBottom() - childHeightMax / 2;

		int childAngleIndex = 0;
        for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {
            double angle = (double) childAngleIndex / (double) childrenNotGone * 2.0 * Math.PI;
            int childCenterX = (int)(layoutRect.centerX() +  layoutRect.width() * 0.5 * Math.cos(angle));
            int childCenterY = (int)(layoutRect.centerY() + layoutRect.height() * 0.5 * Math.sin(angle));

            View child = getChildAt(childIndex);
			mStartingPoints.put((PaintView)child, new PointF(childCenterX, childCenterY));
			Rect childLayout = new Rect();
			if (child.getVisibility() == GONE) {
				childLayout.left = 0;
				childLayout.top = 0;
				childLayout.right = 0;
				childLayout.bottom = 0;
			}
			else {
				childLayout.left = childCenterX - childWidthMax / 2;
				childLayout.top = childCenterY - childHeightMax / 2;
				childLayout.right = childCenterX + childWidthMax / 2;
				childLayout.bottom = childCenterY + childHeightMax / 2;
				childAngleIndex++;
			}

            child.layout(childLayout.left, childLayout.top, childLayout.right, childLayout.bottom);
        }
    }

	private boolean inPalette(float x, float y) {
		double centerX = getWidth() / 2;
		double centerY = getHeight() / 2;
		double topX = Math.pow(x - centerX, 2);
		double topY = Math.pow(y - centerY, 2);
		double rX = Math.pow(getWidth() / 2, 2);
		double rY = Math.pow(getHeight() / 2, 2);
		return topX / rX + topY / rY <= 1;
	}
}
