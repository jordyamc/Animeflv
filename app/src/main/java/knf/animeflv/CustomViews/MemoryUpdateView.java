package knf.animeflv.CustomViews;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.R;
import knf.animeflv.Utils.CacheManager;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.Keys;
import knf.animeflv.Utils.ThemeUtils;

/**
 * Created by Jordy on 14/12/2017.
 */

public class MemoryUpdateView extends RelativeLayout {

    @BindView(R.id.img)
    ImageView imageView;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.size)
    TextView size;
    @BindView(R.id.clear)
    Button clear;
    @BindView(R.id.update)
    Button update;
    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.separator)
    View separator;

    private File file;
    private MemoryStatusView.ClearStatus clearStatus;

    public MemoryUpdateView(Context context) {
        super(context);
        inflateView(context);
    }

    public MemoryUpdateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflateView(context);
    }

    public MemoryUpdateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateView(context);
    }

    private void inflateView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.memory_control_apk, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
            }
        });
        update.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
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

    public void init(MemoryStatusView.ClearStatus clearStatus, ThemeUtils.Theme theme) {
        file = Keys.Dirs.UPDATE;
        setTheme(theme);
        this.clearStatus = clearStatus;
        if (!file.exists()) {
            setVisibility(GONE);
        } else {
            updateSize();
            checkApk();
        }
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
        CacheManager.asyncGetFormatedFileSize(Keys.Dirs.getUpdateFile(), new CacheManager.OnFinishCount() {
            @Override
            public void counted(final String formated) {
                setSize(formated);
            }
        });
    }

    private void checkApk() {
        try {
            PackageInfo extinfo = getContext().getPackageManager().getPackageArchiveInfo(file.getAbsolutePath(), 0);
            PackageInfo intinfo = getContext().getPackageManager().getPackageArchiveInfo(getContext().getPackageName(), 0);
            int extCode = extinfo.versionCode;
            int intCode = intinfo.versionCode;
            if (intCode >= extCode) {
                update.setVisibility(View.GONE);
            } else {
                update.setVisibility(VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            update.setVisibility(GONE);
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

    private void clear() {
        post(new Runnable() {
            @Override
            public void run() {
                clear.setEnabled(false);
            }
        });
        clearStatus.onStartLoading();
        file.delete();
        clearStatus.onStopLoading();
        clearStatus.onRechargeTotal();
        setVisibility(GONE);
    }

    private void update() {
        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE, FileUtil.init(getContext()).getUriForFile(file));
        intent.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, getContext().getPackageName());
        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, false);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        getContext().startActivity(intent);
    }
}
