package knf.animeflv.JsonFactory;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import knf.animeflv.JsonFactory.JsonTypes.ANIME;
import knf.animeflv.JsonFactory.JsonTypes.DOWNLOAD;
import knf.animeflv.Parser;
import knf.animeflv.TaskType;
import knf.animeflv.Utils.NoLogInterface;


public class ServerGetter {
    private static String getInicio(Context context) {
        return new Parser().getInicioUrl(TaskType.NORMAL, context);
    }

    private static String getDirectorio(Context context) {
        return new Parser().getDirectorioUrl(TaskType.NORMAL, context);
    }

    public static void getInicio(final Context context, final BaseGetter.AsyncInterface asyncInterface) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setLogInterface(new NoLogInterface());
        asyncHttpClient.setLoggingEnabled(false);
        asyncHttpClient.setResponseTimeout(5000);
        asyncHttpClient.get(getInicio(context) + "?certificate=" + new Parser().getCertificateSHA1Fingerprint(context), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                OfflineGetter.backupJson(response, OfflineGetter.inicio);
                asyncInterface.onFinish(response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("Server", "GETTER FAIL", throwable);
                SelfGetter.getInicio(context, asyncInterface);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("Server", "GETTER FAIL - TEXT", throwable);
                SelfGetter.getInicio(context, asyncInterface);
            }
        });
    }

    public static void getDir(final Context context, final BaseGetter.AsyncInterface asyncInterface) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setLogInterface(new NoLogInterface());
        asyncHttpClient.setLoggingEnabled(false);
        asyncHttpClient.setResponseTimeout(5000);
        asyncHttpClient.get(getDirectorio(context) + "?certificate=" + new Parser().getCertificateSHA1Fingerprint(context), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                OfflineGetter.backupJson(response, OfflineGetter.directorio);
                asyncInterface.onFinish(response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("Server", "GETTER FAIL", throwable);
                asyncInterface.onFinish(OfflineGetter.getDirectorio());
            }
        });
    }

    public static void getAnime(Context context, ANIME anime, BaseGetter.AsyncInterface asyncInterface) {

    }

    public static void getDownload(final Context context, final DOWNLOAD download, final BaseGetter.AsyncInterface asyncInterface) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setLogInterface(new NoLogInterface());
        asyncHttpClient.setLoggingEnabled(false);
        asyncHttpClient.setResponseTimeout(5000);
        asyncHttpClient.get(getInicio(context) + "?certificate=" + new Parser().getCertificateSHA1Fingerprint(context) + "&url=" + download.url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                asyncInterface.onFinish(response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("Server1", "GETTER FAIL", throwable);
                SelfGetter.getDownload(context, download.url, asyncInterface);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("Server2", "GETTER FAIL", throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("Server3", "GETTER FAIL", throwable);
            }
        });
    }

    public static void getEmision(Context context, final BaseGetter.AsyncInterface asyncInterface) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setLogInterface(new NoLogInterface());
        asyncHttpClient.setLoggingEnabled(false);
        asyncHttpClient.setResponseTimeout(5000);
        asyncHttpClient.get(new Parser().getBaseUrl(TaskType.NORMAL, context) + "emisionlist-test.php", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                OfflineGetter.backupJson(response, OfflineGetter.emision);
                asyncInterface.onFinish(response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("Server", "GETTER FAIL", throwable);
                asyncInterface.onFinish(OfflineGetter.getEmision());
            }
        });
    }

    public static void backupDir(final Context context) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setLogInterface(new NoLogInterface());
        asyncHttpClient.setLoggingEnabled(false);
        asyncHttpClient.setResponseTimeout(5000);
        asyncHttpClient.get(getDirectorio(context) + "?certificate=" + new Parser().getCertificateSHA1Fingerprint(context), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                OfflineGetter.backupJson(response, OfflineGetter.directorio);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }
}
