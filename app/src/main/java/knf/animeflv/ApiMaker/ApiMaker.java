package knf.animeflv.ApiMaker;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.NoLogInterface;

public class ApiMaker {
    private static final String URL_INICIO = "http://animeflv.net";
    private static final String URL_DIRECTORIO = "http://animeflv.net/ajax/animes/lista_completa";
    private static final String TAG_BASE = "ApiMaker";
    private static final String TAG_INICIO = TAG_BASE + "-INICIO";
    private static final String TAG_DIRECTORIO = TAG_BASE + "-DIRECTORIO";

    public static void Inicio(final Activity activity, final OnParseListener listener) {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                SyncHttpClient client = new SyncHttpClient();
                client.setLogInterface(new NoLogInterface());
                client.setResponseTimeout(10000);
                client.setLoggingEnabled(false);
                client.get(URL_INICIO, null, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d(TAG_INICIO, "Parse Error");
                        if (throwable instanceof SocketTimeoutException) {
                            Log.e(TAG_INICIO, "TimeOut!!!!");
                        } else {
                            throwable.printStackTrace();
                        }
                        listener.onError();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            Document main = Jsoup.parse(responseString);
                            Element lista = main.getElementsByClass("ultimos_epis").first();
                            Elements caps = lista.getElementsByClass("not");
                            JSONObject object = new JSONObject();
                            object.put("version", activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName + "-Internal_Api");
                            object.put("cache", "0");
                            object.put("last", getCurrentHour());
                            JSONArray array = new JSONArray();
                            for (Element element : caps) {
                                Element link = element.getElementsByTag("a").first();
                                //String href=URL_INICIO+link.attr("href");
                                String title_semi = link.attr("title").trim();
                                String title = title_semi.substring(0, title_semi.lastIndexOf(" "));
                                String numero = title_semi.substring(title_semi.lastIndexOf(" ") + 1);
                                Element noscript = link.getElementsByTag("noscript").first();
                                Element img = noscript.getElementsByTag("img").first();
                                String img_url = img.attr("src");
                                String aid = img_url.substring(img_url.lastIndexOf("/") + 1, img_url.lastIndexOf("."));
                                String eid = aid + "_" + numero + "E";
                                if (FileUtil.isNumber(numero.trim())) {
                                    JSONObject anime = new JSONObject();
                                    anime.put("aid", aid);
                                    anime.put("titulo", title);
                                    anime.put("numero", numero);
                                    anime.put("tid", getTid(element));
                                    anime.put("eid", eid);
                                    array.put(anime);
                                    Log.d("Add Anime",
                                            "Aid: " + aid + "\n" +
                                                    "Eid: " + eid + "\n" +
                                                    "Title: " + title + "\n" +
                                                    "#: " + numero + "\n" +
                                                    "tid: " + getTid(element) + "\n");
                                }
                            }
                            object.put("lista", array);
                            Log.d(TAG_INICIO, "Finish Parsing");
                            listener.onParsed(object.toString());
                        } catch (Exception e) {
                            Log.d(TAG_INICIO, "Parse Error");
                            e.printStackTrace();
                            listener.onError();
                        }
                    }
                });
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static void Directorio(final OnParseListener listener) {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                SyncHttpClient client = new SyncHttpClient();
                client.setLogInterface(new NoLogInterface());
                client.setLoggingEnabled(false);
                client.setTimeout(10000);
                client.get(URL_DIRECTORIO, null, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d(TAG_DIRECTORIO, "Parse Error");
                        if (throwable instanceof SocketTimeoutException) {
                            Log.e(TAG_DIRECTORIO, "TimeOut!!!!");
                        } else {
                            throwable.printStackTrace();
                        }
                        listener.onError();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            String semi_dir = responseString.replace("var lanime=", "").substring(0, -1);
                            JSONObject object = new JSONObject();
                            object.put("cache", "0");
                            object.put("lista", new JSONArray(semi_dir));
                            Log.d(TAG_DIRECTORIO, "Finish Parsing");
                            listener.onParsed(object.toString());
                        } catch (Exception e) {
                            Log.d(TAG_DIRECTORIO, "Parse Error");
                            e.printStackTrace();
                            listener.onError();
                        }
                    }
                });
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    private static String getTid(Element element) {
        if (element.getElementsByClass("tova").first() != null) {
            return "OVA";
        } else if (element.getElementsByClass("tpeli").first() != null) {
            return "Pelicula";
        } else {
            return "Anime";
        }
    }

    private static String getCurrentHour() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mmaa", Locale.ENGLISH);
        return simpleDateFormat.format(new Date());
    }

    public interface OnParseListener {
        void onParsed(String json);

        void onError();
    }
}
