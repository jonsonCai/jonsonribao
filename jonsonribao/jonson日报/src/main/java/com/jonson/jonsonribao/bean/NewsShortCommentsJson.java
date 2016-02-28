package com.jonson.jonsonribao.bean;

import java.util.ArrayList;

/**
 * Created by jonson on 2016/2/20.
 * 新闻短评论
 * 有可能出现评论被回复
 */
public class NewsShortCommentsJson {

    public ArrayList<ShortComments> comments;

    public class ShortComments{
        public String author;//用户名
        public String avatar;//用户头像
        public String content;//长评论内容
        public String id;//当该条评论是当前加载的评论的最后一条时,用来加载更多评论
        public String likes;//点赞数量
        public String time;//??

        public CommentReplay reply_to;//评论回复
    }

    public class CommentReplay{
        public String author;//回复评论者的用户名
        public String content;//回复评论的内容
        public String id;//??
        public String status;//??
    }
}
