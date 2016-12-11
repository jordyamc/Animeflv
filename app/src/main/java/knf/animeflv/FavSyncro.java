package knf.animeflv;

import android.app.Activity;
import android.content.Context;

import org.json.JSONObject;

import knf.animeflv.LoginActivity.DropboxManager;
import knf.animeflv.LoginActivity.LoginServer;

/**
 * Created by Jordy on 08/12/2016.
 */

public class FavSyncro {
    public static void updateServer(Activity activity) {
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
                                String visto = new Parser().getUserVistos(object.toString());
                                if (visto.equals("")) {
                                    String favs = Parser.getTrimedList(context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", ""), ":::");
                                    if (!favs.equals(favoritos)) {
                                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("favoritos", favoritos).apply();
                                        callback.onUpdate();
                                    }
                                } else {
                                    String favs = Parser.getTrimedList(context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", ""), ":::");
                                    if (!favs.equals(favoritos)) {
                                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("favoritos", favoritos).apply();
                                        callback.onUpdate();
                                    }
                                    String vistos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", "");
                                    try {
                                        if (!vistos.equals(visto)) {
                                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("vistos", visto).apply();
                                            String[] v = visto.split(";;;");
                                            for (String s : v) {
                                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean(s, true).apply();
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
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


    interface UpdateCallback {
        void onUpdate();
    }
}
