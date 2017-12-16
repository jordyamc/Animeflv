package knf.animeflv.CustomViews;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.R;
import knf.animeflv.Utils.CacheManager;
import knf.animeflv.Utils.ThemeUtils;

/**
 * Created by Jordy on 14/12/2017.
 */

public class MemoryTotalView extends RelativeLayout {

    @BindView(R.id.img)
    ImageView imageView;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.size)
    TextView size;
    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.separator)
    View separator;

    public MemoryTotalView(Context context) {
        super(context);
        inflateView(context);
    }

    public MemoryTotalView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflateView(context);
    }

    public MemoryTotalView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateView(context);
    }

    private void inflateView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.memory_control_total, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void setTheme(ThemeUtils.Theme theme) {
        imageView.setColorFilter(theme.iconFilter, PorterDuff.Mode.SRC_ATOP);
        title.setTextColor(theme.secondaryTextColor);
        description.setTextColor(theme.secondaryTextColor);
        size.setTextColor(theme.textColorNormal);
        separator.setBackgroundColor(theme.separator);
        updateSize();
    }

    public void updateSize() {
        post(new Runnable() {
            @Override
            public void run() {
                loading.setVisibility(VISIBLE);
                size.setVisibility(GONE);
            }
        });
        CacheManager.asyncGetFormatedCacheSize(getContext(), new CacheManager.OnFinishCount() {
            @Override
            public void counted(final String formated) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        setSize(formated);
                    }
                });
            }
        });
    }

    private void setSize(final String sizeText) {
        post(new Runnable() {
            @Override
            public void run() {
                size.setText(sizeText);
                loading.setVisibility(GONE);
                AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
                alphaAnimation.setDuration(500);
                size.setVisibility(VISIBLE);
                size.startAnimation(alphaAnimation);
            }
        });
    }
}
