package com.jonson.jonsonribao.bean;

import java.util.ArrayList;

/**
 * Created by jonson on 2016/2/20.
 * 加载更多新闻
 */
public class HistoryNewsJson {

    public String date;//新闻日期

    //更多新闻列表
    public ArrayList<NewsListItem> stories;
    public class NewsListItem{
        String title;//首页新闻列表-标题
        String type;//首页新闻列表
        String id;//首页新闻列表-条目id,点击后使用这个嵌到新闻内容网址后面
        ArrayList<String> images;//首页新闻列表-条目图片
    }
}
