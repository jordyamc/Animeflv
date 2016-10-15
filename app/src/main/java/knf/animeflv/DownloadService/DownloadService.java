package knf.animeflv.DownloadService;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;

import knf.animeflv.Downloader;
import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.FileUtil;

/**
 * Created by Jordy on 19/07/2016.
 */

public class DownloadService extends Service implements Downloader.OnFinishListener {
    String url;
    String eid;
    String titulo;
    String aid;
    String numero;
    File ext_dir;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Bundle bundle;
        try {
            bundle = intent.getExtras();
        } catch (Exception e) {
            stopSelf();
            return START_STICKY_COMPATIBILITY;
        }
        if (bundle == null) {
            stopSelf();
            return START_STICKY_COMPATIBILITY;
        }
        url = bundle.getString("url");
        eid = bundle.getString("eid");
        titulo = bundle.getString("titulo");
        String[] data = eid.replace("E", "").split("_");
        aid = data[0];
        numero = data[1];
        ext_dir = new File(FileUtil.getSDPath() + "/Animeflv/download/" + aid + "/" + eid.replace("E", ".mp4"));

        Downloader downloader = new Downloader(this, url, eid, aid, titulo, numero, ext_dir);
        downloader.setOnFinishListener(this);
        downloader.executeOnExecutor(ExecutorManager.getExecutor());
        startForeground(downloader.idDown, downloader.builder.build());

        return START_REDELIVER_INTENT;
    }

    private void predownload() {
        String descargados = getSharedPreferences("data", MODE_PRIVATE).getString("eids_descarga", "");
        String epID = getSharedPreferences("data", MODE_PRIVATE).getString("epIDS_descarga", "");
        if (descargados.contains(eid)) {
            getSharedPreferences("data", MODE_PRIVATE).edit().putString("eids_descarga", descargados.replace(eid + ":::", "")).apply();
            getSharedPreferences("data", MODE_PRIVATE).edit().putString("epIDS_descarga", epID.replace(aid + "_" + numero + ":::", "")).apply();
        }
        descargados = getSharedPreferences("data", MODE_PRIVATE).getString("eids_descarga", "");
        getSharedPreferences("data", MODE_PRIVATE).edit().putString("eids_descarga", descargados + eid + ":::").apply();
        String tits = getSharedPreferences("data", MODE_PRIVATE).getString("titulos_descarga", "");
        epID = getSharedPreferences("data", MODE_PRIVATE).getString("epIDS_descarga", "");
        getSharedPreferences("data", MODE_PRIVATE).edit().putString("titulos_descarga", tits + aid + ":::").apply();
        getSharedPreferences("data", MODE_PRIVATE).edit().putString("epIDS_descarga", epID + aid + "_" + numero + ":::").apply();
        getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("visto" + aid + "_" + numero, true).apply();
        String vistos = getSharedPreferences("data", MODE_PRIVATE).getString("vistos", "");
        if (!vistos.contains(eid.trim())) {
            vistos = vistos + eid.trim() + ":::";
            getSharedPreferences("data", MODE_PRIVATE).edit().putString("vistos", vistos).apply();
        }
    }

    @Override
    public void onFinish() {
        stopForeground(true);
    }

    @Override
    public void onDestroy() {
        Log.d("Download Service", "On Destroy");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
