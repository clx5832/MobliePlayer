package com.example.moblieplayer.pager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.moblieplayer.R;
import com.example.moblieplayer.activity.SystemVideoPlayer;
import com.example.moblieplayer.base.BasePager;
//import com.example.moblieplayer.data.BiBi;
import com.example.moblieplayer.domain.MediaItem;

import com.example.moblieplayer.utils.CacheUtils;
import com.example.moblieplayer.utils.URL;
import com.example.moblieplayer.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//网络视频的页面
public class Net_VideoPager extends BasePager {


    private ProgressBar pb_loading;

    private TextView tv_nomedia;

    private XListView lv_video_pager;

    private ArrayList<MediaItem> mediaItems;
//    private ArrayList<MediaItem>mediaItems2;

    private MyNetVideoAdaper myNetVideoAdaper;

//    private BiBi  biBi;

    //    private BiBi biBi;
    public Net_VideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.net_video_pager, null);
        pb_loading = view.findViewById(R.id.pb_loading);

        tv_nomedia = view.findViewById(R.id.tv_nomedia);

        lv_video_pager = view.findViewById(R.id.lv_video_pager);

        lv_video_pager.setPullLoadEnable(true);

        lv_video_pager.setOnItemClickListener(new MyOnItemClickListener());

//        lv_video_pager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                  biBi =new BiBi();
////               MediaItem mediaItem= mediaItems2.get(position);
//
//                //隐式意图，通过匹配调用合适的Activity
////                Intent intent=new Intent(context, SystemVideoPlayer.class);
////                intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
//
////                Intent intent=new Intent(context, SystemVideoPlayer.class);
////                intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
////                context.startActivity(intent);
//
//                Intent intent = new Intent(context, SystemVideoPlayer.class);
////                intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("videolist", mediaItems);
//                intent.putExtras(bundle);
//                intent.putExtra("position", position);
//                context.startActivity(intent);
//
//
//            }
//        });

        lv_video_pager.setXListViewListener(new MyXListViewListener());

        return view;


    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(context, SystemVideoPlayer.class);
