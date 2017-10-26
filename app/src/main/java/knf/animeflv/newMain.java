package knf.animeflv;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dropbox.core.android.Auth;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.melnykov.fab.FloatingActionButton;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import knf.animeflv.About.AboutActivity;
import knf.animeflv.AdminControl.PushManager;
import knf.animeflv.AutoEmision.AutoEmisionActivity;
import knf.animeflv.Changelog.ChangelogActivity;
import knf.animeflv.Cloudflare.Bypass;
import knf.animeflv.Directorio.AnimeClass;
import knf.animeflv.Directorio.DB.DirectoryHelper;
import knf.animeflv.Directorio.Directorio;
import knf.animeflv.DownloadService.DownloaderService;
import knf.animeflv.DownloadService.ServerHolder;
import knf.animeflv.Explorer.ExplorerRoot;
import knf.animeflv.Favorites.FavoriteMain;
import knf.animeflv.Favorites.FavotiteDB;
import knf.animeflv.HallFame.HallActivity;
import knf.animeflv.Interfaces.MainRecyclerCallbacks;
import knf.animeflv.JsonFactory.BaseGetter;
import knf.animeflv.JsonFactory.JsonTypes.INICIO;
import knf.animeflv.JsonFactory.SelfGetter;
import knf.animeflv.LoginActivity.DropboxManager;
import knf.animeflv.LoginActivity.LoginActivity;
import knf.animeflv.LoginActivity.LoginServer;
import knf.animeflv.PlayBack.CastPlayBackManager;
import knf.animeflv.PlayBack.PlayBackManager;
import knf.animeflv.Random.RandomActivity;
import knf.animeflv.Recientes.MainOrganizer;
import knf.animeflv.Recientes.Status;
import knf.animeflv.Recyclers.AdapterMain;
import knf.animeflv.Recyclers.AdapterMainNoGIF;
import knf.animeflv.Seen.SeenManager;
import knf.animeflv.State.StateActivity;
import knf.animeflv.Style.ThemeFragmentAdvanced;
import knf.animeflv.Suggestions.SuggestionsActivity;
import knf.animeflv.Tutorial.TutorialActivity;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.Keys;
import knf.animeflv.Utils.Logger;
import knf.animeflv.Utils.MainStates;
import knf.animeflv.Utils.NetworkUtils;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.TrackingHelper;
import knf.animeflv.Utils.UpdateUtil;
import knf.animeflv.Utils.UtilDialogPref;
import knf.animeflv.Utils.UtilNotBlocker;
import knf.animeflv.Utils.admin.adminListeners;
import knf.animeflv.Utils.objects.User;
import knf.animeflv.WaitList.WaitList;
import knf.animeflv.history.HistoryActivity;
import xdroid.toaster.Toaster;

import static knf.animeflv.Utils.Keys.Url.ADMINS;

