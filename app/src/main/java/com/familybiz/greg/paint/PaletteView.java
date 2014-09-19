package com.familybiz.greg.paint;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

public class PaletteView extends ViewGroup {


    public PaletteView(Context context) {
        super(context);
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
        for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {

            View child = getChildAt(childIndex);
            if (child.getVisibility() == GONE)
				continue;
            childHeightMax = Math.max(childWidthMax, child.getMeasuredWidth());
            childWidthMax = Math.max(childHeightMax, child.getMeasuredHeight());
        }

        Rect layoutRect = new Rect();
        layoutRect.left = getPaddingLeft() + 25;
        layoutRect.top = getPaddingTop() + 25;
        layoutRect.right = getWidth() - getPaddingRight() - 25;
        layoutRect.bottom = getHeight() - getPaddingBottom() - 25;

        for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {

            double angle = (double) childIndex / (double) getChildCount() * 2.0 * Math.PI;
            int childCenterX = (int)(layoutRect.centerX() +  layoutRect.width() * 0.5 * Math.cos(angle));
            int childCenterY = (int)(layoutRect.centerY() + layoutRect.height() * 0.5 * Math.sin(angle));

            View child = getChildAt(childIndex);
            Rect childLayout = new Rect();
            childLayout.left = childCenterX - 25;
            childLayout.top = childCenterY - 25;
            childLayout.right = childCenterX + 25;
            childLayout.bottom = childCenterY + 25;

            child.layout(childLayout.left, childLayout.top, childLayout.right, childLayout.bottom);
        }
    }
}
