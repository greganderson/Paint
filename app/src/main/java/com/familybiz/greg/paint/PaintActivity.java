package com.familybiz.greg.paint;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public class PaintActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PaletteView rootLayout = new PaletteView(this);

        for (int splotchIndex = 0; splotchIndex < 10; splotchIndex++) {
            PaintView paintView = new PaintView(this);
            paintView.setBackgroundColor(Color.YELLOW);
            paintView.setColor(Color.DKGRAY);
            rootLayout.addView(paintView, new LinearLayout.LayoutParams(500, ViewGroup.LayoutParams.WRAP_CONTENT));

            paintView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setVisibility(View.GONE);
                }
            });

            paintView.setOnSplotchTouchListener(new PaintView.OnSplotchTouchListener() {
                @Override
                public void onSplotchTouched(PaintView v) {
                    v.setColor(Color.DKGRAY);
                }
            });
        }
        setContentView(rootLayout);
    }
}
