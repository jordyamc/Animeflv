package knf.animeflv.CustomSettingsIntro;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.afollestad.materialdialogs.Theme;
import com.afollestad.materialdialogs.color.ColorChooserDialog;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import knf.animeflv.ColorsRes;
import knf.animeflv.CustomSettingsIntro.fragments.BackupFragment;
import knf.animeflv.CustomSettingsIntro.fragments.CloudFragment;
import knf.animeflv.CustomSettingsIntro.fragments.ThemeFragment;
import knf.animeflv.R;
import knf.animeflv.ThemeHolder;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.newMain;

public class CustomIntro extends MaterialIntroActivity implements ColorChooserDialog.ColorCallback {
    private ThemeFragment themeFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableLastSlideAlphaExitTransition(true);
        addSlide(BackupFragment.get(this, new BackupFragment.BackupCallback() {
            @Override
            public void onBackup() {
                setSkipButtonVisible();
                ThemeHolder.isDark = ThemeUtils.isAmoled(CustomIntro.this);
                ThemeHolder.accentColor = ThemeUtils.getAcentColor(CustomIntro.this);
                themeFragment.onColorChange();
            }
        }));
        addSlide(
                new SlideFragmentBuilder()
                        .title(getString(R.string.intro_title_2))
                        .description(getString(R.string.intro_desc_2))
                        .backgroundColor(R.color.intro_2)
                        .buttonsColor(R.color.intro_2_dark)
                        .image(R.drawable.recientes)
                        .build());
        addSlide(
                new SlideFragmentBuilder()
                        .title(getString(R.string.intro_title_3))
                        .description(getString(R.string.intro_desc_3))
                        .backgroundColor(R.color.intro_3)
                        .buttonsColor(R.color.intro_3_dark)
                        .image(R.drawable.desc_stream_rez)
                        .build());
        themeFragment = ThemeFragment.get(this, new ThemeFragment.ClickCallback() {
            @Override
            public void onClick() {
                onColorChoose();
            }
        });
        addSlide(themeFragment);
        addSlide(CloudFragment.get());
        addSlide(
                new SlideFragmentBuilder()
                        .title(getString(R.string.intro_title_5))
                        .description(getString(R.string.intro_desc_5))
                        .backgroundColor(R.color.intro_5)
                        .buttonsColor(R.color.intro_5_dark)
                        .image(R.drawable.anim_info)
                        .build());
        addSlide(
                new SlideFragmentBuilder()
                        .title(getString(R.string.intro_title_6))
                        .description(getString(R.string.intro_desc_6))
                        .backgroundColor(R.color.intro_6)
                        .buttonsColor(R.color.intro_6_dark)
                        .image(R.drawable.secs)
                        .build());
        addSlide(
                new SlideFragmentBuilder()
                        .title(getString(R.string.intro_title_7))
                        .description(getString(R.string.intro_desc_7))
                        .backgroundColor(R.color.intro_7)
                        .buttonsColor(R.color.intro_7_dark)
                        .image(R.drawable.fav)
                        .build());
        addSlide(
                new SlideFragmentBuilder()
                        .title(getString(R.string.intro_title_8))
                        .description(getString(R.string.intro_desc_8))
                        .backgroundColor(R.color.intro_8)
                        .buttonsColor(R.color.intro_8_dark)
                        .image(R.drawable.nots)
                        .build());
        addSlide(
                new SlideFragmentBuilder()
                        .title(getString(R.string.intro_title_10))
                        .description(getString(R.string.intro_desc_10))
                        .backgroundColor(R.color.intro_10)
                        .buttonsColor(R.color.intro_10_dark)
                        .image(R.drawable.block)
                        .build());
        addSlide(
                new SlideFragmentBuilder()
                        .title(getString(R.string.intro_title_11))
                        .description(getString(R.string.intro_desc_11))
                        .backgroundColor(R.color.intro_11)
                        .buttonsColor(R.color.intro_11_dark)
                        .image(R.drawable.listo)
                        .build());
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
                .cancelButton(android.R.string.cancel)
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
                    .doneButton(android.R.string.ok)
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
    public void onBackPressed() {

    }

    @Override
    public void onFinish() {
        getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("intro", true).apply();
        /*if (ThemeUtils.isTV(this)){
            startActivity(new Intent(this, Main.class));
        }else {
            startActivity(new Intent(this, newMain.class));
        }*/
        startActivity(new Intent(this, newMain.class));
    }

}
