package com.familybiz.greg.paint;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public class PaintActivity extends Activity implements PaletteView.OnColorChangedListener {

	private PaintAreaView mPaintArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		LinearLayout rootLayout = new LinearLayout(this);
		rootLayout.setOrientation(LinearLayout.VERTICAL);

		mPaintArea = new PaintAreaView(this);

        final PaletteView palette = new PaletteView(this);
		mPaintArea.setColor(palette.getCurrentSelectedColor());

		palette.setOnColorChangedListener(this);
		palette.setBackground(new Drawable() {
			@Override
			public void draw(Canvas canvas) {
				RectF rect = new RectF();
				rect.left = palette.getPaddingLeft();
				rect.top = palette.getPaddingTop();
				rect.right = palette.getWidth() - palette.getPaddingRight();
				rect.bottom = palette.getHeight() - palette.getPaddingBottom();

				Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
				paint.setColor(Color.rgb(244, 164, 96));
				float radiusX = rect.width() / 2;
				float radiusY = rect.height() / 2;
				int pointCount = 500;
				Path path = new Path();
				for (int pointIndex = 0; pointIndex < pointCount; pointIndex += 3) {
					PointF point = new PointF();
					point.x = rect.centerX() + radiusX *
							(float) Math.cos(((double) pointIndex / (double) pointCount) * 2.0 * Math.PI);
					point.y = rect.centerY() + radiusY *
							(float) Math.sin(((double) pointIndex / (double) pointCount) * 2.0 * Math.PI);

					if (pointIndex == 0)
						path.moveTo(point.x, point.y);
					else
						path.lineTo(point.x, point.y);
				}

				canvas.drawPath(path, paint);
			}

			@Override
			public void setAlpha(int i) {

			}

			@Override
			public void setColorFilter(ColorFilter colorFilter) {

			}

			@Override
			public int getOpacity() {
				return 0;
			}
		});

		rootLayout.addView(mPaintArea, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
		rootLayout.addView(palette, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
        setContentView(rootLayout);
    }

	@Override
	public void onColorChanged(PaletteView v) {
		mPaintArea.setColor(v.getCurrentSelectedColor());
	}
}
