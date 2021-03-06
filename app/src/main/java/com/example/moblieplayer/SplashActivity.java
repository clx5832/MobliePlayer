package com.example.moblieplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

public class SplashActivity extends Activity {

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("0000000000000000000000000000000000000000000000000000000000000000000");
                startMainActivity();
            }
        }, 2000);
    }

    private Boolean isStartActivity = false;

    private void startMainActivity() {
        if (!isStartActivity) {
            isStartActivity = true;
            System.out.println("1111111111111111111111111111111111111111111111111111111");
            Intent intent = new Intent();
            intent.setClass(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            //关闭启动的页面
            finish();
        }
    }

    //触屏事件，触屏启动界面，马上跳转到主界面
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("22222222222222222222222222222222222222222222222222222222222222");
        startMainActivity();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        System.out.println("3333333333333333333333333333333333333333333333333333333333333333333333333");
        //移除回调函数的信息
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
