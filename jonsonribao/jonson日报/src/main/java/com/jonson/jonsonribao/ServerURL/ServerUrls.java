package com.jonson.jonsonribao.ServerURL;

/**
 * Created by jonson on 2016/2/18.
 */
public class ServerUrls {

    //闪屏
    public static final String SPLASH_IMAGE_URL = "http://news-at.zhihu.com/api/4/start-image/1080*1776";//闪屏页图片

    //新闻
    public static final String NEWS_URL = "http://news-at.zhihu.com/api/4/news/latest";//主页面最新消息
    public static final String NEWS_CONTENT_URL = "http://news-at.zhihu.com/api/4/news/";//主页面消息跳转 后面+主页面消息条目的id
    public static final String NEWS_CONTENT_INFO_URL = "http://news-at.zhihu.com/api/4/story-extra/";//主页面详细信息 后面+主页面消息条目的id
    public static final String NEWS_LONG_COMMENTS = "http://news-at.zhihu.com/api/4/story/'REPLACE_ID'/long-comments";//新闻长评论 id为主页面消息条目的id
    public static final String NEWS_SHORT_COMMENTS = "http://news-at.zhihu.com/api/4/story/'REPLACE_ID'/short-comments";//新闻短评论 id为主页面消息条目的id

    //加载更多
    public static final String HISTORY_NEWS_URL = "http://news.at.zhihu.com/api/4/news/before/";//历时消息(加载更多) 后面+日期(20130520)

    //侧滑菜单
    public static final String THEMES_CLASS_URL = "http://news-at.zhihu.com/api/4/themes";//主题分类(侧滑菜单)
    public static final String THEME_LIST_URL = "http://news-at.zhihu.com/api/4/theme/";//主题列表(显示到主页面),后面+id
    public static final String THEME_CONTENT_URL = "http://news-at.zhihu.com/api/4/news/";//主题列表条目(和NEWS_CONTENT_URL一样) , 后面+id


}
