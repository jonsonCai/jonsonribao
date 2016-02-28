package com.jonson.jonsonribao;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.jonson.jonsonribao.ServerURL.ServerUrls;
import com.jonson.jonsonribao.bean.SplashJson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.io.File;


/**
 * 闪屏页面
 */
public class SplashActivity extends Activity {

    private RelativeLayout rlSplash;
    private ImageView ivSplashBackground;
    private String splashImageUrl;
    private SplashJson splashJson;
    private AnimationSet animationSet;

    private boolean cache = false;
    private BitmapUtils bitmapUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        rlSplash = (RelativeLayout) findViewById(R.id.rl_splash);
        ivSplashBackground = (ImageView) findViewById(R.id.iv_splash_background);

        initAnimation();

        getSplashByServer();


    }

    /**
     * 在服务器获取闪屏页的图片地址
     */
    public void getSplashByServer(){
        HttpUtils httpUtils = new HttpUtils();

        httpUtils.send(HttpRequest.HttpMethod.GET, ServerUrls.SPLASH_IMAGE_URL, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                splashImageUrl = responseInfo.result;
                parseData(splashImageUrl);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                System.out.println("加载失败");
                jump2Main();
            }
        });
    }

    private void parseData(String json){
        Gson gson = new Gson();
        splashJson = gson.fromJson(json, SplashJson.class);
        setSplashBg(splashJson.img);
    }

    /**
     * 设置背景
     */
    public void setSplashBg(String url) {

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/jonsonRiBao/splash";
        Log.e("my" , "path" + path);
        File cachefile = new File(path);
        bitmapUtils = new BitmapUtils(this , path);

        if(cachefile.isDirectory() && cachefile.exists()) {
            if(cachefile.list().length > 0){
                cache = true;
                bitmapUtils.configDefaultLoadingImage(R.mipmap.splash);
                bitmapUtils.display(ivSplashBackground, url);
                ivSplashBackground.startAnimation(animationSet);
                System.out.println("有缓存");
            }
        }else{
            cache = false;
            ivSplashBackground.setImageResource(R.mipmap.splash);
            ivSplashBackground.startAnimation(animationSet);
            System.out.println("没缓存");
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    private void initAnimation(){
        System.out.println("开始动画");
        //缩放动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f , 1.5f , 1f , 1.5f ,
                ScaleAnimation.RELATIVE_TO_SELF , 0.5f , ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(5000);

        //透明动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(1 , 0);
        alphaAnimation.setDuration(2000);
        alphaAnimation.setStartOffset(3000);

        animationSet = new AnimationSet(this , null);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setFillAfter(true);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(!cache){
                    bitmapUtils.display(ivSplashBackground, splashJson.img);
                }
                jump2Main();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void jump2Main() {
        Intent intent = new Intent(this , MainActivity.class);
        startActivity(intent);
        finish();
    }
}
