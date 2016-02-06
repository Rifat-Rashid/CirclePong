package com.splice.rifatrashid.circlepong;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.viewpagerindicator.UnderlinePageIndicator;


/**
 * Created by Reefer on 11/2/15.
 */
public class ViewpagerActivity extends AppCompatActivity {

    ViewPager defaultViewPager;
    private CollectionPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_activity);

        //check if user is using android lolipop or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        adapter = new CollectionPagerAdapter(getSupportFragmentManager());
        defaultViewPager = (ViewPager) findViewById(R.id.viewpager_default);
        defaultViewPager.setOffscreenPageLimit(4);
        // AlertFragment alertFragment = new AlertFragment();
        // alertFragment.show(fm, "null");
        //Underline indicator
        final UnderlinePageIndicator defaultIndicator = (UnderlinePageIndicator) findViewById(R.id.title);
        defaultViewPager.setAdapter(adapter);
        //defaultIndicator.setOnPageChangeListener(new CustomOnPageChangeListener());
        defaultIndicator.setFades(false);
        defaultIndicator.setViewPager(defaultViewPager);
        defaultIndicator.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        defaultIndicator.setSelectedColor(0xFF1fb4dc);
                        break;
                    case 1:
                        defaultIndicator.setSelectedColor(0xFFb270de);
                        break;
                    case 2:
                        defaultIndicator.setSelectedColor(0xFF96c503);
                        break;
                    case 3:
                        defaultIndicator.setSelectedColor(0xFFff4d50);
                        break;
                    case 4:
                        defaultIndicator.setSelectedColor(0xFF1fb4dc);

/*
                        new CountDownTimer(2000, 1000) {
                            public void onTick(long milli) {

                            }

                            public void onFinish() {
                                Intent i = new Intent(ViewpagerActivity.this, MainActivity.class);
                                startActivity(i);
                            }
                        }.start();
                        */
                        break;

                }
                RAM.pageSlide = position;
                //defaultIndicator.setCurrentItem(position);
            }
        });

        //rotate incoming view
        defaultViewPager.setPageTransformer(true, new SliderPageTransformer());


    }

    static class CustomOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            /*
            if(position < (defaultPagerAdapter.getCount() -1) && position < (colors.length - 1)){
                defaultViewPager.setBackgroundColor((Integer) argbEvaluator.evaluate(positionOffset, colors[position], colors[position + 1]));
            }else{
                //the last page color
                defaultViewPager.setBackgroundColor(colors[colors.length - 1]);
            }
            */
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    class FixedSpeedScroller extends Scroller {
        private int mDuration = 800;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
    }
}


