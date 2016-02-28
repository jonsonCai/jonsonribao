package com.jonson.jonsonribao.bean;

import java.util.ArrayList;

/**
 * Created by jonson on 2016/2/20.
 * 分类日报
 */
public class ThemesClassJson {
    public String limit;//??

    public ArrayList<ThemeClass> others;

    public class ThemeClass{
        public String color;//??
        public String description;//??
        public String name;//分类名称
        public String thumbnail;//??图片地址(用不到)
        public String id;//??


    }
}
