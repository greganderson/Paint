package com.familybiz.greg.paint;

import android.app.Activity;
import android.graphics.Color;
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

        PaletteView palette = new PaletteView(this);
		mPaintArea.setColor(palette.getCurrentSelectedColor());
		palette.setBackgroundColor(Color.DKGRAY);

		//palette.setOnColorChangedListener(this);

		rootLayout.addView(mPaintArea, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
		rootLayout.addView(palette, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
        setContentView(rootLayout);
    }

	@Override
	public void onColorChanged(PaletteView v) {
		//mPaintArea.setColor(v.getCurrentSelectedColor());
	}
}
