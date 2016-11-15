package knf.animeflv;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import knf.animeflv.Directorio.AnimeClass;
import knf.animeflv.LoginActivity.LoginServer;
import knf.animeflv.Recyclers.AdapterFavs;
import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.NetworkUtils;
import knf.animeflv.Utils.ThemeUtils;

public class Favoritos extends AppCompatActivity implements RequestFav.callback, RequestFavSort.callback, LoginServer.ServerInterface {
    RecyclerView recyclerView;
    Toolbar toolbar;
    Toolbar ltoolbar;
    List<String> aids;
    String fav;
    String[] favoritos = {};
    Activity context;
    boolean shouldExecuteOnResume;
    Handler handler = new Handler();
    Parser parser = new Parser();
    MaterialDialog dialog;
    RequestFav favo;
    Boolean sorted = false;
    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!sorted)
                ActualizarFavoritos();
            handler.postDelayed(this, 1000);
        }
    };

    public static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anime_favs);
        if (!isXLargeScreen(getApplicationContext())) { //set phones to portrait;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            ltoolbar = (Toolbar) findViewById(R.id.ltoolbar_fav);
        }
        context = this;
        shouldExecuteOnResume = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.dark));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.prim));
        }
        toolbar = (Toolbar) findViewById(R.id.favs_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Favoritos");
        if (isXLargeScreen(context)) {
            ltoolbar.setNavigationIcon(R.drawable.ic_back_r);
            ltoolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (isXLargeScreen(this)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    ThemeUtils.setStatusBarPadding(this, ltoolbar);
                    getWindow().setFlags(
                            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                }
            }
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (ThemeUtils.isAmoled(this)) {
            toolbar.setBackgroundColor(getResources().getColor(android.R.color.black));
            if (!isXLargeScreen(this)) {
                toolbar.getRootView().setBackgroundColor(getResources().getColor(R.color.negro));
            } else {
                toolbar.getRootView().setBackgroundColor(ColorsRes.Prim(this));
                findViewById(R.id.cardMain).setBackgroundColor(ColorsRes.Negro(this));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (!isXLargeScreen(this)) {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.negro));
                    getWindow().setNavigationBarColor(getResources().getColor(R.color.negro));
                } else {
                    getWindow().setStatusBarColor(ColorsRes.Prim(this));
                    getWindow().setNavigationBarColor(ColorsRes.Transparent(this));
                }
            }
        }
        String email = PreferenceManager.getDefaultSharedPreferences(this).getString("login_email", "null");
        final String email_coded = PreferenceManager.getDefaultSharedPreferences(this).getString("login_email_coded", "null");
        final String pass_coded = PreferenceManager.getDefaultSharedPreferences(this).getString("login_pass_coded", "null");
        if (!email_coded.equals("null") && !email_coded.equals("null")) {
            LoginServer.login(this, email_coded, pass_coded, email, this);
        }
        getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("cambio_fav", false).apply();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                init();
            }
        }, 500);
        handler.postDelayed(runnable, 1);
    }

    public void ActualizarFavoritos() {
        if (NetworkUtils.isNetworkAvailable()) {
            String email = PreferenceManager.getDefaultSharedPreferences(this).getString("login_email", "null");
            String email_coded = PreferenceManager.getDefaultSharedPreferences(this).getString("login_email_coded", "null");
            String pass_coded = PreferenceManager.getDefaultSharedPreferences(this).getString("login_pass_coded", "null");
            if (!email_coded.equals("null") && !email_coded.equals("null")) {
                LoginServer.login(this, email_coded, pass_coded, email, this);
            }
        }
    }

    public void init() {
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        fav = sharedPreferences.getString("favoritos", "");
        favoritos = fav.split(":::");
        Log.d("favoritos", fav);
        aids = new ArrayList<String>();
        for (String i : favoritos) {
            if (!i.equals("") && !i.equals("null")) {
                aids.add(i);
            }
        }
        favoritos = new String[aids.size()];
        aids.toArray(favoritos);
        recyclerView = (RecyclerView) findViewById(R.id.rv_favs);
        if (aids.size() == 0) {
            Toast.makeText(context, "No hay favoritos", Toast.LENGTH_LONG).show();
        } else {
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            dialog = new MaterialDialog.Builder(context)
                    .content("Actualizando Favoritos")
                    .progress(true, 0)
                    .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                    .cancelable(true)
                    .cancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            finish();
                            favo.cancel(true);
                        }
                    }).build();
            favo = new RequestFav(this, TaskType.SORT_NORMAL, dialog, aids);
            favo.executeOnExecutor(threadPoolExecutor);
            dialog.show();
        }
        if (NetworkUtils.isNetworkAvailable()) {
            LoginServer.RefreshData(this);
        }
    }

    @Override
    public void favCall(String data, TaskType taskType) {
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
                File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + aid + ".txt");
                if (file.exists()) {
                    links.add("http://cdn.animeflv.net/img/portada/thumb_80/" + aid + ".jpg");
                }
            }
            Log.d("Ntitulos", Integer.toString(titulos.size()));
            Log.d("Naids", Integer.toString(aids.size()));
            Log.d("Nlinks", Integer.toString(links.size()));
            if (!NetworkUtils.isNetworkAvailable()) {
                if (favoritos.length != links.size())
                    Toast.makeText(context, "Sin conexion, cargando favoritos con cache disponible", Toast.LENGTH_SHORT).show();
            }
            AdapterFavs adapter = new AdapterFavs(context, titulos, aids, links);
            recyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (!isXLargeScreen(getApplicationContext())) {
            return;
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
    protected void onResume() {
        super.onResume();
        if (shouldExecuteOnResume) {
            handler.postDelayed(runnable, 1);
            Boolean cambiado = getSharedPreferences("data", MODE_PRIVATE).getBoolean("cambio_fav", false);
            if (cambiado) {
                init();
                getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("cambio_fav", false).apply();
            }
        } else {
            shouldExecuteOnResume = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (shouldExecuteOnResume) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(ThemeUtils.isAmoled(this) ? R.menu.menu_sort_fav : R.menu.menu_sort_fav_dark, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        fav = sharedPreferences.getString("favoritos", "");
        String[] sem = fav.split(":::");
        aids = new ArrayList<>();
        for (String i : sem) {
            if (!i.equals("") && !i.equals("null")) {
                aids.add(i);
            }
        }
        favoritos = new String[aids.size()];
        aids.toArray(favoritos);
        switch (item.getItemId()) {
            case R.id.sort_alph:
                new RequestFavSort(context, TaskType.SORT_ALPH, favoritos).executeOnExecutor(ExecutorManager.getExecutor());
                break;
            case R.id.sort_num:
                new RequestFavSort(context, TaskType.SORT_NUM, favoritos).executeOnExecutor(ExecutorManager.getExecutor());
                break;
        }
        sorted = true;
        return true;
    }

    public void savefavs(List<String> favs) {
        String fin = "";
        for (String aid : favs) {
            fin = fin + ":::" + aid;
        }
        getSharedPreferences("data", MODE_PRIVATE).edit().putString("favoritos", fin).commit();
        LoginServer.RefreshData(this, new LoginServer.RefreshInterface() {
            @Override
            public void onFinishRefresh() {
                sorted = false;
            }
        });
    }

    @Override
    public void favCallSort(List<AnimeClass> list, TaskType taskType, final MaterialDialog dialogo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialogo.setContent("Finalizando...");
            }
        });
        if (taskType == TaskType.SORT_ALPH) {
            List<AnimeClass> alph = AnimeSorter.sortByName(list);
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
            dialogo.dismiss();
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
            dialogo.dismiss();
        }
    }

    @Override
    public void onServerResponse(JSONObject object) {
        try {
            if (object.getString("response").equals("ok")) {
                String favoritos = Parser.getTrimedList(parser.getUserFavs(object.toString()), ":::");
                String visto = parser.getUserVistos(object.toString());
                if (visto.equals("")) {
                    String favs = Parser.getTrimedList(getSharedPreferences("data", MODE_PRIVATE).getString("favoritos", ""), ":::");
                    if (!favs.equals(favoritos)) {
                        getSharedPreferences("data", MODE_PRIVATE).edit().putString("favoritos", favoritos).commit();
                        init();
                    }
                } else {
                    String favs = Parser.getTrimedList(getSharedPreferences("data", MODE_PRIVATE).getString("favoritos", ""), ":::");
                    if (!favs.equals(favoritos)) {
                        getSharedPreferences("data", MODE_PRIVATE).edit().putString("favoritos", favoritos).commit();
                        init();
                    }
                    String vistos = getSharedPreferences("data", MODE_PRIVATE).getString("vistos", "");
                    try {
                        if (!vistos.equals(visto)) {
                            getSharedPreferences("data", MODE_PRIVATE).edit().putString("vistos", visto).commit();
                            String[] v = visto.split(";;;");
                            for (String s : v) {
                                getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean(s, true).commit();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            Log.e("GetFavs", e.getMessage());
        }
    }

    @Override
    public void onServerError() {

    }
}
