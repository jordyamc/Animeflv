package knf.animeflv.info;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
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
import com.github.zawadz88.activitychooser.MaterialActivityChooserBuilder;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.AutoEmision.AutoEmisionHelper;
import knf.animeflv.AutoEmision.EmObj;
import knf.animeflv.Cloudflare.Bypass;
import knf.animeflv.ColorsRes;
import knf.animeflv.Directorio.AnimeClass;
import knf.animeflv.Directorio.DB.DirectoryDB;
import knf.animeflv.Directorio.DB.DirectoryHelper;
import knf.animeflv.Directorio.Directorio;
import knf.animeflv.DownloadService.DownloaderService;
import knf.animeflv.FavSyncro;
import knf.animeflv.Favorites.FavoriteHelper;
import knf.animeflv.Favorites.FavotiteDB;
import knf.animeflv.JsonFactory.BaseGetter;
import knf.animeflv.JsonFactory.JsonTypes.ANIME;
import knf.animeflv.JsonFactory.JsonTypes.DIRECTORIO;
import knf.animeflv.JsonFactory.OfflineGetter;
import knf.animeflv.LoginServer;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Suggestions.Algoritm.SuggestionAction;
import knf.animeflv.Suggestions.Algoritm.SuggestionHelper;
import knf.animeflv.TaskType;
import knf.animeflv.Utils.CacheManager;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.Keys;
import knf.animeflv.Utils.MainStates;
import knf.animeflv.Utils.NetworkUtils;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.TrackingHelper;
import knf.animeflv.info.fragments.FragmentCaps;
import knf.animeflv.info.fragments.FragmentInfo;
import xdroid.toaster.Toaster;

@SuppressWarnings("WeakerAccess")
public class InfoFragments extends AppCompatActivity implements LoginServer.callback {
    public static String ACTION_EDITED = "knf.animeflv.emision.EMISION_EDITED";
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
    @BindView(R.id.viewpagertab)
    SmartTabLayout tabLayout;
    @BindView(R.id.pager)
    ViewPager pager;
    String ext_storage_state = Environment.getExternalStorageState();
    File mediaStorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache");
    Parser parser = new Parser();
    MaterialDialog dialog;
    Spinner spinner;
    Activity context;
    WebView webView;
    String aid;
    int position = -1;
    String u;
    String global_json;
    boolean isInInfo = true;
    boolean infoSeted = false;
    String status_string = "OK";
    private JSONArray eps;
    private BroadcastReceiver receiver;

    private FragmentCaps fragmentCaps;
    private FragmentInfo fragmentInfo;

