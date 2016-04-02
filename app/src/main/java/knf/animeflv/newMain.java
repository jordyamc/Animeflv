package knf.animeflv;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
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
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.melnykov.fab.FloatingActionButton;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import knf.animeflv.Directorio.Directorio;
import knf.animeflv.Emision.EmisionChecker;
import knf.animeflv.Emision.Section.newEmisionActivity;
import knf.animeflv.Interfaces.MainRecyclerCallbacks;
import knf.animeflv.Recientes.MainOrganizer;
import knf.animeflv.Recientes.Status;
import knf.animeflv.Recyclers.AdapterMain;
import knf.animeflv.Recyclers.AdapterMainNoGIF;
import knf.animeflv.Utils.MainStates;
import knf.animeflv.Utils.NetworkUtils;
import knf.animeflv.WaitList.WaitActivity;
import xdroid.toaster.Toaster;

public class newMain extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        LoginServer.callback,
        DirGetter.callback,
        ColorChooserDialog.ColorCallback,
        MainRecyclerCallbacks {
    private Spinner etEmail;
    private EditText etSug;
    private EditText cuenta;
    private Spinner contactoS;
    private WebView webViewFeed;
    private boolean cancelPost;

    private boolean tbool;
    private boolean isAmoled;

    private boolean doubleBackToExitPressedOnce = false;

    private boolean verOk;

    private String ext_storage_state = Environment.getExternalStorageState();
    private File mediaStorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache");

    private RecyclerView recyclerView;
    private LinearLayout root;
    private Toolbar toolbar;
    private SwipeRefreshLayout mswipe;
    private int versionCode;
    private String versionName;
    private String androidID;
    private AccountHeader headerResult;
    private Drawer result;
    private MaterialDialog mat;
    private String headerTit;
    private Context context;

    private Snackbar waiting;

    private FloatingActionButton actionButton;
    private com.github.clans.fab.FloatingActionButton updateButton;

    private Parser parser = new Parser();

    private int intentos;

    private boolean shouldExecuteOnResume;

    private TaskType normal = TaskType.NORMAL;
    private TaskType secundario = TaskType.SECUNDARIA;

    private MaterialDialog RapConf;

    private boolean frun = false;

    private AdapterMain main;
    private AdapterMainNoGIF mainNo;

    private Switch nots;
    private Spinner sonidos;
    private Spinner conexion;
    private Spinner repVid;
    private Spinner repStream;

    private String[] eids;
    private String[] tipos;

    private Handler EmisionHandler = new Handler();
    Runnable EmisionWaiting = new Runnable() {
        @Override
        public void run() {
            if (!MainStates.isLoadingEmision()) {
                getWaitingSnackBar().dismiss();
                EmisionHandler.removeCallbacks(EmisionWaiting);
                getWaitingSnackBar().dismiss();
                startActivity(new Intent(context, newEmisionActivity.class));
            } else {
                EmisionHandler.postDelayed(EmisionWaiting, 500);
            }
        }
    };

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

    private static String getStringFromFile(String filePath) {
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anime_main);
        setUpMain();
    }

    private void setUpMain() {
        if (!getSharedPreferences("data", MODE_PRIVATE).getBoolean("intro", false)) {
            startActivity(new Intent(this, Intro.class));
        }
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        MainRegistrer.init();
        setUpVersion();
        setUpViews();
        setUpAmoled();
        setUpDrawer();
        getJson();
        NetworkUtils.checkVersion(this, updateButton);
        Application application = (Application) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Recientes");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void setUpDrawer() {
        Drawable ic_main;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("is_amoled", false)) {
            ic_main = getResources().getDrawable(R.mipmap.ic_launcher_dark);
        } else {
            ic_main = getResources().getDrawable(R.mipmap.ic_launcher);
        }
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(getHDraw(false))
                .withCompactStyle(true)
                .withSelectionListEnabled(false)
                .addProfiles(
                        new ProfileDrawerItem().withName(headerTit).withEmail("Versión " + versionName + " (" + Integer.toString(versionCode) + ")").withIcon(ic_main).withIdentifier(9)
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
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Recientes").withIcon(FontAwesome.Icon.faw_home).withIdentifier(0),
                        new PrimaryDrawerItem().withName("Favoritos").withIcon(/*GoogleMaterial.Icon.gmd_star*/MaterialDesignIconic.Icon.gmi_star).withIdentifier(1),
                        new PrimaryDrawerItem().withName("Directorio").withIcon(MaterialDesignIconic.Icon.gmi_view_list_alt).withIdentifier(2),
                        new PrimaryDrawerItem().withName("Emision").withIcon(MaterialDesignIconic.Icon.gmi_alarm_check).withIdentifier(3),
                        //new PrimaryDrawerItem().withName("Descargas").withIcon(GoogleMaterial.Icon.gmd_file_download).withIdentifier(3),
                        new PrimaryDrawerItem().withName("Lista").withIcon(MaterialDesignIconic.Icon.gmi_assignment_returned).withIdentifier(4),
                        new PrimaryDrawerItem().withName("Sugerencias").withIcon(MaterialDesignIconic.Icon.gmi_assignment).withIdentifier(5),
                        new PrimaryDrawerItem().withName("Pagina Oficial").withIcon(FontAwesome.Icon.faw_facebook).withIdentifier(6),
                        //new PrimaryDrawerItem().withName("Chat").withIcon(GoogleMaterial.Icon.gmd_message).withIdentifier(6),
                        new PrimaryDrawerItem().withName("Web Oficial").withIcon(MaterialDesignIconic.Icon.gmi_view_web).withIdentifier(7),
                        new PrimaryDrawerItem().withName("Publicidad").withIcon(MaterialDesignIconic.Icon.gmi_cloud).withIdentifier(8)
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
                                if (MainStates.isLoadingEmision()) {
                                    getWaitingSnackBar().show();
                                    EmisionWaiting.run();
                                } else {
                                    startActivity(new Intent(context, newEmisionActivity.class));
                                }
                                result.setSelection(0);
                                result.closeDrawer();
                                break;
                            case 5:
                                startActivity(new Intent(context, WaitActivity.class));
                                result.setSelection(0);
                                result.closeDrawer();
                                break;
                            /*case 4:
                                result.setSelection(0);
                                Intent intent2 = new Intent(context, Descargas.class);
                                startActivity(intent2);
                                break;*/
                            case 6://5
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
                            case 7://6
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
                            case 8://8
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(parser.getBaseUrl(TaskType.NORMAL, context))));
                                result.setSelection(0);
                                result.closeDrawer();
                                break;
                            case 9://9
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

    private void cambiarColor() {
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

    private boolean isXLargeScreen() {
        return (getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private int getHDraw(final Boolean set) {
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
            Drawable ic_main;
            SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(this);
            if (preferences1.getBoolean("is_amoled", false)) {
                ic_main = getResources().getDrawable(R.mipmap.ic_launcher_dark);
            } else {
                ic_main = getResources().getDrawable(R.mipmap.ic_launcher);
            }
            ArrayList<IProfile> profile = new ArrayList<>();
            profile.add(new ProfileDrawerItem().withName(headerTit).withEmail("Versión " + versionName + " (" + Integer.toString(versionCode) + ")").withIcon(ic_main).withIdentifier(9));
            headerResult.setBackgroundRes(drawable);
            headerResult.setProfiles(profile);
        }
        return drawable;
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
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
        }
    }

    private void setUpViews() {
        if (!isXLargeScreen()) { //Portrait
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            toolbar = (Toolbar) findViewById(R.id.ltoolbar);
        }

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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int accent = preferences.getInt("accentColor", ColorsRes.Naranja(context));
        int color = ColorsRes.Naranja(context);
        if (accent == ColorsRes.Rojo(context)) {
            color = ColorsRes.Rojo(context);
        }
        if (accent == ColorsRes.Naranja(context)) {
            color = ColorsRes.Naranja(context);
        }
        if (accent == ColorsRes.Gris(context)) {
            color = ColorsRes.Gris(context);
        }
        if (accent == ColorsRes.Verde(context)) {
            color = ColorsRes.Verde(context);
        }
        if (accent == ColorsRes.Rosa(context)) {
            color = ColorsRes.Rosa(context);
        }
        if (accent == ColorsRes.Morado(context)) {
            color = ColorsRes.Morado(context);
        }
        return color;
    }

    private void setTheme() {
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
    }

    private void setUpVersion() {
        context = this;
        androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Registrer().execute();
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

    public void loadMainDir(final boolean search) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setResponseTimeout(-1);
        asyncHttpClient.get(getDirectorio() + "?certificate=" + parser.getCertificateSHA1Fingerprint(context), null, new JsonHttpResponseHandler() {
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
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
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

    public String getInicioSec() {
        return parser.getInicioUrl(secundario, context);
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
            verOk = false;
            if (file.exists()) {
                String infile = getStringFromFile(file_loc);
                getData(infile);
            } else {
                toast("No hay datos guardados");
            }
        }
    }

    public void getData(String json) {
        eids = parser.parseEID(json);
        tipos = parser.parseTipos(json);
        EmisionChecker.Refresh();
        Status.reload(this);
        if (Status.getCacheStatusInt() == 1) {
            getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isDown", false).apply();
            if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("statusShown", false)) {
                actionButton.hide(true);
                actionButton.setVisibility(View.VISIBLE);
                actionButton.show();
            }
        } else {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("statusShown", false).apply();
            getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isDown", false).apply();
            if (actionButton.isVisible()) {
                actionButton.hide();
                actionButton.setVisibility(View.GONE);
            } else {
                actionButton.setVisibility(View.GONE);
            }
        }
        ActualizarFavoritos();
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("noGif", true)) {
            if (mainNo == null) {
                recreate();
            } else {
                mainNo.setData(MainOrganizer.init(json).list());
            }
        } else {
            if (main == null) {
                recreate();
            } else {
                main.setData(MainOrganizer.init(json).list());
            }
        }
        mswipe.setRefreshing(false);
        isFirst();
        getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isF", false).apply();
    }

    public void ActualizarFavoritos() {
        if (isNetworkAvailable()) {
            String email_coded = PreferenceManager.getDefaultSharedPreferences(this).getString("login_email_coded", "null");
            String pass_coded = PreferenceManager.getDefaultSharedPreferences(this).getString("login_pass_coded", "null");
            if (!email_coded.equals("null") && !pass_coded.equals("null")) {
                new FavLoader().execute(parser.getBaseUrl(normal, context) + "fav-server.php?certificate=" + parser.getCertificateSHA1Fingerprint(context) + "&tipo=get&email_coded=" + email_coded + "&pass_coded=" + pass_coded);
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

    public void loadInicio(String da) {
        String data = da.replace("<!-- Hosting24 Analytics Code -->\n" +
                "                                                         <script type=\"text/javascript\" src=\"http://stats.hosting24.com/count.php\"></script>\n" +
                "                                                         <!-- End Of Analytics Code -->", "").trim();
        Boolean isF = getSharedPreferences("data", MODE_PRIVATE).getBoolean("isF", true);
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
                                getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isDown", true).apply();
                            } else {
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
                                        getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isDown", true).apply();
                                    } else {
                                        getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isDown", false).apply();
                                    }
                                    getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("online", true);
                                    getData(data);
                                    intentos = 0;
                                } else {
                                    Log.d("Cargar 1", "Json existente");
                                    if (parser.checkStatus(data) == 1) {
                                        getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isDown", true).apply();
                                    } else {
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
                                        getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isDown", true).apply();
                                    } else {
                                        getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isDown", false).apply();
                                    }
                                    getData(infile);
                                } else {
                                    file.delete();
                                    toast("Error en cache, sin conexion");
                                    getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("online", false);
                                }
                            } else {
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
                                loadSecJson();
                                intentos++;
                            } else {
                                toast("Error en servidor, sin cache para mostrar");
                                Log.d("Archivo 3", "Existe");
                                String infile = getStringFromFile(file_loc);
                                toast("Cargando desde cache");
                                Log.d("Cargar 3", "Json existente");
                                if (parser.checkStatus(data) == 1) {
                                    getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isDown", true).apply();
                                } else {
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
                                getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("isDown", true).apply();
                            } else {
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
                        for (String son : getResources().getStringArray(R.array.sonidos)) {
                            sonido.add(son);
                        }
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

    public void loadMainJson() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setResponseTimeout(10000);
        asyncHttpClient.get(getInicio() + "?certificate=" + parser.getCertificateSHA1Fingerprint(context), null, new JsonHttpResponseHandler() {
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
        asyncHttpClient.get(getInicioSec() + "?certificate=" + parser.getCertificateSHA1Fingerprint(context), null, new JsonHttpResponseHandler() {
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

    private Snackbar getWaitingSnackBar() {
        if (waiting == null) {
            waiting = Snackbar.make(root, "Cargando Lista", Snackbar.LENGTH_INDEFINITE);
            return waiting;
        } else {
            return waiting;
        }
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("accentColor", selectedColor).apply();
        recreate();
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
        getSharedPreferences("data", MODE_PRIVATE).edit().putInt("nCaps", 0).apply();
        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putStringSet("eidsNot", new HashSet<String>()).apply();
        parser.refreshUrls(context);
        ActualizarFavoritos();
        if (shouldExecuteOnResume) {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("is_amoled", false) != isAmoled) {
                recreate();
            }
            if (isNetworkAvailable()) {
                //checkBan(APP);
                //new Requests(context, TaskType.VERSION).execute("https://raw.githubusercontent.com/jordyamc/Animeflv/master/app/version.html");
                loadMainJson();
            } else {
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
        MainStates.setProcessing(false, "destroyed");
        MainStates.setLoadingEmision(false);
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
            getMenuInflater().inflate(R.menu.menu_main_dark, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        setDir(true);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!isXLargeScreen()) {
            return;
        }
    }

    private class Registrer extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            new SyncHttpClient().get(new Parser().getInicioUrl(TaskType.NORMAL, context) + "contador.php?id=" + androidID.trim() + "&version=" + Integer.toString(versionCode), null, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.d("Registrer", throwable.getMessage());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.d("Registrer", "OK");
                }
            });
            return null;
        }
    }

    private class FavLoader extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            new SyncHttpClient().get(params[0], null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    if (isJSONValid(response.toString())) {
                        String favoritos = parser.getUserFavs(response.toString());
                        String visto = parser.getUserVistos(response.toString());
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

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });
            return null;
        }
    }
}
