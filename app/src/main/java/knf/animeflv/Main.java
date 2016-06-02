package knf.animeflv;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.nullwire.trace.ExceptionHandler;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import knf.animeflv.Directorio.Directorio;
import knf.animeflv.DownloadManager.CookieConstructor;
import knf.animeflv.DownloadManager.ManageDownload;
import knf.animeflv.Emision.AnimeListConstructor;
import knf.animeflv.Emision.EmisionChecker;
import knf.animeflv.Emision.Section.EmisionActivity;
import knf.animeflv.StreamManager.StreamManager;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.UtilSound;
import knf.animeflv.info.Info;
import knf.animeflv.info.InfoNew;
import pl.droidsonroids.gif.GifImageButton;
import xdroid.toaster.Toaster;

public class Main extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        Requests.callback,
        LoginServer.callback,
        DirGetter.callback,
        ColorChooserDialog.ColorCallback {
    public Boolean tbool;
    WebView web;
    WebView web_Links;
    Toolbar toolbar;
    Context context;
    ScrollView scrollView;
    ImageView imgCard1;
    ImageView imgCard2;
    ImageView imgCard3;
    ImageView imgCard4;
    ImageView imgCard5;
    ImageView imgCard6;
    ImageView imgCard7;
    ImageView imgCard8;
    ImageView imgCard9;
    ImageView imgCard10;
    ImageView imgCard11;
    ImageView imgCard12;
    ImageView imgCard13;
    ImageView imgCard14;
    ImageView imgCard15;
    ImageView imgCard16;
    ImageView imgCard17;
    ImageView imgCard18;
    ImageView imgCard19;
    ImageView imgCard20;
    TextView txtTitulo1;
    TextView txtTitulo2;
    TextView txtTitulo3;
    TextView txtTitulo4;
    TextView txtTitulo5;
    TextView txtTitulo6;
    TextView txtTitulo7;
    TextView txtTitulo8;
    TextView txtTitulo9;
    TextView txtTitulo10;
    TextView txtTitulo11;
    TextView txtTitulo12;
    TextView txtTitulo13;
    TextView txtTitulo14;
    TextView txtTitulo15;
    TextView txtTitulo16;
    TextView txtTitulo17;
    TextView txtTitulo18;
    TextView txtTitulo19;
    TextView txtTitulo20;
    TextView txtCapitulo1;
    TextView txtCapitulo2;
    TextView txtCapitulo3;
    TextView txtCapitulo4;
    TextView txtCapitulo5;
    TextView txtCapitulo6;
    TextView txtCapitulo7;
    TextView txtCapitulo8;
    TextView txtCapitulo9;
    TextView txtCapitulo10;
    TextView txtCapitulo11;
    TextView txtCapitulo12;
    TextView txtCapitulo13;
    TextView txtCapitulo14;
    TextView txtCapitulo15;
    TextView txtCapitulo16;
    TextView txtCapitulo17;
    TextView txtCapitulo18;
    TextView txtCapitulo19;
    TextView txtCapitulo20;
    GifImageButton ibDes1;
    GifImageButton ibDes2;
    GifImageButton ibDes3;
    GifImageButton ibDes4;
    GifImageButton ibDes5;
    GifImageButton ibDes6;
    GifImageButton ibDes7;
    GifImageButton ibDes8;
    GifImageButton ibDes9;
    GifImageButton ibDes10;
    GifImageButton ibDes11;
    GifImageButton ibDes12;
    GifImageButton ibDes13;
    GifImageButton ibDes14;
    GifImageButton ibDes15;
    GifImageButton ibDes16;
    GifImageButton ibDes17;
    GifImageButton ibDes18;
    GifImageButton ibDes19;
    GifImageButton ibDes20;
    ImageButton ibVer1;
    ImageButton ibVer2;
    ImageButton ibVer3;
    ImageButton ibVer4;
    ImageButton ibVer5;
    ImageButton ibVer6;
    ImageButton ibVer7;
    ImageButton ibVer8;
    ImageButton ibVer9;
    ImageButton ibVer10;
    ImageButton ibVer11;
    ImageButton ibVer12;
    ImageButton ibVer13;
    ImageButton ibVer14;
    ImageButton ibVer15;
    ImageButton ibVer16;
    ImageButton ibVer17;
    ImageButton ibVer18;
    ImageButton ibVer19;
    ImageButton ibVer20;
    CardView card1;
    CardView card2;
    CardView card3;
    CardView card4;
    CardView card5;
    CardView card6;
    CardView card7;
    CardView card8;
    CardView card9;
    CardView card10;
    CardView card11;
    CardView card12;
    CardView card13;
    CardView card14;
    CardView card15;
    CardView card16;
    CardView card17;
    CardView card18;
    CardView card19;
    CardView card20;
    Switch nots;
    Spinner sonidos;
    Spinner conexion;
    Spinner sp;
    Spinner repVid;
    Spinner repStream;
    TextView textoff;
    ArrayList<GifImageButton> IBsDesList = new ArrayList<>();
    ArrayList<ImageButton> IBsVerList = new ArrayList<>();
    ArrayList<CardView> Cards = new ArrayList<>();
    List<Boolean> isDesc = new ArrayList<>();
    SwipeRefreshLayout mswipe;
    int first = 1;
    String[] eids;
    String[] aids;
    String[] numeros;
    String[] titulos;
    String[] tipos;
    String url;
    String json = "{}";
    Alarm alarm = new Alarm();
    String ext_storage_state = Environment.getExternalStorageState();
    File mediaStorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache");
    Parser parser = new Parser();
    String aidInfo;
    int versionCode;
    String versionName;
    Boolean Streaming = false;
    MaterialDialog d;
    Drawer result;
    boolean doubleBackToExitPressedOnce = false;
    Toolbar ltoolbar;
    Toolbar Dtoolbar;
    File descarga = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache", "Animeflv_Nver.apk");
    SharedPreferences.OnSharedPreferenceChangeListener listener;
    boolean descargando = false;
    GifImageButton GIBT;
    ImageButton IBVT;
    int indexT;
    String eidT = "0";
    boolean shouldExecuteOnResume;
    boolean version = false;
    boolean verOk = false;
    String[] mensaje;
    boolean disM = false;
    boolean pause = false;
    int actdown;
    int Tindex;
    Spinner etEmail;
    EditText etSug;
    EditText cuenta;
    WebView webViewFeed;
    MaterialDialog mat;
    Boolean cancelPost = false;
    Boolean showact = true;
    Spinner contactoS;
    AccountHeader headerResult;
    String headerTit;
    MaterialDialog ndialog;
    int posT;
    int APP = 1;
    int CHAT = 2;
    String urlInfoT = "";
    MaterialDialog RapConf;
    MaterialDialog dialog;
    TaskType normal = TaskType.NORMAL;
    TaskType secundario = TaskType.SECUNDARIA;
    int intentos = 0;
    boolean frun;
    Boolean isAmoled = false;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ActualizarFavoritos();
            handler.postDelayed(this, 500);
        }
    };

    public static String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1) h = "0" + h;
            if (l > 2) h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1)) str.append(':');
        }
        return str.toString();
    }

    public static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile(String filePath) {
        String ret = "";
        try {
            File fl = new File(filePath);
            FileInputStream fin = new FileInputStream(fl);
            ret = convertStreamToString(fin);
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static boolean isNumeric(String str) {
        DecimalFormatSymbols currentLocaleSymbols = DecimalFormatSymbols.getInstance();
        char localeMinusSign = currentLocaleSymbols.getMinusSign();
        if (!Character.isDigit(str.charAt(0)) && str.charAt(0) != localeMinusSign) return false;
        boolean isDecimalSeparatorFound = false;
        char localeDecimalSeparator = currentLocaleSymbols.getDecimalSeparator();
        for (char c : str.substring(1).toCharArray()) {
            if (!Character.isDigit(c)) {
                if (c == localeDecimalSeparator && !isDecimalSeparatorFound) {
                    isDecimalSeparatorFound = true;
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int accent = preferences.getInt("accentColor", ColorsRes.Naranja(this));
        if (preferences.getBoolean("is_amoled", false)) {
            if (accent == ColorsRes.Rojo(this)) {
                setTheme(R.style.AppThemeDarkNoRojo);
            }
            if (accent == ColorsRes.Naranja(this)) {
                setTheme(R.style.AppThemeDarkNoNaranja);
            }
            if (accent == ColorsRes.Gris(this)) {
                setTheme(R.style.AppThemeDarkNoGris);
            }
            if (accent == ColorsRes.Verde(this)) {
                setTheme(R.style.AppThemeDarkNoVerde);
            }
            if (accent == ColorsRes.Rosa(this)) {
                setTheme(R.style.AppThemeDarkNoRosa);
            }
            if (accent == ColorsRes.Morado(this)) {
                setTheme(R.style.AppThemeDarkNoMorado);
            }
        } else {
            if (accent == ColorsRes.Rojo(this)) {
                setTheme(R.style.AppThemeNoRojo);
            }
            if (accent == ColorsRes.Naranja(this)) {
                setTheme(R.style.AppThemeNoNaranja);
            }
            if (accent == ColorsRes.Gris(this)) {
                setTheme(R.style.AppThemeNoGris);
            }
            if (accent == ColorsRes.Verde(this)) {
                setTheme(R.style.AppThemeNoVerde);
            }
            if (accent == ColorsRes.Rosa(this)) {
                setTheme(R.style.AppThemeNoRosa);
            }
            if (accent == ColorsRes.Morado(this)) {
                setTheme(R.style.AppThemeNoMorado);
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anime_inicio);
        context = this;
        Application application = (Application) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Recientes");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        parser.refreshUrls(this);
        extraRules();
        String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        mTracker.send(new HitBuilders.EventBuilder("Inicio", "Start as " + androidID).build());
        ExceptionHandler.register(this, parser.getBaseUrl(normal, this) + "/errors/server.php?id=" + androidID);
        shouldExecuteOnResume = false;
        if (!getSharedPreferences("data", MODE_PRIVATE).getBoolean("intro", false)) {
            startActivity(new Intent(this, Intronew.class));
        }
        checkBan(APP);
        getSharedPreferences("data", MODE_PRIVATE).edit().putInt("nCaps", 0).apply();
        getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("notVer", false).apply();
        Boolean not = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notificaciones", true);
        if (not) {
            alarm.SetAlarm(this);
        }
        first = 1;
        if (!isXLargeScreen(getApplicationContext())) { //Portrait
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            ltoolbar = (Toolbar) findViewById(R.id.ltoolbar);
        }
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("is_amoled", false)) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.negro));
            toolbar.getRootView().setBackgroundColor(getResources().getColor(R.color.negro));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(getResources().getColor(R.color.negro));
                getWindow().setNavigationBarColor(getResources().getColor(R.color.negro));
            }
            isAmoled = true;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(getResources().getColor(R.color.dark));
                getWindow().setNavigationBarColor(getResources().getColor(R.color.prim));
            }
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Recientes");
        parser = new Parser();
        setLoad();
        try {
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (Exception e) {
            toast("ERROR");
        }
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            toast("ERROR");
        }
        final int change = getSharedPreferences("data", MODE_PRIVATE).getInt(Integer.toString(versionCode), 0);
        if (change == 0) {
            Boolean isF = getSharedPreferences("data", MODE_PRIVATE).getBoolean("isF", true);
            if (!isF) ChangelogDialog.create().show(getSupportFragmentManager(), "changelog");
            getSharedPreferences("data", MODE_PRIVATE).edit().putInt(Integer.toString(versionCode), 1).apply();
            extraRules();
        }
        if (isNetworkAvailable()) {
            Log.d("Registrar", androidID);
            new Requests(context, TaskType.CONTAR).execute(parser.getBaseUrl(normal, context) + "contador.php?id=" + androidID.trim() + "&version=" + Integer.toString(versionCode));
        }
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(getHDraw(false))
                .withCompactStyle(true)
                .withSelectionListEnabled(false)
                .addProfiles(
                        new ProfileDrawerItem().withName(headerTit).withEmail("Versión " + versionName + " (" + Integer.toString(versionCode) + ")").withIcon(getResources().getDrawable(R.mipmap.ic_launcher)).withIdentifier(9)
                )
                .withProfileImagesClickable(true)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        result.closeDrawer();
                        cambiarColor();
                        return false;
                    }
                })
                .build();
        if (isXLargeScreen(getApplicationContext())) {
            Dtoolbar = ltoolbar;
        } else {
            Dtoolbar = toolbar;
        }
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(Dtoolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(headerResult)

                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Recientes").withIcon(FontAwesome.Icon.faw_home).withIdentifier(0),
                        new PrimaryDrawerItem().withName("Favoritos").withIcon(/*GoogleMaterial.Icon.gmd_star*/MaterialDesignIconic.Icon.gmi_star).withIdentifier(1),
                        new PrimaryDrawerItem().withName("Directorio").withIcon(MaterialDesignIconic.Icon.gmi_view_list_alt).withIdentifier(2),
                        new PrimaryDrawerItem().withName("Emision").withIcon(MaterialDesignIconic.Icon.gmi_alarm_check).withIdentifier(3),
                        //new PrimaryDrawerItem().withName("Descargas").withIcon(GoogleMaterial.Icon.gmd_file_download).withIdentifier(3),
                        new PrimaryDrawerItem().withName("Sugerencias").withIcon(MaterialDesignIconic.Icon.gmi_assignment).withIdentifier(4),
                        new PrimaryDrawerItem().withName("Pagina Oficial").withIcon(FontAwesome.Icon.faw_facebook).withIdentifier(5),
                        //new PrimaryDrawerItem().withName("Chat").withIcon(GoogleMaterial.Icon.gmd_message).withIdentifier(6),
                        new PrimaryDrawerItem().withName("Web Oficial").withIcon(MaterialDesignIconic.Icon.gmi_view_web).withIdentifier(6),
                        new PrimaryDrawerItem().withName("Publicidad").withIcon(MaterialDesignIconic.Icon.gmi_cloud).withIdentifier(7)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
                        switch (i) {
                            case -1:
                                Intent intent = new Intent(context, Configuracion.class);
                                startActivity(intent);
                                result.closeDrawer();
                                result.setSelection(0);
                                break;
                            case 2:
                                result.setSelection(0);
                                Intent in = new Intent(context, Favoritos.class);
                                startActivity(in);
                                break;
                            case 3:
                                result.setSelection(0);
                                setDir(false);
                                break;
                            case 4:
                                startActivity(new Intent(context, EmisionActivity.class));
                                result.setSelection(0);
                                result.closeDrawer();
                                break;
                            /*case 4:
                                result.setSelection(0);
                                Intent intent2 = new Intent(context, Descargas.class);
                                startActivity(intent2);
                                break;*/
                            case 5://5
                                result.closeDrawer();
                                result.setSelection(0);
                                mat = new MaterialDialog.Builder(context)
                                        .title("Sugerencias")
                                        .titleGravity(GravityEnum.CENTER)
                                        .customView(R.layout.feedback, true)
                                        .positiveText("Enviar")
                                        .negativeText("Cancelar")
                                        .autoDismiss(false)
                                        .callback(new MaterialDialog.ButtonCallback() {
                                            @Override
                                            public void onPositive(MaterialDialog dialog) {
                                                super.onPositive(dialog);
                                                mat.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                                                String email = etEmail.getSelectedItem().toString();
                                                String feedback = etSug.getText().toString();
                                                String Scuenta = cuenta.getText().toString();
                                                String type = contactoS.getSelectedItem().toString().toLowerCase().trim();
                                                int tipo;
                                                if (type.equals("email")) {
                                                    tipo = 0;
                                                } else {
                                                    tipo = 1;
                                                }
                                                boolean ok = false;
                                                if (tipo == 0) {
                                                    ok = true;
                                                }
                                                if (tipo == 1) {
                                                    ok = !Scuenta.trim().equals("");
                                                }
                                                feedback = feedback
                                                        .replace("&", "")
                                                        .replace("=", "")
                                                        .replace("?", "")
                                                        .replace("á", "%a")
                                                        .replace("é", "%e")
                                                        .replace("í", "%i")
                                                        .replace("ó", "%o")
                                                        .replace("ú", "%u")
                                                        .replace(".", "")
                                                        .replace(":::", "");
                                                if (!type.equals("selecciona")) {
                                                    if (ok) {
                                                        if (!feedback.trim().equals("")) {
                                                            if (isNetworkAvailable()) {
                                                                if (tipo == 0) {
                                                                    //webViewFeed.loadUrl("http://animeflvapp.x10.mx/feedback.php?tipo=" + type + "&cuenta=" + email + "&nombre=" + email.toLowerCase() + "&data=" + feedback.replace(" ", "_"));
                                                                    new Requests(context, TaskType.FEEDBACK).execute(parser.getBaseUrl(normal, context) + "feedback.php?tipo=" + type + "&cuenta=" + email + "&nombre=" + email.toLowerCase() + "&data=" + feedback.replace(" ", "_"));
                                                                    new Requests(context, TaskType.FEEDBACK).execute(parser.getBaseUrl(secundario, context) + "feedback.php?tipo=" + type + "&cuenta=" + email + "&nombre=" + email.toLowerCase() + "&data=" + feedback.replace(" ", "_"));
                                                                } else {
                                                                    if (type.equals("twitter") && !Scuenta.startsWith("@")) {
                                                                        Scuenta = "@" + Scuenta;
                                                                    }
                                                                    //webViewFeed.loadUrl("http://animeflvapp.x10.mx/feedback.php?tipo=" + type + "&cuenta=" + Scuenta.replace(" ", "_") + "&nombre=" + email.toLowerCase() + "&data=" + feedback.replace(" ", "_"));
                                                                    new Requests(context, TaskType.FEEDBACK).execute(parser.getBaseUrl(normal, context) + "feedback.php?tipo=" + type + "&cuenta=" + Scuenta.replace(" ", "_") + "&nombre=" + email.toLowerCase() + "&data=" + feedback.replace(" ", "_"));
                                                                    new Requests(context, TaskType.FEEDBACK).execute(parser.getBaseUrl(secundario, context) + "feedback.php?tipo=" + type + "&cuenta=" + Scuenta.replace(" ", "_") + "&nombre=" + email.toLowerCase() + "&data=" + feedback.replace(" ", "_"));
                                                                }
                                                            } else {
                                                                toast("No hay conexion");
                                                                mat.dismiss();
                                                            }
                                                        } else {
                                                            etSug.setError("Por favor escribe algo");
                                                        }
                                                    } else {
                                                        cuenta.setError("Cuenta necesaria");
                                                        if (feedback.trim().equals("")) {
                                                            etSug.setError("Por favor escribe algo");
                                                        }
                                                    }
                                                } else {
                                                    toast("Selecciona medio de contacto");
                                                    if (feedback.trim().equals("")) {
                                                        etSug.setError("Por favor escribe algo");
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onNegative(MaterialDialog dialog) {
                                                super.onPositive(dialog);
                                                cancelPost = true;
                                                mat.dismiss();
                                            }

                                        })
                                        .cancelable(false)
                                        .build();
                                AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
                                Account[] list = manager.getAccounts();
                                etEmail = (Spinner) mat.getCustomView().findViewById(R.id.et_correo);
                                etSug = (EditText) mat.getCustomView().findViewById(R.id.et_sug);
                                cuenta = (EditText) mat.getCustomView().findViewById(R.id.cuenta);
                                cuenta.setTextColor(getResources().getColor(R.color.prim));
                                contactoS = (Spinner) mat.getCustomView().findViewById(R.id.et_contacto);
                                contactoS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        switch (position) {
                                            case 0:
                                                cuenta.setVisibility(View.GONE);
                                            case 1:
                                                cuenta.setVisibility(View.GONE);
                                                etSug.requestFocus();
                                                break;
                                            case 2:
                                                cuenta.setHint("Nombre");
                                                cuenta.setVisibility(View.VISIBLE);
                                                cuenta.requestFocus();
                                                break;
                                            case 3:
                                                cuenta.setHint("@Cuenta");
                                                cuenta.setVisibility(View.VISIBLE);
                                                cuenta.requestFocus();
                                                break;
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                                webViewFeed = (WebView) mat.getCustomView().findViewById(R.id.wv_feedback);
                                webViewFeed.setWebViewClient(new WebViewClient() {
                                    @Override
                                    public void onPageFinished(WebView view, String url) {
                                        if (url.trim().equals(new Parser().getBaseUrl(TaskType.NORMAL, context) + "feedback.php?ok=ok") && !cancelPost) {
                                            view.loadUrl("about:blank");
                                            cancelPost = true;
                                            toast("Sugerencia enviada");
                                            mat.dismiss();
                                        }
                                    }

                                    @Override
                                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                        view.loadUrl(url);
                                        return true;
                                    }
                                });
                                List<String> emails = new ArrayList<String>();
                                for (Account account : list) {
                                    if (account.name.contains("@") && !emails.contains(account.name)) {
                                        Log.d("Agregar", account.name);
                                        emails.add(account.name);
                                    }
                                }
                                String[] mails = new String[emails.size()];
                                emails.toArray(mails);
                                String[] contacto = {"Selecciona", "Email", "Facebook", "Twitter"};
                                if (list.length > 0) {
                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, mails);
                                    etEmail.setAdapter(arrayAdapter);
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, contacto);
                                contactoS.setAdapter(adapter);
                                cancelPost = false;
                                mat.show();
                                break;
                            case 6://6
                                String facebookUrl = "https://www.facebook.com/animeflv.app.jordy";
                                Uri uri;
                                try {
                                    getPackageManager().getPackageInfo("com.facebook.katana", 0);
                                    uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
                                } catch (PackageManager.NameNotFoundException e) {
                                    uri = Uri.parse(facebookUrl);
                                }
                                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                                result.setSelection(0);
                                result.closeDrawer();
                                break;
                            /*case 7:
                                if (isNetworkAvailable()) {
                                    checkBan(CHAT);
                                } else {
                                    toast("Se necesita internet");
                                }
                                result.setSelection(0);
                                result.closeDrawer();
                                break;*/
                            case 7://8
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(parser.getBaseUrl(TaskType.NORMAL, context))));
                                result.setSelection(0);
                                result.closeDrawer();
                                break;
                            case 8://9
                                startActivity(new Intent(context, ADS.class));
                                result.setSelection(0);
                                result.closeDrawer();
                                break;
                        }
                        return false;
                    }
                })
                .addStickyDrawerItems(
                        new SecondaryDrawerItem().withName("Configuracion").withIcon(FontAwesome.Icon.faw_cog)
                )
                .build();
        mswipe.setOnRefreshListener(this);
        mswipe.post(new Runnable() {
            @Override
            public void run() {
                mswipe.setRefreshing(true);
            }
        });
        getJson();
        if (isXLargeScreen(getApplicationContext())) {
            toolbar.inflateMenu(R.menu.menu_main_dark);
        } else {
            toolbar.inflateMenu(R.menu.menu_main);
        }
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                setDir(true);
                return true;
            }
        });
        if (isNetworkAvailable()) {
            new Requests(context, TaskType.VERSION).execute("https://raw.githubusercontent.com/jordyamc/Animeflv/master/app/version.html");
        }
        SharedPreferences prefs = this.getSharedPreferences("data", MODE_PRIVATE);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals("reload") && !pause) {
                    mswipe.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!mswipe.isRefreshing()) {
                                mswipe.setRefreshing(true);
                            }
                        }
                    });
                    if (isNetworkAvailable()) {
                        new Requests(context, TaskType.VERSION).execute("https://raw.githubusercontent.com/jordyamc/Animeflv/master/app/version.html");
                        //new Requests(context, TaskType.GET_INICIO).execute(getInicio());
                        loadMainJson();
                    } else {
                        if (mswipe.isRefreshing()) {
                            mswipe.setRefreshing(false);
                        }
                    }
                    getSharedPreferences("data", MODE_PRIVATE).edit().putInt("nCaps", 0).apply();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            NotificationManager notificationManager = (NotificationManager) context
                                    .getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancel(6991);
                        }
                    }, 200);
                }
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(listener);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            new Requests(context, TaskType.VERSION).execute("https://raw.githubusercontent.com/jordyamc/Animeflv/master/app/version.html");
        }
        if (descarga.exists()) {
            try {
                PackageInfo info = getPackageManager().getPackageArchiveInfo(descarga.getAbsolutePath(), 0);
                if (info.versionCode <= versionCode) {
                    descarga.delete();
                } else {
                    Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                            .setDataAndType(Uri.fromFile(descarga),
                                    "application/vnd.android.package-archive");
                    finish();
                    startActivity(promptInstall);
                }
            } catch (Exception e) {
                descarga.delete();
            }

        }
        ActualizarFavoritos();
        handler.postDelayed(runnable, 500);
        Boolean isF = getSharedPreferences("data", MODE_PRIVATE).getBoolean("isF", true);
        if (!isF) parser.saveBackup(context);
        web_Links.loadUrl(parser.getBaseUrl(TaskType.NORMAL, context));
    }

    public void ActualizarFavoritos() {
        if (isNetworkAvailable()) {
            String email_coded = PreferenceManager.getDefaultSharedPreferences(this).getString("login_email_coded", "null");
            String pass_coded = PreferenceManager.getDefaultSharedPreferences(this).getString("login_pass_coded", "null");
            if (!email_coded.equals("null") && !pass_coded.equals("null")) {
                new Requests(this, TaskType.GET_FAV).execute(parser.getBaseUrl(normal, context) + "fav-server.php?certificate=" + getCertificateSHA1Fingerprint() + "&tipo=get&email_coded=" + email_coded + "&pass_coded=" + pass_coded);
            }
        }
    }

    public int getHDraw(final Boolean set) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int drawable = R.drawable.cargando;
        headerTit = "Animeflv";
        String e = PreferenceManager.getDefaultSharedPreferences(this).getString("login_email", "null");
        int accent = preferences.getInt("accentColor", ColorsRes.Naranja(this));
        if (!e.equals("null")) headerTit = e;
        if (accent == ColorsRes.Rojo(this)) {
            drawable = R.drawable.rojo;
        }
        if (accent == ColorsRes.Naranja(this)) {
            drawable = R.drawable.naranja;
        }
        if (accent == ColorsRes.Gris(this)) {
            drawable = R.drawable.gris;
        }
        if (accent == ColorsRes.Verde(this)) {
            drawable = R.drawable.verde;
        }
        if (accent == ColorsRes.Rosa(this)) {
            drawable = R.drawable.rosa;
        }
        if (accent == ColorsRes.Morado(this)) {
            drawable = R.drawable.morado;
        }

        if (set) {
            ArrayList<IProfile> profile = new ArrayList<>();
            profile.add(new ProfileDrawerItem().withName(headerTit).withEmail("Versión " + versionName + " (" + Integer.toString(versionCode) + ")").withIcon(getResources().getDrawable(R.mipmap.ic_launcher)).withIdentifier(9));
            headerResult.setBackgroundRes(drawable);
            headerResult.setProfiles(profile);
        }
        return drawable;
    }

    public void toast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }

    public String getSD1() {
        String sSDpath = null;
        File fileCur = null;
        for (String sPathCur : Arrays.asList("MicroSD", "external_SD", "sdcard1", "ext_card", "external_sd", "ext_sd", "external", "extSdCard", "externalSdCard", "8E84-7E70")) {
            fileCur = new File("/mnt/", sPathCur);
            if (fileCur.isDirectory() && fileCur.canWrite()) {
                sSDpath = fileCur.getAbsolutePath();
                break;
            }
            if (sSDpath == null) {
                fileCur = new File("/storage/", sPathCur);
                if (fileCur.isDirectory() && fileCur.canWrite()) {
                    sSDpath = fileCur.getAbsolutePath();
                    break;
                }
            }
            if (sSDpath == null) {
                fileCur = new File("/storage/emulated", sPathCur);
                if (fileCur.isDirectory() && fileCur.canWrite()) {
                    sSDpath = fileCur.getAbsolutePath();
                    Log.e("path", sSDpath);
                    break;
                }
            }
        }
        return sSDpath;
    }

    public void onVerclicked(View view) {
        String id = view.getResources().getResourceName(view.getId());
        int index = Integer.parseInt(id.substring(id.lastIndexOf("D") + 1)) - 1;
        List<String> a = Arrays.asList(aids);
        List<String> n = Arrays.asList(numeros);
        File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + a.get(index) + "/" + a.get(index) + "_" + n.get(a.indexOf(a.get(index))) + ".mp4");
        File sd = new File(FileUtil.getSDPath() + "/Animeflv/download/" + a.get(index) + "/" + a.get(index) + "_" + n.get(a.indexOf(a.get(index))) + ".mp4");
        int type = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_video", "0"));
        Log.d("Play type", String.valueOf(type));
        switch (type) {
            case 0:
                if (file.exists()) {
                    StreamManager.internal(context).Play(eids[index], file);
                } else {
                    if (sd.exists()) {
                        StreamManager.internal(context).Play(eids[index], sd);
                    }
                }
                break;
            case 1:
                if (file.exists()) {
                    StreamManager.external(context).Play(eids[index], file);
                } else {
                    if (sd.exists()) {
                        StreamManager.external(context).Play(eids[index], sd);
                    }
                }
                break;
        }
    }

    public void DescargarSD(int position, String downUrl) {
        if (isNetworkAvailable()) {
            checkBan(APP);
            ManageDownload.external(context).startDownload(eids[position], downUrl);
            cancelDown();
        } else {
            toast("No hay conexion a internet");
            cancelDown();
        }
    }

    public void DescargarSD(int position, String downUrl, CookieConstructor constructor) {
        if (isNetworkAvailable()) {
            checkBan(APP);
            ManageDownload.external(context).startDownload(eids[position], downUrl, constructor);
            cancelDown();
        } else {
            toast("No hay conexion a internet");
            cancelDown();
        }
    }

    public void cancelDown() {
        GIBT.setScaleType(ImageView.ScaleType.FIT_END);
        GIBT.setImageResource(R.drawable.ic_borrar_r);
        GIBT.setEnabled(true);
        IBVT.setImageResource(R.drawable.ic_rep_r);
        IBVT.setEnabled(true);
        descargando = false;
    }

    public void cancelStream() {
        GIBT.setScaleType(ImageView.ScaleType.FIT_END);
        GIBT.setImageResource(R.drawable.ic_borrar_r);
        GIBT.setEnabled(true);
        IBVT.setImageResource(R.drawable.ic_rep_r);
        IBVT.setEnabled(true);
        Streaming = false;
        descargando = false;
    }

    public void DescargarInbyURL(int position, String downUrl) {
        if (isNetworkAvailable()) {
            ManageDownload.internal(context).startDownload(eids[position], downUrl);
            cancelDown();
        } else {
            toast("No hay conexion a internet");
            cancelDown();
        }
    }

    public void DescargarInbyURL(int position, String downUrl, CookieConstructor constructor) {
        if (isNetworkAvailable()) {
            ManageDownload.internal(context).startDownload(eids[position], downUrl, constructor);
            cancelDown();
        } else {
            toast("No hay conexion a internet");
            cancelDown();
        }
    }

    public void StreamInbyURL(int position, String url) {
        if (isNetworkAvailable()) {
            checkBan(APP);
            cancelStream();
            int type = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_streaming", "0"));
            Log.d("Streaming", PreferenceManager.getDefaultSharedPreferences(context).getString("t_streaming", "0"));
            switch (type) {
                case 0:
                    StreamManager.internal(context).Stream(eids[position], url);
                    CancelPreDown();
                    break;
                case 1:
                    StreamingExtbyURL(position, url);
                    break;
            }
        } else {
            toast("No hay conexion a internet");
            cancelStream();
        }
    }

    public void StreamingExtbyURL(int position, String url) {
        Intent i = (new Intent(Intent.ACTION_VIEW, Uri.parse(url)).setType("application/mp4"));
        PackageManager pm = context.getPackageManager();
        final ResolveInfo mInfo = pm.resolveActivity(i, 0);
        String id = mInfo.activityInfo.applicationInfo.processName;
        if (id.startsWith("com.mxtech.videoplayer")) {
            StreamManager.mx(context).Stream(eids[position], url);
            CancelPreDown();
        } else {
            StreamManager.external(context).Stream(eids[position], url);

        }
    }

    public void extraRules() {
        String email = PreferenceManager.getDefaultSharedPreferences(this).getString("login_email", "null");
        String email_coded = PreferenceManager.getDefaultSharedPreferences(this).getString("login_email_coded", "null");
        String pass_coded = PreferenceManager.getDefaultSharedPreferences(this).getString("login_pass_coded", "null");
        String fav = getSharedPreferences("data", MODE_PRIVATE).getString("favoritos", "");
        String vistos = getSharedPreferences("data", MODE_PRIVATE).getString("vistos", "");
        if (!email_coded.equals("null") && !pass_coded.equals("null")) {
            new LoginServer(context, TaskType.NEW_USER, email, email_coded, pass_coded, null).execute(parser.getBaseUrl(normal, context) + "fav-server.php?certificate=" + getCertificateSHA1Fingerprint() + "&tipo=nCuenta&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&fav_code=" + fav + ":;:" + vistos);
            //new LoginServer(context, TaskType.NEW_USER, email, email_coded, pass_coded, null).execute(parser.getBaseUrl(secundario, context) + "fav-server.php?tipo=nCuenta&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&fav_code=" + fav + ":;:" + vistos);
        }
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("streaming", true).apply();
    }

    public void onDesClicked(final View view) {
        final GifImageButton imageButton = (GifImageButton) view;
        String id = view.getResources().getResourceName(view.getId());
        final int index = Integer.parseInt(id.substring(id.lastIndexOf("D") + 1)) - 1;
        if (isDesc.get(index)) {
            List<String> a = Arrays.asList(aids);
            List<String> n = Arrays.asList(numeros);
            final File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + a.get(index) + "/" + a.get(index) + "_" + n.get(a.indexOf(a.get(index))) + ".mp4");
            final File sd = new File(FileUtil.getSDPath() + "/Animeflv/download/" + a.get(index) + "/" + a.get(index) + "_" + n.get(a.indexOf(a.get(index))) + ".mp4");
            File del = new File("");
            if (file.exists()) {
                del = file;
            }
            if (sd.exists()) {
                del = sd;
            }
            if (del.exists()) {
                MaterialDialog borrar = new MaterialDialog.Builder(context)
                        .title("Eliminar")
                        .titleGravity(GravityEnum.CENTER)
                        .content("Desea eliminar el capitulo " + n.get(a.indexOf(a.get(index))) + " de " + titulos[index] + "?")
                        .positiveText("Eliminar")
                        .negativeText("Cancelar")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                File del = new File("");
                                if (file.exists()) {
                                    del = file;
                                }
                                if (sd.exists()) {
                                    del = sd;
                                }
                                if (del.delete()) {
                                    isDesc.add(index, false);
                                    imageButton.setImageResource(R.drawable.ic_get_r);
                                    IBsVerList.get(index).setImageResource(R.drawable.ic_ver_no);
                                    IBsVerList.get(index).setEnabled(false);
                                    ManageDownload.cancel(context, eids[index]);
                                    toast("Archivo Eliminado");
                                }
                            }
                        })
                        .build();
                borrar.show();
            } else {
                isDesc.add(index, false);
                imageButton.setScaleType(ImageView.ScaleType.FIT_END);
                imageButton.setImageResource(R.drawable.ic_get_r);
                IBsVerList.get(index).setImageResource(R.drawable.ic_ver_no);
                IBsVerList.get(index).setEnabled(false);
                toast("El archivo ya no existe");
            }
        } else {
            if (!descargando) {
                if (isNetworkAvailable() && verOk) {
                    if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("streaming", false)) {
                        new Requests(context, TaskType.VERSION).execute("https://raw.githubusercontent.com/jordyamc/Animeflv/master/app/version.html");
                        imageButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        imageButton.setImageResource(R.drawable.cargando);
                        imageButton.setEnabled(false);
                        Tindex = index;
                        GIBT = imageButton;
                        IBVT = IBsVerList.get(index);
                        isDesc.add(index, true);
                        descargando = true;
                        indexT = index;
                        eidT = eids[index];
                        if (isNetworkAvailable()) {
                            posT = index;
                            String urlDes = parser.getUrlCached(aids[index], numeros[index]);
                            if (!urlDes.trim().equals("null")) {
                                new Requests(this, TaskType.D_OPTIONS).execute(parser.getInicioUrl(normal, context) + "?url=" + urlDes);
                            } else {
                                toast("Anime no encontrado, actualizando el directorio...");
                                new DirGetter(this, TaskType.ACT_DIR).execute(parser.getDirectorioUrl(normal, context));
                            }
                        } else {
                            toast("No hay conexion a internet");
                            cancelStream();
                        }
                    } else {
                        ndialog = new MaterialDialog.Builder(context)
                                .title(titulos[index])
                                .titleGravity(GravityEnum.CENTER)
                                .content("Desea descargar el capitulo " + numeros[index] + "?")
                                .autoDismiss(false)
                                .cancelable(true)
                                .positiveText("DESCARGA")
                                .negativeText("STREAMING")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        super.onPositive(dialog);
                                        new Requests(context, TaskType.VERSION).execute("https://raw.githubusercontent.com/jordyamc/Animeflv/master/app/version.html");
                                        imageButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                        imageButton.setImageResource(R.drawable.cargando);
                                        imageButton.setEnabled(false);
                                        Tindex = index;
                                        GIBT = imageButton;
                                        IBVT = IBsVerList.get(index);
                                        isDesc.add(index, true);
                                        descargando = true;
                                        indexT = index;
                                        eidT = eids[index];
                                        if (isNetworkAvailable()) {
                                            posT = index;
                                            String urlDes = parser.getUrlCached(aids[index], numeros[index]);
                                            if (!urlDes.trim().equals("null")) {
                                                new Requests(context, TaskType.D_OPTIONS).execute(parser.getInicioUrl(normal, context) + "?url=" + urlDes);
                                            } else {
                                                toast("Anime no encontrado, actualizando el directorio...");
                                                new DirGetter(context, TaskType.ACT_DIR).execute(parser.getDirectorioUrl(normal, context));
                                            }
                                        } else {
                                            toast("No hay conexion a internet");
                                            cancelStream();
                                        }
                                        ndialog.dismiss();
                                    }

                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        super.onPositive(dialog);
                                        new Requests(context, TaskType.VERSION).execute("https://raw.githubusercontent.com/jordyamc/Animeflv/master/app/version.html");
                                        imageButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                        imageButton.setImageResource(R.drawable.cargando);
                                        imageButton.setEnabled(false);
                                        Tindex = index;
                                        GIBT = imageButton;
                                        IBVT = IBsVerList.get(index);
                                        isDesc.add(index, true);
                                        descargando = true;
                                        indexT = index;
                                        eidT = eids[index];
                                        Streaming = true;
                                        if (isNetworkAvailable()) {
                                            Streaming = true;
                                            posT = index;
                                            String urlStream = parser.getUrlCached(aids[index], numeros[index]);
                                            if (!urlStream.trim().equals("null")) {
                                                new Requests(context, TaskType.S_OPTIONS).execute(parser.getInicioUrl(normal, context) + "?url=" + urlStream);
                                            } else {
                                                toast("Anime no encontrado, actualizando el directorio...");
                                                new DirGetter(context, TaskType.ACT_DIR_S).execute(parser.getDirectorioUrl(normal, context));
                                            }
                                        } else {
                                            toast("No hay conexion a internet");
                                            cancelStream();
                                        }
                                        ndialog.dismiss();
                                    }

                                    @Override
                                    public void onNeutral(MaterialDialog dialog) {
                                        super.onNegative(dialog);
                                        ndialog.dismiss();
                                    }
                                })
                                .build();
                        ndialog.show();
                    }
                } else {
                    if (!verOk) {
                        toast("Error en version");
                    } else {
                        toast("No hay conexion");
                    }
                }
            } else {
                toast("Por favor espera...");
            }
        }
    }

    public void onCardClicked(View view) {
        String id = view.getResources().getResourceName(view.getId());
        int index = Integer.parseInt(id.substring(id.lastIndexOf("d") + 1)) - 1;
        setInfo(aids[index], titulos[index], tipos[index]);
    }

    public void checkBan(int Type) {
        String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        switch (Type) {
            case 1:
                if (isNetworkAvailable()) {
                    new Requests(context, TaskType.APP_BAN).execute(parser.getBaseUrl(normal, context) + "ban-hammer.php?type=get&id=" + androidID);
                } else if (getSharedPreferences("data", MODE_PRIVATE).getBoolean("appBanned", false)) {
                    toast("Has sido baneado de la app :(");
                    finish();
                }
                break;
            case 2:
                new Requests(context, TaskType.CHAT_BAN).execute(parser.getBaseUrl(normal, context) + "ban-hammer.php?type=get&id=" + androidID + "&ckattempt=1");
                break;
        }
    }

    public void setDir(Boolean busqueda) {
        tbool = busqueda;
        if (!busqueda) {
            if (isNetworkAvailable()) {
                loadMainDir(busqueda);
            } else {
                if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                    if (!mediaStorage.exists()) {
                        mediaStorage.mkdirs();
                    }
                }
                File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt");
                String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
                if (file.exists()) {
                    if (isJSONValid(getStringFromFile(file_loc))) {
                        Intent intent = new Intent(context, Directorio.class);
                        startActivity(intent);
                    } else {
                        file.delete();
                        setDir(tbool);
                    }
                } else {
                    toast("No hay datos guardados");
                }
            }
        } else {
            if (isNetworkAvailable()) {
                loadMainDir(busqueda);
            } else {
                if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                    if (!mediaStorage.exists()) {
                        mediaStorage.mkdirs();
                    }
                }
                File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt");
                String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
                if (file.exists()) {
                    if (isJSONValid(file_loc)) {
                        Intent intent = new Intent(context, Directorio.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("tipo", "Busqueda");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        file.delete();
                        setDir(tbool);
                    }
                } else {
                    toast("No hay datos guardados");
                }
            }
        }
    }

    public void setInfo(String aid, String titulo, String tipo) {
        aidInfo = aid;
        getSharedPreferences("data", MODE_PRIVATE).edit().putString("aid", aidInfo).apply();
        urlInfoT = parser.getInicioUrl(normal, context) + "?url=" + parser.getUrlAnimeCached(aid);
        if (urlInfoT.trim().contains("url=null")) {
            String url = getUrlInfo(titulo, tipo);
            if (url.trim().equals("null"))
                toast("Error, abra el directorio para actualizar la lista de animes");
            urlInfoT = parser.getInicioUrl(normal, context) + "?url=" + url;
            Log.d("Buscar", "Parser Error ---> GET");
        } else {
            Log.d("Buscar", "Parser");
            Log.d("URL", urlInfoT);
        }
        Bundle bundleInfo = new Bundle();
        bundleInfo.putString("aid", aid);
        Intent intent = new Intent(this, InfoNew.class);
        intent.putExtras(bundleInfo);
        startActivity(intent);
        //new Requests(this, TaskType.GET_INFO).execute(urlInfoT);

    }

    private String getCertificateSHA1Fingerprint() {
        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();
        int flags = PackageManager.GET_SIGNATURES;
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(packageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Signature[] signatures = packageInfo.signatures;
        byte[] cert = signatures[0].toByteArray();
        InputStream input = new ByteArrayInputStream(cert);
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        X509Certificate c = null;
        try {
            c = (X509Certificate) cf.generateCertificate(input);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        String hexString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(c.getEncoded());
            hexString = byte2HexFormatted(publicKey);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return hexString;
    }

    public void actCacheInfo(String json) {
        Bundle bundleInfo = new Bundle();
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!mediaStorage.exists()) {
                mediaStorage.mkdirs();
            }
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + aidInfo + ".txt");
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + aidInfo + ".txt";
        if (isNetworkAvailable()) {
            if (!file.exists()) {
                Log.d("Archivo:", "No existe");
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    Log.d("Archivo:", "Error al crear archivo");
                }
                writeToFile(json, file);
                bundleInfo.putString("aid", parser.getAID(json));
                Intent intent = new Intent(this, Info.class);
                intent.putExtras(bundleInfo);
                startActivity(intent);
            } else {
                Log.d("Archivo", "Existe");
                String infile = getStringFromFile(file_loc);
                if (json.trim().equals(infile.trim())) {
                    bundleInfo.putString("aid", parser.getAID(json));
                    Intent intent = new Intent(this, Info.class);
                    intent.putExtras(bundleInfo);
                    startActivity(intent);
                } else {
                    writeToFile(json, file);
                    bundleInfo.putString("aid", parser.getAID(json));
                    Intent intent = new Intent(this, Info.class);
                    intent.putExtras(bundleInfo);
                    startActivity(intent);
                }
            }
        } else {
            if (file.exists()) {
                bundleInfo.putString("aid", parser.getAID(json));
                Intent intent = new Intent(this, Info.class);
                intent.putExtras(bundleInfo);
                startActivity(intent);
            } else {
                toast("No hay datos guardados");
            }
        }
    }

    public void setLoad() {
        scrollView = (ScrollView) findViewById(R.id.sv_inicio);
        mswipe = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        imgCard1 = (ImageView) findViewById(R.id.imgCardD1);
        imgCard2 = (ImageView) findViewById(R.id.imgCardD2);
        imgCard3 = (ImageView) findViewById(R.id.imgCardD3);
        imgCard4 = (ImageView) findViewById(R.id.imgCardD4);
        imgCard5 = (ImageView) findViewById(R.id.imgCardD5);
        imgCard6 = (ImageView) findViewById(R.id.imgCardD6);
        imgCard7 = (ImageView) findViewById(R.id.imgCardD7);
        imgCard8 = (ImageView) findViewById(R.id.imgCardD8);
        imgCard9 = (ImageView) findViewById(R.id.imgCardD9);
        imgCard10 = (ImageView) findViewById(R.id.imgCardD10);
        imgCard11 = (ImageView) findViewById(R.id.imgCardD11);
        imgCard12 = (ImageView) findViewById(R.id.imgCardD12);
        imgCard13 = (ImageView) findViewById(R.id.imgCardD13);
        imgCard14 = (ImageView) findViewById(R.id.imgCardD14);
        imgCard15 = (ImageView) findViewById(R.id.imgCardD15);
        imgCard16 = (ImageView) findViewById(R.id.imgCardD16);
        imgCard17 = (ImageView) findViewById(R.id.imgCardD17);
        imgCard18 = (ImageView) findViewById(R.id.imgCardD18);
        imgCard19 = (ImageView) findViewById(R.id.imgCardD19);
        imgCard20 = (ImageView) findViewById(R.id.imgCardD20);

        txtTitulo1 = (TextView) findViewById(R.id.tv_cardD_titulo1);
        txtTitulo2 = (TextView) findViewById(R.id.tv_cardD_titulo2);
        txtTitulo3 = (TextView) findViewById(R.id.tv_cardD_titulo3);
        txtTitulo4 = (TextView) findViewById(R.id.tv_cardD_titulo4);
        txtTitulo5 = (TextView) findViewById(R.id.tv_cardD_titulo5);
        txtTitulo6 = (TextView) findViewById(R.id.tv_cardD_titulo6);
        txtTitulo7 = (TextView) findViewById(R.id.tv_cardD_titulo7);
        txtTitulo8 = (TextView) findViewById(R.id.tv_cardD_titulo8);
        txtTitulo9 = (TextView) findViewById(R.id.tv_cardD_titulo9);
        txtTitulo10 = (TextView) findViewById(R.id.tv_cardD_titulo10);
        txtTitulo11 = (TextView) findViewById(R.id.tv_cardD_titulo11);
        txtTitulo12 = (TextView) findViewById(R.id.tv_cardD_titulo12);
        txtTitulo13 = (TextView) findViewById(R.id.tv_cardD_titulo13);
        txtTitulo14 = (TextView) findViewById(R.id.tv_cardD_titulo14);
        txtTitulo15 = (TextView) findViewById(R.id.tv_cardD_titulo15);
        txtTitulo16 = (TextView) findViewById(R.id.tv_cardD_titulo16);
        txtTitulo17 = (TextView) findViewById(R.id.tv_cardD_titulo17);
        txtTitulo18 = (TextView) findViewById(R.id.tv_cardD_titulo18);
        txtTitulo19 = (TextView) findViewById(R.id.tv_cardD_titulo19);
        txtTitulo20 = (TextView) findViewById(R.id.tv_cardD_titulo20);

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("is_amoled", false)) {
            txtTitulo1.setTextColor(getResources().getColor(R.color.blanco));
            txtTitulo2.setTextColor(getResources().getColor(R.color.blanco));
            txtTitulo3.setTextColor(getResources().getColor(R.color.blanco));
            txtTitulo4.setTextColor(getResources().getColor(R.color.blanco));
            txtTitulo5.setTextColor(getResources().getColor(R.color.blanco));
            txtTitulo6.setTextColor(getResources().getColor(R.color.blanco));
            txtTitulo7.setTextColor(getResources().getColor(R.color.blanco));
            txtTitulo8.setTextColor(getResources().getColor(R.color.blanco));
            txtTitulo9.setTextColor(getResources().getColor(R.color.blanco));
            txtTitulo10.setTextColor(getResources().getColor(R.color.blanco));
            txtTitulo11.setTextColor(getResources().getColor(R.color.blanco));
            txtTitulo12.setTextColor(getResources().getColor(R.color.blanco));
            txtTitulo13.setTextColor(getResources().getColor(R.color.blanco));
            txtTitulo14.setTextColor(getResources().getColor(R.color.blanco));
            txtTitulo15.setTextColor(getResources().getColor(R.color.blanco));
            txtTitulo16.setTextColor(getResources().getColor(R.color.blanco));
            txtTitulo17.setTextColor(getResources().getColor(R.color.blanco));
            txtTitulo18.setTextColor(getResources().getColor(R.color.blanco));
            txtTitulo19.setTextColor(getResources().getColor(R.color.blanco));
            txtTitulo20.setTextColor(getResources().getColor(R.color.blanco));
        }

        txtCapitulo1 = (TextView) findViewById(R.id.tv_cardD_capitulo1);
        txtCapitulo2 = (TextView) findViewById(R.id.tv_cardD_capitulo2);
        txtCapitulo3 = (TextView) findViewById(R.id.tv_cardD_capitulo3);
        txtCapitulo4 = (TextView) findViewById(R.id.tv_cardD_capitulo4);
        txtCapitulo5 = (TextView) findViewById(R.id.tv_cardD_capitulo5);
        txtCapitulo6 = (TextView) findViewById(R.id.tv_cardD_capitulo6);
        txtCapitulo7 = (TextView) findViewById(R.id.tv_cardD_capitulo7);
        txtCapitulo8 = (TextView) findViewById(R.id.tv_cardD_capitulo8);
        txtCapitulo9 = (TextView) findViewById(R.id.tv_cardD_capitulo9);
        txtCapitulo10 = (TextView) findViewById(R.id.tv_cardD_capitulo10);
        txtCapitulo11 = (TextView) findViewById(R.id.tv_cardD_capitulo11);
        txtCapitulo12 = (TextView) findViewById(R.id.tv_cardD_capitulo12);
        txtCapitulo13 = (TextView) findViewById(R.id.tv_cardD_capitulo13);
        txtCapitulo14 = (TextView) findViewById(R.id.tv_cardD_capitulo14);
        txtCapitulo15 = (TextView) findViewById(R.id.tv_cardD_capitulo15);
        txtCapitulo16 = (TextView) findViewById(R.id.tv_cardD_capitulo16);
        txtCapitulo17 = (TextView) findViewById(R.id.tv_cardD_capitulo17);
        txtCapitulo18 = (TextView) findViewById(R.id.tv_cardD_capitulo18);
        txtCapitulo19 = (TextView) findViewById(R.id.tv_cardD_capitulo19);
        txtCapitulo20 = (TextView) findViewById(R.id.tv_cardD_capitulo20);
        int color = ColorsRes.Naranja(this);
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            int accent = preferences.getInt("accentColor", ColorsRes.Naranja(this));
            if (accent == ColorsRes.Rojo(this)) {
                color = ColorsRes.Rojo(this);
            }
            if (accent == ColorsRes.Naranja(this)) {
                color = ColorsRes.Naranja(this);
            }
            if (accent == ColorsRes.Gris(this)) {
                color = ColorsRes.Gris(this);
            }
            if (accent == ColorsRes.Verde(this)) {
                color = ColorsRes.Verde(this);
            }
            if (accent == ColorsRes.Rosa(this)) {
                color = ColorsRes.Rosa(this);
            }
            if (accent == ColorsRes.Morado(this)) {
                color = ColorsRes.Morado(this);
            }

            txtCapitulo1.setTextColor(color);
            txtCapitulo2.setTextColor(color);
            txtCapitulo3.setTextColor(color);
            txtCapitulo4.setTextColor(color);
            txtCapitulo5.setTextColor(color);
            txtCapitulo6.setTextColor(color);
            txtCapitulo7.setTextColor(color);
            txtCapitulo8.setTextColor(color);
            txtCapitulo9.setTextColor(color);
            txtCapitulo10.setTextColor(color);
            txtCapitulo11.setTextColor(color);
            txtCapitulo12.setTextColor(color);
            txtCapitulo13.setTextColor(color);
            txtCapitulo14.setTextColor(color);
            txtCapitulo15.setTextColor(color);
            txtCapitulo16.setTextColor(color);
            txtCapitulo17.setTextColor(color);
            txtCapitulo18.setTextColor(color);
            txtCapitulo19.setTextColor(color);
            txtCapitulo20.setTextColor(color);
        } catch (Exception e) {
            toast(e.getMessage());
        }

        ibDes1 = (GifImageButton) findViewById(R.id.ib_descargar_cardD1);
        ibDes2 = (GifImageButton) findViewById(R.id.ib_descargar_cardD2);
        ibDes3 = (GifImageButton) findViewById(R.id.ib_descargar_cardD3);
        ibDes4 = (GifImageButton) findViewById(R.id.ib_descargar_cardD4);
        ibDes5 = (GifImageButton) findViewById(R.id.ib_descargar_cardD5);
        ibDes6 = (GifImageButton) findViewById(R.id.ib_descargar_cardD6);
        ibDes7 = (GifImageButton) findViewById(R.id.ib_descargar_cardD7);
        ibDes8 = (GifImageButton) findViewById(R.id.ib_descargar_cardD8);
        ibDes9 = (GifImageButton) findViewById(R.id.ib_descargar_cardD9);
        ibDes10 = (GifImageButton) findViewById(R.id.ib_descargar_cardD10);
        ibDes11 = (GifImageButton) findViewById(R.id.ib_descargar_cardD11);
        ibDes12 = (GifImageButton) findViewById(R.id.ib_descargar_cardD12);
        ibDes13 = (GifImageButton) findViewById(R.id.ib_descargar_cardD13);
        ibDes14 = (GifImageButton) findViewById(R.id.ib_descargar_cardD14);
        ibDes15 = (GifImageButton) findViewById(R.id.ib_descargar_cardD15);
        ibDes16 = (GifImageButton) findViewById(R.id.ib_descargar_cardD16);
        ibDes17 = (GifImageButton) findViewById(R.id.ib_descargar_cardD17);
        ibDes18 = (GifImageButton) findViewById(R.id.ib_descargar_cardD18);
        ibDes19 = (GifImageButton) findViewById(R.id.ib_descargar_cardD19);
        ibDes20 = (GifImageButton) findViewById(R.id.ib_descargar_cardD20);

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("is_amoled", false)) {
            ibDes1.setColorFilter(Color.argb(255, 255, 255, 255));
            ibDes2.setColorFilter(Color.argb(255, 255, 255, 255));
            ibDes3.setColorFilter(Color.argb(255, 255, 255, 255));
            ibDes4.setColorFilter(Color.argb(255, 255, 255, 255));
            ibDes5.setColorFilter(Color.argb(255, 255, 255, 255));
            ibDes6.setColorFilter(Color.argb(255, 255, 255, 255));
            ibDes7.setColorFilter(Color.argb(255, 255, 255, 255));
            ibDes8.setColorFilter(Color.argb(255, 255, 255, 255));
            ibDes9.setColorFilter(Color.argb(255, 255, 255, 255));
            ibDes10.setColorFilter(Color.argb(255, 255, 255, 255));
            ibDes11.setColorFilter(Color.argb(255, 255, 255, 255));
            ibDes12.setColorFilter(Color.argb(255, 255, 255, 255));
            ibDes13.setColorFilter(Color.argb(255, 255, 255, 255));
            ibDes14.setColorFilter(Color.argb(255, 255, 255, 255));
            ibDes15.setColorFilter(Color.argb(255, 255, 255, 255));
            ibDes16.setColorFilter(Color.argb(255, 255, 255, 255));
            ibDes17.setColorFilter(Color.argb(255, 255, 255, 255));
            ibDes18.setColorFilter(Color.argb(255, 255, 255, 255));
            ibDes19.setColorFilter(Color.argb(255, 255, 255, 255));
            ibDes20.setColorFilter(Color.argb(255, 255, 255, 255));
        }

        ibVer1 = (ImageButton) findViewById(R.id.ib_ver_cardD1);
        ibVer2 = (ImageButton) findViewById(R.id.ib_ver_cardD2);
        ibVer3 = (ImageButton) findViewById(R.id.ib_ver_cardD3);
        ibVer4 = (ImageButton) findViewById(R.id.ib_ver_cardD4);
        ibVer5 = (ImageButton) findViewById(R.id.ib_ver_cardD5);
        ibVer6 = (ImageButton) findViewById(R.id.ib_ver_cardD6);
        ibVer7 = (ImageButton) findViewById(R.id.ib_ver_cardD7);
        ibVer8 = (ImageButton) findViewById(R.id.ib_ver_cardD8);
        ibVer9 = (ImageButton) findViewById(R.id.ib_ver_cardD9);
        ibVer10 = (ImageButton) findViewById(R.id.ib_ver_cardD10);
        ibVer11 = (ImageButton) findViewById(R.id.ib_ver_cardD11);
        ibVer12 = (ImageButton) findViewById(R.id.ib_ver_cardD12);
        ibVer13 = (ImageButton) findViewById(R.id.ib_ver_cardD13);
        ibVer14 = (ImageButton) findViewById(R.id.ib_ver_cardD14);
        ibVer15 = (ImageButton) findViewById(R.id.ib_ver_cardD15);
        ibVer16 = (ImageButton) findViewById(R.id.ib_ver_cardD16);
        ibVer17 = (ImageButton) findViewById(R.id.ib_ver_cardD17);
        ibVer18 = (ImageButton) findViewById(R.id.ib_ver_cardD18);
        ibVer19 = (ImageButton) findViewById(R.id.ib_ver_cardD19);
        ibVer20 = (ImageButton) findViewById(R.id.ib_ver_cardD20);

        textoff = (TextView) findViewById(R.id.textOffline);
        textoff.setVisibility(View.GONE);

        IBsDesList.add(ibDes1);
        IBsVerList.add(ibVer1);
        IBsDesList.add(ibDes2);
        IBsVerList.add(ibVer2);
        IBsDesList.add(ibDes3);
        IBsVerList.add(ibVer3);
        IBsDesList.add(ibDes4);
        IBsVerList.add(ibVer4);
        IBsDesList.add(ibDes5);
        IBsVerList.add(ibVer5);
        IBsDesList.add(ibDes6);
        IBsVerList.add(ibVer6);
        IBsDesList.add(ibDes7);
        IBsVerList.add(ibVer7);
        IBsDesList.add(ibDes8);
        IBsVerList.add(ibVer8);
        IBsDesList.add(ibDes9);
        IBsVerList.add(ibVer9);
        IBsDesList.add(ibDes10);
        IBsVerList.add(ibVer10);
        IBsDesList.add(ibDes11);
        IBsVerList.add(ibVer11);
        IBsDesList.add(ibDes12);
        IBsVerList.add(ibVer12);
        IBsDesList.add(ibDes13);
        IBsVerList.add(ibVer13);
        IBsDesList.add(ibDes14);
        IBsVerList.add(ibVer14);
        IBsDesList.add(ibDes15);
        IBsVerList.add(ibVer15);
        IBsDesList.add(ibDes16);
        IBsVerList.add(ibVer16);
        IBsDesList.add(ibDes17);
        IBsVerList.add(ibVer17);
        IBsDesList.add(ibDes18);
        IBsVerList.add(ibVer18);
        IBsDesList.add(ibDes19);
        IBsVerList.add(ibVer19);
        IBsDesList.add(ibDes20);
        IBsVerList.add(ibVer20);

        card1 = (CardView) findViewById(R.id.card1);
        card2 = (CardView) findViewById(R.id.card2);
        card3 = (CardView) findViewById(R.id.card3);
        card4 = (CardView) findViewById(R.id.card4);
        card5 = (CardView) findViewById(R.id.card5);
        card6 = (CardView) findViewById(R.id.card6);
        card7 = (CardView) findViewById(R.id.card7);
        card8 = (CardView) findViewById(R.id.card8);
        card9 = (CardView) findViewById(R.id.card9);
        card10 = (CardView) findViewById(R.id.card10);
        card11 = (CardView) findViewById(R.id.card11);
        card12 = (CardView) findViewById(R.id.card12);
        card13 = (CardView) findViewById(R.id.card13);
        card14 = (CardView) findViewById(R.id.card14);
        card15 = (CardView) findViewById(R.id.card15);
        card16 = (CardView) findViewById(R.id.card16);
        card17 = (CardView) findViewById(R.id.card17);
        card18 = (CardView) findViewById(R.id.card18);
        card19 = (CardView) findViewById(R.id.card19);
        card20 = (CardView) findViewById(R.id.card20);
        Cards.add(card1);
        Cards.add(card2);
        Cards.add(card3);
        Cards.add(card4);
        Cards.add(card5);
        Cards.add(card6);
        Cards.add(card7);
        Cards.add(card8);
        Cards.add(card9);
        Cards.add(card10);
        Cards.add(card11);
        Cards.add(card12);
        Cards.add(card13);
        Cards.add(card14);
        Cards.add(card15);
        Cards.add(card16);
        Cards.add(card17);
        Cards.add(card18);
        Cards.add(card19);
        Cards.add(card20);
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("is_amoled", false)) {
            for (ImageButton imageButton : IBsVerList) {
                imageButton.setColorFilter(Color.argb(255, 255, 255, 255));
            }
            for (GifImageButton gifImageButton : IBsDesList) {
                gifImageButton.setColorFilter(Color.argb(255, 255, 255, 255));
            }
            for (CardView cardView : Cards) {
                cardView.setCardBackgroundColor(getResources().getColor(R.color.prim));
            }
            textoff.setTextColor(getResources().getColor(R.color.blanco));
            toolbar.getRootView().setBackgroundColor(getResources().getColor(R.color.negro));
            mswipe.setBackgroundColor(getResources().getColor(R.color.prim));
        }
        mswipe.setColorSchemeColors(color);
        web = (WebView) findViewById(R.id.wv_inicio);
        web.getSettings().setJavaScriptEnabled(true);
        web.addJavascriptInterface(new JavaScriptInterface(context), "HtmlViewer");
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().startSync();
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                //web.loadUrl("javascript:"+"var num=e();"+"window.HtmlViewer.showHTMLD2(e());");
                if (!url.contains("api.php") && !url.contains("getHtml.php")) {
                    if (!url.contains("animeflv")) {
                        if (url.contains("zippyshare.com") || url.contains("blank")) {
                            web.loadUrl("javascript:("
                                    + "function(){var l=document.getElementById('dlbutton');" + "var f=document.createEvent('HTMLEvents');" + "f.initEvent('click',true,true);" + "l.dispatchEvent(f);}"
                                    + ")()");
                        } else {
                            if (!url.contains("izanagi.php")) {
                                if (url.contains("amazona")) {
                                    Log.d("Download", "Amazona");
                                    DescargarInbyURL(posT, url);
                                } else {
                                    CancelPreDown();
                                    toast("Error al descargar");
                                }
                            }
                        }
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //web.loadUrl("javascript:window.HtmlViewer.showHTMLD2(document.getElementsByTagName('body')[0].innerHTML);");
                            }
                        }, 10000);
                    }
                } else {
                    if (view.getUrl().contains("api.php?accion=anime")) {
                        web.loadUrl("javascript:window.HtmlViewer.HTMLInfo(document.getElementsByTagName('body')[0].innerHTML);");
                        CookieManager cookieManager = CookieManager.getInstance();
                        String cookies = cookieManager.getCookie(url);
                        getSharedPreferences("data", MODE_PRIVATE).edit().putString("cookies", cookies).apply();
                        //web.loadUrl("about:blank");
                    } else {
                        if (view.getUrl().contains("api.php?accion=inicio")) {
                            web.loadUrl("javascript:window.HtmlViewer.showHTMLD2(document.getElementsByTagName('body')[0].innerHTML);");
                            CookieManager cookieManager = CookieManager.getInstance();
                            String cookies = cookieManager.getCookie(url);
                            getSharedPreferences("data", MODE_PRIVATE).edit().putString("cookies", cookies).apply();
                            //web.loadUrl("about:blank");
                        } else {
                            if (!view.getUrl().contains("izanagi.php")) {
                                web.loadUrl("javascript:window.HtmlViewer.showHTMLD2(document.getElementsByTagName('body')[0].innerHTML);");
                            }
                        }
                    }
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        web.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                String fileName = url.substring(url.lastIndexOf("/") + 1);
                //Log.d("Descarga",url+" " + fileName);
                if (!Streaming) {
                    File Dstorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("_")));
                    if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                        if (!Dstorage.exists()) {
                            Dstorage.mkdirs();
                        }
                    }
                    File archivo = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("_")) + "/" + fileName);
                    if (!archivo.exists() && descargando && verOk) {
                        cancelDown();
                        String urlD = getSharedPreferences("data", MODE_PRIVATE).getString("urlD", null);
                        CookieManager cookieManager = CookieManager.getInstance();
                        String cookie = cookieManager.getCookie(url.substring(0, url.indexOf("/", 8)));
                        CookieConstructor constructor = new CookieConstructor(cookie, web.getSettings().getUserAgentString(), urlD);
                        chooseDownDir(posT, url, constructor);
                        web.loadUrl("about:blank");
                    } else {
                        web.loadUrl("about:blank");
                        cancelDown();
                    }
                } else {
                    int type = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_streaming", "0"));
                    String urlD = getSharedPreferences("data", MODE_PRIVATE).getString("urlD", null);
                    CookieManager cookieManager = CookieManager.getInstance();
                    String cookie = cookieManager.getCookie(url.substring(0, url.indexOf("/", 8)));
                    CookieConstructor constructor = new CookieConstructor(cookie, web.getSettings().getUserAgentString(), urlD);
                    cancelStream();
                    web.loadUrl("about:blank");
                    if (type == 1) {
                        StreamManager.mx(context).Stream(eids[posT], url, constructor);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            StreamManager.internal(context).Stream(eids[posT], url, constructor);
                        } else {
                            if (isMXinstalled()) {
                                toast("Version de android por debajo de lo requerido, reproduciendo en MXPlayer");
                                StreamManager.mx(context).Stream(eids[posT], url, constructor);
                            } else {
                                toast("No hay reproductor adecuado disponible");
                            }
                        }
                    }
                }
            }
        });

        web_Links = (WebView) findViewById(R.id.wv_inicio2);
        web_Links.getSettings().setJavaScriptEnabled(true);
        web_Links.getSettings().setLoadsImagesAutomatically(false);
        web_Links.getSettings().setBlockNetworkLoads(true);
        web_Links.addJavascriptInterface(new JavaScriptInterface(context), "HtmlViewer");
        web_Links.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                //web_Links.loadUrl("javascript:window.HtmlViewer.showHTMLD1" + "(document.getElementById('descargas_box').getElementsByTagName('a')[1].href);");
                web_Links.loadUrl("javascript:" +
                        "var json=JSON.stringify(videos);" +
                        //"var json=JSON.stringify(document.getElementsByName('FlashVars')[0].value);"+
                        "window.HtmlViewer.showHTMLD1(json);");
            }
        });
    }

    public boolean isMXinstalled() {
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = getPackageManager();
        packages = pm.getInstalledApplications(0);
        String pack = "null";
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals("com.mxtech.videoplayer.pro")) {
                pack = "com.mxtech.videoplayer.pro";
                break;
            }
            if (packageInfo.packageName.equals("com.mxtech.videoplayer.ad")) {
                pack = "com.mxtech.videoplayer.ad";
                break;
            }
        }
        return !pack.equals("null");
    }

    public void loadImg(String[] list) {
        final Context context = getApplicationContext();
        final String[] url = list;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PicassoCache.getPicassoInstance(context).load(parser.getBaseUrl(normal, context) + "imagen.php?certificate=" + getCertificateSHA1Fingerprint() + "&thumb=" + url[0]).error(R.drawable.ic_block_r).into(imgCard1);
                PicassoCache.getPicassoInstance(context).load(parser.getBaseUrl(normal, context) + "imagen.php?certificate=" + getCertificateSHA1Fingerprint() + "&thumb=" + url[1]).error(R.drawable.ic_block_r).into(imgCard2);
                PicassoCache.getPicassoInstance(context).load(parser.getBaseUrl(normal, context) + "imagen.php?certificate=" + getCertificateSHA1Fingerprint() + "&thumb=" + url[2]).error(R.drawable.ic_block_r).into(imgCard3);
                PicassoCache.getPicassoInstance(context).load(parser.getBaseUrl(normal, context) + "imagen.php?certificate=" + getCertificateSHA1Fingerprint() + "&thumb=" + url[3]).error(R.drawable.ic_block_r).into(imgCard4);
                PicassoCache.getPicassoInstance(context).load(parser.getBaseUrl(normal, context) + "imagen.php?certificate=" + getCertificateSHA1Fingerprint() + "&thumb=" + url[4]).error(R.drawable.ic_block_r).into(imgCard5);
                PicassoCache.getPicassoInstance(context).load(parser.getBaseUrl(normal, context) + "imagen.php?certificate=" + getCertificateSHA1Fingerprint() + "&thumb=" + url[5]).error(R.drawable.ic_block_r).into(imgCard6);
                PicassoCache.getPicassoInstance(context).load(parser.getBaseUrl(normal, context) + "imagen.php?certificate=" + getCertificateSHA1Fingerprint() + "&thumb=" + url[6]).error(R.drawable.ic_block_r).into(imgCard7);
                PicassoCache.getPicassoInstance(context).load(parser.getBaseUrl(normal, context) + "imagen.php?certificate=" + getCertificateSHA1Fingerprint() + "&thumb=" + url[7]).error(R.drawable.ic_block_r).into(imgCard8);
                PicassoCache.getPicassoInstance(context).load(parser.getBaseUrl(normal, context) + "imagen.php?certificate=" + getCertificateSHA1Fingerprint() + "&thumb=" + url[8]).error(R.drawable.ic_block_r).into(imgCard9);
                PicassoCache.getPicassoInstance(context).load(parser.getBaseUrl(normal, context) + "imagen.php?certificate=" + getCertificateSHA1Fingerprint() + "&thumb=" + url[9]).error(R.drawable.ic_block_r).into(imgCard10);
                PicassoCache.getPicassoInstance(context).load(parser.getBaseUrl(normal, context) + "imagen.php?certificate=" + getCertificateSHA1Fingerprint() + "&thumb=" + url[10]).error(R.drawable.ic_block_r).into(imgCard11);
                PicassoCache.getPicassoInstance(context).load(parser.getBaseUrl(normal, context) + "imagen.php?certificate=" + getCertificateSHA1Fingerprint() + "&thumb=" + url[11]).error(R.drawable.ic_block_r).into(imgCard12);
                PicassoCache.getPicassoInstance(context).load(parser.getBaseUrl(normal, context) + "imagen.php?certificate=" + getCertificateSHA1Fingerprint() + "&thumb=" + url[12]).error(R.drawable.ic_block_r).into(imgCard13);
                PicassoCache.getPicassoInstance(context).load(parser.getBaseUrl(normal, context) + "imagen.php?certificate=" + getCertificateSHA1Fingerprint() + "&thumb=" + url[13]).error(R.drawable.ic_block_r).into(imgCard14);
                PicassoCache.getPicassoInstance(context).load(parser.getBaseUrl(normal, context) + "imagen.php?certificate=" + getCertificateSHA1Fingerprint() + "&thumb=" + url[14]).error(R.drawable.ic_block_r).into(imgCard15);
                PicassoCache.getPicassoInstance(context).load(parser.getBaseUrl(normal, context) + "imagen.php?certificate=" + getCertificateSHA1Fingerprint() + "&thumb=" + url[15]).error(R.drawable.ic_block_r).into(imgCard16);
                PicassoCache.getPicassoInstance(context).load(parser.getBaseUrl(normal, context) + "imagen.php?certificate=" + getCertificateSHA1Fingerprint() + "&thumb=" + url[16]).error(R.drawable.ic_block_r).into(imgCard17);
                PicassoCache.getPicassoInstance(context).load(parser.getBaseUrl(normal, context) + "imagen.php?certificate=" + getCertificateSHA1Fingerprint() + "&thumb=" + url[17]).error(R.drawable.ic_block_r).into(imgCard18);
                PicassoCache.getPicassoInstance(context).load(parser.getBaseUrl(normal, context) + "imagen.php?certificate=" + getCertificateSHA1Fingerprint() + "&thumb=" + url[18]).error(R.drawable.ic_block_r).into(imgCard19);
                PicassoCache.getPicassoInstance(context).load(parser.getBaseUrl(normal, context) + "imagen.php?certificate=" + getCertificateSHA1Fingerprint() + "&thumb=" + url[19]).error(R.drawable.ic_block_r).into(imgCard20);
            }
        });
    }

    public boolean isOnline() {
        return getSharedPreferences("data", MODE_PRIVATE).getBoolean("online", false);
    }

    public void CancelPreDown() {
        try {
            GIBT.setScaleType(ImageView.ScaleType.FIT_END);
            GIBT.setImageResource(R.drawable.ic_get_r);
            GIBT.setEnabled(true);
            IBVT.setImageResource(R.drawable.ic_ver_no);
            IBVT.setEnabled(false);
            isDesc.set(Tindex, false);
            checkButtons(aids, numeros, eids);
            descargando = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadTitulos(String[] list) {
        final String[] titulo = list;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtTitulo1.setText(titulo[0]);
                txtTitulo2.setText(titulo[1]);
                txtTitulo3.setText(titulo[2]);
                txtTitulo4.setText(titulo[3]);
                txtTitulo5.setText(titulo[4]);
                txtTitulo6.setText(titulo[5]);
                txtTitulo7.setText(titulo[6]);
                txtTitulo8.setText(titulo[7]);
                txtTitulo9.setText(titulo[8]);
                txtTitulo10.setText(titulo[9]);
                txtTitulo11.setText(titulo[10]);
                txtTitulo12.setText(titulo[11]);
                txtTitulo13.setText(titulo[12]);
                txtTitulo14.setText(titulo[13]);
                txtTitulo15.setText(titulo[14]);
                txtTitulo16.setText(titulo[15]);
                txtTitulo17.setText(titulo[16]);
                txtTitulo18.setText(titulo[17]);
                txtTitulo19.setText(titulo[18]);
                txtTitulo20.setText(titulo[19]);
            }
        });
    }

    public void loadCapitulos(String[] list) {
        final String[] capitulo = list;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtCapitulo1.setText(capitulo[0]);
                txtCapitulo2.setText(capitulo[1]);
                txtCapitulo3.setText(capitulo[2]);
                txtCapitulo4.setText(capitulo[3]);
                txtCapitulo5.setText(capitulo[4]);
                txtCapitulo6.setText(capitulo[5]);
                txtCapitulo7.setText(capitulo[6]);
                txtCapitulo8.setText(capitulo[7]);
                txtCapitulo9.setText(capitulo[8]);
                txtCapitulo10.setText(capitulo[9]);
                txtCapitulo11.setText(capitulo[10]);
                txtCapitulo12.setText(capitulo[11]);
                txtCapitulo13.setText(capitulo[12]);
                txtCapitulo14.setText(capitulo[13]);
                txtCapitulo15.setText(capitulo[14]);
                txtCapitulo16.setText(capitulo[15]);
                txtCapitulo17.setText(capitulo[16]);
                txtCapitulo18.setText(capitulo[17]);
                txtCapitulo19.setText(capitulo[18]);
                txtCapitulo20.setText(capitulo[19]);
            }
        });
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
            textoff.setVisibility(View.GONE);
            //new Requests(this, TaskType.GET_INICIO).execute(getInicio());
            //web.loadUrl("http://animeflvapp.x10.mx/getHtml.php");
            loadMainJson();
        } else {
            verOk = false;
            if (file.exists()) {
                textoff.setVisibility(View.VISIBLE);
                textoff.setText("MODO OFFLINE");
                String infile = getStringFromFile(file_loc);
                getData(infile);
            } else {
                toast("No hay datos guardados");
            }
        }
    }

    public void loadMainJson() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setResponseTimeout(10000);
        asyncHttpClient.get(getInicio() + "?certificate=" + getCertificateSHA1Fingerprint(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                loadInicio(response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                loadInicio(response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                loadInicio(responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                loadInicio("error");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                loadInicio("error");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                loadInicio("error");
            }
        });
    }

    public void loadSecJson() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setResponseTimeout(10000);
        asyncHttpClient.get(getInicioSec() + "?certificate=" + getCertificateSHA1Fingerprint(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                loadInicio(response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                loadInicio("error");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                loadInicio("error");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                loadInicio("error");
            }
        });
    }

    public void loadMainDir(final boolean search) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setResponseTimeout(10000);
        asyncHttpClient.get(getDirectorio() + "?certificate=" + getCertificateSHA1Fingerprint(), null, new JsonHttpResponseHandler() {
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

    public void loadSecDir(final boolean search) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setResponseTimeout(10000);
        asyncHttpClient.get(getDirectorioSec() + "?certificate=" + getCertificateSHA1Fingerprint(), null, new JsonHttpResponseHandler() {
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
                String infile = getStringFromFile(file_loc);
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
            if (fileoff.exists() && isJSONValid(getStringFromFile(file_loc_off))) {
                Intent intent = new Intent(context, Directorio.class);
                startActivity(intent);
            } else {
                //Toaster.toast("Servidor fallando y no hay datos en cache");
                //new DirGetter(context, TaskType.DIRECTORIO).execute(getDirectorioSec());
                loadSecDir(false);
            }
        }
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
                String infile = getStringFromFile(file_loc);
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
            //new DirGetter(context, TaskType.DIRECTORIO1).execute(getDirectorioSec());
            loadSecDir(true);
        }
    }

    public void getlinks(String json) {
        loadImg(parser.parseLinks(json));
    }

    public void gettitulos(String json) {
        loadTitulos(parser.parseTitulos(json));
    }

    public void getCapitulos(String json) {
        loadCapitulos(parser.parseCapitulos(json));
    }

    public void isFirst() {
        mswipe.post(new Runnable() {
            @Override
            public void run() {
                mswipe.setRefreshing(false);
            }
        });
        if (first == 1) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    scrollView.setVisibility(View.VISIBLE);
                }
            });
            if (mswipe.isRefreshing()) {
                mswipe.setRefreshing(false);
            }
            first = 0;
            NotificationManager notificationManager = (NotificationManager) this
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(6991);
        }
    }

    public void checkForNew(String[] capitulos, String[] aids) {
        List<String> caps = Arrays.asList(capitulos);
        List<String> aid = Arrays.asList(aids);
        for (int a = 0; a < 20; a++) {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("is_amoled", false)) {
                Cards.get(a).setCardBackgroundColor(getResources().getColor(R.color.prim));
            } else {
                Cards.get(a).setCardBackgroundColor(getResources().getColor(R.color.blanco));
            }
            String capitulo = caps.get(a);
            Boolean resaltar = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("resaltar", true);
            if (capitulo.trim().equals("Capitulo 1") || capitulo.trim().contains("OVA") || capitulo.trim().contains("Pelicula")) {
                if (resaltar)
                    Cards.get(a).setCardBackgroundColor(Color.argb(100, 253, 250, 93));
            }
            String favoritos = getSharedPreferences("data", MODE_PRIVATE).getString("favoritos", "");
            Boolean comp = favoritos.startsWith(aid.get(a) + ":::") || favoritos.contains(":::" + aid.get(a) + ":::") || favoritos.endsWith(":::" + aid.get(a));
            if (comp) {
                if (resaltar)
                    Cards.get(a).setCardBackgroundColor(Color.argb(100, 26, 206, 246));
            }
        }
    }

    public void getData(String json) {
        getlinks(json);
        gettitulos(json);
        getCapitulos(json);
        checkForNew(parser.parseCapitulos(json), parser.parseAID(json));
        ActualizarFavoritos();
        titulos = parser.parseTitulos(json);
        eids = parser.parseEID(json);
        aids = parser.parseAID(json);
        numeros = parser.parsenumeros(json);
        tipos = parser.parseTipos(json);
        List<String> estados = parser.parseEstado(json);
        List<String> daycodes = parser.parseDayCode(json);
        List<String> hours = parser.parseHour(json);
        EmisionChecker.init(context).Check(new AnimeListConstructor(eids, tipos, estados, daycodes, hours).list());
        mswipe.setRefreshing(false);
        checkButtons(aids, numeros, eids);
        String teids = "";
        for (String s : eids) {
            teids += ":::" + s;
        }
        getSharedPreferences("data", MODE_PRIVATE).edit().putString("teids", teids).apply();
        isFirst();
        getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isF", false).apply();
    }

    public void checkButtons(String[] aids, String[] numeros, String[] eids) {
        List<String> a = Arrays.asList(aids);
        List<String> n = Arrays.asList(numeros);
        List<String> e = Arrays.asList(eids);
        isDesc = new ArrayList<Boolean>();
        for (String s : e) {
            int index = e.indexOf(s);
            if (index == 20) break;
            File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + a.get(e.indexOf(s)) + "/" + a.get(e.indexOf(s)) + "_" + n.get(e.indexOf(s)) + ".mp4");
            File fileSD = new File(FileUtil.getSDPath() + "/Animeflv/download/" + a.get(e.indexOf(s)) + "/" + a.get(e.indexOf(s)) + "_" + n.get(e.indexOf(s)) + ".mp4");
            if (file.exists() || fileSD.exists()) {
                IBsDesList.get(index).setImageResource(R.drawable.ic_borrar_r);
                IBsVerList.get(index).setEnabled(true);
                IBsVerList.get(index).setImageResource(R.drawable.ic_rep_r);
                isDesc.add(true);
            } else {
                IBsDesList.get(index).setImageResource(R.drawable.ic_get_r);
                IBsVerList.get(index).setEnabled(false);
                IBsVerList.get(index).setImageResource(R.drawable.ic_ver_no);
                isDesc.add(false);
            }
        }
    }

    public String getUrlInfo(String titulo, String tipo) {
        String ftitulo = "";
        String atitulo = titulo.toLowerCase();
        atitulo = atitulo.replace("*", "-");
        atitulo = atitulo.replace(":", "");
        atitulo = atitulo.replace(",", "");
        atitulo = atitulo.replace(" \u2606 ", "-");
        atitulo = atitulo.replace("\u2606", "-");
        atitulo = atitulo.replace("  ", "-");
        atitulo = atitulo.replace("@", "a");
        atitulo = atitulo.replace("&", "-");
        atitulo = atitulo.replace("/", "-");
        atitulo = atitulo.replace(".", "");
        atitulo = atitulo.replace("\"", "");
        atitulo = atitulo.replace("♥", "-");
        for (int x = 0; x < atitulo.length(); x++) {
            if (atitulo.charAt(x) != ' ') {
                ftitulo += atitulo.charAt(x);
            } else {
                if (atitulo.charAt(x) == ' ') {
                    ftitulo += "-";
                }
            }
        }
        ftitulo = ftitulo.replace("!!!", "-3");
        ftitulo = ftitulo.replace("!", "");
        ftitulo = ftitulo.replace("°", "");
        ftitulo = ftitulo.replace("&deg;", "");
        ftitulo = ftitulo.replace("(", "");
        ftitulo = ftitulo.replace(")", "");
        ftitulo = ftitulo.replace("2nd-season", "2");
        ftitulo = ftitulo.replace("'", "");
        if (ftitulo.trim().equals("gintama")) {
            ftitulo = ftitulo + "-2015";
        }
        if (ftitulo.trim().equals("miss-monochrome-the-animation-2")) {
            ftitulo = "miss-monochrome-the-animation-2nd-season";
        }
        if (ftitulo.trim().equals("ore-ga-ojousama-gakkou-ni-shomin-sample-toshite-gets-sareta-ken")) {
            ftitulo = "ore-ga-ojousama-gakkou-ni-shomin-sample-toshite-gets-sareta-";
        }
        if (ftitulo.trim().equals("diabolik-lovers-moreblood")) {
            ftitulo = "diabolik-lovers-more-blood";
        }
        String link = "http://animeflv.net/" + tipo.toLowerCase() + "/" + ftitulo + ".html";
        return link;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isXLargeScreen(getApplicationContext())) {
            getMenuInflater().inflate(R.menu.menu_main_dark, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
        return true;
    }

    @Override
    public void onRefresh() {
        if (isNetworkAvailable()) {
            web_Links.loadUrl(parser.getBaseUrl(TaskType.NORMAL, context));
            getSharedPreferences("data", MODE_PRIVATE).edit().putInt("nCaps", 0).apply();
            //textoff.setVisibility(View.GONE);
            parser.refreshUrls(context);
            new Requests(context, TaskType.VERSION).execute("https://raw.githubusercontent.com/jordyamc/Animeflv/master/app/version.html");
            //new Requests(this, TaskType.GET_INICIO).execute(getInicio());
            loadMainJson();
        } else {
            textoff.setVisibility(View.VISIBLE);
            textoff.setText("MODO OFFLINE");
            if (mswipe.isRefreshing()) {
                mswipe.setRefreshing(false);
            }
        }
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(6991);
    }

    public String getInicio() {
        return parser.getInicioUrl(normal, context);
    }

    public String getInicioSec() {
        return parser.getInicioUrl(secundario, context);
    }

    public String getDirectorio() {
        return parser.getDirectorioUrl(normal, context);
    }

    public String getDirectorioSec() {
        return parser.getDirectorioUrl(secundario, context);
    }

    public void loadInicio(String da) {
        String data = da.replace("<!-- Hosting24 Analytics Code -->\n" +
                "                                                         <script type=\"text/javascript\" src=\"http://stats.hosting24.com/count.php\"></script>\n" +
                "                                                         <!-- End Of Analytics Code -->", "").trim();
        Boolean isF = getSharedPreferences("data", MODE_PRIVATE).getBoolean("isF", true);
        web_Links.loadUrl(parser.getBaseUrl(TaskType.NORMAL, context));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED || isF) {
            if (!isF) {
                frun = false;
                if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                    if (!mediaStorage.exists()) {
                        mediaStorage.mkdirs();
                    }
                }
                File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/inicio.txt");
                String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/inicio.txt";
                if (isNetworkAvailable() && !data.trim().equals("error")) {
                    if (isJSONValid(data)) {
                        if (!file.exists()) {
                            Log.d("Archivo 1:", "No existe");
                            Log.d("Json", data);
                            try {
                                file.createNewFile();
                            } catch (IOException e) {
                                Log.d("Archivo 1:", "Error al crear archivo");
                            }
                            writeToFile(data, file);
                            if (parser.checkStatus(data) == 1) {
                                web.loadUrl("http://animeflv.net");
                                textoff.setText("SERVIDOR DESACTUALIZADO");
                                getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isDown", true).apply();
                                textoff.setVisibility(View.VISIBLE);
                            } else {
                                textoff.setVisibility(View.GONE);
                                getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isDown", false).apply();
                            }
                            getData(data);
                            getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("online", true);
                            intentos = 0;
                        } else {
                            Log.d("Archivo 1", "Existe");
                            String infile = getStringFromFile(file_loc);
                            if (isJSONValid(infile) && isJSONValid(data)) {
                                if (!infile.trim().equals(data.trim())) {
                                    Log.d("Cargar 1", "Json nuevo");
                                    writeToFile(data, file);
                                    if (parser.checkStatus(data) == 1) {
                                        web.loadUrl("http://animeflv.net");
                                        textoff.setText("SERVIDOR DESACTUALIZADO");
                                        textoff.setVisibility(View.VISIBLE);
                                        getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isDown", true).apply();
                                    } else {
                                        textoff.setVisibility(View.GONE);
                                        getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isDown", false).apply();
                                    }
                                    getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("online", true);
                                    getData(data);
                                    intentos = 0;
                                } else {
                                    Log.d("Cargar 1", "Json existente");
                                    if (parser.checkStatus(data) == 1) {
                                        web.loadUrl("http://animeflv.net");
                                        textoff.setText("SERVIDOR DESACTUALIZADO");
                                        textoff.setVisibility(View.VISIBLE);
                                        getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isDown", true).apply();
                                    } else {
                                        textoff.setVisibility(View.GONE);
                                        getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isDown", false).apply();
                                    }
                                    getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("online", true);
                                    getData(infile);
                                    intentos = 0;
                                }
                            } else {
                                file.delete();
                                toast("Error en cache, volviendo a cargar");
                                //new Requests(context, TaskType.GET_INICIO).execute(getInicio());
                                loadMainJson();
                                //web.loadUrl("http://animeflvapp.x10.mx/getHtml.php");
                            }
                        }
                    } else {
                        if (!file.exists()) {
                            Log.d("Archivo 2:", "No existe");
                            if (data.trim().equals("error")) {
                                //toast("Error en servidor, sin cache para mostrar");
                                if (mswipe.isRefreshing()) mswipe.setRefreshing(false);
                                if (intentos < 1) {
                                    //new Requests(context, TaskType.GET_INICIO).execute(getInicioSec());
                                    loadSecJson();
                                    intentos++;
                                } else {
                                    getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("online", false);
                                    toast("Error en servidor, sin cache para mostrar");
                                    intentos = 0;
                                }
                            }
                            if (!isNetworkAvailable()) {
                                toast("Sin cache para mostrar");
                                if (mswipe.isRefreshing()) mswipe.setRefreshing(false);
                            }
                        } else {
                            Log.d("Archivo 2", "Existe");
                            String infile = getStringFromFile(file_loc);
                            if (data.trim().equals("error"))
                                toast("Error en servidor");
                            if (!isNetworkAvailable()) {
                                toast("Cargando desde cache");
                                Log.d("Cargar 2", "Json existente");
                                if (isJSONValid(infile)) {
                                    if (parser.checkStatus(data) == 1) {
                                        web.loadUrl("http://animeflv.net");
                                        textoff.setText("SERVIDOR DESACTUALIZADO");
                                        textoff.setVisibility(View.VISIBLE);
                                        getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isDown", true).apply();
                                    } else {
                                        textoff.setVisibility(View.GONE);
                                        getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isDown", false).apply();
                                    }
                                    getData(infile);
                                } else {
                                    file.delete();
                                    toast("Error en cache, sin conexion");
                                    getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("online", false);
                                    //new Requests(context, TaskType.GET_INICIO).execute(getInicio());
                                }
                            } else {
                                //new Requests(context, TaskType.GET_INICIO).execute(getInicioSec());
                                loadSecJson();
                            }
                        }
                    }
                } else {
                    if (!file.exists()) {
                        Log.d("Archivo 3:", "No existe");
                        if (data.trim().equals("error")) {
                            //toast("Error en servidor, sin cache para mostrar");
                            if (mswipe.isRefreshing()) mswipe.setRefreshing(false);
                            if (intentos < 1) {
                                //new Requests(context, TaskType.GET_INICIO).execute(getInicioSec());
                                loadSecJson();
                                intentos++;
                            } else {
                                getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("online", false);
                                toast("Error en servidor, sin cache para mostrar");
                                intentos = 0;
                            }
                        }
                        if (!isNetworkAvailable()) {
                            toast("Sin cache para mostrar");
                            if (mswipe.isRefreshing()) mswipe.setRefreshing(false);
                        }
                    } else {
                        if (!data.trim().equals("error") && isNetworkAvailable()) {
                            if (intentos < 1) {
                                //new Requests(context, TaskType.GET_INICIO).execute(getInicioSec());
                                loadSecJson();
                                intentos++;
                            } else {
                                toast("Error en servidor, sin cache para mostrar");
                                Log.d("Archivo 3", "Existe");
                                String infile = getStringFromFile(file_loc);
                                toast("Cargando desde cache");
                                Log.d("Cargar 3", "Json existente");
                                if (parser.checkStatus(data) == 1) {
                                    textoff.setText("SERVIDOR DESACTUALIZADO");
                                    textoff.setVisibility(View.VISIBLE);
                                    getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isDown", true).apply();
                                } else {
                                    textoff.setVisibility(View.GONE);
                                    getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isDown", false).apply();
                                }
                                if (isJSONValid(infile)) {
                                    getData(infile);
                                } else {
                                    file.delete();
                                    if (mswipe.isRefreshing()) mswipe.setRefreshing(false);
                                }
                                intentos = 0;
                            }
                        } else {
                            Log.d("Archivo 3", "Existe");
                            String infile = getStringFromFile(file_loc);
                            toast("Cargando desde cache");
                            Log.d("Cargar 3", "Json existente");
                            if (parser.checkStatus(data) == 1) {
                                textoff.setText("SERVIDOR DESACTUALIZADO");
                                textoff.setVisibility(View.VISIBLE);
                                getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isDown", true).apply();
                            } else {
                                textoff.setVisibility(View.GONE);
                                getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isDown", false).apply();
                            }
                            if (isJSONValid(infile)) {
                                getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("online", true);
                                getData(infile);
                            } else {
                                file.delete();
                                if (mswipe.isRefreshing()) mswipe.setRefreshing(false);
                            }
                            intentos = 0;
                        }
                    }
                }
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    frun = true;
                    new DirGetter(context, TaskType.ACT_DIR_MAIN).execute(getDirectorio());
                    getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isF", false).apply();
                    final File saveData = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/data.save");
                    if (saveData.exists()) {
                        new MaterialDialog.Builder(context)
                                .title("Respaldo")
                                .content("Se ah encontrado un respaldo de la configuracion, ¿Desea restaurarlo?")
                                .positiveText("SI")
                                .negativeText("NO")
                                .autoDismiss(true)
                                .cancelable(true)
                                .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        super.onPositive(dialog);
                                        String save = getStringFromFile(saveData.getPath());
                                        if (parser.restoreBackup(save, context) != Parser.Response.OK) {
                                            toast("Error al restaurar");
                                            saveData.delete();
                                        } else {
                                            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("is_amoled", false)) {
                                                recreate();
                                            } else {
                                                loadMainJson();
                                                getHDraw(true);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        super.onNegative(dialog);
                                        saveData.delete();
                                        parser.saveBackup(context);
                                    }
                                })
                                .cancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        saveData.delete();
                                        parser.saveBackup(context);
                                    }
                                }).build().show();
                    } else {
                        RapConf = new MaterialDialog.Builder(context)
                                .title("Configuracion rapida")
                                .titleGravity(GravityEnum.CENTER)
                                .customView(R.layout.rap_conf, false)
                                .positiveText("CONTINUAR")
                                .autoDismiss(false)
                                .cancelable(false)
                                .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        super.onPositive(dialog);
                                        if (sonidos.getSelectedItemPosition() > 0 && conexion.getSelectedItemPosition() > 0 && repVid.getSelectedItemPosition() > 0 && repStream.getSelectedItemPosition() > 0) {
                                            toast("Se pueden volver a modificar desde configuracion");
                                            RapConf.dismiss();
                                            parser.saveBackup(context);
                                            new Login().show(getSupportFragmentManager(), "Login");
                                        } else {
                                            toast("Falta cambiar configuraciones!!!");
                                        }
                                    }
                                }).build();
                        nots = (Switch) RapConf.getCustomView().findViewById(R.id.switch_not_conf);
                        sonidos = (Spinner) RapConf.getCustomView().findViewById(R.id.spinner_sonido_conf);
                        conexion = (Spinner) RapConf.getCustomView().findViewById(R.id.spinner_conexion_conf);
                        repVid = (Spinner) RapConf.getCustomView().findViewById(R.id.spinner_rep_vid);
                        repStream = (Spinner) RapConf.getCustomView().findViewById(R.id.spinner_rep_stream);
                        nots.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("notificaciones", true).apply();
                                } else {
                                    PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("notificaciones", false).apply();
                                }
                            }
                        });
                        List<String> sonido = new ArrayList<>();
                        sonido.add("Selecciona...");
                        sonido.addAll(Arrays.asList(UtilSound.getSoundsNameList()));
                        ArrayAdapter<String> adapterSonidos = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, sonido);
                        sonidos.setAdapter(adapterSonidos);
                        sonidos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position > 0)
                                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString("sonido", Integer.toString(position - 1)).apply();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                        List<String> tipos = new ArrayList<>();
                        tipos.add("Selecciona...");
                        for (String dat : getResources().getStringArray(R.array.tipos)) {
                            tipos.add(dat);
                        }
                        ArrayAdapter<String> adapterConx = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, tipos);
                        conexion.setAdapter(adapterConx);
                        conexion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position > 0)
                                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString("t_conexion", Integer.toString(position - 1)).apply();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                        List<String> repVids = new ArrayList<>();
                        repVids.add("Selecciona...");
                        for (String dat : getResources().getStringArray(R.array.players)) {
                            repVids.add(dat);
                        }
                        ArrayAdapter<String> adapterreps = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, repVids);
                        repVid.setAdapter(adapterreps);
                        repStream.setAdapter(adapterreps);
                        repVid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position > 0)
                                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString("t_video", Integer.toString(position - 1)).apply();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        repStream.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position > 0)
                                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString("t_streaming", Integer.toString(position - 1)).apply();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        RapConf.show();
                    }
                }
                //new Requests(context, TaskType.GET_INICIO).execute(getInicio());
                loadMainJson();
            }
        } else {
            toast("El permiso de almacenamiento es necesario para continuar");
            finish();
            Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            i.setData(Uri.parse("package:" + getPackageName()));
            startActivity(i);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (!isXLargeScreen(getApplicationContext())) {
            return;
        }
    }

    public Boolean isMXInstaled() {
        Boolean is = false;
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = context.getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals("com.mxtech.videoplayer.pro")) {
                is = true;
                break;
            }
            if (packageInfo.packageName.equals("com.mxtech.videoplayer.ad")) {
                is = true;
                break;
            }
        }
        return is;
    }

    public void cambiarColor() {
        Resources getRes = getResources();
        int[] colorl = new int[]{getRes.getColor(R.color.theme_naranja), getRes.getColor(R.color.theme_rojo), getRes.getColor(R.color.theme_gris), getRes.getColor(R.color.theme_verde), getRes.getColor(R.color.theme_rosa), getRes.getColor(R.color.theme_morado)};

        ColorChooserDialog dialog = new ColorChooserDialog.Builder(this, R.string.color_chooser)
                .customColors(colorl, null)
                .dynamicButtonColor(true)
                .allowUserColorInput(false)
                .allowUserColorInputAlpha(false)
                .doneButton(android.R.string.ok)
                .cancelButton(android.R.string.cancel)
                .preselect(PreferenceManager.getDefaultSharedPreferences(context).getInt("accentColor", ColorsRes.Naranja(context)))
                .accentMode(true)
                .build();
        dialog.show(this);
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

    private boolean isNetworkAvailable() {
        Boolean net = false;
        int Tcon = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_conexion", "0"));
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

    @Override
    public void ReqDirs(final String data, TaskType taskType) {
        if (taskType == TaskType.DIRECTORIO) {
            new Thread(new Runnable() {
                @Override
                public void run() {
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
                            String infile = getStringFromFile(file_loc);
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
                        if (fileoff.exists() && isJSONValid(getStringFromFile(file_loc_off))) {
                            Intent intent = new Intent(context, Directorio.class);
                            startActivity(intent);
                        } else {
                            //Toaster.toast("Servidor fallando y no hay datos en cache");
                            //new DirGetter(context, TaskType.DIRECTORIO).execute(getDirectorioSec());
                            loadMainDir(false);
                        }
                    }
                }
            }).start();
        }
        if (taskType == TaskType.ACT_DIR_MAIN) {
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
                        try {
                            writeToFile(trimed, file);
                            Log.d("Directorio", "Actualizado");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        file.delete();
                        Toaster.toast("Error al actualizar directorio");
                        recreate();
                    }
                } else {
                    String infile = getStringFromFile(file_loc);
                    if (!infile.trim().equals(trimed)) {
                        if (isJSONValid(infile)) {
                            try {
                                writeToFile(trimed, file);
                                Log.d("Directorio", "Actualizado");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (isJSONValid(trimed)) {
                                try {
                                    writeToFile(trimed, file);
                                    Log.d("Directorio", "Actualizado");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toaster.toast("Error al actualizar directorio");
                                recreate();
                            }
                        }
                    } else {
                        if (isJSONValid(infile)) {
                            Log.d("Directorio", "Actualizado");
                        } else {
                            Toaster.toast("Error al actualizar directorio");
                            recreate();
                        }
                    }
                }
            }
        }
        if (taskType == TaskType.ACT_DIR) {
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
                        try {
                            writeToFile(trimed, file);
                            if (!frun) {
                                String urlDes = parser.getUrlCached(aids[posT], numeros[posT]);
                                if (!urlDes.equals("null")) {
                                    new Requests(this, TaskType.D_OPTIONS).execute(parser.getInicioUrl(normal, context) + "?url=" + urlDes);
                                } else {
                                    Toaster.toast("Error en servidor");
                                    recreate();
                                }
                            } else {
                                Log.d("Directorio", "Actualizado");
                                frun = false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        file.delete();
                        Toaster.toast("Error al actualizar directorio");
                        recreate();
                    }
                } else {
                    String infile = getStringFromFile(file_loc);
                    if (!infile.trim().equals(trimed)) {
                        if (isJSONValid(infile)) {
                            try {
                                writeToFile(trimed, file);
                                if (!frun) {
                                    String urlStream = parser.getUrlCached(aids[posT], numeros[posT]);
                                    if (!urlStream.trim().equals("null")) {
                                        new Requests(this, TaskType.D_OPTIONS).execute(parser.getInicioUrl(normal, context) + "?url=" + urlStream);
                                    } else {
                                        Toaster.toast("Error en servidor");
                                        recreate();
                                    }
                                } else {
                                    Log.d("Directorio", "Actualizado");
                                    frun = false;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (isJSONValid(trimed)) {
                                try {
                                    writeToFile(trimed, file);
                                    if (!frun) {
                                        String urlStream = parser.getUrlCached(aids[posT], numeros[posT]);
                                        if (!urlStream.trim().equals("null")) {
                                            new Requests(this, TaskType.D_OPTIONS).execute(parser.getInicioUrl(normal, context) + "?url=" + urlStream);
                                        } else {
                                            Toaster.toast("Error en servidor");
                                            recreate();
                                        }
                                    } else {
                                        Log.d("Directorio", "Actualizado");
                                        frun = false;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toaster.toast("Error al actualizar directorio");
                                recreate();
                            }
                        }
                    } else {
                        if (isJSONValid(infile)) {
                            if (!frun) {
                                String urlStream = parser.getUrlCached(aids[posT], numeros[posT]);
                                if (!urlStream.trim().equals("null")) {
                                    new Requests(this, TaskType.D_OPTIONS).execute(parser.getInicioUrl(normal, context) + "?url=" + urlStream);
                                } else {
                                    Toaster.toast("Error en servidor");
                                    recreate();
                                }
                            } else {
                                Log.d("Directorio", "Actualizado");
                                frun = false;
                            }
                        } else {
                            Toaster.toast("Error al actualizar directorio");
                            recreate();
                        }
                    }
                }
            }
        }
        if (taskType == TaskType.ACT_DIR_S) {
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
                        String urlDes = parser.getUrlCached(aids[posT], numeros[posT]);
                        if (!urlDes.equals("null")) {
                            new Requests(this, TaskType.S_OPTIONS).execute(parser.getInicioUrl(normal, context) + "?url=" + urlDes);
                        } else {
                            Toaster.toast("Error en servidor");
                            recreate();
                        }
                    } else {
                        Toaster.toast("Error al actualizar directorio");
                        recreate();
                    }
                } else {
                    String infile = getStringFromFile(file_loc);
                    if (!infile.trim().equals(trimed)) {
                        if (isJSONValid(infile)) {
                            writeToFile(trimed, file);
                            String urlDes = parser.getUrlCached(aids[posT], numeros[posT]);
                            if (!urlDes.equals("null")) {
                                new Requests(this, TaskType.S_OPTIONS).execute(parser.getInicioUrl(normal, context) + "?url=" + urlDes);
                            } else {
                                Toaster.toast("Error en servidor");
                                recreate();
                            }
                        } else {
                            Toaster.toast("Error al actualizar directorio");
                            recreate();
                        }
                    } else {
                        if (isJSONValid(infile)) {
                            String urlDes = parser.getUrlCached(aids[posT], numeros[posT]);
                            if (!urlDes.equals("null")) {
                                new Requests(this, TaskType.S_OPTIONS).execute(parser.getInicioUrl(normal, context) + "?url=" + urlDes);
                            } else {
                                Toaster.toast("Error en servidor");
                                recreate();
                            }
                        } else {
                            Toaster.toast("Error al actualizar directorio");
                            recreate();
                        }
                    }
                }
            }
        }
        if (taskType == TaskType.DIRECTORIO1) {
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
                    String infile = getStringFromFile(file_loc);
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
                //new DirGetter(context, TaskType.DIRECTORIO1).execute(getDirectorioSec());
                loadSecDir(true);
            }
        }
    }

    @Override
    public void sendtext1(final String data, TaskType taskType) {
        if (taskType == TaskType.VERSION) {
            String vers = "";
            if (!isNetworkAvailable() || data.trim().equals("error")) {
                vers = Integer.toString(versionCode);
            } else {
                if (isNumeric(data.trim())) {
                    vers = data;
                } else {
                    vers = "0";
                    mensaje = data.split(":::");
                }
            }
            Log.d("Version", Integer.toString(versionCode) + " >> " + vers.trim());
            if (versionCode >= Integer.parseInt(vers.trim())) {
                if (Integer.parseInt(vers.trim()) == 0) {
                    if (!disM) {
                        MaterialDialog dialog = new MaterialDialog.Builder(this)
                                .title(mensaje[0])
                                .content(mensaje[1])
                                .titleColorRes(R.color.prim)
                                .autoDismiss(Boolean.valueOf(mensaje[2].trim()))
                                .cancelable(Boolean.valueOf(mensaje[3].trim()))
                                .backgroundColor(Color.WHITE)
                                .titleGravity(GravityEnum.CENTER)
                                .positiveText(mensaje[4])
                                .positiveColorRes(R.color.prim)
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        if (mensaje[4].trim().toLowerCase().equals("salir")) {
                                            finish();
                                        }
                                        if (mensaje[4].trim().toLowerCase().equals("cerrar")) {
                                            disM = true;
                                            dialog.dismiss();
                                        }
                                        if (!mensaje[4].trim().toLowerCase().equals("salir") || !mensaje[4].trim().toLowerCase().equals("cerrar")) {
                                            if (mensaje[5].trim().equals("toast")) {
                                                toast(mensaje[6].trim());
                                            }
                                            if (mensaje[5].trim().equals("toast&notshow")) {
                                                toast(mensaje[6].trim());
                                                disM = true;
                                            }
                                            if (mensaje[5].trim().equals("finish")) {
                                                finish();
                                            }
                                            if (mensaje[5].trim().equals("dismiss")) {
                                                dialog.dismiss();
                                            }
                                            if (mensaje[5].trim().equals("dismiss&notshow")) {
                                                disM = true;
                                                dialog.dismiss();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        finish();
                                    }
                                }).build();
                        dialog.show();
                    }
                } else {
                    Log.d("Version", "OK");
                    verOk = true;
                    getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("notVer", false);
                }
            } else {
                if (showact) {
                    showact = false;
                    Log.d("Version", "Actualizar");
                    verOk = false;
                    dialog = new MaterialDialog.Builder(this)
                            .title("Nueva Version " + vers.trim())
                            .customView(R.layout.text_d_act, false)
                            .titleColorRes(R.color.prim)
                            .autoDismiss(false)
                            .cancelable(false)
                            .backgroundColor(Color.WHITE)
                            .titleGravity(GravityEnum.CENTER)
                            .positiveText("Actualizar")
                            .positiveColorRes(R.color.prim)
                            .negativeText("Salir")
                            .negativeColorRes(R.color.prim)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(final MaterialDialog dialog) {
                                    if (!version) {
                                        version = true;
                                        if (descarga.exists()) {
                                            descarga.delete();
                                        }
                                        final ThinDownloadManager downloadManager = new ThinDownloadManager();
                                        Uri download = Uri.parse("https://github.com/jordyamc/Animeflv/blob/master/app/app-release.apk?raw=true");
                                        final DownloadRequest downloadRequest = new DownloadRequest(download)
                                                .setDestinationURI(Uri.fromFile(descarga))
                                                .setStatusListener(new DownloadStatusListenerV1() {
                                                    @Override
                                                    public void onDownloadComplete(DownloadRequest downloadRequest) {
                                                        Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                                                                .setDataAndType(Uri.fromFile(descarga),
                                                                        "application/vnd.android.package-archive");
                                                        dialog.dismiss();
                                                        finish();
                                                        startActivity(promptInstall);
                                                    }

                                                    @Override
                                                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                                                        toast(errorMessage);
                                                        dialog.dismiss();
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
                                                        //textView.setText(Integer.toString(progress) + "%");
                                                    }
                                                });
                                        actdown = downloadManager.add(downloadRequest);
                                    }
                                }

                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    dialog.dismiss();
                                    finish();
                                }
                            }).build();
                    TextView textView = (TextView) dialog.getCustomView().findViewById(R.id.tv_dialog);
                    textView.setText("Esta version (" + versionCode + ") es obsoleta, porfavor actualiza para continuar.");
                    dialog.show();
                }
            }
        }
        if (taskType == TaskType.D_OPTIONS) {
            if (isJSONValid(data.trim())) {
                try {
                    JSONObject jsonObject = new JSONObject(data.trim());
                    JSONArray jsonArray = jsonObject.getJSONArray("downloads");
                    final List<String> nombres = new ArrayList<>();
                    final List<String> urls = new ArrayList<>();
                    try {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String u = object.getString("url");
                            if (!u.trim().equals("null")) {
                                nombres.add(object.getString("name"));
                                urls.add(u);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    d = new MaterialDialog.Builder(context)
                            .title("Opciones")
                            .titleGravity(GravityEnum.CENTER)
                            .customView(R.layout.dialog_down, false)
                            .cancelable(true)
                            .autoDismiss(false)
                            .positiveText("Descargar")
                            .negativeText("Cancelar")
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    String des = nombres.get(sp.getSelectedItemPosition());
                                    final String ur = urls.get(sp.getSelectedItemPosition());
                                    Log.d("Descargar", "URL -> " + ur);
                                    switch (des.toLowerCase()) {
                                        case "izanagi":
                                            new Izanagi().execute(ur);
                                            d.dismiss();
                                            break;
                                        case "zippyshare":
                                            web.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    web.loadUrl(ur);
                                                }
                                            });
                                            d.dismiss();
                                            break;
                                        case "mega":
                                            d.dismiss();
                                            CancelPreDown();
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ur)));
                                            break;
                                        default:
                                            chooseDownDir(posT, ur);
                                            d.dismiss();
                                            break;
                                    }
                                }

                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    super.onNegative(dialog);
                                    d.dismiss();
                                    CancelPreDown();
                                }
                            })
                            .cancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    d.dismiss();
                                    CancelPreDown();
                                }
                            })
                            .build();
                    sp = (Spinner) d.getCustomView().findViewById(R.id.spinner_down);
                    sp.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, nombres));
                    d.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Data", data);
                    Toaster.toast("Error en JSON");
                    CancelPreDown();
                }
            } else {
                Log.d("Data", data);
                Toaster.toast("Error en JSON");
                CancelPreDown();
            }
        }
        if (taskType == TaskType.S_OPTIONS) {
            if (isJSONValid(data.trim())) {
                try {
                    JSONObject jsonObject = new JSONObject(data.trim());
                    JSONArray jsonArray = jsonObject.getJSONArray("downloads");
                    final List<String> nombres = new ArrayList<>();
                    final List<String> urls = new ArrayList<>();
                    try {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String u = object.getString("url");
                            if (!u.trim().equals("null")) {
                                nombres.add(object.getString("name"));
                                urls.add(u);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    d = new MaterialDialog.Builder(context)
                            .title("Opciones")
                            .titleGravity(GravityEnum.CENTER)
                            .customView(R.layout.dialog_down, false)
                            .cancelable(true)
                            .autoDismiss(false)
                            .positiveText("Ver")
                            .negativeText("Cancelar")
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    String des = nombres.get(sp.getSelectedItemPosition());
                                    final String ur = urls.get(sp.getSelectedItemPosition());
                                    Log.d("Streaming", "URL -> " + ur);
                                    switch (des.toLowerCase()) {
                                        case "izanagi":
                                            new IzanagiStream().execute(ur);
                                            d.dismiss();
                                            break;
                                        case "zippyshare":
                                            web.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Streaming = true;
                                                    web.loadUrl(ur);
                                                }
                                            });
                                            d.dismiss();
                                            break;
                                        case "mega":
                                            d.dismiss();
                                            CancelPreDown();
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ur)));
                                            break;
                                        default:
                                            StreamInbyURL(posT, ur);
                                            d.dismiss();
                                            break;
                                    }
                                }

                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    super.onNegative(dialog);
                                    d.dismiss();
                                    CancelPreDown();
                                }
                            })
                            .cancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    d.dismiss();
                                    CancelPreDown();
                                }
                            })
                            .build();
                    sp = (Spinner) d.getCustomView().findViewById(R.id.spinner_down);
                    sp.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, nombres));
                    d.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Data", data);
                    Toaster.toast("Error en JSON");
                    CancelPreDown();
                }
            } else {
                Log.d("Data", data);
                Toaster.toast("Error en JSON");
                CancelPreDown();
            }
        }
        if (taskType == TaskType.GET_INICIO) {
            loadInicio(data);
        }
        if (taskType == TaskType.GET_HTML1) {
            web_Links.loadUrl("about:blank");
            web_Links.loadData(data, "text/html", "UTF-8");
        }
        if (taskType == TaskType.GET_INFO) {
            if (isJSONValid(data.trim())) {
                actCacheInfo(data);
            } else {
                if (data.trim().equals("error")) {
                    web.loadUrl(urlInfoT);
                }
            }
        }
        if (taskType == TaskType.GET_HTML2) {
            int a = Integer.parseInt(data.substring(data.indexOf("document.getElementById('lang-one').a = ") + 40, data.indexOf("document.getElementById('lang-one').a = ") + 46).trim());
            int b = 1234567;
            String code = Integer.toString((((a + 3) * 3) % b) + 3);
            Log.d("Int a", Integer.toString(a));
            Log.d("Int b", Integer.toString(b));
            Log.d("code", code);
            String durl = data.substring(data.indexOf("document.getElementById('dlbutton').href = ") + 45, data.indexOf(";", data.indexOf("document.getElementById('dlbutton').href = ") + 45) - 1);
            durl = durl.replace("\"+e()+\"", code);
            String url = getSharedPreferences("data", MODE_PRIVATE).getString("urlD", null);
            String furl = "http://" + url.substring(url.indexOf("www"), url.indexOf(".", url.indexOf("www"))) + ".zippyshare.com/" + durl;
            Log.d("Final D Link", furl);
        }
        if (taskType == TaskType.GET_FAV) {
            if (isJSONValid(data)) {
                String favoritos = parser.getUserFavs(data.trim());
                String visto = parser.getUserVistos(data.trim());
                if (visto.equals("")) {
                    String favs = getSharedPreferences("data", MODE_PRIVATE).getString("favoritos", "");
                    if (!favs.equals(favoritos)) {
                        getSharedPreferences("data", MODE_PRIVATE).edit().putString("favoritos", favoritos).commit();
                        //new Requests(context, TaskType.GET_INICIO).execute(getInicio());
                        loadMainJson();
                    }
                } else {
                    String favs = getSharedPreferences("data", MODE_PRIVATE).getString("favoritos", "");
                    if (!favs.equals(favoritos)) {
                        getSharedPreferences("data", MODE_PRIVATE).edit().putString("favoritos", favoritos).commit();
                        //new Requests(context, TaskType.GET_INICIO).execute(getInicio());
                        loadMainJson();
                    }
                    String vistos = getSharedPreferences("data", MODE_PRIVATE).getString("vistos", "");
                    try {
                        if (!vistos.equals(visto)) {
                            getSharedPreferences("data", MODE_PRIVATE).edit().putString("vistos", visto).commit();
                            String[] v = visto.split(";;;");
                            for (String s : v) {
                                getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean(s, true).apply();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (taskType == TaskType.APP_BAN) {
            if (data.contains(":::")) {
                String[] values = data.split(":::");
                if (values[0].trim().equals("ban")) {
                    toast("Has sido baneado de la app :(");
                    getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("appBanned", true).apply();
                    String l = getSharedPreferences("data", MODE_PRIVATE).getString(eidT, "0");
                    DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    downloadManager.remove(Long.parseLong(l));
                    new File(mediaStorage, "inicio.txt").delete();
                    finish();
                } else if (values[0].trim().equals("ok")) {
                    if (getSharedPreferences("data", MODE_PRIVATE).getBoolean("appBanned", false))
                        toast("Tu Ban ah sido retirado :D");
                    getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("appBanned", false).apply();
                }
            }
        }
        if (taskType == TaskType.CHAT_BAN) {
            if (data.contains(":::")) {
                String[] values = data.split(":::");
                if (values[1].trim().equals("ban")) {
                    toast("Has sido baneado del chat :(");
                    getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("chatBanned", true).apply();
                } else if (values[1].trim().equals("ok")) {
                    if (getSharedPreferences("data", MODE_PRIVATE).getBoolean("chatBanned", false))
                        toast("Tu Ban ah sido retirado :D");
                    getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("chatBanned", false).apply();
                    startActivity(new Intent(context, Chat.class));
                }
            } else {
                Log.d("error", data);
            }
        }
        if (taskType == TaskType.FEEDBACK) {
            if (!data.trim().toLowerCase().equals("error")) {
                if (data.trim().toLowerCase().equals("ok")) {
                    toast("Sugerencia enviada");
                    mat.dismiss();
                }
            } else {
                toast("Error, porfavor intentalo de nuevo");
                mat.dismiss();
            }
        }
    }

    @TargetApi(23)
    public void checkPermission(final String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Dexter.checkPermission(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse response) {
                    if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                    } else if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        checkPermission(Manifest.permission.GET_ACCOUNTS);
                    }
                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse response) {
                    if (!response.isPermanentlyDenied()) {
                        String titulo;
                        String desc;
                        if (response.getPermissionName().equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) || response.getPermissionName().equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            titulo = "Leer/Escribir archivos";
                            desc = "Este permiso es necesario para descargar los animes, asi como para funcionar sin conexion";
                        } else {
                            titulo = "Obtener cuentas";
                            desc = "Este permiso es necesario para obtener tu correo en las sugerencias y sincronixar favoritos";
                        }
                        new MaterialDialog.Builder(context)
                                .title(titulo)
                                .content(desc)
                                .positiveText("ACEPTAR")
                                .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                                .cancelable(false)
                                .autoDismiss(true)
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        super.onPositive(dialog);
                                        checkPermission(permission);
                                    }
                                })
                                .build().show();
                    } else {
                        toast("El permiso es necesario, por favor activalo");
                        finish();
                        Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        i.addCategory(Intent.CATEGORY_DEFAULT);
                        i.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(i);
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
            }, permission);
        }
    }

    public boolean isJSONValid(String test) {
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

    @Override
    protected void onResume() {
        super.onResume();
        CookieSyncManager.getInstance().stopSync();
        pause = false;
        getSharedPreferences("data", MODE_PRIVATE).edit().putInt("nCaps", 0).apply();
        ActualizarFavoritos();
        if (shouldExecuteOnResume) {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("is_amoled", false) != isAmoled) {
                recreate();
            }
            if (isNetworkAvailable()) {
                //getSharedPreferences("data",MODE_PRIVATE).edit().putInt("nCaps",0).apply();
                textoff.setVisibility(View.GONE);
                checkBan(APP);
                new Requests(context, TaskType.VERSION).execute("https://raw.githubusercontent.com/jordyamc/Animeflv/master/app/version.html");
                //new Requests(this, TaskType.GET_INICIO).execute(getInicio());
                loadMainJson();
            } else {
                textoff.setVisibility(View.VISIBLE);
                textoff.setText("MODO OFFLINE");
                if (mswipe.isRefreshing()) {
                    mswipe.setRefreshing(false);
                }
            }
            NotificationManager notificationManager = (NotificationManager) this
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(6991);
        } else {
            shouldExecuteOnResume = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CookieSyncManager.getInstance().sync();
        pause = true;
    }

    @Override
    public void onBackPressed() {
        if (!result.isDrawerOpen()) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
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
            result.closeDrawer();
        }
    }

    @Override
    public void response(String data, TaskType taskType) {
        if (taskType == TaskType.GET_FAV) {
            if (data.equals("OK")) {
                Log.d("Login", "Actualizando favoritos");
                //new Requests(this, TaskType.GET_INICIO).execute(getInicio());
                loadMainJson();
            }
        }
    }

    public void chooseDownDir(int position, String url) {
        Boolean inSD = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sd_down", false);
        if (inSD) {
            DescargarSD(position, url);
        } else {
            DescargarInbyURL(position, url);
        }
    }

    public void chooseDownDir(int position, String url, CookieConstructor constructor) {
        Boolean inSD = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sd_down", false);
        if (inSD) {
            DescargarSD(position, url, constructor);
        } else {
            DescargarInbyURL(position, url, constructor);
        }
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("accentColor", selectedColor).apply();
        recreate();
    }

    class JavaScriptInterface {
        private Context ctx;

        JavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }

        @JavascriptInterface
        public void showHTML(String html) {
            String s_html_i = html.substring(21);
            String s_html_f = "{" + s_html_i.substring(0, s_html_i.length() - 7);
            json = s_html_f;
            getData(s_html_f);
        }

        @JavascriptInterface
        public void showHTMLD1(String html) {
            String replace = html.replace("\\/", "/");
            String cortado;
            if (!replace.trim().contains("Error 404") && replace.contains("zippyshare")) {
                cortado = replace.substring(replace.indexOf("&proxy.link=") + 12);
                cortado = cortado.substring(0, cortado.indexOf("file.html") + 9).trim();
                getSharedPreferences("data", MODE_PRIVATE).edit().putString("urlD", cortado).apply();
                getSharedPreferences("data", MODE_PRIVATE).edit().putInt("sov", 1).apply();
            } else {
                cortado = "http://error-al-descargar.com";
            }
            final String finalstring = cortado;
            web.post(new Runnable() {
                @Override
                public void run() {
                    web.loadUrl(finalstring);
                }
            });
            //new Requests(context,TaskType.GET_HTML2).execute(cortado);
            //Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(cortado));
            //startActivity(intent);

            //final String link=html.substring(html.indexOf(".link=")+6,html.lastIndexOf(".html")+5);
            //Log.d("link",link);
        }


        @JavascriptInterface
        public void showHTMLD2(final String htmlInicio) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isJSONValid(htmlInicio)) {
                        loadInicio(htmlInicio.trim());
                    } else {

                        if (htmlInicio.contains("<title>Anime Online - AnimeFLV</title>")) {
                            try {
                                new Requests(context, TaskType.GET_INICIO).execute(parser.getInicioUrl(normal, context) + "?certificate=" + getCertificateSHA1Fingerprint() + "&data=" + URLEncoder.encode(htmlInicio, "UTF-8"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            web.loadUrl("http://animeflv.net");
                        }
                    }
                }
            });
        }

        @JavascriptInterface
        public void HTMLInfo(final String htmlInfo) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isJSONValid(htmlInfo)) {
                        actCacheInfo(htmlInfo.trim());
                        toast("Cargando Info en modo alternativo");
                    } else {
                        web.loadUrl(urlInfoT);
                    }
                }
            });
        }
    }

    public class Izanagi extends AsyncTask<String, String, String> {
        String _response;

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection c = null;
            try {
                URL u = new URL(params[0]);
                c = (HttpURLConnection) u.openConnection();
                c.setRequestProperty("Content-length", "0");
                c.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
                c.setRequestProperty("Accept", "*/*");
                c.setInstanceFollowRedirects(true);
                c.setUseCaches(false);
                c.setConnectTimeout(10000);
                c.setAllowUserInteraction(false);
                c.connect();
                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                //c.disconnect();
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                _response = sb.toString();
                //String fullPage = page.asXml();
            } catch (Exception e) {
                Log.e("Requests", "Error in http connection " + e.toString());
                _response = "error";
            }
            return _response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String furl = s.substring(s.indexOf("URL=") + 4, s.lastIndexOf("\">"));
            chooseDownDir(posT, furl);
        }
    }

    public class IzanagiStream extends AsyncTask<String, String, String> {
        String _response;

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection c = null;
            try {
                URL u = new URL(params[0]);
                c = (HttpURLConnection) u.openConnection();
                c.setRequestProperty("Content-length", "0");
                c.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
                c.setRequestProperty("Accept", "*/*");
                c.setInstanceFollowRedirects(true);
                c.setUseCaches(false);
                c.setConnectTimeout(10000);
                c.setAllowUserInteraction(false);
                c.connect();
                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                //c.disconnect();
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                _response = sb.toString();
                //String fullPage = page.asXml();
            } catch (Exception e) {
                Log.e("Requests", "Error in http connection " + e.toString());
                _response = "error";
            }
            return _response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String furl = s.substring(s.indexOf("URL=") + 4, s.lastIndexOf("\">"));
            StreamInbyURL(posT, furl);
        }
    }

    public class JSONCreatorAsync extends AsyncTask<String, String, String> {
        String _response;
        JsonCreator creator = new JsonCreator();
        JSONType type;

        public JSONCreatorAsync(JSONType tipo) {
            this.type = tipo;
        }

        @Override
        protected String doInBackground(@Nullable String... params) {
            if (type == JSONType.INICIO) {
                _response = creator.getJSONinicio();
            } else {
                Log.d("NADA", "NADA");
            }
            return _response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            toast(_response);
        }
    }
}