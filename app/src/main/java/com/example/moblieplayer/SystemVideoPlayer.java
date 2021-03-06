package com.example.moblieplayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.moblieplayer.domain.MediaItem;
import com.example.moblieplayer.utils.LogUtil;
import com.example.moblieplayer.utils.Utils;
import com.example.moblieplayer.view.VideoView2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

//设置播放功能的类
public class SystemVideoPlayer extends Activity implements View.OnClickListener {
    /*
     * 1.进度更新
     * */
    private static final int PROGRESS = 0;
    /*
     * 隐藏控制面板的
     * */
    private static final int HIDEMEDIACONTROLLER = 2;
    //默认播放
    private static final int DEFAULT_SCREEN = 3;

    //全屏播放
    private static final int FULL_SCREEN = 4;
    //    @BindView(R.id.video_player)
//    VideoView videoPlayer;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.btn_voice)
    Button btnVoice;
    @BindView(R.id.seekbar_voice)
    SeekBar seekbarVoice;
    @BindView(R.id.btn_switch_player)
    Button btnSwitchPlayer;
    @BindView(R.id.ll_top)
    LinearLayout llTop;
    @BindView(R.id.tv_current_time)
    TextView tvCurrentTime;
    @BindView(R.id.seekbar_video)
    SeekBar seekbarVideo;
    @BindView(R.id.tv_duration_time)
    TextView tvDurationTime;
    @BindView(R.id.btn_video_exit)
    Button btnVideoExit;
    @BindView(R.id.btn_video_pre)
    Button btnVideoPre;
    @BindView(R.id.btn_video_start_pause)
    Button btnVideoStartPause;
    @BindView(R.id.btn_video_next)
    Button btnVideoNext;
    @BindView(R.id.btn_video_switch_screen)
    Button btnVideoSwitchScreen;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;
    //    @BindView(R.id.tv_name)
//    TextView tvName;
    @BindView(R.id.iv_battery)
    ImageView ivBattery;
    private Uri uri;//这个是视频的地址
    private VideoView2 video_player;

    //定义Utils
    private Utils utils;

    private BatteryReceiver receiver;

    //判断是否为全屏
    private boolean isFullScreen = false;

    //定义handler
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case PROGRESS:

                    //得到当前的播放进度
                    int currentPositon = video_player.getCurrentPosition();//获得当前的时间
                    seekbarVideo.setProgress(currentPositon);//设置seekBar的当前时间

                    tvCurrentTime.setText(utils.stringForTime(currentPositon));

                    //更新系统时间
                    tvTime.setText(getSystemTime());
                    //先移除信息
                    //然后一秒钟发送一次信息
                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;

                case HIDEMEDIACONTROLLER://隐藏控制面板
                    hidemediaController();
                    break;
            }
        }
    };
    private int position;
    private ArrayList<MediaItem> mediaItems;

    //定义手势识别器
    private GestureDetector detector;
    private int screenWidth;//屏目的宽
    private int screenHeight;//屏幕的高

    /*
    * 视频本身的宽和高
    * */
    private int VideoHeight;
    private int VideoWidth;

    /*
     * 1.得到系统时间的方法
     * */
    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        return format.format(new Date());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtil.v("onCreate", "A,onCreate======================");
        setContentView(R.layout.activity_system_player);
        ButterKnife.bind(this);

        video_player = findViewById(R.id.video_player);
        btnSwitchPlayer.setOnClickListener(this);
        btnVideoExit.setOnClickListener(this);
        btnVideoNext.setOnClickListener(this);
        btnVideoPre.setOnClickListener(this);
        btnVideoStartPause.setOnClickListener(this);
        btnVideoSwitchScreen.setOnClickListener(this);
        btnVoice.setOnClickListener(this);
//        seekbarVideo.setOnSeekBarChangeListener();

//        mediaItems =new ArrayList<MediaItem>();
        initData();
        setLisener();
        getData();
        setData();

        //设播放面板控制，是调用系统的播放面板