//                intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist", mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position", position - 1);//减一的意思是，XListView刷新时，
            // 点击会点击到下一个的视频，而不是当前的视频，减一的话，就是回到当前的视频
            context.startActivity(intent);
        }
    }

    private void onLoad() {
        lv_video_pager.stopRefresh();
        lv_video_pager.stopLoadMore();
        lv_video_pager.setRefreshTime(getSystemTime());
    }

    class MyXListViewListener implements XListView.IXListViewListener {

        @Override
        public void onRefresh() {//刷新，就是重新请求数据
            getDataFromNet();
            onLoad();//解析数据好之后，调用这个方法
        }

        @Override
        public void onLoadMore() {
            getMoreDataFromNet();
        }
    }

    private void getMoreDataFromNet() {
        RequestParams params = new RequestParams(URL.NET_VIDEO_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.i("联网请求成功===" + result);

                //联网请求成功后,设置缓存
                CacheUtils.putString(context, URL.NET_VIDEO_URL, result);
                System.out.println("++++++++++++++++++++++++++++" + result);

                //解析数据
                parseMoreData(result);

                myNetVideoAdaper.notifyDataSetChanged();//刷新数据
                onLoad();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.i("联网请求失败===");
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.i("onCancelled===");
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished===");
            }
        });
    }

    /*
    * 解析更多的数据
    * 上拉刷新的时候
    * */
    private void parseMoreData(String json) {
        try {

            JSONObject object = new JSONObject(json);
            JSONArray jsonArray = object.optJSONArray("movice");
            //遍历
            for (int i = 0; i < jsonArray.length(); i++) {


                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                System.out.println("-----------------------------" + jsonObject);
                //判断视频是否为空
                if (jsonObject != null) {
                    MediaItem mediaItem = new MediaItem();
//                    BiBi biBi = new BiBi();
                    mediaItems.add(mediaItem);//把内容添加到集合中
                    String pic = jsonObject.getString("pic");//图片网址
                    mediaItem.setPic(pic);//图片网址
                    String redirect_url = jsonObject.optString("redirect_url");//视频网址
                    System.out.println("=========================================" + redirect_url);
                    mediaItem.setData(redirect_url);

                    String title = jsonObject.getString("title");
                    mediaItem.setName(title);
                    String desc = jsonObject.optString("desc");//描述
                    mediaItem.setDesc(desc);
                }
            }
//            object.optString("bmsg");
//            object.get("bmsg");//这个方法不好
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
     * 1.得到系统时间的方法
     * */
    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        return format.format(new Date());
    }

    @Override
    public void initData() {
        super.initData();
        System.out.println("textView.setText(\"这是网络视频\")初始化了;====================");
        getDataFromNet();
        /*
         * 获取保存的信息,即缓存的信息
         * */
        String saveJson = CacheUtils.getString(context, URL.NET_VIDEO_URL);

        //如果储存的不为空，则解析
        if (!TextUtils.isEmpty(saveJson)) {
            processData(saveJson);
        }

    }


    /*
     * 联网请求数据
     * */
    private void getDataFromNet() {

        RequestParams params = new RequestParams(URL.NET_VIDEO_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.i("联网请求成功===" + result);

                //联网请求成功后,设置缓存
                CacheUtils.putString(context, URL.NET_VIDEO_URL, result);
                System.out.println("++++++++++++++++++++++++++++" + result);

                //解析数据
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.i("联网请求失败===");
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.i("onCancelled===");
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished===");
            }
        });
    }

    private void processData(String json) {

        /*解析数据：1.手动解析(系统的接口)
        2.用第三方解析工具：gson和fastjson(阿里的解析工具)
        * */

        parseJson(json);

        if (mediaItems != null && mediaItems.size() > 0) {

            tv_nomedia.setVisibility(View.GONE);
            /*
             * 设置适配器
             * */
            myNetVideoAdaper = new MyNetVideoAdaper();
            lv_video_pager.setAdapter(myNetVideoAdaper);


        } else {
            tv_nomedia.setVisibility(View.VISIBLE);
        }
        pb_loading.setVisibility(View.GONE);


    }


    class MyNetVideoAdaper extends BaseAdapter {

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
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_net_video_pager, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_icon = convertView.findViewById(R.id.iv_icon);
                viewHolder.tv_name = convertView.findViewById(R.id.tv_name);
                viewHolder.tv_description = convertView.findViewById(R.id.tv_description);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //根据数据得到相应的数据
            MediaItem mediaItem = mediaItems.get(position);
//            mediaItem.setName(mediaItem.getName());//设置名字
//            mediaItem.setShortTitle(mediaItem.getShortTitle());//设置短标题
//            mediaItem.setVpic(mediaItem.getVpic());//设置图片
            viewHolder.tv_name.setText(mediaItem.getName());//标题名字
            viewHolder.tv_description.setText(mediaItem.getDesc());//得到详情

            //请求图片:Xutils3和Glide都支持缓存
//            x.image().bind(viewHolder.iv_icon, mediaItem.getPic());//得到图片

            //使用Glide请求图片
            Glide.with(context).load(mediaItem.getPic()).
                    diskCacheStrategy(DiskCacheStrategy.ALL).//图片的缓存
                    placeholder(R.drawable.video_default_icon).//网络加载时默认显示的图片
                    error(R.drawable.video_default_icon).//图片加载失败时也是加载这个
                    into(viewHolder.iv_icon);
            return convertView;//返回视图
        }
    }

    static class ViewHolder {

        ImageView iv_icon;
        TextView tv_name;
        TextView tv_description;
    }

    private void parseJson(String json) {
//        JSONObject 是系统自带的
         /*解析数据：1.手动解析(系统的接口)
        2.用第三方解析工具：gson和fastjson(阿里的解析工具)
        * */
        try {
            mediaItems = new ArrayList<>();
//            mediaItems2=new ArrayList<>();
            JSONObject object = new JSONObject(json);
            JSONArray jsonArray = object.optJSONArray("movice");
            //遍历
            for (int i = 0; i < jsonArray.length(); i++) {


                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                System.out.println("-----------------------------" + jsonObject);
                //判断视频是否为空
                if (jsonObject != null) {
                    MediaItem mediaItem = new MediaItem();
//                    BiBi biBi = new BiBi();
                    mediaItems.add(mediaItem);//把内容添加到集合中
                    String pic = jsonObject.getString("pic");//图片网址
                    mediaItem.setPic(pic);//图片网址
                    String redirect_url = jsonObject.optString("redirect_url");//视频网址
                    System.out.println("=========================================" + redirect_url);
                    mediaItem.setData(redirect_url);

                    String title = jsonObject.getString("title");
                    mediaItem.setName(title);
                    String desc = jsonObject.optString("desc");//描述
                    mediaItem.setDesc(desc);


//                    mediaItem.add(mediaItem);

                }
            }
//            object.optString("bmsg");
//            object.get("bmsg");//这个方法不好
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
