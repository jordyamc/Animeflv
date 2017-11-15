package knf.animeflv;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONObject;

import knf.animeflv.Favorites.FavotiteDB;
import knf.animeflv.LoginActivity.DropboxManager;
import knf.animeflv.Seen.SeenManager;
import knf.animeflv.Utils.NetworkUtils;

public class FavSyncro {
    public static boolean isLogedIn(Context context) {
        return DropboxManager.islogedIn();
    }

    public static void updateServer(Context activity) {
        if (DropboxManager.islogedIn()) {
            DropboxManager.updateFavs(activity, null);
            DropboxManager.updateSeen(activity, null);
        }
    }

    public static void updateFavs(Context activity) {
        if (DropboxManager.islogedIn())
            DropboxManager.updateFavs(activity, null);
    }

    public static void updateSeen(Context activity, DropboxManager.UploadCallback callback) {
        if (DropboxManager.islogedIn())
            DropboxManager.updateSeen(activity, callback);
    }

    public static String getEmail(Context context) {
        if (DropboxManager.islogedIn()) {
            if (DropboxManager.islogedIn()) {
                String email = DropboxManager.getEmail(context);
                if (email != null) {
                    return email;
                } else if (NetworkUtils.isNetworkAvailable()) {
                    DropboxManager.setEmail(context);
                }
            }
        }
        return "AnimeFLV";
    }

    public static String getEmailHelp(Context context) {
        if (DropboxManager.islogedIn()) {
            if (DropboxManager.islogedIn()) {
                String email = DropboxManager.getEmail(context);
                if (email != null) {
                    return email;
                } else if (NetworkUtils.isNetworkAvailable()) {
                    DropboxManager.setEmail(context);
                }
            }

        }
        return getGeneratedHelp(context);
    }

    public static String getGeneratedHelp(Context context) {
        String email = PreferenceManager.getDefaultSharedPreferences(context).getString("help_email", null);
        if (email == null) {
            email = "animeflv-" + System.currentTimeMillis() + "@animeflvapp.com";
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("help_email", email).apply();
        }
        return email;
    }

    public static void updateLocal(final Context context, final UpdateCallback callback) {
        if (isLogedIn(context)) {
            DropboxManager.downloadFavs(context, new DropboxManager.DownloadCallback() {
                @Override
                public void onDownload(JSONObject object, boolean success) {
                    if (success) {
                        try {
                            Log.e("Fav Sync", "Dropbox Success, saving local");
                            new FavotiteDB(context).updatebyJSON(object, new FavotiteDB.updateDataInterface() {
                                @Override
                                public void onFinish() {
                                    callback.onUpdate();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("Fav Sync", "Error getting from Dropbox");
                    }
                }
            });
        } else {
            callback.onUpdate();
        }
    }

    public static void updateLocalSeen(final Context context, final UpdateCallback callback) {
        DropboxManager.downloadSeen(context, new DropboxManager.DownloadCallback() {
            @Override
            public void onDownload(JSONObject object, boolean success) {
                if (success) {
                    try {
                        String visto = new Parser().getUserVistos(object.toString());
                        if (!visto.equals("")) {
                            String vistos = SeenManager.get(context).getSeenList();
                            try {
                                if (!vistos.equals(visto)) {
                                    SeenManager.get(context).updateSeen(visto, new SeenManager.SeenCallback() {
                                        @Override
                                        public void onSeenUpdated() {
                                            Log.e("Seen Sync", "Dropbox Updated");
                                            callback.onUpdate();
                                        }
                                    });
                                } else {
                                    Log.e("Seen Sync", "Dropbox same info");
                                    callback.onUpdate();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                callback.onUpdate();
                            }
                        } else {
                            callback.onUpdate();
                        }
                    } catch (Exception e) {
                        callback.onUpdate();
                    }
                } else {
                    callback.onUpdate();
                    Log.e("Seen Sync", "Dropbox Error");
                }
            }
        });
    }


    public interface UpdateCallback {
        void onUpdate();
    }
}
