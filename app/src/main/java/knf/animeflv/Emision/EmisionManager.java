package knf.animeflv.Emision;

import android.app.Activity;
import android.os.Looper;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import knf.animeflv.Parser;
import knf.animeflv.TaskType;

public class EmisionManager {
    private static String getBaseUrl(Activity activity) {
        return new Parser().getBaseUrl(TaskType.NORMAL, activity) + "emision-control.php?certificate=" + Parser.getCertificateSHA1Fingerprint(activity);
    }

    private static AsyncHttpClient getClient() {
        if (Looper.myLooper() == null)
            return new SyncHttpClient();
        return new AsyncHttpClient();
    }

    public static void get(Activity activity, String aid, final ServerListener listener) {
        AsyncHttpClient client = getClient();
        client.setTimeout(10000);
        client.get(getBaseUrl(activity) + "&aid=" + aid + "&get", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                listener.OnServerResponse(new EmisionObject(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                listener.OnServerResponse(new EmisionObject());
            }
        });
    }

    public static void edit(Activity activity, EmisionObject object, final ServerListener listener) {
        AsyncHttpClient client = getClient();
        client.setTimeout(10000);
        client.get(getBaseUrl(activity) + getEditString(object), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                listener.OnServerResponse(new EmisionObject(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                listener.OnServerResponse(new EmisionObject());
            }
        });
    }

    private static String getEditString(EmisionObject object) {
        if (object.exist) {
            return "&aid=" + object.aid + "&title=" + object.getCodedTitle() + "&daycode=" + object.daycode + "&hour=" + object.getUTCHour() + "&edit";
        } else {
            return "&aid=" + object.aid + "&edit&delete";
        }
    }

    public interface ServerListener {
        void OnServerResponse(EmisionObject emisionObject);
    }
}
