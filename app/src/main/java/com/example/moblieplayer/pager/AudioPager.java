package com.example.moblieplayer.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.moblieplayer.base.BasePager;

//本地音频的页面
public  class AudioPager extends BasePager {

    private TextView textView;
    public AudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        textView =new TextView(context);
        textView.setTextSize(30);
//        textView.setText("这是本地音频");
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    @Override
    public void initData() {
        System.out.println(" textView.setText(\"这是本地音频\");初始化了=============");
        textView.setText("这是本地音频");
        super.initData();
    }
}
