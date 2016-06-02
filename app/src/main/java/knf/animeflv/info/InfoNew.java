package knf.animeflv.info;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import knf.animeflv.Application;
import knf.animeflv.ColorsRes;
import knf.animeflv.LoginServer;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Requests;
import knf.animeflv.TaskType;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.ThemeUtils;

/**
 * Created by Jordy on 07/03/2016.
 */
public class InfoNew extends AppCompatActivity implements Requests.callback, LoginServer.callback {
    Parser parser = new Parser();
    Toolbar toolbar;
    String ext_storage_state = Environment.getExternalStorageState();
    File mediaStorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache");
    Menu Amenu;
    String aid;
    String titulo = "";
    String id = "";
    MaterialDialog dialog;
    Spinner spinner;
    Context context;
    WebView webView;
    SmartTabLayout viewPagerTab;

    public static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
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

    public static String getStringFromFile(String filePath) {
        String ret = "";
        try {
            File fl = new File(filePath);
            FileInputStream fin = new FileInputStream(fl);
            ret = convertStreamToString(fin);
            fin.close();
        } catch (IOException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("is_amoled", false)) {
            setTheme(R.style.AppThemeDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
        context = this;
        getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("cambio", false).apply();
        if (!isXLargeScreen(getApplicationContext())) { //set phones to portrait;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.dark));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.prim));
        }
        toolbar = (Toolbar) findViewById(R.id.info_toolbar);
        viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab1);
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("is_amoled", false)) {
            toolbar.setBackgroundColor(getResources().getColor(android.R.color.black));
            toolbar.getRootView().setBackgroundColor(getResources().getColor(android.R.color.black));
            viewPagerTab.setBackgroundColor(getResources().getColor(android.R.color.black));
            viewPagerTab.setSelectedIndicatorColors(getResources().getColor(R.color.prim));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(getResources().getColor(R.color.negro));
                getWindow().setNavigationBarColor(getResources().getColor(R.color.negro));
            }
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back_r);
        upArrow.setColorFilter(getResources().getColor(R.color.blanco), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Bundle bundleR = getIntent().getExtras();
        aid = bundleR.getString("aid", "1");
        Application application = (Application) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Info " + aid);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!mediaStorage.exists()) {
                mediaStorage.mkdirs();
            }
        }
        if (!isNetworkAvailable()) {
            File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + aid + ".txt");
            String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + aid + ".txt";
            if (file.exists()) {
                String infile = getStringFromFile(file_loc);
                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putString("titInfo", parser.getTit(infile)).apply();
                Log.d("Load","Title 1");
                getSupportActionBar().setTitle(FileUtil.corregirTit(parser.getTit(infile)));
                Bundle bundle = new Bundle();
                bundle.putString("aid", aid);
                FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                        getSupportFragmentManager(), FragmentPagerItems.with(this)
                        .add("INFORMACION", AnimeInfo.class, bundle)
                        .add("EPISODIOS", InfoCap.class, bundle)
                        .create());
                ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager1);
                viewPager.setOffscreenPageLimit(2);
                viewPager.setAdapter(adapter);
                viewPagerTab.setViewPager(viewPager);
            } else {
                toast("No hay cache para mostrar");
                finish();
            }
        } else {
            if (parser.getTitCached(aid).equals("null")) {
                getSupportActionBar().setTitle("Cargando...");
                loadMainDir();
            } else {
                LoadData();
            }
        }
    }

    public void loadMainDir() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setResponseTimeout(10000);
        asyncHttpClient.get(parser.getDirectorioUrl(TaskType.NORMAL, context) + "?certificate=" + parser.getCertificateSHA1Fingerprint(context), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                loadDir(response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                loadDir(response.toString());
                toast("No hay cache para mostrar");
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                toast("No hay cache para mostrar");
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                toast("No hay cache para mostrar");
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                toast("No hay cache para mostrar");
                finish();
            }
        });
    }

    private void LoadData() {
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putString("titInfo", FileUtil.corregirTit(parser.getTitCached(aid))).apply();
        Log.d("Load","Title 2");
        getSupportActionBar().setTitle(FileUtil.corregirTit(parser.getTitCached(aid)));
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setResponseTimeout(5000);
        asyncHttpClient.get(parser.getInicioUrl(TaskType.NORMAL, context) + "?url=" + parser.getUrlAnimeCached(aid) + "&certificate=" + parser.getCertificateSHA1Fingerprint(context), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                setJson(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                onError(throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                onError(throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                onError(throwable);
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

                if (FileUtil.isJSONValid(trimed)) {
                    writeToFile(trimed, file);
                    LoadData();
                } else {
                    toast("No hay cache para mostrar");
                    finish();
                }
            } else {
                Log.d("Archivo", "Existe");
                String infile = getStringFromFile(file_loc);
                if (!infile.trim().equals(trimed)) {
                    if (FileUtil.isJSONValid(infile)) {
                        if (FileUtil.isJSONValid(trimed)) {
                            Log.d("Cargar", "Json nuevo");
                            writeToFile(trimed, file);
                            LoadData();
                        }
                    }
                } else {
                    toast("No hay cache para mostrar");
                    finish();
                }
            }
        } else {
            toast("No hay cache para mostrar");
            finish();
        }
    }

    private void onError(Throwable throwable) {
        File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + aid + ".txt");
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + aid + ".txt";
        if (file.exists()) {
            Log.d("Cargar", "Error");
            String infile = getStringFromFile(file_loc);
            SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
            editor.putString("titInfo", parser.getTit(infile)).apply();
            titulo = FileUtil.corregirTit(parser.getTit(infile));
            Log.d("Load","Title 3");
            getSupportActionBar().setTitle(titulo);
            Bundle bundle = new Bundle();
            bundle.putString("aid", aid);
            FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                    getSupportFragmentManager(), FragmentPagerItems.with(context)
                    .add("INFORMACION", AnimeInfo.class, bundle)
                    .add("EPISODIOS", InfoCap.class, bundle)
                    .create());
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager1);
            viewPager.setOffscreenPageLimit(2);
            viewPager.setAdapter(adapter);
            viewPagerTab.setViewPager(viewPager);
        } else {
            toast("No hay cache para mostrar");
            toast(throwable.getMessage());
            finish();
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

    public void toast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    public String getJson() {
        String json = "";
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!mediaStorage.exists()) {
                mediaStorage.mkdirs();
            }
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + aid + ".txt");
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + aid + ".txt";
        if (file.exists()) {
            Log.d("Archivo", "Existe");
            json = getStringFromFile(file_loc);
        }
        return json;
    }

    private void setJson(JSONObject json) {
        String s = json.toString();
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!mediaStorage.exists()) {
                mediaStorage.mkdirs();
            }
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + aid + ".txt");
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + aid + ".txt";
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            writeToFile(s, file);
            Bundle bundle = new Bundle();
            bundle.putString("aid", aid);
            FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                    getSupportFragmentManager(), FragmentPagerItems.with(context)
                    .add("INFORMACION", AnimeInfo.class, bundle)
                    .add("EPISODIOS", InfoCap.class, bundle)
                    .create());
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager1);
            viewPager.setOffscreenPageLimit(2);
            viewPager.setAdapter(adapter);
            viewPagerTab.setViewPager(viewPager);
        } else {
            Log.d("Archivo", "Existe");
            String infile = getStringFromFile(file_loc);
            if (s.equals(infile.trim())) {
                Bundle bundle = new Bundle();
                bundle.putString("aid", aid);
                FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                        getSupportFragmentManager(), FragmentPagerItems.with(context)
                        .add("INFORMACION", AnimeInfo.class, bundle)
                        .add("EPISODIOS", InfoCap.class, bundle)
                        .create());
                ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager1);
                viewPager.setOffscreenPageLimit(2);
                viewPager.setAdapter(adapter);
                viewPagerTab.setViewPager(viewPager);
            } else {
                writeToFile(s, file);
                Bundle bundle = new Bundle();
                bundle.putString("aid", aid);
                FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                        getSupportFragmentManager(), FragmentPagerItems.with(context)
                        .add("INFORMACION", AnimeInfo.class, bundle)
                        .add("EPISODIOS", InfoCap.class, bundle)
                        .create());
                ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager1);
                viewPager.setOffscreenPageLimit(2);
                viewPager.setAdapter(adapter);
                viewPagerTab.setViewPager(viewPager);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Amenu = menu;
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        String fav = sharedPreferences.getString("favoritos", "");
        String[] favoritos = {};
        favoritos = fav.split(":::");
        Boolean isfav = false;
        for (String favo : favoritos) {
            if (!favo.equals("")) {
                if (Integer.parseInt(favo) == Integer.parseInt(aid)) {
                    getMenuInflater().inflate(R.menu.menu_fav_si, menu);
                    isfav = true;
                    break;
                }
            }
        }
        if (isfav) {
            Amenu.clear();
            getMenuInflater().inflate(R.menu.menu_fav_si, menu);
        } else {
            Amenu.clear();
            getMenuInflater().inflate(R.menu.menu_fav_no, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String email_coded = PreferenceManager.getDefaultSharedPreferences(this).getString("login_email_coded", "null");
        String pass_coded = PreferenceManager.getDefaultSharedPreferences(this).getString("login_pass_coded", "null");
        switch (item.getItemId()) {
            case R.id.favorito_si:
                SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
                String fav = sharedPreferences.getString("favoritos", "");
                String[] favoritos = {};
                favoritos = fav.split(":::");
                List<String> list = new ArrayList<String>();
                for (String i : favoritos) {
                    if (!i.equals("")) {
                        if (Integer.parseInt(i) != Integer.parseInt(aid)) {
                            list.add(i);
                        }
                    }
                }
                favoritos = new String[list.size()];
                list.toArray(favoritos);
                StringBuilder builder = new StringBuilder();
                for (String i : favoritos) {
                    builder.append(":::" + i);
                }
                toast("Favorito Eliminado");
                getSharedPreferences("data", MODE_PRIVATE).edit().putString("favoritos", builder.toString()).commit();
                String vistos = getSharedPreferences("data", MODE_PRIVATE).getString("vistos", "");
                if (!email_coded.equals("null") && !email_coded.equals("null")) {
                    new LoginServer(this, TaskType.UPDATE, null, null, null, null).execute(parser.getBaseUrl(TaskType.NORMAL, context) + "fav-server.php?certificate=" + parser.getCertificateSHA1Fingerprint(context) + "&tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + builder.toString() + ":;:" + vistos);
                    new LoginServer(this, TaskType.UPDATE, null, null, null, null).execute(parser.getBaseUrl(TaskType.SECUNDARIA, context) + "fav-server.php?certificate=" + parser.getCertificateSHA1Fingerprint(context) + "&tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + builder.toString() + ":;:" + vistos);
                }
                Amenu.clear();
                getMenuInflater().inflate(R.menu.menu_fav_no, Amenu);
                getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("cambio_fav", true).apply();
                break;
            case R.id.favorito_no:
                String[] favoritosNo = {getSharedPreferences("data", MODE_PRIVATE).getString("favoritos", "")};
                String titNo = getSharedPreferences("data", MODE_PRIVATE).getString("aid", "");
                List<String> Listno = new ArrayList<String>(Arrays.asList(favoritosNo));
                Listno.add(aid);
                favoritos = new String[Listno.size()];
                Listno.toArray(favoritos);
                StringBuilder builderNo = new StringBuilder();
                for (String i : favoritos) {
                    builderNo.append(":::" + i);
                }
                toast("Favorito Agregado");
                getSharedPreferences("data", MODE_PRIVATE).edit().putString("favoritos", builderNo.toString()).commit();
                String vistos1 = getSharedPreferences("data", MODE_PRIVATE).getString("vistos", "");
                if (!email_coded.equals("null") && !email_coded.equals("null")) {
                    new LoginServer(this, TaskType.UPDATE, null, null, null, null).execute(parser.getBaseUrl(TaskType.NORMAL, context) + "fav-server.php?certificate=" + parser.getCertificateSHA1Fingerprint(context) + "&tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + builderNo.toString() + ":;:" + vistos1);
                    new LoginServer(this, TaskType.UPDATE, null, null, null, null).execute(parser.getBaseUrl(TaskType.SECUNDARIA, context) + "fav-server.php?certificate=" + parser.getCertificateSHA1Fingerprint(context) + "&tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + builderNo.toString() + ":;:" + vistos1);
                }
                Amenu.clear();
                getMenuInflater().inflate(R.menu.menu_fav_si, Amenu);
                getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("cambio_fav", true).apply();
                break;
            case R.id.comentarios:
                dialog = new MaterialDialog.Builder(this)
                        .title("COMENTARIOS")
                        .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                        .titleGravity(GravityEnum.CENTER)
                        .customView(R.layout.comentarios, false)
                        .positiveText("SALIR")
                        .build();
                spinner = (Spinner) dialog.getCustomView().findViewById(R.id.comentarios_box_cap);
                final List<String> caps = parser.parseNumerobyEID(getJson());
                String[] array = new String[caps.size()];
                caps.toArray(array);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array);
                spinner.setAdapter(arrayAdapter);
                webView = (WebView) dialog.getCustomView().findViewById(R.id.comentarios_box);
                webView.getSettings().setJavaScriptEnabled(true);
                String newUA = "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0";
                webView.getSettings().setUserAgentString(newUA);
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                });
                String url = "";
                try {
                    url = "https://www.facebook.com/plugins/comments.php?api_key=133687500123077&channel_url=http%3A%2F%2Fstatic.ak.facebook.com%2Fconnect%2Fxd_arbiter%2Fjb3BUxkAISL.js%3Fversion%3D41%23cb%3Dfbb6634b4%26domain%3Danimeflv.com%26origin%3Dhttp%253A%252F%252Fanimeflv.com%252Ff1449cd23c%26relation%3Dparent.parent&href=" + URLEncoder.encode(new Parser().getUrlCached(aid, caps.get(0).substring(caps.get(0).lastIndexOf(" ") + 1)), "UTF-8") + "&locale=es_LA&numposts=15&sdk=joey&version=v2.3&width=1000";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("Comentarios", url);
                webView.loadUrl(url);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String urlch = "";
                        try {
                            urlch = "https://www.facebook.com/plugins/comments.php?api_key=133687500123077&channel_url=http%3A%2F%2Fstatic.ak.facebook.com%2Fconnect%2Fxd_arbiter%2Fjb3BUxkAISL.js%3Fversion%3D41%23cb%3Dfbb6634b4%26domain%3Danimeflv.com%26origin%3Dhttp%253A%252F%252Fanimeflv.com%252Ff1449cd23c%26relation%3Dparent.parent&href=" + URLEncoder.encode(new Parser().getUrlCached(aid, caps.get(position).substring(caps.get(position).lastIndexOf(" ") + 1)), "UTF-8") + "&locale=es_LA&numposts=15&sdk=joey&version=v2.3&width=1000";
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.d("Comentarios", urlch);
                        webView.loadUrl(urlch);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                dialog.show();
                break;
        }
        return true;
    }

    @Override
    public void sendtext1(String data, TaskType taskType) {
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putString("titInfo", parser.getTit(data)).apply();
        Log.d("Load","Title 4");
        getSupportActionBar().setTitle(FileUtil.corregirTit(parser.getTit(data)));
        titulo = parser.getTit(data);
        id = parser.getAID(data);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (!isXLargeScreen(getApplicationContext())) {
            return;
        }
    }

    @Override
    public void response(String data, TaskType taskType) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        final String email_coded = PreferenceManager.getDefaultSharedPreferences(this).getString("login_email_coded", "null");
        final String pass_coded = PreferenceManager.getDefaultSharedPreferences(this).getString("login_pass_coded", "null");
        String Svistos = getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", "");
        String favoritos = getSharedPreferences("data", MODE_PRIVATE).getString("favoritos", "");
        Boolean cambio = getSharedPreferences("data", MODE_PRIVATE).getBoolean("cambio", false);
        if (!email_coded.equals("null") && !email_coded.equals("null") && cambio) {
            new LoginServer(this, TaskType.UPDATE, null, null, null, null).execute(parser.getBaseUrl(TaskType.NORMAL, context) + "fav-server.php?certificate=" + parser.getCertificateSHA1Fingerprint(context) + "&tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + favoritos + ":;:" + Svistos);
            new LoginServer(this, TaskType.UPDATE, null, null, null, null).execute(parser.getBaseUrl(TaskType.SECUNDARIA, context) + "fav-server.php?certificate=" + parser.getCertificateSHA1Fingerprint(context) + "&tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + favoritos + ":;:" + Svistos);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}