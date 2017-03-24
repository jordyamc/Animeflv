package knf.animeflv.CustomSettingsIntro.fragments;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import agency.tango.materialintroscreen.SlideFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.R;
import knf.animeflv.ThemeHolder;
import knf.animeflv.Utils.ThemeUtils;

public class ThemeFragment extends SlideFragment {

    private final int DURATION = 500;
    @BindView(R.id.color_choose)
    RelativeLayout color_choose;

    @BindView(R.id.color)
    ImageView color;
    @BindView(R.id.accent)
    ImageView accent;

    @BindView(R.id.linear)
    LinearLayout linearLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.card1)
    CardView card1;
    @BindView(R.id.card2)
    CardView card2;
    @BindView(R.id.card3)
    CardView card3;

    @BindView(R.id.tit1)
    TextView title1;
    @BindView(R.id.cap1)
    TextView cap1;

    @BindView(R.id.tit2)
    TextView title2;
    @BindView(R.id.cap2)
    TextView cap2;

    @BindView(R.id.tit3)
    TextView title3;
    @BindView(R.id.cap3)
    TextView cap3;

    private ClickCallback callback;
    private Activity activity;

    public static ThemeFragment get(Activity act, ClickCallback clickCallback) {
        ThemeFragment fragment = new ThemeFragment();
        fragment.setInterface(clickCallback);
        fragment.setActivity(act);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.theme_fragment, container, false);
        ButterKnife.bind(this, view);
        ThemeHolder.isDark = ThemeUtils.isAmoled(getA());
        ThemeHolder.accentColor = ThemeUtils.getAcentColor(getA());
        toolbar.setTitleTextColor(ColorsRes.Blanco(getA()));
        toolbar.setTitle("Recientes");
        toolbar.setBackgroundColor(ThemeUtils.isAmoled(getA()) ? ColorsRes.Negro(getA()) : ColorsRes.Prim(getA()));
        linearLayout.setBackgroundColor(ThemeUtils.isAmoled(getA()) ? ColorsRes.Negro(getA()) : ColorsRes.Blanco(getA()));
        color.setColorFilter(ThemeUtils.isAmoled(getA()) ? ColorsRes.Prim(getA()) : ColorsRes.Gris(getA()));
        accent.setColorFilter(ThemeUtils.getAcentColor(getA()));
        card1.setCardBackgroundColor(ThemeUtils.isAmoled(getA()) ? ColorsRes.Prim(getA()) : ColorsRes.Blanco(getA()));
        title1.setTextColor(ThemeUtils.isAmoled(getA()) ? ColorsRes.Blanco(getA()) : Color.parseColor("#4d4d4d"));
        title2.setTextColor(ThemeUtils.isAmoled(getA()) ? ColorsRes.Blanco(getA()) : Color.parseColor("#4d4d4d"));
        title3.setTextColor(ThemeUtils.isAmoled(getA()) ? ColorsRes.Blanco(getA()) : Color.parseColor("#4d4d4d"));
        cap1.setTextColor(ThemeUtils.getAcentColor(getA()));
        cap2.setTextColor(ThemeUtils.getAcentColor(getA()));
        cap3.setTextColor(ThemeUtils.getAcentColor(getA()));
        color_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null)
                    callback.onClick();
            }
        });
        return view;
    }

    public void setActivity(Activity a) {
        activity = a;
    }

    private Activity getA() {
        if (activity == null) {
            return getActivity();
        } else {
            return activity;
        }
    }

    public void setInterface(ClickCallback clickCallback) {
        callback = clickCallback;
    }

    public void onColorChange() {
        ValueAnimator animM = ValueAnimator.ofInt((ThemeHolder.old_isDark ? ColorsRes.Negro(getA()) : ColorsRes.Prim(getA())), (ThemeHolder.isDark ? ColorsRes.Negro(getA()) : ColorsRes.Prim(getA())));
        animM.setEvaluator(new ArgbEvaluator());
        animM.setDuration(DURATION);
        animM.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (toolbar != null)
                            toolbar.setBackgroundColor((Integer) animation.getAnimatedValue());
                    }
                });
            }
        });
        animM.start();

        ValueAnimator animB = ValueAnimator.ofInt((ThemeHolder.old_isDark ? ColorsRes.Negro(getA()) : ColorsRes.Blanco(getA())), (ThemeHolder.isDark ? ColorsRes.Negro(getA()) : ColorsRes.Blanco(getA())));
        animB.setEvaluator(new ArgbEvaluator());
        animB.setDuration(DURATION);
        animB.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (linearLayout != null)
                        linearLayout.setBackgroundColor((Integer) animation.getAnimatedValue());
                    }
                });
            }
        });
        animB.start();

        ValueAnimator animI = ValueAnimator.ofInt((ThemeHolder.old_isDark ? ColorsRes.Negro(getA()) : ColorsRes.Gris(getA())), (ThemeHolder.isDark ? ColorsRes.Negro(getA()) : ColorsRes.Gris(getA())));
        animI.setEvaluator(new ArgbEvaluator());
        animI.setDuration(DURATION);
        animI.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (color != null)
                        color.setColorFilter((Integer) animation.getAnimatedValue());
                    }
                });
            }
        });
        animI.start();

        ValueAnimator animC = ValueAnimator.ofInt((ThemeHolder.old_isDark ? ColorsRes.Prim(getA()) : ColorsRes.Blanco(getA())), (ThemeHolder.isDark ? ColorsRes.Prim(getA()) : ColorsRes.Blanco(getA())));
        animC.setEvaluator(new ArgbEvaluator());
        animC.setDuration(DURATION);
        animC.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (card1 != null)
                        card1.setCardBackgroundColor((Integer) animation.getAnimatedValue());
                    }
                });
            }
        });
        animC.start();

        ValueAnimator animT = ValueAnimator.ofInt((ThemeHolder.old_isDark ? ColorsRes.Blanco(getA()) : Color.parseColor("#4d4d4d")), (ThemeHolder.isDark ? ColorsRes.Blanco(getA()) : Color.parseColor("#4d4d4d")));
        animT.setEvaluator(new ArgbEvaluator());
        animT.setDuration(DURATION);
        animT.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (title1 != null && title2 != null && title3 != null) {
                            title1.setTextColor((Integer) animation.getAnimatedValue());
                            title2.setTextColor((Integer) animation.getAnimatedValue());
                            title3.setTextColor((Integer) animation.getAnimatedValue());
                        }
                    }
                });
            }
        });
        animT.start();

        ValueAnimator animS = ValueAnimator.ofInt((ThemeHolder.old_accentColor == -1 ? ThemeUtils.getAcentColor(getA()) : ThemeHolder.old_accentColor), ThemeHolder.accentColor);
        animS.setEvaluator(new ArgbEvaluator());
        animS.setDuration(DURATION);
        animS.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                getA().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (cap1 != null && cap2 != null && cap3 != null && accent != null) {
                            cap1.setTextColor((Integer) animation.getAnimatedValue());
                            cap2.setTextColor((Integer) animation.getAnimatedValue());
                            cap3.setTextColor((Integer) animation.getAnimatedValue());
                            accent.setColorFilter((Integer) animation.getAnimatedValue());
                        }
                    }
                });
            }
        });
        animS.start();
    }

    @Override
    public int backgroundColor() {
        return R.color.intro_4;
    }

    @Override
    public int buttonsColor() {
        return R.color.intro_4_dark;
    }

    @Override
    public boolean canMoveFurther() {
        return true;
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return super.cantMoveFurtherErrorMessage();
    }

    public interface ClickCallback {
        void onClick();
    }
}
