package knf.animeflv.HallFame;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import com.github.ndczz.infinityloading.InfinityLoading;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import knf.animeflv.ColorsRes;
import knf.animeflv.HallFame.Objects.ListItem;
import knf.animeflv.HallFame.Objects.SectionItem;
import knf.animeflv.HallFame.Objects.TitleItem;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.TaskType;
import knf.animeflv.Utils.ThemeUtils;

public class HallActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recycler)
    RecyclerView recyclerView;
    @Bind(R.id.loader)
    InfinityLoading loading;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_hall);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Salon de la Fama");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (ThemeUtils.isTablet(this)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    ThemeUtils.setNavigationBarPadding(this, recyclerView);
                    getWindow().setFlags(
                            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                }
            }
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (ThemeUtils.isAmoled(this)) {
            toolbar.setBackgroundColor(ColorsRes.Negro(this));
            toolbar.getRootView().setBackgroundColor(ColorsRes.Negro(this));
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        HallListFactory.create(this, new HallListFactory.ListReadyListener() {
            @Override
            public void onFinish(final List<ListItem> list) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.setVisibility(View.GONE);
                        recyclerView.setAdapter(new HallAdapter(HallActivity.this, list));
                    }
                });
            }

            @Override
            public void onFailed() {

            }
        });
    }

    private static class HallListFactory {
        public static void create(Activity activity, final ListReadyListener listener) {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(new Parser().getBaseUrl(TaskType.NORMAL, activity) + "hall-of-fame.php?list", null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    List<ListItem> list = new ArrayList<ListItem>();
                    try {
                        JSONArray array = response.getJSONArray("list");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.optJSONObject(i);
                            list.add(new TitleItem(object.getString("name")));
                            JSONArray sub = object.getJSONArray("list");
                            for (int e = 0; e < sub.length(); e++) {
                                JSONObject subObject = sub.getJSONObject(e);
                                list.add(new SectionItem(subObject.getString("name"), subObject.getString("description"), subObject.getString("id")));
                            }
                        }
                        listener.onFinish(list);
                    } catch (Exception e) {
                        listener.onFailed();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
            });
        }

        interface ListReadyListener {
            void onFinish(List<ListItem> list);

            void onFailed();
        }
    }
}
