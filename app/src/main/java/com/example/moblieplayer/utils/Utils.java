package com.example.moblieplayer.utils;

import android.content.Context;
import android.net.TrafficStats;
import android.os.Message;

import java.util.Formatter;
import java.util.Locale;

//将毫秒转换成正常时间显示的模式
public class Utils {
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;

    public Utils() {
//转换成字符串的时间
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    }

    /**
     * 把毫秒转换成: 1:20:30这里形式
     *
     * @param timeMs
     * @return
     */
    public String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d :%02d:%02d", hours, minutes, seconds)
                    .toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();

        }

    }

    /*
     * 判断是否是网络的资源
     * */
    public boolean isNetUri(String uri) {

        boolean result = false;
        if (uri != null) {
            /*
             * toLowerCase是变化为小写的意思
             * mms短信信息的类型
             * http超文本传输协议
             * rtsp实时流协议
             * */
            if (uri.toLowerCase().startsWith("rtsp") || uri.toLowerCase().startsWith("mms")
                    || uri.toLowerCase().startsWith("http")) {
                result = true;
            }
        }
        return result;
    }

    //获取网速的方法
    /*
    * 得到网络速度的方法
    * */
    public String getshowNetSpeed(Context context) {
        long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) ==
                TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换
        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;


        String speedStr = String.valueOf(speed) + " kb/s";
        System.out.println(speed+"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++speed");

        return speedStr;
    }
}
