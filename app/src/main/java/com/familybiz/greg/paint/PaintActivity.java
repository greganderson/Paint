package com.familybiz.greg.paint;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public class PaintActivity extends Activity {

	private final int[] startingColors = {
			Color.BLACK,
			Color.WHITE,
			Color.RED,
			Color.YELLOW,
			Color.BLUE,
			Color.GREEN
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		LinearLayout rootLayout = new LinearLayout(this);
		rootLayout.setOrientation(LinearLayout.VERTICAL);

		PaintView paintArea = new PaintView(this);

        PaletteView palette = new PaletteView(this);
		palette.setBackgroundColor(Color.DKGRAY);

        for (int splotchIndex = 0; splotchIndex < startingColors.length; splotchIndex++) {
            SplotchView splotchView = new SplotchView(this);
            splotchView.setColor(startingColors[splotchIndex]);
            palette.addView(splotchView, new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));

            splotchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

            splotchView.setOnSplotchTouchListener(new SplotchView.OnSplotchTouchListener() {
                @Override
                public void onSplotchTouched(SplotchView v) {
                }
            });
        }
		rootLayout.addView(paintArea, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT,
				1));
		rootLayout.addView(palette, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT,
				1));
        setContentView(rootLayout);
    }
}
