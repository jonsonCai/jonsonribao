package com.jonson.jonsonribao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jonson.jonsonribao.ServerURL.ServerUrls;
import com.jonson.jonsonribao.bean.NewsLongCommentsJson;
import com.jonson.jonsonribao.bean.NewsShortCommentsJson;
import com.jonson.jonsonribao.view.MyScrollView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;

public class CommentActivity extends Activity {

    private String id;
    private HttpUtils mHttpUtils;
    private Gson mGson;

    private ArrayList<NewsLongCommentsJson.LongComments> longCommentsList;
    private ArrayList<NewsShortCommentsJson.ShortComments> shortCommentsList;
    private BitmapUtils bitmapUtils;
    private ListView commentLongListView;

    private boolean isLongCommentLoad = false;
    private boolean shortCommentOpen = false;
    private int headerViewCount = 0;
    private String shortJson;
    private String longJson;
    private CommentLongAdapter commentLongAdapter;
    private TextView tvCommentLongCount;
    private MyScrollView svComment;
    private LinearLayout llLongCommentEmpty;
    private String longCommentUrl;
    private NewsLongCommentsJson newsLongCommentsJson;
    private ListView commentShortListView;
    private TextView tvCommentShortCount;
    private NewsShortCommentsJson newsShortCommentsJson;
    private String shortCommentUrl;
    private CommentShortAdapter commentShortAdapter;
    private ImageButton btnCommentBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_comment);

        mHttpUtils = new HttpUtils();
        bitmapUtils = new BitmapUtils(this);
        mGson = new Gson();


        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        String commentsCount = intent.getStringExtra("comments_count");
        String longCommentsCount = intent.getStringExtra("long_comments_count");
        String shortCommentsCount = intent.getStringExtra("short_comments_count");

        TextView tvCommentTitle = (TextView) findViewById(R.id.tv_comment_title);
        btnCommentBack = (ImageButton) findViewById(R.id.btn_comment_back);
        commentLongListView = (ListView) findViewById(R.id.lv_long_comment);
        commentShortListView = (ListView) findViewById(R.id.lv_short_comment);
        tvCommentLongCount = (TextView) findViewById(R.id.tv_comment_long_count);
        tvCommentShortCount = (TextView) findViewById(R.id.tv_comment_short_count);
        svComment = (MyScrollView) findViewById(R.id.sv_comment);
        llLongCommentEmpty = (LinearLayout) findViewById(R.id.ll_long_comment_empty);

        ImageView ivShared = (ImageView) findViewById(R.id.iv_shared);


        //设置titie显示的评论数量
        if(Integer.parseInt(commentsCount) > 0){
            tvCommentTitle.setText(commentsCount + "条评论");
        }else{
            tvCommentTitle.setText("0条评论");
        }

        if(Integer.parseInt(longCommentsCount) > 0){
            tvCommentLongCount.setText(longCommentsCount + "条长评论");
        }else{
            tvCommentLongCount.setText("0条长评论");
        }

        if(Integer.parseInt(shortCommentsCount) > 0){
            tvCommentShortCount.setText(shortCommentsCount +"条短评论");
        }else{
            tvCommentShortCount.setText("0条短评论");
        }



        //设置点击back
        btnCommentBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        svComment.setOnScrollListener(new MyScrollView.OnScrollListener() {
            @Override
            public void onScrollChange(int x, int y, int oldX, int oldY) {

                int currentY = svComment.getChildAt(0).getMeasuredHeight();
                if (currentY == svComment.getHeight() + y) {
                    System.out.println("到底部,加载更多");
                    if (!isLongCommentLoad) {
                        isLongCommentLoad = true;
                        initLongCommentsData();
                    }

                    if(shortCommentOpen){
                        initShortCommentsData();
                    }


                }
            }
        });

        //点击展开短评论
        tvCommentShortCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shortCommentOpen){//点击关闭短评论
                    shortCommentOpen = !shortCommentOpen;
                    commentShortListView.setVisibility(View.GONE);
                }else{//点击展开短评论

                    commentShortListView.setVisibility(View.VISIBLE);
                    initShortCommentsData();//初始化短评论数据
                }
            }
        });

        initLongCommentsData();//初始化长评论数据

    }

    //初始化长评论数据
    private void initLongCommentsData() {

        if(isLongCommentLoad){
            if(newsLongCommentsJson.comments.size() > 0){
                String moreLongCommentUrl = longCommentUrl + "/before/" + newsLongCommentsJson.comments.get(newsLongCommentsJson.comments.size() -1).id;
                mHttpUtils.send(HttpRequest.HttpMethod.GET, moreLongCommentUrl, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String longCommentJson = responseInfo.result;
                        parseLongComment(longCommentJson);
                    }
                    @Override
                    public void onFailure(HttpException e, String s) {

                    }
                });
            }

        }else{
            longCommentUrl = ServerUrls.NEWS_LONG_COMMENTS;
            longCommentUrl = longCommentUrl.replaceAll("'REPLACE_ID'", id);

            mHttpUtils.send(HttpRequest.HttpMethod.GET, longCommentUrl, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    longJson = responseInfo.result;
                    parseLongComment(longJson);//把初始化的长评论和短评论数据封装到bean中
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(CommentActivity.this , "获取长评论失败" , Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    //初始化短评论数据
    private void initShortCommentsData() {

        if(shortCommentOpen){
            if(newsShortCommentsJson.comments.size() > 0){
                String moreShortCommentUrl = shortCommentUrl + "/before/" + newsShortCommentsJson.comments.get(newsShortCommentsJson.comments.size() -1).id;
                mHttpUtils.send(HttpRequest.HttpMethod.GET, moreShortCommentUrl, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String moreShortJson = responseInfo.result;
                        parseShortComment(moreShortJson);
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Toast.makeText(CommentActivity.this , "获取更多短评论失败" , Toast.LENGTH_SHORT).show();
                    }
                });
            }


        }else{
            shortCommentUrl = ServerUrls.NEWS_SHORT_COMMENTS;
            shortCommentUrl = shortCommentUrl.replaceAll("'REPLACE_ID'", id);

            mHttpUtils.send(HttpRequest.HttpMethod.GET, shortCommentUrl, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    shortJson = responseInfo.result;
                    parseShortComment(shortJson);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(CommentActivity.this , "加载短评失败" , Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    //转换json成bean;
    private void parseLongComment(String longJson){

        //判断是否是加载更多的操作
        if(isLongCommentLoad){
            NewsLongCommentsJson logdLongCommentsJson = mGson.fromJson(longJson, NewsLongCommentsJson.class);
            if(logdLongCommentsJson.comments .size() == 0){
                isLongCommentLoad = false;
                return;
            }else{//判断有没有更多数据
                longCommentsList.addAll(logdLongCommentsJson.comments);
                commentLongAdapter.notifyDataSetChanged();
                isLongCommentLoad = false;
            }

        }else{
            newsLongCommentsJson = mGson.fromJson(longJson, NewsLongCommentsJson.class);

            longCommentsList = newsLongCommentsJson.comments;

            int longCommentsSize = newsLongCommentsJson.comments.size();

            if(longCommentsSize > 0){
                commentLongListView.setVisibility(View.VISIBLE);
                llLongCommentEmpty.setVisibility(View.GONE);
            }else{//如果长评论为空隐藏listView,显示空页面;
                commentLongListView.setVisibility(View.GONE);
                llLongCommentEmpty.setVisibility(View.VISIBLE);
                return;
            }
            //设置评论列表适配器
            setLongCommentsAdapter();
        }
    }


    //设置评论列表适配器
    private void setLongCommentsAdapter(){
        commentLongAdapter = new CommentLongAdapter();
        commentLongListView.setAdapter(commentLongAdapter);
    }


    private void parseShortComment(String shortJson){
        if(shortCommentOpen){
            NewsShortCommentsJson newsShortCommentsJson = mGson.fromJson(shortJson, NewsShortCommentsJson.class);
            int moreShortCommentsSize = newsShortCommentsJson.comments.size();
            if(moreShortCommentsSize >0){
                shortCommentsList.addAll(newsShortCommentsJson.comments);
                commentShortAdapter.notifyDataSetChanged();
            }else{
                return;
            }

        }else{
            newsShortCommentsJson = mGson.fromJson(shortJson, NewsShortCommentsJson.class);
            shortCommentsList = newsShortCommentsJson.comments;
            int shortCommentsSize = shortCommentsList.size();
            if(shortCommentsSize > 0){
                setShortCommentsAdapter();
                shortCommentOpen = true;
            }else{
                return;
            }
        }


    }

    private void setShortCommentsAdapter(){

        commentShortAdapter = new CommentShortAdapter();
        commentShortListView.setAdapter(commentShortAdapter);
    }





    /**
     * 评论列表适配器
     */
    private class CommentLongAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return longCommentsList.size();
        }

        @Override
        public Object getItem(int position) {
            return longCommentsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

                final CommentLongHolder holder;
                if(convertView == null){
                    convertView = View.inflate(CommentActivity.this , R.layout.comment_long_item , null);
                    holder = new CommentLongHolder();

                    holder.ivLongCommenterAvatar = (ImageView) convertView.findViewById(R.id.iv_longcommenter_avatar);
                    holder.tvLongCommenterName = (TextView) convertView.findViewById(R.id.tv_longcommenter_name);
                    holder.tvLongCommenterPariseCount = (TextView) convertView.findViewById(R.id.tv_longcommenter_parise_count);
                    holder.tvLongCommentContent = (TextView) convertView.findViewById(R.id.tv_longcomment_content);
                    holder.tvShortCommentReply = (TextView) convertView.findViewById(R.id.tv_comment_reply);

                    convertView.setTag(holder);

                }else{
                    holder = (CommentLongHolder) convertView.getTag();
                }

                bitmapUtils.display(holder.ivLongCommenterAvatar, longCommentsList.get(position).avatar);
                holder.tvLongCommenterName.setText(longCommentsList.get(position).author);
                holder.tvLongCommenterPariseCount.setText(longCommentsList.get(position).likes);
                holder.tvLongCommentContent.setText(longCommentsList.get(position).content);

                if(longCommentsList.get(position).reply_to != null){
                    NewsLongCommentsJson.CommentReplay reply = longCommentsList.get(position).reply_to;
                    holder.tvShortCommentReply.setText(Html.fromHtml("<font color='#000000'><b>//" + reply.author + "</b></font>" + ":" + reply.content));
                    holder.tvShortCommentReply.setVisibility(View.VISIBLE);

                }else{
                    holder.tvShortCommentReply.setVisibility(View.GONE);
                }

                return convertView;

        }
    }

    class CommentLongHolder{
        ImageView ivLongCommenterAvatar;
        TextView tvLongCommenterName;
        TextView tvLongCommenterPariseCount;
        TextView tvLongCommentContent;
        TextView tvShortCommentReply;
    }


    /**
     * 评论列表适配器
     */
    private class CommentShortAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return shortCommentsList.size();
        }

        @Override
        public Object getItem(int position) {
            return shortCommentsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final CommentShortHolder holder;
            if(convertView == null){
                convertView = View.inflate(CommentActivity.this , R.layout.comment_long_item , null);
                holder = new CommentShortHolder();

                holder.ivShortCommenterAvatar = (ImageView) convertView.findViewById(R.id.iv_longcommenter_avatar);
                holder.tvShortCommenterName = (TextView) convertView.findViewById(R.id.tv_longcommenter_name);
                holder.tvShortCommenterPariseCount = (TextView) convertView.findViewById(R.id.tv_longcommenter_parise_count);
                holder.tvShortCommentContent = (TextView) convertView.findViewById(R.id.tv_longcomment_content);
                holder.tvShortCommentReply = (TextView) convertView.findViewById(R.id.tv_comment_reply);

                convertView.setTag(holder);

            }else{
                holder = (CommentShortHolder) convertView.getTag();
            }

            bitmapUtils.display(holder.ivShortCommenterAvatar, shortCommentsList.get(position).avatar);
            holder.tvShortCommenterName.setText(shortCommentsList.get(position).author);
            holder.tvShortCommenterPariseCount.setText(shortCommentsList.get(position).likes);
            holder.tvShortCommentContent.setText(shortCommentsList.get(position).content);

            if(shortCommentsList.get(position).reply_to != null){
                NewsShortCommentsJson.CommentReplay reply = shortCommentsList.get(position).reply_to;
                holder.tvShortCommentReply.setText(Html.fromHtml("<font color='#000000'><b>//" + reply.author + "</b></font>" + ":" + reply.content));
                holder.tvShortCommentReply.setVisibility(View.VISIBLE);

            }else{
                holder.tvShortCommentReply.setVisibility(View.GONE);
            }

            return convertView;

        }
    }

    class CommentShortHolder{
        ImageView ivShortCommenterAvatar;
        TextView tvShortCommenterName;
        TextView tvShortCommenterPariseCount;
        TextView tvShortCommentContent;
        TextView tvShortCommentReply;
    }


}
