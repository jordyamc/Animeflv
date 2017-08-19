package knf.animeflv.Explorer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.munix.multidisplaycast.CastControlsActivity;
import knf.animeflv.ColorsRes;
import knf.animeflv.Configuracion;
import knf.animeflv.Directorio.DB.DirectoryHelper;
import knf.animeflv.DownloadService.WebServer;
import knf.animeflv.Explorer.Fragments.DirectoryFragment;
import knf.animeflv.Explorer.Fragments.VideoFilesFragment;
import knf.animeflv.Explorer.Models.ModelFactory;
import knf.animeflv.FavSyncro;
import knf.animeflv.FileMover;
import knf.animeflv.Parser;
import knf.animeflv.PlayBack.CastPlayBackManager;
import knf.animeflv.R;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.FragmentExtras;
import knf.animeflv.Utils.Keys;
import knf.animeflv.Utils.NetworkUtils;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.TrackingHelper;
import xdroid.toaster.Toaster;


public class ExplorerRoot extends AppCompatActivity implements ExplorerInterfaces {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fileDir)
    TextView textView;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    DirectoryFragment directoryFragment;
    VideoFilesFragment videoFilesFragment;
    boolean waitForResult = false;
    private boolean isDirectory = true;
    private String TAG_DIRECTORY = "directorio";
    private String TAG_ANIME = "anime";

    private WebServer server;

    public static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explorer_root);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ThemeUtils.Theme theme = ThemeUtils.Theme.create(this);
        textView.getRootView().setBackgroundColor(theme.background);
        textView.setTextColor(theme.textColor);
        toolbar.setBackgroundColor(theme.primary);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitleTextColor(theme.textColorToolbar);
        floatingActionButton.hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(theme.primaryDark);
            getWindow().setNavigationBarColor(theme.primary);
        }
        getSupportActionBar().setTitle("Explorador");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ThemeUtils.setNavigationColor(toolbar, theme.toolbarNavigation);
        if (downloadInSD()) {
            if (!FileUtil.init(this).searchforSD().existSD()) {
                Toaster.toast("No se encontro la memoria SD");
                commitDownloadInSD(false);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (!FileUtil.init(this).RootFileHaveAccess()) {
                        Toaster.toast("Se necesitan permisos de escritura!!");
                        waitForResult = true;
                        FragmentExtras.KEY = Configuracion.GET_WRITE_PERMISSIONS;
                        Intent intent = new Intent(this, Configuracion.class);
                        intent.putExtra("return", 12877);
                        startActivityForResult(intent, 1547);
                    } else if (!FileUtil.init(this).RootFileHaveAccess(null)) {
                        Toaster.toast("El permiso de escritura no concuerda con la SD seleccionada!!");
                        waitForResult = true;
                        FragmentExtras.KEY = Configuracion.GET_WRITE_PERMISSIONS;
                        Intent intent = new Intent(this, Configuracion.class);
                        intent.putExtra("return", 12877);
                        startActivityForResult(intent, 1547);
                    }
                }
            }
        }
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExplorerRoot.this, CastControlsActivity.class));
            }
        });
        if (!waitForResult) {
            textView.setText(ModelFactory.getDirectoryFile(this).getAbsolutePath());
            directoryFragment = new DirectoryFragment();
            videoFilesFragment = new VideoFilesFragment();
            String aid_extra = getIntent().getStringExtra("aid");
            if (aid_extra == null) {
                showDirectory();
            } else {
                File file = new File(ModelFactory.getDirectoryFile(this), aid_extra);
                isDirectory = false;
                textView.setText(file.getAbsolutePath());
                getSupportActionBar().setTitle(DirectoryHelper.get(this).getTitle(aid_extra));
                showVideoFile(file);
                supportInvalidateOptionsMenu();
            }
        }
    }

    private void showDirectory() {
        try {
            isDirectory = true;
            textView.setText(ModelFactory.getDirectoryFile(this).getAbsolutePath());
            directoryFragment.recharge(this);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            if (directoryFragment.isAdded()) {
                transaction.show(directoryFragment);
            } else {
                transaction.add(R.id.root, directoryFragment, TAG_DIRECTORY);
            }
            if (videoFilesFragment.isAdded()) transaction.remove(videoFilesFragment);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showVideoFile(File file) {
        isDirectory = false;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (videoFilesFragment.isAdded()) transaction.remove(videoFilesFragment);
        if (directoryFragment.isAdded()) transaction.hide(directoryFragment);
        transaction.commit();
        videoFilesFragment = new VideoFilesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("path", file.getAbsolutePath());
        videoFilesFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.root, videoFilesFragment, TAG_ANIME)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (!isDirectory) {
            getSupportActionBar().setTitle("Explorador");
            textView.setText(ModelFactory.getDirectoryFile(this).getAbsolutePath());
            showDirectory();
            isDirectory = true;
            supportInvalidateOptionsMenu();
        } else {
            finish();
        }
    }

    @Override
    public void OnDirectoryClicked(File file, String name) {
        isDirectory = false;
        textView.setText(file.getAbsolutePath());
        getSupportActionBar().setTitle(name);
        showVideoFile(file);
        supportInvalidateOptionsMenu();
    }

    @Override
    public void OnFileClicked(File file) {

    }

    private String getIpWport(int port, boolean formatted) {
        return (formatted ? "http://" : "") + NetworkUtils.getIPAddress(this) + ":" + port;
    }

    @Override
    public void OnCastFile(File file, String eid) {
        try {
            if (server != null && server.isAlive())
                server.stop();
            server = new WebServer(this, file);
            server.start();
            CastPlayBackManager.get(this).play(getIpWport(8880, true), eid);
            if (CastPlayBackManager.get(this).isDeviceConnected())
                floatingActionButton.show();
        } catch (Exception e) {
            e.printStackTrace();
            Toaster.toast("Error al activar webserver");
        }
    }

    @Override
    public void OnDirectoryFileChange() {
        directoryFragment.recharge(this);
    }

    @Override
    public void OnDirectoryEmpty(String aid) {
        directoryFragment.deleteDirectory(this, aid);
        showDirectory();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!isXLargeScreen(getApplicationContext())) {
            return;
        }
    }

    private void rechargePath() {
        supportInvalidateOptionsMenu();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(ModelFactory.getDirectoryFile(ExplorerRoot.this).getAbsolutePath());
            }
        });
    }

    private boolean downloadInSD() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("sd_down", false);
    }

    @SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
    private void commitDownloadInSD(boolean inSD) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("sd_down", inSD).commit();
    }

    @SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!downloadInSD()) {
                if (FileUtil.init(this).searchforSD().existSD()) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("sd_down", !downloadInSD()).commit();
                        getSupportFragmentManager().beginTransaction().remove(directoryFragment).commit();
                        directoryFragment = new DirectoryFragment();
                        showDirectory();
                        rechargePath();
                    } else {
                        if (PreferenceManager.getDefaultSharedPreferences(this).getString(Keys.Extra.EXTERNAL_SD_ACCESS_URI, null) != null) {
                            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("sd_down", !downloadInSD()).commit();
                            getSupportFragmentManager().beginTransaction().remove(directoryFragment).commit();
                            directoryFragment = new DirectoryFragment();
                            showDirectory();
                            rechargePath();
                        }
                    }
                }
            } else {
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("sd_down", !downloadInSD()).commit();
                getSupportFragmentManager().beginTransaction().remove(directoryFragment).commit();
                directoryFragment = new DirectoryFragment();
                showDirectory();
                rechargePath();
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        boolean isInSD = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("sd_down", false);
        if (!isInSD && isDirectory && count() > 0) {
            if (FileUtil.init(this).searchforSD().existSD()) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    getMenuInflater().inflate(R.menu.menu_move, menu);
                } else {
                    if (PreferenceManager.getDefaultSharedPreferences(this).getString(Keys.Extra.EXTERNAL_SD_ACCESS_URI, null) != null) {
                        getMenuInflater().inflate(R.menu.menu_move, menu);
                    }
                }
            }
        }
        ThemeUtils.setMenuColor(menu, ThemeUtils.Theme.get(this, ThemeUtils.Theme.KEY_TOOLBAR_NAVIGATION));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.move:
                new MaterialDialog.Builder(ExplorerRoot.this)
                        .content("Los animes en la memoria interna se moveran a la SD, esta seguro?")
                        .positiveText("Continuar")
                        .negativeText("Cancelar")
                        .backgroundColor(ThemeUtils.isAmoled(ExplorerRoot.this) ? ColorsRes.Prim(ExplorerRoot.this) : ColorsRes.Blanco(ExplorerRoot.this))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                startMovingAnime();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        }).build().show();
                break;
        }
        return true;
    }

    private void startMovingAnime() {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Moviendo Animes...")
                .titleGravity(GravityEnum.CENTER)
                .backgroundColor(ThemeUtils.isAmoled(this) ? ColorsRes.Prim(this) : ColorsRes.Blanco(this))
                .customView(R.layout.move_lay, false)
                .cancelable(false)
                .build();
        dialog.show();
        final ProgressBar progressTotal = (ProgressBar) dialog.getCustomView().findViewById(R.id.progressTotal);
        final ProgressBar progressSemi = (ProgressBar) dialog.getCustomView().findViewById(R.id.progressSemi);
        final TextView progress_total = (TextView) dialog.getCustomView().findViewById(R.id.progress_total);
        final TextView progress_file_number = (TextView) dialog.getCustomView().findViewById(R.id.total_files);
        final TextView progress_semi = (TextView) dialog.getCustomView().findViewById(R.id.progress_semi);
        final TextView file_name = (TextView) dialog.getCustomView().findViewById(R.id.tit_semi);
        if (ThemeUtils.isAmoled(this)) {
            progress_file_number.setTextColor(ColorsRes.SecondaryTextDark(this));
            progress_semi.setTextColor(ColorsRes.SecondaryTextDark(this));
            progress_total.setTextColor(ColorsRes.SecondaryTextDark(this));
            file_name.setTextColor(ColorsRes.SecondaryTextDark(this));
        } else {
            progress_file_number.setTextColor(ColorsRes.SecondaryTextLight(this));
            progress_semi.setTextColor(ColorsRes.SecondaryTextLight(this));
            progress_total.setTextColor(ColorsRes.SecondaryTextLight(this));
            file_name.setTextColor(ColorsRes.SecondaryTextLight(this));
        }
        FileMover.PrepareMove(this, new FileMover.OnProgressListener() {
            @Override
            public void onStep(final int progress, final int total) {
                if (progress < total) {
                    final String totalprog = ((progress * 100) / total) + "%";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressTotal.setIndeterminate(false);
                            progressTotal.setMax(total);
                            progressTotal.setProgress(progress);
                            progress_total.setText(totalprog);
                            progress_file_number.setText(progress + "/" + total);
                        }
                    });
                    directoryFragment.recharge(ExplorerRoot.this);
                } else {
                    dialog.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recreate();
                        }
                    });
                }
            }

            @Override
            public void onSemiStep(final String name, final int progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progress != 100) {
                            progressSemi.setIndeterminate(false);
                            file_name.setText(name);
                            progressSemi.setMax(100);
                            progressSemi.setProgress(progress);
                            progress_semi.setText(progress + "%");
                        } else {
                            progressSemi.setIndeterminate(true);
                        }
                    }
                });
            }

            @Override
            public void onError(String name) {
                Toaster.toast("Error al Mover " + name);
            }
        });
    }

    private int count() {
        int count = 0;
        File f = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download");
        File[] files = f.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    if (file.list() != null) {
                        count += file.list().length;
                    }
                }
            }
        }
        return count;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1547 && resultCode == 12877) {
            textView.setText(ModelFactory.getDirectoryFile(this).getAbsolutePath());
            directoryFragment = new DirectoryFragment();
            videoFilesFragment = new VideoFilesFragment();
            showDirectory();
        }
        if (requestCode == 1547 && resultCode == -1) {
            Toaster.toast("Error al obtener permisos");
            commitDownloadInSD(false);
            textView.setText(ModelFactory.getDirectoryFile(this).getAbsolutePath());
            directoryFragment = new DirectoryFragment();
            videoFilesFragment = new VideoFilesFragment();
            showDirectory();
        }
    }

    @Override
    protected void onResume() {
        TrackingHelper.track(this, TrackingHelper.EXPLORADOR);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        new Parser().saveBackup(this);
        FavSyncro.updateServer(this);
        if (server != null)
            server.stop();
        CastPlayBackManager.get(this).stop();
        super.onDestroy();
    }
}
