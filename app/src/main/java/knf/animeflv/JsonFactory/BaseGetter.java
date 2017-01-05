package knf.animeflv.JsonFactory;

import android.content.Context;

import knf.animeflv.JsonFactory.JsonTypes.ANIME;
import knf.animeflv.JsonFactory.JsonTypes.DIRECTORIO;
import knf.animeflv.JsonFactory.JsonTypes.DOWNLOAD;
import knf.animeflv.JsonFactory.JsonTypes.EMISION;
import knf.animeflv.JsonFactory.JsonTypes.INICIO;
import knf.animeflv.Utils.NetworkUtils;


public class BaseGetter {
    public static void getJson(Context context, INICIO inicio, AsyncInterface asyncInterface) {
        if (NetworkUtils.isNetworkAvailable()) {
            SelfGetter.getInicio(context, inicio, asyncInterface);
        } else {
            if (inicio.type == 0) {
                asyncInterface.onFinish(OfflineGetter.getInicio());
            } else {
                asyncInterface.onFinish("null");
            }
        }

    }

    public static void getJson(Context context, DIRECTORIO directorio, AsyncInterface asyncInterface) {
        if (NetworkUtils.isNetworkAvailable()) {
            SelfGetter.getDir(context, asyncInterface);
        } else {
            asyncInterface.onFinish(OfflineGetter.getDirectorio());
        }
    }

    public static void getJson(Context context, ANIME anime, AsyncInterface asyncInterface) {
        if (NetworkUtils.isNetworkAvailable()) {
            SelfGetter.getAnime(context, anime, asyncInterface);
        } else {
            asyncInterface.onFinish(OfflineGetter.getAnime(anime));
        }
    }

    public static void getJson(Context context, EMISION emision, AsyncInterface asyncInterface) {
        if (NetworkUtils.isNetworkAvailable()) {
            ServerGetter.getEmision(context, asyncInterface);
        } else {
            asyncInterface.onFinish(OfflineGetter.getEmision());
        }
    }

    public static void getJson(Context context, DOWNLOAD download, AsyncInterface asyncInterface) {
        if (NetworkUtils.isNetworkAvailable()) {
            SelfGetter.getDownload(context, download.url, asyncInterface);
        } else {
            asyncInterface.onFinish("null");
        }
    }

    public interface AsyncInterface {
        void onFinish(String json);
    }

}
