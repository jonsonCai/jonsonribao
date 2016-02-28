package com.jonson.jonsonribao.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by jonson on 2016/2/27.
 */
public class MyScrollView extends ScrollView{
    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(scrollListener != null){
            scrollListener.onScrollChange(l , t , oldl , oldt);
        }
    }

    private OnScrollListener scrollListener;

    public void setOnScrollListener(OnScrollListener scrollListener){
        this.scrollListener = scrollListener;
    }

    public interface OnScrollListener{
       public void onScrollChange(int x , int y , int oldX , int oldY);
    }
}
