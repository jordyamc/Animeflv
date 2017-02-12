package knf.animeflv;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.dropbox.core.android.Auth;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import cz.msebera.android.httpclient.Header;
import knf.animeflv.AutoEmision.AutoEmisionActivity;
import knf.animeflv.Changelog.ChangelogActivity;
import knf.animeflv.Directorio.Directorio;
import knf.animeflv.DownloadService.DownloaderService;
import knf.animeflv.Explorer.ExplorerRoot;
import knf.animeflv.HallFame.HallActivity;
import knf.animeflv.Interfaces.EncryptionListener;
import knf.animeflv.Interfaces.MainRecyclerCallbacks;
import knf.animeflv.JsonFactory.BaseGetter;
import knf.animeflv.JsonFactory.JsonTypes.INICIO;
import knf.animeflv.JsonFactory.ServerGetter;
import knf.animeflv.LoginActivity.DropboxManager;
import knf.animeflv.LoginActivity.LoginBase;
import knf.animeflv.LoginActivity.LoginUser;
import knf.animeflv.PlayBack.CastPlayBackManager;
import knf.animeflv.PlayBack.PlayBackManager;
import knf.animeflv.Random.RandomActivity;
import knf.animeflv.Recientes.MainOrganizer;
import knf.animeflv.Recientes.Status;
import knf.animeflv.Recyclers.AdapterMain;
import knf.animeflv.Recyclers.AdapterMainNoGIF;
import knf.animeflv.Seen.SeenManager;
import knf.animeflv.Tutorial.TutorialActivity;
import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.Keys;
import knf.animeflv.Utils.Logger;
import knf.animeflv.Utils.MainStates;
import knf.animeflv.Utils.NetworkUtils;
import knf.animeflv.Utils.NoLogInterface;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.TrackingHelper;
import knf.animeflv.Utils.UtilDialogPref;
import knf.animeflv.Utils.UtilNotBlocker;
import knf.animeflv.Utils.UtilSound;
import knf.animeflv.Utils.admin.adminListeners;
import knf.animeflv.Utils.objects.User;
import knf.animeflv.WaitList.WaitList;
import knf.animeflv.history.HistoryActivity;
import xdroid.toaster.Toaster;

import static knf.animeflv.Utils.Keys.Login.EMAIL_NORMAL;
import static knf.animeflv.Utils.Keys.Url.ADMINS;

