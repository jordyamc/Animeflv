package knf.animeflv.info;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import knf.animeflv.ColorsRes;
import knf.animeflv.LoginServer;
import knf.animeflv.Parser;
import knf.animeflv.PicassoCache;
import knf.animeflv.R;
import knf.animeflv.Recyclers.AdapterInfoCapsMaterial;
import knf.animeflv.Recyclers.AdapterRel;
import knf.animeflv.ServerReload.Adapter.CustomRecycler;
import knf.animeflv.TaskType;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.NetworkUtils;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

public class InfoNewMaterial extends AppCompatActivity implements LoginServer.callback {
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.image)
    ImageView imageView;
    @Bind(R.id.rv)
    RecyclerView recyclerView;
    @Bind(R.id.app_bar_layout)
    AppBarLayout barLayout;
    @Bind(R.id.fab)
    FloatingActionButton button;
    @Bind(R.id.nested)
    NestedScrollView nestedScrollView;
    @Bind(R.id.info_descripcion)
    TextView txt_sinopsis;
    @Bind(R.id.titulo)
    TextView txt_titulo;
    @Bind(R.id.tipo)
    TextView txt_tipo;
    @Bind(R.id.estado)
    TextView txt_estado;
    @Bind(R.id.generos)
    TextView txt_generos;
    @Bind(R.id.debug_info)
    TextView txt_debug;
    @Bind(R.id.rv_relacionados)
    CustomRecycler rv_rel;
    @Bind(R.id.coordinator)
    CoordinatorLayout layout;
    String ext_storage_state = Environment.getExternalStorageState();
    File mediaStorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache");
    Parser parser = new Parser();
    MaterialDialog dialog;
    Spinner spinner;
    Context context;
    WebView webView;
    String aid;
    Menu Amenu;
    String json = "{}";
    boolean isInInfo = true;

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
        }
        return ret;
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

    public static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_layout_info_material);
        ButterKnife.bind(this);
        if (!isXLargeScreen(this)) { //set phones to portrait;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        setSupportActionBar(toolbar);
        aid = getIntent().getExtras().getString("aid");
        context = this;
        setCollapsingToolbarLayoutTitle(getIntent().getExtras().getString("title"));
        PicassoCache.getPicassoInstance(this).load(parser.getBaseUrl(TaskType.NORMAL, this) + "imagen.php?certificate=" + getCertificateSHA1Fingerprint() + "&hd=http://cdn.animeflv.net/img/portada/" + aid + ".jpg").error(R.drawable.ic_block_r).into(imageView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInInfo) {
                    button.setImageResource(R.drawable.information);
                    nestedScrollView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    barLayout.setExpanded(false, true);
                    isInInfo = false;
                } else {
                    button.setImageResource(R.drawable.playlist);
                    nestedScrollView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    scrollToTop();
                    isInInfo = true;
                }
            }
        });
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (ThemeUtils.isAmoled(this)) {
            layout.setBackgroundColor(ColorsRes.Negro(this));
            txt_sinopsis.setTextColor(getResources().getColor(R.color.blanco));
            TextView tit1 = (TextView) findViewById(R.id.info_titles1);
            TextView tit2 = (TextView) findViewById(R.id.info_titles2);
            TextView tit3 = (TextView) findViewById(R.id.info_titles3);
            TextView tit4 = (TextView) findViewById(R.id.info_titles4);
            TextView tit5 = (TextView) findViewById(R.id.info_titles5);
            tit1.setTextColor(getResources().getColor(R.color.blanco));
            tit2.setTextColor(getResources().getColor(R.color.blanco));
            tit3.setTextColor(getResources().getColor(R.color.blanco));
            tit4.setTextColor(getResources().getColor(R.color.blanco));
            tit5.setTextColor(getResources().getColor(R.color.blanco));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setNavigationBarColor(getResources().getColor(R.color.negro));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setNavigationBarColor(getResources().getColor(R.color.prim));
            }
        }

        int color = ThemeUtils.getAcentColor(this);
        txt_titulo.setTextColor(color);
        txt_tipo.setTextColor(color);
        txt_estado.setTextColor(color);
        txt_generos.setTextColor(color);
        txt_debug.setTextColor(color);
        if (NetworkUtils.isNetworkAvailable()) {
            getJsonfromApi();
        } else {
            getJsonfromFile();
        }
    }

    private void scrollToTop() {
        barLayout.setExpanded(true, true);
        /*CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) barLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        int[] consumed = new int[2];
        //behavior.onNestedPreScroll(layout, barLayout, null, 0, -1000, consumed);
        behavior.onNestedFling(layout, barLayout, null, 0, -10000, true);*/
        nestedScrollView.scrollTo(0, -1000);
    }

    private void getJsonfromApi() {
        if (new Parser().getTitCached(aid).equals("null")) {
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
                    Toaster.toast("No hay cache para mostrar");
                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Toaster.toast("No hay cache para mostrar");
                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Toaster.toast("No hay cache para mostrar");
                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toaster.toast("No hay cache para mostrar");
                    finish();
                }
            });
        } else {
            Log.d("Info", "API");
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            asyncHttpClient.setResponseTimeout(5000);
            asyncHttpClient.get(parser.getInicioUrl(TaskType.NORMAL, context) + "?url=" + parser.getUrlAnimeCached(aid) + "&certificate=" + parser.getCertificateSHA1Fingerprint(context), null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    setInfo(response.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.d("Info", "Cache");
                    getJsonfromFile();
                }
            });
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
        if (NetworkUtils.isNetworkAvailable() && !data.trim().equals("error")) {
            String trimed = data.trim();
            if (!file.exists()) {
                Log.d("Archivo:", "No existe");
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    Log.d("Archivo:", "Error al crear archivo");
                }

                if (FileUtil.isJSONValid(trimed)) {
                    FileUtil.writeToFile(trimed, file);
                    getJsonfromApi();
                } else {
                    Toaster.toast("No hay cache para mostrar");
                    finish();
                }
            } else {
                Log.d("Archivo", "Existe");
                String infile = getStringFromFile(file_loc);
                if (!infile.trim().equals(trimed)) {
                    if (FileUtil.isJSONValid(infile)) {
                        if (FileUtil.isJSONValid(trimed)) {
                            Log.d("Cargar", "Json nuevo");
                            FileUtil.writeToFile(trimed, file);
                            getJsonfromApi();
                        }
                    }
                } else {
                    Toaster.toast("No hay cache para mostrar");
                    finish();
                }
            }
        } else {
            Toaster.toast("No hay cache para mostrar");
            finish();
        }
    }

    public void getJsonfromFile() {
        String aid = getIntent().getExtras().getString("aid");
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!mediaStorage.exists()) {
                mediaStorage.mkdirs();
            }
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + aid + ".txt");
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + aid + ".txt";
        if (file.exists()) {
            Log.d("Archivo", "Existe");
            String infile = getStringFromFile(file_loc);
            setInfo(infile);
        } else {
            Toaster.toast("No hay cache para mostrar");
            finish();
        }
    }

    public String getJsonStringfromFile() {
        String aid = getIntent().getExtras().getString("aid");
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

    public void setInfo(final String json) {
        AnimeDetail animeDetail = new AnimeDetail(json);
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
            FileUtil.writeToFile(json, file);
            txt_sinopsis.setText(animeDetail.getSinopsis());
            txt_titulo.setText(animeDetail.getTitulo());
            txt_tipo.setText(animeDetail.getTid());
            txt_estado.setText(animeDetail.getEstado());
            txt_generos.setText(animeDetail.getGeneros());
            txt_debug.setText(aid);
            String[] urls = parser.urlsRel(json);
            if (urls.length == 0) {
                rv_rel.setVisibility(View.GONE);
            } else {
                rv_rel.setHasFixedSize(true);
                rv_rel.setLayoutManager(new LinearLayoutManager(this));
                List<String> titulos = parser.parseTitRel(json);
                List<String> tipos = parser.parseTiposRel(json);
                String[] aids = parser.parseAidRel(json);
                AdapterRel adapter = new AdapterRel(this, titulos, tipos, urls, aids);
                rv_rel.setAdapter(adapter);
            }
            nestedScrollView.setVisibility(View.VISIBLE);
            setRecyclerView(json);
        } else {
            Log.d("Archivo", "Existe");
            String infile = getStringFromFile(file_loc);
            if (json.equals(infile.trim())) {
                txt_sinopsis.setText(animeDetail.getSinopsis());
                txt_titulo.setText(animeDetail.getTitulo());
                txt_tipo.setText(animeDetail.getTid());
                txt_estado.setText(animeDetail.getEstado());
                txt_generos.setText(animeDetail.getGeneros());
                txt_debug.setText(aid);
                String[] urls = parser.urlsRel(json);
                if (urls.length == 0) {
                    rv_rel.setVisibility(View.GONE);
                } else {
                    rv_rel.setHasFixedSize(true);
                    rv_rel.setLayoutManager(new LinearLayoutManager(this));
                    List<String> titulos = parser.parseTitRel(json);
                    List<String> tipos = parser.parseTiposRel(json);
                    String[] aids = parser.parseAidRel(json);
                    AdapterRel adapter = new AdapterRel(this, titulos, tipos, urls, aids);
                    rv_rel.setAdapter(adapter);
                }
                nestedScrollView.setVisibility(View.VISIBLE);
                setRecyclerView(json);
            } else {
                FileUtil.writeToFile(json, file);
                txt_sinopsis.setText(animeDetail.getSinopsis());
                txt_titulo.setText(animeDetail.getTitulo());
                txt_tipo.setText(animeDetail.getTid());
                txt_estado.setText(animeDetail.getEstado());
                txt_generos.setText(animeDetail.getGeneros());
                txt_debug.setText(aid);
                String[] urls = parser.urlsRel(json);
                if (urls.length == 0) {
                    rv_rel.setVisibility(View.GONE);
                } else {
                    rv_rel.setHasFixedSize(true);
                    rv_rel.setLayoutManager(new LinearLayoutManager(this));
                    List<String> titulos = parser.parseTitRel(json);
                    List<String> tipos = parser.parseTiposRel(json);
                    String[] aids = parser.parseAidRel(json);
                    AdapterRel adapter = new AdapterRel(this, titulos, tipos, urls, aids);
                    rv_rel.setAdapter(adapter);
                }
                nestedScrollView.setVisibility(View.VISIBLE);
                setRecyclerView(json);
            }
        }
        scrollToTop();
    }

    private void setRecyclerView(final String json) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new AdapterInfoCapsMaterial(context, parser.parseNumerobyEID(json), aid, parser.parseEidsbyEID(json)));
    }

    private String getCertificateSHA1Fingerprint() {
        PackageManager pm = getPackageManager();
        String packageName = getPackageName();
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

    private void setCollapsingToolbarLayoutTitle(String title) {
        collapsingToolbarLayout.setTitle(title);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
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
    public void response(String data, TaskType taskType) {

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
                Toaster.toast("Favorito Eliminado");
                getSharedPreferences("data", MODE_PRIVATE).edit().putString("favoritos", builder.toString()).commit();
                String vistos = getSharedPreferences("data", MODE_PRIVATE).getString("vistos", "");
                if (!email_coded.equals("null") && !email_coded.equals("null")) {
                    new LoginServer(this, TaskType.UPDATE, null, null, null, null).execute(parser.getBaseUrl(TaskType.NORMAL, context) + "fav-server.php?certificate=" + getCertificateSHA1Fingerprint() + "&tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + builder.toString() + ":;:" + vistos);
                    new LoginServer(this, TaskType.UPDATE, null, null, null, null).execute(parser.getBaseUrl(TaskType.SECUNDARIA, context) + "fav-server.php?certificate=" + getCertificateSHA1Fingerprint() + "&tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + builder.toString() + ":;:" + vistos);
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
                Toaster.toast("Favorito Agregado");
                getSharedPreferences("data", MODE_PRIVATE).edit().putString("favoritos", builderNo.toString()).commit();
                String vistos1 = getSharedPreferences("data", MODE_PRIVATE).getString("vistos", "");
                if (!email_coded.equals("null") && !email_coded.equals("null")) {
                    new LoginServer(this, TaskType.UPDATE, null, null, null, null).execute(parser.getBaseUrl(TaskType.NORMAL, context) + "fav-server.php?certificate=" + getCertificateSHA1Fingerprint() + "&tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + builderNo.toString() + ":;:" + vistos1);
                    new LoginServer(this, TaskType.UPDATE, null, null, null, null).execute(parser.getBaseUrl(TaskType.SECUNDARIA, context) + "fav-server.php?certificate=" + getCertificateSHA1Fingerprint() + "&tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + builderNo.toString() + ":;:" + vistos1);
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
                final List<String> caps = parser.parseNumerobyEID(getJsonStringfromFile());
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
    public void onBackPressed() {
        if (!isInInfo) {
            button.setImageResource(R.drawable.playlist);
            nestedScrollView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            scrollToTop();
            isInInfo = true;
        } else {
            finish();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!isXLargeScreen(getApplicationContext())) {
            return;
        }
    }
}
