package knf.animeflv.LoginActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONObject;

import java.io.File;

import knf.animeflv.Favorites.FavotiteDB;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.Keys;

/**
 * Created by Jordy on 22/02/2018.
 */

public class MigrationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (getIntent().getIntExtra("type", 0)) {
            case 0:
                prepareFavs();
                break;
        }
    }

    private void prepareFavs() {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content("Preparando datos")
                .progress(true, 0)
                .cancelable(false)
                .build();
        dialog.show();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                JSONObject object = new FavotiteDB(MigrationActivity.this).getDBJSON(true);
                File tmpFile = new File(Keys.Dirs.CACHE, "favs.save");
                FileUtil.writeToFile(object.toString(), tmpFile);
                Intent intent = new Intent("knf.kuma.MIGRATE", FileProvider.getUriForFile(MigrationActivity.this, "knf.animeflv.RequestsBackground", tmpFile));
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                setResult(RESULT_OK, intent);
                if (dialog.isShowing())
                    dialog.dismiss();
                finish();
            }
        });
    }

}
