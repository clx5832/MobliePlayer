package com.example.moblieplayer;

import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.moblieplayer.base.BasePager;
import com.example.moblieplayer.fragment.ReplaceFragment;
import com.example.moblieplayer.pager.AudioPager;
import com.example.moblieplayer.pager.Net_AudioPager;
import com.example.moblieplayer.pager.Net_VideoPager;
import com.example.moblieplayer.pager.VideoPager;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
    //实例化RadioGroup
//    FrameLayout rl_main;
    RadioGroup rg_main;
    //新建一个集合存放各个页面，且用BasePager作为泛型
    private ArrayList<BasePager> basePagers;

    private int position;

    //    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rg_main = (RadioGroup) findViewById(R.id.rg_main);

        basePagers = new ArrayList<>();
        basePagers.add(new VideoPager(this));//本地视频
        basePagers.add(new AudioPager(this));//本地音乐
        basePagers.add(new Net_VideoPager(this));//网络视频
        basePagers.add(new Net_AudioPager(this));//网络音乐

        //设置帧布局的监听
        rg_main.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        rg_main.check(R.id.rb_video);
    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            switch (checkedId) {

                default://默认的会选择本地视频
                    position = 0;
                    break;

                case R.id.rb_music://本地音乐
                    position = 1;
                    break;

                case R.id.rb_net_video://网络视频
                    position = 2;
                    break;

                case R.id.rb_net_music://网络音乐
                    position = 3;
                    break;
            }
            setFragment();
        }
    }

    private void setFragment() {

        FragmentManager fm = getSupportFragmentManager();//得到fragmentManager
        FragmentTransaction ft = fm.beginTransaction();//开启事务
        ft.replace(R.id.fl_main, new ReplaceFragment(getBasePager()));
        //这种方法不行，只能另外创建一个公共类来实现
//        ft.replace(R.id.fl_main,new Fragment(){
//            @Nullable
//            @Override
//            public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//                BasePager basePager =getBasePager();
//                if (basePager !=null){
//                    return basePager.rootView;
//                }
//                return null;
//            }
//        });
        ft.commit();//提交
    }

    private BasePager getBasePager() {
        BasePager basePager = basePagers.get(position);

        //加上 basePager.isInitData = true;的作用是只调用一次initData中的数据，
        // 以至于每次不用都调用initData中的数据
        if (basePager != null&& !basePager.isInitData ) {
            basePager.isInitData = true;
            basePager.initData();
        }
        return basePager;
    }
}