    public static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.layout_info_fragments);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        context = this;
        try {
            aid = getIntent().getExtras().getString("aid");
            if (aid == null) {
                u = getIntent().getDataString();
                String url = u.replace("http://animeflv.net/", "");
                String[] data = url.split("/");
                Log.e("Debug", data[2]);
                aid = DirectoryHelper.get(this).getAid(data[2]);
                Log.e("Debug", aid);
                if (aid != null) {
                    setCollapsingToolbarLayoutTitle(DirectoryHelper.get(this).getTitle(aid));
                } else {
                    Toaster.toast("Error al abrir informacion desde link!!!");
                    finish();
                }
            } else {
                position = Integer.parseInt(getIntent().getExtras().getString("position", "-1"));
                setCollapsingToolbarLayoutTitle(getIntent().getExtras().getString("title"));
                u = DirectoryHelper.get(context).getAnimeUrl(aid);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toaster.toast("Error al abrir informacion desde link!!!");
            finish();
        }
        button.setImageResource(FavoriteHelper.isFav(this, aid) ? R.drawable.star_on : R.drawable.star_off);
        TrackingHelper.track(this, TrackingHelper.INFO + aid);
        new CacheManager().portada(this, aid, imageView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean isFav = FavoriteHelper.isFav(InfoFragments.this, aid);
                SuggestionHelper.register(InfoFragments.this, aid, isFav ? SuggestionAction.UNFAV : SuggestionAction.FAV);
                FavoriteHelper.setFav(InfoFragments.this, aid, !isFav, new FavotiteDB.updateDataInterface() {
                    @Override
                    public void onFinish() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toaster.toast("Favorito " + (isFav ? "Eliminado" : "Agregado"));
                                button.setImageResource(FavoriteHelper.isFav(InfoFragments.this, aid) ? R.drawable.star_on : R.drawable.star_off);
                            }
                        });
                        FavSyncro.updateFavs(InfoFragments.this);
                    }
                });
            }
        });
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toaster.toast(status_string);
                return true;
            }
        });
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ThemeUtils.Theme theme = ThemeUtils.Theme.create(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        layout.setBackgroundColor(theme.background);
        collapsingToolbarLayout.setContentScrimColor(theme.primary);
        collapsingToolbarLayout.setStatusBarScrimColor(ColorsRes.Transparent(context));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(theme.primaryDark);
            getWindow().setNavigationBarColor(theme.primary);
        }
        MainStates.setListing(false);
        imageView.setOnClickListener(getExpandListener());
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final CacheManager cacheManager = new CacheManager();
                cacheManager.invalidatePortada(aid);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cacheManager.portada(InfoFragments.this, aid, imageView);
                        Toaster.toast("Portada recreada");
                    }
                });
                return true;
            }
        });
        toolbar.setOnClickListener(getExpandListener());
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloaderService.RECEIVER_ACTION_ERROR);
        registerReceiver(getReceiver(), filter);
        if (savedInstanceState != null) {
            isInInfo = savedInstanceState.getBoolean("isInInfo", true);
            if (!isInInfo)
                position = savedInstanceState.getInt("f_item", -1);
        }
        getJsonfromApi();
    }

    private BroadcastReceiver getReceiver() {
        if (receiver == null)
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(DownloaderService.RECEIVER_ACTION_ERROR))
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (fragmentCaps != null)
                                    fragmentCaps.resetList();
                            }
                        });
                }
            };
        return receiver;
    }

    private boolean isEmision() {
        if (global_json != null) {
            try {
                JSONObject object = new JSONObject(global_json);
                switch (object.getString("fecha_fin").trim()) {
                    case "0000-00-00":
                    case "prox":
                        return true;
                    default:
                        return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    private void shareAid() {
        TrackingHelper.action(this, aid);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, u);
        intent.setType("text/plain");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            startActivity(Intent.createChooser(intent, "Compartir Link"));
        } else {
            new MaterialActivityChooserBuilder(context)
                    .withIntent(intent)
                    .withTitle("Compartir Link")
                    .show();
        }
    }

    private View.OnClickListener getExpandListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barLayout.setExpanded(true, true);
            }
        };
    }

    private void getJsonfromApi() {
        SuggestionHelper.register(InfoFragments.this, aid, SuggestionAction.EXPLORE);
        if (DirectoryHelper.get(this).getTitle(aid).equals("null")) {
            BaseGetter.getJson(this, new DIRECTORIO(), new BaseGetter.AsyncProgressDBInterface() {
                @Override
                public void onFinish(List<AnimeClass> list) {
                    if (DirectoryHelper.get(InfoFragments.this).getTitle(aid).equals("null")) {
                        Toaster.toast("Error al actualizar directorio");
                        finish();
                    } else {
                        getJsonfromApi();
                    }
                }

                @Override
                public void onProgress(int progress) {

                }

                @Override
                public void onError(Throwable throwable) {
                    Toaster.toast("Error al actualizar directorio");
                    finish();
                }
            });
        } else {
            BaseGetter.getJson(this, new ANIME(Integer.parseInt(aid)), new BaseGetter.AsyncInterface() {
                @Override
                public void onFinish(String json) {
                    if (json.startsWith("error")) {
                        switch (json.split(":")[1]) {
                            case "404":
                                Snackbar.make(layout, "Error en directorio", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("RECREAR", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Keys.Dirs.CACHE_DIRECTORIO.delete();
                                                new DirectoryDB(context).reset();
                                                finish();
                                                startActivity(new Intent(InfoFragments.this, Directorio.class));
                                            }
                                        }).show();
                                break;
                            default:
                                json = OfflineGetter.getAnime(new ANIME(Integer.parseInt(aid)));
                                status_string = "CACHE";
                                break;
                        }
                    }
                    if (!json.equals("null")) {
                        global_json = json;
                        setInfo(json);
                        try {
                            eps = new JSONObject(json).getJSONArray("episodios");
                        } catch (Exception e) {
                            e.printStackTrace();
                            eps = null;
                        }
                    } else {
                        Toaster.toast("No hay cache para mostrar");
                        finish();
                    }
                }
            });
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
            return FileUtil.getStringFromFile(file_loc);
        }
        return "{}";
    }

    public void setInfo(final String json) {
        infoSeted = true;
        supportInvalidateOptionsMenu();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                bundle.putString("aid", aid);
                bundle.putString("json", json);
                bundle.putInt("position", position);
                FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                        getSupportFragmentManager(), FragmentPagerItems.with(InfoFragments.this)
                        .add("INFO", FragmentInfo.class, bundle)
                        .add("CAPITULOS", FragmentCaps.class, bundle)
                        .create());
                pager.setAdapter(adapter);
                tabLayout.setDistributeEvenly(false);
                tabLayout.setViewPager(pager);
                tabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i1) {

                    }

                    @Override
                    public void onPageSelected(int i) {
                        if (i == 1) {
                            isInInfo = false;
                            barLayout.setExpanded(false, true);
                        } else {
                            isInInfo = true;
                            barLayout.setExpanded(true, true);
                        }
                        supportInvalidateOptionsMenu();
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {

                    }
                });
                tabLayout.setOnTabClickListener(new SmartTabLayout.OnTabClickListener() {
                    @Override
                    public void onTabClicked(int position) {
                        if (position == 1) {
                            isInInfo = false;
                            barLayout.setExpanded(false, true);
                        } else {
                            isInInfo = true;
                            barLayout.setExpanded(true, true);
                        }
                        supportInvalidateOptionsMenu();
                    }
                });
                fragmentInfo = (FragmentInfo) adapter.getPage(0);
                fragmentCaps = (FragmentCaps) adapter.getPage(1);
                if (!infoSeted)
                    if (position != -1)
                        pager.setCurrentItem(1);
            }
        });
    }

    private void toogleFragments() {
        try {
            if (isInInfo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pager.setCurrentItem(1);
                    }
                });

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pager.setCurrentItem(0);
                    }
                });
            }
            supportInvalidateOptionsMenu();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setCollapsingToolbarLayoutTitle(String title) {
        collapsingToolbarLayout.setTitle(title);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
    }

    private void showComments() {
        if (eps != null && eps.length() > 0) {
            dialog = new MaterialDialog.Builder(this)
                    .title("COMENTARIOS")
                    .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                    .titleGravity(GravityEnum.CENTER)
                    .customView(R.layout.comentarios, false)
                    .positiveText("SALIR")
                    .build();
            spinner = dialog.getCustomView().findViewById(R.id.comentarios_box_cap);
            final List<String> caps = parser.parseNumerobyEID(getJsonStringfromFile());
            String[] array = new String[caps.size()];
            caps.toArray(array);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array);
            spinner.setAdapter(arrayAdapter);
            webView = dialog.getCustomView().findViewById(R.id.comentarios_box);
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
                String num = caps.get(0).substring(caps.get(0).lastIndexOf(" ") + 1);
                url = "https://www.facebook.com/plugins/comments.php?api_key=133687500123077&channel_url=http%3A%2F%2Fstatic.ak.facebook.com%2Fconnect%2Fxd_arbiter%2Fjb3BUxkAISL.js%3Fversion%3D41%23cb%3Dfbb6634b4%26domain%3Danimeflv.com%26origin%3Dhttp%253A%252F%252Fanimeflv.com%252Ff1449cd23c%26relation%3Dparent.parent&href=" + URLEncoder.encode(DirectoryHelper.get(this).getEpUrl(aid, num, eps.getJSONObject(0).getString("sid")), "UTF-8") + "&locale=es_LA&numposts=15&sdk=joey&version=v2.3&width=1000";
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
                        urlch = "https://www.facebook.com/plugins/comments.php?api_key=133687500123077&channel_url=http%3A%2F%2Fstatic.ak.facebook.com%2Fconnect%2Fxd_arbiter%2Fjb3BUxkAISL.js%3Fversion%3D41%23cb%3Dfbb6634b4%26domain%3Danimeflv.com%26origin%3Dhttp%253A%252F%252Fanimeflv.com%252Ff1449cd23c%26relation%3Dparent.parent&href=" + URLEncoder.encode(DirectoryHelper.get(InfoFragments.this).getEpUrl(aid, caps.get(position).substring(caps.get(position).lastIndexOf(" ") + 1), eps.getJSONObject(position).getString("sid")), "UTF-8") + "&locale=es_LA&numposts=15&sdk=joey&version=v2.3&width=1000";
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
        } else {
            Toaster.toast("No se pueden mostrar los comentarios");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            if (isInInfo) {
                if (infoSeted) {
                    getMenuInflater().inflate(R.menu.menu_fav_em, menu);
                } else {
                    getMenuInflater().inflate(R.menu.menu_fav_no_em, menu);
                }
                if (infoSeted) {
                    if (isEmision()) {
                        if (AutoEmisionHelper.isAnimeAdded(this, aid))
                            menu.findItem(R.id.emision).setIcon(R.drawable.alarm_added);
                    } else {
                        menu.removeItem(R.id.emision);
                    }
                }
            } else {
                getMenuInflater().inflate(R.menu.menu_fast_seen, menu);
            }
            ThemeUtils.setMenuColor(menu, ColorsRes.Blanco(this));
        } catch (Exception e) {
            e.printStackTrace();
            Toaster.toast("Error al crear Menu");
            finish();
        }
        return true;
    }

    @Override
    public void response(String data, TaskType taskType) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.emision:
                    if (AutoEmisionHelper.isAnimeAdded(this, aid)) {
                        new MaterialDialog.Builder(this)
                                .content("¿Quieres eliminar este anime de la lista de emision?")
                                .positiveText("aceptar")
                                .negativeText("cancelar")
                                .neutralText("editar")
                                .autoDismiss(false)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                                        SuggestionHelper.register(InfoFragments.this, aid, SuggestionAction.UNFOLLOW);
                                        AutoEmisionHelper.removeAnimeFromList(InfoFragments.this, aid, new EmisionEditDialog.SearchListener() {
                                            @Override
                                            public void OnResponse(EmObj obj) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                                                    }
                                                });
                                                sendBroadcast(new Intent(InfoFragments.ACTION_EDITED));
                                                supportInvalidateOptionsMenu();
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void OnError() {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                                                    }
                                                });
                                                dialog.dismiss();
                                                supportInvalidateOptionsMenu();
                                                Toaster.toast("Error al eliminar");
                                            }
                                        });
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                        EmisionEditDialog.create(aid, false, new EmisionEditDialog.onEditListener() {
                                            @Override
                                            public void onEdit() {
                                                supportInvalidateOptionsMenu();
                                            }
                                        }).show(getSupportFragmentManager(), "dialog");
                                    }
                                }).build().show();
                    } else {
                        EmisionEditDialog.create(aid, true, new EmisionEditDialog.onEditListener() {
                            @Override
                            public void onEdit() {
                                SuggestionHelper.register(InfoFragments.this, aid, SuggestionAction.FOLLOW);
                                supportInvalidateOptionsMenu();
                            }
                        }).show(getSupportFragmentManager(), "dialog");
                    }
                    break;
                case R.id.comentarios:
                    showComments();
                    break;
                case R.id.sel_all:
                    fragmentCaps.setallAsSeen();
                    break;
                case R.id.sel_none:
                    fragmentCaps.setallAsNotSeen();
                    break;
                case R.id.share:
                    shareAid();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (fragmentCaps != null) {
            outState.putBoolean("isInInfo", isInInfo);
            outState.putInt("f_item", fragmentCaps.getFirstItem());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            isInInfo = savedInstanceState.getBoolean("isInInfo", true);
            if (!isInInfo)
                position = savedInstanceState.getInt("f_item", -1);
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (!isInInfo) {
            toogleFragments();
        } else {
            super.onBackPressed();
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
        if (keyCode == KeyEvent.KEYCODE_BACK && fragmentInfo != null && fragmentCaps != null) {
            toogleFragments();
        } else if (keyCode == KeyEvent.KEYCODE_BACK && fragmentInfo == null && fragmentCaps == null) {
            Toaster.toast("Espera a que cargue la informaciom");
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        if (NetworkUtils.isNetworkAvailable()) {
            FavSyncro.updateServer(this);
        }
        unregisterReceiver(getReceiver());
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fragmentCaps != null) {
            Bypass.check(this, null);
            fragmentCaps.resetList();
        }
    }
}
