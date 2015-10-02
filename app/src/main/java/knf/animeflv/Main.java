package knf.animeflv;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;


import knf.animeflv.Directorio.Directorio;
import knf.animeflv.info.Info;
import pl.droidsonroids.gif.GifImageButton;

public class Main extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,Requests.callback {
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
    TextView textoff;
    ArrayList<GifImageButton> IBsDesList=new ArrayList<GifImageButton>();
    ArrayList<ImageButton> IBsVerList=new ArrayList<ImageButton>();
    List<Boolean> isDesc=new ArrayList<Boolean>();
    SwipeRefreshLayout mswipe;
    RecyclerView rv_fav;
    int first = 1;
    String[] eids;
    String[] aids;
    String[] numeros;
    String[] titulos;
    String inicio = "http://animeflv.net/api.php?accion=inicio";
    String json = "{}";
    Alarm alarm = new Alarm();
    String ext_storage_state = Environment.getExternalStorageState();
    File mediaStorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache");
    Parser parser=new Parser();
    String aidInfo;
    String html="<html></html>";
    int versionCode;
    String versionName;

    Drawer result;
    boolean doubleBackToExitPressedOnce = false;
    Toolbar ltoolbar;
    Toolbar Dtoolbar;
    File descarga = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache","Animeflv_Nver.apk");
    SharedPreferences.OnSharedPreferenceChangeListener listener;
    boolean descargando=false;
    GifImageButton GIBT;
    ImageButton IBVT;
    int indexT;
    String eidT;
    boolean shouldExecuteOnResume;
    int esperando=0;
    boolean login=false;
    boolean version=false;
    boolean verOk=false;
    String[] mensaje;
    boolean disM=false;
    boolean pause=false;
    int actdown;
    int Tindex;
    Spinner etEmail;
    EditText etSug;
    EditText cuenta;
    WebView webViewFeed;
    MaterialDialog mat;
    Boolean cancelPost=false;
    Boolean showact=true;
    Spinner contactoS;
    AccountHeader headerResult;
    String headerTit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anime_inicio);
        context=this;
        shouldExecuteOnResume = false;
        getSharedPreferences("data",MODE_PRIVATE).edit().putInt("nCaps",0).apply();
        getSharedPreferences("data",MODE_PRIVATE).edit().putBoolean("notVer",false).apply();
        Boolean not= PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notificaciones", true);
        if (not) {
            alarm.SetAlarm(this);
        }
        first=1;
        if (!isXLargeScreen(getApplicationContext())) { //set phones to portrait;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            ltoolbar=(Toolbar)findViewById(R.id.ltoolbar);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.dark));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.prim));
        }
        toolbar=(Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Recientes");
        parser=new Parser();
        setLoad();
        try {versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;} catch (Exception e) {toast("ERROR");}
        try {versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;} catch (Exception e) {toast("ERROR");}
        final int change=getSharedPreferences("data",MODE_PRIVATE).getInt(Integer.toString(versionCode),0);
        int regver = getSharedPreferences("data",MODE_PRIVATE).getInt("reg"+Integer.toString(versionCode),0);
        if (change==0){
            ChangelogDialog.create()
                    .show(getSupportFragmentManager(), "changelog");
            getSharedPreferences("data",MODE_PRIVATE).edit().putInt(Integer.toString(versionCode),1).apply();
        }
        if (isNetworkAvailable()){
            if (regver==0){
                String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                Log.d("Registrar", androidID);
                web.loadUrl("http://necrotic-neganebulus.hol.es/contador.php?id=" + androidID.trim());
            }
        }
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(getHDraw(false))
                .withCompactStyle(true)
                .withSelectionListEnabled(false)
                .addProfiles(
                        new ProfileDrawerItem().withName(headerTit).withEmail("Versión " + versionName).withIcon(getResources().getDrawable(R.mipmap.ic_launcher)).withIdentifier(9)
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
        if (isXLargeScreen(getApplicationContext())){
            Dtoolbar=ltoolbar;
        }else {
            Dtoolbar=toolbar;
        }
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(Dtoolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Recientes").withIcon(FontAwesome.Icon.faw_home).withIdentifier(0),
                        new PrimaryDrawerItem().withName("Favoritos").withIcon(GoogleMaterial.Icon.gmd_star).withIdentifier(1),
                        new PrimaryDrawerItem().withName("Directorio").withIcon(GoogleMaterial.Icon.gmd_library_books).withIdentifier(2),
                        new PrimaryDrawerItem().withName("Sugerencias").withIcon(GoogleMaterial.Icon.gmd_assignment).withIdentifier(3),
                        new PrimaryDrawerItem().withName("Pagina oficial").withIcon(FontAwesome.Icon.faw_facebook_f).withIdentifier(4)
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
                                Intent in=new Intent(context,Favoritos.class);
                                startActivity(in);
                                break;
                            case 3:
                                result.setSelection(0);
                                setDir(false);
                                break;
                            case 4:
                                result.closeDrawer();
                                result.setSelection(0);
                                mat=new MaterialDialog.Builder(context)
                                        .title("Sugerencias")
                                        .titleColor(getResources().getColor(R.color.prim))
                                        .customView(R.layout.feedback,true)
                                        .positiveText("Enviar")
                                        .positiveColor(getResources().getColor(R.color.prim))
                                        .negativeText("Cancelar")
                                        .autoDismiss(false)
                                        .negativeColor(getResources().getColor(R.color.prim))
                                        .callback(new MaterialDialog.ButtonCallback() {
                                            @Override
                                            public void onPositive(MaterialDialog dialog) {
                                                super.onPositive(dialog);
                                                String email=etEmail.getSelectedItem().toString();
                                                String feedback=etSug.getText().toString();
                                                String Scuenta=cuenta.getText().toString();
                                                String type=contactoS.getSelectedItem().toString().toLowerCase().trim();
                                                int tipo;
                                                if (type.equals("email")){
                                                    tipo=0;
                                                }else {
                                                    tipo=1;
                                                }
                                                boolean ok=false;
                                                if (tipo==0){
                                                    ok=true;
                                                }
                                                if (tipo==1){
                                                    ok=!Scuenta.trim().equals("");
                                                }
                                                feedback=feedback
                                                        .replace("&","")
                                                        .replace("á", "&aacute;")
                                                        .replace("é", "&eacute")
                                                        .replace("í","&iacute")
                                                        .replace("ó","&oacute")
                                                        .replace("ú","&uacute")
                                                        .replace(".","")
                                                        .replace(":::","");
                                                if (!type.equals("selecciona")) {
                                                    if (ok) {
                                                        if (!feedback.trim().equals("")) {
                                                            if (isNetworkAvailable()) {
                                                                if (tipo == 0) {
                                                                    webViewFeed.loadUrl("http://necrotic-neganebulus.hol.es/feedback.php?tipo=" + type + "&cuenta=" + email + "&nombre=" + email.toLowerCase() + "&data=" + feedback.replace(" ", "_"));
                                                                } else {
                                                                    if (type.equals("twitter") && !Scuenta.startsWith("@")) {
                                                                        Scuenta = "@" + Scuenta;
                                                                    }
                                                                    webViewFeed.loadUrl("http://necrotic-neganebulus.hol.es/feedback.php?tipo=" + type + "&cuenta=" + Scuenta.replace(" ", "_") + "&nombre=" + email.toLowerCase() + "&data=" + feedback.replace(" ", "_"));
                                                                }
                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        if (!cancelPost) {
                                                                            toast("Esto se esta tardando");
                                                                        }
                                                                    }
                                                                }, 5000);
                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        if (!cancelPost) {
                                                                            webViewFeed.loadUrl("about:blank");
                                                                            toast("Error, porfavor intentalo de nuevo");
                                                                            mat.dismiss();
                                                                        }
                                                                    }
                                                                }, 10000);
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
                                                }else {
                                                    toast("Selecciona medio de contacto");
                                                    if (feedback.trim().equals("")) {
                                                        etSug.setError("Por favor escribe algo");
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onNegative(MaterialDialog dialog) {
                                                super.onPositive(dialog);
                                                cancelPost=true;
                                                mat.dismiss();
                                            }

                                        })
                                        .cancelable(false)
                                        .build();
                                AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
                                Account[] list = manager.getAccounts();
                                etEmail=(Spinner) mat.getCustomView().findViewById(R.id.et_correo);
                                etSug=(EditText) mat.getCustomView().findViewById(R.id.et_sug);
                                cuenta=(EditText) mat.getCustomView().findViewById(R.id.cuenta);
                                cuenta.setTextColor(getResources().getColor(R.color.prim));
                                contactoS=(Spinner) mat.getCustomView().findViewById(R.id.et_contacto);
                                contactoS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        switch (position){
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
                                webViewFeed=(WebView) mat.getCustomView().findViewById(R.id.wv_feedback);
                                webViewFeed.setWebViewClient(new WebViewClient() {
                                    @Override
                                    public void onPageFinished(WebView view, String url) {
                                        if (url.trim().equals("http://necrotic-neganebulus.hol.es/feedback.php?ok=ok")&&!cancelPost){
                                            view.loadUrl("about:blank");
                                            cancelPost=true;
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
                                List<String> emails=new ArrayList<String>();
                                for (Account account:list){
                                    if (account.name.contains("@")&&!emails.contains(account.name)){
                                        Log.d("Agregar",account.name);
                                        emails.add(account.name);
                                    }
                                }
                                String[] mails=new String[emails.size()];
                                emails.toArray(mails);
                                String[] contacto={"Selecciona","Email","Facebook","Twitter"};
                                if (list.length>0) {
                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, mails);
                                    etEmail.setAdapter(arrayAdapter);
                                }
                                ArrayAdapter<String> adapter=new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,contacto);
                                contactoS.setAdapter(adapter);
                                cancelPost=false;
                                mat.show();
                                break;
                            case 5:
                                String facebookUrl = "https://www.facebook.com/animeflv.app.jordy";
                                Uri uri;
                                try {
                                    getPackageManager().getPackageInfo("com.facebook.katana", 0);
                                    uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
                                } catch (PackageManager.NameNotFoundException e) {
                                    uri = Uri.parse(facebookUrl);
                                }
                                startActivity(new Intent(Intent.ACTION_VIEW, uri));
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
        if (isXLargeScreen(getApplicationContext())){
            toolbar.inflateMenu(R.menu.menu_main_dark);
        }else {
            toolbar.inflateMenu(R.menu.menu_main);
        }
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                setDir(true);
                return true;
            }
        });
        if (isNetworkAvailable()){new Requests(context,TaskType.VERSION).execute("https://raw.githubusercontent.com/jordyamc/Animeflv/master/app/version.html");}
        SharedPreferences prefs = this.getSharedPreferences("data", MODE_PRIVATE);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals("reload")&&!pause) {
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
                        new Requests(context, TaskType.GET_INICIO).execute(inicio);
                    }else {
                        if (mswipe.isRefreshing()){mswipe.setRefreshing(false);}
                    }
                    getSharedPreferences("data",MODE_PRIVATE).edit().putInt("nCaps",0).apply();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            NotificationManager notificationManager = (NotificationManager) context
                                    .getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancel(6991);
                        }
                    },200);
                }
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(listener);
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null){
            new Requests(context, TaskType.VERSION).execute("https://raw.githubusercontent.com/jordyamc/Animeflv/master/app/version.html");
        }
        if (descarga.exists()){descarga.delete();}
    }
    public int getHDraw (final Boolean set){
        int color=getSharedPreferences("data", Context.MODE_PRIVATE).getInt("color", 0);
        int drawable=R.drawable.cargando;
        switch (color){
            case 0:
                drawable=R.drawable.naranja;
                headerTit="Animeflv";
                break;
            case 1:
                drawable=R.drawable.amarillo;
                headerTit="Animeflv";
                break;
            case 2:
                drawable=R.drawable.rojo;
                headerTit="Animeflv";
                break;
            case 3:
                drawable=R.drawable.rosa;
                headerTit="Animeflv";
                break;
            case 4:
                drawable=R.drawable.alnek;
                headerTit="";
                break;
        }
                if (set){
                    ArrayList<IProfile> profile=new ArrayList<IProfile>();
                    profile.add(new ProfileDrawerItem().withName(headerTit).withEmail("Versión " + versionName).withIcon(getResources().getDrawable(R.mipmap.ic_launcher)).withIdentifier(9));
                    headerResult.setBackgroundRes(drawable);
                    headerResult.setProfiles(profile);
                }
        return drawable;
    }
    public void toast(String texto){
        Toast.makeText(this,texto,Toast.LENGTH_LONG).show();
    }
    public void onVerclicked(View view){
        String id=view.getResources().getResourceName(view.getId());
        int index=Integer.parseInt(id.substring(id.lastIndexOf("D") + 1))-1;
        List<String> a= Arrays.asList(aids);
        List<String> n=Arrays.asList(numeros);
        File file=new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/"+a.get(index)+"/"+a.get(index)+"_"+n.get(a.indexOf(a.get(index)))+".mp4");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file));
        intent.setDataAndType(Uri.fromFile(file), "video/mp4");
        startActivity(intent);
    }
    public void onDesClicked(View view){
        String url;
        final GifImageButton imageButton=(GifImageButton) view;
        String id=view.getResources().getResourceName(view.getId());
        final int index=Integer.parseInt(id.substring(id.lastIndexOf("D") + 1))-1;
        if (isDesc.get(index)){
            List<String> a= Arrays.asList(aids);
            List<String> n=Arrays.asList(numeros);
            final File file=new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/"+a.get(index)+"/"+a.get(index)+"_"+n.get(a.indexOf(a.get(index)))+".mp4");
            if (file.exists()) {
                MaterialDialog borrar=new MaterialDialog.Builder(context)
                        .title("Eliminar")
                        .titleGravity(GravityEnum.CENTER)
                        .content("Desea eliminar el capitulo "+n.get(a.indexOf(a.get(index)))+" de "+titulos[index]+"?")
                        .positiveText("Eliminar")
                        .negativeText("Cancelar")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                if (file.delete()) {
                                    isDesc.add(index, false);
                                    imageButton.setImageResource(R.drawable.ic_get_r);
                                    IBsVerList.get(index).setImageResource(R.drawable.ic_ver_no);
                                    IBsVerList.get(index).setEnabled(false);
                                    long l=Long.parseLong(getSharedPreferences("data",MODE_PRIVATE).getString(eids[index],"0"));
                                    if (l!=0) {
                                        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                        manager.remove(l);
                                    }
                                    toast("Archivo Eliminado");
                                }
                            }
                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                dialog.dismiss();
                            }
                        })
                        .build();
                borrar.show();
            }else {
                isDesc.add(index, false);
                imageButton.setScaleType(ImageView.ScaleType.FIT_END);
                imageButton.setImageResource(R.drawable.ic_get_r);
                IBsVerList.get(index).setImageResource(R.drawable.ic_ver_no);
                IBsVerList.get(index).setEnabled(false);
                toast("El archivo ya no existe");
            }
        }else {
            if (!descargando) {
                if (isNetworkAvailable()&&verOk) {
                    new Requests(context, TaskType.VERSION).execute("https://raw.githubusercontent.com/jordyamc/Animeflv/master/app/version.html");
                    imageButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    imageButton.setImageResource(R.drawable.cargando);
                    imageButton.setEnabled(false);
                    Tindex=index;
                    GIBT = imageButton;
                    IBVT = IBsVerList.get(index);
                    isDesc.add(index, true);
                    descargando = true;
                    indexT = index;
                    eidT = eids[index];
                    switch (view.getId()) {
                        case R.id.ib_descargar_cardD1:
                            url = getUrl(titulos[0], numeros[0]);
                            new Requests(this, TaskType.GET_HTML1).execute(url);
                            break;
                        case R.id.ib_descargar_cardD2:
                            url = getUrl(titulos[1], numeros[1]);
                            new Requests(this, TaskType.GET_HTML1).execute(url);
                            break;
                        case R.id.ib_descargar_cardD3:
                            url = getUrl(titulos[2], numeros[2]);
                            new Requests(this, TaskType.GET_HTML1).execute(url);
                            break;
                        case R.id.ib_descargar_cardD4:
                            url = getUrl(titulos[3], numeros[3]);
                            new Requests(this, TaskType.GET_HTML1).execute(url);
                            break;
                        case R.id.ib_descargar_cardD5:
                            url = getUrl(titulos[4], numeros[4]);
                            new Requests(this, TaskType.GET_HTML1).execute(url);
                            break;
                        case R.id.ib_descargar_cardD6:
                            url = getUrl(titulos[5], numeros[5]);
                            new Requests(this, TaskType.GET_HTML1).execute(url);
                            break;
                        case R.id.ib_descargar_cardD7:
                            url = getUrl(titulos[6], numeros[6]);
                            new Requests(this, TaskType.GET_HTML1).execute(url);
                            break;
                        case R.id.ib_descargar_cardD8:
                            url = getUrl(titulos[7], numeros[7]);
                            new Requests(this, TaskType.GET_HTML1).execute(url);
                            break;
                        case R.id.ib_descargar_cardD9:
                            url = getUrl(titulos[8], numeros[8]);
                            new Requests(this, TaskType.GET_HTML1).execute(url);
                            break;
                        case R.id.ib_descargar_cardD10:
                            url = getUrl(titulos[9], numeros[9]);
                            new Requests(this, TaskType.GET_HTML1).execute(url);
                            break;
                        case R.id.ib_descargar_cardD11:
                            url = getUrl(titulos[10], numeros[10]);
                            new Requests(this, TaskType.GET_HTML1).execute(url);
                            break;
                        case R.id.ib_descargar_cardD12:
                            url = getUrl(titulos[11], numeros[11]);
                            new Requests(this, TaskType.GET_HTML1).execute(url);
                            break;
                        case R.id.ib_descargar_cardD13:
                            url = getUrl(titulos[12], numeros[12]);
                            new Requests(this, TaskType.GET_HTML1).execute(url);
                            break;
                        case R.id.ib_descargar_cardD14:
                            url = getUrl(titulos[13], numeros[13]);
                            new Requests(this, TaskType.GET_HTML1).execute(url);
                            break;
                        case R.id.ib_descargar_cardD15:
                            url = getUrl(titulos[14], numeros[14]);
                            new Requests(this, TaskType.GET_HTML1).execute(url);
                            break;
                        case R.id.ib_descargar_cardD16:
                            url = getUrl(titulos[15], numeros[15]);
                            new Requests(this, TaskType.GET_HTML1).execute(url);
                            break;
                        case R.id.ib_descargar_cardD17:
                            url = getUrl(titulos[16], numeros[16]);
                            new Requests(this, TaskType.GET_HTML1).execute(url);
                            break;
                        case R.id.ib_descargar_cardD18:
                            url = getUrl(titulos[17], numeros[17]);
                            new Requests(this, TaskType.GET_HTML1).execute(url);
                            break;
                        case R.id.ib_descargar_cardD19:
                            url = getUrl(titulos[18], numeros[18]);
                            new Requests(this, TaskType.GET_HTML1).execute(url);
                            break;
                        case R.id.ib_descargar_cardD20:
                            url = getUrl(titulos[19], numeros[19]);
                            new Requests(this, TaskType.GET_HTML1).execute(url);
                            break;
                    }
                }else {
                    if (!verOk) {
                        toast("Error en version");
                    }else {
                        toast("No hay conexion");
                    }
                }
            }else {
                toast("Por favor espera...");
            }
        }
    }
    public void onCardClicked(View view){
        switch (view.getId()){
            case R.id.card1:
                setInfo(aids[0]);
                break;
            case R.id.card2:
                setInfo(aids[1]);
                break;
            case R.id.card3:
                setInfo(aids[2]);
                break;
            case R.id.card4:
                setInfo(aids[3]);
                break;
            case R.id.card5:
                setInfo(aids[4]);
                break;
            case R.id.card6:
                setInfo(aids[5]);
                break;
            case R.id.card7:
                setInfo(aids[6]);
                break;
            case R.id.card8:
                setInfo(aids[7]);
                break;
            case R.id.card9:
                setInfo(aids[8]);
                break;
            case R.id.card10:
                setInfo(aids[9]);
                break;
            case R.id.card11:
                setInfo(aids[10]);
                break;
            case R.id.card12:
                setInfo(aids[11]);
                break;
            case R.id.card13:
                setInfo(aids[12]);
                break;
            case R.id.card14:
                setInfo(aids[13]);
                break;
            case R.id.card15:
                setInfo(aids[14]);
                break;
            case R.id.card16:
                setInfo(aids[15]);
                break;
            case R.id.card17:
                setInfo(aids[16]);
                break;
            case R.id.card18:
                setInfo(aids[17]);
                break;
            case R.id.card19:
                setInfo(aids[18]);
                break;
            case R.id.card20:
                setInfo(aids[19]);
                break;
        }
    }
    public void setDir(Boolean busqueda){
        if (!busqueda) {
            if (isNetworkAvailable()) {
                new Requests(context, TaskType.DIRECTORIO).execute("http://animeflv.net/api.php?accion=directorio");
            } else {
                if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                    if (!mediaStorage.exists()) {
                        mediaStorage.mkdirs();
                    }
                }
                File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt");
                String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
                if (file.exists()) {
                    Intent intent = new Intent(context, Directorio.class);
                    startActivity(intent);
                } else {
                    toast("No hay datos guardados");
                }
            }
        }else {
            if (isNetworkAvailable()) {
                new Requests(context, TaskType.DIRECTORIO1).execute("http://animeflv.net/api.php?accion=directorio");
            } else {
                if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                    if (!mediaStorage.exists()) {
                        mediaStorage.mkdirs();
                    }
                }
                File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt");
                String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
                if (file.exists()) {
                    Intent intent=new Intent(context,Directorio.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("tipo","Busqueda");
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    toast("No hay datos guardados");
                }
            }
        }
    }
    public void setInfo(String aid){
        aidInfo=aid;
        SharedPreferences sharedPreferences=getSharedPreferences("data",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("aid",aidInfo);
        editor.commit();
        new Requests(this,TaskType.GET_INFO).execute("http://animeflv.net/api.php?accion=anime&aid=" + aid);
    }
    public void actCacheInfo(String json){
        Bundle bundleInfo=new Bundle();
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!mediaStorage.exists()) {
                mediaStorage.mkdirs();
            }
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/"+aidInfo+".txt");
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/"+aidInfo+".txt";
        if (isNetworkAvailable()) {
            if (!file.exists()) {
                Log.d("Archivo:", "No existe");
                try {file.createNewFile();} catch (IOException e) {Log.d("Archivo:", "Error al crear archivo");}
                writeToFile(json, file);
                bundleInfo.putString("aid",parser.getAID(json));
                Intent intent=new Intent(this,Info.class);
                intent.putExtras(bundleInfo);
                startActivity(intent);
            } else {
                Log.d("Archivo", "Existe");
                String infile = getStringFromFile(file_loc);
                if (json.trim().equals(infile.trim())) {
                    bundleInfo.putString("aid",parser.getAID(json));
                    Intent intent = new Intent(this, Info.class);
                    intent.putExtras(bundleInfo);
                    startActivity(intent);
                }else {
                    writeToFile(json,file);
                    bundleInfo.putString("aid", parser.getAID(json));
                    Intent intent = new Intent(this, Info.class);
                    intent.putExtras(bundleInfo);
                    startActivity(intent);
                }
            }
        } else {
            if (file.exists()) {
                bundleInfo.putString("aid",parser.getAID(json));
                Intent intent = new Intent(this, Info.class);
                intent.putExtras(bundleInfo);
                startActivity(intent);
            } else {
                toast("No hay datos guardados");
            }
        }
    }
    public void setLoad(){
        scrollView=(ScrollView) findViewById(R.id.sv_inicio);
        mswipe=(SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        imgCard1=(ImageView) findViewById(R.id.imgCardD1);
        imgCard2=(ImageView) findViewById(R.id.imgCardD2);
        imgCard3=(ImageView) findViewById(R.id.imgCardD3);
        imgCard4=(ImageView) findViewById(R.id.imgCardD4);
        imgCard5=(ImageView) findViewById(R.id.imgCardD5);
        imgCard6=(ImageView) findViewById(R.id.imgCardD6);
        imgCard7=(ImageView) findViewById(R.id.imgCardD7);
        imgCard8=(ImageView) findViewById(R.id.imgCardD8);
        imgCard9=(ImageView) findViewById(R.id.imgCardD9);
        imgCard10=(ImageView) findViewById(R.id.imgCardD10);
        imgCard11=(ImageView) findViewById(R.id.imgCardD11);
        imgCard12=(ImageView) findViewById(R.id.imgCardD12);
        imgCard13=(ImageView) findViewById(R.id.imgCardD13);
        imgCard14=(ImageView) findViewById(R.id.imgCardD14);
        imgCard15=(ImageView) findViewById(R.id.imgCardD15);
        imgCard16=(ImageView) findViewById(R.id.imgCardD16);
        imgCard17=(ImageView) findViewById(R.id.imgCardD17);
        imgCard18=(ImageView) findViewById(R.id.imgCardD18);
        imgCard19=(ImageView) findViewById(R.id.imgCardD19);
        imgCard20=(ImageView) findViewById(R.id.imgCardD20);

        txtTitulo1=(TextView) findViewById(R.id.tv_cardD_titulo1);
        txtTitulo2=(TextView) findViewById(R.id.tv_cardD_titulo2);
        txtTitulo3=(TextView) findViewById(R.id.tv_cardD_titulo3);
        txtTitulo4=(TextView) findViewById(R.id.tv_cardD_titulo4);
        txtTitulo5=(TextView) findViewById(R.id.tv_cardD_titulo5);
        txtTitulo6=(TextView) findViewById(R.id.tv_cardD_titulo6);
        txtTitulo7=(TextView) findViewById(R.id.tv_cardD_titulo7);
        txtTitulo8=(TextView) findViewById(R.id.tv_cardD_titulo8);
        txtTitulo9=(TextView) findViewById(R.id.tv_cardD_titulo9);
        txtTitulo10=(TextView) findViewById(R.id.tv_cardD_titulo10);
        txtTitulo11=(TextView) findViewById(R.id.tv_cardD_titulo11);
        txtTitulo12=(TextView) findViewById(R.id.tv_cardD_titulo12);
        txtTitulo13=(TextView) findViewById(R.id.tv_cardD_titulo13);
        txtTitulo14=(TextView) findViewById(R.id.tv_cardD_titulo14);
        txtTitulo15=(TextView) findViewById(R.id.tv_cardD_titulo15);
        txtTitulo16=(TextView) findViewById(R.id.tv_cardD_titulo16);
        txtTitulo17=(TextView) findViewById(R.id.tv_cardD_titulo17);
        txtTitulo18=(TextView) findViewById(R.id.tv_cardD_titulo18);
        txtTitulo19=(TextView) findViewById(R.id.tv_cardD_titulo19);
        txtTitulo20=(TextView) findViewById(R.id.tv_cardD_titulo20);

        txtCapitulo1=(TextView) findViewById(R.id.tv_cardD_capitulo1);
        txtCapitulo2=(TextView) findViewById(R.id.tv_cardD_capitulo2);
        txtCapitulo3=(TextView) findViewById(R.id.tv_cardD_capitulo3);
        txtCapitulo4=(TextView) findViewById(R.id.tv_cardD_capitulo4);
        txtCapitulo5=(TextView) findViewById(R.id.tv_cardD_capitulo5);
        txtCapitulo6=(TextView) findViewById(R.id.tv_cardD_capitulo6);
        txtCapitulo7=(TextView) findViewById(R.id.tv_cardD_capitulo7);
        txtCapitulo8=(TextView) findViewById(R.id.tv_cardD_capitulo8);
        txtCapitulo9=(TextView) findViewById(R.id.tv_cardD_capitulo9);
        txtCapitulo10=(TextView) findViewById(R.id.tv_cardD_capitulo10);
        txtCapitulo11=(TextView) findViewById(R.id.tv_cardD_capitulo11);
        txtCapitulo12=(TextView) findViewById(R.id.tv_cardD_capitulo12);
        txtCapitulo13=(TextView) findViewById(R.id.tv_cardD_capitulo13);
        txtCapitulo14=(TextView) findViewById(R.id.tv_cardD_capitulo14);
        txtCapitulo15=(TextView) findViewById(R.id.tv_cardD_capitulo15);
        txtCapitulo16=(TextView) findViewById(R.id.tv_cardD_capitulo16);
        txtCapitulo17=(TextView) findViewById(R.id.tv_cardD_capitulo17);
        txtCapitulo18=(TextView) findViewById(R.id.tv_cardD_capitulo18);
        txtCapitulo19=(TextView) findViewById(R.id.tv_cardD_capitulo19);
        txtCapitulo20=(TextView) findViewById(R.id.tv_cardD_capitulo20);

        ibDes1=(GifImageButton) findViewById(R.id.ib_descargar_cardD1);
        ibDes2=(GifImageButton) findViewById(R.id.ib_descargar_cardD2);
        ibDes3=(GifImageButton) findViewById(R.id.ib_descargar_cardD3);
        ibDes4=(GifImageButton) findViewById(R.id.ib_descargar_cardD4);
        ibDes5=(GifImageButton) findViewById(R.id.ib_descargar_cardD5);
        ibDes6=(GifImageButton) findViewById(R.id.ib_descargar_cardD6);
        ibDes7=(GifImageButton) findViewById(R.id.ib_descargar_cardD7);
        ibDes8=(GifImageButton) findViewById(R.id.ib_descargar_cardD8);
        ibDes9=(GifImageButton) findViewById(R.id.ib_descargar_cardD9);
        ibDes10=(GifImageButton) findViewById(R.id.ib_descargar_cardD10);
        ibDes11=(GifImageButton) findViewById(R.id.ib_descargar_cardD11);
        ibDes12=(GifImageButton) findViewById(R.id.ib_descargar_cardD12);
        ibDes13=(GifImageButton) findViewById(R.id.ib_descargar_cardD13);
        ibDes14=(GifImageButton) findViewById(R.id.ib_descargar_cardD14);
        ibDes15=(GifImageButton) findViewById(R.id.ib_descargar_cardD15);
        ibDes16=(GifImageButton) findViewById(R.id.ib_descargar_cardD16);
        ibDes17=(GifImageButton) findViewById(R.id.ib_descargar_cardD17);
        ibDes18=(GifImageButton) findViewById(R.id.ib_descargar_cardD18);
        ibDes19=(GifImageButton) findViewById(R.id.ib_descargar_cardD19);
        ibDes20=(GifImageButton) findViewById(R.id.ib_descargar_cardD20);

        ibVer1=(ImageButton) findViewById(R.id.ib_ver_cardD1);
        ibVer2=(ImageButton) findViewById(R.id.ib_ver_cardD2);
        ibVer3=(ImageButton) findViewById(R.id.ib_ver_cardD3);
        ibVer4=(ImageButton) findViewById(R.id.ib_ver_cardD4);
        ibVer5=(ImageButton) findViewById(R.id.ib_ver_cardD5);
        ibVer6=(ImageButton) findViewById(R.id.ib_ver_cardD6);
        ibVer7=(ImageButton) findViewById(R.id.ib_ver_cardD7);
        ibVer8=(ImageButton) findViewById(R.id.ib_ver_cardD8);
        ibVer9=(ImageButton) findViewById(R.id.ib_ver_cardD9);
        ibVer10=(ImageButton) findViewById(R.id.ib_ver_cardD10);
        ibVer11=(ImageButton) findViewById(R.id.ib_ver_cardD11);
        ibVer12=(ImageButton) findViewById(R.id.ib_ver_cardD12);
        ibVer13=(ImageButton) findViewById(R.id.ib_ver_cardD13);
        ibVer14=(ImageButton) findViewById(R.id.ib_ver_cardD14);
        ibVer15=(ImageButton) findViewById(R.id.ib_ver_cardD15);
        ibVer16=(ImageButton) findViewById(R.id.ib_ver_cardD16);
        ibVer17=(ImageButton) findViewById(R.id.ib_ver_cardD17);
        ibVer18=(ImageButton) findViewById(R.id.ib_ver_cardD18);
        ibVer19=(ImageButton) findViewById(R.id.ib_ver_cardD19);
        ibVer20=(ImageButton) findViewById(R.id.ib_ver_cardD20);
        textoff=(TextView) findViewById(R.id.textOffline);
        textoff.setVisibility(View.GONE);
        IBsDesList.add(ibDes1);IBsVerList.add(ibVer1);
        IBsDesList.add(ibDes2);IBsVerList.add(ibVer2);
        IBsDesList.add(ibDes3);IBsVerList.add(ibVer3);
        IBsDesList.add(ibDes4);IBsVerList.add(ibVer4);
        IBsDesList.add(ibDes5);IBsVerList.add(ibVer5);
        IBsDesList.add(ibDes6);IBsVerList.add(ibVer6);
        IBsDesList.add(ibDes7);IBsVerList.add(ibVer7);
        IBsDesList.add(ibDes8);IBsVerList.add(ibVer8);
        IBsDesList.add(ibDes9);IBsVerList.add(ibVer9);
        IBsDesList.add(ibDes10);IBsVerList.add(ibVer10);
        IBsDesList.add(ibDes11);IBsVerList.add(ibVer11);
        IBsDesList.add(ibDes12);IBsVerList.add(ibVer12);
        IBsDesList.add(ibDes13);IBsVerList.add(ibVer13);
        IBsDesList.add(ibDes14);IBsVerList.add(ibVer14);
        IBsDesList.add(ibDes15);IBsVerList.add(ibVer15);
        IBsDesList.add(ibDes16);IBsVerList.add(ibVer16);
        IBsDesList.add(ibDes17);IBsVerList.add(ibVer17);
        IBsDesList.add(ibDes18);IBsVerList.add(ibVer18);
        IBsDesList.add(ibDes19);IBsVerList.add(ibVer19);
        IBsDesList.add(ibDes20);IBsVerList.add(ibVer20);
        web=(WebView) findViewById(R.id.wv_inicio);
        web.getSettings().setJavaScriptEnabled(true);
        web.addJavascriptInterface(new JavaScriptInterface(context), "HtmlViewer");
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                //web.loadUrl("javascript:"+"var num=e();"+"window.HtmlViewer.showHTMLD2(e());");
                if (!url.startsWith("http://necrotic-neganebulus.hol.es/contador.php?id=")) {
                    if (url.contains("zippyshare.com")||url.contains("blank")) {
                        web.loadUrl("javascript:("
                                + "function(){var l=document.getElementById('dlbutton');" + "var f=document.createEvent('HTMLEvents');" + "f.initEvent('click',true,true);" + "l.dispatchEvent(f);}"
                                + ")()");
                    }else {
                        isDesc.add(Tindex, false);
                        GIBT.setScaleType(ImageView.ScaleType.FIT_END);
                        GIBT.setImageResource(R.drawable.ic_get_r);
                        IBsVerList.get(Tindex).setImageResource(R.drawable.ic_ver_no);
                        IBsVerList.get(Tindex).setEnabled(false);
                        toast("Error al descargar");
                    }
                }else {
                    getSharedPreferences("data",MODE_PRIVATE).edit().putInt("reg" + Integer.toString(versionCode), 1);
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
                File Dstorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("_")));
                if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                    if (!Dstorage.exists()) {
                        Dstorage.mkdirs();
                    }
                }
                File archivo = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("_")) + "/" + fileName);
                if (!archivo.exists() && descargando && verOk) {
                    GIBT.setScaleType(ImageView.ScaleType.FIT_END);
                    GIBT.setImageResource(R.drawable.ic_borrar_r);
                    GIBT.setEnabled(true);
                    IBVT.setImageResource(R.drawable.ic_rep_r);
                    IBVT.setEnabled(true);
                    String urlD = getSharedPreferences("data", MODE_PRIVATE).getString("urlD", null);
                    CookieManager cookieManager = CookieManager.getInstance();
                    String cookie = cookieManager.getCookie(url.substring(0, url.indexOf("/", 8)));
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    //request.setTitle(fileName.substring(0, fileName.indexOf(".")));
                    request.setTitle(titulos[indexT]);
                    request.setDescription("Capitulo "+numeros[Tindex]);
                    request.addRequestHeader("cookie", cookie);
                    request.addRequestHeader("User-Agent", web.getSettings().getUserAgentString());
                    request.addRequestHeader("Accept", "text/html, application/xhtml+xml, *" + "/" + "*");
                    request.addRequestHeader("Accept-Language", "en-US,en;q=0.7,he;q=0.3");
                    request.addRequestHeader("Referer", urlD);
                    request.setMimeType("video/mp4");
                    request.setDestinationInExternalPublicDir("Animeflv/download/" + url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("_")), fileName);
                    DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    long l = manager.enqueue(request);
                    descargando = false;
                    web.loadUrl("about:blank");
                    getSharedPreferences("data", MODE_PRIVATE).edit().putString(eidT, Long.toString(l)).apply();
                } else {
                    web.loadUrl("about:blank");
                    GIBT.setScaleType(ImageView.ScaleType.FIT_END);
                    GIBT.setImageResource(R.drawable.ic_get_r);
                    GIBT.setEnabled(true);
                    IBVT.setImageResource(R.drawable.ic_ver_no);
                    IBVT.setEnabled(false);
                }
            }
        });

        web_Links=(WebView) findViewById(R.id.wv_inicio2);
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
                            "window.HtmlViewer.showHTMLD1(json);");
            }
        });
    }
    public void loadImg(String[] list){
        final Context context=getApplicationContext();
        final String[] url=list;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PicassoCache.getPicassoInstance(context).load(url[0]).error(R.drawable.ic_block_r).into(imgCard1);
                PicassoCache.getPicassoInstance(context).load(url[1]).error(R.drawable.ic_block_r).into(imgCard2);
                PicassoCache.getPicassoInstance(context).load(url[2]).error(R.drawable.ic_block_r).into(imgCard3);
                PicassoCache.getPicassoInstance(context).load(url[3]).error(R.drawable.ic_block_r).into(imgCard4);
                PicassoCache.getPicassoInstance(context).load(url[4]).error(R.drawable.ic_block_r).into(imgCard5);
                PicassoCache.getPicassoInstance(context).load(url[5]).error(R.drawable.ic_block_r).into(imgCard6);
                PicassoCache.getPicassoInstance(context).load(url[6]).error(R.drawable.ic_block_r).into(imgCard7);
                PicassoCache.getPicassoInstance(context).load(url[7]).error(R.drawable.ic_block_r).into(imgCard8);
                PicassoCache.getPicassoInstance(context).load(url[8]).error(R.drawable.ic_block_r).into(imgCard9);
                PicassoCache.getPicassoInstance(context).load(url[9]).error(R.drawable.ic_block_r).into(imgCard10);
                PicassoCache.getPicassoInstance(context).load(url[10]).error(R.drawable.ic_block_r).into(imgCard11);
                PicassoCache.getPicassoInstance(context).load(url[11]).error(R.drawable.ic_block_r).into(imgCard12);
                PicassoCache.getPicassoInstance(context).load(url[12]).error(R.drawable.ic_block_r).into(imgCard13);
                PicassoCache.getPicassoInstance(context).load(url[13]).error(R.drawable.ic_block_r).into(imgCard14);
                PicassoCache.getPicassoInstance(context).load(url[14]).error(R.drawable.ic_block_r).into(imgCard15);
                PicassoCache.getPicassoInstance(context).load(url[15]).error(R.drawable.ic_block_r).into(imgCard16);
                PicassoCache.getPicassoInstance(context).load(url[16]).error(R.drawable.ic_block_r).into(imgCard17);
                PicassoCache.getPicassoInstance(context).load(url[17]).error(R.drawable.ic_block_r).into(imgCard18);
                PicassoCache.getPicassoInstance(context).load(url[18]).error(R.drawable.ic_block_r).into(imgCard19);
                PicassoCache.getPicassoInstance(context).load(url[19]).error(R.drawable.ic_block_r).into(imgCard20);
            }
        });
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
    public void getJson(){
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!mediaStorage.exists()) {
                mediaStorage.mkdirs();
            }
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/inicio.txt");
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/inicio.txt";
        if (isNetworkAvailable()) {
            textoff.setVisibility(View.GONE);
            new Requests(this, TaskType.GET_INICIO).execute(inicio);
        }else {
            verOk=false;
            if (file.exists()) {
                textoff.setVisibility(View.VISIBLE);
                String infile = getStringFromFile(file_loc);
                getData(infile);
            } else {
                toast("No hay datos guardados");
            }
        }
    }
    public void getlinks(String json) {
        loadImg(parser.parseLinks(json));
    }
    public void gettitulos(String json){
        loadTitulos(parser.parseTitulos(json));
    }
    public void getCapitulos(String json){
        loadCapitulos(parser.parseCapitulos(json));
    }
    public void isFirst(){
        mswipe.post(new Runnable() {
            @Override
            public void run() {
                mswipe.setRefreshing(false);
            }
        });
        if (first==1){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    scrollView.setVisibility(View.VISIBLE);
                }});
            if (mswipe.isRefreshing()){mswipe.setRefreshing(false);}
            first=0;
            NotificationManager notificationManager = (NotificationManager) this
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(6991);
        }
    }
    public void getData(String json){
        getlinks(json);
        gettitulos(json);
        getCapitulos(json);
        titulos=parser.parseTitulos(json);
        eids=parser.parseEID(json);
        aids=parser.parseAID(json);
        numeros=parser.parsenumeros(json);
        mswipe.setRefreshing(false);
        checkButtons(aids, numeros, eids);
        String teids="";
        for (String s:eids){
            teids+=":::"+s;
        }
        getSharedPreferences("data",MODE_PRIVATE).edit().putString("teids",teids).apply();
        isFirst();
    }
    public void checkButtons(String[] aids,String[] numeros,String[] eids){
        List<String> a= Arrays.asList(aids);
        List<String> n=Arrays.asList(numeros);
        List<String> e=Arrays.asList(eids);
        isDesc=new ArrayList<Boolean>();
        for (String s:e){
            Log.i("dir", Environment.getExternalStorageDirectory() + "/Animeflv/download/" + a.get(e.indexOf(s)) + "/" + a.get(e.indexOf(s)) + "_" + n.get(e.indexOf(s)) + ".mp4");
            int index=e.indexOf(s);
            File file=new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/"+a.get(e.indexOf(s))+"/"+a.get(e.indexOf(s))+"_"+n.get(e.indexOf(s))+".mp4");
            Log.i("Existe",String.valueOf(file.exists()));
            if (file.exists()){
                IBsDesList.get(index).setImageResource(R.drawable.ic_borrar_r);
                IBsVerList.get(index).setEnabled(true);
                IBsVerList.get(index).setImageResource(R.drawable.ic_rep_r);
                isDesc.add(true);
            }else {
                IBsDesList.get(index).setImageResource(R.drawable.ic_get_r);
                IBsVerList.get(index).setEnabled(false);
                IBsVerList.get(index).setImageResource(R.drawable.ic_ver_no);
                isDesc.add(false);
            }
        }
    }
    public String getUrl(String titulo,String capitulo){
        String ftitulo="";
        String atitulo=titulo.toLowerCase();
        atitulo=atitulo.replace("*","-");
        atitulo=atitulo.replace(":","");
        atitulo=atitulo.replace(",","");
        atitulo=atitulo.replace(" \u2606 ","-");
        atitulo=atitulo.replace("\u2606","-");
        atitulo=atitulo.replace("  ","-");
        atitulo=atitulo.replace("@","a");
        atitulo=atitulo.replace("/","-");
        atitulo=atitulo.replace(".","");
        for (int x=0; x < atitulo.length(); x++) {
            if (atitulo.charAt(x) != ' ') {
                ftitulo += atitulo.charAt(x);
            }else {
                if (atitulo.charAt(x) == ' ') {
                    ftitulo += "-";
                }
            }
        }
        ftitulo=ftitulo.replace("!!!","-3");
        ftitulo=ftitulo.replace("!", "");
        ftitulo=ftitulo.replace("°", "");
        ftitulo=ftitulo.replace("&deg;", "");
        ftitulo=ftitulo.replace("(","");
        ftitulo=ftitulo.replace(")","");
        ftitulo=ftitulo.replace("2nd-season","2");
        ftitulo=ftitulo.replace("'","");
        if (ftitulo.trim().equals("gintama")){ftitulo=ftitulo+"-2015";}
        if (ftitulo.trim().equals("miss-monochrome-the-animation-2")){ftitulo="miss-monochrome-the-animation-2nd-season";}
        String link="http://animeflv.net/ver/"+ftitulo+"-"+capitulo+".html";
        return link;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isXLargeScreen(getApplicationContext())){
            getMenuInflater().inflate(R.menu.menu_main_dark, menu);
        }else {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
        return true;
    }
    @Override
    public void onRefresh() {
        if (isNetworkAvailable()) {
            getSharedPreferences("data",MODE_PRIVATE).edit().putInt("nCaps",0).apply();
            textoff.setVisibility(View.GONE);
            new Requests(context, TaskType.VERSION).execute("https://raw.githubusercontent.com/jordyamc/Animeflv/master/app/version.html");
            new Requests(this, TaskType.GET_INICIO).execute(inicio);
        }else {
            textoff.setVisibility(View.VISIBLE);
            if (mswipe.isRefreshing()){mswipe.setRefreshing(false);}
        }
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(6991);
    }
    public static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }
    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (!isXLargeScreen(getApplicationContext()) ) {
            return;
        }
    }
    public void cambiarColor(){
            final MaterialDialog dialog = new MaterialDialog.Builder(context)
                    .title("Selecciona un color:")
                    .customView(R.layout.dialog_colores, false)
                    .build();
            ImageButton naranja = (ImageButton) dialog.getCustomView().findViewById(R.id.ib_color_naranja);
            ImageButton amarillo = (ImageButton) dialog.getCustomView().findViewById(R.id.ib_color_amarillo);
            ImageButton rojo = (ImageButton) dialog.getCustomView().findViewById(R.id.ib_color_rojo);
            ImageButton rosa = (ImageButton) dialog.getCustomView().findViewById(R.id.ib_color_rosa);
            ImageButton alnek = (ImageButton) dialog.getCustomView().findViewById(R.id.ib_color_AlNek);
            naranja.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt("color", 0).apply();
                    getHDraw(true);
                    dialog.dismiss();
                }
            });
            amarillo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt("color", 1).apply();
                    getHDraw(true);
                    dialog.dismiss();
                }
            });
            rojo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt("color", 2).apply();
                    getHDraw(true);
                    dialog.dismiss();
                }
            });
            rosa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt("color", 3).apply();
                    getHDraw(true);
                    dialog.dismiss();
                }
            });
            alnek.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt("color", 4).apply();
                    getHDraw(true);
                    dialog.dismiss();
                }
            });
            dialog.show();
    }
    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }
    public static String getStringFromFile (String filePath) {
        String ret="";
        try {
            File fl = new File(filePath);
            FileInputStream fin = new FileInputStream(fl);
            ret = convertStreamToString(fin);
            fin.close();
        }catch (IOException e){}catch (Exception e){}
        return ret;
    }
    public  void writeToFile(String body,File file){
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(body.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean isNumeric( String str ) {
        DecimalFormatSymbols currentLocaleSymbols = DecimalFormatSymbols.getInstance();
        char localeMinusSign = currentLocaleSymbols.getMinusSign();
        if ( !Character.isDigit( str.charAt( 0 ) ) && str.charAt( 0 ) != localeMinusSign ) return false;
        boolean isDecimalSeparatorFound = false;
        char localeDecimalSeparator = currentLocaleSymbols.getDecimalSeparator();
        for ( char c : str.substring( 1 ).toCharArray() ) {
            if ( !Character.isDigit( c ) ) {
                if ( c == localeDecimalSeparator && !isDecimalSeparatorFound ) {
                    isDecimalSeparatorFound = true;
                    continue;
                }
                return false;
            }
        }
        return true;
    }
    private boolean isNetworkAvailable() {
        Boolean net=false;
        int Tcon=Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_conexion", "0"));
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        switch (Tcon){
            case 0:
                NetworkInfo Wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                net=Wifi.isConnected();
                break;
            case 1:
                NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                net=mobile.isConnected();
                break;
            case 2:
                NetworkInfo WifiA = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mobileA = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                net=WifiA.isConnected()||mobileA.isConnected();
                break;
        }
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && net;
    }
    @Override
    public void sendtext1(String data,TaskType taskType){
        if (taskType==TaskType.DIRECTORIO){
            if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                if (!mediaStorage.exists()) {
                    mediaStorage.mkdirs();
                }
            }
            File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt");
            String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
            if (isNetworkAvailable()&&!data.trim().equals("")) {
                if (!file.exists()) {
                    Log.d("Archivo:", "No existe");
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        Log.d("Archivo:", "Error al crear archivo");
                    }
                    writeToFile(data, file);
                    Intent intent=new Intent(context,Directorio.class);
                    startActivity(intent);
                } else {
                    Log.d("Archivo", "Existe");
                    String infile = getStringFromFile(file_loc);
                    if (infile.trim().equals(data.trim())) {
                        Log.d("Cargar", "Json nuevo");
                        writeToFile(data, file);
                        Intent intent=new Intent(context,Directorio.class);
                        startActivity(intent);
                    } else {
                        Log.d("Cargar", "Json existente");
                        Intent intent=new Intent(context,Directorio.class);
                        startActivity(intent);
                    }
                }
            }
        }
        if (taskType==TaskType.DIRECTORIO1){
            if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                if (!mediaStorage.exists()) {
                    mediaStorage.mkdirs();
                }
            }
            File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt");
            String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
            if (isNetworkAvailable()&&!data.trim().equals("")) {
                if (!file.exists()) {
                    Log.d("Archivo:", "No existe");
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        Log.d("Archivo:", "Error al crear archivo");
                    }
                    writeToFile(data, file);
                    Intent intent=new Intent(context,Directorio.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("tipo", "Busqueda");
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Log.d("Archivo", "Existe");
                    String infile = getStringFromFile(file_loc);
                    if (infile.trim().equals(data.trim())) {
                        Log.d("Cargar", "Json nuevo");
                        writeToFile(data, file);
                        Intent intent=new Intent(context,Directorio.class);
                        Bundle bundle=new Bundle();
                        bundle.putString("tipo", "Busqueda");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        Log.d("Cargar", "Json existente");
                        Intent intent=new Intent(context,Directorio.class);
                        Bundle bundle=new Bundle();
                        bundle.putString("tipo","Busqueda");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            }
        }
        if (taskType==TaskType.VERSION){
            String vers="";
            if (!isNetworkAvailable()||data.trim().equals("")){
                vers=Integer.toString(versionCode);
            }else {
                if (isNumeric(data.trim())) {
                    vers = data;
                }else {
                    vers = "0";
                    mensaje=data.split(":::");
                }
            }
            Log.d("Version", Integer.toString(versionCode)+ " >> "+vers.trim());
            if (versionCode>=Integer.parseInt(vers.trim())){
                if (Integer.parseInt(vers.trim())==0){
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
                                            if (mensaje[5].trim().equals("toast")){
                                                toast(mensaje[6].trim());
                                            }
                                            if (mensaje[5].trim().equals("toast&notshow")){
                                                toast(mensaje[6].trim());
                                                disM=true;
                                            }
                                            if (mensaje[5].trim().equals("finish")){
                                                finish();
                                            }
                                            if (mensaje[5].trim().equals("dismiss")){
                                                dialog.dismiss();
                                            }
                                            if (mensaje[5].trim().equals("dismiss&notshow")){
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
                }else {
                    Log.d("Version", "OK");
                    verOk = true;
                    getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("notVer", false);
                }
            }else {
                if (showact) {
                    showact=false;
                    Log.d("Version", "Actualizar");
                    verOk = false;
                    MaterialDialog dialog = new MaterialDialog.Builder(this)
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
                                        final TextView textView = (TextView) dialog.getCustomView().findViewById(R.id.tv_progress);
                                        textView.setVisibility(View.VISIBLE);
                                        final ThinDownloadManager downloadManager = new ThinDownloadManager();
                                        Uri download = Uri.parse("https://github.com/jordyamc/Animeflv/blob/master/app/app-release.apk?raw=true");
                                        final DownloadRequest downloadRequest = new DownloadRequest(download)
                                                .setDestinationURI(Uri.fromFile(descarga))
                                                .setDownloadListener(new DownloadStatusListener() {
                                                    @Override
                                                    public void onDownloadComplete(int i) {
                                                        Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                                                                .setDataAndType(Uri.fromFile(descarga),
                                                                        "application/vnd.android.package-archive");
                                                        dialog.dismiss();
                                                        finish();
                                                        startActivity(promptInstall);
                                                    }

                                                    @Override
                                                    public void onDownloadFailed(int i, int i1, String s) {
                                                        toast(s);
                                                        dialog.dismiss();
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onProgress(int i, long l, int i1) {
                                                        textView.setText(Integer.toString(i1) + "%");
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
        if(taskType == TaskType.GET_INICIO) {
            if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                if (!mediaStorage.exists()) {
                    mediaStorage.mkdirs();
                }
            }
            File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/inicio.txt");
            String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/inicio.txt";
            if (isNetworkAvailable()&&!data.trim().equals("")) {
                if (!file.exists()) {
                    Log.d("Archivo:", "No existe");
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        Log.d("Archivo:", "Error al crear archivo");
                    }
                    writeToFile(data, file);
                    getData(data);
                } else {
                    Log.d("Archivo", "Existe");
                    String infile = getStringFromFile(file_loc);
                    if (!parser.parseEID(infile)[0].trim().equals(parser.parseEID(data)[0].trim())) {
                        Log.d("Cargar", "Json nuevo");
                        writeToFile(data, file);
                        getData(data);
                    } else {
                        Log.d("Cargar", "Json existente");
                        getData(infile);
                    }
                }
            }
        }
        if(taskType == TaskType.GET_HTML1) {
            web_Links.loadUrl("about:blank");
            web_Links.loadData(data, "text/html", "UTF-8");
        }
        if (taskType==TaskType.GET_INFO){
            actCacheInfo(data);
        }
        if (taskType==TaskType.GET_HTML2){
            int a=Integer.parseInt(data.substring(data.indexOf("document.getElementById('lang-one').a = ")+40,data.indexOf("document.getElementById('lang-one').a = ") + 46).trim());
            int b=1234567;
            String code=Integer.toString((((a + 3) * 3) % b) + 3);
            Log.d("Int a",Integer.toString(a));
            Log.d("Int b",Integer.toString(b));
            Log.d("code", code);
            String durl=data.substring(data.indexOf("document.getElementById('dlbutton').href = ") + 45, data.indexOf(";", data.indexOf("document.getElementById('dlbutton').href = ") + 45) - 1);
            durl=durl.replace("\"+e()+\"",code);
            String url=getSharedPreferences("data",MODE_PRIVATE).getString("urlD",null);
            String furl="http://"+url.substring(url.indexOf("www"),url.indexOf(".",url.indexOf("www")))+".zippyshare.com/"+durl;
            Log.d("Final D Link",furl);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        pause=false;
        if(shouldExecuteOnResume){
            if (isNetworkAvailable()) {
                getSharedPreferences("data",MODE_PRIVATE).edit().putInt("nCaps",0).apply();
                textoff.setVisibility(View.GONE);
                new Requests(context, TaskType.VERSION).execute("https://raw.githubusercontent.com/jordyamc/Animeflv/master/app/version.html");
                new Requests(this, TaskType.GET_INICIO).execute(inicio);
            }else {
                textoff.setVisibility(View.VISIBLE);
                if (mswipe.isRefreshing()){mswipe.setRefreshing(false);}
            }
            NotificationManager notificationManager = (NotificationManager) this
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(6991);
        } else{
            shouldExecuteOnResume = true;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        pause=true;
    }

    @Override
    public void onBackPressed() {
        if (!result.isDrawerOpen()) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Precione ATRAS para salir", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }else {
            result.closeDrawer();
        }
    }
    class JavaScriptInterface {
        private Context ctx;
        JavaScriptInterface(Context ctx) {
            this.ctx = ctx;}
        @JavascriptInterface
        public void showHTML(String html) {
            String s_html_i=html.substring(21);
            String s_html_f="{"+s_html_i.substring(0,s_html_i.length()-7);
            json = s_html_f;
            getData(s_html_f);
        }
        @JavascriptInterface
        public void showHTMLD1(String html) {
            String replace=html.replace("\\/","/");
            String cortado=replace.substring(replace.indexOf("&proxy.link=")+12);
            cortado=cortado.substring(0,cortado.indexOf("file.html")+9).trim();
            getSharedPreferences("data", MODE_PRIVATE).edit().putString("urlD", cortado).apply();
            getSharedPreferences("data", MODE_PRIVATE).edit().putInt("sov",1).apply();
            final String finalstring=cortado;
            web.post(new Runnable() {
                @Override
                public void run() {
                    web.loadUrl(finalstring);
                }
            });
            //new Requests(context,TaskType.GET_HTML2).execute(cortado);
            //Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(cortado));
           //startActivity(intent);
        }
        @JavascriptInterface
        public void showHTMLD2(String html) {
            Log.d("Zippy", html);
        }}

}
