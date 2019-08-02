package knf.animeflv.FavSync;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.FavSyncro;
import knf.animeflv.R;
import knf.animeflv.Utils.ThemeUtils;

/**
 * Created by Jordy on 19/07/2017.
 */

public class SyncActivity extends AppCompatActivity {
    public static final int LOCAL = 0;
    public static final int CLOUD = 1;

    private static final String CONF_LOCAL = "Conservar la lista local? (La lista de DropBox se perdera)";
    private static final String CONF_CLOUD = "Conservar la lista de DropBox? (La lista Local se perdera)";

    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    SmartTabLayout tabLayout;
    @BindView(R.id.pager)
    ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setResult(Activity.RESULT_CANCELED);
        setContentView(R.layout.layout_fav_sync);
        ButterKnife.bind(this);
        ThemeUtils.Theme theme = ThemeUtils.Theme.create(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(theme.primaryDark);
            getWindow().setNavigationBarColor(theme.primary);
        }
        Drawable drawable = getResources().getDrawable(R.drawable.clear);
        drawable.setColorFilter(theme.toolbarNavigation, PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(drawable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setTitle("Sincronizar Favoritos");
        toolbar.setBackgroundColor(theme.primary);
        toolbar.setTitleTextColor(theme.textColorToolbar);
        tabLayout.setBackgroundColor(theme.primary);
        tabLayout.setSelectedIndicatorColors(theme.indicatorColor);
        tabLayout.setDefaultTabTextColor(theme.textColorToolbar);
        toolbar.getRootView().setBackgroundColor(theme.background);
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                        .add("LOCAL", ListFragment.class, ListFragment.get(LOCAL))
                        .add("DROPBOX", ListFragment.class, ListFragment.get(CLOUD))
                        .create());
        viewPager.setAdapter(adapter);
        tabLayout.setViewPager(viewPager);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(SyncActivity.this)
                        .content(viewPager.getCurrentItem() == 0 ? CONF_LOCAL : CONF_CLOUD)
                        .positiveText("confirmar")
                        .negativeText("cancelar")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                setResult(Activity.RESULT_OK);
                                if (viewPager.getCurrentItem() == 0) {
                                    FavSyncro.updateFavs(SyncActivity.this);
                                    finish();
                                } else {
                                    final MaterialDialog dialog = new MaterialDialog.Builder(SyncActivity.this)
                                            .content("Sincronizando favoritos...")
                                            .progress(true, 0)
                                            .cancelable(false)
                                            .build();
                                    dialog.show();
                                    FavSyncro.updateLocal(SyncActivity.this, new FavSyncro.UpdateCallback() {
                                        @Override
                                        public void onUpdate() {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    dialog.dismiss();
                                                    finish();
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        }).build().show();
            }
        });
    }
}
