package com.example.moblieplayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
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

    //**********************************************************************
//    public float startY;//记录手指按下的Y坐标
//    public float startX;//记录手指按下的X坐标
//    public int downVal;//记录手指按下的音量
    public Vibrator vibrator;//手机振动器

    //***********************************************************************
    /*定义Screen类中的对象*/
//    private Screen screen = new Screen(this);
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
    private AudioManager am;
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
                    tv_light.setVisibility(View.GONE);
                    tv_show.setVisibility(View.GONE);
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
    //当前的音量，音量范围0-15
    private int currentVolume;
    //最大的音量
    private int maxVolume;
    private boolean isMute = false;
    private float mBrightness;
    private int intScreenBrightness;

    private TextView tv_show;
    private TextView tv_light;

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

        tv_show = findViewById(R.id.tv_show);
        tv_light = findViewById(R.id.tv_light);
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
                VideoHeight = mp.getVideoHeight();
                VideoWidth = mp.getVideoWidth();

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

        seekbarVoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
//                    seekbarVoice.setTO
                    updateVolumeProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                //设置控件消失的
                handler.removeMessages(HIDEMEDIACONTROLLER);
            }

            //手离开触摸屏幕时
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                handler.sendEmptyMessageDelayed(HIDEMEDIACONTROLLER, 5000);
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

    private void updateVolumeProgress(int volume) {
        am.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);//取消掉系统的声音控件的
        seekbarVoice.setProgress(volume);
        currentVolume = volume;
        if (volume <= 0) {
            isMute = true;
        } else {
            isMute = false;
        }
    }

    /*
     * 根据音量传入的值修改音量
     * */
    private void updateVolume(int volume) {

        if (isMute) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);//取消掉系统的声音控件的
            seekbarVoice.setProgress(0);
        } else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);//取消掉系统的声音控件的
            seekbarVoice.setProgress(volume);
            currentVolume = volume;
        }