//        video_player.setMediaController(new MediaController(this));
    }

    //设置视频的路径
    private void setData() {
        if (mediaItems != null && mediaItems.size() > 0) {
            MediaItem mediaItem = mediaItems.get(position);
            //找到视频的路径
            video_player.setVideoPath(mediaItem.getData());
            tvName.setText(mediaItem.getName());
        } else if (uri != null) {
            video_player.setVideoURI(uri);
            tvName.setText(uri.toString());
        }
        setButtonState();
        //设置手机屏幕不锁屏/*
        // 下面两种方法都可以
        // */
        video_player.setKeepScreenOn(true);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void getData() {
        //得到一个地址，来自文件浏览器，浏览器，相册，
        uri = getIntent().getData();//返回的是uri的地址
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        position = getIntent().getIntExtra("position", 0);
    }


    public void setLisener() {
        //设置准备的监听
        //当底层解码器准备好的时候，回调这个方法
        video_player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //得到视频原始的宽和高
                VideoHeight=  mp.getVideoHeight();
                VideoWidth=mp.getVideoWidth();

                //1.得到视频的总时长和SeekBar.setMax()；
                int duration = video_player.getDuration();
                seekbarVideo.setMax(duration);//把这个时长设置为seekBar的最大时长

                tvDurationTime.setText(utils.stringForTime(duration));
                //发送消息
                handler.sendEmptyMessage(PROGRESS);
                video_player.start();//播放开始

                //开始播放时默认隐藏
                hidemediaController();

                setVideoType(DEFAULT_SCREEN);
//                video_player.setVideoSize(60,60);
            }
        });
        //播放失败的监听事件
        //播放的时候出错了，回调这个方法
        video_player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(SystemVideoPlayer.this, "播放出错了", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        //播放完成的监听
        video_player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(SystemVideoPlayer.this, "播放完成了", Toast.LENGTH_SHORT).show();
//                finish();//突出播放界面
                setNextPlay();
            }
        });
        seekbarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /*
             * 1.当我们的进度更新的时候回调这个方法
             * progress//当今进度
             * fromUser//是否是由用户引起的
             * */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                //这个为用户行为引起的，使用MediaPlayer的方法seekTo方法，来设置seekBar可以滑动调节
                if (fromUser) {
                    video_player.seekTo(progress);
                }
            }

            //当手触碰SeekBar的时候回调这个方法
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                handler.removeMessages(HIDEMEDIACONTROLLER);
            }

            //当手停止触碰SeekBar的时候回调这个方法
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                handler.sendEmptyMessageDelayed(HIDEMEDIACONTROLLER, 5000);
            }
        });
    }


    private void setVideoType(int type) {

        switch (type) {

            case FULL_SCREEN://全屏
                video_player.setVideoSize(screenWidth, screenHeight);
                isFullScreen = true;
                btnVideoSwitchScreen.setBackgroundResource(R.drawable.btn_video_switch_default_screen_selector);
                break;

            case DEFAULT_SCREEN://默认的大小
                /*
                * 真实视频本身的宽和高
                * */
                int mVideoWidth = VideoWidth;
                int mVideoHeight = VideoHeight;
                /*
                 * 要播放视频的宽和高
                 * */
                int width = screenWidth;
                int height = screenHeight;

                //进行等比例缩放的方法
                if (mVideoWidth > 0 && mVideoHeight > 0){
                    // for compatibility, we adjust size based on aspect ratio
                    if (mVideoWidth * height < width * mVideoHeight) {
                        //Log.i("@@@", "image too wide, correcting");
                        width = height * mVideoWidth / mVideoHeight;
                    } else if (mVideoWidth * height > width * mVideoHeight) {
                        //Log.i("@@@", "image too tall, correcting");
                        height = width * mVideoHeight / mVideoWidth;
                    }
                    video_player.setVideoSize(width,height);
                }

                btnVideoSwitchScreen.setBackgroundResource(R.drawable.btn_video_switch_full_screen_selector);
                isFullScreen = false;
                break;
        }
    }

    public void initData() {

        //得到屏幕的宽和高
        //这个方法是过时的
//        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
//        screenWidth= wm.getDefaultDisplay().getWidth();//得到屏幕的宽
//        screenheight= wm.getDefaultDisplay().getHeight();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
/*
1.这个方法也可以得到window的大小
* DisplayMetrics displayMetrics = new DisplayMetrics();
this . getWindowManager() .getDef aultDi splay() . getMetrics (displayMetrics);
screenWidth = di splayMetrics . widthPixels;
screenHeight = displayMetrics . heightPixels;
*/
        utils = new Utils();
        //注册电量广播事件
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        receiver = new BatteryReceiver();
        registerReceiver(receiver, intentfilter);

        //实例话手势识别器
        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                startAndPause();
                Toast.makeText(SystemVideoPlayer.this, "我被长按了", Toast.LENGTH_SHORT).show();
            }

            //            private boolean isOnDoubleTap = false;
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Toast.makeText(SystemVideoPlayer.this, "我被双击了", Toast.LENGTH_SHORT).show();

                if (isFullScreen){
                    setVideoType(DEFAULT_SCREEN);
                }else {
                    setVideoType(FULL_SCREEN);
                }
