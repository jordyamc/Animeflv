package knf.animeflv.AutoEmision;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.json.JSONObject;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
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

public class AutoEmisionActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_emision);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Emision");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (ThemeUtils.isAmoled(this)) {
            toolbar.getRootView().setBackgroundColor(ColorsRes.Negro(this));
            toolbar.setBackgroundColor(ColorsRes.Negro(this));
            smartTabLayout.setBackgroundColor(ColorsRes.Negro(this));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ColorsRes.Negro(this));
                getWindow().setNavigationBarColor(ColorsRes.Negro(this));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ColorsRes.Dark(this));
                getWindow().setNavigationBarColor(ColorsRes.Prim(this));
            }
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
                AutoEmisionListHolder.reloadEpisodes();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                                getSupportFragmentManager(), FragmentPagerItems.with(AutoEmisionActivity.this)
                                .add("LUNES", AutoEmisionFragment.class, getDayBundle(1))
                                .add("MARTES", AutoEmisionFragment.class, getDayBundle(2))
                                .add("MIERCOLES", AutoEmisionFragment.class, getDayBundle(3))
                                .add("JUEVES", AutoEmisionFragment.class, getDayBundle(4))
                                .add("VIERNES", AutoEmisionFragment.class, getDayBundle(5))
                                .add("SABADO", AutoEmisionFragment.class, getDayBundle(6))
                                .add("DOMINGO", AutoEmisionFragment.class, getDayBundle(7))
                                .create());
                        viewPager.invalidate();
                        viewPager.setOffscreenPageLimit(7);
                        viewPager.setAdapter(adapter);
                        smartTabLayout.setViewPager(viewPager);
                        viewPager.setCurrentItem(Math.abs(getActualDayCode() - 1), true);
                    }
                });
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    private Bundle getDayBundle(int day) {
        if (listJson == null) {
            Log.e("AutoEmision", "Get New Json");
            listJson = AutoEmisionHelper.getJson(this);
        }
        Bundle bundle = new Bundle();
        bundle.putInt("day", day);
        bundle.putString("array", AutoEmisionHelper.getDayJson(listJson, day).toString());
        return bundle;
    }

    private int getActualDayCode() {
        switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                return 1;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        if (DropboxManager.islogedIn() && NetworkUtils.isNetworkAvailable()) {
            getMenuInflater().inflate(R.menu.menu_emision, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (DropboxManager.islogedIn()) {
            switch (item.getItemId()) {
                case R.id.download:
                    final MaterialDialog dialog = getLoadingDialog(0);
                    dialog.show();
                    DropboxManager.downloadEmision(this, new DropboxManager.DownloadCallback() {
                        @Override
                        public void onDownload(JSONObject result, boolean success) {
                            dialog.dismiss();
                            if (success) {
                                AutoEmisionHelper.updateSavedList(AutoEmisionActivity.this, result);
                                Toaster.toast("Descarga exitosa");
                                invalidateCurrentList();
                            } else {
                                Toaster.toast("Error al descargar");
                            }
                        }
                    });
                    break;
                case R.id.upload:
                    final MaterialDialog dialog_up = getLoadingDialog(1);
                    dialog_up.show();
                    DropboxManager.updateEmision(this, new DropboxManager.UploadCallback() {
                        @Override
                        public void onUpload(boolean success) {
                            dialog_up.dismiss();
                            if (success) {
                                Toaster.toast("Subida exitosamente");
                            } else {
                                Toaster.toast("Error al subir lista");
                            }
                        }
                    });
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
