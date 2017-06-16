package com.example.mark.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    /**
     * Called when the activity is first created.
     */
    long timeOnClock = 0;
    boolean running = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView uploadStatus = (TextView)findViewById(R.id.uploadStatus);
        Button buttonStart = (Button) findViewById(R.id.buttonstart);
        Button buttonStop = (Button) findViewById(R.id.buttonstop);

        buttonStart.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadStatus.setText("uploading...");
            }
        });
        buttonStop.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadStatus.setText("not uploading");
            }
        });

    }
}
