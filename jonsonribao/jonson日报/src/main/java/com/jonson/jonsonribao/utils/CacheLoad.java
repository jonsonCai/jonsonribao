package com.jonson.jonsonribao.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.jonson.jonsonribao.ServerURL.ServerUrls;
import com.jonson.jonsonribao.bean.ThemeListJson;
import com.jonson.jonsonribao.bean.ThemesClassJson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by jonson on 2016/3/4.
 */
public class CacheLoad {

    float themesCount = 0;
    float allThemeCount;
    private MyDbUtils myDbUtils;

    public void load(ThemesClassJson themesClassJson , Context context){

        myDbUtils = new MyDbUtils(context);

        allThemeCount = (float)themesClassJson.others.size() + 1;
        loadMainData(ServerUrls.NEWS_URL, context);
        ArrayList<ThemesClassJson.ThemeClass> themes = themesClassJson.others;

        //循环取得每个日报分类的主Fragment的json数据
        for(ThemesClassJson.ThemeClass theme : themes){
            String itemUrl = ServerUrls.THEME_LIST_URL + theme.id;
            loadMainData(itemUrl, context);
        }
    }

    //加载主Fragment的json
    private void loadMainData(final String url, final Context context){



        //加载主页面数据
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;

                //解析当前日报分类的主Fragment的json
                Gson gson = new Gson();
                ThemeListJson themeListJson = gson.fromJson(result, ThemeListJson.class);
                ArrayList<ThemeListJson.ThemeListItems> stories = themeListJson.stories;

                //循环取得WebView的json
                for(ThemeListJson.ThemeListItems items : stories){
                    loadWebViewData(ServerUrls.NEWS_CONTENT_URL + items.id , context);
                }

                //查找数据库中对应网址MD5的条目
                String currentDate = DateUtils.date2String(new Date());
                HashMap<String, String> dbMain = myDbUtils.findDb(MD5Encoder.encode(url), DbNamesAndOther.MAIN_DB);


                myDbUtils.addDb(MD5Encoder.encode(url), currentDate, result, DbNamesAndOther.MAIN_DB);
                //返回完成
                ++themesCount;
                if(progressChangeListener != null){
                    int percent = (int)(((themesCount / allThemeCount)*100)+0.5);
                    progressChangeListener.onProgressChange(percent+"");
                }
            }
            @Override
            public void onFailure(HttpException e, String s) {

                //返回-1
            }
        });
    }

    //加载webView的json
    private void loadWebViewData(final String url , Context context){
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                String currentDate = DateUtils.date2String(new Date());
                HashMap<String, String> dbMain = myDbUtils.findDb(MD5Encoder.encode(url), DbNamesAndOther.WEB_DB);
                if(dbMain == null){
                    myDbUtils.addDb(MD5Encoder.encode(url) ,currentDate, result , DbNamesAndOther.WEB_DB);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });
    }

    OnProgressChangeListener progressChangeListener;

    public void setOnProgressChangeListener(OnProgressChangeListener progressChangeListener){
        this.progressChangeListener = progressChangeListener;
    }

    public interface OnProgressChangeListener{
        public void onProgressChange(String percent);
    }
}
