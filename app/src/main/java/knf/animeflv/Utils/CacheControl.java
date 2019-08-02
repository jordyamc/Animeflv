package knf.animeflv.Utils;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.CustomViews.MemoryClearView;
import knf.animeflv.CustomViews.MemoryStatusView;
import knf.animeflv.CustomViews.MemoryTotalView;
import knf.animeflv.CustomViews.MemoryUpdateView;
import knf.animeflv.R;
import knf.animeflv.Splash;
import xdroid.toaster.Toaster;

public class CacheControl extends AppCompatActivity implements MemoryStatusView.ClearStatus {
    MaterialDialog loading;
    MaterialDialog executing;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.memory_total)
    MemoryTotalView memory_total;
    @BindView(R.id.memory_mini)
    MemoryStatusView memory_mini;
    @BindView(R.id.memory_portrait)
    MemoryStatusView memory_portrait;
    @BindView(R.id.memory_thumbs)
    MemoryStatusView memory_thumbs;
    @BindView(R.id.memory_cache)
    MemoryStatusView memory_cache;
    @BindView(R.id.memory_logs)
    MemoryStatusView memory_logs;
    @BindView(R.id.memory_apk)
    MemoryUpdateView memory_apk;
    @BindView(R.id.memory_cache_android)
    MemoryStatusView memory_cache_android;
    @BindView(R.id.memory_clear)
    MemoryClearView memory_clear;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memory_control);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Administrar Cache");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ThemeUtils.Theme theme = ThemeUtils.Theme.create(this);
        setUpTheme(theme);
        memory_total.setTheme(theme);
        memory_mini.setSizeDir(this, Keys.Dirs.CACHE_MINI, true, false, theme);
        memory_portrait.setSizeDir(this, Keys.Dirs.CACHE_PORTADA, true, false, theme);
        memory_thumbs.setSizeDir(this, Keys.Dirs.CACHE_THUMBS, true, false, theme);
        memory_cache.setSizeDir(this, Keys.Dirs.CACHE, false, true, theme);
        memory_logs.setSizeDir(this, Keys.Dirs.LOGS, true, false, theme);
        memory_apk.init(this, theme);
        memory_cache_android.setSizeDir(this, getCacheDir(), true, false, theme);
        memory_clear.setTheme(theme);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Uri uri = getIntent().getData();
        if (uri != null) {
            DoActions(uri.getLastPathSegment());
        }
    }

    private MaterialDialog getExecutingDialog() {
        if (executing == null) {
            executing = new MaterialDialog.Builder(CacheControl.this)
                    .content("Ejecutando...")
                    .progress(true, 0)
                    .cancelable(false)
                    .build();
        }
        return executing;
    }

    private void DoActions(final String instructions) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getExecutingDialog().show();
                    }
                });
                String[] data = instructions.split("-");
                for (String d : data) {
                    File file = new File(Keys.Dirs.CACHE, d + ".txt");
                    if (file.exists()) {
                        file.delete();
                    }
                    if (d.equals("animes")) {
                        CacheManager.invalidateCacheSync(Keys.Dirs.CACHE, false, true);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getExecutingDialog().dismiss();
                    }
                });
                Toaster.toast("Reiniciando app...");
                finish();
                startActivity(new Intent(CacheControl.this, Splash.class));
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    private MaterialDialog getLoading() {
        if (loading == null) {
            loading = new MaterialDialog.Builder(this)
                    .cancelable(false)
                    .content("Trabajando...")
                    .backgroundColor(ThemeUtils.isAmoled(CacheControl.this) ? ColorsRes.Prim(CacheControl.this) : ColorsRes.Blanco(CacheControl.this))
                    .progress(true, 0)
                    .build();
            return loading;
        } else {
            return loading;
        }
    }

    private void setUpTheme(ThemeUtils.Theme theme) {
        toolbar.setBackgroundColor(theme.primary);
        toolbar.getRootView().setBackgroundColor(theme.background);
        toolbar.setTitleTextColor(theme.textColorToolbar);
        ThemeUtils.setNavigationColor(toolbar, theme.toolbarNavigation);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(theme.primaryDark);
            getWindow().setNavigationBarColor(theme.primary);
        }
    }

    @Override
    public void onStartLoading() {
        getLoading().show();
    }

    @Override
    public void onStopLoading() {
        getLoading().dismiss();
    }

    @Override
    public void onRechargeTotal() {
        memory_total.updateSize();
    }
}
