package knf.animeflv;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import knf.animeflv.CustomSettingsIntro.fragments.BackupFragmentNew;
import knf.animeflv.CustomSettingsIntro.fragments.CloudFragmentNew;
import knf.animeflv.CustomSettingsIntro.fragments.ThemeFragmentNew;
import knf.animeflv.Utils.ThemeUtils;

import com.afollestad.materialdialogs.Theme;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;


public class Intronew extends IntroActivity implements ColorChooserDialog.ColorCallback {
    private ThemeFragmentNew themeFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(new FragmentSlide.Builder()
                .fragment(BackupFragmentNew.get(this, () -> {
                    setButtonBackFunction(BUTTON_BACK_FUNCTION_SKIP);
                    ThemeHolder.isDark = ThemeUtils.isAmoled(Intronew.this);
                    ThemeHolder.accentColor = ThemeUtils.getAcentColor(Intronew.this);
                    themeFragment.onColorChange();
                }))
                .background(R.color.intro_1)
                .backgroundDark(R.color.intro_1_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .layout(R.layout.simple_slide)
                .title(R.string.intro_title_2)
                .description(R.string.intro_desc_2)
                .image(R.drawable.recientes)
                .background(R.color.intro_2)
                .backgroundDark(R.color.intro_2_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .layout(R.layout.simple_slide)
                .title(R.string.intro_title_3)
                .description(R.string.intro_desc_3)
                .image(R.drawable.desc_stream_rez)
                .background(R.color.intro_3)
                .backgroundDark(R.color.intro_3_dark)
                .build());
        themeFragment = ThemeFragmentNew.get(this, this::onColorChoose);
        addSlide(new FragmentSlide.Builder()
                .fragment(themeFragment)
                .background(R.color.intro_4)
                .backgroundDark(R.color.intro_4_dark)
                .build());
        addSlide(new FragmentSlide.Builder()
                .fragment(CloudFragmentNew.get())
                .background(R.color.blanco)
                .backgroundDark(R.color.dropbox)
                .build());
        addSlide(new SimpleSlide.Builder()
                .layout(R.layout.simple_slide)
                .title(R.string.intro_title_5)
                .description(R.string.intro_desc_5)
                .image(R.drawable.anim_info)
                .background(R.color.intro_5)
                .backgroundDark(R.color.intro_5_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .layout(R.layout.simple_slide)
                .title(R.string.intro_title_6)
                .description(R.string.intro_desc_6)
                .image(R.drawable.secs)
                .background(R.color.intro_6)
                .backgroundDark(R.color.intro_6_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .layout(R.layout.simple_slide)
                .title(R.string.intro_title_7)
                .description(R.string.intro_desc_7)
                .image(R.drawable.fav)
                .background(R.color.intro_7)
                .backgroundDark(R.color.intro_7_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .layout(R.layout.simple_slide)
                .title(R.string.intro_title_8)
                .description(R.string.intro_desc_8)
                .image(R.drawable.nots)
                .background(R.color.intro_8)
                .backgroundDark(R.color.intro_8_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .layout(R.layout.simple_slide)
                .title(R.string.intro_title_9)
                .description(R.string.intro_desc_9)
                .image(R.drawable.conf)
                .background(R.color.intro_9)
                .backgroundDark(R.color.intro_9_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .layout(R.layout.simple_slide)
                .title(R.string.intro_title_10)
                .description(R.string.intro_desc_10)
                .image(R.drawable.block)
                .background(R.color.intro_10)
                .backgroundDark(R.color.intro_10_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .layout(R.layout.simple_slide)
                .title(R.string.intro_title_11)
                .description(R.string.intro_desc_11)
                .image(R.drawable.listo)
                .background(R.color.intro_11)
                .backgroundDark(R.color.intro_11_dark)
                .build());
        setButtonBackFunction(BUTTON_BACK_FUNCTION_BACK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.intro_1));
        }

        addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position==11){
                    colorCase(position);
                    Intronew.this.getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("intro", true).apply();
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
                changeNavigationColor(R.color.blanco);
                break;
            case 5:
                changeNavigationColor(R.color.intro_5);
                break;
            case 6:
                changeNavigationColor(R.color.intro_6);
                break;
            case 7:
                changeNavigationColor(R.color.intro_7);
                break;
            case 8:
                changeNavigationColor(R.color.intro_8);
                break;
            case 9:
                changeNavigationColor(R.color.intro_9);
                break;
            case 10:
                changeNavigationColor(R.color.intro_10);
                break;
            case 11:
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
            colorAnimation.addUpdateListener(animator -> getWindow().setNavigationBarColor((Integer) animator.getAnimatedValue()));
            colorAnimation.start();
        }
    }

    private void onColorChoose() {
        int[] colorl = new int[]{
                ColorsRes.Gris(this),
                ColorsRes.Prim(this)
        };
        ColorChooserDialog dialog = new ColorChooserDialog.Builder(this, R.string.color_chooser_prim)
                .theme(ThemeUtils.isAmoled(this) ? Theme.DARK : Theme.LIGHT)
                .customColors(colorl, null)
                .dynamicButtonColor(true)
                .allowUserColorInput(false)
                .allowUserColorInputAlpha(false)
                .doneButton(R.string.next)
                .cancelButton(R.string.cancel)
                .preselect(ThemeUtils.isAmoled(this) ? ColorsRes.Dark(this) : ColorsRes.Gris(this))
                .accentMode(true)
                .build();
        dialog.show(this);
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
        if (selectedColor == ColorsRes.Prim(this) || selectedColor == ColorsRes.Gris(this)) {
            ThemeHolder.old_isDark = ThemeHolder.isDark;
            ThemeHolder.isDark = selectedColor == ColorsRes.Prim(this);
            int[] colorl = new int[]{
                    ColorsRes.Naranja(this),
                    ColorsRes.Rojo(this),
                    ColorsRes.Gris(this),
                    ColorsRes.Verde(this),
                    ColorsRes.Rosa(this),
                    ColorsRes.Morado(this)
            };
            new ColorChooserDialog.Builder(this, R.string.color_chooser)
                    .theme(ThemeUtils.isAmoled(this) ? Theme.DARK : Theme.LIGHT)
                    .customColors(colorl, null)
                    .dynamicButtonColor(true)
                    .allowUserColorInput(false)
                    .allowUserColorInputAlpha(false)
                    .doneButton(R.string.ok)
                    .cancelButton(R.string.back)
                    .preselect(PreferenceManager.getDefaultSharedPreferences(this).getInt("accentColor", ColorsRes.Naranja(this)))
                    .accentMode(true)
                    .build().show(this);
        } else {
            ThemeHolder.old_accentColor = ThemeHolder.accentColor;
            ThemeHolder.accentColor = selectedColor;
            ThemeHolder.applyThemeNoReset(this);
            themeFragment.onColorChange();
        }
    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog colorChooserDialog) {

    }

    @Override
    public void finish() {
        startActivity(new Intent(this, newMain.class));
        Intronew.this.getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("intro", true).apply();
        super.finish();
    }

    @Override
    public void onBackPressed() {
    }
}
