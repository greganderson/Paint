package com.familybiz.greg.paint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class PaintView extends View {

	// List of all the different sets of poly lines that have been drawn
	private ArrayList<PointF[]> mPointList = new ArrayList<PointF[]>();
	// Holds the current poly line data
	private ArrayList<PointF> mPoints = new ArrayList<PointF>();

	public PaintView(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		if (event.getActionMasked() == MotionEvent.ACTION_UP) {
			PointF[] copy = new PointF[mPoints.size()];
			mPoints.toArray(copy);
			mPointList.add(copy);
			mPoints.clear();
		}
		else
			mPoints.add(new PointF(x, y));

		invalidate();
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mPoints.size() > 0) {
			Paint polylinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			polylinePaint.setStyle(Paint.Style.STROKE);
			polylinePaint.setStrokeWidth(2.0f);
			polylinePaint.setColor(Color.RED);
			Path polylinePath = new Path();
			polylinePath.moveTo(mPoints.get(0).x, mPoints.get(0).y);
			for (PointF point : mPoints)
				polylinePath.lineTo(point.x, point.y);
			canvas.drawPath(polylinePath, polylinePaint);
		}

		if (mPointList.size() > 0) {
			for (int i = 0; i < mPointList.size(); i++) {
				PointF[] points = mPointList.get(i);
				Paint polylinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
				polylinePaint.setStyle(Paint.Style.STROKE);
				polylinePaint.setStrokeWidth(2.0f);
				polylinePaint.setColor(Color.RED);
				Path polylinePath = new Path();
				polylinePath.moveTo(points[0].x, points[0].y);
				for (PointF point : points)
					polylinePath.lineTo(point.x, point.y);
				canvas.drawPath(polylinePath, polylinePaint);
			}
		}
	}
}
