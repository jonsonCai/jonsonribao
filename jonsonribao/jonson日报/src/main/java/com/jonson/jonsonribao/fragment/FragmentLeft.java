package com.jonson.jonsonribao.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jonson.jonsonribao.MainActivity;
import com.jonson.jonsonribao.R;
import com.jonson.jonsonribao.ServerURL.ServerUrls;
import com.jonson.jonsonribao.bean.ThemesClassJson;
import com.jonson.jonsonribao.utils.CacheLoad;
import com.jonson.jonsonribao.utils.DateUtils;
import com.jonson.jonsonribao.utils.DbNamesAndOther;
import com.jonson.jonsonribao.utils.MD5Encoder;
import com.jonson.jonsonribao.utils.MyDbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by jonson on 2016/2/19.
 */
public class FragmentLeft extends Fragment {

    private Activity mActivity;
    private View mView;
    private ListView lvLeftMenuList;
    private ArrayList<ThemesClassJson.ThemeClass> lists;
    private ImageView ivCacheLoad;

    private boolean isCacheLoading = false;
    private TextView tvOffLine;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    /**
     * 开始处理布局
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.menu_left, container, false);
        lvLeftMenuList = (ListView) mView.findViewById(R.id.lv_left_menu_list);
        ivCacheLoad = (ImageView) mView.findViewById(R.id.iv_cache_load);
        tvOffLine = (TextView) mView.findViewById(R.id.tv_offline);
        return mView;
    }



    /**
     * 布局处理完成
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }


    public void initData() {
        final MyDbUtils dbUtils = new MyDbUtils(mActivity);
        final HashMap<String , String> dbResult = dbUtils.findDb(MD5Encoder.encode(ServerUrls.THEMES_CLASS_URL), DbNamesAndOther.LEFT_DB);

        if(dbResult != null && dbResult.get("dbJson") != null){
            String dbJson = dbResult.get("dbJson");
            parseData(dbJson);
            System.out.println("侧边菜单从数据库取");
        }else{
            System.out.println("侧边菜单从网络库取");
            HttpUtils httpUtils = new HttpUtils();
            httpUtils.send(HttpRequest.HttpMethod.GET, ServerUrls.THEMES_CLASS_URL, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    String result = responseInfo.result;

                    dbUtils.addDb(MD5Encoder.encode(ServerUrls.THEMES_CLASS_URL) , DateUtils.date2String(new Date()) , result , DbNamesAndOther.LEFT_DB);

                    parseData(result);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(mActivity, "加载日报分类失败", Toast.LENGTH_SHORT).show();
                }
            });
        }



    }

    public void parseData(String json){

        MainActivity MainUi = (MainActivity) mActivity;

        final FragmentMain fragmentMain = MainUi.getFragmentMain();
        final SlidingMenu slidingMenu = MainUi.getSlidingMenu();

        Gson gson = new Gson();
        final ThemesClassJson themesClassJson = gson.fromJson(json, ThemesClassJson.class);
        lists = themesClassJson.others;
        lvLeftMenuList.setAdapter(new LeftMenuListAdapter());

        lvLeftMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    fragmentMain.setCurrentPage(false, true, ServerUrls.NEWS_URL, -1 + "");
                    slidingMenu.toggle();
                } else {
                    String currentItemId = themesClassJson.others.get(position - 1).id;
                    fragmentMain.setCurrentPage(false, false, ServerUrls.THEME_LIST_URL + currentItemId, currentItemId);
                    slidingMenu.toggle();
                }

            }
        });

        //设置点击离线下载
        ivCacheLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCacheLoading) {
                    final CacheLoad cacheLoad = new CacheLoad();
                    cacheLoad.setOnProgressChangeListener(new CacheLoad.OnProgressChangeListener() {
                        @Override
                        public void onProgressChange(String percent) {
                            Log.e("my", "下载进度:::::::::::::::" + percent);
                            tvOffLine.setText(percent+"%");
                            if(percent.equals("100")){
                                tvOffLine.setText("离线完成");
                                isCacheLoading = false;
                            }
                        }
                    });
                    isCacheLoading = true;
                    new Thread(){
                        @Override
                        public void run() {
                            cacheLoad.load(themesClassJson , mActivity);
                        }
                    }.start();

                }

            }
        });
    }

    class LeftMenuListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return lists.size()+1;
        }

        @Override
        public Object getItem(int position) {
            return lists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LeftMenuListHolder holder;
            if(convertView == null){
                convertView = View.inflate(mActivity , R.layout.left_menu_item , null);
                holder = new LeftMenuListHolder();
                holder.tvItemTitle = (TextView) convertView.findViewById(R.id.tv_left_menu_item_title);
                holder.ivItemPlusIco = (ImageView) convertView.findViewById(R.id.iv_left_menu_plus);
                holder.ivItemHomeIco = (ImageView) convertView.findViewById(R.id.iv_left_menu_home);
                convertView.setTag(holder);
            }else{
                holder = (LeftMenuListHolder) convertView.getTag();
            }

            if(position == 0){//第一个条目
                holder.tvItemTitle.setText("首页");
                holder.tvItemTitle.setTextColor(getResources().getColor(R.color.zhihuBlue));
                holder.ivItemHomeIco.setVisibility(View.VISIBLE);
                holder.ivItemPlusIco.setVisibility(View.INVISIBLE);
                return convertView;
            }

            //复用错乱,需要再设置回原来的样式
            holder.tvItemTitle.setText(lists.get(position - 1).name);
            holder.tvItemTitle.setTextColor(Color.BLACK);
            holder.ivItemHomeIco.setVisibility(View.GONE);

            holder.ivItemPlusIco.setVisibility(View.VISIBLE);


            return convertView;
        }
    }

    class LeftMenuListHolder{
        TextView tvItemTitle;
        ImageView ivItemPlusIco;
        ImageView ivItemHomeIco;
    }



}