//        am.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 1);//显示系统的控件
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
                if (mVideoWidth > 0 && mVideoHeight > 0) {
                    // for compatibility, we adjust size based on aspect ratio
                    if (mVideoWidth * height < width * mVideoHeight) {
                        //Log.i("@@@", "image too wide, correcting");
                        width = height * mVideoWidth / mVideoHeight;
                    } else if (mVideoWidth * height > width * mVideoHeight) {
                        //Log.i("@@@", "image too tall, correcting");
                        height = width * mVideoHeight / mVideoWidth;
                    }
                    video_player.setVideoSize(width, height);
                }

                btnVideoSwitchScreen.setBackgroundResource(R.drawable.btn_video_switch_full_screen_selector);
                isFullScreen = false;
                break;
        }
    }

    public void initData() {
        //实例化AudioManager
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        //得到当前的音量
        currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        //得到最大音量
        maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        //设置声音的seekBar的最大值
        seekbarVoice.setMax(maxVolume);
        //设置默认值,即当前的进度
        seekbarVoice.setProgress(currentVolume);
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

                if (isFullScreen) {
                    setVideoType(DEFAULT_SCREEN);
                } else {
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

    //*****************************************************************************************
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
////        /*的到屏幕的宽和搞得方法*/
////        DisplayMetrics displayMetrics = new DisplayMetrics();
////        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
////        screenWidth = displayMetrics.widthPixels;
////        screenHeight = displayMetrics.heightPixels;
//
//        /*
//         * 得到声音的最大或者最小值
//         * */
////        //实例化AudioManager
////        am = (AudioManager) getSystemService(AUDIO_SERVICE);
////        //得到当前的音量
////        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
////        //得到最大音量
////        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//        /*
//         * 把事件传递给手势识别器
//         * 对事件进行了解析处理，没有拦截，解析成手势识别的单击和双击
//         * */
//        detector.onTouchEvent(event);
//
//        switch (event.getAction()) {
//
//            case MotionEvent.ACTION_DOWN://手触碰到屏幕的时候
//                startX = event.getX();//获取初始的位置
//                startY = event.getY();
//
//                downVal = am.getStreamVolume(AudioManager.STREAM_MUSIC);
//                handler.removeMessages(HIDEMEDIACONTROLLER);
////                showmediaController();
//                break;
//
//            case MotionEvent.ACTION_MOVE://手势移动
//                float endY = event.getY();
//                float distanceY = startY - endY;//移动的距离=开始的减去后面
//                int touchRang = Math.min(screenWidth, screenHeight);
//                //判断
//                if (startY > screenWidth / 2) {
//
//                    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
////                    2.得到新的x轴
//               float endX = event.getX();
//
//                float distanceX = startX - endX;
//                //2.来到新的坐标
////                float endY = event.getY();
//                //3.计算偏移量
////                float distanceY = startY - endY;
//                //4.屏幕滑动的距离： 总距离 =改变的声音：最大的音量
//                float changeVolume =(distanceY / touchRang) *maxVolume;
//                //5.最终的声音 = 原来的音量 + 改变的声音
//                float endvolume = Math.min(Math.max(downVal + changeVolume,0),maxVolume);
//
//
//                if (changeVolume !=0){
//                    updateVolumeProgress((int) endvolume);
//                }
//                    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//                    //屏幕右半部分上滑，声音变大，下滑，声音变小
//
////                    int curvol = (int) (downVal + (distanceY / touchRang) * maxVolume);//考虑到横竖屏切换的问题
////
////                    int volume = Math.min(Math.max(0, curvol), maxVolume);
////                    //更新声音
////                    updateVolume(volume);
//                } else {
//                    //屏幕左半部分上滑，亮度变大，下滑亮度变小
//                    final double FLING_MIN_DISTANCE = 0.5;//距离
//                    final double FLING_MIN_VELOCITY = 0.5;//速度
//                    /*Math.abs返回绝对值的意思
//                     * */
//                    if (distanceY > FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
//                        setBrightness(20);
//                    }
//                    if (distanceY < FLING_MIN_DISTANCE && Math.abs(distanceY) < FLING_MIN_VELOCITY) {
//                        setBrightness(-20);
//                    }
//                }
//                break;
//
//            case MotionEvent.ACTION_UP:
//                handler.sendEmptyMessageDelayed(HIDEMEDIACONTROLLER, 5000);
//                break;
//        }
//        return super.onTouchEvent(event);
//    }
//

    public void setBrightness(float brightness) {
//        screenBrightness_check();//关闭系统自动调节的亮度
//        //不让屏幕全暗
//        if (brightness <= 1) {
//            brightness = 1;
//        }
//        //设置当前activity的屏幕亮度
//        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
//        //0到1,调整亮度暗到全亮
//        lp.screenBrightness = Float.valueOf(brightness / 255f);
//        this.getWindow().setAttributes(lp);
//
//        //保存为系统亮度方法1
//        android.provider.Settings.System.putInt(getContentResolver(),
//                android.provider.Settings.System.SCREEN_BRIGHTNESS,
//                (int) brightness);

        //保存为系统亮度方法2
//        Uri uri = android.provider.Settings.System.getUriFor("screen_brightness");
//        android.provider.Settings.System.putInt(getContentResolver(), "screen_brightness", brightness);
//        // resolver.registerContentObserver(uri, true, myContentObserver);
//        getContentResolver().notifyChange(uri, null);

        //99999999999999999999999999999999999999999999999999999999999999999999999
//        if (mBrightness < 0) {
//            mBrightness = getWindow().getAttributes().screenBrightness;
//            if (mBrightness <= 0.00f)
//                mBrightness = 0.50f;
//            if (mBrightness < 0.01f)
//                mBrightness = 0.01f;
//        }
//        WindowManager.LayoutParams lpa = getWindow().getAttributes();
//        lpa.screenBrightness = mBrightness + (endY - startY) / screenHeight;
//        if (lpa.screenBrightness > 1.0f)
//            lpa.screenBrightness = 1.0f;
//        else if (lpa.screenBrightness < 0.01f)
//            lpa.screenBrightness = 0.01f;
//        getWindow().setAttributes(lpa);

        //99999999999999999999999999999999999999999999999999999999999999999999999


//        WindowManager.LayoutParams lp = getWindow().getAttributes();//获取窗口的属性
//
//        lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;//得到的屏幕亮度 =得到的屏幕亮度 + 亮度/255.0f
//        if (lp.screenBrightness > 1) {
//
//            lp.screenBrightness = 1;//设置屏幕为最亮的
//            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);//设置震动
//
//            long[] pattern = {10, 200};//OFF/ON/OFF/ON...关闭10秒震动200毫秒，不停切换
//
//            vibrator.vibrate(pattern, -1);//要添加权限
//
//        } else if (lp.screenBrightness < 0.2) {
//            lp.screenBrightness = (float) 0.2;
//
//            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);//设置震动
//
//            long[] pattern = {10, 200};//OFF/ON/OFF/ON...关闭10秒震动200毫秒，不停切换
//
//            vibrator.vibrate(pattern, -1);//要添加权限
//        }
//        getWindow().setAttributes(lp);

        //0000000000000000000000000000000000000000000000000000000000000000
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
        } else if (lp.screenBrightness < 0.0) {
            lp.screenBrightness = (float) 0.0;
        }
        float sb = lp.screenBrightness;
        tv_show.setText((int) Math.ceil(sb * 100) + "%");
        getWindow().setAttributes(lp);
        //0000000000000000000000000000000000000000000000000000000000000000
    }

    //******************************************************************************************
    private float startY;
    private float touchRang;//滑动距离
    private int mVolume;//当前的音量
    private float startX;
    private float touchRangX;
    private float distanceY;
    private float endY;
    private float endX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //3.把事件给手势识别器解析
        detector.onTouchEvent(event);//把手势绑定到触摸事件中
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                //11111111111111111111111111111111111111111111111
                startX = event.getX();
                //111111111111111111111111111111111111111111111111

                //1.按下时，记录初始值
                startY = event.getY();


                touchRang = Math.min(screenHeight, screenWidth);//滑动的距离
                mVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                handler.removeMessages(HIDEMEDIACONTROLLER);
                break;

            case MotionEvent.ACTION_MOVE:

                endX = event.getX();
                //*****************************************************************************
                /*
                * 实现视频进度快进的代码
                * */
                float distanceX = endX - startX;
                float deltailX = (distanceX / screenWidth) * seekbarVideo.getMax();
                deltailX = distanceX / 1;
                int progress = (int) Math.min(Math.max(deltailX + seekbarVideo.getProgress(),0),seekbarVideo.getMax());
                seekbarVideo.setProgress(progress);
                video_player.seekTo(progress);
                handler.removeMessages(HIDEMEDIACONTROLLER);
                handler.sendEmptyMessageDelayed(HIDEMEDIACONTROLLER,5000);
//                tv_show.setVisibility(View.VISIBLE);
//                tv_show.setText("进度："+progress);
//                tv_light.setText("进度：");
                showmediaController();
                //*****************************************************************************

                endY = event.getY();
                //3.计算偏移量
                distanceY = startY - endY;
                if (startX > (screenWidth / 2)) {

                    //原来的+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    //
//                //2.得到新的x轴
//               float endX = event.getX();
//
////                float distanceX = startX - endX;
                    //2.来到新的坐标

                    //4.屏幕滑动的距离： 总距离 =改变的声音：最大的音量
                    float changeVolume = (distanceY / touchRang) * maxVolume;
                    //5.最终的声音 = 原来的音量 + 改变的声音
                    float endvolume = Math.min(Math.max(mVolume + changeVolume, 0), maxVolume);


                    if (changeVolume != 0) {
                        updateVolumeProgress((int) endvolume);
                    }
                    //原来的+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

                    //添加的********************************************************************
                } else {


                    tv_light.setVisibility(View.VISIBLE);
                    tv_show.setVisibility(View.VISIBLE);
                    //屏幕左半部分上滑，亮度变大，下滑亮度变小
                    final double FLING_MIN_DISTANCE = 0.5;//距离
                    final double FLING_MIN_VELOCITY = 0.5;//速度
                    /*Math.abs返回绝对值的意思
                     * */
                    if (distanceY > FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
                        setBrightness(20);
                    }
                    if (distanceY < FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
                        setBrightness(-20);
                    }

                    //88888888888888888888888888888888888888888888888888888888888888888888888888888


                    //88888888888888888888888888888888888888888888888888888888888888888888888888888

                }
                //添加的********************************************************************
                break;

            case MotionEvent.ACTION_UP:

                handler.sendEmptyMessageDelayed(HIDEMEDIACONTROLLER, 5000);
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        if (v == btnVoice) {
            isMute = !isMute;
            updateVolume(currentVolume);
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
            if (isFullScreen) {
                setVideoType(DEFAULT_SCREEN);
            } else {
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

    /*
     * 这个是关掉系统自动调节亮度的方法
     * */
    private void screenBrightness_check() {
        //先关闭系统的亮度自动调节
        try {
            if (android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE) == android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                android.provider.Settings.System.putInt(getContentResolver(),
                        android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE,
                        android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch (Settings.SettingNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //获取当前亮度,获取失败则返回255
        intScreenBrightness = (int) (android.provider.Settings.System.getInt(getContentResolver(),
                android.provider.Settings.System.SCREEN_BRIGHTNESS,
                255));

    }

    //设置手机音量摁键控件调节音量变化
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

            currentVolume--;
            updateVolumeProgress(currentVolume);
            showmediaController();
            handler.removeMessages(HIDEMEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDEMEDIACONTROLLER, 5000);
            return true;//一定要写，不然出现系统同时执行，自己也执行，返回true不让系统的弹出来
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            currentVolume++;
            updateVolumeProgress(currentVolume);
            showmediaController();
            handler.removeMessages(HIDEMEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDEMEDIACONTROLLER, 5000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}