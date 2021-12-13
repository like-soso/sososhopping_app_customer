package com.sososhopping.customer.common.carousel;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.sososhopping.customer.R;

import java.util.ArrayList;

public class CarouselMethod {

    LinearLayout layoutIndicators;
    ViewPager2 viewPager;

    public CarouselMethod(LinearLayout layoutIndicators, ViewPager2 viewPager, Context context) {
        this.layoutIndicators = layoutIndicators;
        this.viewPager = viewPager;
        this.context = context;
    }

    Context context;

    public void setCarousel(ArrayList<String> imgUrls){
        //sample Images
        //ArrayList<String> sampleImage = new ArrayList<>();
        //sampleImage.add("https://postfiles.pstatic.net/MjAyMDA4MjZfNDgg/MDAxNTk4NDEyODEzOTE5.PXbPOn5JDUAI1voaqLg24k6NZ0sHfmf8cmhY2HC_I5sg.oyeDpP0pF4iPgSC5nwLsjNvYXhq5KN984lqmFD0FXgwg.JPEG.gmldms784/IMG_8194.jpg?type=w966");
        //sampleImage.add("https://cdn.pixabay.com/photo/2020/11/04/15/29/coffee-beans-5712780_1280.jpg");
        //sampleImage.add("https://cdn.pixabay.com/photo/2020/03/08/21/41/landscape-4913841_1280.jpg");
        //sampleImage.add("https://cdn.pixabay.com/photo/2020/09/02/18/03/girl-5539094_1280.jpg");
        //sampleImage.add( "https://cdn.pixabay.com/photo/2014/03/03/16/15/mosque-279015_1280.jpg");

        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(new CarouselImgAdapter(imgUrls));

       viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position, layoutIndicators);
            }
        });
        setupIndicators(imgUrls.size());
    }

    public void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(16, 8, 16, 8);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(context);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.drawable_bg_indicator_inactive));
            indicators[i].setLayoutParams(params);
            layoutIndicators.addView(indicators[i]);
        }
        setCurrentIndicator(0,layoutIndicators);
    }

    public void setCurrentIndicator(int position, LinearLayout layoutIndicators) {
        int childCount = layoutIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicators.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        context,
                        R.drawable.drawable_bg_indicator_active
                ));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        context,
                        R.drawable.drawable_bg_indicator_inactive
                ));
            }
        }
    }
}
