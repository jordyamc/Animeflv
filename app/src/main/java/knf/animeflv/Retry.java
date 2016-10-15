package knf.animeflv;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

import knf.animeflv.Utils.ExecutorManager;
import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 01/03/2016.
 */
public class Retry extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String aid = bundle.getString("aid");
            String eid = bundle.getString("eid");
            String titulo = bundle.getString("titulo");
            String numero = bundle.getString("numero");
            String url = bundle.getString("url");
            File file = new File(bundle.getString("file"));
            new Downloader(this, url, eid, aid, titulo, numero, file).executeOnExecutor(ExecutorManager.getExecutor());
            Toaster.toast("Reintentando");
            finish();
        } else {
            Toaster.toast("Error al reintentar");
            finish();
        }
    }
}
