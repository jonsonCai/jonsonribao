package com.jonson.jonsonribao.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jonson.jonsonribao.MainActivity;
import com.jonson.jonsonribao.R;

/**
 * Created by jonson on 2016/2/20.
 */
public class HomeListView extends ListView{

    private MainActivity mActivity;
    private int startX;
    private int startY;
    private int headerViewHeight;
    private View mHeaderView;

    private static final int REFRESH_PULL = 0;
    private static final int REFRESH_RELAX = 1;
    private static final int REFRESHING = 2;

    private int PULL_STATE = REFRESH_PULL;
    private TextView tvPullTitle;
    private ImageView ivPullArrow;
    private RotateAnimation rotateDown;
    private RotateAnimation rotateUp;
    private ProgressBar pbRefreshArrow;


    public HomeListView(Context context) {
        super(context);
        mActivity = (MainActivity) context;
        setDividerHeight(0);
        initAnimation();
        initHeaderView();
    }

    public HomeListView(Context context, AttributeSet    attrs) {
        super(context, attrs);
        mActivity = (MainActivity) context;
        setDividerHeight(0);
        initAnimation();
        initHeaderView();
    }

    private void initHeaderView() {
        mHeaderView = View.inflate(mActivity, R.layout.header_view_pull, null);
        tvPullTitle = (TextView) mHeaderView.findViewById(R.id.tv_pull_title);
        ivPullArrow = (ImageView) mHeaderView.findViewById(R.id.iv_pull_arrow);
        pbRefreshArrow = (ProgressBar) mHeaderView.findViewById(R.id.pb_refresh_arrow);
        addHeaderView(mHeaderView);
        mHeaderView.measure(0, 0);
        headerViewHeight = mHeaderView.getMeasuredHeight();
        mHeaderView.setPadding(0, -headerViewHeight, 0, 0);

        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    System.out.println("最后显示条目" + getLastVisiblePosition() + "总条目" + getAdapter().getCount() + "headerView :" + getHeaderViewsCount());
                    if (getLastVisiblePosition() == getCount() - (getHeaderViewsCount()-1)) {
                        if(onLoadMoreListener != null){
                            onLoadMoreListener.onLoadMore();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN) {
            startX = (int) ev.getRawX();
            startY = (int) ev.getRawY();
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()){
            case MotionEvent.ACTION_MOVE:
                int endX = (int) ev.getRawX();
                int endY = (int) ev.getRawY();

                int rangeX = endX - startX;
                int rangeY = endY - startY;

                System.out.println("滑动距离" + -headerViewHeight + rangeY);

                if(rangeY > 0 && rangeY > rangeX){
                    if(getFirstVisiblePosition() == 0){
                        System.out.println("第一条");
                        mHeaderView.setPadding(0 , (-headerViewHeight + rangeY) / 2 , 0 , 0);

                        if((-headerViewHeight + rangeY) > 100 && PULL_STATE != REFRESH_RELAX){
                            PULL_STATE = REFRESH_RELAX;
                            tvPullTitle.setText("松开刷新");
                            ivPullArrow.startAnimation(rotateDown);

                        }else if((-headerViewHeight + rangeY) < 100 && PULL_STATE != REFRESH_PULL){
                            PULL_STATE = REFRESH_PULL;
                            tvPullTitle.setText("下拉刷新");
                            ivPullArrow.startAnimation(rotateUp);
                        }
                        return false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(PULL_STATE == REFRESH_RELAX){
                    //正在刷新
                    mHeaderView.setPadding(0, 0, 0, 0);
                    tvPullTitle.setText("正在刷新");
                    if(pdListener != null){
                        pdListener.onRefresh();
                        pbRefreshArrow.setVisibility(VISIBLE);
                        ivPullArrow.setVisibility(GONE);
                        return true;
                    }
                }else if(PULL_STATE == REFRESH_PULL){
                    PULL_STATE = REFRESHING;
                    mHeaderView.setPadding(0 , -headerViewHeight , 0 , 0);
                    tvPullTitle.setText("下拉刷新");
                    ivPullArrow.clearAnimation();
                }

                break;
        }
        return super.onTouchEvent(ev);
    }

    PullDowanRefreshListener pdListener;

    public void setOnPullDownRefreshListener(PullDowanRefreshListener pdListener){
        this.pdListener = pdListener;
    }

    public interface PullDowanRefreshListener{
        public void onRefresh();
    }

    OnLoadMoreListener onLoadMoreListener;

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener){
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener{
        public void onLoadMore();
    }

    public void RefreshFinish(){
        PULL_STATE = REFRESH_PULL;
        mHeaderView.setPadding(0, -headerViewHeight , 0 , 0);
        ivPullArrow.clearAnimation();
        ivPullArrow.setVisibility(VISIBLE);
        pbRefreshArrow.setVisibility(GONE);
    }


    private void initAnimation(){
        rotateDown = new RotateAnimation(0 , 180 , RotateAnimation.RELATIVE_TO_SELF , 0.5f , RotateAnimation.RELATIVE_TO_SELF , 0.5f);
        rotateDown.setFillAfter(true);
        rotateDown.setDuration(400);

        rotateUp = new RotateAnimation(180 , 0 , RotateAnimation.RELATIVE_TO_SELF , 0.5f , RotateAnimation.RELATIVE_TO_SELF , 0.5f);
        rotateUp.setFillAfter(true);
        rotateUp.setDuration(400);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
