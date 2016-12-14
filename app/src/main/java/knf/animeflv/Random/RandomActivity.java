package knf.animeflv.Random;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.R;
import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

public class RandomActivity extends AppCompatActivity implements RandomInterfaces, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    private RandomAdapter adapter;
    private File DirFile = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        if (!DirFile.exists()) {
            Toaster.toast("Error!!! Por favor actualiza el directorio!!!");
            finish();
            return;
        }
        setContentView(R.layout.random_layout);
        ButterKnife.bind(this);
        setTheme();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Random");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        asyncSetAdapter();
    }

    private void asyncSetAdapter() {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                adapter = new RandomAdapter(RandomActivity.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(adapter);
                    }
                });
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    private void setTheme() {
        if (ThemeUtils.isAmoled(this)) {
            toolbar.setBackgroundColor(ColorsRes.Negro(this));
            toolbar.getRootView().setBackgroundColor(ColorsRes.Negro(this));
        }
    }

    @Override
    public void onFinishRefresh() {
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        adapter.onStartRefreshing();
    }
}
