package com.example.ye.panlibdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.example.panlibrary.PanView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.panView)
    PanView panView;
    @BindView(R.id.button)
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        FirebaseCrash.report(new Exception("My first Android non-fatal error"));
    }

    @OnClick(R.id.button)
    public void onViewClicked() {
        panView.reset();
    }
}
