package com.sonaive.viewdraghelpersample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by liutao on 15-7-22.
 */
public class PantsOffActivity extends ActionBarActivity implements View.OnClickListener {
    private Button mPantsBelt;
    private Button mButt;
    private PantsOffLayout mPantsOffLayout;
    private LinearLayout mMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pants_off);
        mPantsOffLayout = (PantsOffLayout) findViewById(R.id.pants_off_layout);
        mMainLayout = (LinearLayout) findViewById(R.id.main_layout);
        mButt = (Button) findViewById(R.id.butt);
        mButt.setOnClickListener(this);
        mPantsBelt = (Button) findViewById(R.id.pants_belt);
        mPantsBelt.setOnClickListener(this);
//        mMainLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                if (mPantsOffLayout.isDragging()) {
//                    v.setTop(oldTop);
//                    v.setBottom(oldBottom);
//                    v.setLeft(oldLeft);
//                    v.setRight(oldRight);
//                }
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        Button b = (Button) v;
        Toast t = Toast.makeText(this, b.getText() + " clicked", Toast.LENGTH_SHORT);
        t.show();
    }
}
