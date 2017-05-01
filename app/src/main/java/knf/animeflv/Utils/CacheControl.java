package knf.animeflv.Utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.R;
import knf.animeflv.Splash;
import xdroid.toaster.Toaster;

public class CacheControl extends AppCompatActivity {
    MaterialDialog loading;
    MaterialDialog executing;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.linearLayout)
    LinearLayout layout;
    @BindView(R.id.lay_apk)
    View apk_view;
    @BindView(R.id.total)
    TextView total;

    @BindView(R.id.switch_mini)
    SwitchCompat switch_mini;
    @BindView(R.id.clear_mini)
    Button clear_mini;

    @BindView(R.id.switch_portada)
    SwitchCompat switch_portada;
    @BindView(R.id.clear_portada)
    Button clear_portada;

    @BindView(R.id.clear_thumb)
    Button clear_thumb;

    @BindView(R.id.clear_logs)
    Button clear_logs;

    @BindView(R.id.clear_anime)
    Button clear_anime;

    @BindView(R.id.desc_apk)
    TextView desc_apk;
    @BindView(R.id.clear_apk)
    Button clear_apk;
    @BindView(R.id.install_apk)
    Button apply_apk;

    @BindView(R.id.clear_android)
    Button clear_android;

    @BindView(R.id.clear_all)
    Button clear_all;

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
        setUpTheme();
        rechargeTotal();
        rechargeMini();
        rechargePortada();
        rechargeThumb();
        rechargeAnime();
        rechargeLogs();
        checkApk();
        rechargeAndroid();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        switch_mini.setChecked(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("save_mini", true));
        switch_portada.setChecked(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("save_portada", true));
        switch_mini.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PreferenceManager.getDefaultSharedPreferences(CacheControl.this).edit().putBoolean("save_mini", b).apply();
            }
        });
        switch_portada.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PreferenceManager.getDefaultSharedPreferences(CacheControl.this).edit().putBoolean("save_portada", b).apply();
            }
        });
        clear_mini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLoading().show();
                CacheManager.invalidateCache(Keys.Dirs.CACHE_MINI, true, false, new CacheManager.OnInvalidateCache() {
                    @Override
                    public void onFinish() {
                        rechargeMini();
                        rechargeTotal();
                        getLoading().dismiss();
                    }
                });
            }
        });
        clear_portada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLoading().show();
                CacheManager.invalidateCache(Keys.Dirs.CACHE_PORTADA, true, false, new CacheManager.OnInvalidateCache() {
                    @Override
                    public void onFinish() {
                        rechargePortada();
                        rechargeTotal();
                        getLoading().dismiss();
                    }
                });
            }
        });
        clear_thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLoading().show();
                CacheManager.invalidateCache(Keys.Dirs.CACHE_THUMBS, true, false, new CacheManager.OnInvalidateCache() {
                    @Override
                    public void onFinish() {
                        rechargeThumb();
                        rechargeTotal();
                        getLoading().dismiss();
                    }
                });
            }
        });
        clear_anime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLoading().show();
                CacheManager.invalidateCache(Keys.Dirs.CACHE, false, true, new CacheManager.OnInvalidateCache() {
                    @Override
                    public void onFinish() {
                        rechargeAnime();
                        rechargeTotal();
                        getLoading().dismiss();
                    }
                });
            }
        });
        clear_logs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLoading().show();
                CacheManager.invalidateCache(Keys.Dirs.LOGS, true, false, new CacheManager.OnInvalidateCache() {
                    @Override
                    public void onFinish() {
                        rechargeLogs();
                        rechargeTotal();
                        getLoading().dismiss();
                    }
                });
            }
        });
        clear_apk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLoading().show();
                if (Keys.Dirs.getUpdateFile(CacheControl.this).delete()) {
                    checkApk();
                    rechargeTotal();
                } else {
                    Toaster.toast("Error al Eliminar!");
                }
                getLoading().dismiss();
            }
        });
        clear_android.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLoading().show();
                CacheManager.invalidateCache(getCacheDir(), true, false, new CacheManager.OnInvalidateCache() {
                    @Override
                    public void onFinish() {
                        rechargeAndroid();
                        rechargeTotal();
                        getLoading().dismiss();
                    }
                });
            }
        });
        clear_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(CacheControl.this)
                        .content("Se eliminaran todas las preferencias del usuario, esta seguro?")
                        .positiveText("SI")
                        .cancelable(false)
                        .backgroundColor(ThemeUtils.isAmoled(CacheControl.this) ? ColorsRes.Prim(CacheControl.this) : ColorsRes.Blanco(CacheControl.this))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @SuppressLint("ApplySharedPref")
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                PreferenceManager.getDefaultSharedPreferences(CacheControl.this).edit().clear().commit();
                                getSharedPreferences("data", 0).edit().clear().commit();
                                finish();
                                startActivity(new Intent(CacheControl.this, Splash.class));
                            }
                        }).show();
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

    private void rechargeMini() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clear_mini.setText("VACIAR (...)");
            }
        });
        CacheManager.asyncGetFormatedFileSize(Keys.Dirs.CACHE_MINI, new CacheManager.OnFinishCount() {
            @Override
            public void counted(final String formated) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clear_mini.setText("VACIAR (" + formated + ")");
                    }
                });
            }
        });
    }

    private void rechargePortada() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clear_portada.setText("VACIAR (...)");
            }
        });
        CacheManager.asyncGetFormatedFileSize(Keys.Dirs.CACHE_PORTADA, new CacheManager.OnFinishCount() {
            @Override
            public void counted(final String formated) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clear_portada.setText("VACIAR (" + formated + ")");
                    }
                });
            }
        });
    }

    private void rechargeThumb() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clear_thumb.setText("VACIAR (...)");
            }
        });
        CacheManager.asyncGetFormatedFileSize(Keys.Dirs.CACHE_THUMBS, new CacheManager.OnFinishCount() {
            @Override
            public void counted(final String formated) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clear_thumb.setText("VACIAR (" + formated + ")");
                    }
                });
            }
        });
    }

    private void rechargeAnime() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clear_anime.setText("VACIAR (...)");
            }
        });
        CacheManager.asyncGetFormatedCacheSize(Keys.Dirs.CACHE, new CacheManager.OnFinishCount() {
            @Override
            public void counted(final String formated) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clear_anime.setText("VACIAR (" + formated + ")");
                    }
                });
            }
        });
    }

    private void rechargeLogs() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clear_logs.setText("VACIAR (...)");
            }
        });
        CacheManager.asyncGetFormatedFileSize(Keys.Dirs.LOGS, new CacheManager.OnFinishCount() {
            @Override
            public void counted(final String formated) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clear_logs.setText("VACIAR (" + formated + ")");
                    }
                });
            }
        });
    }

    private void rechargeAndroid() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clear_android.setText("VACIAR (...)");
            }
        });
        CacheManager.asyncGetFormatedFileSize(getCacheDir(), new CacheManager.OnFinishCount() {
            @Override
            public void counted(final String formated) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clear_android.setText("VACIAR (" + formated + ")");
                    }
                });
            }
        });
    }

    private void rechargeTotal() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                total.setText("Espacio Utilizado: ...");
            }
        });
        CacheManager.asyncGetFormatedCacheSize(this, new CacheManager.OnFinishCount() {
            @Override
            public void counted(final String formated) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        total.setText("Espacio Utilizado: " + formated);
                    }
                });
            }
        });
    }

    private void checkApk() {
        if (Keys.Dirs.getUpdateFile(this).exists()) {
            try {
                PackageInfo extinfo = getPackageManager().getPackageArchiveInfo(Keys.Dirs.getUpdateFile(this).getAbsolutePath(), 0);
                PackageInfo intinfo = getPackageManager().getPackageArchiveInfo(getPackageName(), 0);
                int extCode = extinfo.versionCode;
                int intCode = intinfo.versionCode;
                desc_apk.setText(desc_apk.getText().toString().replace("%intver%", String.valueOf(intCode)).replace("%extver%", String.valueOf(extCode)));
                if (intCode > extCode) {
                    apply_apk.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                desc_apk.setText("Apk descargado en la actualización.");
                apply_apk.setVisibility(View.GONE);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CacheManager.asyncGetFormatedFileSize(Keys.Dirs.getUpdateFile(CacheControl.this), new CacheManager.OnFinishCount() {
                        @Override
                        public void counted(final String formated) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    clear_apk.setText("Borrar (" + formated + ")");
                                }
                            });
                        }
                    });
                }
            });
        } else {
            apk_view.setVisibility(View.GONE);
        }
    }

    private boolean isXLargeScreen() {
        return (getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private void setUpTheme() {
        if (!isXLargeScreen()) { //Portrait
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        if (ThemeUtils.isAmoled(this)) {
            layout.getRootView().setBackgroundColor(ColorsRes.Negro(this));
            changeTextsColor(layout, ColorsRes.SecondaryTextDark(this));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(getResources().getColor(R.color.negro));
                getWindow().setNavigationBarColor(getResources().getColor(R.color.negro));
            }
        } else {
            layout.getRootView().setBackgroundColor(ColorsRes.Blanco(this));
            changeTextsColor(layout, ColorsRes.SecondaryTextLight(this));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(getResources().getColor(R.color.dark));
                getWindow().setNavigationBarColor(getResources().getColor(R.color.prim));
            }
        }
    }

    private void changeTextsColor(LinearLayout linear, @ColorInt int color) {
        int accent = ThemeUtils.getAcentColor(this);
        int count = linear.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = linear.getChildAt(i);
            if (view instanceof SwitchCompat) {
                ((SwitchCompat) view).setTextColor(color);
            } else if (view instanceof Button) {
                ((Button) view).setTextColor(accent);
            } else if (view instanceof TextView) {
                ((TextView) view).setTextColor(color);
            } else if (view instanceof LinearLayout) {
                changeTextsColor((LinearLayout) view, color);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
