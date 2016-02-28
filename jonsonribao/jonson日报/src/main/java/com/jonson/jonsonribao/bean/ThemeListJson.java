package com.jonson.jonsonribao.bean;

import java.util.ArrayList;

/**
 * Created by jonson on 2016/2/20.
 * 分类日报页面(主页面中显示)
 */
public class ThemeListJson {
    public String name;//分类名称
    public String background;//列表头的动画背景图片
    public String color;//??
    public String description;//列表头动画标题
    public String image;//??图片地址
    public ArrayList<Editors> editors;//主编者
    public ArrayList<ThemeListItems> stories;

    public class Editors{
        public String avatar;//主编头像
        public String name;//主编名字
        public String bio;//主编个性签名
        public String url;//主编个人主页;
    }

    public class ThemeListItems{
        public String id;//条目id
        public String title;//条目标题
        public String type;//??
        public ArrayList<String> images;//条目图片,用户推荐日报有些条目没有图片

    }
}
