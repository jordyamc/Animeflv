package knf.animeflv.Random;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.Directorio.DB.DirectoryHelper;
import knf.animeflv.R;
import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.TrackingHelper;
import xdroid.toaster.Toaster;

public class RandomActivity extends AppCompatActivity implements RandomInterfaces, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    private RandomAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        if (!DirectoryHelper.get(this).isDirectoryValid()) {
            Toaster.toast("Error!!! Por favor actualiza el directorio!!!");
            finish();
            return;
        }
        setContentView(R.layout.random_layout);
        ButterKnife.bind(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Random");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTheme();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        ThemeUtils.Theme theme = ThemeUtils.Theme.create(this);
        toolbar.setBackgroundColor(theme.primary);
        toolbar.getRootView().setBackgroundColor(theme.background);
        toolbar.setTitleTextColor(theme.textColorToolbar);
        ThemeUtils.setNavigationColor(toolbar, theme.toolbarNavigation);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(theme.primaryDark);
            getWindow().setNavigationBarColor(theme.primary);
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

    @Override
    protected void onResume() {
        TrackingHelper.track(this, TrackingHelper.RANDOM);
        super.onResume();
    }
}
