package knf.animeflv;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceManager;
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
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ScrollView;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import knf.animeflv.Directorio.Directorio;
import knf.animeflv.info.Info;

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
    File mediaStorage = new File(Environment.getExternalStorageDirectory() + "/.Animeflv/cache");
    Parser parser=new Parser();
    String aidInfo;
    String html="<html></html>";
    int versionCode;
    String versionName;

    Drawer result;
    boolean doubleBackToExitPressedOnce = false;
    Toolbar ltoolbar;
    Toolbar Dtoolbar;
    File descarga = new File(Environment.getExternalStorageDirectory() + "/.Animeflv/cache","Animeflv_Nver.apk");
    SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anime_inicio);
        context=this;
        Boolean not= PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notificaciones",true);
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
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.graphic)
                .withCompactStyle(true)
                .withSelectionListEnabled(false)
                .addProfiles(
                        new ProfileDrawerItem().withName("AnimeFLV").withEmail("Versión " + versionName).withIcon(getResources().getDrawable(R.mipmap.ic_launcher))
                )
                .withProfileImagesClickable(false)
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
                        new PrimaryDrawerItem().withName("Directorio").withIcon(GoogleMaterial.Icon.gmd_library_books).withIdentifier(2)
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
                                setDir();
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
                Intent intent=new Intent(context,Directorio.class);
                Bundle bundle=new Bundle();
                bundle.putString("tipo","Busqueda");
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            }
        });
        if (isNetworkAvailable()){new Requests(context,TaskType.VERSION).execute("https://raw.githubusercontent.com/jordyamc/Animeflv/master/app/version.html");}
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        SharedPreferences prefs = this.getSharedPreferences("data", MODE_PRIVATE);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals("reload")){
                    mswipe.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!mswipe.isRefreshing()){
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
                    NotificationManager notificationManager = (NotificationManager) context
                            .getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(6991);
                }
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }
    public void toast(String texto){
        Toast.makeText(this,texto,Toast.LENGTH_LONG).show();
    }
    public void onVerclicked(View view){
        toast("Ver click");
    }
    public void onDesClicked(View view){
        String url;
        switch (view.getId()){
            case R.id.ib_descargar_cardD1:
                url=getUrl(titulos[0], numeros[0]);
                new Requests(this,TaskType.GET_HTML1).execute(url);
                break;
            case R.id.ib_descargar_cardD2:
                url=getUrl(titulos[1], numeros[1]);
                new Requests(this,TaskType.GET_HTML1).execute(url);
                break;
            case R.id.ib_descargar_cardD3:
                url=getUrl(titulos[2], numeros[2]);
                new Requests(this,TaskType.GET_HTML1).execute(url);
                break;
            case R.id.ib_descargar_cardD4:
                url=getUrl(titulos[3], numeros[3]);
                new Requests(this,TaskType.GET_HTML1).execute(url);
                break;
            case R.id.ib_descargar_cardD5:
                url=getUrl(titulos[4], numeros[4]);
                new Requests(this,TaskType.GET_HTML1).execute(url);
                break;
            case R.id.ib_descargar_cardD6:
                url=getUrl(titulos[5], numeros[5]);
                new Requests(this,TaskType.GET_HTML1).execute(url);
                break;
            case R.id.ib_descargar_cardD7:
                url=getUrl(titulos[6], numeros[6]);
                new Requests(this,TaskType.GET_HTML1).execute(url);
                break;
            case R.id.ib_descargar_cardD8:
                url=getUrl(titulos[7], numeros[7]);
                new Requests(this,TaskType.GET_HTML1).execute(url);
                break;
            case R.id.ib_descargar_cardD9:
                url=getUrl(titulos[8], numeros[8]);
                new Requests(this,TaskType.GET_HTML1).execute(url);
                break;
            case R.id.ib_descargar_cardD10:
                url=getUrl(titulos[9], numeros[9]);
                new Requests(this,TaskType.GET_HTML1).execute(url);
                break;
            case R.id.ib_descargar_cardD11:
                url=getUrl(titulos[10], numeros[10]);
                new Requests(this,TaskType.GET_HTML1).execute(url);
                break;
            case R.id.ib_descargar_cardD12:
                url=getUrl(titulos[11], numeros[11]);
                new Requests(this,TaskType.GET_HTML1).execute(url);
                break;
            case R.id.ib_descargar_cardD13:
                url=getUrl(titulos[12], numeros[12]);
                new Requests(this,TaskType.GET_HTML1).execute(url);
                break;
            case R.id.ib_descargar_cardD14:
                url=getUrl(titulos[13], numeros[13]);
                new Requests(this,TaskType.GET_HTML1).execute(url);
                break;
            case R.id.ib_descargar_cardD15:
                url=getUrl(titulos[14], numeros[14]);
                new Requests(this,TaskType.GET_HTML1).execute(url);
                break;
            case R.id.ib_descargar_cardD16:
                url=getUrl(titulos[15], numeros[15]);
                new Requests(this,TaskType.GET_HTML1).execute(url);
                break;
            case R.id.ib_descargar_cardD17:
                url=getUrl(titulos[16], numeros[16]);
                new Requests(this,TaskType.GET_HTML1).execute(url);
                break;
            case R.id.ib_descargar_cardD18:
                url=getUrl(titulos[17], numeros[17]);
                new Requests(this,TaskType.GET_HTML1).execute(url);
                break;
            case R.id.ib_descargar_cardD19:
                url=getUrl(titulos[18], numeros[18]);
                new Requests(this,TaskType.GET_HTML1).execute(url);
                break;
            case R.id.ib_descargar_cardD20:
                url=getUrl(titulos[19], numeros[19]);
                new Requests(this,TaskType.GET_HTML1).execute(url);
                break;
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
    public void setDir(){
        if (isNetworkAvailable()){
            new Requests(context,TaskType.DIRECTORIO).execute("http://animeflv.net/api.php?accion=directorio");
        }else {
            if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                if (!mediaStorage.exists()) {
                    mediaStorage.mkdirs();
                }
            }
            File file = new File(Environment.getExternalStorageDirectory() + "/.Animeflv/cache/directorio.txt");
            String file_loc = Environment.getExternalStorageDirectory() + "/.Animeflv/cache/directorio.txt";
            if (file.exists()){
                Intent intent=new Intent(context,Directorio.class);
                startActivity(intent);
            }else {
                toast("No hay datos guardados");
            }
        }
    }
    public void setInfo(String aid){
        aidInfo=aid;
        SharedPreferences sharedPreferences=getSharedPreferences("data",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("aid",aidInfo);
        editor.commit();
        new Requests(this,TaskType.GET_INFO).execute("http://animeflv.net/api.php?accion=anime&aid="+aid);
    }
    public void actCacheInfo(String json){
        Bundle bundleInfo=new Bundle();
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!mediaStorage.exists()) {
                mediaStorage.mkdirs();
            }
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/.Animeflv/cache/"+aidInfo+".txt");
        String file_loc = Environment.getExternalStorageDirectory() + "/.Animeflv/cache/"+aidInfo+".txt";
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

        web=(WebView) findViewById(R.id.wv_inicio);
        web.getSettings().setJavaScriptEnabled(true);
        web.addJavascriptInterface(new JavaScriptInterface(context), "HtmlViewer");
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                web.loadUrl("javascript:window.HtmlViewer.showHTML" +
                        "('<html>'+document.getElementsByTagName('body')[0].innerHTML+'</html>');");
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
                web_Links.loadUrl("javascript:window.HtmlViewer.showHTMLD1" + "(document.getElementById('descargas_box').getElementsByTagName('a')[1].href);");
                web_Links.loadUrl("javascript:window.HtmlViewer.showHTMLD2" + "(document.getElementById('dlbutton').href);");
                if (url.contains("http://www.")) {
                    web_Links.loadUrl("javascript:(function(){" + "l=document.getElementById('skiplink');" + "e=document.createEvent('HTMLEvents');" + "e.initEvent('click',true,true);" + "l.dispatchEvent(e);" + "})()");
                }
            }
        });
        web_Links.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                new Main().toast("Descarga Iniciada");
                String fileName = url.substring(url.lastIndexOf("/") + 1);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDescription("Cecyt 3");
                request.setTitle(fileName);
                // in order for this if to run, you must use the android 3.2 to compile your app
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

                // get download service and enqueue file
                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);
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
        File file = new File(Environment.getExternalStorageDirectory() + "/.Animeflv/cache/inicio.txt");
        String file_loc = Environment.getExternalStorageDirectory() + "/.Animeflv/cache/inicio.txt";
        if (isNetworkAvailable()) {
            new Requests(this, TaskType.GET_INICIO).execute(inicio);
        }else {
            if (file.exists()) {
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
        isFirst();
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
        ftitulo=ftitulo.replace("!", "");
        ftitulo=ftitulo.replace("°", "");
        ftitulo=ftitulo.replace("&deg;", "");
        ftitulo=ftitulo.replace("(","");
        ftitulo=ftitulo.replace(")","");
        if (ftitulo.trim().equals("gintama")){ftitulo=ftitulo+"-2015";}
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
            new Requests(context, TaskType.VERSION).execute("https://raw.githubusercontent.com/jordyamc/Animeflv/master/app/version.html");
            new Requests(this, TaskType.GET_INICIO).execute(inicio);
        }else {
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
        }
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && net;
    }
    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
           if (descarga.exists()){
               Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                       .setDataAndType(Uri.fromFile(descarga),
                               "application/vnd.android.package-archive");
               startActivity(promptInstall);
           }
        }
    };
    @Override
    public void sendtext1(String data,TaskType taskType){
        if (taskType==TaskType.DIRECTORIO){
            if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                if (!mediaStorage.exists()) {
                    mediaStorage.mkdirs();
                }
            }
            File file = new File(Environment.getExternalStorageDirectory() + "/.Animeflv/cache/directorio.txt");
            String file_loc = Environment.getExternalStorageDirectory() + "/.Animeflv/cache/directorio.txt";
            if (isNetworkAvailable()) {
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
        if (taskType==TaskType.VERSION){
            Log.d("Version", Integer.toString(versionCode)+ " >> "+data.trim());
            if (versionCode>=Integer.parseInt(data.trim())){
                Log.d("Version","OK");
                getSharedPreferences("data",MODE_PRIVATE).edit().putBoolean("notVer",false);
            }else {
                Log.d("Version", "Actualizar");
                MaterialDialog dialog = new MaterialDialog.Builder(this)
                        .title("Nueva Version "+data.trim())
                        .content("Hay una nueva version disponible, porfavor instala para continuar.")
                        .titleColorRes(R.color.prim)
                        .autoDismiss(false)
                        .cancelable(false)
                        .backgroundColor(Color.WHITE)
                        .titleGravity(GravityEnum.CENTER)
                        .positiveText("Instalar")
                        .positiveColorRes(R.color.prim)
                        .negativeText("Salir")
                        .negativeColorRes(R.color.prim)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                if (descarga.exists()) {
                                    descarga.delete();
                                }
                                toast("Descarga Iniciada...");
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse("https://github.com/jordyamc/Animeflv/blob/master/app/app-release.apk?raw=true"));
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setDescription("Descargar Actualizacion");
                                request.setTitle("Animeflv");
                                request.setDestinationInExternalPublicDir("/.animeflv/cache", "Animeflv_Nver.apk");
                                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                manager.enqueue(request);
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                finish();
                            }
                        }).build();
                dialog.show();
            }
        }
        if(taskType == TaskType.GET_INICIO) {
            if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                if (!mediaStorage.exists()) {
                    mediaStorage.mkdirs();
                }
            }
            File file = new File(Environment.getExternalStorageDirectory() + "/.Animeflv/cache/inicio.txt");
            String file_loc = Environment.getExternalStorageDirectory() + "/.Animeflv/cache/inicio.txt";
            if (isNetworkAvailable()) {
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
            toast(html);
            Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(html));
            startActivity(intent);
        }
        @JavascriptInterface
        public void showHTMLD2(String html) {
            toast(html);
        }}
}
