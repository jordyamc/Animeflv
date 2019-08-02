package knf.animeflv.AutoEmision;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.LoginActivity.DropboxManager;
import knf.animeflv.R;
import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.NetworkUtils;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.TrackingHelper;
import knf.animeflv.info.InfoFragments;
import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 09/01/2017.
 */

public class AutoEmisionActivity extends AppCompatActivity implements AutoEmisionFragment.EmisionRemoveListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.smartTab)
    SmartTabLayout smartTabLayout;
    @BindView(R.id.vp_emision)
    ViewPager viewPager;

    private boolean reset = false;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(InfoFragments.ACTION_EDITED)) {
                Log.e("Broadcast", "Reload activity");
                reset = true;
            }
        }
    };
    private JSONObject listJson;

    private int count = 0;
    private TextView count_text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_emision);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Siguiendo");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ThemeUtils.Theme theme = ThemeUtils.Theme.create(this);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.getRootView().setBackgroundColor(theme.background);
        toolbar.setBackgroundColor(theme.primary);
        toolbar.setTitleTextColor(theme.textColorToolbar);
        ThemeUtils.setNavigationColor(toolbar, theme.toolbarNavigation);
        smartTabLayout.setBackgroundColor(theme.primary);
        smartTabLayout.setSelectedIndicatorColors(theme.indicatorColor);
        smartTabLayout.setDefaultTabTextColor(theme.textColorToolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(theme.primaryDark);
            getWindow().setNavigationBarColor(theme.primary);
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(InfoFragments.ACTION_EDITED);
        registerReceiver(receiver, filter);
        asyncStart();
    }

    private void asyncStart() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                count = 0;
                AutoEmisionListHolder.reloadEpisodes(AutoEmisionActivity.this);
                runOnUiThread(() -> {
                    FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                            getSupportFragmentManager(), FragmentPagerItems.with(AutoEmisionActivity.this)
                            .add("LUNES", AutoEmisionFragment.class, getDayBundle(1))
                            .add("MARTES", AutoEmisionFragment.class, getDayBundle(2))
                            .add("MIÉRCOLES", AutoEmisionFragment.class, getDayBundle(3))
                            .add("JUEVES", AutoEmisionFragment.class, getDayBundle(4))
                            .add("VIERNES", AutoEmisionFragment.class, getDayBundle(5))
                            .add("SÁBADO", AutoEmisionFragment.class, getDayBundle(6))
                            .add("DOMINGO", AutoEmisionFragment.class, getDayBundle(7))
                            .create());
                    viewPager.invalidate();
                    viewPager.setOffscreenPageLimit(7);
                    viewPager.setAdapter(adapter);
                    smartTabLayout.setViewPager(viewPager);
                    viewPager.setCurrentItem(Math.abs(getActualDayCode() - 1), true);
                    setFragmentsListener(adapter);
                    supportInvalidateOptionsMenu();
                });
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    private void setFragmentsListener(FragmentPagerItemAdapter adapter) {
        try {
            for (int i = 0; i < 8; i++) {
                ((AutoEmisionFragment) adapter.getPage(i)).setListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bundle getDayBundle(int day) {
        if (listJson == null) {
            Log.e("AutoEmision", "Get New Json");
            listJson = AutoEmisionHelper.getJson(this);
        }
        Bundle bundle = new Bundle();
        bundle.putInt("day", day);
        JSONArray array = AutoEmisionHelper.getDayJson(listJson, day);
        count = count + array.length();
        bundle.putString("array", array.toString());
        return bundle;
    }

    private int getActualDayCode() {
        switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            case Calendar.TUESDAY:
                return 2;
            case Calendar.WEDNESDAY:
                return 3;
            case Calendar.THURSDAY:
                return 4;
            case Calendar.FRIDAY:
                return 5;
            case Calendar.SATURDAY:
                return 6;
            case Calendar.SUNDAY:
                return 7;
            default:
                return 1;
        }
    }

    @Override
    public void onEmisionRemove(int removeCount) {
        count -= removeCount;
        if (count_text != null)
            runOnUiThread(() -> count_text.setOnClickListener(getCountListener()));
    }

    @UiThread
    private View.OnClickListener getCountListener() {
        count_text.setText(String.valueOf(count));
        return view -> Toaster.toast("Actualmente sigues " + count + " " + (count == 1 ? "anime" : "animes") + " de la temporada");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (DropboxManager.islogedIn() && NetworkUtils.isNetworkAvailable()) {
            getMenuInflater().inflate(R.menu.menu_emision, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_emision_count, menu);
        }
        MenuItem c = menu.findItem(R.id.count);
        count_text = (c.getActionView().findViewById(R.id.count_text));
        count_text.setOnClickListener(getCountListener());
        int color = ThemeUtils.Theme.get(this, ThemeUtils.Theme.KEY_TOOLBAR_NAVIGATION);
        count_text.setTextColor(color);
        ThemeUtils.setMenuColor(menu, color);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (DropboxManager.islogedIn()) {
            switch (item.getItemId()) {
                case R.id.sync:
                    PopupMenu popupMenu = new PopupMenu(new ContextThemeWrapper(this, R.style.PopupMenu), findViewById(item.getItemId()));
                    popupMenu.inflate(R.menu.menu_emision_sync);
                    popupMenu.setOnMenuItemClickListener(menuItem -> {
                        if (DropboxManager.islogedIn()) {
                            switch (menuItem.getItemId()) {
                                case R.id.download:
                                    final MaterialDialog dialog = getLoadingDialog(0);
                                    dialog.show();
                                    DropboxManager.downloadEmision(AutoEmisionActivity.this, (result, success) -> {
                                        dialog.dismiss();
                                        if (success) {
                                            AutoEmisionHelper.updateSavedList(AutoEmisionActivity.this, result);
                                            Toaster.toast("Descarga exitosa");
                                            invalidateCurrentList();
                                        } else {
                                            Toaster.toast("Error al descargar");
                                        }
                                    });
                                    break;
                                case R.id.upload:
                                    final MaterialDialog dialog_up = getLoadingDialog(1);
                                    dialog_up.show();
                                    DropboxManager.updateEmision(AutoEmisionActivity.this, success -> {
                                        dialog_up.dismiss();
                                        if (success) {
                                            Toaster.toast("Subida exitosamente");
                                        } else {
                                            Toaster.toast("Error al subir lista");
                                        }
                                    });
                                    break;
                            }
                        }
                        return true;
                    });
                    popupMenu.show();
                    break;
            }
        } else {
            Toaster.toast("Se necesita tener vinculada una cuenta de Dropbox");
        }
        return true;
    }

    private MaterialDialog getLoadingDialog(int type) {
        return new MaterialDialog.Builder(this)
                .content((type == 0 ? "Descargando " : "Subiendo ") + "lista...")
                .progress(true, 0)
                .build();
    }

    private void invalidateCurrentList() {
        listJson = null;
        AutoEmisionListHolder.invalidateLists();
        finish();
        startActivity(new Intent(this, AutoEmisionActivity.class));
    }

    @Override
    protected void onResume() {
        TrackingHelper.track(this, TrackingHelper.EMISION);
        if (reset) {
            invalidateCurrentList();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        AutoEmisionHelper.asyncSaveAllDays(getApplicationContext());
        super.onDestroy();
    }
}