public class newMain extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        LoginServer.callback,
        DirGetter.callback,
        ColorChooserDialog.ColorCallback,
        MainRecyclerCallbacks {
    public Drawer result;
    SwitchCompat typeEncrypt;
    TextInputEditText normalText;
    TextInputEditText finalText;
    TextInputLayout inputLayout;
    private boolean isAmoled;
    private boolean doubleBackToExitPressedOnce = false;
    private String ext_storage_state = Environment.getExternalStorageState();
    private File mediaStorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache");
    private File DirFile = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt");
    private RecyclerView recyclerView;
    private LinearLayout root;
    private Toolbar toolbar;
    private Toolbar menu_toolbar;
    private SwipeRefreshLayout mswipe;
    private int versionCode;
    private String versionName;
    private String androidID;
    private AccountHeader headerResult;
    private String headerTit;
    private Context context;
    private Snackbar waiting;
    private FloatingActionButton actionButton;
    private com.github.clans.fab.FloatingActionButton updateButton;
    private Parser parser = new Parser();
    private boolean shouldExecuteOnResume;
    private TaskType normal = TaskType.NORMAL;
    private TaskType secundario = TaskType.SECUNDARIA;
    private MaterialDialog RapConf;
    private boolean frun = true;
    private boolean tbool = false;
    private boolean dropboxloging = false;
    private AdapterMain main;
    private AdapterMainNoGIF mainNo;
    private Switch nots;
    private AppCompatSpinner sonidos;
    private AppCompatSpinner conexion;
    private AppCompatSpinner repVid;
    private AppCompatSpinner repStream;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private String currUser = "null";

    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anime_main);
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
    }

    private void setUpMain() {
        MainRegistrer.init();
        setUpVersion();
        setUpViews();
        setUpAmoled();
        setUpDrawer();
        getJson();
        NetworkUtils.checkVersion(this, updateButton);
        SharedPreferences prefs = this.getSharedPreferences("data", MODE_PRIVATE);
        frun = true;
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
                    if (isNetworkAvailable()) {
                        Log.d("NewMain", "Block Nots");
                        UtilNotBlocker.setBlocked(true);
                        NetworkUtils.checkVersion(context, updateButton);
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
        if (!DirFile.exists()) {
            ServerGetter.backupDir(this);
        } else if (!FileUtil.isJSONValid(FileUtil.getStringFromFile(DirFile))) {
            DirFile.delete();
            ServerGetter.backupDir(this);
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloaderService.RECEIVER_ACTION_ERROR);
        registerReceiver(getReceiver(), filter);
    }

    private BroadcastReceiver getReceiver() {
        if (receiver == null)
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(DownloaderService.RECEIVER_ACTION_ERROR))
                        loadMainJson();
                }
            };
        return receiver;
    }

    //FIXME: FIX SERVER LOGIN
    private void setUpDrawer() {
        Drawable ic_main;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("is_amoled", false)) {
            ic_main = ContextCompat.getDrawable(this, R.mipmap.ic_launcher_dark);
        } else {
            ic_main = ContextCompat.getDrawable(this, R.mipmap.ic_launcher);
        }
        AccountHeaderBuilder builder = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(getHDraw(false))
                .withCompactStyle(true)
                .withDividerBelowHeader(false)
                .withCloseDrawerOnProfileListClick(false)
                .withSelectionListEnabled(true)
                .withProfileImagesClickable(false);
        if (knf.animeflv.LoginActivity.LoginServer.isLogedIn(this)) {
            builder.addProfiles(
                    new ProfileDrawerItem().withName("Versión " + versionName + " (" + Integer.toString(versionCode) + ")").withEmail(headerTit).withIcon(ic_main).withIdentifier(9),
                    new ProfileSettingDrawerItem().withName("Cambiar colores").withIcon(CommunityMaterial.Icon.cmd_palette).withIdentifier(22),
                    new ProfileSettingDrawerItem().withName("Actualizar vistos").withIcon(MaterialDesignIconic.Icon.gmi_cloud_upload).withIdentifier(87),
                    new ProfileSettingDrawerItem().withName("Sincronizar vistos").withIcon(CommunityMaterial.Icon.cmd_cloud_sync).withIdentifier(88),
                    new ProfileSettingDrawerItem().withName("Dropbox (" + (DropboxManager.islogedIn() ? "DESCONECTAR" : "CONECTAR") + ")").withIcon(CommunityMaterial.Icon.cmd_dropbox).withIdentifier(120)
                    //new ProfileSettingDrawerItem().withName("Configurar cuenta").withIcon(CommunityMaterial.Icon.cmd_account_settings_variant).withIdentifier(99)
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
                            if (knf.animeflv.LoginActivity.LoginServer.isLogedIn(newMain.this) || DropboxManager.islogedIn()) {
                                startSeenUpdate();
                                result.closeDrawer();
                            } else {
                                Toaster.toast("Por favor inicia sesión en la app o en Dropbox");
                            }
                            break;
                        case 88:
                            if (knf.animeflv.LoginActivity.LoginServer.isLogedIn(newMain.this) || DropboxManager.islogedIn()) {
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
                        case 99:
                            startActivityForResult(new Intent(newMain.this, LoginUser.class), 1147);
                            result.closeDrawer();
                            break;
                    }
                    return false;
                }
            });
        } else {
            builder.addProfiles(
                    new ProfileDrawerItem().withName(headerTit).withEmail("Versión " + versionName + " (" + Integer.toString(versionCode) + ")").withIcon(ic_main).withIdentifier(9),
                    new ProfileSettingDrawerItem().withName("Cambiar colores").withIcon(CommunityMaterial.Icon.cmd_palette).withIdentifier(22),
                    new ProfileSettingDrawerItem().withName("Actualizar vistos").withIcon(MaterialDesignIconic.Icon.gmi_cloud_upload).withIdentifier(87),
                    new ProfileSettingDrawerItem().withName("Sincronizar vistos").withIcon(CommunityMaterial.Icon.cmd_cloud_sync).withIdentifier(88),
                    new ProfileSettingDrawerItem().withName("Dropbox").withEmail(DropboxManager.islogedIn() ? "DESCONECTAR" : "CONECTAR").withIcon(CommunityMaterial.Icon.cmd_dropbox).withIdentifier(120)
                    //new ProfileSettingDrawerItem().withName("Iniciar sesión").withIcon(CommunityMaterial.Icon.cmd_account_key).withIdentifier(110)
            ).withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                @Override
                public boolean onProfileChanged(View view, final IProfile profile, boolean current) {
                    switch (((int) profile.getIdentifier())) {
                        case 9:
                            headerResult.toggleSelectionList(newMain.this);
                            break;
                        case 87:
                            if (knf.animeflv.LoginActivity.LoginServer.isLogedIn(newMain.this) || DropboxManager.islogedIn()) {
                                startSeenUpdate();
                                result.closeDrawer();
                            } else {
                                Toaster.toast("Por favor inicia sesión en la app o en Dropbox");
                            }
                            break;
                        case 88:
                            if (knf.animeflv.LoginActivity.LoginServer.isLogedIn(newMain.this) || DropboxManager.islogedIn()) {
                                startSeenSync();
                                result.closeDrawer();
                            } else {
                                Toaster.toast("Por favor inicia sesión en la app o en Dropbox");
                            }
                            break;
                        case 110:
                            startActivityForResult(new Intent(newMain.this, LoginBase.class), 1147);
                            result.closeDrawer();
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
                .withToolbar(menu_toolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(headerResult)
                .withHeaderDivider(false)
                .withFooterDivider(false)
                .withStickyFooterDivider(false)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Recientes").withIcon(FontAwesome.Icon.faw_home).withIdentifier(0),
                        new PrimaryDrawerItem().withName("Favoritos").withIcon(MaterialDesignIconic.Icon.gmi_star).withIdentifier(1),
                        new PrimaryDrawerItem().withName("Directorio").withIcon(MaterialDesignIconic.Icon.gmi_view_list_alt).withIdentifier(2),
                        new PrimaryDrawerItem().withName("Emision").withIcon(MaterialDesignIconic.Icon.gmi_alarm_check).withIdentifier(3),
                        new PrimaryDrawerItem().withName("Random").withIcon(MaterialDesignIconic.Icon.gmi_shuffle).withIdentifier(11),
                        new PrimaryDrawerItem().withName("Explorador").withIcon(MaterialDesignIconic.Icon.gmi_folder).withIdentifier(9),
                        new PrimaryDrawerItem().withName("Historial").withIcon(MaterialDesignIconic.Icon.gmi_eye).withIdentifier(10),
                        new PrimaryDrawerItem().withName("Lista").withIcon(MaterialDesignIconic.Icon.gmi_assignment_returned).withIdentifier(4),
                        new PrimaryDrawerItem().withName("VIP").withIcon(CommunityMaterial.Icon.cmd_crown).withIdentifier(15),
                        new PrimaryDrawerItem().withName("Pagina Oficial").withIcon(FontAwesome.Icon.faw_facebook).withIdentifier(6),
                        new PrimaryDrawerItem().withName("Discord Oficial").withIcon(CommunityMaterial.Icon.cmd_discord).withIdentifier(7),
                        new PrimaryDrawerItem().withName("Publicidad").withIcon(MaterialDesignIconic.Icon.gmi_cloud).withIdentifier(8)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
                        switch ((int) iDrawerItem.getIdentifier()) {
                            case 0:
                                result.setSelection(0, false);
                                break;
                            case 1:
                                Intent in = new Intent(context, Favoritos.class);
                                startActivity(in);
                                result.setSelection(0, false);
                                break;
                            case 2:
                                setDir(false);
                                result.setSelection(0, false);
                                break;
                            case 3:
                                //startActivity(new Intent(context, newEmisionTest.class));
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
                            case 8:
                                startActivity(new Intent(context, ADS.class));
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
                            case 15:
                                result.setSelection(0, false);
                                startActivity(new Intent(context, HallActivity.class));
                                break;
                            case -1:
                                Intent intent = new Intent(context, Configuracion.class);
                                startActivity(intent);
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
        String email = PreferenceManager.getDefaultSharedPreferences(this).getString(EMAIL_NORMAL, "null");
        if (email.equals("null")) {
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
        result.addItem(new PrimaryDrawerItem().withName("Des/Encriptor").withIcon(MaterialDesignIconic.Icon.gmi_lock_open).withOnDrawerItemClickListener(listeners.onEncButton()).withIdentifier(55));
        result.addItem(new PrimaryDrawerItem().withName("Actualizar Server").withIcon(MaterialDesignIconic.Icon.gmi_refresh_sync).withOnDrawerItemClickListener(listeners.onManualButton()).withIdentifier(56));
        result.addItem(new PrimaryDrawerItem().withName("Control de Cuentas").withIcon(MaterialDesignIconic.Icon.gmi_account_circle).withOnDrawerItemClickListener(listeners.onAccountsButton()).withIdentifier(57));
        result.addItem(new PrimaryDrawerItem().withName("Filtrar Emision").withIcon(CommunityMaterial.Icon.cmd_filter_remove).withOnDrawerItemClickListener(listeners.onFilterList()).withIdentifier(58));
    }

    public void showEncDialog() {
        MaterialDialog encrypt = new MaterialDialog.Builder(context)
                .customView(R.layout.encrypt_dialog, false)
                .positiveText("COMENZAR")
                .negativeText("CERRAR")
                .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                .autoDismiss(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finalText.requestFocus();
                        finalText.setHint("Procesando...");
                        inputLayout.setHint("Procesando...");
                        BackEncryption encryption = new BackEncryption(typeEncrypt.isChecked() ? BackEncryption.Type.DECRYPT : BackEncryption.Type.ENCRYPT, normalText.getText().toString());
                        encryption.setOnFinishEncryptListener(new EncryptionListener() {
                            @Override
                            public void onFinish(final String finalString) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (finalString != null) {
                                            finalText.setHint("OK");
                                            inputLayout.setHint("OK");
                                            finalText.setText(finalString);
                                            finalText.requestFocus();
                                        } else {
                                            finalText.setHint("Error!");
                                            inputLayout.setHint("Error");
                                            finalText.setError("Error al ejecutar!");
                                            finalText.requestFocus();
                                        }
                                    }
                                });
                            }
                        });
                        encryption.executeOnExecutor(ExecutorManager.getExecutor());
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .build();
        boolean isAmoled = ThemeUtils.isAmoled(this);
        View root = encrypt.getCustomView();
        typeEncrypt = (SwitchCompat) root.findViewById(R.id.switch_type);
        typeEncrypt.setTextColor(isAmoled ? ColorsRes.Blanco(context) : ColorsRes.Prim(context));
        inputLayout = (TextInputLayout) root.findViewById(R.id.text_input_layout);
        normalText = (TextInputEditText) root.findViewById(R.id.normalText);
        normalText.setHintTextColor(isAmoled ? ColorsRes.Blanco(context) : ColorsRes.Prim(context));
        finalText = (TextInputEditText) root.findViewById(R.id.finalText);
        finalText.setHintTextColor(ColorsRes.Blanco(context));
        typeEncrypt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    typeEncrypt.setText("Desencriptar");
                } else {
                    typeEncrypt.setText("Encriptar");
                }
                finalText.setText("");
                finalText.setHint("Resultado");
                inputLayout.setHint("Resultado");
            }
        });
        encrypt.show();
    }

    private boolean isNetworkAvailable() {
        Boolean net = false;
        int Tcon = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_conexion", "2"));
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        switch (Tcon) {
            case 0:
                NetworkInfo Wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                net = Wifi.isConnected();
                break;
            case 1:
                NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                net = mobile.isConnected();
                break;
            case 2:
                NetworkInfo WifiA = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mobileA = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                net = WifiA.isConnected() || mobileA.isConnected();
                break;
        }
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && net;
    }

    private void cambiarColor() {
        int[] colorl = new int[]{
                ColorsRes.Gris(this),
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
        dialog.show(this);
    }

    private boolean isXLargeScreen() {
        return (getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private int getHDraw(final Boolean set) {
        int drawable = ThemeUtils.getAccentColorDrawable(this);
        headerTit = FavSyncro.getEmail(this);

        if (set) {
            ArrayList<IProfile> profile = new ArrayList<>();
            profile.add(new ProfileDrawerItem().withName(headerTit).withEmail("Versión " + versionName + " (" + Integer.toString(versionCode) + ")").withIcon(getHeaderDrawable()).withIdentifier(9));
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

    public void writeToFile(String body, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(body.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                ex1.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void setUpAmoled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (ThemeUtils.isAmoled(this)) {
            isAmoled = true;
            toolbar.setBackgroundColor(getResources().getColor(R.color.negro));
            if (!isXLargeScreen()) {
                toolbar.getRootView().setBackgroundColor(getResources().getColor(R.color.negro));
            } else {
                findViewById(R.id.frame).setBackgroundColor(ColorsRes.Negro(this));
                toolbar.getRootView().setBackgroundColor(ColorsRes.Prim(this));
                findViewById(R.id.cardMain).setBackgroundColor(ColorsRes.Negro(this));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (!isXLargeScreen()) {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.negro));
                    getWindow().setNavigationBarColor(getResources().getColor(R.color.negro));
                } else {
                    getWindow().setStatusBarColor(ColorsRes.Prim(this));
                    getWindow().setNavigationBarColor(ColorsRes.Negro(this));
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                getWindow().setNavigationBarColor(ColorsRes.Prim(this));
        }
    }

    private void setUpViews() {
        if (!isXLargeScreen()) { //Portrait
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            toolbar = (Toolbar) findViewById(R.id.main_toolbar);
            menu_toolbar = toolbar;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            toolbar = (Toolbar) findViewById(R.id.main_toolbar);
            menu_toolbar = (Toolbar) findViewById(R.id.ltoolbar);

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
        updateButton = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.action_download_update);
        updateButton.setColorNormal(getColor());
        updateButton.setColorPressed(getColor());
        Drawable icon = getResources().getDrawable(R.drawable.ic_get_r);
        icon.setColorFilter(ColorsRes.Blanco(context), PorterDuff.Mode.SRC_ATOP);
        updateButton.setImageDrawable(icon);
        updateButton.hide(false);
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
        new Registrer().execute();
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

    public void loadDir(String data, boolean search) {
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!mediaStorage.exists()) {
                mediaStorage.mkdirs();
            }
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt");
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
        if (isNetworkAvailable() && !data.trim().equals("error")) {
            String trimed = data.trim();
            if (!file.exists()) {
                Log.d("Archivo:", "No existe");
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    Log.d("Archivo:", "Error al crear archivo");
                }
                writeToFile(trimed, file);
                Intent intent = new Intent(context, Directorio.class);
                Bundle bundle = new Bundle();
                bundle.putString("tipo", "Busqueda");
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                Log.d("Archivo", "Existe");
                String infile = FileUtil.getStringFromFile(file_loc);
                if (!infile.trim().equals(data.trim())) {
                    Log.d("Cargar", "Json nuevo");
                    writeToFile(trimed, file);
                    Intent intent = new Intent(context, Directorio.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("tipo", "Busqueda");
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Log.d("Cargar", "Json existente");
                    Intent intent = new Intent(context, Directorio.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("tipo", "Busqueda");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        } else {
            loadSecDir(true);
        }
    }

    public void loadDir(String data) {
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!mediaStorage.exists()) {
                mediaStorage.mkdirs();
            }
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt");
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
        if (isNetworkAvailable() && !data.trim().equals("error")) {
            String trimed = data.trim();
            if (!file.exists()) {
                Log.d("Archivo:", "No existe");
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    Log.d("Archivo:", "Error al crear archivo");
                }

                if (isJSONValid(trimed)) {
                    writeToFile(trimed, file);
                    Intent intent = new Intent(context, Directorio.class);
                    startActivity(intent);
                } else {
                    Toaster.toast("Error en Servidor");
                }
            } else {
                Log.d("Archivo", "Existe");
                String infile = FileUtil.getStringFromFile(file_loc);
                if (!infile.trim().equals(trimed)) {
                    if (isJSONValid(infile)) {
                        if (isJSONValid(trimed)) {
                            Log.d("Cargar", "Json nuevo");
                            writeToFile(trimed, file);
                            Intent intent = new Intent(context, Directorio.class);
                            startActivity(intent);
                        } else {
                            setDir(tbool);
                        }
                    } else {
                        file.delete();
                        setDir(tbool);
                        Toaster.toast("Error en cache, recargando");
                    }
                } else {
                    if (isJSONValid(infile)) {
                        Log.d("Cargar", "Json existente");
                        Intent intent = new Intent(context, Directorio.class);
                        startActivity(intent);
                    } else {
                        file.delete();
                        setDir(tbool);
                        Toaster.toast("Error en cache, recargando");
                    }
                }
            }
        } else {
            if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                if (!mediaStorage.exists()) {
                    mediaStorage.mkdirs();
                }
            }
            File fileoff = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt");
            String file_loc_off = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
            if (fileoff.exists() && isJSONValid(FileUtil.getStringFromFile(file_loc_off))) {
                Intent intent = new Intent(context, Directorio.class);
                startActivity(intent);
            } else {
                //Toaster.toast("Servidor fallando y no hay datos en cache");
                //new DirGetter(context, TaskType.DIRECTORIO).execute(getDirectorioSec());
                loadSecDir(false);
            }
        }
    }

    public void loadSecDir(final boolean search) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setResponseTimeout(10000);
        asyncHttpClient.get(getDirectorioSec() + "?certificate=" + parser.getCertificateSHA1Fingerprint(context), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                if (search) {
                    loadDir(response.toString(), true);
                } else {
                    loadDir(response.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (search) {
                    loadDir("error", true);
                } else {
                    loadDir("error");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (search) {
                    loadDir("error", true);
                } else {
                    loadDir("error");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                if (search) {
                    loadDir("error", true);
                } else {
                    loadDir("error");
                }
            }
        });
    }

    public String getInicio() {
        return parser.getInicioUrl(normal, context);
    }

    public String getDirectorio() {
        return parser.getDirectorioUrl(normal, context);
    }

    public String getDirectorioSec() {
        return parser.getDirectorioUrl(secundario, context);
    }

    public void getJson() {
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!mediaStorage.exists()) {
                mediaStorage.mkdirs();
            }
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/inicio.txt");
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/inicio.txt";
        if (isNetworkAvailable()) {
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
                NetworkUtils.checkVersion(newMain.this, updateButton);
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
                ActualizarFavoritos();
                if (PreferenceManager.getDefaultSharedPreferences(newMain.this).getBoolean("noGif", true)) {
                    if (mainNo == null) {
                        recreate();
                    } else {
                        if (frun) {
                            mainNo = new AdapterMainNoGIF(newMain.this, MainOrganizer.init(json).list());
                            recyclerView.setAdapter(mainNo);
                            frun = false;
                        } else {
                            mainNo.setData(MainOrganizer.init(json).list());
                        }
                    }
                } else {
                    if (main == null) {
                        recreate();
                    } else {
                        if (frun) {
                            main = new AdapterMain(newMain.this, MainOrganizer.init(json).list());
                            recyclerView.setAdapter(main);
                            frun = false;
                        } else {
                            main.setData(MainOrganizer.init(json).list());
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
        if (isNetworkAvailable()) {
            if (knf.animeflv.LoginActivity.LoginServer.isLogedIn(this) || DropboxManager.islogedIn()) {
                FavSyncro.updateLocal(this, new FavSyncro.UpdateCallback() {
                    @Override
                    public void onUpdate() {
                        loadMainJson();
                        Log.d("Reload by Favs", "Main");
                    }
                });
            }
        }
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
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
        if (selectedColor == ColorsRes.Prim(this) || selectedColor == ColorsRes.Gris(context)) {
            ThemeHolder.isDark = selectedColor == ColorsRes.Prim(this);
            int[] colorl = new int[]{
                    ColorsRes.Naranja(this),
                    ColorsRes.Rojo(this),
                    ColorsRes.Gris(this),
                    ColorsRes.Verde(this),
                    ColorsRes.Rosa(this),
                    ColorsRes.Morado(this)
            };
            new ColorChooserDialog.Builder(this, R.string.color_chooser)
                    .theme(ThemeUtils.isAmoled(this) ? Theme.DARK : Theme.LIGHT)
                    .customColors(colorl, null)
                    .dynamicButtonColor(true)
                    .allowUserColorInput(false)
                    .allowUserColorInputAlpha(false)
                    .doneButton(android.R.string.ok)
                    .cancelButton(R.string.back)
                    .preselect(PreferenceManager.getDefaultSharedPreferences(context).getInt("accentColor", ColorsRes.Naranja(context)))
                    .accentMode(true)
                    .build().show(this);
        } else {
            ThemeHolder.accentColor = selectedColor;
            ThemeHolder.applyTheme(this);
            if (UtilSound.getAudioWidget().isShown()) UtilSound.getAudioWidget().hide();
            if (UtilSound.isNotSoundShow)
                UtilSound.toogleNotSound(UtilSound.getCurrentMediaPlayerInt());
            recreate();
        }
    }

    @Override
    public void onRefresh() {
        if (isNetworkAvailable()) {
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
    public void response(String data, TaskType taskType) {

    }

    @Override
    public void ReqDirs(String data, TaskType taskType) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        TrackingHelper.track(this, TrackingHelper.MAIN);
        getSharedPreferences("data", MODE_PRIVATE).edit().putInt("nCaps", 0).apply();
        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putStringSet("eidsNot", new HashSet<String>()).apply();
        parser.refreshUrls(context);
        ActualizarFavoritos();
        UtilNotBlocker.setPaused(false);
        if (shouldExecuteOnResume) {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("is_amoled", false) != isAmoled) {
                recreate();
            }
            if (isNetworkAvailable()) {
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
                main.setData(MainOrganizer.getList());
            }
        } else {
            result.closeDrawer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CastPlayBackManager.get(this).destroyManager();
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
        Snackbar.make(root, "Añadido a la lista", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDelFromList() {
        Snackbar.make(root, "Eliminado de la lista", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (isXLargeScreen()) {
            getMenuInflater().inflate(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("new_user", true) ? R.menu.menu_main_dark_new : R.menu.menu_main_dark, menu);
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
        }
    }

    private class Registrer extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            SyncHttpClient client = new SyncHttpClient();
            client.setLogInterface(new NoLogInterface());
            client.setLoggingEnabled(false);
            client.get(new Parser().getBaseUrl(TaskType.NORMAL, context) + "contador.php?id=" + androidID.trim() + "&version=" + Integer.toString(versionCode), null, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    //Logger.Error(Registrer.this.getClass(), throwable);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.d("Registrer", "OK");
                }
            });
            return null;
        }
    }
}
