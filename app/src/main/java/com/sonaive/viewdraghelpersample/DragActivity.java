package com.sonaive.viewdraghelpersample;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by liutao on 15-7-20.
 */
public class DragActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag);
        DragLayout dragLayout = (DragLayout) findViewById(R.id.drag_layout);
        if (getIntent().getBooleanExtra("drag_horizontal", false)) {
            dragLayout.setDragHorizontal(true);
        }
        if (getIntent().getBooleanExtra("drag_vertical", false)) {
            dragLayout.setDragVertical(true);
        }
        if (getIntent().getBooleanExtra("drag_capture", false)) {
            dragLayout.setCapterView(true);
        }
    }
}
