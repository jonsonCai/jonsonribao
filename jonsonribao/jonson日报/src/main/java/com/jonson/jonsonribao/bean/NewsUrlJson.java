package com.jonson.jonsonribao.bean;

import java.util.ArrayList;

/**
 * Created by jonson on 2016/2/20.
 * 首页新闻数据
 * http://news-at.zhihu.com/api/4/news/latest 返回
 */
public class NewsUrlJson {
    //日期
    public String date;

    //首页新闻列表
    public ArrayList<NewsListItem> stories;
        public class NewsListItem{
            public String title;//首页新闻列表-标题
            public String type;//首页新闻列表
            public String id;//首页新闻列表-条目id,点击后使用这个嵌到新闻内容网址后面
            public ArrayList<String> images;//首页新闻列表-条目图片
        }

    //首页新闻-轮播新闻
    public ArrayList<ScrollNews> top_stories;
        public class ScrollNews{
            public String id;//轮播新闻id,用于点击后使用这个嵌到新闻内容网址后面
            public String image;//轮播新闻图片
            public String title;//轮播新闻标题
            public String type;//轮播新闻类型
        }


}
