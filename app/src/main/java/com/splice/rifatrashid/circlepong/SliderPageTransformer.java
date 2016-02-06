package com.splice.rifatrashid.circlepong;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Seize on 2/6/2016.
 */
public class SliderPageTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View page, float position) {

        if (page != null) {
            if (position < -1) {

            } else if (position <= 1) {
                page.findViewById(R.id.img).setRotationY(-position * 30);
                if (position <= 0) {
                    page.findViewById(R.id.img).setTranslationY(Math.abs(position) * (page.getHeight() / 4));
                } else {
                    if(RAM.pageSlide%2 == 1){
                        page.findViewById(R.id.img).setTranslationY(-(Math.abs(position) * (page.getHeight() / 4)));
                    }
                    page.findViewById(R.id.img).setTranslationY(Math.abs(position) * (page.getHeight() / 4));
                }
            } else {

            }
        }
    }
}
