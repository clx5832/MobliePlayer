package com.example.moblieplayer.pager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.moblieplayer.R;
import com.example.moblieplayer.activity.AudioPlayer;
import com.example.moblieplayer.activity.SystemVideoPlayer;
import com.example.moblieplayer.base.BasePager;
import com.example.moblieplayer.domain.MediaItem;
import com.example.moblieplayer.utils.Utils;

import java.util.ArrayList;

//本地音频的页面
public  class AudioPager extends BasePager {

    private AudioPager.ViewHolder viewHolder;

    private ProgressBar pb_loading;

    private TextView tv_nomedia;

    private ListView lv_video_pager;

    private Handler handler;

    //转换时间的类
    private Utils utils;
    //新建一个集合
    private ArrayList<MediaItem> mediaItems;

    public AudioPager(Context context) {

        super(context);
        this.context = context;

        utils = new Utils();
    }

    @Override
    public View initView() {

        View view = View.inflate(context, R.layout.audio_pager, null);
//
        pb_loading = view.findViewById(R.id.pb_loading);

        tv_nomedia = view.findViewById(R.id.tv_nomedia);

        lv_video_pager = view.findViewById(R.id.lv_video_pager);

        lv_video_pager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MediaItem mediaItem = mediaItems.get(position);


                Intent intent = new Intent(context, AudioPlayer.class);
                /*不传列表，只传位置
                列表在服务里加载
                * */
                intent.putExtra("position", position);//播放列表中的某个音频
                context.startActivity(intent);


            }
        });
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        System.out.println("textView.setText(\"这是本地音乐\");初始化了=====================");

        getData();//获取本地音乐的方法
    }


    public void getData() {

        //子线程
        new Thread() {

            @Override
            public void run() {
                super.run();

                SystemClock.sleep(100);//系统时钟睡眠2秒(这里是子线程)
                mediaItems = new ArrayList<MediaItem>();

                ContentResolver contentResolver = context.getContentResolver();
//                Uri uri = Uri.parse("content://media/external/video/media/");
                String[] s = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//音乐的名称
                        MediaStore.Audio.Media.DURATION,//音乐总时长
                        MediaStore.Audio.Media.SIZE,//音乐的大小
                        MediaStore.Audio.Media.DATA,//音乐的绝对地址
                        MediaStore.Audio.Media.ARTIST//音乐艺术家
                };
                Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, s, null, null, null);

                if (cursor != null) {

                    while (cursor.moveToNext()) {

                        MediaItem mediaItem = new MediaItem();

                        String name = cursor.getString(0);
                        mediaItem.setName(name);

                        long duration = cursor.getLong(1);
                        mediaItem.setDuration(duration);

                        long size = cursor.getLong(2);
                        mediaItem.setSize(size);

                        String data = cursor.getString(3);

                        mediaItem.setData(data);

                        String artist = cursor.getString(4);
                        mediaItem.setArtist(artist);

                        mediaItems.add(mediaItem);

                    }
                    cursor.close();
                }

                handler.sendEmptyMessage(0);
            }
        }.start();
        handler = new Handler() {

            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                if (msg.what == 0) {

                    //如果数组表中不为空
                    if (mediaItems != null && mediaItems.size() > 0) {

                        tv_nomedia.setVisibility(View.GONE);
                        pb_loading.setVisibility(View.GONE);

                        lv_video_pager.setAdapter(new VideoPagerAdapatr());

                    } else {
                        tv_nomedia.setVisibility(View.VISIBLE);
                        pb_loading.setVisibility(View.GONE);
                    }
                }
            }
        };
    }

    private class VideoPagerAdapatr extends BaseAdapter {
        @Override
        public int getCount() {
            return mediaItems.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            viewHolder = new ViewHolder();

            if (convertView == null) {
                convertView = View.inflate(context, R.layout.video_pager_item, null);
                viewHolder.iv_icon =convertView.findViewById(R.id.iv_icon);
                viewHolder.tv_name = convertView.findViewById(R.id.tv_name);
                viewHolder.tv_duration = convertView.findViewById(R.id.tv_duration);
                viewHolder.tv_size = convertView.findViewById(R.id.tv_size);
                viewHolder.iv_icon.setImageResource(R.drawable.phone_category_music_selected);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            MediaItem mediaItem = mediaItems.get(position);
            viewHolder.tv_name.setText(mediaItem.getName());
            viewHolder.tv_size.setText(Formatter.formatFileSize(context, mediaItem.getSize()));
            viewHolder.tv_duration.setText(utils.stringForTime((int) mediaItem.getDuration()));
            System.out.println(utils.stringForTime((int) mediaItem.getDuration()) + "++++++++++++++++++++++++++++++++++++++++++++");
            return convertView;
        }


    }

    static class ViewHolder {

        ImageView iv_icon;
        TextView tv_name;
        TextView tv_duration;
        TextView tv_size;
}
}
