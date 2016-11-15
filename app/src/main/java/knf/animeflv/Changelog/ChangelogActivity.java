package knf.animeflv.Changelog;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import knf.animeflv.Changelog.Adapters.VersionAdapter;
import knf.animeflv.ColorsRes;
import knf.animeflv.R;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

public class ChangelogActivity extends AppCompatActivity {
    @Bind(R.id.recycler)
    RecyclerView recyclerView;
    @Bind(R.id.progress)
    ProgressBar progressBar;

    private static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_changelog);
        ButterKnife.bind(this);
        setUpColors();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ChangeLogOrganizer.organize(this, new ChangeLogOrganizer.ChangelogListListener() {
            @Override
            public void onListCreated(final List<ChangeLogObjects.Version> versions) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(new VersionAdapter(ChangelogActivity.this, versions));
                        progressBar.setVisibility(View.GONE);
                        ThemeUtils.setStatusBarPadding(ChangelogActivity.this, recyclerView);
                    }
                });
            }

            @Override
            public void onListFailed() {
                Toaster.toast("List Failed");
            }
        });
    }

    private void setUpColors() {
        if (!isXLargeScreen(getApplicationContext())) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ThemeUtils.setStatusBarPadding(this, recyclerView);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (ThemeUtils.isAmoled(this)) {
            if (!isXLargeScreen(this)) {
                recyclerView.getRootView().setBackgroundColor(ColorsRes.Negro(this));
            } else {
                recyclerView.getRootView().setBackgroundColor(ColorsRes.Prim(this));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (!isXLargeScreen(this)) {
                    getWindow().setStatusBarColor(ColorsRes.Negro(this));
                    getWindow().setNavigationBarColor(ColorsRes.Transparent(this));
                } else {
                    getWindow().setStatusBarColor(ColorsRes.Prim(this));
                    getWindow().setNavigationBarColor(ColorsRes.Transparent(this));
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (!isXLargeScreen(this)) {
                    getWindow().setStatusBarColor(ColorsRes.Dark(this));
                    getWindow().setNavigationBarColor(ColorsRes.Transparent(this));
                } else {
                    getWindow().setStatusBarColor(ColorsRes.Dark(this));
                    getWindow().setNavigationBarColor(ColorsRes.Transparent(this));
                }
            }
        }
    }
}
