package com.sonaive.viewdraghelpersample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(this);
        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(this);
        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(this);
        Button button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(this);
        Button button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                Intent intent = new Intent(this, DragActivity.class);
                intent.putExtra("drag_horizontal", true);
                startActivity(intent);
                break;

            case R.id.button2:
                intent = new Intent(this, DragActivity.class);
                intent.putExtra("drag_vertical", true);
                startActivity(intent);
                break;

            case R.id.button3:
                intent = new Intent(this, DragActivity.class);
                intent.putExtra("drag_capture", true);
                startActivity(intent);
                break;
            case R.id.button4:
                intent = new Intent(this, YoutubeActivity.class);
                startActivity(intent);
                break;
            case R.id.button5:
                intent = new Intent(this, PantsOffActivity.class);
                startActivity(intent);
                break;
        }
    }
}