public class newMain extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        MainRecyclerCallbacks {
    public Drawer result;
    private boolean isAmoled;
    private boolean isSpaced = false;
    private boolean doubleBackToExitPressedOnce = false;
    private String ext_storage_state = Environment.getExternalStorageState();
    private File mediaStorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache");
    private RecyclerView recyclerView;
    private LinearLayout root;
    private Toolbar toolbar;
    private Toolbar menu_toolbar;
    private SwipeRefreshLayout mswipe;
    private int versionCode;
    private String versionName;
    private String androidID;
    private AccountHeader headerResult;
    private Context context;
    private FloatingActionButton actionButton;
    private Parser parser = new Parser();
    private boolean shouldExecuteOnResume;
    private TaskType normal = TaskType.NORMAL;
    private boolean frun = true;
    private boolean favs_data_ok = false;
    private boolean dropboxloging = false;
    private AdapterMain main;
    private AdapterMainNoGIF mainNo;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private String currUser = "null";

    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("force_phone", false) ? R.layout.anime_main_force : R.layout.anime_main);
        TrackingHelper.track(this, TrackingHelper.MAIN);
        startUp();
        setUpMain();
    }

    private void startUp() {
        FileMover.cleantmp();
        if (!getSharedPreferences("data", MODE_PRIVATE).getBoolean("seenUpdated", false)) {
            getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("seenUpdated", true).apply();
            SeenManager.get(this).updateSeen(getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", ""), new SeenManager.SeenCallback() {
                @Override
                public void onSeenUpdated() {
                    Toaster.toast("Lista de capitulos vistos creada");
                }
            });
        }
        String date = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime());
        if (!PreferenceManager.getDefaultSharedPreferences(this).getString("curr_date", "").equals(date)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            preferences.edit().putString("curr_date", date).apply();
            preferences.edit().putInt("add_count", 0).apply();
        }
    }

    private void setUpMain() {
        MainRegistrer.init();
        setUpVersion();
        try {
            setUpViews();
        } catch (Exception e) {
            e.printStackTrace();
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("force_phone", false))
                Toaster.toast("Error al cargar pantalla, desactiva la opcion de forzar vista de telefono!!!");
        }
        setUpDrawer();
        try {
            setUpAmoled();
        } catch (Exception e) {
            e.printStackTrace();
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("force_phone", false))
                Toaster.toast("Error al cargar pantalla, desactiva la opcion de forzar vista de telefono!!!");
        }
        getJson();
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("update_check_start", true))
            NetworkUtils.checkVersion(this);
        ActualizarFavoritos();
        SharedPreferences prefs = this.getSharedPreferences("data", MODE_PRIVATE);
        frun = true;
        isSpaced = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("use_space", false);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals("reload") && !UtilNotBlocker.isPaused()) {
                    mswipe.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!mswipe.isRefreshing()) {
                                mswipe.setRefreshing(true);
                            }
                        }
                    });
                    if (NetworkUtils.isNetworkAvailable()) {
                        Log.d("NewMain", "Block Nots");
                        UtilNotBlocker.setBlocked(true);
                        loadMainJson();
                    } else {
                        if (mswipe.isRefreshing()) {
                            mswipe.setRefreshing(false);
                        }
                    }
                    getSharedPreferences("data", MODE_PRIVATE).edit().putInt("nCaps", 0).apply();
                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putStringSet("eidsNot", new HashSet<String>()).apply();
                }
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(listener);
        if (!DirectoryHelper.get(this).isDirectoryValid()) {
            blockToUpdateDB();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloaderService.RECEIVER_ACTION_ERROR);
        filter.addAction(ThemeFragmentAdvanced.ACTION_THEME_CHANGE);
        registerReceiver(getReceiver(), filter);
    }

    private void blockToUpdateDB() {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .progress(true, 0)
                .content("Creando directorio...\n\nAgregados: 0")
                .cancelable(false)
                .build();
        dialog.show();
        SelfGetter.getDirDB(this, new BaseGetter.AsyncProgressDBInterface() {
            @Override
            public void onFinish(List<AnimeClass> list) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onProgress(final int progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dialog.setContent("Creando directorio...\n\nAgregados: " + progress);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dialog.dismiss();
                            Toaster.toast("Error al crear directorio!!!");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private BroadcastReceiver getReceiver() {
        if (receiver == null)
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(DownloaderService.RECEIVER_ACTION_ERROR)) {
                        loadMainJson();
                    } else if (intent.getAction().equals(ThemeFragmentAdvanced.ACTION_THEME_CHANGE)) {
                        try {
                            setUpAmoled();
                            loadMainJson();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
        return receiver;
    }

    private void setUpDrawer() {
        Drawable ic_main;
        if (ThemeUtils.isAmoled(this)) {
            ic_main = ContextCompat.getDrawable(this, R.mipmap.ic_launcher_dark);
        } else {
            ic_main = ContextCompat.getDrawable(this, R.mipmap.ic_launcher);
        }
        AccountHeaderBuilder builder = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(getHDraw(false))
                .withCompactStyle(true)
                .withDividerBelowHeader(false)
                .withTranslucentStatusBar(true)
                .withCloseDrawerOnProfileListClick(false)
                .withSelectionListEnabled(true)
                .withProfileImagesClickable(true);
        builder.withOnAccountHeaderProfileImageListener(new AccountHeader.OnAccountHeaderProfileImageListener() {
            @Override
            public boolean onProfileImageClick(View view, IProfile profile, boolean current) {
                startActivityForResult(new Intent(newMain.this, LoginActivity.class), 57894);
                if (result != null)
                    result.closeDrawer();
                return true;
            }

            @Override
            public boolean onProfileImageLongClick(View view, IProfile profile, boolean current) {
                result.closeDrawer();
                startActivity(new Intent(newMain.this, StateActivity.class));
                return true;
            }
        });
        if (LoginServer.isLogedIn(this)) {
            builder.addProfiles(
                    new ProfileDrawerItem().withName("Versión " + versionName + " (" + Integer.toString(versionCode) + ")" + (UpdateUtil.isBeta ? " - BETA" : "")).withEmail(FavSyncro.getEmail(this)).withIcon(ic_main).withIdentifier(9),
                    new ProfileSettingDrawerItem().withName("Cambiar colores").withIcon(CommunityMaterial.Icon.cmd_palette).withIdentifier(22),
                    new ProfileSettingDrawerItem().withName("Actualizar vistos").withIcon(MaterialDesignIconic.Icon.gmi_cloud_upload).withIdentifier(87),
                    new ProfileSettingDrawerItem().withName("Sincronizar vistos").withIcon(CommunityMaterial.Icon.cmd_cloud_sync).withIdentifier(88)
                    //new ProfileSettingDrawerItem().withName("Dropbox (" + (DropboxManager.islogedIn() ? "DESCONECTAR" : "CONECTAR") + ")").withIcon(CommunityMaterial.Icon.cmd_dropbox).withIdentifier(120)
            ).withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                @Override
                public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                    switch (((int) profile.getIdentifier())) {
                        case 9:
                            headerResult.toggleSelectionList(newMain.this);
                            break;
                        case 22:
                            cambiarColor();
                            result.closeDrawer();
                            break;
                        case 87:
                            if (LoginServer.isLogedIn(newMain.this) || DropboxManager.islogedIn()) {
                                startSeenUpdate();
                                result.closeDrawer();
                            } else {
                                Toaster.toast("Por favor inicia sesión en la app o en Dropbox");
                            }
                            break;
                        case 88:
                            if (LoginServer.isLogedIn(newMain.this) || DropboxManager.islogedIn()) {
                                startSeenSync();
                                result.closeDrawer();
                            } else {
                                Toaster.toast("Por favor inicia sesión en la app o en Dropbox");
                            }
                            break;
                        case 120:
                            if (DropboxManager.islogedIn()) {
                                new MaterialDialog.Builder(newMain.this)
                                        .content("¿Cerrar sesión de Dropbox?")
                                        .positiveText("cerrar")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                DropboxManager.logoff(newMain.this);
                                                setUpDrawer();
                                            }
                                        }).build().show();
                            } else {
                                dropboxloging = true;
                                DropboxManager.login(newMain.this, new DropboxManager.LoginCallback() {
                                    @Override
                                    public void onLogin(boolean loged) {
                                        if (loged)
                                            setUpDrawer();
                                    }

                                    @Override
                                    public void onStartLogin() {

                                    }
                                });
                            }
                            result.closeDrawer();
                            break;
                    }
                    return false;
                }
            });
        } else {
            builder.addProfiles(
                    new ProfileDrawerItem().withName(FavSyncro.getEmail(this)).withEmail("Versión " + versionName + " (" + Integer.toString(versionCode) + ")" + (UpdateUtil.isBeta ? " - BETA" : "")).withIcon(ic_main).withIdentifier(9),
                    new ProfileSettingDrawerItem().withName("Cambiar colores").withIcon(CommunityMaterial.Icon.cmd_palette).withIdentifier(22),
                    new ProfileSettingDrawerItem().withName("Actualizar vistos").withIcon(MaterialDesignIconic.Icon.gmi_cloud_upload).withIdentifier(87),
                    new ProfileSettingDrawerItem().withName("Sincronizar vistos").withIcon(CommunityMaterial.Icon.cmd_cloud_sync).withIdentifier(88)
                    //new ProfileSettingDrawerItem().withName("Dropbox").withEmail(DropboxManager.islogedIn() ? "DESCONECTAR" : "CONECTAR").withIcon(CommunityMaterial.Icon.cmd_dropbox).withIdentifier(120)
            ).withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                @Override
                public boolean onProfileChanged(View view, final IProfile profile, boolean current) {
                    switch (((int) profile.getIdentifier())) {
                        case 9:
                            headerResult.toggleSelectionList(newMain.this);
                            break;
                        case 87:
                            if (LoginServer.isLogedIn(newMain.this) || DropboxManager.islogedIn()) {
                                startSeenUpdate();
                                result.closeDrawer();
                            } else {
                                Toaster.toast("Por favor inicia sesión en la app o en Dropbox");
                            }
                            break;
                        case 88:
                            if (LoginServer.isLogedIn(newMain.this) || DropboxManager.islogedIn()) {
                                startSeenSync();
                                result.closeDrawer();
                            } else {
                                Toaster.toast("Por favor inicia sesión en la app o en Dropbox");
                            }
                            break;
                        case 120:
                            /*if (DropboxManager.islogedIn()) {
                                new MaterialDialog.Builder(newMain.this)
                                        .content("¿Cerrar sesión de Dropbox?")
                                        .positiveText("cerrar")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                DropboxManager.logoff(newMain.this);
                                                setUpDrawer();
                                            }
                                        }).build().show();
                            } else {
                                dropboxloging = true;
                                DropboxManager.login(newMain.this, new DropboxManager.LoginCallback() {
                                    @Override
                                    public void onLogin(boolean loged) {
                                        if (loged)
                                            setUpDrawer();
                                    }

                                    @Override
                                    public void onStartLogin() {

                                    }
                                });
                            }*/
                            startActivityForResult(new Intent(newMain.this, LoginActivity.class), 57894);
                            result.closeDrawer();
                            break;
                        case 22:
                            cambiarColor();
                            result.closeDrawer();
                            break;
                    }
                    return false;
                }
            });
        }
        headerResult = builder.build();
        result = new DrawerBuilder()
                .withActivity(this)
                //.withToolbar(menu_toolbar)
                //.withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(headerResult)
                .withTranslucentStatusBar(true)
                .withHeaderDivider(false)
                .withFooterDivider(false)
                .withStickyFooterDivider(false)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Recientes").withIcon(FontAwesome.Icon.faw_home).withIdentifier(0),
                        new PrimaryDrawerItem().withName("Favoritos").withIcon(MaterialDesignIconic.Icon.gmi_star).withIdentifier(1),
                        new PrimaryDrawerItem().withName("Directorio").withIcon(MaterialDesignIconic.Icon.gmi_view_list_alt).withIdentifier(2),
                        new PrimaryDrawerItem().withName("Siguiendo").withIcon(MaterialDesignIconic.Icon.gmi_alarm_check).withIdentifier(3),
                        new PrimaryDrawerItem().withName("Sugeridos").withIcon(CommunityMaterial.Icon.cmd_account_network).withIdentifier(13),
                        new PrimaryDrawerItem().withName("Random").withIcon(MaterialDesignIconic.Icon.gmi_shuffle).withIdentifier(11),
                        new PrimaryDrawerItem().withName("Explorador").withIcon(MaterialDesignIconic.Icon.gmi_folder).withIdentifier(9),
                        new PrimaryDrawerItem().withName("Historial").withIcon(MaterialDesignIconic.Icon.gmi_eye).withIdentifier(10),
                        new PrimaryDrawerItem().withName("Lista").withIcon(MaterialDesignIconic.Icon.gmi_assignment_returned).withIdentifier(4),
                        new PrimaryDrawerItem().withName("Sobre la app").withIcon(CommunityMaterial.Icon.cmd_information_outline).withIdentifier(20)
                        //new PrimaryDrawerItem().withName("Publicidad").withIcon(MaterialDesignIconic.Icon.gmi_cloud).withIdentifier(8)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
                        switch ((int) iDrawerItem.getIdentifier()) {
                            case 0:
                                result.setSelection(0, false);
                                break;
                            case 1:
                                if (favs_data_ok)
                                    startActivity(new Intent(context, FavoriteMain.class));
                                result.setSelection(0, false);
                                break;
                            case 2:
                                setDir(false);
                                result.setSelection(0, false);
                                break;
                            case 3:
                                startActivity(new Intent(context, AutoEmisionActivity.class));
                                result.setSelection(0, false);
                                result.closeDrawer();
                                break;
                            case 4:
                                startActivity(new Intent(context, WaitList.class));
                                result.setSelection(0, false);
                                result.closeDrawer();
                                break;
                            case 6:
                                String facebookUrl = "https://www.facebook.com/animeflv.app.jordy";
                                Uri uri;
                                try {
                                    getPackageManager().getPackageInfo("com.facebook.katana", 0);
                                    uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
                                } catch (PackageManager.NameNotFoundException e) {
                                    uri = Uri.parse(facebookUrl);
                                }
                                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                                result.setSelection(0, false);
                                result.closeDrawer();
                                break;
                            case 7:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/6hzpua6")));
                                result.setSelection(0, false);
                                result.closeDrawer();
                                break;
                            case 9:
                                result.setSelection(0, false);
                                Intent intent2 = new Intent(context, ExplorerRoot.class);
                                startActivity(intent2);
                                break;
                            case 10:
                                result.setSelection(0, false);
                                startActivity(new Intent(context, HistoryActivity.class));
                                break;
                            case 11:
                                result.setSelection(0, false);
                                startActivity(new Intent(context, RandomActivity.class));
                                break;
                            case 13:
                                result.setSelection(0, false);
                                startActivity(new Intent(context, SuggestionsActivity.class));
                                break;
                            case 15:
                                result.setSelection(0, false);
                                startActivity(new Intent(context, HallActivity.class));
                                break;
                            case 20:
                                result.setSelection(0, false);
                                startActivity(new Intent(context, AboutActivity.class));
                                break;
                            case -1:
                                Intent intent = new Intent(context, Configuracion.class);
                                startActivityForResult(intent, 1326);
                                result.closeDrawer();
                                result.setSelection(0, false);
                                break;
                            default:
                                result.closeDrawer();
                                result.setSelection(0, false);
                                break;
                        }
                        return false;
                    }
                })
                .addStickyDrawerItems(
                        new SecondaryDrawerItem().withName("Configuracion").withIcon(FontAwesome.Icon.faw_cog).withIdentifier(-1)
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {

                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        if (headerResult.isSelectionListShown())
                            headerResult.toggleSelectionList(newMain.this);
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                })
                .build();
        setUpAdmin(NetworkUtils.isNetworkAvailable());
    }

    private void startSeenSync() {
        if (NetworkUtils.isNetworkAvailable()) {
            Log.e("Seen Sync", "Start");
            final MaterialDialog p = new MaterialDialog.Builder(this)
                    .content("Actualizando vistos")
                    .cancelable(false)
                    .progress(true, 0).build();
            p.show();
            FavSyncro.updateLocalSeen(this, new FavSyncro.UpdateCallback() {
                @Override
                public void onUpdate() {
                    Toaster.toast("Lista de capitulos vistos actualizada");
                    p.dismiss();
                    loadMainJson();
                }
            });
        } else {
            Toaster.toast("Sin internet");
        }
    }

    private void startSeenUpdate() {
        if (NetworkUtils.isNetworkAvailable()) {
            Log.e("Seen Update", "Start");
            Toaster.toast("Actualizando...");
            FavSyncro.updateSeen(this, new DropboxManager.UploadCallback() {
                @Override
                public void onUpload(boolean success) {
                    if (success) {
                        Toaster.toast("Actualizado correctamente");
                    } else {
                        Toaster.toast("Error al actualizar");
                    }
                }
            });
        } else {
            Toaster.toast("Sin internet");
        }
    }

    private User getUser(JSONObject object) {
        String email = FavSyncro.getEmail(this);
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return new User(false);
        }
        try {
            JSONArray array = object.getJSONArray("admins");
            for (int o = 0; o < array.length(); o++) {
                if (array.getJSONObject(o).getString("email").equals(email)) {
                    return new User(true, array.getJSONObject(o).getString("name"));
                }
            }
        } catch (JSONException e) {
            Logger.Error(getClass(), e);
            return new User(false);
        }
        return new User(false);
    }

    private void setUpAdmin(boolean isNetwork) {
        if (isNetwork) {
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(5000);
            client.get(ADMINS, null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Keys.Extra.JSON_ADMINS, response.toString()).apply();
                    User current = getUser(response);
                    if (current.isAdmin()) {
                        addAdminOptions(current);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    setUpAdmin(false);
                    Logger.Error(newMain.this.getClass(), throwable);
                }
            });
        } else {
            String json_admin = PreferenceManager.getDefaultSharedPreferences(context).getString(Keys.Extra.JSON_ADMINS, "null");
            if (FileUtil.isJSONValid(json_admin)) {
                try {
                    User current = getUser(new JSONObject(json_admin));
                    if (current.isAdmin()) {
                        addAdminOptions(current);
                    }
                } catch (JSONException e) {
                    Logger.Error(getClass(), e);
                }
            }
        }
    }

    private void addAdminOptions(User current) {
        adminListeners listeners = new adminListeners(context);
        result.addItem(new DividerDrawerItem());
        result.addItem(new SecondaryDrawerItem().withName("ADMIN - " + current.getName()).withSelectable(false));
        result.addItem(new PrimaryDrawerItem().withName("Notificaciones").withIcon(CommunityMaterial.Icon.cmd_code_not_equal_variant).withOnDrawerItemClickListener(listeners.onEncButton()).withIdentifier(55));
    }

    public void showEncDialog() {
        startActivity(new Intent(this, PushManager.class));
    }

    private void cambiarColor() {
        /*int[] colorl = new int[]{
                ColorsRes.GrisLigth(this),
                ColorsRes.Prim(this)
        };
        ColorChooserDialog dialog = new ColorChooserDialog.Builder(this, R.string.color_chooser_prim)
                .theme(ThemeUtils.isAmoled(this) ? Theme.DARK : Theme.LIGHT)
                .customColors(colorl, null)
                .dynamicButtonColor(true)
                .allowUserColorInput(false)
                .allowUserColorInputAlpha(false)
                .doneButton(R.string.next)
                .cancelButton(android.R.string.cancel)
                .preselect(ThemeUtils.isAmoled(this) ? ColorsRes.Dark(this) : ColorsRes.Gris(this))
                .accentMode(true)
                .build();
        dialog.show(this);*/
        startActivityForResult(new Intent(this, ThemeFragmentAdvanced.class), 6699);
    }

    private boolean isXLargeScreen() {
        return (getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private int getHDraw(final Boolean set) {
        int drawable = ThemeUtils.getAccentColorDrawable(this);

        if (set) {
            ArrayList<IProfile> profile = new ArrayList<>();
            profile.add(new ProfileDrawerItem().withName(FavSyncro.getEmail(this)).withEmail("Versión " + versionName + " (" + Integer.toString(versionCode) + ")").withIcon(getHeaderDrawable()).withIdentifier(9));
            headerResult.setBackgroundRes(drawable);
            headerResult.setProfiles(profile);
        }
        return drawable;
    }

    private Drawable getHeaderDrawable() {
        if (ThemeUtils.isAmoled(this)) {
            return ContextCompat.getDrawable(this, R.mipmap.ic_launcher_dark);
        } else {
            return ContextCompat.getDrawable(this, R.mipmap.ic_launcher);
        }
    }

    private void toast(String text) {
        Toaster.toast(text);
    }

    private void setUpAmoled() throws Exception {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ThemeUtils.Theme theme = ThemeUtils.Theme.create(newMain.this);
                isAmoled = theme.isDark;
                toolbar.setBackgroundColor(theme.primary);
                toolbar.setTitleTextColor(theme.textColorToolbar);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    result.getDrawerLayout().setStatusBarBackgroundColor(theme.primaryDark);
                    setTranslucentStatusFlag(true);
                    getWindow().setNavigationBarColor(theme.primary);
                }
                try {
                    if (isXLargeScreen()) {
                        findViewById(R.id.frame).setBackgroundColor(theme.primary);
                        ((CardView) findViewById(R.id.cardMain)).setCardBackgroundColor(theme.primary);
                        menu_toolbar.setBackgroundColor(theme.tablet_toolbar);
                        menu_toolbar.getRootView().setBackgroundColor(theme.tablet_background);
                        toolbar.getRootView().setBackgroundColor(theme.tablet_background);
                    } else {
                        toolbar.getRootView().setBackgroundColor(theme.background);
                    }
                    menu_toolbar.setNavigationIcon(R.drawable.menu);
                    ThemeUtils.setNavigationColor(menu_toolbar, theme.toolbarNavigation);
                    menu_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            result.openDrawer();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private void setTranslucentStatusFlag(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void setUpViews() throws Exception {
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        if (!isXLargeScreen()) {
            menu_toolbar = toolbar;
        } else {
            menu_toolbar = (Toolbar) findViewById(R.id.ltoolbar);
            toolbar.setTitleTextColor(ThemeUtils.isAmoled(this) ? ColorsRes.Blanco(context) : ColorsRes.Negro(context));
        }
        root = (LinearLayout) findViewById(R.id.main_root);
        recyclerView = (RecyclerView) findViewById(R.id.rv_main);
        actionButton = (FloatingActionButton) findViewById(R.id.action_info);
        actionButton.setColorNormal(getColor());
        actionButton.attachToRecyclerView(recyclerView);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStatus();
            }
        });
        actionButton.setVisibility(View.GONE);
        Drawable icon = getResources().getDrawable(R.drawable.ic_get_r);
        icon.setColorFilter(ColorsRes.Blanco(context), PorterDuff.Mode.SRC_ATOP);
        mswipe = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mswipe.setOnRefreshListener(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("noGif", true)) {
            mainNo = new AdapterMainNoGIF(this);
            recyclerView.setAdapter(mainNo);
        } else {
            main = new AdapterMain(this);
            recyclerView.setAdapter(main);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Recientes");
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    if (!recyclerView.canScrollVertically(-1)) {
                        toolbar.setElevation(0);
                    } else {
                        toolbar.setElevation(50);
                    }
            }
        });
        shouldExecuteOnResume = false;
    }

    private void showStatus() {
        new MaterialDialog.Builder(context)
                .content(
                        "Status: " +
                                Status.getCacheStatusString() + "\n\n" +
                                "Version: " +
                                Status.getVersion() + "\n\n" +
                                "Ultima Actualizacion: " +
                                Status.getLateRefresh()
                )
                .title("SERVER STATUS")
                .titleGravity(GravityEnum.CENTER)
                .autoDismiss(true)
                .positiveText("OK")
                .negativeText("CERRAR")
                .neutralText("OCULTAR")
                .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("statusShown", true).apply();
                        actionButton.hide();
                        actionButton.setVisibility(View.GONE);
                    }
                }).build().show();
    }

    private int getColor() {
        return ThemeUtils.getAcentColor(this);
    }

    private void setUpVersion() {
        context = this;
        androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            int ant_ver = PreferenceManager.getDefaultSharedPreferences(this).getInt(Keys.Conf.CURRENT_VERSION, -1);
            if (ant_ver == -1) {
                PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(Keys.Conf.CURRENT_VERSION, versionCode).apply();
                ant_ver = versionCode;
            }
            if (versionCode > ant_ver) {
                PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(Keys.Conf.CURRENT_VERSION, versionCode).apply();
                new MaterialDialog.Builder(this)
                        .title("Versión " + versionName)
                        .titleGravity(GravityEnum.CENTER)
                        .content("Nueva version detectada!!!")
                        .positiveText("changelog")
                        .negativeText("cerrar")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                startActivity(new Intent(newMain.this, ChangelogActivity.class));
                            }
                        }).build().show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDir(Boolean search) {
        Intent intent = new Intent(this, Directorio.class);
        if (search) {
            Bundle bundle = new Bundle();
            bundle.putString("tipo", "Busqueda");
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    public String getInicio() {
        return parser.getInicioUrl(normal, context);
    }

    public void getJson() {
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!mediaStorage.exists()) {
                mediaStorage.mkdirs();
            }
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/inicio.txt");
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/inicio.txt";
        if (NetworkUtils.isNetworkAvailable()) {
            loadMainJson();
        } else {
            if (file.exists()) {
                String infile = FileUtil.getStringFromFile(file_loc);
                getData(infile);
            } else {
                toast("No hay datos guardados");
            }
        }
    }

    public void getData(final String json) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //eids = parser.parseEID(json);
                //tipos = parser.parseTipos(json);
                //EmisionChecker.Refresh();
                Status.reload();
                if (Status.getCacheStatusInt() == 1) {
                    getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isDown", false).apply();
                    if (!PreferenceManager.getDefaultSharedPreferences(newMain.this).getBoolean("statusShown", false)) {
                        actionButton.hide(true);
                        actionButton.setVisibility(View.VISIBLE);
                        actionButton.show();
                    }
                } else {
                    PreferenceManager.getDefaultSharedPreferences(newMain.this).edit().putBoolean("statusShown", false).apply();
                    getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isDown", false).apply();
                    if (actionButton.isVisible()) {
                        actionButton.hide();
                        actionButton.setVisibility(View.GONE);
                    } else {
                        actionButton.setVisibility(View.GONE);
                    }
                }
                if (PreferenceManager.getDefaultSharedPreferences(newMain.this).getBoolean("noGif", true)) {
                    if (mainNo == null) {
                        recreate();
                    } else {
                        if (frun) {
                            mainNo = new AdapterMainNoGIF(newMain.this, MainOrganizer.init(json).list(newMain.this));
                            recyclerView.setAdapter(mainNo);
                            frun = false;
                        } else {
                            mainNo.setData(MainOrganizer.init(json).list(newMain.this));
                        }
                    }
                } else {
                    if (main == null) {
                        recreate();
                    } else {
                        if (frun) {
                            main = new AdapterMain(newMain.this, MainOrganizer.init(json).list(newMain.this));
                            recyclerView.setAdapter(main);
                            frun = false;
                        } else {
                            main.setData(MainOrganizer.init(json).list(newMain.this));
                        }
                    }
                }
                mswipe.setRefreshing(false);
                isFirst();
                getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isF", false).apply();
            }
        });
    }

    public void ActualizarFavoritos() {
        new FavotiteDB(this).updateOldData(new FavotiteDB.updateDataInterface() {
            @Override
            public void onFinish() {
                favs_data_ok = true;
            }
        });
    }

    public void isFirst() {
        mswipe.post(new Runnable() {
            @Override
            public void run() {
                mswipe.setRefreshing(false);
            }
        });
        if (mswipe.isRefreshing()) {
            mswipe.setRefreshing(false);
        }
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(6991);

    }

    public void load(String s) {
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!mediaStorage.exists()) {
                mediaStorage.mkdirs();
            }
        }
        if (s.equals("null")) {
            toast("Sin cache para mostrar");
        } else {
            getData(s);
        }
    }

    public void loadMainJson() {
        BaseGetter.getJson(this, new INICIO(), new BaseGetter.AsyncInterface() {
            @Override
            public void onFinish(String json) {
                load(json);
            }
        });
    }

    @Override
    public void onRefresh() {
        if (NetworkUtils.isNetworkAvailable()) {
            getSharedPreferences("data", MODE_PRIVATE).edit().putInt("nCaps", 0).apply();
            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putStringSet("eidsNot", new HashSet<String>()).apply();
            parser.refreshUrls(context);
            loadMainJson();
        } else {
            if (mswipe.isRefreshing()) {
                mswipe.setRefreshing(false);
            }
        }
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(6991);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSharedPreferences("data", MODE_PRIVATE).edit().putInt("nCaps", 0).apply();
        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putStringSet("eidsNot", new HashSet<String>()).apply();
        parser.refreshUrls(context);
        UtilNotBlocker.setPaused(false);
        if (shouldExecuteOnResume) {
            supportInvalidateOptionsMenu();
            Bypass.check(this, null);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            if (preferences.getBoolean("is_amoled", false) != isAmoled || preferences.getBoolean("use_space", false) != isSpaced) {
                recreate();
            }
            if (NetworkUtils.isNetworkAvailable()) {
                //checkBan(APP);
                loadMainJson();
            } else {
                if (mswipe.isRefreshing()) {
                    mswipe.setRefreshing(false);
                }
            }
            NotificationManager notificationManager = (NotificationManager) this
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(6991);
            if (!currUser.equals(PreferenceManager.getDefaultSharedPreferences(this).getString("login_email", "null"))) {
                setUpDrawer();
            }
            if (dropboxloging) {
                dropboxloging = false;
                String token = Auth.getOAuth2Token();
                if (token != null) {
                    DropboxManager.UpdateToken(token);
                    PreferenceManager.getDefaultSharedPreferences(this).edit().putString(DropboxManager.KEY_DROPBOX, token).apply();
                    setUpDrawer();
                }
            }
        } else {
            shouldExecuteOnResume = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        UtilNotBlocker.setPaused(true);
        currUser = PreferenceManager.getDefaultSharedPreferences(this).getString("login_email", "null");
    }

    @Override
    public void onBackPressed() {
        if (!result.isDrawerOpen()) {
            if (!MainStates.isListing()) {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    MainStates.setProcessing(false, "closed");
                    return;
                }
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Presione ATRAS para salir", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else {
                MainStates.setListing(false);
                if (main != null) {
                    main.setData(MainOrganizer.getList());
                } else if (mainNo != null) {
                    mainNo.setData(MainOrganizer.getList());
                }
            }
        } else {
            result.closeDrawer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CastPlayBackManager.get(this).destroyManager();
        ServerHolder.getInstance().stopServer(this);
        MainStates.setProcessing(false, "destroyed");
        MainStates.setLoadingEmision(false);
        if (UtilDialogPref.getPlayer() != null) {
            if (UtilDialogPref.getPlayer().isPlaying()) {
                UtilDialogPref.getPlayer().stop();
                UtilDialogPref.getPlayer().release();
                UtilDialogPref.setPlayer(null);
            }
        }
        new Parser().saveBackup(this);
        SeenManager.get(this).close();
        FavSyncro.updateServer(context);
        if (receiver != null)
            unregisterReceiver(getReceiver());
    }

    @Override
    public void onPutInList() {
        try {
            Snackbar.make(root, "Añadido a la lista", Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDelFromList() {
        try {
            Snackbar.make(root, "Eliminado de la lista", Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (isXLargeScreen()) {
            if (ThemeUtils.isAmoled(this)) {
                getMenuInflater().inflate(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("new_user", true) ? R.menu.menu_main_new : R.menu.menu_main, menu);
            } else {
                getMenuInflater().inflate(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("new_user", true) ? R.menu.menu_main_dark_new : R.menu.menu_main_dark, menu);
            }
        } else {
            getMenuInflater().inflate(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("new_user", true) ? R.menu.menu_main_new : R.menu.menu_main, menu);
        }
        menu.removeItem(R.id.carg);
        /*MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route);
        MediaRouteActionProvider mediaRouteActionProvider =
                (MediaRouteActionProvider) MenuItemCompat.getActionProvider(
                        mediaRouteMenuItem);
        mediaRouteActionProvider.setRouteSelector(PlayBackManager.get(this).getSelector());*/
        CastPlayBackManager.get(this).registrerMenu(menu, R.id.media_route);
        ThemeUtils.setMenuColor(menu, ThemeUtils.Theme.get(this, ThemeUtils.Theme.KEY_TOOLBAR_NAVIGATION));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.search:
                setDir(true);
                break;
            case R.id.new_user:
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("new_user", false).apply();
                startActivity(new Intent(this, TutorialActivity.class));
                supportInvalidateOptionsMenu();
                break;
        }

        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!isXLargeScreen()) {
            return;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        PlayBackManager.get(this).addCallbacks();
    }

    @Override
    protected void onStop() {
        super.onStop();
        PlayBackManager.get(this).removeCallbacks();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1147) {
            setUpDrawer();
        } else if (requestCode == 6699 && resultCode == 1506) {
            recreate();
        } else if (resultCode == 9988) {
            recreate();
        } else if (requestCode == 57894) {
            setUpDrawer();
        }
    }
}
