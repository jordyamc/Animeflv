package knf.animeflv.Utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Jordy on 08/04/2016.
 */
public class SoundsLoader {
    public static void start(Context context) {
        if (NetworkUtils.isNetworkAvailable()) {
            new Loader(context).executeOnExecutor(ExecutorManager.getExecutor());
        }
    }

    private static void checkSounds(List<String> list) {
        if (!Keys.Dirs.SOUNDS.exists()) {
            Keys.Dirs.SOUNDS.mkdirs();
        }
        if (!Keys.Dirs.SOUNDS_NOMEDIA.exists()) {
            try {
                Keys.Dirs.SOUNDS_NOMEDIA.createNewFile();
            } catch (Exception e) {
                Log.e("No Media", e.getMessage(), e);
            }
        }
        ThinDownloadManager downloadManager = new ThinDownloadManager();
        for (final String name : list) {
            File file = new File(Keys.Dirs.SOUNDS, name);
            if (!file.exists()) {
                Uri download = Uri.parse(Keys.Url.SOUNDS + name);
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

    private static String getSoundUrl(Context context) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("betaSounds", false)) {
            return Keys.Url.SOUNDS_JSON_BETA;
        } else {
            return Keys.Url.SOUNDS_JSON;
        }
    }

    private static class Loader extends AsyncTask<String, String, String> {
        Context context;

        public Loader(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            new SyncHttpClient().get(getSoundUrl(context), null, new JsonHttpResponseHandler() {
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
}
