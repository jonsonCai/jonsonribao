package com.jonson.jonsonribao;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.jonson.jonsonribao.fragment.FragmentLeft;
import com.jonson.jonsonribao.fragment.FragmentMain;

public class MainActivity extends SlidingFragmentActivity {

    private FragmentMain fmMain;
    private FragmentLeft fmLeft;
    private float density;
    private FragmentManager fm;
    private SlidingMenu mSlidingMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.fragment_main);
        setBehindContentView(R.layout.fragment_left);


        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        mSlidingMenu = getSlidingMenu();
        System.out.println(density);
        mSlidingMenu.setBehindOffset((int) (50 * density));
        mSlidingMenu.setTouchModeAbove(SlidingMenu.LEFT);



        initFragment();
    }

    private void initFragment() {
        fm = getSupportFragmentManager();
        FragmentTransaction fmTransaction = fm.beginTransaction();
        fmTransaction.replace(R.id.fm_main, new FragmentMain(), "FRAGMENT_MAIN");
        fmTransaction.replace(R.id.fm_left, new FragmentLeft(), "FRAGMENT_LEFT");
        fmTransaction.commit();
    }

    public FragmentMain getFragmentMain(){
        return (FragmentMain)fm.findFragmentByTag("FRAGMENT_MAIN");
    }

    public FragmentLeft getFragmentLeft(){
        return (FragmentLeft)fm.findFragmentByTag("FRAGMENT_LEFT");

    }

    /**
     * 条状到条目内容
     * @param id
     */
    public void jumpToContent(String id){

        Intent intent = new Intent(this , WebContentActivity.class);
        intent.putExtra("id" , id);

        startActivity(intent);
    }

}
