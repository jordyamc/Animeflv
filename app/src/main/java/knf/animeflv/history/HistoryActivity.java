package knf.animeflv.history;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.R;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.history.adapter.HistoryAdapter;
import knf.animeflv.history.adapter.HistoryHelper;

public class HistoryActivity extends AppCompatActivity implements HistoryAdapter.HistoryAdapterInterface {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.no_data)
    LinearLayout no_data;
    @BindView(R.id.img_no_data)
    ImageView img_no_data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Historial");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setUpTheme();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        RecyclerViewSwipeManager swipeMgr = new RecyclerViewSwipeManager();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(swipeMgr.createWrappedAdapter(new HistoryAdapter(this)));
        swipeMgr.attachRecyclerView(recyclerView);
        img_no_data.setImageResource(ThemeUtils.getFlatImage(this));
        checkList();
    }

    private void setUpTheme() {
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

    private void checkList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (HistoryHelper.getHistoryArray(HistoryActivity.this).length() == 0) {
                    no_data.setVisibility(View.VISIBLE);
                } else {
                    no_data.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onDelete() {
        checkList();
    }
}
