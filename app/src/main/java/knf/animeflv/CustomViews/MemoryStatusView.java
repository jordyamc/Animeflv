package knf.animeflv.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.R;
import knf.animeflv.Utils.CacheManager;
import knf.animeflv.Utils.ThemeUtils;

/**
 * Created by Jordy on 14/12/2017.
 */

public class MemoryStatusView extends RelativeLayout {

    @BindView(R.id.img)
    ImageView imageView;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.switch_state)
    SwitchCompat switchCompat;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.size)
    TextView size;
    @BindView(R.id.clear)
    Button clear;
    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.separator)
    View separator;

    private boolean showSwitch = false;
    private Drawable image;
    private String title_text, description_text, id;
    private File file;
    private boolean withDirectory, exclude;
    private ClearStatus clearStatus;

    public MemoryStatusView(Context context) {
        super(context);
        inflateView(context);
    }

    public MemoryStatusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflateView(context);
        setAttrs(context, attrs);
    }

    public MemoryStatusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateView(context);
        setAttrs(context, attrs);
    }

    private void inflateView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.memory_control_section, this);
    }

    private void setAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MemoryStatusView, 0, 0);
        id = typedArray.getString(R.styleable.MemoryStatusView_msv_id);
        title_text = typedArray.getString(R.styleable.MemoryStatusView_msv_title);
        description_text = typedArray.getString(R.styleable.MemoryStatusView_msv_description);
        image = typedArray.getDrawable(R.styleable.MemoryStatusView_msv_img);
        showSwitch = typedArray.getBoolean(R.styleable.MemoryStatusView_msv_showSwitch, true);
        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        title.setText(title_text);
        description.setText(description_text);
        imageView.setImageDrawable(image);
        if (!showSwitch) {
            switchCompat.setVisibility(GONE);
        } else {
            switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("save_" + id, b).apply();
                }
            });
        }
        clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
            }
        });
    }

    private void setTheme(ThemeUtils.Theme theme) {
        imageView.setColorFilter(theme.iconFilter, PorterDuff.Mode.SRC_ATOP);
        title.setTextColor(theme.secondaryTextColor);
        description.setTextColor(theme.secondaryTextColor);
        size.setTextColor(theme.textColorNormal);
        separator.setBackgroundColor(theme.separator);
    }

    public void setSizeDir(ClearStatus clearStatus, File file, boolean withDirectory, boolean exclude, ThemeUtils.Theme theme) {
        this.file = file;
        this.withDirectory = withDirectory;
        this.exclude = exclude;
        this.clearStatus = clearStatus;
        updateSize();
        setTheme(theme);
    }

    public void updateSize() {
        post(new Runnable() {
            @Override
            public void run() {
                loading.setVisibility(VISIBLE);
                size.setVisibility(GONE);
                clear.setEnabled(false);
            }
        });
        if (file != null) {
            if (withDirectory) {
                CacheManager.asyncGetFormatedFileSize(file, new CacheManager.OnFinishCount() {
                    @Override
                    public void counted(final String formated) {
                        setSize(formated);
                    }
                });
            } else {
                CacheManager.asyncGetFormatedCacheSize(file, new CacheManager.OnFinishCount() {
                    @Override
                    public void counted(final String formated) {
                        setSize(formated);
                    }
                });
            }
        } else {
            setVisibility(GONE);
        }
    }

    private void setSize(final String sizeText) {
        post(new Runnable() {
            @Override
            public void run() {
                size.setText(sizeText);
                clear.setEnabled(true);
                loading.setVisibility(GONE);
                AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
                alphaAnimation.setDuration(500);
                size.setVisibility(VISIBLE);
                size.startAnimation(alphaAnimation);
            }
        });
    }

    public void clear() {
        post(new Runnable() {
            @Override
            public void run() {
                clear.setEnabled(false);
            }
        });
        clearStatus.onStartLoading();
        CacheManager.invalidateCache(file, withDirectory, exclude, new CacheManager.OnInvalidateCache() {
            @Override
            public void onFinish() {
                post(new Runnable() {
                    @Override
                    public void run() {
                        clear.setEnabled(true);
                    }
                });
                clearStatus.onStopLoading();
                clearStatus.onRechargeTotal();
                updateSize();
            }
        });
    }

    public static interface ClearStatus {
        void onStartLoading();

        void onStopLoading();

        void onRechargeTotal();
    }
}
