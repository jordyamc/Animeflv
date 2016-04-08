package knf.animeflv;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

/**
 * Created by Jordy on 06/04/2016.
 */
public class Intronew extends IntroActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_title_1)
                .description(R.string.intro_desc_1)
                .image(R.drawable.app_icon_intro)
                .background(R.color.intro_1)
                .backgroundDark(R.color.intro_1_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_title_2)
                .description(R.string.intro_desc_2)
                .image(R.drawable.recientes)
                .background(R.color.intro_2)
                .backgroundDark(R.color.intro_2_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_title_3)
                .description(R.string.intro_desc_3)
                .image(R.drawable.desc_stream_rez)
                .background(R.color.intro_3)
                .backgroundDark(R.color.intro_3_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_title_4)
                .description(R.string.intro_desc_4)
                .image(R.drawable.comparacion_rez)
                .background(R.color.intro_4)
                .backgroundDark(R.color.intro_4_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_title_5)
                .description(R.string.intro_desc_5)
                .image(R.drawable.info_rez_comp)
                .background(R.color.intro_5)
                .backgroundDark(R.color.intro_5_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_title_6)
                .description(R.string.intro_desc_6)
                .image(R.drawable.secs)
                .background(R.color.intro_6)
                .backgroundDark(R.color.intro_6_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_title_7)
                .description(R.string.intro_desc_7)
                .image(R.drawable.fav)
                .background(R.color.intro_7)
                .backgroundDark(R.color.intro_7_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_title_8)
                .description(R.string.intro_desc_8)
                .image(R.drawable.nots)
                .background(R.color.intro_8)
                .backgroundDark(R.color.intro_8_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_title_9)
                .description(R.string.intro_desc_9)
                .image(R.drawable.conf)
                .background(R.color.intro_9)
                .backgroundDark(R.color.intro_9_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_title_10)
                .description(R.string.intro_desc_10)
                .image(R.drawable.block)
                .background(R.color.intro_10)
                .backgroundDark(R.color.intro_10_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_title_11)
                .description(R.string.intro_desc_11)
                .image(R.drawable.listo)
                .background(R.color.intro_11)
                .backgroundDark(R.color.intro_11_dark)
                .build());
        setSkipEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.intro_1));
        }
        addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position==10){
                    colorCase(position);
                }
            }
            @Override
            public void onPageSelected(int position) {
                colorCase(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    private void colorCase(int c){
        switch (c){
            case 0:
                changeNavigationColor(R.color.intro_1);
                break;
            case 1:
                changeNavigationColor(R.color.intro_2);
                break;
            case 2:
                changeNavigationColor(R.color.intro_3);
                break;
            case 3:
                changeNavigationColor(R.color.intro_4);
                break;
            case 4:
                changeNavigationColor(R.color.intro_5);
                break;
            case 5:
                changeNavigationColor(R.color.intro_6);
                break;
            case 6:
                changeNavigationColor(R.color.intro_7);
                break;
            case 7:
                changeNavigationColor(R.color.intro_8);
                break;
            case 8:
                changeNavigationColor(R.color.intro_9);
                break;
            case 9:
                changeNavigationColor(R.color.intro_10);
                break;
            case 10:
                changeNavigationColor(R.color.intro_11);
                break;
        }
    }

    @TargetApi(21)
    private void changeNavigationColor(int code){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int from = getWindow().getNavigationBarColor();
            int to = getResources().getColor(code);
            ValueAnimator colorAnimation = ValueAnimator.ofArgb(from, to);
            colorAnimation.setDuration(200);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    getWindow().setNavigationBarColor((Integer) animator.getAnimatedValue());
                }
            });
            colorAnimation.start();
        }
    }
}
