package knf.animeflv.LoginActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.WriteMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import knf.animeflv.AutoEmision.AutoEmisionHelper;
import knf.animeflv.FavSyncro;
import knf.animeflv.Favorites.FavotiteDB;
import knf.animeflv.R;
import knf.animeflv.Seen.SeenManager;
import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.Keys;
import knf.animeflv.Utils.NetworkUtils;

public class DropboxManager {
    public static final String KEY_DROPBOX = "token_dropbox";

    private static String accessToken = null;

    public static void login(final Context context, @Nullable final LoginCallback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                accessToken = preferences.getString(KEY_DROPBOX, null);
                if (accessToken == null && callback != null) {
                    callback.onStartLogin();
                    Auth.startOAuth2Authentication(context, context.getResources().getString(R.string.app_key));
                } else {
                    if (accessToken != null)
                        Log.e("AccessToken", accessToken);
                    if (callback != null)
                        if (accessToken == null) {
                            callback.onLogin(false);
                        } else {
                            preferences.edit().putString(KEY_DROPBOX, accessToken).apply();
                            DropboxClientFactory.init(accessToken);
                            FavSyncro.updateServer(context);
                            callback.onLogin(true);
                        }
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static void init(Context context) {
        accessToken = PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_DROPBOX, null);
        DropboxClientFactory.init(accessToken);
    }

    public static boolean islogedIn() {
        return getToken() != null;
    }

    @Nullable
    public static String getEmail(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("dropbox_email", null);
    }

    public static void setEmail(final Context context) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (getToken() == null) {
                    return null;
                }
                try {
                    DbxClientV2 client = DropboxClientFactory.getClient();
                    if (client == null) {
                        DropboxClientFactory.init(accessToken);
                        client = DropboxClientFactory.getClient();
                    }
                    String email = client.users().getCurrentAccount().getEmail();
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString("dropbox_email", email).apply();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Dropbox Api", "Error getting Account Email");
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static void logoff(Context context) {
        accessToken = null;
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(KEY_DROPBOX, null).apply();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("dropbox_email", null).apply();
    }

    public static void updateFavs(final Context context, @Nullable final UploadCallback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if (islogedIn() && NetworkUtils.isNetworkAvailable()) {
                    Log.e("DropBoxUpload", "Start Upload");
                    if (getToken() == null) {
                        if (callback != null)
                            callback.onUpload(false);
                        return null;
                    }
                    try {
                        JSONObject object = new FavotiteDB(context).getDBJSON(true);
                        File tmpFile = new File(Keys.Dirs.CACHE, "favs.save");
                        FileUtil.writeToFile(object.toString(), tmpFile);
                        InputStream inputStream = new FileInputStream(tmpFile);
                        DbxClientV2 client = DropboxClientFactory.getClient();
                        if (client == null) {
                            DropboxClientFactory.init(accessToken);
                            client = DropboxClientFactory.getClient();
                        }
                        client.files().uploadBuilder("/favs.save")
                                .withMode(WriteMode.OVERWRITE)
                                .withMute(true)
                                .uploadAndFinish(inputStream);
                        tmpFile.delete();
                        if (callback != null)
                            callback.onUpload(true);
                        Log.e("DropBoxUpload", "Upload Success");
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (callback != null)
                            callback.onUpload(false);
                        Log.e("DropBoxUpload", "Upload Error");
                    }
                } else {
                    if (callback != null)
                        callback.onUpload(false);
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static void updateEmision(final Context context, @Nullable final UploadCallback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if (getToken() == null) {
                    if (callback != null)
                        callback.onUpload(false);
                    return null;
                }
                try {
                    JSONObject object = AutoEmisionHelper.getJson(context);
                    File tmpFile = new File(Keys.Dirs.CACHE, "emision.save");
                    FileUtil.writeToFile(object.toString(), tmpFile);
                    InputStream inputStream = new FileInputStream(tmpFile);
                    DbxClientV2 client = DropboxClientFactory.getClient();
                    if (client == null) {
                        DropboxClientFactory.init(accessToken);
                        client = DropboxClientFactory.getClient();
                    }
                    client.files().uploadBuilder("/emision.save")
                            .withMode(WriteMode.OVERWRITE)
                            .withMute(true)
                            .uploadAndFinish(inputStream);
                    tmpFile.delete();
                    if (callback != null)
                        callback.onUpload(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null)
                        callback.onUpload(false);
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static void downloadEmision(final Context context, @Nullable final DownloadCallback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if (getToken() == null) {
                    if (callback != null)
                        callback.onDownload(null, false);
                    return null;
                }
                try {
                    File tmpFile = new File(Keys.Dirs.CACHE, "emision.save");
                    OutputStream outputStream = new FileOutputStream(tmpFile);
                    DbxClientV2 client = DropboxClientFactory.getClient();
                    if (client == null) {
                        DropboxClientFactory.init(accessToken);
                        client = DropboxClientFactory.getClient();
                    }
                    client.files().downloadBuilder("/emision.save")
                            .download(outputStream);
                    try {
                        JSONObject object = new JSONObject(FileUtil.getStringFromFile(tmpFile));
                        if (callback != null)
                            callback.onDownload(object, true);
                    } catch (JSONException e) {
                        client.files().delete("/emision.save");
                        updateEmision(context, null);
                        if (callback != null)
                            callback.onDownload(null, false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null)
                        callback.onDownload(null, false);
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static void updateSeen(final Context context, @Nullable final UploadCallback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if (getToken() == null) {
                    if (callback != null)
                        callback.onUpload(false);
                    return null;
                }
                String vistos = SeenManager.get(context).getSeenList();
                try {
                    JSONObject object = new JSONObject();
                    object.put("response", "ok");
                    object.put("vistos", vistos);
                    File tmpFile = new File(Keys.Dirs.CACHE, "seen.save");
                    FileUtil.writeToFile(object.toString(), tmpFile);
                    InputStream inputStream = new FileInputStream(tmpFile);
                    DbxClientV2 client = DropboxClientFactory.getClient();
                    if (client == null) {
                        DropboxClientFactory.init(accessToken);
                        client = DropboxClientFactory.getClient();
                    }
                    client.files().uploadBuilder("/seen.save")
                            .withMode(WriteMode.OVERWRITE)
                            .withMute(true)
                            .uploadAndFinish(inputStream);
                    tmpFile.delete();
                    if (callback != null)
                        callback.onUpload(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null)
                        callback.onUpload(false);
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static void downloadFavs(final Context context, @Nullable final DownloadCallback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if (getToken() == null) {
                    if (callback != null)
                        callback.onDownload(null, false);
                    return null;
                }
                try {
                    File tmpFile = new File(Keys.Dirs.CACHE, "favs.save");
                    OutputStream outputStream = new FileOutputStream(tmpFile);
                    DbxClientV2 client = DropboxClientFactory.getClient();
                    if (client == null) {
                        DropboxClientFactory.init(accessToken);
                        client = DropboxClientFactory.getClient();
                    }
                    client.files().downloadBuilder("/favs.save")
                            .download(outputStream);
                    try {
                        JSONObject object = new JSONObject(FileUtil.getStringFromFile(tmpFile));
                        if (callback != null)
                            callback.onDownload(object, true);
                    } catch (JSONException e) {
                        client.files().delete("/favs.save");
                        updateFavs(context, null);
                        if (callback != null)
                            callback.onDownload(null, false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null)
                        callback.onDownload(null, false);
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static void downloadSeen(final Context context, @Nullable final DownloadCallback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if (getToken() == null) {
                    if (callback != null)
                        callback.onDownload(null, false);
                    return null;
                }
                try {
                    File tmpFile = new File(Keys.Dirs.CACHE, "seen.save");
                    OutputStream outputStream = new FileOutputStream(tmpFile);
                    DbxClientV2 client = DropboxClientFactory.getClient();
                    if (client == null) {
                        DropboxClientFactory.init(accessToken);
                        client = DropboxClientFactory.getClient();
                    }
                    client.files().downloadBuilder("/seen.save")
                            .download(outputStream);
                    try {
                        JSONObject object = new JSONObject(FileUtil.getStringFromFile(tmpFile));
                        if (callback != null)
                            callback.onDownload(object, true);
                    } catch (JSONException e) {
                        client.files().delete("/seen.save");
                        updateSeen(context, null);
                        if (callback != null)
                            callback.onDownload(null, false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null)
                        callback.onDownload(null, false);
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static void UpdateToken(String token) {
        accessToken = token;
        DropboxClientFactory.init(token);
    }

    @Nullable
    public static String getToken() {
        return accessToken;
    }

    public interface LoginCallback {
        void onLogin(boolean loged);

        void onStartLogin();
    }

    public interface UploadCallback {
        void onUpload(boolean success);
    }

    public interface DownloadCallback {
        void onDownload(JSONObject result, boolean success);
    }

}
