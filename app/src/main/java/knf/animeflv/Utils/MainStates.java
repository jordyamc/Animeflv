package knf.animeflv.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.Nullable;
import android.widget.ImageButton;

import knf.animeflv.Utils.eNums.DownloadTask;
import pl.droidsonroids.gif.GifImageButton;

/**
 * Created by Jordy on 26/03/2016.
 */
public class MainStates {
    private static MainStates states;
    private static SharedPreferences preferences;
    private static boolean listing = false;
    private static boolean isprocessing = false;
    private static DownloadTask task;
    private static String UrlZippy;
    private static ImageButton imageButton;
    private static GifImageButton gifImageButton;
    private static ImageButton downState;
    private static String processingEid = "";
    private static boolean loadingEmision = true;
    private static int position = -1;
    private static Context context;

    public MainStates(Context context) {
        preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        MainStates.context = context;
    }

    public static MainStates init(Context con) {
        if (states == null) {
            states = new MainStates(con);
        }
        return states;
    }

    public static void setZippyState(DownloadTask task1, String url, ImageButton button, ImageButton state, int position) {
        task = task1;
        UrlZippy = url;
        imageButton = button;
        downState = state;
        MainStates.position = position;
    }

    public static void setZippyState(DownloadTask task1, String url, GifImageButton button, ImageButton state, int position) {
        task = task1;
        UrlZippy = url;
        gifImageButton = button;
        downState = state;
        MainStates.position = position;
    }

    public static int getPosition() {
        return position;
    }

    public static boolean isLoadingEmision() {
        return loadingEmision;
    }

    public static void setLoadingEmision(boolean loadingEmision) {
        MainStates.loadingEmision = loadingEmision;
    }

    public static ImageButton getDownStateButton() {
        return downState;
    }

    public static GifImageButton getGifDownButton() {
        return gifImageButton;
    }

    public static String getUrlZippy() {
        return UrlZippy;
    }

    public static DownloadTask getDowloadTask() {
        return task;
    }

    public static boolean isListing() {
        return listing;
    }

    public static void setListing(boolean value) {
        listing = value;
    }

    public static void setProcessing(boolean value, @Nullable String eid) {
        isprocessing = value;
        if (eid != null) {
            processingEid = eid;
        } else {
            processingEid = "null";
        }
    }

    public static String getProcessingEid() {
        return processingEid;
    }

    public static boolean isProcessing() {
        return isprocessing;
    }
}
