package knf.animeflv.JsonFactory;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.FileUtil;

public class SelfGetter {
    private static final String ua = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 UBrowser/5.7.15533.1010 Safari/537.36";

    public static void getInicio(final Context context, final BaseGetter.AsyncInterface asyncInterface) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Document main = Jsoup.connect("http://animeflv.net").userAgent(ua).cookie("dev", "1").get();
                    Element lista = main.getElementsByClass("ultimos_epis").first();
                    Elements caps = lista.getElementsByClass("not");
                    JSONObject object = new JSONObject();
                    object.put("version", context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName + "-Internal_Api");
                    object.put("cache", "0");
                    object.put("last", getCurrentHour());
                    JSONArray array = new JSONArray();
                    for (Element element : caps) {
                        Element link = element.getElementsByTag("a").first();
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
                    asyncInterface.onFinish(object.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    asyncInterface.onFinish(OfflineGetter.getInicio());
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static void getDownload(final Context context, final String url, final BaseGetter.AsyncInterface asyncInterface) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                String tit = "null";
                String num = "null";
                String aid = "null";
                String eid = "null";
                String izanagi = "null";
                String zippy = "null";
                String sync = "null";
                String mega = "null";
                String aflv = "null";
                String maru = "null";
                String Yotta = "null";
                String[] names = new String[]{"Izanagi", "Yotta", "Zippyshare", "4Sync", "Mega", "Animeflv", "Maru"};
                try {
                    Document main = Jsoup.connect(url).userAgent(ua).cookie("dev", "1").get();
                    String titinfo = main.select("div.episodio_titulo").first().getElementsByTag("h1").text();
                    tit = titinfo.substring(0, titinfo.lastIndexOf(" "));
                    num = titinfo.substring(titinfo.lastIndexOf(" ") + 1);
                    String link = main.select("meta[property='og:image']").attr("content");
                    aid = link.substring(link.lastIndexOf("/") + 1, link.lastIndexOf("."));
                    eid = aid + "_" + num + "E";
                    Elements descargas = main.select("a.op_download");
                    if (descargas.outerHtml().contains("zippyshare")) {
                        for (Element e : descargas) {
                            String z = e.attr("href");
                            if (z.contains("zippyshare")) {
                                zippy = z;
                                break;
                            }
                        }
                    }
                    Element script = main.select("div#contenido").first().getElementsByTag("script").last();
                    String j = script.outerHtml();
                    String json = j.substring(j.indexOf("{"), j.indexOf("}") + 1);
                    JSONObject js = new JSONObject(json);
                    Iterator<String> iterator = js.keys();
                    while (iterator.hasNext()) {
                        String el = js.get(iterator.next()).toString();
                        if (el.contains("izanagi")) {
                            String[] p = el.split("\",\"");
                            for (String r : p) {
                                if (r.contains("embed_izanagi.php")) {
                                    try {
                                        String body = Jsoup.connect("https://animeflv.net/embed_izanagi.php?key=" + r.substring(r.indexOf("key=") + 4, r.indexOf("\\\" "))).userAgent(ua).get().outerHtml();
                                        izanagi = new JSONObject(Jsoup.connect(body.substring(body.indexOf("get('") + 5, body.indexOf("',"))).userAgent(ua).get().body().text()).getString("file");
                                    } catch (Exception e) {

                                    }
                                }
                            }
                        } else if (el.contains("embed_yotta.php")) {
                            String[] p = el.split("\",\"");
                            for (String r : p) {
                                if (r.contains("embed_yotta.php")) {
                                    try {
                                        Yotta = new JSONObject(Jsoup.connect("http://s1.animeflv.net/yotta.php?id=" + r.substring(r.indexOf("key=") + 4, r.indexOf("\\\" "))).userAgent(ua).get().body().text()).getJSONArray("sources").getJSONObject(0).getString("file");
                                    } catch (Exception e) {

                                    }
                                }
                            }
                        } else if (el.contains("https://mega.nz")) {
                            String[] p = el.split("\",\"");
                            for (String r : p) {
                                if (r.contains("https://mega.nz")) {
                                    try {
                                        mega = r.substring(r.indexOf("https://mega.nz"), r.indexOf("\\\" "));
                                    } catch (Exception e) {

                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String[] links = new String[]{izanagi, Yotta, zippy, sync, mega, aflv, maru};
                try {
                    JSONObject object = new JSONObject();
                    object.put("version", context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName + "-Internal_Api");
                    object.put("cache", "0");
                    object.put("titulo", tit);
                    object.put("eid", eid);
                    JSONArray array = new JSONArray();
                    for (int i = 0; i < names.length; i++) {
                        JSONObject o = new JSONObject();
                        o.put("name", names[i]);
                        o.put("url", links[i]);
                        array.put(o);
                    }
                    object.put("downloads", array);
                    asyncInterface.onFinish(object.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    asyncInterface.onFinish("null");
                }
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
}