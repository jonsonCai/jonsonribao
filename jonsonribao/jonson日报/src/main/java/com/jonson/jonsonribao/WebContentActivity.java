package com.jonson.jonsonribao;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jonson.jonsonribao.ServerURL.ServerUrls;
import com.jonson.jonsonribao.bean.NewsContentInfoJson;
import com.jonson.jonsonribao.bean.NewsContentJson;
import com.jonson.jonsonribao.view.MyScrollView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class WebContentActivity extends Activity {

    private TextView tvPraiseCount;
    private TextView tvCommentCount;
    private WebView wvContent;
    private int titleBarHeight;
    private ImageView ivWebViewHeader;
    private BitmapUtils bitmapUtils;

    private int alpha = 255;
    private RelativeLayout rlTitleBar;
    private MyScrollView slContent;
    private String headerImageUrl;
    private ImageView ivComment;
    private String id;
    private String commentsCount;
    private NewsContentInfoJson newsContentInfoJson;
    private NewsContentJson newsContentJson;
    private ImageView ivShared;
    private ImageView ivCollect;
    private ImageView ivPraise;
    private ImageButton ivBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_web_content);

        rlTitleBar = (RelativeLayout) findViewById(R.id.rl_web_view_titlebar);
        slContent = (MyScrollView) findViewById(R.id.sl_content);
        wvContent = (WebView) findViewById(R.id.wv_content);
        ivWebViewHeader = (ImageView) findViewById(R.id.iv_webview_header);
        tvPraiseCount = (TextView) findViewById(R.id.tv_praise_count);
        tvCommentCount = (TextView) findViewById(R.id.tv_comment_count);
        ivShared = (ImageView) findViewById(R.id.iv_shared);
        ivCollect = (ImageView) findViewById(R.id.iv_collect);
        ivComment = (ImageView) findViewById(R.id.iv_comment);
        ivPraise = (ImageView) findViewById(R.id.iv_praise);
        ivBack = (ImageButton) findViewById(R.id.iv_webView_back);

        //默认设置透明度为不透明
        rlTitleBar.getBackground().setAlpha(alpha);
        tvPraiseCount.setTextColor(Color.argb(alpha , 255 , 255 , 255));
        tvCommentCount.setTextColor(Color.argb(alpha, 255, 255, 255));
        ivShared.getBackground().setAlpha(alpha);
        ivCollect.getBackground().setAlpha(alpha);
        ivComment.getBackground().setAlpha(alpha);
        ivPraise.getBackground().setAlpha(alpha);
        ivBack.getBackground().setAlpha(alpha);



        WebSettings settings = wvContent.getSettings();
        settings.setJavaScriptEnabled(true);


        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        String dataUrl = ServerUrls.NEWS_CONTENT_URL + id;
        String dataInfoUrl = ServerUrls.NEWS_CONTENT_INFO_URL + id;

        System.out.println("dataUrl" + dataUrl);

        initWebViewData(dataUrl);
        initWebViewInfo(dataInfoUrl);



        rlTitleBar.measure(0, 0);
        titleBarHeight = rlTitleBar.getMeasuredHeight();


        rlTitleBar.getBackground().setAlpha(255);

        ivWebViewHeader.setPadding(0, titleBarHeight, 0, 0);



        bitmapUtils = new BitmapUtils(this);


        slContent.setOnScrollListener(new MyScrollView.OnScrollListener() {
            @Override
            public void onScrollChange(int x, int y, int oldX, int oldY) {
                int rangeY = y - oldY;
                alpha = alpha + (-rangeY);
                if (alpha < 0) {
                    alpha = 0;
                } else if (alpha > 255) {
                    alpha = 255;
                }

                //设置滚动时title透明度变化
                rlTitleBar.getBackground().setAlpha(alpha);
                tvPraiseCount.setTextColor(Color.argb(alpha , 255 , 255 , 255));
                tvCommentCount.setTextColor(Color.argb(alpha, 255, 255, 255));
                ivShared.getBackground().setAlpha(alpha);
                ivCollect.getBackground().setAlpha(alpha);
                ivComment.getBackground().setAlpha(alpha);
                ivPraise.getBackground().setAlpha(alpha);
                ivBack.getBackground().setAlpha(alpha);
                if (alpha <= 0) {
                    rlTitleBar.setVisibility(View.INVISIBLE);
                } else {
                    rlTitleBar.setVisibility(View.VISIBLE);
                }
                if (y == 0) {
                    rlTitleBar.getBackground().setAlpha(255);
                    alpha = 255;
                }

                //判断页面是否有图片
                if (headerImageUrl != null) {
                    //设置滚动时页面顶部图片padding变化
                    ivWebViewHeader.setPadding(0, titleBarHeight, 0, -y);
                }
            }
        });

        //点击退出当前Activity
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //点击跳转到评论页面
        ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WebContentActivity.this, CommentActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("comments_count", newsContentInfoJson.comments);
                intent.putExtra("long_comments_count", newsContentInfoJson.long_comments);
                intent.putExtra("short_comments_count", newsContentInfoJson.short_comments);
                startActivity(intent);
            }
        });


        ivShared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShareSDK.initSDK(WebContentActivity.this);
                OnekeyShare oks = new OnekeyShare();
                //关闭sso授权
                oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
                //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
                // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
                oks.setTitle(newsContentJson.title);
                // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
                oks.setTitleUrl(newsContentJson.share_url);
                // text是分享文本，所有平台都需要这个字段
                oks.setText("我是分享文本");
                // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
                oks.setImagePath(newsContentJson.image_source);//确保SDcard下面存在此张图片
                // url仅在微信（包括好友和朋友圈）中使用
                oks.setUrl(newsContentJson.share_url);
                // comment是我对这条分享的评论，仅在人人网和QQ空间使用
                oks.setComment("我是测试评论文本");
                // site是分享此内容的网站名称，仅在QQ空间使用
                oks.setSite(getString(R.string.app_name));
                // siteUrl是分享此内容的网站地址，仅在QQ空间使用
                oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
                oks.show(WebContentActivity.this);

            }
        });

    }

    /**
     * 初始化webView数据
     * @param dataUrl
     */
    private void initWebViewData(String dataUrl) {

        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, dataUrl, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String json = responseInfo.result;
                Gson gson = new Gson();
                newsContentJson = gson.fromJson(json, NewsContentJson.class);
                String htmlBody = newsContentJson.body;
                headerImageUrl = newsContentJson.image;

                //给每个img标签加一个Style,宽度为页面的100%,用以适应手机屏幕
                Document document = Jsoup.parse(htmlBody);
                Elements img = document.getElementsByTag("img");

                int i = 0;
                if(img.size() != 0){
                    for (Element element:img) {
                        if(i == 0){//默认第一张图片是作者头像,不用设置适应屏幕
                            i++;
                        }else{
                            element.attr("style" , "width:100%");
                            i++;
                        }
                    }
                }
                htmlBody = document.toString();

                //判断页面是否有图片
                if(headerImageUrl != null){
                    bitmapUtils.display(ivWebViewHeader, headerImageUrl);
                }

                wvContent.loadDataWithBaseURL(null, htmlBody, null, "UTF-8", null);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(WebContentActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 初始化当前页面的详情信息
     */
    private void initWebViewInfo(String dataInfoUrl){
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, dataInfoUrl, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                Gson gson = new Gson();
                newsContentInfoJson = gson.fromJson(result, NewsContentInfoJson.class);
                commentsCount = newsContentInfoJson.comments;
                String popularity = newsContentInfoJson.popularity;

                tvCommentCount.setText(commentsCount);
                tvPraiseCount.setText(popularity);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(WebContentActivity.this, "加载评论信息失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        slContent.removeAllViews();//关闭Activity时,移除掉WebView,
        super.onDestroy();
    }
}
