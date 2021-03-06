package com.example.moblieplayer.pager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.moblieplayer.R;
import com.example.moblieplayer.SystemVideoPlayer;
import com.example.moblieplayer.base.BasePager;
import com.example.moblieplayer.domain.MediaItem;
import com.example.moblieplayer.utils.Utils;

import java.util.ArrayList;

//本地视频的页面
public class VideoPager extends BasePager {


    private ViewHolder viewHolder;

    private ProgressBar pb_loading;

    private TextView tv_nomedia;

    private ListView lv_video_pager;

    private Handler handler;

    //转换时间的类
    private Utils utils;
    //新建一个集合
    private ArrayList<MediaItem> mediaItems;

    public VideoPager(Context context) {

        super(context);
        this.context =context;

        utils = new Utils();
    }

    @Override
    public View initView() {

        View view = View.inflate(context, R.layout.video_pager, null);
//
        pb_loading = view.findViewById(R.id.pb_loading);

        tv_nomedia = view.findViewById(R.id.tv_nomedia);

        lv_video_pager = view.findViewById(R.id.lv_video_pager);

        lv_video_pager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MediaItem mediaItem=mediaItems.get(position);

                //隐式意图，通过匹配调用合适的Activity
//                Intent intent=new Intent(context, SystemVideoPlayer.class);
//                intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");

//                Intent intent=new Intent(context, SystemVideoPlayer.class);
//                intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
//                context.startActivity(intent);

                Intent intent=new Intent(context, SystemVideoPlayer.class);
//                intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
                Bundle bundle = new Bundle();
                bundle.putSerializable("videolist",mediaItems);
                intent.putExtras(bundle);
                intent.putExtra("position",position);
                context.startActivity(intent);


            }
        });
