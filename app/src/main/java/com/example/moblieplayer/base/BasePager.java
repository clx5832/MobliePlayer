package com.example.moblieplayer.base;

import android.content.Context;
import android.view.View;

//基类就是父类，就是本地视频，本地音乐，网络视频，网路音乐的公共类（基类）
public abstract class BasePager {

    //上下文
    public Context context;

    public Boolean isInitData = false;

    //视图,各个页面实例化结果
    public View rootView;

    //公共类需要一个构造器，需要传入上下文
//    构造器可以提供许多特殊的方法，构造器作为一种方法，
//    负责类中成员变量（域）的初始化。
//    实例构造器分为缺省构造器和非缺省构造器
    public BasePager(Context context) {
        this.context = context;
        rootView = initView();
        isInitData = false;
    }

    //强制孩子实现该方法，实现特定的效果
    //所以不能有方法体
    public abstract View initView();

    //当孩子需要初始化数据的时候，重写该方法，用于请求数据
    //或者显示数据
    public void initData() {

    }
}
