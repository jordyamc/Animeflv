package knf.animeflv.TV;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.JsonFactory.BaseGetter;
import knf.animeflv.JsonFactory.JsonTypes.INICIO;
import knf.animeflv.JsonFactory.SelfGetter;
import knf.animeflv.R;
import knf.animeflv.Recientes.MainOrganizer;
import knf.animeflv.TV.MainFiles.AdapterMain;
import knf.animeflv.Utils.ThemeUtils;

/**
 * Created by Jordy on 09/05/2017.
 */

public class Main extends AppCompatActivity {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_tv);
        ButterKnife.bind(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
        SelfGetter.getInicio(this, new INICIO(), new BaseGetter.AsyncInterface() {
            @Override
            public void onFinish(String json) {
                final AdapterMain adapterMain = new AdapterMain(Main.this, MainOrganizer.init(json).list());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(adapterMain);
                    }
                });
            }
        });
    }
}
