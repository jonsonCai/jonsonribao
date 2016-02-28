package com.jonson.jonsonribao.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by jonson on 2016/2/23.
 */
public class ThemeListView extends ListView{

    private int startX;
    private int startY;

    public ThemeListView(Context context) {
        super(context);
    }

    public ThemeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            getParent().requestDisallowInterceptTouchEvent(false);
            System.out.println("下手");
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(false);
                startX = (int) ev.getRawX();
                startY = (int) ev.getRawY();
                System.out.println("拦截");
                break;
            case MotionEvent.ACTION_MOVE:
                int endX = (int) ev.getRawX();
                int endY = (int) ev.getRawY();
                int rangeX = endX - startX;
                int rangeY = endY - startY;
                if(Math.abs(rangeY) > Math.abs(rangeX)){
                    return true;
                }
                break;
            default:

                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

}