//                if (isOnDoubleTap){
//                    isOnDoubleTap =false;
//                   video_player.getLayoutParams();
//
//                }else {
//                    video_player.setVideoSize(600,550);
//                    isOnDoubleTap = true;
//                }

                return super.onDoubleTap(e);

            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (isShowMediaController) {
                    hidemediaController();
                    handler.removeMessages(HIDEMEDIACONTROLLER);
                } else {
                    showmediaController();
                    handler.sendEmptyMessageDelayed(HIDEMEDIACONTROLLER, 5000);
                }
                Toast.makeText(SystemVideoPlayer.this, "我被单击了", Toast.LENGTH_SHORT).show();
                return super.onSingleTapConfirmed(e);
            }
        });
    }

    class BatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            int level = intent.getIntExtra("level", 0);//电量0-100
            //Service是在主线程中
            setBattery(level);
        }
    }

    private void setBattery(int level) {
        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    /*
    1.是否隐藏控件面板

    false是隐藏
    true是显示
    * */
    private Boolean isShowMediaController = false;

    private void hidemediaController() {
        llBottom.setVisibility(View.GONE);
        llTop.setVisibility(View.GONE);
        isShowMediaController = false;
    }

    private void showmediaController() {
        llBottom.setVisibility(View.VISIBLE);
        llTop.setVisibility(View.VISIBLE);
        isShowMediaController = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.v("onCreate", "A,onDestroy======================");
        if (receiver != null) {
            unregisterReceiver(receiver);//取消注册监听
            receiver = null;
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.v("onCreate", "A,onRestart======================");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.v("onCreate", "A,onResume======================");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.v("onCreate", "A,onStop======================");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.v("onCreate", "A,onPause======================");
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.v("onCreate", "A,onStart======================");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //3.把事件给手势识别器解析
        detector.onTouchEvent(event);//把手势绑定到触摸事件中
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
//                Intent intent =new Intent(this, TestActivity.class);
//                startActivity(intent);
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        if (v == btnVoice) {
// Handle clicks for btnVoice
        } else if (v == btnSwitchPlayer) {
// Handle clicks for btnSwitchPlayer
        } else if (v == btnVideoExit) {
            finish();
// Handle clicks for btnVideoExit
        } else if (v == btnVideoPre) {
// Handle clicks for btnVideoPre
            setPrePlay();
        } else if (v == btnVideoStartPause) {

            startAndPause();
// Handle clicks for btnVideostartPause
        } else if (v == btnVideoNext) {
// Handle clicks for btnVideoNext
            setNextPlay();
        } else if (v == btnVideoSwitchScreen) {
            if (isFullScreen){
                setVideoType(DEFAULT_SCREEN);
            }else {
                setVideoType(FULL_SCREEN);
            }
// Handle clicks for btnvideoswi tchScreen
        }
        handler.removeMessages(HIDEMEDIACONTROLLER);
        handler.sendEmptyMessageDelayed(HIDEMEDIACONTROLLER, 5000);
    }

    private void startAndPause() {
        if (video_player.isPlaying()) {
            //暂停
            //按钮设置播放状态
            video_player.pause();
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_play_selector);
        } else {
            //播放
            //按钮设置暂停状态
            video_player.start();
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }

    private void setPrePlay() {

        if (mediaItems != null && mediaItems.size() > 0) {

            //播放上一个
            position--;
            if (position >= 0) {

                MediaItem mediaItem = mediaItems.get(position);
                video_player.setVideoPath(mediaItem.getData());//设置播放地址,开始播放
                tvName.setText(mediaItem.getName());

                setButtonState();

                if (position == 0) {
                    Toast.makeText(SystemVideoPlayer.this, "这是第一个视频了", Toast.LENGTH_SHORT).show();
                }
            }

//位置为第零个时不用退出
//        } else if (uri != null) {
//
//            //退出播放器
//            finish();
//        }
        }
    }

    private void setNextPlay() {

        if (mediaItems != null && mediaItems.size() > 0) {

            //播放下一个
            position++;
            if (position < mediaItems.size()) {

                MediaItem mediaItem = mediaItems.get(position);
                video_player.setVideoPath(mediaItem.getData());//设置播放地址,开始播放
                tvName.setText(mediaItem.getName());

                setButtonState();

                if (position == mediaItems.size() - 1) {
                    Toast.makeText(SystemVideoPlayer.this, "已经是最后一个视频了", Toast.LENGTH_SHORT).show();
                }
            } else {
                finish();//退出视频
            }


        } else if (uri != null) {

            //退出播放器
            finish();
        }
    }

    /*
     * 设置上一个按钮和下一个按钮的状态
     * */
    private void setButtonState() {

        if (mediaItems != null && mediaItems.size() > 0) {

            if (position == 0) {//第一个视频的时候
                btnVideoPre.setEnabled(false);
                btnVideoPre.setBackgroundResource(R.drawable.video_pre_gray);
            } else if (position == mediaItems.size() - 1) {//最后一个视频的时候
                btnVideoNext.setEnabled(false);//设置按钮不可点击
                btnVideoNext.setBackgroundResource(R.drawable.videonextbtn_bg);
            } else {
                btnVideoPre.setEnabled(true);
                btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                btnVideoNext.setEnabled(true);//设置按钮不可点击
                btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
            }
        } else if (uri != null) {

            btnVideoPre.setEnabled(false);
            btnVideoPre.setBackgroundResource(R.drawable.video_pre_gray);
            btnVideoNext.setEnabled(false);//设置按钮不可点击
            btnVideoNext.setBackgroundResource(R.drawable.videonextbtn_bg);

        } else {
            Toast.makeText(SystemVideoPlayer.this, "没有播放地址", Toast.LENGTH_SHORT).show();
        }
    }
}
