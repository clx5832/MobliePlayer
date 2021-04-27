package com.example.moblieplayer.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.moblieplayer.R;
import com.example.moblieplayer.activity.JinRiTouTiaoSearchAcitivity;

public class TitleBar extends LinearLayout {
    private final Context context;
    private View search;
    private View game;
    private View history;

    public TitleBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    //当布局加载完时就加载这个方法
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        search = getChildAt(1);
        game = getChildAt(2);
        history = getChildAt(3);

        search.setOnClickListener(new MyOnClickListener());
        game.setOnClickListener(new MyOnClickListener());
        history.setOnClickListener(new MyOnClickListener());
    }

    class MyOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.tv_search:
//                    Toast.makeText(context, "搜索", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(context, JinRiTouTiaoSearchAcitivity.class);
                    context.startActivity(intent);
                    break;

                case R.id.rl_game:
                    Toast.makeText(context, "游戏", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.iv_history:
                    Toast.makeText(context, "历史记录", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
