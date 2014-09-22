package com.familybiz.greg.paint;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class PaletteView extends ViewGroup implements PaintView.OnSplotchTouchListener {

	@Override
	public void onSplotchTouched(PaintView v) {
		setCurrentSelectedColor(v.getColor());
		if (mOnColorChangedListener != null)
			mOnColorChangedListener.onColorChanged(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		for (int i = 0; i < getChildCount(); i++) {
			PaintView v = (PaintView)getChildAt(i);
			if (v.isActive()) {
				v.setX(event.getX() - v.getWidth() / 2);
				v.setY(event.getY() - v.getHeight() / 2);
				break;
			}
		}
		return true;
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
	private int mCurrentSelectedColor;
	private PaintView mRovingPaint;

    public PaletteView(Context context) {
		super(context);

		mSplotches = new ArrayList<PaintView>();

		for (int splotchIndex = 0; splotchIndex < startingColors.length; splotchIndex++) {
        	PaintView paintView = new PaintView(context);
        	paintView.setColor(startingColors[splotchIndex]);
			if (splotchIndex == 0) {
				paintView.setActive(true);
				setCurrentSelectedColor(paintView.getColor());
			}
        	addView(paintView, new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));

			mSplotches.add(paintView);

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
            child.measure(MeasureSpec.AT_MOST | 75, MeasureSpec.AT_MOST | 75);
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
}
