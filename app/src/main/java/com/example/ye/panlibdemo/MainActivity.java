package com.example.ye.panlibdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

import com.example.panlibrary.PanView;
import com.google.firebase.crash.FirebaseCrash;

public class MainActivity extends AppCompatActivity {

    private Button button, button2;

    private PanView panView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        panView = (PanView) findViewById(R.id.panView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panView.setRotation(190f);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panView.setRotation(10f);
            }
        });
        FirebaseCrash.report(new Exception("My first Android non-fatal error"));
    }
}
