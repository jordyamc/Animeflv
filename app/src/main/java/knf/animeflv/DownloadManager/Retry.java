package knf.animeflv.DownloadManager;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
            String eid = bundle.getString("eid");
            String url = bundle.getString("url");
            ManageDownload.chooseDownDir(this, eid, url);
            Toaster.toast("Reintentando");
            finish();
        } else {
            Toaster.toast("Error al reintentar");
            finish();
        }
    }
}
