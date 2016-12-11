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

import knf.animeflv.JsonFactory.JsonTypes.ANIME;
import knf.animeflv.Parser;
import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.FileUtil;

public class SelfGetter {
    private static final int TIMEOUT = 10000;
    private static final String ua = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 UBrowser/5.7.15533.1010 Safari/537.36";

    public static void getInicio(final Context context, final BaseGetter.AsyncInterface asyncInterface) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Document main = Jsoup.connect("http://animeflv.net").userAgent(ua).cookie("dev", "1").timeout(TIMEOUT).get();
                    Element lista = main.getElementsByClass("ultimos_epis").first();
                    Elements caps = main.select("div.not");
                    JSONObject object = new JSONObject();
                    object.put("version", context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName + "-Internal_Api");
                    object.put("cache", "0");
                    object.put("last", getCurrentHour());
                    JSONArray array = new JSONArray();
                    for (Element element : caps) {
                        Element link = element.select("a").first();
                        String title_semi = link.attr("title").trim();
                        String title = title_semi.substring(0, title_semi.lastIndexOf(" "));
                        String numero = title_semi.substring(title_semi.lastIndexOf(" ") + 1);
                        Element img = element.select("img").first();
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
                String Yotta480 = "null";
                String Yotta360 = "null";
                String[] names = new String[]{"Izanagi", "Yotta", "Yotta 480p", "Yotta 360p", "Zippyshare", "4Sync", "Mega", "Animeflv", "Maru"};
                try {
                    Log.e("Url", url);
                    Document main = Jsoup.connect(url).userAgent(ua).cookie("dev", "1").timeout(TIMEOUT).get();
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
                                        JSONArray ja = new JSONObject(Jsoup.connect("http://s1.animeflv.net/yotta.php?id=" + r.substring(r.indexOf("key=") + 4, r.indexOf("\\\" "))).userAgent(ua).get().body().text()).getJSONArray("sources");
                                        if (ja.length() > 1) {
                                            Yotta480 = ja.getJSONObject(0).getString("file");
                                            Yotta360 = ja.getJSONObject(1).getString("file");
                                        } else {
                                            Yotta = ja.getJSONObject(0).getString("file");
                                        }
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
                String[] links = new String[]{izanagi, Yotta, Yotta480, Yotta360, zippy, sync, mega, aflv, maru};
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

    public static void getAnime(final Context context, final ANIME anime, final BaseGetter.AsyncInterface asyncInterface) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Document document = Jsoup.connect(new Parser().getUrlAnimeCached(anime.getAidString())).userAgent(ua).cookie("dev", "1").timeout(TIMEOUT).get();
                    String imgUrl = document.select("img.portada").first().attr("src");
                    String aid = imgUrl.substring(imgUrl.lastIndexOf("/") + 1, imgUrl.lastIndexOf("."));
                    Elements infos = document.select("ul.ainfo").first().select("li");
                    String tid = infos.get(0).ownText();
                    String state = getState(infos.get(1).select("span").first().attr("class"));
                    String generos = infos.get(2).text().replace("Generos:", "");
                    String start = infos.get(3).ownText();
                    String title = document.select("h1").first().text();
                    String sinopsis = Parser.InValidateSinopsis(document.select("div.sinopsis").text().trim());
                    Elements epis = document.select("ul.anime_episodios").first().select("li");
                    JSONArray array = new JSONArray();
                    for (Element ep : epis) {
                        JSONObject object = new JSONObject();
                        String name = ep.select("a").first().ownText().trim();
                        String num = name.substring(name.lastIndexOf(" ") + 1);
                        object.put("num", num);
                        object.put("eid", aid + "_" + num + "E");
                        array.put(object);
                    }
                    Elements rels = document.select("div.relacionados").first().select("li");
                    JSONArray j_rels = new JSONArray();
                    for (Element rel : rels) {
                        String t_tid = rel.select("b").first().ownText();
                        Element link = rel.select("a").first();
                        String full_link = "http://animeflv.net" + link.attr("href");
                        String name = link.ownText();
                        String type = rel.ownText().trim().replace("(", "").replace(")", "").replace(":", "");
                        Document t_document = Jsoup.connect(full_link).userAgent(ua).cookie("dev", "1").get();
                        String t_imgUrl = t_document.select("img.portada").first().attr("src");
                        String t_aid = t_imgUrl.substring(t_imgUrl.lastIndexOf("/") + 1, t_imgUrl.lastIndexOf("."));
                        JSONObject object = new JSONObject();
                        object.put("aid", t_aid);
                        object.put("tid", type);
                        object.put("titulo", name);
                        object.put("rel_tipo", t_tid);
                        j_rels.put(object);
                    }
                    JSONObject fobject = new JSONObject();
                    fobject.put("cache", "0");
                    fobject.put("aid", aid);
                    fobject.put("tid", tid);
                    fobject.put("titulo", title);
                    fobject.put("sinopsis", sinopsis);
                    fobject.put("fecha_inicio", start);
                    fobject.put("fecha_fin", state);
                    fobject.put("generos", generos);
                    fobject.put("episodios", array);
                    fobject.put("relacionados", j_rels);
                    asyncInterface.onFinish(fobject.toString());
                } catch (Exception e) {
                    asyncInterface.onFinish(OfflineGetter.getAnime(anime));
                    e.printStackTrace();
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    private static String getState(String className) {
        if (className.contains("1")) {
            return "0000-00-00";
        } else if (className.contains("3")) {
            return "prox";
        } else {
            return "1";
        }
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