package knf.animeflv.ServerReload;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import knf.animeflv.ColorsRes;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.ServerReload.Adapter.AdapterAdminRecs;
import knf.animeflv.ServerReload.Adapter.CustomRecycler;
import knf.animeflv.ServerReload.Adapter.RecObject;
import knf.animeflv.TaskType;
import knf.animeflv.Utils.Logger;
import knf.animeflv.Utils.ThemeUtils;

public class manualServerReload extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ib_admin_recientes_reload)
    ImageButton rec_reload;
    @BindView(R.id.tv_admin_recientes_last)
    TextView last_rec;
    @BindView(R.id.tv_admin_recientes_state)
    TextView state_rec;
    @BindView(R.id.ib_admin_dir_reload)
    ImageButton dir_reload;
    @BindView(R.id.tv_admin_dir_state)
    TextView state_dir;
    @BindView(R.id.admin_reload)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.rv_rec_animes)
    CustomRecycler rec_animes;
    @BindView(R.id.card_main)
    CardView card_rec;
    @BindView(R.id.card_animes)
    CardView card_animes;
    @BindView(R.id.card_dir)
    CardView card_dir;
    @BindView(R.id.tv_admin_recientes)
    TextView tit_rec;
    @BindView(R.id.tv_admin_dir)
    TextView tit_dir;
    private Parser parser = new Parser();
    private TaskType NORMAL = TaskType.NORMAL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_server_reload);
        ButterKnife.bind(this);
        setUpListeners();
        reloadAll();
    }

    @SuppressWarnings("all")
    private void setUpListeners() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Actualizar Server");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rec_reload.setColorFilter(ThemeUtils.getAcentColor(this));
        dir_reload.setColorFilter(ThemeUtils.getAcentColor(this));
        refreshLayout.setOnRefreshListener(this);
        if (ThemeUtils.isAmoled(this)){
            toolbar.getRootView().setBackgroundColor(ColorsRes.Negro(this));
            toolbar.setBackgroundColor(ColorsRes.Negro(this));
            card_rec.setBackgroundColor(ColorsRes.Prim(this));
            card_dir.setBackgroundColor(ColorsRes.Prim(this));
            card_animes.setBackgroundColor(ColorsRes.Prim(this));
            tit_rec.setTextColor(ColorsRes.SecondaryTextDark(this));
            tit_dir.setTextColor(ColorsRes.SecondaryTextDark(this));
            last_rec.setTextColor(ColorsRes.SecondaryTextDark(this));
            state_dir.setTextColor(ColorsRes.Blanco(this));
            state_rec.setTextColor(ColorsRes.Blanco(this));
        }else {
            toolbar.getRootView().setBackgroundColor(ColorsRes.Blanco(this));
            card_rec.setBackgroundColor(ColorsRes.Blanco(this));
            card_dir.setBackgroundColor(ColorsRes.Blanco(this));
            card_animes.setBackgroundColor(ColorsRes.Blanco(this));
            tit_rec.setTextColor(ColorsRes.SecondaryTextLight(this));
            tit_dir.setTextColor(ColorsRes.SecondaryTextLight(this));
            last_rec.setTextColor(ColorsRes.SecondaryTextLight(this));
            state_dir.setTextColor(ColorsRes.Negro(this));
            state_rec.setTextColor(ColorsRes.Negro(this));
        }
        rec_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state_rec.setText("Cargando...");
                state_rec.setTextColor(ColorsRes.Prim(manualServerReload.this));
                reloadRec(true);
            }
        });
        dir_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state_dir.setText("Cargando...");
                state_dir.setTextColor(ColorsRes.Prim(manualServerReload.this));
                reloadDir(true);
            }
        });
    }

    private void reloadAll() {
        reloadRec(false);
        reloadAnimesRec();
        reloadDir(false);
    }

    private void reloadRec(final boolean bypass) {
        String url = parser.getInicioUrl(NORMAL, this) + "?certificate=" + parser.getCertificateSHA1Fingerprint(this);
        if (bypass) {
            url += "&bypass";
        }
        Log.d("Load url", url);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setConnectTimeout(15000);
        client.get(url, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        last_rec.setVisibility(View.GONE);
                        state_rec.setTextColor(ColorsRes.Rojo(manualServerReload.this));
                        state_rec.setText("ERROR!");
                    }
                });
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject object = new JSONObject(responseString);
                    final boolean isCache = object.getString("cache").equals("1");
                    final String last = UTCtoLocal(object.getString("last"));
                    last_rec.setVisibility(View.VISIBLE);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            last_rec.setText(last);
                            if (isCache) {
                                state_rec.setTextColor(ColorsRes.Amarillo(manualServerReload.this));
                                state_rec.setText("MODO CACHE");
                            } else {
                                state_rec.setTextColor(ColorsRes.Verde(manualServerReload.this));
                                state_rec.setText("OK");
                            }
                        }
                    });
                    if (bypass) {
                        Log.d("Recientes", "Load bypass");
                    }
                } catch (JSONException e) {
                    Logger.Error(manualServerReload.this.getClass(), e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            last_rec.setVisibility(View.GONE);
                            state_rec.setTextColor(ColorsRes.Rojo(manualServerReload.this));
                            state_rec.setText("ERROR!");
                        }
                    });
                }
            }
        });
    }

    private void reloadAnimesRec() {
        String url = parser.getInicioUrl(NORMAL, this) + "?certificate=" + parser.getCertificateSHA1Fingerprint(this);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setConnectTimeout(5000);
        client.get(url, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Logger.Error(manualServerReload.this.getClass(), throwable);
                rec_animes.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                rec_animes.setVisibility(View.VISIBLE);
                try {
                    JSONObject object = new JSONObject(responseString);
                    JSONArray array = object.getJSONArray("lista");
                    List<RecObject> list = new ArrayList<RecObject>();
                    for (int i = 0; i < array.length(); i++) {
                        list.add(new RecObject(array.getJSONObject(i)));
                    }
                    AdapterAdminRecs adminRecs = new AdapterAdminRecs(manualServerReload.this, list, object.getString("cache").equals("1") ? State.CACHE : State.ONLINE);
                    rec_animes.enableVersticleScroll(false);
                    rec_animes.setLayoutManager(new LinearLayoutManager(manualServerReload.this));
                    rec_animes.setAdapter(adminRecs);
                } catch (JSONException e) {
                    Logger.Error(manualServerReload.this.getClass(), e);
                    rec_animes.setVisibility(View.GONE);
                }
            }
        });
    }

    private void reloadDir(final boolean bypass) {
        String url = parser.getDirectorioUrl(NORMAL, this) + "?certificate=" + parser.getCertificateSHA1Fingerprint(this);
        if (bypass) {
            url += "&bypass";
        }
        Log.d("Load url", url);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setConnectTimeout(15000);
        client.get(url, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        state_dir.setTextColor(ColorsRes.Rojo(manualServerReload.this));
                        state_dir.setText("ERROR!");
                    }
                });
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject object = new JSONObject(responseString);
                    final boolean isCache = object.getString("cache").equals("1");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isCache) {
                                state_dir.setTextColor(ColorsRes.Amarillo(manualServerReload.this));
                                state_dir.setText("MODO CACHE");
                            } else {
                                state_dir.setTextColor(ColorsRes.Verde(manualServerReload.this));
                                state_dir.setText("OK");
                            }
                        }
                    });
                    if (bypass) {
                        Log.d("Directorio", "Load bypass");
                    }
                } catch (JSONException e) {
                    Logger.Error(manualServerReload.this.getClass(), e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            state_dir.setTextColor(ColorsRes.Rojo(manualServerReload.this));
                            state_dir.setText("ERROR!");
                        }
                    });
                }
            }
        });
    }

    private void endReload() {
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
    }

    private String UTCtoLocal(String utc) {
        String convert = "";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mmaa", Locale.ENGLISH);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date myDate = simpleDateFormat.parse(utc);
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            convert = simpleDateFormat.format(myDate);
        } catch (Exception e) {
            e.printStackTrace();
            convert = utc;
        }
        return convert;
    }

    @Override
    public void onRefresh() {
        state_rec.setText("Cargando...");
        state_dir.setText("Cargando...");
        reloadAll();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                endReload();
            }
        }, 3000);
    }

    public enum State {
        ONLINE(0),
        CACHE(1);
        int value;

        State(int value) {
            this.value = value;
        }
    }
}
