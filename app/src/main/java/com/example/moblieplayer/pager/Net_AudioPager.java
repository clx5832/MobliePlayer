package com.example.moblieplayer.pager;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.moblieplayer.R;
import com.example.moblieplayer.adapter.ShrotVedioAdapter;
import com.example.moblieplayer.base.BasePager;
import com.example.moblieplayer.domain.ShortVedioBean;
import com.example.moblieplayer.utils.CacheUtils;
import com.example.moblieplayer.utils.URL;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

//网络音频的页面
public class Net_AudioPager extends BasePager {

    private ListView list_view;
    /**
     * @date:2021/4/7 16:40
     * @author: 陈良项
     * @decription:数据集合
     */
    private List<ShortVedioBean.DataBean.ListBean> list;


    public Net_AudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.net_audio_pager, null);
        list_view = view.findViewById(R.id.list_view);
        return view;
    }

    @Override
    public void initData() {
        super.initData();


        /**
         *@date:2021/4/7 13:54
         *@author: 陈良项
         *@decription:缓存
         */
        String savejson = CacheUtils.getString(context, URL.VIDEO_SHORT_URL);
        if (!TextUtils.isEmpty(savejson)) {
            processData(savejson);
        }


        /**
         *@date:2021/4/7 13:49
         *@author: 陈良项
         *@decription:获取联网请求
         */
        getDataFromNet();
    }

    private void getDataFromNet() {
        /**
         *@date:2021/4/7 13:49
         *@author: 陈良项
         *@decription:利用xutils联网请求
         */
        RequestParams params = new RequestParams(URL.VIDEO_SHORT_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("result=======" + result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

                LogUtil.e("onError=======" + ex.getMessage());

            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled=======" + cex.getMessage());

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * @date:2021/4/7 13:56
     * @author: 陈良项
     * @decription:解析数据用的和显示数据用的
     */
    private void processData(String json) {
        ShortVedioBean listBean = parseJson(json);

        list = listBean.getData().getList();
        if (list != null && list.size() > 0){

            list_view.setAdapter(new ShrotVedioAdapter(context,list));
        }else {
            Toast.makeText( context, "没有得到数据", Toast.LENGTH_SHORT ).show();

        }
    }

    /**
     * @date:2021/4/7 16:24
     * @author: 陈良项
     * @decription:解析数据用的
     */
    private ShortVedioBean parseJson(String json) {
        Gson gson = new Gson();
        ShortVedioBean searBean = gson.fromJson(json, ShortVedioBean.class);
        return searBean;
    }
}