//
//        lv_video_pager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                MediaItem mediaItem = mediaItems.get(position);
//                Intent intent = new Intent();
//                intent.setDataAndType(Uri.parse(mediaItem.getData()), "video/*");
//                context.startActivity(intent);
//            }
//        });
//
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        System.out.println("textView.setText(\"这是本地视频\");初始化了=====================");

        getData();//获取本地视频的方法
    }


    public void getData() {

        //子线程
        new Thread(){

            @Override
            public void run() {
                super.run();

                SystemClock.sleep(3000);//系统时钟睡眠2秒(这里是子线程)
                mediaItems =new ArrayList<MediaItem>();

                ContentResolver contentResolver = context.getContentResolver();
//                Uri uri = Uri.parse("content://media/external/video/media/");
                String[] s ={
                        MediaStore.Video.Media.DISPLAY_NAME,//视频的名称
                        MediaStore.Video.Media.DURATION,//视频总时长
                        MediaStore.Video.Media.SIZE,//视频的大小
                        MediaStore.Video.Media.DATA//视频的绝对地址

                };
              Cursor cursor =  contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,s,null,null,null);

              if (cursor !=null){

                  while (cursor.moveToNext()){

                      MediaItem mediaItem =new MediaItem();

                      String name =cursor.getString(0);
                      mediaItem.setName(name);

                      long duration=cursor.getLong(1);
                      mediaItem.setDuration(duration);

                      long size =cursor.getLong(2);
                      mediaItem.setSize(size);

                      String data =cursor.getString(3);
                      mediaItem.setData(data);

                    mediaItems.add(mediaItem);

                  }
                  cursor.close();
              }

                handler.sendEmptyMessage(0);
            }
        }.start();
        handler =new Handler(){

            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                if (msg.what == 0){

                    //如果数组表中不为空
                    if (mediaItems !=null &&mediaItems.size() > 0){

                        tv_nomedia.setVisibility(View.GONE);
                        pb_loading.setVisibility(View.GONE);

                        lv_video_pager.setAdapter(new VideoPagerAdapatr() );

                    }else {
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

            if (convertView == null){
                convertView =View.inflate(context,R.layout.video_pager_item,null);
                viewHolder.tv_name =convertView.findViewById(R.id.tv_name);
                viewHolder.tv_duration=convertView.findViewById(R.id.tv_duration);
                viewHolder.tv_size=convertView.findViewById(R.id.tv_size);
                convertView.setTag(viewHolder);
            }else {
               viewHolder = (ViewHolder) convertView.getTag();
            }

            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            MediaItem mediaItem =mediaItems.get(position);
            viewHolder.tv_name.setText(mediaItem.getName());
            viewHolder.tv_size.setText(Formatter.formatFileSize(context,mediaItem.getSize()));
            viewHolder.tv_duration.setText(utils.stringForTime((int) mediaItem.getDuration()));
            System.out.println(utils.stringForTime((int) mediaItem.getDuration())+"++++++++++++++++++++++++++++++++++++++++++++");
            return convertView;
        }


    }
    static class ViewHolder{

        TextView tv_name;
        TextView tv_duration;
        TextView tv_size;
    }


//    public Handler handler = new Handler() {
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            //主线程中设置适配器
//            //如果有数据和不为空的时候
//
//
//            if (mediaItems != null && mediaItems.size() > 0) {
//
//                tv_nomedia.setVisibility(View.GONE);//设置不可见
//
//                pb_loading.setVisibility(View.GONE);
//
//                //设置适配器
//                lv_video_pager.setAdapter(new VideoPagerAdapter());
//
//            } else {
//
//                tv_nomedia.setVisibility(View.VISIBLE);
//
//                pb_loading.setVisibility(View.GONE);
//
//            }
//
//            super.handleMessage(msg);
//
//        }
//    };

    //获取本地视频的方法
//    private void getData() {
//
//
//        //一定要在使用前new出来
//
//        mediaItems = new ArrayList<MediaItem>();
//        //子线程
//
//        new Thread() {
//            @Override
//            public void run() {
//
//                super.run();
//
//
//                ContentResolver contentResolver = context.getContentResolver();
//
//               Uri uriMatcher =MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//
//
//                String[] objects = {MediaStore.Video.Media.DISPLAY_NAME,//在SD卡显示的名称
//
//                        MediaStore.Video.Media.DURATION,// 显示视频的长度
//
//                        MediaStore.Video.Media.SIZE,//视频的大小
//
//                        MediaStore.Video.Media.DATA//视频的绝对地址
//                };
//
//              Cursor cursor =contentResolver.query(uriMatcher,objects,null,null,null);
//                if (cursor != null) {
//
//                    while (cursor.moveToNext()) {
//
//                        MediaItem mediaItem = new MediaItem();
//
//                        String name = cursor.getString(0);
//                        System.out.println("name=" + name + "=================================");
//                        mediaItem.setName(name);
//
//                        long duration = cursor.getLong(1);
//                        System.out.println("duration=" + duration + "=================================");
//                        mediaItem.setDuration(duration);
//
//                        long size = cursor.getLong(2);
//                        System.out.println("size=" + size + "=====================================");
//                        mediaItem.setSize(size);
//
//                        String data = cursor.getString(3);
//                        System.out.println("data=" + data + "=================================");
//                        mediaItem.setData(data);
//
//                        mediaItems.add(mediaItem);
//
//                    }
//
//                    cursor.close();
//
//                }

//                handler.sendEmptyMessage(0);

            }

//        }.start();
//
//    }


//    private class VideoPagerAdapter extends BaseAdapter {
//
//        @Override
//        public int getCount() {
//
//            return mediaItems.size();
//
//        }
//
//        @Override
//        public Object getItem(int position) {
//
//            return null;
//
//        }
//
//        @Override
//        public long getItemId(int position) {
//
//            return 0;
//
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            ViewHolder viewHolder;
//
//            if (convertView == null) {
//
//                convertView = View.inflate(context, R.layout.video_pager_item, null);
//
//                viewHolder = new ViewHolder();
//
//                viewHolder.tv_name = convertView.findViewById(R.id.tv_name);
//
//                viewHolder.tv_duration = convertView.findViewById(R.id.tv_duration);
//
//                viewHolder.tv_size = convertView.findViewById(R.id.tv_size);
//
//                convertView.setTag(viewHolder);//设置标签
//            } else {
//
//                viewHolder = (ViewHolder) convertView.getTag();
//
//            }
//
//            MediaItem mediaItem = mediaItems.get(position);
//
//            viewHolder.tv_name.setText(mediaItem.getName());
//
//            viewHolder.tv_size.setText(Formatter.formatFileSize(context, mediaItem.getSize()));//得到文件的大小，即格式化
//
//            viewHolder.tv_duration.setText(utils.stringForTime((int) mediaItem.getDuration()));
//
//            return convertView;
//        }
//    }
//
//    static class ViewHolder {
//
//        TextView tv_name;
//
//        TextView tv_duration;
//
//        TextView tv_size;
//    }

//}
