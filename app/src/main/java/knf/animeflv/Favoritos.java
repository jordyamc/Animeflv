package knf.animeflv;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import knf.animeflv.Directorio.AnimeClass;
import knf.animeflv.Recyclers.AdapterFavs;

/**
 * Created by Jordy on 23/08/2015.
 */
public class Favoritos extends AppCompatActivity implements RequestFav.callback, LoginServer.callback, Requests.callback, RequestFavSort.callback {
    RecyclerView recyclerView;
    Toolbar toolbar;
    Toolbar ltoolbar;
    List<String> aids;
    List<String> Naids=new ArrayList<String>();
    String fav;
    String[] favoritos={};
    Context context;
    boolean shouldExecuteOnResume;
    Handler handler = new Handler();
    Parser parser = new Parser();
    MaterialDialog dialog;
    RequestFav favo;
    Boolean sorted = false;
    Boolean paused = false;
    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ActualizarFavoritos();
            handler.postDelayed(this, 1000);
        }
    };

    public static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int accent = preferences.getInt("accentColor", ColorsRes.Naranja(this));
        if (preferences.getBoolean("is_amoled", false)) {
            if (accent == ColorsRes.Rojo(this)) {
                setTheme(R.style.AppThemeDarkRojo);
            }
            if (accent == ColorsRes.Naranja(this)) {
                setTheme(R.style.AppThemeDarkNaranja);
            }
            if (accent == ColorsRes.Gris(this)) {
                setTheme(R.style.AppThemeDarkGris);
            }
            if (accent == ColorsRes.Verde(this)) {
                setTheme(R.style.AppThemeDarkVerde);
            }
            if (accent == ColorsRes.Rosa(this)) {
                setTheme(R.style.AppThemeDarkRosa);
            }
            if (accent == ColorsRes.Morado(this)) {
                setTheme(R.style.AppThemeDarkMorado);
            }
        } else {
            if (accent == ColorsRes.Rojo(this)) {
                setTheme(R.style.AppThemeRojo);
            }
            if (accent == ColorsRes.Naranja(this)) {
                setTheme(R.style.AppThemeNaranja);
            }
            if (accent == ColorsRes.Gris(this)) {
                setTheme(R.style.AppThemeGris);
            }
            if (accent == ColorsRes.Verde(this)) {
                setTheme(R.style.AppThemeVerde);
            }
            if (accent == ColorsRes.Rosa(this)) {
                setTheme(R.style.AppThemeRosa);
            }
            if (accent == ColorsRes.Morado(this)) {
                setTheme(R.style.AppThemeMorado);
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anime_favs);
        if (!isXLargeScreen(getApplicationContext())) { //set phones to portrait;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            ltoolbar=(Toolbar) findViewById(R.id.ltoolbar_fav);
        }
        context=this;
        shouldExecuteOnResume = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.dark));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.prim));
        }
        toolbar=(Toolbar) findViewById(R.id.favs_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Favoritos");
        if (isXLargeScreen(context)){
            ltoolbar.setNavigationIcon(R.drawable.ic_back_r);
            ltoolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("is_amoled", false)) {
            toolbar.setBackgroundColor(getResources().getColor(android.R.color.black));
            toolbar.getRootView().setBackgroundColor(getResources().getColor(R.color.negro));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(getResources().getColor(R.color.negro));
                getWindow().setNavigationBarColor(getResources().getColor(R.color.negro));
            }
        }
        final String email_coded=PreferenceManager.getDefaultSharedPreferences(this).getString("login_email_coded", "null");
        final String pass_coded=PreferenceManager.getDefaultSharedPreferences(this).getString("login_pass_coded", "null");
        if (!email_coded.equals("null")&&!email_coded.equals("null")) {
            new LoginServer(this, TaskType.GET_FAV_SL, null, null, null, null).execute(new Parser().getBaseUrl(TaskType.NORMAL, context) + "fav-server.php?tipo=get&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&certificate=" + getCertificateSHA1Fingerprint());
        }
        getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("cambio_fav", false).apply();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                init();
            }
        },500);
        handler.postDelayed(runnable, 1000);
    }

    public void ActualizarFavoritos() {
        if (isNetworkAvailable()) {
            String email_coded = PreferenceManager.getDefaultSharedPreferences(this).getString("login_email_coded", "null");
            String pass_coded = PreferenceManager.getDefaultSharedPreferences(this).getString("login_pass_coded", "null");
            if (!email_coded.equals("null") && !email_coded.equals("null")) {
                new Requests(this, TaskType.GET_FAV).execute(new Parser().getBaseUrl(TaskType.NORMAL, context) + "fav-server.php?tipo=get&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&certificate=" + getCertificateSHA1Fingerprint());
            }
        }
    }

    public void init(){
        SharedPreferences sharedPreferences=getSharedPreferences("data", MODE_PRIVATE);
        fav=sharedPreferences.getString("favoritos", "");
        favoritos=fav.split(":::");
        Log.d("favoritos",fav);
        aids=new ArrayList<String>();
        for (String i:favoritos){
            if (!i.equals("")) {
                aids.add(i);
            }
        }
        favoritos=new String[aids.size()];
        aids.toArray(favoritos);
        recyclerView=(RecyclerView) findViewById(R.id.rv_favs);
        if (aids.size()==0){
            Toast.makeText(context,"No hay favoritos",Toast.LENGTH_LONG).show();
        }else {
            if (!paused) {
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                dialog = new MaterialDialog.Builder(context)
                        .content("Actualizando Favoritos")
                        .progress(true, 0)
                        .cancelable(true)
                        .cancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                finish();
                                favo.cancel(true);
                            }
                        })
                        .build();
                favo = new RequestFav(this, TaskType.SORT_NORMAL, dialog,aids);
                favo.executeOnExecutor(threadPoolExecutor);
                dialog.show();
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        init();
                    }
                }, 1000);
            }
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
                break;
        }
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && net;
    }

    @Override
    public void favCall(String data,TaskType taskType){
        dialog.dismiss();
        if (!data.trim().equals("")) {
            String[] crop = data.split(":::");
            List<String> titulos = new ArrayList<String>();
            for (String i : crop) {
                if (!i.trim().equals("")) {
                    titulos.add(i);
                }
            }
            List<String> links = new ArrayList<String>();
            for (String aid : aids) {
                /*if (isNetworkAvailable()) {
                    links.add("http://cdn.animeflv.net/img/portada/thumb_80/" + aid + ".jpg");
                }else {*/
                    File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/"+aid+".txt");
                    if (file.exists()){
                        links.add("http://cdn.animeflv.net/img/portada/thumb_80/" + aid + ".jpg");
                    }
                //}
            }
            Log.d("Ntitulos", Integer.toString(titulos.size()));
            Log.d("Naids", Integer.toString(aids.size()));
            Log.d("Nlinks", Integer.toString(links.size()));
            if (!isNetworkAvailable()){
                if (favoritos.length!=links.size())
                Toast.makeText(context,"Sin conexion, cargando favoritos con cache disponible",Toast.LENGTH_SHORT).show();
            }
            AdapterFavs adapter = new AdapterFavs(context, titulos, aids, links);
            recyclerView.setAdapter(adapter);
        }else {
            Toast.makeText(context,"Error de red",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        if (!isXLargeScreen(getApplicationContext()) ) {
            return;
        }
    }

    @Override
    public void response(String data, TaskType taskType) {
        if (taskType == TaskType.UPDATE) {
            sorted = false;
        }
    }

    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void sendtext1(String data, TaskType taskType) {
        if (taskType == TaskType.GET_FAV) {
            if (isJSONValid(data)) {
                String favoritos = parser.getUserFavs(data.trim());
                String visto = parser.getUserVistos(data.trim());
                if (visto.equals("")) {
                    String favs = getSharedPreferences("data", MODE_PRIVATE).getString("favoritos", "");
                    if (!favs.equals(favoritos) && sorted) {
                        getSharedPreferences("data", MODE_PRIVATE).edit().putString("favoritos", favoritos).commit();
                        init();
                    }
                } else {
                    String favs = getSharedPreferences("data", MODE_PRIVATE).getString("favoritos", "");
                    if (!favs.equals(favoritos) && !sorted) {
                        getSharedPreferences("data", MODE_PRIVATE).edit().putString("favoritos", favoritos).commit();
                        init();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(shouldExecuteOnResume){
            paused = false;
            Boolean cambiado = getSharedPreferences("data", MODE_PRIVATE).getBoolean("cambio_fav", false);
            if (cambiado) {
                init();
                getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("cambio_fav", false).apply();
            }
        } else{
            shouldExecuteOnResume = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (shouldExecuteOnResume) {
            paused = true;
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sort_fav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        fav = sharedPreferences.getString("favoritos", "");
        String[] sem = fav.split(":::");
        Log.d("favoritos", fav);
        aids = new ArrayList<>();
        for (String i : sem) {
            if (!i.equals("")) {
                aids.add(i);
            }
        }
        favoritos = new String[aids.size()];
        aids.toArray(favoritos);
        switch (item.getItemId()) {
            case R.id.sort_alph:
                new RequestFavSort(context, TaskType.SORT_ALPH).execute(favoritos);
                break;
            case R.id.sort_num:
                new RequestFavSort(context, TaskType.SORT_NUM).execute(favoritos);
                break;
        }
        return true;
    }

    public void savefavs(List<String> favs) {
        sorted = true;
        String fin = "";
        for (String aid : favs) {
            fin = fin + ":::" + aid;
        }
        getSharedPreferences("data", MODE_PRIVATE).edit().putString("favoritos", fin).apply();
        final String email_coded = PreferenceManager.getDefaultSharedPreferences(this).getString("login_email_coded", "null");
        final String pass_coded = PreferenceManager.getDefaultSharedPreferences(this).getString("login_pass_coded", "null");
        String Svistos = getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", "");
        if (!email_coded.equals("null") && !email_coded.equals("null")) {
            new LoginServer(this, TaskType.UPDATE, null, null, null, null).execute(parser.getBaseUrl(TaskType.NORMAL, context) + "fav-server.php?certificate=" + getCertificateSHA1Fingerprint() + "&tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + fin + ":;:" + Svistos);
            new LoginServer(this, TaskType.UPDATE, null, null, null, null).execute(parser.getBaseUrl(TaskType.SECUNDARIA, context) + "fav-server.php?certificate=" + getCertificateSHA1Fingerprint() + "&tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + fin + ":;:" + Svistos);
        }
    }

    @Override
    public void favCallSort(List<AnimeClass> list, TaskType taskType) {
        if (taskType == TaskType.SORT_ALPH) {
            List<AnimeClass> alph = list;
            Collections.sort(alph, new AnimeCompare());
            List<String> links = new ArrayList<>();
            List<String> aids = new ArrayList<>();
            List<String> titulos = new ArrayList<>();
            for (AnimeClass an : alph) {
                titulos.add(an.getNombre());
                aids.add(an.getAid());
                links.add("http://cdn.animeflv.net/img/portada/thumb_80/" + an.getAid() + ".jpg");
            }
            AdapterFavs adapter = new AdapterFavs(context, titulos, aids, links);
            recyclerView.setAdapter(adapter);
            savefavs(aids);
        }

        if (taskType == TaskType.SORT_NUM) {
            List<AnimeClass> num = list;
            Collections.sort(num, new AnimeAidCompare());
            List<String> links = new ArrayList<>();
            List<String> aids = new ArrayList<>();
            List<String> titulos = new ArrayList<>();
            for (AnimeClass an : num) {
                titulos.add(an.getNombre());
                aids.add(an.getAid());
                links.add("http://cdn.animeflv.net/img/portada/thumb_80/" + an.getAid() + ".jpg");
            }
            AdapterFavs adapter = new AdapterFavs(context, titulos, aids, links);
            recyclerView.setAdapter(adapter);
            savefavs(aids);
        }
    }
}
