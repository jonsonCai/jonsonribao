package com.jonson.jonsonribao.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jonson.jonsonribao.MainActivity;
import com.jonson.jonsonribao.R;
import com.jonson.jonsonribao.ServerURL.ServerUrls;
import com.jonson.jonsonribao.bean.NewsUrlJson;
import com.jonson.jonsonribao.bean.ThemeListJson;
import com.jonson.jonsonribao.utils.DateUtils;
import com.jonson.jonsonribao.utils.MD5Encoder;
import com.jonson.jonsonribao.utils.MyDbUtils;
import com.jonson.jonsonribao.utils.DbNamesAndOther;
import com.jonson.jonsonribao.view.HomeListView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by jonson on 2016/2/19.
 */
public class FragmentMain extends Fragment {

    Activity mActivity;
    private MainActivity mainUi;
    private SlidingMenu slidingMenu;
    private TextView tvTitle;
    private Object dataFromServer;
    private ImageView ivMenuBtn;
    private ImageButton btnMessage;
    private ImageButton btnAddTheme;
    private ImageButton btnMore;
    private FrameLayout flNewsList;
    private RelativeLayout rlPullDown;
    private ImageView ivPullDownArrow;

    private ArrayList<NewsUrlJson.NewsListItem> stories;
    private ArrayList<NewsUrlJson.ScrollNews> topStories;

    private HomeListViewAdapter homeListViewAdapter;
    private VpTopNewsAdapter vpTopNewsAdapter;

    private HomeListView homeListView;

    TextView tvTopNewTitle ;
    ViewPager vpTopNews;
    CirclePageIndicator indicator ;

    int homeLoadCount = 0;

    View scrollView;

