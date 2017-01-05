package knf.animeflv;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import knf.animeflv.LoginActivity.DropboxManager;
import knf.animeflv.LoginActivity.LoginServer;
import knf.animeflv.Seen.SeenManager;

/**
 * Created by Jordy on 08/12/2016.
 */

public class FavSyncro {
    public static void updateServer(Context activity) {
        if (LoginServer.isLogedIn(activity))
            LoginServer.RefreshData(activity);
        if (DropboxManager.islogedIn())
            DropboxManager.updateFavs(activity, null);
    }

    public static void updateLocal(final Context context, final UpdateCallback callback) {
        LoginServer.RefreshLocalData(context, new LoginServer.RefreshLocalInterface() {
            @Override
            public void onUpdate() {
                callback.onUpdate();
            }

            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                DropboxManager.downloadFavs(context, new DropboxManager.DownloadCallback() {
                    @Override
                    public void onDownload(JSONObject object, boolean success) {
                        if (success) {
                            try {
                                String favoritos = Parser.getTrimedList(new Parser().getUserFavs(object.toString()), ":::");
                                String favs = Parser.getTrimedList(context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", ""), ":::");
                                if (!favs.equals(favoritos)) {
                                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("favoritos", favoritos).apply();
                                    callback.onUpdate();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    public static void updateLocalSeen(final Context context, final UpdateCallback callback) {
        LoginServer.RefreshLocalData(context, new LoginServer.RefreshLocalInterface() {
            @Override
            public void onUpdate() {
                callback.onUpdate();
            }

            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Log.e("Seen Sync", "Error getting from server, trying Dropbox");
                DropboxManager.downloadFavs(context, new DropboxManager.DownloadCallback() {
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
        });
    }


    interface UpdateCallback {
        void onUpdate();
    }
}
