package knf.animeflv;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import knf.animeflv.info.Info;

/**
 * Created by Jordy on 20/08/2015.
 */
public class BackDownload extends AppCompatActivity implements CheckVideo.callback {
    String aid;
    String num;
    String titulo;
    String eid;
    String ext_storage_state = Environment.getExternalStorageState();
    File mediaStorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache");

    Parser parser = new Parser();
    Context context;
    String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        Bundle bundle = getIntent().getExtras();
        aid = bundle.getString("aid", "");
        num = bundle.getString("num", "");
        titulo = bundle.getString("titulo", "");
        eid = bundle.getString("eid", "");
        new CheckVideo(this, TaskType.GET_INFO, 0).execute("http://subidas.com/files/" + aid + "/" + num + ".mp4");
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile(String filePath) {
        String ret = "";
        try {
            File fl = new File(filePath);
            FileInputStream fin = new FileInputStream(fl);
            ret = convertStreamToString(fin);
            fin.close();
        } catch (IOException e) {
        } catch (Exception e) {
        }
        return ret;
    }

    public void toast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }

    public void Descargar(String aid, String num, String titulo, String eid) {
        File Dstorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + aid);
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!Dstorage.exists()) {
                Dstorage.mkdirs();
            }
        }
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://subidas.com/files/" + aid + "/" + num + ".mp4"));
            Log.d("DURL", "http://subidas.com/files/" + aid + "/" + num + ".mp4");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            //request.setTitle(fileName.substring(0, fileName.indexOf(".")));
            request.setTitle(titulo);
            request.setDescription("Capitulo " + num);
            request.setMimeType("video/mp4");
            request.setDestinationInExternalPublicDir("Animeflv/download/" + aid, aid + "_" + num + ".mp4");
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            long l = manager.enqueue(request);
            context.getSharedPreferences("data", MODE_PRIVATE).edit().putString(eid, Long.toString(l)).apply();
            String descargados = context.getSharedPreferences("data", MODE_PRIVATE).getString("eids_descarga", "");
            String epID = context.getSharedPreferences("data", MODE_PRIVATE).getString("epIDS_descarga", "");
            if (descargados.contains(eid)) {
                context.getSharedPreferences("data", MODE_PRIVATE).edit().putString("eids_descarga", descargados.replace(eid + ":::", "")).apply();
                context.getSharedPreferences("data", MODE_PRIVATE).edit().putString("epIDS_descarga", epID.replace(aid + "_" + num + ":::", "")).apply();
            }
            descargados = context.getSharedPreferences("data", MODE_PRIVATE).getString("eids_descarga", "");
            context.getSharedPreferences("data", MODE_PRIVATE).edit().putString("eids_descarga", descargados + eid + ":::").apply();
            String tits = context.getSharedPreferences("data", MODE_PRIVATE).getString("titulos_descarga", "");
            epID = context.getSharedPreferences("data", MODE_PRIVATE).getString("epIDS_descarga", "");
            context.getSharedPreferences("data", MODE_PRIVATE).edit().putString("titulos_descarga", tits + aid + ":::").apply();
            context.getSharedPreferences("data", MODE_PRIVATE).edit().putString("epIDS_descarga", epID + aid + "_" + num + ":::").apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendtext1(String data, TaskType taskType, int position) {
        if (data.trim().equals("ok")) {
            Descargar(aid, num, titulo, eid);
        } else {
            finish();
        }
    }
}
