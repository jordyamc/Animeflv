package knf.animeflv.WaitList;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
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

import java.util.ArrayList;
import java.util.List;

import knf.animeflv.ColorsRes;
import knf.animeflv.DownloadManager.ManageDownload;
import knf.animeflv.Interfaces.WaitDownloadCallback;
import knf.animeflv.JsonFactory.SelfGetter;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.MainStates;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.UrlUtils;
import knf.animeflv.WaitList.Costructor.WaitManager;

public class WaitList extends AppCompatActivity implements
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
    private Activity context;
    private AdapterWait adapterWait;
    private MaterialDialog processing;
    private Parser parser = new Parser();
    private List<String> urls = new ArrayList<>();
    private List<String> eids = new ArrayList<>();

    public static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    public void startDownloads(List<WaitDownloadElement> elements) {
        for (WaitDownloadElement element : elements) {
            ManageDownload.chooseDownDir(this, element.eid, element.url);
            MainStates.init(this).delFromWaitList(element.eid);
        }
        WaitManager.Refresh();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                processing.dismiss();
                recreate();
            }
        });
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
        getSupportActionBar().setDisplayShowHomeEnabled(false);
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
    public void onAllCapsDownload(final String aid, final List<Integer> list) {
        processing.show();
        processing.setContent(UrlUtils.getTitCached(aid));
        urls.clear();
        eids.clear();
        Log.d("DownloadAll", "Start " + UrlUtils.getUrlAnimeCached(aid));
        new startAllDownloads(aid, list).executeOnExecutor(ExecutorManager.getExecutor());
    }

    @Override
    public void onSingleCapDownload(String aid, int cap) {
        processing.show();
        processing.setContent("Capitulo " + cap);
        urls.clear();
        eids.clear();
        Log.d("DownloadSingle", "Start " + UrlUtils.getUrlAnimeCached(aid));
        List<Integer> list = new ArrayList<>();
        list.add(cap);
        new startSingleDownload(aid, list).executeOnExecutor(ExecutorManager.getExecutor());
    }


    public interface ListListener {
        void onListCreated(List<WaitDownloadElement> list);
    }

    private class startAllDownloads extends AsyncTask<String, String, String> {
        String aid;
        List<Integer> list;

        public startAllDownloads(String aid, List<Integer> list) {
            this.aid = aid;
            this.list = list;
        }

        @Override
        protected String doInBackground(String... params) {
            List<String> eids = new ArrayList<>();
            for (int curr : list) {
                final String eid = aid + "_" + curr + "E";
                eids.add(eid);
            }
            SelfGetter.getDownloadList(WaitList.this, eids, new ListListener() {
                @Override
                public void onListCreated(List<WaitDownloadElement> list) {
                    startDownloads(list);
                }
            });
            return null;
        }
    }

    private class startSingleDownload extends AsyncTask<String, String, String> {
        String aid;
        List<Integer> list;

        public startSingleDownload(String aid, List<Integer> list) {
            this.aid = aid;
            this.list = list;
        }

        @Override
        protected String doInBackground(String... params) {
            List<String> eids = new ArrayList<>();
            for (int curr : list) {
                final String eid = aid + "_" + curr + "E";
                eids.add(eid);
            }
            SelfGetter.getDownloadList(WaitList.this, eids, new ListListener() {
                @Override
                public void onListCreated(List<WaitDownloadElement> list) {
                    startDownloads(list);
                }
            });
            return null;
        }
    }
}
