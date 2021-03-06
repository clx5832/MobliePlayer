package com.example.moblieplayer.test;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.moblieplayer.utils.LogUtil;

public class TestActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.v("onCreate", "B,onDestroy======================");

        TextView textView =new TextView(this);
        textView.setText("我是测试页面");
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(30);
        textView.setTextColor(Color.RED);
        setContentView(textView);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.v("onCreate", "B,onDestroy======================");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.v("onCreate", "B,onRestart======================");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.v("onCreate", "B,onResume======================");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.v("onCreate", "B,onStop======================");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.v("onCreate", "B,onPause======================");
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.v("onCreate", "B,onStart======================");
    }

}
