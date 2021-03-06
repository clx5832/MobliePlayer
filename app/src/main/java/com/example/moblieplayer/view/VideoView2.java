package com.example.moblieplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.VideoView;

public class VideoView2 extends android.widget.VideoView {
    public VideoView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);//保存测量结果
    }

    /*
    * 设置视频画面大小
    * width视频的宽
    * height视频的高
    * */
    public void setVideoSize(int width,int height){

        //获得布局参数
        ViewGroup.LayoutParams params =  getLayoutParams();

        params.width =width;
        params.height =height;
        setLayoutParams(params);//把设置的参数传进去
    }
}
