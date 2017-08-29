package knf.animeflv.JsonFactory;

import android.os.Looper;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;


public class ServerGetter {

    public static AsyncHttpClient getClient() {
        if (Looper.myLooper() == null) {
            return new SyncHttpClient();
        } else {
            return new AsyncHttpClient();
        }
    }
}
