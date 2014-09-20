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

public class PaintAreaView extends View {

	// List of all the different sets of poly lines that have been drawn
	private ArrayList<LineColorPair> mPointList = new ArrayList<LineColorPair>();
	// Holds the current poly line data
	private ArrayList<PointF> mPoints = new ArrayList<PointF>();

	private int mColor = Color.RED;

	public PaintAreaView(Context context) {
		super(context);
	}

	public void setColor(int color) {
		mColor = color;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		if (event.getActionMasked() == MotionEvent.ACTION_UP) {
			PointF[] points = new PointF[mPoints.size()];
			mPoints.toArray(points);
			mPointList.add(new LineColorPair(points, mColor));
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

		// Draw previous lines
		if (mPointList.size() > 0) {
			for (int i = 0; i < mPointList.size(); i++) {
				PointF[] points = mPointList.get(i).getPoints();
				int color = mPointList.get(i).getColor();
				Paint polylinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
				polylinePaint.setStyle(Paint.Style.STROKE);
				polylinePaint.setStrokeWidth(2.0f);
				polylinePaint.setColor(color);
				Path polylinePath = new Path();
				polylinePath.moveTo(points[0].x, points[0].y);
				for (PointF point : points)
					polylinePath.lineTo(point.x, point.y);
				canvas.drawPath(polylinePath, polylinePaint);
			}
		}

		// Draw line user is currently drawing
		if (mPoints.size() > 0) {
			Paint polylinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			polylinePaint.setStyle(Paint.Style.STROKE);
			polylinePaint.setStrokeWidth(2.0f);
			polylinePaint.setColor(mColor);
			Path polylinePath = new Path();
			polylinePath.moveTo(mPoints.get(0).x, mPoints.get(0).y);
			for (PointF point : mPoints)
				polylinePath.lineTo(point.x, point.y);
			canvas.drawPath(polylinePath, polylinePaint);
		}
	}

	/**
	 * Represents the points of a users dragging across the screen as well as
	 * the color that they were.
	 */
	private class LineColorPair {

		private PointF[] mPoints;
		private int mColor;

		public LineColorPair(PointF[] points, int color) {
			mPoints = points;
			mColor = color;
		}

		public PointF[] getPoints() {
			return mPoints;
		}

		public int getColor() {
			return mColor;
		}
	}
}
