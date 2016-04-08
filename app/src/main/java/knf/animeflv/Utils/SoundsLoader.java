package knf.animeflv.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;
import knf.animeflv.R;
import knf.animeflv.Utils.eNums.UpdateState;
import knf.animeflv.newMain;
import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 08/04/2016.
 */
public class SoundsLoader {
    public static void start(Context context) {
        int corePoolSize = 60;
        int maximumPoolSize = 80;
        int keepAliveTime = 10;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
        Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
        new Loader(context).executeOnExecutor(threadPoolExecutor);
    }

    private static class Loader extends AsyncTask<String, String, String> {
        Context context;

        public Loader(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            new SyncHttpClient().get("https://raw.githubusercontent.com/jordyamc/Animeflv/master/app/sounds.json", null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("SoundJson",response.toString()).apply();
                        JSONArray array = response.getJSONArray("sounds");
                        List<String> files = new ArrayList<String>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            files.add(object.getString("file"));
                        }
                        checkSounds(files);
                    } catch (Exception e) {
                        Log.e("Load List Sound", e.getMessage(), e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.e("Load List Sound", throwable.getMessage(), throwable);
                }
            });
            return null;
        }
    }


    private static void checkSounds(List<String> list) {
        File dir = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/.sounds");
        File nomedia = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/.sounds/.nomedia");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!nomedia.exists()){
            try {
                nomedia.createNewFile();
            }catch (Exception e){
                Log.e("No Media",e.getMessage(),e);
            }
        }
        ThinDownloadManager downloadManager = new ThinDownloadManager();
        for (final String name : list) {
            File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/.sounds", name);
            if (!file.exists()) {
                Uri download = Uri.parse("https://raw.githubusercontent.com/jordyamc/Animeflv/master/app/sounds/" + name);
                DownloadRequest downloadRequest = new DownloadRequest(download)
                        .setDestinationURI(Uri.fromFile(file))
                        .setStatusListener(new DownloadStatusListenerV1() {
                            @Override
                            public void onDownloadComplete(DownloadRequest downloadRequest) {
                                Log.d("Sound Check", name + " Downloaded");
                            }

                            @Override
                            public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                                Log.d("Sound Check", name + " Error");
                            }

                            @Override
                            public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {

                            }
                        });
                downloadManager.add(downloadRequest);
            } else {
                Log.d("Sound Check", name + " Exist");
            }
        }
        Log.d("Sound Check", "Finish");
    }
}
