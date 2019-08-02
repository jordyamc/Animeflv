package knf.animeflv.JsonFactory;

import android.content.Context;
import androidx.annotation.NonNull;

import java.util.List;

import knf.animeflv.Directorio.AnimeClass;
import knf.animeflv.Directorio.DB.DirectoryHelper;
import knf.animeflv.JsonFactory.JsonTypes.ANIME;
import knf.animeflv.JsonFactory.JsonTypes.DIRECTORIO;
import knf.animeflv.JsonFactory.JsonTypes.DOWNLOAD;
import knf.animeflv.JsonFactory.JsonTypes.INICIO;
import knf.animeflv.Utils.NetworkUtils;
import knf.animeflv.VideoServers.Server;


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

    public static void getJson(Context context, DIRECTORIO directorio, AsyncProgressInterface asyncInterface) {
        if (NetworkUtils.isNetworkAvailable()) {
            SelfGetter.getDir(context, asyncInterface);
        } else {
            asyncInterface.onFinish(OfflineGetter.getDirectorio());
        }
    }

    public static void getJson(Context context, DIRECTORIO directorio, AsyncProgressDBInterface asyncInterface) {
        if (NetworkUtils.isNetworkAvailable()) {
            SelfGetter.getDirDB(context, asyncInterface);
        } else {
            asyncInterface.onFinish(DirectoryHelper.get(context).getAll());
        }
    }

    public static void getJson(Context context, ANIME anime, AsyncInterface asyncInterface) {
        if (NetworkUtils.isNetworkAvailable()) {
            SelfGetter.getAnime(context, anime, asyncInterface);
        } else {
            asyncInterface.onFinish(OfflineGetter.getAnime(anime));
        }
    }

    public static void getJson(Context context, DOWNLOAD download, AsyncDownloadInterface asyncInterface) {
        if (NetworkUtils.isNetworkAvailable()) {
            SelfGetter.getDownload(context, download, asyncInterface);
        } else {
            asyncInterface.onError("Network");
        }
    }

    public interface AsyncInterface {
        void onFinish(String json);
    }

    /*public interface AsyncDownloadInterface {
        void onFinish(@NonNull List<VideoServer> videoServers);

        void onError(String error);
    }*/

    public interface AsyncDownloadInterface {
        void onFinish(@NonNull List<Server> servers);

        void onError(String error);
    }

    public interface AsyncProgressInterface {
        void onFinish(String json);

        void onProgress(int progress);

        void onError(Throwable throwable);
    }

    public interface AsyncProgressDBInterface {
        void onFinish(List<AnimeClass> list);

        void onProgress(int progress);

        void onError(Throwable throwable);
    }

}