    public android.os.Handler mHandler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){
                int nextItem = vpTopNews.getCurrentItem() + 1;
                if(nextItem < vpTopNews.getAdapter().getCount()){
                    vpTopNews.setCurrentItem(vpTopNews.getCurrentItem() + 1);
                }else{
                    vpTopNews.setCurrentItem(0);
                }

            }
        }
    };

    private View homeHeaderView;
    private BitmapUtils bitmapUtils;
    private ArrayList<ThemeListJson.ThemeListItems> themeStories;
    private Timer timer;
    private String currentThemeItemId;
    private HomeListView themeListView;
    private ThemeListViewAdapter themeListViewAdapter;
    private String currentDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    /**
     * 处理fragment布局
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        System.out.println("创建创建");
        View view = inflater.inflate(R.layout.main, container, false);
        ivMenuBtn = (ImageView) view.findViewById(R.id.iv_menu_btn);
        btnMessage = (ImageButton) view.findViewById(R.id.btn_message);
        btnMore = (ImageButton) view.findViewById(R.id.btn_more);
        btnAddTheme = (ImageButton) view.findViewById(R.id.btn_add_theme);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        flNewsList = (FrameLayout) view.findViewById(R.id.fl_news_list);


        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //////////////点击信息///////////
            }
        });
        ivMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.toggle();
            }
        });
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ////////////////点击更多弹窗//////////////
            }
        });
        btnAddTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /////////////点击添加主题/////////////
            }
        });

        return  view;
    }

    /**
     * 布局处理完成
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainUi = (MainActivity) mActivity;
        slidingMenu = mainUi.getSlidingMenu();
        setCurrentPage(false , true, ServerUrls.NEWS_URL , -1 + "");

        bitmapUtils = new BitmapUtils(mActivity);
    }


    public void setCurrentPage(boolean isRefresh ,boolean isHome , String url , String currentThemeItemId){
        this.currentThemeItemId = currentThemeItemId;
        homeLoadCount = 0;//重设加载次数为0;

        if(isHome){//隐藏信息按钮和更多按钮
            btnMessage.setVisibility(View.VISIBLE);
            btnMore.setVisibility(View.VISIBLE);
            btnAddTheme.setVisibility(View.GONE);
            tvTitle.setText("首页");
        }else{//隐藏添加到主题按钮
            btnMessage.setVisibility(View.GONE);
            btnMore.setVisibility(View.GONE);
            btnAddTheme.setVisibility(View.VISIBLE);
        }
        initData(isRefresh, isHome, url);
    }

    /**
     * 初始化数据
     */
    public void initData(boolean isRefresh ,boolean isHome , String url) {
        getDataFromServer(false, isRefresh, isHome, url);
    }

    /**
     * 从服务器获取数据
     * @return
     */
    public Object getDataFromServer(final boolean isLoadMore , final boolean isRefresh ,final boolean isHome , final String url) {

    //从数据库取数据

        final MyDbUtils dbUtils = new MyDbUtils(mActivity);
        final HashMap<String , String> dbResult = dbUtils.findDb(MD5Encoder.encode(url), DbNamesAndOther.MAIN_DB);
        currentDate = DateUtils.date2String(new Date());

    //对比存入数据库的日期是否和当前日期一致;
        if(dbResult != null && dbResult.get("dbJson") != null && !isLoadMore && !isRefresh && currentDate.equals(dbResult.get("dbDate"))){
            System.out.println("取用缓存");
            parseData(isLoadMore, isRefresh, isHome, dbResult.get("dbJson"));
    //加载更多从数据库取数据
        }else if(dbResult != null && dbResult.get("dbJson") != null && isLoadMore){
            System.out.println("加载更多取用缓存");
            parseData(isLoadMore , isRefresh , isHome , dbResult.get("dbJson"));
    //数据库中没有缓存,从网络获取
        }else{

            HttpUtils httpUtils = new HttpUtils();
            httpUtils.send(HttpRequest.HttpMethod.GET, url , new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    String result = responseInfo.result;
                    parseData(isLoadMore, isRefresh, isHome, result);
                    dbUtils.addDb(MD5Encoder.encode(url) , currentDate , result , DbNamesAndOther.MAIN_DB);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(mActivity, "连接失败", Toast.LENGTH_SHORT).show();
                    if(homeListView != null){
                        homeListView.RefreshFinish();
                        if(dbResult != null){
                            parseData(isLoadMore, isRefresh, isHome, dbResult.get("dbJson"));
                        }

                    }
                }
            });
        }

    return dataFromServer;
    }

    public void parseData(boolean isLoadMore , boolean isRefresh , boolean isHome , String json){


        //主页面,使用NewsUrlJson接受解析
        if(isHome){

            btnMessage.setVisibility(View.VISIBLE);
            btnMessage.setVisibility(View.VISIBLE);
            btnAddTheme.setVisibility(View.GONE);

            //解析数据
            Gson gson = new Gson();
            NewsUrlJson newsUrlJson = gson.fromJson(json, NewsUrlJson.class);

            //判断是否是刷新操作
            if(isRefresh){
                stories = newsUrlJson.stories;
                topStories = newsUrlJson.top_stories;
                vpTopNewsAdapter.notifyDataSetChanged();
                homeListViewAdapter.notifyDataSetChanged();

                new Thread(){
                    @Override
                    public void run() {
                        SystemClock.sleep(300);
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                homeListView.RefreshFinish();
                            }
                        });
                    }
                }.start();
                return;
            }

            //判断是否是加载更多操作
            if(isLoadMore && stories != null){
                ArrayList<NewsUrlJson.NewsListItem> moreStories = newsUrlJson.stories;
                stories.addAll(moreStories);
                vpTopNewsAdapter.notifyDataSetChanged();
                homeListViewAdapter.notifyDataSetChanged();
                return;
            }

            homeListView = new HomeListView(mActivity);

            stories = newsUrlJson.stories;
            topStories = newsUrlJson.top_stories;

            //初始化滚动头条View
            scrollView = View.inflate(mActivity, R.layout.scroll_top_news, null);
            vpTopNews = (ViewPager) scrollView.findViewById(R.id.vp_top_news);
            tvTopNewTitle = (TextView) scrollView.findViewById(R.id.tv_top_news_title);
            indicator = (CirclePageIndicator) scrollView.findViewById(R.id.indicator);

            //设置滚动头条数据
            vpTopNewsAdapter = new VpTopNewsAdapter();
            vpTopNews.setAdapter(vpTopNewsAdapter);
            tvTopNewTitle.setText(topStories.get(0).title);
            indicator.setViewPager(vpTopNews);

            indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    tvTopNewTitle.setText(topStories.get(position).title);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            //把滚动头条添加到头布局
            homeListView.addHeaderView(scrollView);

            //设置HomeListView的Adapter
            homeListViewAdapter = new HomeListViewAdapter();
            homeListView.setAdapter(homeListViewAdapter);

            //设置下拉刷新监听
            homeListView.setOnPullDownRefreshListener(new HomeListView.PullDowanRefreshListener() {
                @Override
                public void onRefresh() {
                    getDataFromServer(false, true, true, ServerUrls.NEWS_URL);
                }
            });

            //设置滑动监听(滑动到底部加载更多)
            homeListView.setOnLoadMoreListener(new HomeListView.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    Date date = new Date();
                    DateUtils dateUtils = new DateUtils();
                    String historyDate = dateUtils.getResult(date, --homeLoadCount);
                    String loadMoreUrl = ServerUrls.HISTORY_NEWS_URL + historyDate;
                    Log.e("my", loadMoreUrl);
                    getDataFromServer(true, false, true, loadMoreUrl);
                }
            });


            //设置条目点击监听,跳转到WebView界面
            homeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String itemId = stories.get(position - homeListView.getHeaderViewsCount()).id;
                    MainActivity mainUi = (MainActivity) FragmentMain.this.mActivity;
                    mainUi.jumpToContent(itemId);
                }
            });

            flNewsList.removeAllViews();
            flNewsList.addView(homeListView);

            //设置定时器实现自动滚动
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message msg = Message.obtain();
                    msg.what = 0;
                    mHandler.sendMessage(msg);
                }
            }, 5000, 5000);




        //专题日报页面,使用ThemeListJson接受解析
        }else{

            timer.cancel();//切换到其他主题日报,取消定时器,避免定时器叠加导致主页轮播图重复切换

            Gson gson = new Gson();
            ThemeListJson themeListJson = gson.fromJson(json, ThemeListJson.class);

            //判断是否是刷新操作
            if(isRefresh){
                themeStories = themeListJson.stories;
                themeListViewAdapter.notifyDataSetChanged();
                themeListView.RefreshFinish();
                return;
            }

            //判断是否是加载更多操作
            if(isLoadMore){
                ArrayList<ThemeListJson.ThemeListItems> ThemeLoadMorestories = themeListJson.stories;
                this.themeStories.addAll(ThemeLoadMorestories);
                themeListViewAdapter.notifyDataSetChanged();
                return;
            }

            themeStories = themeListJson.stories;

            btnMessage.setVisibility(View.GONE);
            btnMessage.setVisibility(View.GONE);
            btnAddTheme.setVisibility(View.VISIBLE);

            tvTitle.setText(themeListJson.name);

            themeListView = new HomeListView(mActivity);

            final View themeListHeader = View.inflate(mActivity, R.layout.theme_top_news, null);
            ImageView ivThemeHeadImage = (ImageView) themeListHeader.findViewById(R.id.iv_theme_header_Image);
            TextView tvThemeHeaderImage = (TextView) themeListHeader.findViewById(R.id.tv_theme_header_title);


            bitmapUtils.display(ivThemeHeadImage, themeListJson.background);
            tvThemeHeaderImage.setText(themeListJson.description);

            themeListView.addHeaderView(themeListHeader);
            themeListViewAdapter = new ThemeListViewAdapter();
            themeListView.setAdapter(themeListViewAdapter);

            //设置点击监听,点击后跳转页面
            themeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(position < themeListView.getHeaderViewsCount()){
                        return;
                    }
                    position = position - themeListView.getHeaderViewsCount();
                    String itemId = themeStories.get(position).id;
                    MainActivity mainUi = (MainActivity) FragmentMain.this.mActivity;
                    mainUi.jumpToContent(itemId);
                }
            });

            //设置下拉刷新监听
            themeListView.setOnPullDownRefreshListener(new HomeListView.PullDowanRefreshListener() {
                @Override
                public void onRefresh() {
                    getDataFromServer(false, true, false, ServerUrls.THEME_LIST_URL + currentThemeItemId);
                }
            });

            //设置滑动监听(滑动到底部加载更多)
            themeListView.setOnLoadMoreListener(new HomeListView.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    Date date = new Date();
                    DateUtils dateUtils = new DateUtils();
                    String historyDate = dateUtils.getResult(date, --homeLoadCount);
                    String loadMoreUrl = ServerUrls.THEME_LIST_URL + currentThemeItemId + "/before/" + themeStories.get(themeStories.size() - 1).id;
                    System.out.println("loadMoreUrl" + loadMoreUrl);
                    Log.e("my", loadMoreUrl);
                    getDataFromServer(true, false, false, loadMoreUrl);
                }
            });


            flNewsList.removeAllViews();
            flNewsList.addView(themeListView);

        }
    }


    /**
     * 首页List适配器
     */
    public class HomeListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return stories.size();
        }

        @Override
        public Object getItem(int position) {
            return stories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HomeNewsItemHolder holder;
            if(convertView == null){
                convertView = View.inflate(mActivity , R.layout.home_news_item , null);
                holder = new HomeNewsItemHolder();
                holder.tvHomeNewsItemTitle = (TextView) convertView.findViewById(R.id.tv_home_news_item_title);
                holder.ivHomeNewsItemImage = (ImageView) convertView.findViewById(R.id.tv_home_news_item_image);
                convertView.setTag(holder);
            }else {
                holder = (HomeNewsItemHolder) convertView.getTag();
            }
            holder.tvHomeNewsItemTitle.setText(stories.get(position).title);
            bitmapUtils.display(holder.ivHomeNewsItemImage, stories.get(position).images.get(0));
            return convertView;
        }
    }

    class HomeNewsItemHolder{
        TextView tvHomeNewsItemTitle;
        ImageView ivHomeNewsItemImage;
    }


    public class ThemeListViewAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return themeStories.size();
        }

        @Override
        public Object getItem(int position) {
            return themeStories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ThemeNewsItemHolder holder;
            if(convertView == null){
                convertView = View.inflate(mActivity , R.layout.home_news_item , null);
                holder = new ThemeNewsItemHolder();
                holder.tvThemeNewsItemTitle = (TextView) convertView.findViewById(R.id.tv_home_news_item_title);
                holder.ivThemeNewsItemImage = (ImageView) convertView.findViewById(R.id.tv_home_news_item_image);
                convertView.setTag(holder);
            }else{
                holder = (ThemeNewsItemHolder) convertView.getTag();
            }

            holder.tvThemeNewsItemTitle.setText(themeStories.get(position).title);


            if(themeStories.get(position).images != null){
                bitmapUtils.display(holder.ivThemeNewsItemImage , themeStories.get(position).images.get(0));
            }else{
                holder.ivThemeNewsItemImage.setVisibility(View.GONE);
            }

            return convertView;
        }
    }

    class ThemeNewsItemHolder{
        TextView tvThemeNewsItemTitle;
        ImageView ivThemeNewsItemImage;
    }


    /**
     * 首页viewPager适配器
     */
    class VpTopNewsAdapter extends PagerAdapter {


        BitmapUtils bitmapUtils = new BitmapUtils(mActivity);

        @Override
        public CharSequence getPageTitle(int position) {

            return topStories.get(position).title;
        }

        @Override
        public int getCount() {

            return topStories.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {

            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView imageView = new ImageView(mActivity);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(R.mipmap.image_top_default);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity mainUi = (MainActivity) mActivity;
                    mainUi.jumpToContent(topStories.get(position).id);
                }
            });
            NewsUrlJson.ScrollNews scrollNewsData = topStories.get(position);
            bitmapUtils.display(imageView, scrollNewsData.image);

            container.addView(imageView);

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
