package knf.animeflv.info;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import knf.animeflv.ColorsRes;
import knf.animeflv.FavSyncro;
import knf.animeflv.JsonFactory.BaseGetter;
import knf.animeflv.JsonFactory.JsonTypes.ANIME;
import knf.animeflv.LoginServer;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.TaskType;
import knf.animeflv.Utils.CacheManager;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.Keys;
import knf.animeflv.Utils.Logger;
import knf.animeflv.Utils.MainStates;
import knf.animeflv.Utils.NetworkUtils;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.objects.User;
import knf.animeflv.info.fragments.FragmentCaps;
import knf.animeflv.info.fragments.FragmentInfo;
import xdroid.toaster.Toaster;

import static knf.animeflv.Utils.Keys.Login.EMAIL_NORMAL;

@SuppressWarnings("WeakerAccess")
public class InfoFragments extends AppCompatActivity implements LoginServer.callback {
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.image)
    ImageView imageView;

    @BindView(R.id.app_bar_layout)
    AppBarLayout barLayout;
    @BindView(R.id.fab)
    FloatingActionButton button;
    @BindView(R.id.coordinator)
    CoordinatorLayout layout;
    String ext_storage_state = Environment.getExternalStorageState();
    File mediaStorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache");
    Parser parser = new Parser();
    MaterialDialog dialog;
    Spinner spinner;
    Activity context;
    WebView webView;
    String aid;
    String u;
    Menu Amenu;
    String json = "{}";
    boolean isInInfo = true;
    boolean infoSeted = false;

    private FragmentInfo fragmentInfo;
    private FragmentCaps fragmentCaps;

    private FragmentManager fragmentManager;

    public static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_info_fragments);
        ButterKnife.bind(this);
        if (!isXLargeScreen(this)) { //set phones to portrait;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        setSupportActionBar(toolbar);
        context = this;
        aid = getIntent().getExtras().getString("aid");
        if (aid == null) {
            u = getIntent().getDataString();
            String url = u.replace("http://animeflv.net/", "").replace(".html", "");
            String[] data = url.split("/");
            aid = parser.getAidCached(data[1]);
            if (aid != null) {
                setCollapsingToolbarLayoutTitle(parser.getTitCached(aid));
            } else {
                Toaster.toast("Error al abrir informacion!!!");
                finish();
            }
        } else {
            setCollapsingToolbarLayoutTitle(getIntent().getExtras().getString("title"));
            u = parser.getUrlAnimeCached(aid);
        }
        new CacheManager().portada(this, aid, imageView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toogleFragments();
                    }
                });
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setNavigationBarColor(getResources().getColor(R.color.negro));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setNavigationBarColor(getResources().getColor(R.color.prim));
            }
        }
        button.hide();
        MainStates.setListing(false);
        imageView.setOnClickListener(getExpandListener());
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isUserAdmin()) {
                    new MaterialDialog.Builder(InfoFragments.this)
                            .title("Administrador")
                            .titleGravity(GravityEnum.CENTER)
                            .content("Seleccione accion")
                            .positiveText("editar")
                            .neutralText("compartir")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    if (NetworkUtils.isNetworkAvailable()) {
                                        EmisionEditDialog.create(aid).show(getSupportFragmentManager(), "dialog");
                                    } else {
                                        Toaster.toast("Se necesita internet!!!");
                                    }
                                }
                            })
                            .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    shareAid();
                                }
                            }).build().show();
                } else {
                    shareAid();
                }
                return true;
            }
        });
        toolbar.setOnClickListener(getExpandListener());
        getJsonfromApi();
    }

    private void shareAid() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, u);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "Compartir Link"));
    }

    private View.OnClickListener getExpandListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barLayout.setExpanded(true, true);
            }
        };
    }

    private boolean isUserAdmin() {
        String json_admin = PreferenceManager.getDefaultSharedPreferences(context).getString(Keys.Extra.JSON_ADMINS, "null");
        if (FileUtil.isJSONValid(json_admin)) {
            try {
                return getUser(new JSONObject(json_admin)).isAdmin();
            } catch (JSONException e) {
                return false;
            }
        }
        return false;
    }

    private User getUser(JSONObject object) {
        String email = PreferenceManager.getDefaultSharedPreferences(context).getString(EMAIL_NORMAL, "null");
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

    private void scrollToTop() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                barLayout.setExpanded(true, true);
                if (fragmentInfo != null)
                    fragmentInfo.scrollTop();
            }
        });
    }

    private void getJsonfromApi() {
        if (new Parser().getTitCached(aid).equals("null")) {
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            asyncHttpClient.setResponseTimeout(10000);
            asyncHttpClient.get(parser.getDirectorioUrl(TaskType.NORMAL, context) + "?certificate=" + Parser.getCertificateSHA1Fingerprint(context), null, new JsonHttpResponseHandler() {
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
            BaseGetter.getJson(this, new ANIME(Integer.parseInt(aid)), new BaseGetter.AsyncInterface() {
                @Override
                public void onFinish(String json) {
                    if (!json.equals("null")) {
                        setInfo(json);
                    } else {
                        Toaster.toast("No hay cache para mostrar");
                        finish();
                    }
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
                String infile = FileUtil.getStringFromFile(file_loc);
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
            json = FileUtil.getStringFromFile(file_loc);
        }
        return json;
    }

    public void setInfo(final String json) {
        if (!infoSeted) {
            infoSeted = true;
            fragmentInfo = FragmentInfo.get(aid, json);
            fragmentCaps = FragmentCaps.get(aid, json);
            fragmentInfo.setReference(this);
            fragmentCaps.setReference(this);
            String pos = getIntent().getStringExtra("position");
            final int position = pos == null ? -1 : Integer.valueOf(pos);
            addFragments(position);
            fragmentInfo.startAnimation(position);
            scrollToTop();
        }
    }

    private void toogleFragments() {
        FragmentTransaction transaction = getManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (isInInfo) {
            barLayout.setExpanded(false, true);
            button.setImageResource(R.drawable.information);
            transaction.hide(fragmentInfo);
            transaction.show(fragmentCaps);
            fragmentCaps.resetListButton();
            isInInfo = false;
        } else {
            scrollToTop();
            button.setImageResource(R.drawable.playlist);
            transaction.hide(fragmentCaps);
            transaction.show(fragmentInfo);
            isInInfo = true;
        }
        transaction.commit();
    }

    private FragmentManager getManager() {
        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager();
        }
        return fragmentManager;
    }

    private void addFragments(int position) {
        FragmentTransaction transaction = getManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(R.id.root, fragmentCaps, "caps");
        transaction.add(R.id.root, fragmentInfo, "info");
        transaction.commit();
        transaction = getManager().beginTransaction();
        if (position == -1) {
            scrollToTop();
            isInInfo = true;
            button.setImageResource(R.drawable.playlist);
            transaction.hide(fragmentCaps);
            transaction.show(fragmentInfo);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    barLayout.setExpanded(false, true);
                }
            });
            isInInfo = false;
            button.setImageResource(R.drawable.information);
            transaction.hide(fragmentInfo);
            transaction.show(fragmentCaps);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.show();
            }
        });
        transaction.commit();
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
                getSharedPreferences("data", MODE_PRIVATE).edit().putString("favoritos", builder.toString()).apply();
                FavSyncro.updateServer(InfoFragments.this);
                Amenu.clear();
                getMenuInflater().inflate(R.menu.menu_fav_no, Amenu);
                getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("cambio_fav", true).apply();
                break;
            case R.id.favorito_no:
                String[] favoritosNo = {getSharedPreferences("data", MODE_PRIVATE).getString("favoritos", "")};
                List<String> Listno = new ArrayList<String>(Arrays.asList(favoritosNo));
                Listno.add(aid);
                favoritos = new String[Listno.size()];
                Listno.toArray(favoritos);
                StringBuilder builderNo = new StringBuilder();
                for (String i : favoritos) {
                    builderNo.append(":::" + i);
                }
                Toaster.toast("Favorito Agregado");
                getSharedPreferences("data", MODE_PRIVATE).edit().putString("favoritos", builderNo.toString()).apply();
                FavSyncro.updateServer(InfoFragments.this);
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
            toogleFragments();
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

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        super.onKeyLongPress(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            toogleFragments();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        if (NetworkUtils.isNetworkAvailable()) {
            FavSyncro.updateServer(this);
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fragmentCaps != null)
            fragmentCaps.resetList();
    }
}
