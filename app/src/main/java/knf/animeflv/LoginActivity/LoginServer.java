package knf.animeflv.LoginActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import knf.animeflv.Parser;
import knf.animeflv.TaskType;
import knf.animeflv.Utils.ExecutorManager;

public class LoginServer {
    private static AsyncHttpClient syncHttpClient = new SyncHttpClient();
    private static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    private static AsyncHttpClient getClient() {
        if (Looper.myLooper() == null) return syncHttpClient;
        return asyncHttpClient;
    }

    private static String getServerUrl(Context context) {
        return new Parser().getBaseUrl(TaskType.NORMAL, context) + "fav-server.php?certificate=" + Parser.getCertificateSHA1Fingerprint(context);
    }

    public static void login(Activity activity, String email_c, String pass_c, String email_normal, final ServerInterface serverInterface) {
        AsyncHttpClient client = getClient();
        client.setTimeout(10000);
        client.get(activity, getServerUrl(activity) + "&tipo=get&email_coded=" + email_c + "&pass_coded=" + pass_c + "&email_normal=" + email_normal, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                serverInterface.onServerResponse(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                serverInterface.onServerError();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                serverInterface.onServerError();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                serverInterface.onServerError();
            }
        });
    }

    public static void signup(Activity activity, String email_c, String pass_c, final ServerInterface serverInterface) {
        AsyncHttpClient client = getClient();
        client.setTimeout(10000);
        client.get(activity, getServerUrl(activity) + "&tipo=nCuenta&email_coded=" + email_c + "&pass_coded=" + pass_c + getFavsServerCode(activity), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                serverInterface.onServerResponse(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                serverInterface.onServerError();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                serverInterface.onServerError();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                serverInterface.onServerError();
            }
        });
    }

    public static void cAccount(Activity activity, String email_c, String pass_c, String new_email_c, String new_pass_c, String email_normal, String new_email_normal, final ServerInterface serverInterface) {
        AsyncHttpClient client = getClient();
        client.setTimeout(10000);
        client.get(activity, getServerUrl(activity) + "&tipo=cAccount&past_email_coded=" + email_c + "&new_email_coded=" + new_email_c + "&pass_coded=" + pass_c + "&new_pass_coded=" + new_pass_c + "&email_normal=" + email_normal + "&new_email_normal" + new_email_normal, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                serverInterface.onServerResponse(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                serverInterface.onServerError();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                serverInterface.onServerError();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                serverInterface.onServerError();
            }
        });
    }

    public static void cEmail(Activity activity, String email_c, String new_email_c, String pass_c, String email_normal, String new_email_normal, final ServerInterface serverInterface) {
        AsyncHttpClient client = getClient();
        client.setTimeout(10000);
        client.get(activity, getServerUrl(activity) + "&tipo=cCuenta&past_email_coded=" + email_c + "&new_email_coded=" + new_email_c + "&pass_coded=" + pass_c + "&email_normal=" + email_normal + "&new_email_normal" + new_email_normal, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                serverInterface.onServerResponse(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                serverInterface.onServerError();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                serverInterface.onServerError();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                serverInterface.onServerError();
            }
        });
    }

    public static void cPass(Activity activity, String email_c, String pass_c, String new_pass_c, final ServerInterface serverInterface) {
        AsyncHttpClient client = getClient();
        client.setTimeout(10000);
        client.get(activity, getServerUrl(activity) + "&tipo=cPass&email_coded=" + email_c + "&pass_coded=" + pass_c + "&new_pass_coded=" + new_pass_c, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                serverInterface.onServerResponse(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                serverInterface.onServerError();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                serverInterface.onServerError();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                serverInterface.onServerError();
            }
        });
    }

    public static void RefreshData(final Activity activity) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                String email_c = preferences.getString("login_email_coded", "null");
                if (!email_c.equals("null")) {
                    String pass_c = preferences.getString("login_pass_coded", "null");
                    String[] favoritosNo = {activity.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", "")};
                    List<String> Listno = new ArrayList<String>(Arrays.asList(favoritosNo));
                    String[] favoritos = new String[Listno.size()];
                    Listno.toArray(favoritos);
                    StringBuilder builderNo = new StringBuilder();
                    for (String i : favoritos) {
                        builderNo.append(":::" + i);
                    }
                    String vistos = activity.getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", "");
                    AsyncHttpClient client = getClient();
                    client.setTimeout(10000);
                    client.get(activity, getServerUrl(activity) + "&tipo=refresh&email_coded=" + email_c + "&pass_coded=" + pass_c + "&new_favs=" + builderNo.toString() + ":;:" + vistos, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                        }
                    });
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());

    }

    public static void RefreshData(final Activity activity, final ServerInterface serverInterface) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                String email_c = preferences.getString("login_email_coded", "null");
                if (!email_c.equals("null")) {
                    String pass_c = preferences.getString("login_pass_coded", "null");
                    String[] favoritosNo = {activity.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", "")};
                    List<String> Listno = new ArrayList<String>(Arrays.asList(favoritosNo));
                    String[] favoritos = new String[Listno.size()];
                    Listno.toArray(favoritos);
                    StringBuilder builderNo = new StringBuilder();
                    for (String i : favoritos) {
                        builderNo.append(":::" + i);
                    }
                    String vistos = activity.getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", "");
                    AsyncHttpClient client = getClient();
                    client.setTimeout(10000);
                    client.get(activity, getServerUrl(activity) + "&tipo=refresh&email_coded=" + email_c + "&pass_coded=" + pass_c + "&new_favs=" + builderNo.toString() + ":;:" + vistos, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            serverInterface.onServerResponse(response);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            serverInterface.onServerError();
                        }
                    });
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static void addEmail(Activity activity, String email, String email_coded, final ServerInterface serverInterface) {
        AsyncHttpClient client = getClient();
        client.setTimeout(10000);
        client.get(activity, getServerUrl(activity) + "&tipo=addU&email_normal=" + email + "&email_coded=" + email_coded, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                serverInterface.onServerResponse(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                serverInterface.onServerError();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                serverInterface.onServerError();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                serverInterface.onServerError();
            }
        });
    }

    public static void getUserList(final Activity activity, final ListResponse listResponse) {
        AsyncHttpClient client = getClient();
        client.setTimeout(10000);
        client.get(activity, getServerUrl(activity) + "&tipo=list", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONArray array = response.getJSONArray("list");
                    List<String> list = new ArrayList<String>();
                    for (int i = 0; i < array.length(); i++) {
                        list.add(array.getString(i));
                    }
                    listResponse.onUserListCreated(list);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listResponse.onUserListCreated(null);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }
        });
    }

    public static String getFavsServerCode(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences("data", Context.MODE_PRIVATE);
        return "&fav_code=" + preferences.getString("favoritos", "") + ":;:" + preferences.getString("vistos", "");
    }

    public interface ServerInterface {
        void onServerResponse(JSONObject object);

        void onServerError();
    }

    public interface ListResponse {
        void onUserListCreated(@Nullable List<String> list);
    }
}
