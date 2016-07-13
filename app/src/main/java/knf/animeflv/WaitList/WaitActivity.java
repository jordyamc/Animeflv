package knf.animeflv.WaitList;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import knf.animeflv.ColorsRes;
import knf.animeflv.DownloadManager.ManageDownload;
import knf.animeflv.Interfaces.WaitDownloadCallback;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Utils.MainStates;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.UrlUtils;
import knf.animeflv.WaitList.Costructor.WaitManager;
import xdroid.toaster.Toaster;

public class WaitActivity extends AppCompatActivity implements
        RecyclerViewExpandableItemManager.OnGroupCollapseListener,
        RecyclerViewExpandableItemManager.OnGroupExpandListener,
        WaitDownloadCallback {
    Toolbar toolbar;
    RecyclerView recyclerView;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewExpandableItemManager mRecyclerViewExpandableItemManager;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
    private RecyclerViewSwipeManager mRecyclerViewSwipeManager;
    private RecyclerViewTouchActionGuardManager mRecyclerViewTouchActionGuardManager;
    private Context context;
    private AdapterWait adapterWait;
    private MaterialDialog processing;
    private Parser parser = new Parser();
    private List<String> urls = new ArrayList<>();
    private List<String> eids = new ArrayList<>();
    private Handler waitUrls = new Handler();
    Runnable waitrun = new Runnable() {
        @Override
        public void run() {
            if (!urls.contains("_noData_")) {
                waitUrls.removeCallbacks(waitrun);
                for (String s : eids) {
                    String url = urls.get(eids.indexOf(s));
                    if (!url.equals("null")) {
                        ManageDownload.chooseDownDir(context, s, url);
                        MainStates.delFromWaitList(s);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processing.dismiss();
                        adapterWait.notifyResume();
                    }
                });
            } else {
                waitUrls.postDelayed(waitrun, 500);
            }
        }
    };
    Runnable waitrunSingle = new Runnable() {
        @Override
        public void run() {
            if (!urls.contains("_noData_")) {
                waitUrls.removeCallbacks(waitrunSingle);
                for (String s : eids) {
                    String url = urls.get(eids.indexOf(s));
                    if (!url.equals("null")) {
                        ManageDownload.chooseDownDir(context, s, url);
                    }
                }
                MainStates.delFromWaitList(eids.get(0));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processing.dismiss();
                        adapterWait.notifyResume();
                    }
                });
            } else {
                waitUrls.postDelayed(waitrunSingle, 500);
            }
        }
    };

    public static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_list_lay);
        context = this;
        if (!isXLargeScreen(getApplicationContext())) { //set phones to portrait;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar_wait);
        if (ThemeUtils.isAmoled(this)) {
            toolbar.setBackgroundColor(getResources().getColor(android.R.color.black));
            toolbar.getRootView().setBackgroundColor(ColorsRes.Negro(this));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(getResources().getColor(R.color.negro));
                getWindow().setNavigationBarColor(getResources().getColor(R.color.negro));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(getResources().getColor(R.color.dark));
                getWindow().setNavigationBarColor(getResources().getColor(R.color.prim));
            }
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar_wait);
        recyclerView = (RecyclerView) findViewById(R.id.rv_wait_list);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setTitle("Lista de Espera");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        WaitManager.Refresh();
        processing = new MaterialDialog.Builder(this)
                .content("Procesando...")
                .progress(true, 0)
                .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                .cancelable(false)
                .build();
        final Parcelable eimSavedState = (savedInstanceState != null) ? savedInstanceState.getParcelable("RecyclerViewExpandableItemManager") : null;
        mRecyclerViewExpandableItemManager = new RecyclerViewExpandableItemManager(eimSavedState);
        // touch guard manager  (this class is required to suppress scrolling while swipe-dismiss animation is running)
        mRecyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        mRecyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        mRecyclerViewTouchActionGuardManager.setEnabled(true);

        // drag & drop manager
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();

        // swipe manager
        mRecyclerViewSwipeManager = new RecyclerViewSwipeManager();
        adapterWait = new AdapterWait(this, mRecyclerViewExpandableItemManager, mRecyclerViewSwipeManager);
        mWrappedAdapter = mRecyclerViewExpandableItemManager.createWrappedAdapter(adapterWait);       // wrap for expanding
        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(mWrappedAdapter);           // wrap for dragging
        mWrappedAdapter = mRecyclerViewSwipeManager.createWrappedAdapter(mWrappedAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        //recyclerView.setItemAnimator(animator);
        recyclerView.setAdapter(mWrappedAdapter);
    }

    @Override
    public void onGroupCollapse(int groupPosition, boolean fromUser) {

    }

    @Override
    public void onGroupExpand(int groupPosition, boolean fromUser) {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (!isXLargeScreen(getApplicationContext())) {
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapterWait != null) {
            adapterWait.notifyResume();
        }
    }

    @Override
    public void onAllCapsDownload(final String aid, final List<Float> list) {
        processing.show();
        processing.setContent(UrlUtils.getTitCached(aid));
        urls.clear();
        eids.clear();
        Log.d("DownloadAll", "Start " + UrlUtils.getUrlAnimeCached(aid));
        new startAllDownloads(aid, list).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onSingleCapDownload(String aid, float cap) {
        processing.show();
        processing.setContent("Capitulo " + cap);
        urls.clear();
        eids.clear();
        Log.d("DownloadAll", "Start " + UrlUtils.getUrlAnimeCached(aid));
        List<Float> list = new ArrayList<>();
        list.add(cap);
        new startSingleDownload(aid, list).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class getTrueIzanagi extends AsyncTask<String, String, String> {
        String url;
        String eid;

        public getTrueIzanagi(String url, String eid) {
            this.url = url;
            this.eid = eid;
        }

        @Override
        protected String doInBackground(String... params) {
            new SyncHttpClient().get(url, null, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    urls.set(eids.indexOf(eid), "null");
                    Log.d("Not True Url", url);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    urls.set(eids.indexOf(eid), responseString.substring(responseString.indexOf("URL=") + 4, responseString.lastIndexOf(",,") + 2));
                    Log.d("Url Add", responseString.substring(responseString.indexOf("URL=") + 4, responseString.lastIndexOf(",,") + 2));
                }
            });
            return null;
        }
    }

    private class getDownloadInfo extends AsyncTask<String, String, String> {
        private String eid;

        public getDownloadInfo(String eid) {
            this.eid = eid;
        }

        @Override
        protected String doInBackground(String... params) {
            new SyncHttpClient().get(UrlUtils.getUrlCapCached(eid), null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        JSONArray downloads = response.getJSONArray("downloads");
                        JSONObject izanagi = downloads.getJSONObject(0);
                        String url = izanagi.getString("url");
                        if (url.contains("/d.php")) {
                            new getTrueIzanagi(url, eid).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            Log.d("Url Add", url);
                            urls.set(eids.indexOf(eid), url);
                        }
                    } catch (Exception e) {
                        Log.e("DownloadInfo", e.getMessage(), e.getCause());
                        urls.set(eids.indexOf(eid), "null");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.e("DownloadInfo", throwable.getMessage(), throwable);
                    urls.set(eids.indexOf(eid), "null");
                }
            });
            return null;
        }
    }

    private class startAllDownloads extends AsyncTask<String, String, String> {
        String aid;
        List<Float> list;

        public startAllDownloads(String aid, List<Float> list) {
            this.aid = aid;
            this.list = list;
        }

        @Override
        protected String doInBackground(String... params) {
            new SyncHttpClient().get(UrlUtils.getUrlAnimeCached(aid), null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        JSONArray array = response.getJSONArray("episodios");
                        for (float num : list) {
                            String number = String.valueOf(num);
                            String eid = aid + "_" + number.replace(".0", "") + "E";
                            eids.add(eid);
                            JSONArray downarray = null;
                            for (int i = 0; i <= array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                if (object.getString("eid").equals(eid)) {
                                    downarray = object.optJSONArray("downloads");
                                    break;
                                }
                            }
                            if (downarray != null) {
                                JSONObject sub = downarray.getJSONObject(0);
                                String izanagiurl = sub.getString("url");
                                if (izanagiurl.contains("/d.php")) {
                                    urls.add("_noData_");
                                    new getTrueIzanagi(izanagiurl, eid).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                } else {
                                    Log.d("Url Add", izanagiurl);
                                    urls.add(izanagiurl);
                                }
                            } else {
                                urls.add("_noData_");
                                new getDownloadInfo(eid).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        }
                        waitrun.run();
                    } catch (Exception e) {
                        Log.e("DownloadList", e.getMessage(), e);
                        processing.dismiss();
                        Toaster.toast("Error al iniciar descargas");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.e("AllCapsDownload", "Error", throwable);
                    processing.dismiss();
                    Toaster.toast("Error al iniciar descargas");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.e("AllCapsDownload", "Error", throwable);
                    Log.d("AllCapsDownload", responseString);
                    processing.dismiss();
                    Toaster.toast("Error al iniciar descargas");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.e("AllCapsDownload", "Error", throwable);
                    processing.dismiss();
                    Toaster.toast("Error al iniciar descargas");
                }
            });
            return null;
        }
    }

    private class startSingleDownload extends AsyncTask<String, String, String> {
        String aid;
        List<Float> list;

        public startSingleDownload(String aid, List<Float> list) {
            this.aid = aid;
            this.list = list;
        }

        @Override
        protected String doInBackground(String... params) {
            new SyncHttpClient().get(UrlUtils.getUrlAnimeCached(aid), null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        JSONArray array = response.getJSONArray("episodios");
                        for (float num : list) {
                            String numer = String.valueOf(num);
                            String eid = aid + "_" + numer.replace(".0", "") + "E";
                            eids.add(eid);
                            JSONArray downarray = null;
                            for (int i = 0; i <= array.length(); i++) {
                                JSONObject object = array.getJSONObject(eids.indexOf(eid));
                                if (object.getString("eid").equals(eid)) {
                                    downarray = object.optJSONArray("downloads");
                                    break;
                                }
                            }
                            if (downarray != null) {
                                JSONObject sub = downarray.getJSONObject(0);
                                String izanagiurl = sub.getString("url");
                                if (izanagiurl.contains("/d.php")) {
                                    urls.add("_noData_");
                                    new getTrueIzanagi(izanagiurl, eid).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                } else {
                                    Log.d("Url Add", izanagiurl);
                                    urls.add(izanagiurl);
                                }
                            } else {
                                urls.add("_noData_");
                                new getDownloadInfo(eid).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        }
                        waitrunSingle.run();
                    } catch (Exception e) {
                        Log.e("DownloadList", e.getMessage(), e.getCause());
                        processing.dismiss();
                        Toaster.toast("Error al inicar descargas");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.e("AllCapsDownload", "Error", throwable);
                    processing.dismiss();
                    Toaster.toast("Error al inicar descargas");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.e("AllCapsDownload", "Error", throwable);
                    Log.d("AllCapsDownload", responseString);
                    processing.dismiss();
                    Toaster.toast("Error al inicar descargas");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.e("AllCapsDownload", "Error", throwable);
                    processing.dismiss();
                    Toaster.toast("Error al inicar descargas");
                }
            });
            return null;
        }
    }
}
