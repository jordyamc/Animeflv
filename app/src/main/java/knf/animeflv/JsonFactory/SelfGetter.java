package knf.animeflv.JsonFactory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import knf.animeflv.Cloudflare.BypassHolder;
import knf.animeflv.Directorio.DB.DirectoryDB;
import knf.animeflv.Directorio.DB.DirectoryHelper;
import knf.animeflv.JsonFactory.JsonTypes.ANIME;
import knf.animeflv.JsonFactory.JsonTypes.DOWNLOAD;
import knf.animeflv.JsonFactory.JsonTypes.INICIO;
import knf.animeflv.JsonFactory.Objects.AnimeInfo;
import knf.animeflv.JsonFactory.Objects.VideoServer;
import knf.animeflv.Parser;
import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.KUtilsKt;
import knf.animeflv.VideoServers.Server;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class SelfGetter {
    public static final int TIMEOUT = 10000;
    public static final String ua = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 UBrowser/5.7.15533.1010 Safari/537.36";

    //FIXME:OVA PELICULA

    public static void getInicio(final Context context, final INICIO inicio, final BaseGetter.AsyncInterface asyncInterface) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Document main;
                    if (inicio.type == 0) {
                        main = Jsoup.connect("http://animeflv.net").userAgent(BypassHolder.getUserAgent()).cookies(BypassHolder.getBasicCookieMap()).timeout(TIMEOUT).get();
                    } else {
                        main = Jsoup.connect("http://animeflv.net").userAgent(BypassHolder.getUserAgent(context)).cookies(BypassHolder.getBasicCookieMap(context)).timeout(TIMEOUT).get();
                    }
                    Element list = main.select("ul.ListEpisodios.AX.Rows.A06.C04.D03").last();
                    Elements caps = list.select("li");
                    JSONObject object = new JSONObject();
                    object.put("version", context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName + "-Internal_Api");
                    object.put("cache", "0");
                    object.put("last", getCurrentHour());
                    JSONArray array = new JSONArray();
                    for (Element element : caps) {
                        String title = element.select("strong.Title").first().ownText();
                        String numero_semi = element.select("span.Capi").first().ownText();
                        String numero = numero_semi.substring(numero_semi.lastIndexOf(" ") + 1);
                        Element img = element.select("img[src]").first();
                        String img_url = img.attr("src");
                        String aid = img_url.substring(img_url.lastIndexOf("/") + 1, img_url.lastIndexOf("."));
                        String eid = aid + "_" + numero + "E";
                        String sid = element.select("a").first().attr("href").split("/")[2];
                        if (FileUtil.isNumber(numero.trim())) {
                            JSONObject anime = new JSONObject();
                            anime.put("aid", aid);
                            anime.put("titulo", title);
                            anime.put("numero", numero);
                            anime.put("tid", getTid(element));
                            anime.put("eid", eid);
                            anime.put("sid", sid);
                            array.put(anime);
                        }
                    }
                    object.put("lista", array);
                    if (inicio.type == 0)
                        OfflineGetter.backupJson(object, OfflineGetter.inicio);
                    asyncInterface.onFinish(object.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    if (inicio.type != 0) {
                        asyncInterface.onFinish("null");
                    } else {
                        asyncInterface.onFinish(OfflineGetter.getInicio());
                    }
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static void getDownload(final Context context, final DOWNLOAD download, final BaseGetter.AsyncDownloadInterface asyncInterface) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                getAnime(context, new ANIME(download.eid.split("_")[0]), new BaseGetter.AsyncInterface() {
                    @Override
                    public void onFinish(String json) {
                        try {
                            JSONArray array = new JSONObject(json).getJSONArray("episodios");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                if (object.getString("eid").equals(download.eid)) {
                                    /*List<VideoServer> videoServers = getDownloadServers(context, DirectoryHelper.get(context).getEpUrl(download.eid, object.getString("sid")));
                                    if (videoServers != null) {
                                        asyncInterface.onFinish(videoServers);
                                    } else {
                                        asyncInterface.onError("null");
                                    }*/
                                    List<Server> servers = getServers(context, DirectoryHelper.get(context).getEpUrl(download.eid, object.getString("sid")));
                                    if (servers != null) {
                                        asyncInterface.onFinish(servers);
                                    } else {
                                        asyncInterface.onError("null");
                                    }
                                    return;
                                }
                            }
                            asyncInterface.onError("Ep not found");
                            Crashlytics.logException(new FileNotFoundException("Not found: " + download.eid + " in:\n\n" + array.toString()));
                        } catch (Exception e) {
                            Crashlytics.logException(e);
                            if (json.startsWith("error") && json.contains("503")) {
                                asyncInterface.onError(json);
                            } else {
                                asyncInterface.onError(e.getMessage());
                            }
                        }
                    }
                });
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static String[] getDownloadServersOptions() {
        ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(new String[]{"Ninguno"}));
        arrayList.addAll(Arrays.asList(VideoServer.Names.getDownloadServers()));
        return arrayList.toArray(new String[0]);
    }

    public static String[] getDownloadServers() {
        return new String[]{"Izanagi", "Minhateca", "Yotta", "Yotta 720p", "Yotta 480p", "Yotta 360p", "Hyperion", "Hyperion Direct", "Hyperion 360p", "Hyperion 480p", "Hyperion 720p", "Okru SD", "Okru HD", "Clup", /*"Openload",*/ "Mp4Upload", "YourUpload", "Zippyshare", "4Sync", "Mega", "Animeflv", "Maru"};
    }

    private static List<Server> getServers(final Context context, final String url) {
        try {
            Log.e("Url", url);
            Document main = Jsoup.connect(url).userAgent(BypassHolder.getUserAgent()).cookies(BypassHolder.getBasicCookieMap()).timeout(TIMEOUT).get();
            Elements descargas = main.select("table.RTbl.Dwnl").first().select("a.Button.Sm.fa-download");
            final List<Server> servers = new ArrayList<>();
            for (Element e : descargas) {
                String z = e.attr("href");
                z = z.substring(z.lastIndexOf("http"));
                Server server = Server.check(context, z);
                if (server != null)
                    servers.add(server);
            }
            Elements s_script = main.select("script");
            String j = "";
            for (Element element : s_script) {
                String s_el = element.outerHtml();
                if (s_el.contains("var videos = {")) {
                    j = s_el;
                    break;
                }
            }
            JSONArray array = new JSONObject(KUtilsKt.regexFindFirst("\\{\"[SUBLAT]+\":\\[.*\\]\\}", j)).getJSONArray("SUB");
            KUtilsKt.forEachJsonArray(array, new Function1<JSONObject, Unit>() {
                @Override
                public Unit invoke(JSONObject jsonObject) {
                    Server server = Server.check(context, jsonObject.optString("code"));
                    if (server != null)
                        servers.add(server);
                    return Unit.INSTANCE;
                }
            });
            /*String[] parts = j.substring(j.indexOf("var video = [];") + 14, j.indexOf("$(document).ready(function()")).split("video\\[[^a-z]*\\]");
            for (String baseLink : parts) {
                Server server = Server.check(context, baseLink);
                if (server != null)
                    servers.add(server);
            }*/
            Collections.sort(servers);
            return servers;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    public static void getAnime(final Context context, final ANIME anime, final BaseGetter.AsyncInterface asyncInterface) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Log.e("Get Anime Info", DirectoryHelper.get(context).getAnimeUrl(anime.getAidString()));
                    Document document = Jsoup.connect(DirectoryHelper.get(context).getAnimeUrl(anime.getAidString())).userAgent(BypassHolder.getUserAgent()).cookies(BypassHolder.getBasicCookieMap()).timeout(TIMEOUT).get();
                    String imgUrl = document.select("div.Image").first().select("img[src]").first().attr("src");
                    String aid = imgUrl.substring(imgUrl.lastIndexOf("/") + 1, imgUrl.lastIndexOf("."));
                    String tid = document.select("div.Ficha").first().select("div.Container").first().select("span").first().attr("class");
                    String state = getState(document.select("aside.SidebarA.BFixed").first().select("p").last().attr("class"));
                    String rate_stars;
                    String rate_count;
                    try {
                        rate_stars = document.select("span.vtprmd").first().text();
                    } catch (Exception e) {
                        rate_stars = "0.0";
                    }
                    try {
                        rate_count = document.select("span#votes_nmbr").first().text();
                    } catch (Exception e) {
                        rate_count = "0";
                    }
                    Elements categories = document.select("nav.Nvgnrs").first().select("a");
                    String generos = "";
                    String gens = "";
                    for (Element gen : categories) {
                        gens += gen.ownText();
                        gens += ", ";
                    }
                    if (!gens.trim().equals(""))
                        generos = gens.substring(0, gens.lastIndexOf(","));
                    String start = "Sin Fecha";
                    String title = document.select("meta[property='og:title']").attr("content");
                    Matcher matcher = Pattern.compile("^ ?V?e?r? ?A?n?i?m?e? ?(.+ ?O?n?l?i?n?e?) Online").matcher(title);
                    matcher.find();
                    title = matcher.group(1).trim();
                    String sinopsis = Parser.InValidateSinopsis(document.select("div.Description").text().trim());
                    JSONArray array = new JSONArray();
                    try {
                        try {
                            tryNewEpMethod(document, aid, array);
                        } catch (NullPointerException e) {
                            tryOldEpMethod(document, aid, title, array);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Crashlytics.logException(e);
                        Log.e("Get Anime Info", "No Ep List");
                    }
                    JSONArray j_rels = new JSONArray();
                    try {
                        Elements rels = document.select("ul.ListAnmRel").first().select("li");
                        for (Element rel : rels) {
                            Element link = rel.select("a").first();
                            String full_link = "http://animeflv.net" + link.attr("href");
                            String name = link.ownText();
                            String t_rel_t = rel.ownText();
                            String type = t_rel_t.substring(t_rel_t.indexOf("(") + 1, t_rel_t.lastIndexOf(")")).trim();
                            Document t_document = Jsoup.connect(full_link).userAgent(BypassHolder.getUserAgent()).cookies(BypassHolder.getBasicCookieMap()).timeout(10000).get();
                            String t_imgUrl = t_document.select("div.Image").first().select("img[src]").first().attr("src");
                            String t_aid = t_imgUrl.substring(t_imgUrl.lastIndexOf("/") + 1, t_imgUrl.lastIndexOf("."));
                            String rel_t = t_document.select("div.Ficha").first().select("div.Container").first().select("span").first().attr("class");
                            JSONObject object = new JSONObject();
                            object.put("aid", t_aid);
                            object.put("tid", rel_t);
                            object.put("titulo", name);
                            object.put("rel_tipo", type);
                            j_rels.put(object);
                        }
                    } catch (Exception e) {
                        Log.e("Get Anime Info", "No Rel List");
                    }
                    JSONObject fobject = new JSONObject();
                    fobject.put("cache", "0");
                    fobject.put("aid", aid);
                    fobject.put("tid", tid);
                    fobject.put("titulo", title);
                    fobject.put("sinopsis", sinopsis);
                    fobject.put("fecha_inicio", start);
                    fobject.put("fecha_fin", state);
                    fobject.put("rating_value", rate_stars);
                    fobject.put("rating_count", rate_count);
                    fobject.put("generos", generos);
                    fobject.put("episodios", array);
                    fobject.put("relacionados", j_rels);
                    OfflineGetter.backupJson(fobject, new File(OfflineGetter.animecache, anime.getAidString() + ".txt"));
                    asyncInterface.onFinish(fobject.toString());
                } catch (HttpStatusException he) {
                    asyncInterface.onFinish("error:" + he.getStatusCode());
                } catch (Exception e) {
                    asyncInterface.onFinish(OfflineGetter.getAnime(anime));
                    e.printStackTrace();
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static String getSize(String fileUrl) {
        try {
            URLConnection connection = new URL(fileUrl).openConnection();
            connection.connect();
            String size = connection.getHeaderField("content-length");
            switch (size) {
                case "empty":
                case "null":
                    return "-1";
                default:
                    return size;

            }
        } catch (Exception e) {
            e.printStackTrace();
            return "-1";
        }
    }

    public static String getAnimeSync(final Context context, final ANIME anime) {
        try {
            Log.e("Get Anime Info", DirectoryHelper.get(context).getAnimeUrl(anime.getAidString()));
            Document document = Jsoup.connect(DirectoryHelper.get(context).getAnimeUrl(anime.getAidString())).userAgent(BypassHolder.getUserAgent()).cookies(BypassHolder.getBasicCookieMap()).timeout(TIMEOUT).get();
            String imgUrl = document.select("div.Image").first().select("img[src]").first().attr("src");
            String aid = imgUrl.substring(imgUrl.lastIndexOf("/") + 1, imgUrl.lastIndexOf("."));
            String tid = document.select("div.Ficha").first().select("div.Container").first().select("span").first().attr("class");
            String state = getState(document.select("aside.SidebarA.BFixed").first().select("p").last().attr("class"));
            String rate_stars = document.select("meta[itemprop='ratingValue']").first().attr("content");
            String rate_count = document.select("meta[itemprop='ratingCount']").first().attr("content");
            Elements categories = document.select("nav.Nvgnrs").first().select("a");
            String generos = "";
            String gens = "";
            for (Element gen : categories) {
                gens += gen.ownText();
                gens += ", ";
            }
            if (!gens.trim().equals(""))
                generos = gens.substring(0, gens.lastIndexOf(","));
            String start = "Sin Fecha";

            String title = document.select("h1.Title").first().text();
            if (title.trim().equals("") || title.contains("protected"))
                title = document.select("meta[property='og:title']").attr("content").replace(" Online", "");
            String sinopsis = Parser.InValidateSinopsis(document.select("div.Description").first().select("p").first().text().trim());
            JSONArray array = new JSONArray();
            try {
                try {
                    tryNewEpMethod(document, aid, array);
                } catch (NullPointerException e) {
                    tryOldEpMethod(document, aid, title, array);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);
                Log.e("Get Anime Info", "No Ep List");
            }
            JSONArray j_rels = new JSONArray();
            try {
                Elements rels = document.select("ul.ListAnmRel").first().select("li");
                for (Element rel : rels) {
                    //FIXME: Buscar diferencia de Precuela/Secuela
                    //String t_tid = rel.select("b").first().ownText();
                    Element link = rel.select("a").first();
                    String full_link = "http://animeflv.net" + link.attr("href");
                    String name = link.ownText();
                    String t_rel_t = rel.ownText();
                    String type = t_rel_t.substring(t_rel_t.indexOf("(") + 1, t_rel_t.lastIndexOf(")")).trim();
                    Document t_document = Jsoup.connect(full_link).userAgent(BypassHolder.getUserAgent()).cookies(BypassHolder.getBasicCookieMap()).timeout(10000).get();
                    String t_imgUrl = t_document.select("div.Image").first().select("img[src]").first().attr("src");
                    String t_aid = t_imgUrl.substring(t_imgUrl.lastIndexOf("/") + 1, t_imgUrl.lastIndexOf("."));
                    String rel_t = t_document.select("div.Ficha").first().select("div.Container").first().select("span").first().attr("class");
                    JSONObject object = new JSONObject();
                    object.put("aid", t_aid);
                    object.put("tid", rel_t);
                    object.put("titulo", name);
                    object.put("rel_tipo", type);
                    j_rels.put(object);
                }
            } catch (Exception e) {
                Log.e("Get Anime Info", "No Rel List");
            }
            JSONObject fobject = new JSONObject();
            fobject.put("cache", "0");
            fobject.put("aid", aid);
            fobject.put("tid", tid);
            fobject.put("titulo", title);
            fobject.put("sinopsis", sinopsis);
            fobject.put("fecha_inicio", start);
            fobject.put("fecha_fin", state);
            fobject.put("rating_value", rate_stars);
            fobject.put("rating_count", rate_count);
            fobject.put("generos", generos);
            fobject.put("episodios", array);
            fobject.put("relacionados", j_rels);
            OfflineGetter.backupJson(fobject, new File(OfflineGetter.animecache, anime.getAidString() + ".txt"));
            return fobject.toString();
        } catch (HttpStatusException he) {
            return "error:" + he.getStatusCode();
        } catch (Exception e) {
            e.printStackTrace();
            return OfflineGetter.getAnime(anime);
        }
    }

    private static Element findDataScript(Elements scripts) {
        try {
            for (Element element : scripts)
                if (element.html().contains("var anime_info"))
                    return element;
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private static void tryNewEpMethod(Document document, String aid, JSONArray array) throws Exception {
        AnimeInfo info = new AnimeInfo(findDataScript(document.select("script:not([src])")).html());
        for (Map.Entry<String, String> entry : info.epMap.entrySet()) {
            JSONObject object = new JSONObject();
            String num = entry.getKey();
            String s_id = entry.getValue();
            object.put("num", num);
            object.put("eid", aid + "_" + num + "E");
            object.put("sid", s_id);
            array.put(object);
        }
    }

    private static void tryOldEpMethod(Document document, String aid, String title, JSONArray array) throws Exception {
        Elements epis = document.select("ul.ListEpisodes").first().select("li");
        for (Element ep : epis) {
            JSONObject object = new JSONObject();
            String name = ep.select("a").first().text().trim();
            String num = name.replace(title, "").trim();
            String s_id = ep.select("a").first().attr("href").split("/")[2];
            if (num.contains(":")) {
                num = num.replace(" ", "").split(":")[0];
            }
            object.put("num", num);
            object.put("eid", aid + "_" + num + "E");
            object.put("sid", s_id);
            array.put(object);
        }
    }

    public static void getDirDB(final Context context, @Nullable final BaseGetter.AsyncProgressDBInterface asyncInterface) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                DirectoryDB db = new DirectoryDB(context);
                int last_page_num = 0;
                try {
                    if (!db.isDBEmpty(false)) {
                        try {
                            Document init = Jsoup.connect("https://animeflv.net/browse?order=added").userAgent(BypassHolder.getUserAgent()).cookies(BypassHolder.getBasicCookieMap()).timeout(10000).get();
                            Element last = init.select("article").first();
                            String last_url = last.select("img[src]").first().attr("src");
                            String last_aid = last_url.substring(last_url.lastIndexOf("/") + 1, last_url.lastIndexOf("."));
                            if (db.animedExist(last_aid, false)) {
                                Log.e("Dir DEBUG", "Dir up to date | Animes: " + db.getCount(false));
                                if (asyncInterface != null)
                                    asyncInterface.onFinish(db.getAll(false));
                                db.close();
                                return null;
                            }
                        } catch (Exception e) {
                            Log.e("Dir DEBUG", "Dir Error Cancel", e);
                            if (asyncInterface != null)
                                asyncInterface.onFinish(db.getAll(false));
                            db.close();
                            return null;
                        }
                    }
                    Document init = Jsoup.connect("https://animeflv.net/browse?order=added&page=1").userAgent(BypassHolder.getUserAgent()).cookies(BypassHolder.getBasicCookieMap()).timeout(10000).get();
                    Elements pages = init.select("ul.pagination").first().select("a");
                    Element last_page = pages.get(pages.size() - 2);
                    last_page_num = Integer.parseInt(last_page.ownText().trim());
                    int progress = 0;
                    if (asyncInterface != null)
                        asyncInterface.onProgress(0);
                    for (int index = 1; index <= last_page_num; index++) {
                        Document page = Jsoup.connect("https://animeflv.net/browse?order=added&page=" + index).userAgent(BypassHolder.getUserAgent()).cookies(BypassHolder.getBasicCookieMap()).timeout(10000).get();
                        Elements animes = page.select("article");
                        for (Element element : animes) {
                            String img = element.select("img[src]").first().attr("src");
                            String a = img.substring(img.lastIndexOf("/") + 1, img.lastIndexOf("."));
                            if (db.animedExist(a, false)) {
                                if (asyncInterface != null)
                                    asyncInterface.onFinish(db.getAll(false));
                                db.close();
                                return null;
                            }
                            Element info = element.select("h3.Title").first();
                            String b = info.ownText();
                            String c = getType(element.select("span[class^=Type]").first().attr("class"));
                            String link = element.select("a").first().attr("href");
                            String[] semi = link.split("/");
                            String d = semi[3];
                            String e = semi[2];
                            String f = "";
                            String gens = "";
                            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("use_tags", false))
                                try {
                                    Document tags = Jsoup.connect("https://animeflv.net/" + c.trim().toLowerCase() + "/" + e + "/" + d).userAgent(BypassHolder.getUserAgent()).cookies(BypassHolder.getBasicCookieMap()).timeout(10000).get();
                                    StringBuilder builder = new StringBuilder();
                                    for (Element g : tags.select("nav.Nvgnrs").first().select("a")) {
                                        builder.append(g.ownText().trim())
                                                .append(", ");
                                    }
                                    gens = builder.toString();
                                    if (!gens.equals(""))
                                        f = gens.substring(0, gens.lastIndexOf(","));
                                    Log.e("Dir DEBUG", "Tags: " + f);
                                } catch (NullPointerException nog) {
                                    Log.e("Dir DEBUG", "No Tags");
                                }
                            if (b.trim().equals(""))
                                b = Jsoup.connect("https://animeflv.net/" + c.trim().toLowerCase() + "/" + e + "/" + d).userAgent(BypassHolder.getUserAgent()).cookies(BypassHolder.getBasicCookieMap()).timeout(10000).get().select("meta[property='og:title']").first().attr("content").replace(" Capítulos Online", "").replace(" Ver ", "").trim();
                            db.addAnime(new DirectoryDB.DirectoryItem(a, b, c, d, e, f));
                            progress++;
                            if (asyncInterface != null)
                                asyncInterface.onProgress(progress);
                        }
                    }
                    if (asyncInterface != null)
                        asyncInterface.onFinish(db.getAll(false));
                    db.close();
                    return null;
                } catch (Exception e) {
                    Log.e("Dir DEBUG", "Error loading dir", e);
                    Crashlytics.logException(e);
                    if (asyncInterface != null)
                        if (db.isDBEmpty(false)) {
                            asyncInterface.onError(e);
                        } else if (db.getAll(false).size() <= ((last_page_num - 1) * 24)) {
                            db.reset();
                            asyncInterface.onError(e);
                        } else {
                            asyncInterface.onFinish(db.getAll(false));
                        }
                    db.close();
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static void getDir(final Context context, @Nullable final BaseGetter.AsyncProgressInterface asyncInterface) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    JSONObject current;
                    JSONArray array = new JSONArray();
                    try {
                        current = new JSONObject(OfflineGetter.getDirectorio());
                        current.getBoolean("verified");
                        array = current.getJSONArray("lista");
                    } catch (Exception e) {
                        current = new JSONObject();
                        current.put("verified", true);
                        current.put("lista", array);
                    }
                    JSONObject last_obj = null;
                    if (array.length() > 0) {
                        try {
                            Document init = Jsoup.connect("http://animeflv.net/browse?order=added").userAgent(BypassHolder.getUserAgent()).cookies(BypassHolder.getBasicCookieMap()).timeout(10000).get();
                            Element last = init.select("article").first();
                            last_obj = array.getJSONObject(0);
                            String last_url = last.select("img[src]").first().attr("src");
                            String last_aid = last_url.substring(last_url.lastIndexOf("/") + 1, last_url.lastIndexOf("."));
                            if (last_aid.equals(last_obj.getString("a"))) {
                                Log.e("Dir DEBUG", "Dir up to date | Animes: " + array.length());
                                if (asyncInterface != null)
                                    asyncInterface.onFinish(current.toString());
                                return null;
                            }
                        } catch (Exception e) {
                            Log.e("Dir DEBUG", "Dir Error Cancel", e);
                            if (asyncInterface != null)
                                asyncInterface.onFinish(current.toString());
                            return null;
                        }
                    }
                    Document init = Jsoup.connect("http://animeflv.net/browse?order=added&page=1").userAgent(BypassHolder.getUserAgent()).cookies(BypassHolder.getBasicCookieMap()).timeout(10000).get();
                    Elements pages = init.select("ul.pagination").first().select("a");
                    Element last_page = pages.get(pages.size() - 2);
                    JSONArray new_array = new JSONArray();
                    int last = Integer.parseInt(last_page.ownText().trim());
                    int progress = 0;
                    if (asyncInterface != null)
                        asyncInterface.onProgress(0);
                    for (int index = 1; index <= last; index++) {
                        Document page = Jsoup.connect("http://animeflv.net/browse?order=added&page=" + index).userAgent(BypassHolder.getUserAgent()).cookies(BypassHolder.getBasicCookieMap()).timeout(10000).get();
                        Elements animes = page.select("article");
                        for (Element element : animes) {
                            String img = element.select("img[src]").first().attr("src");
                            String a = img.substring(img.lastIndexOf("/") + 1, img.lastIndexOf("."));
                            if (last_obj != null && a.equals(last_obj.getString("a"))) {
                                mergeLists(current, array, new_array, asyncInterface);
                                return null;
                            }
                            Element info = element.select("h3.Title").first();
                            String b = info.ownText();
                            String c = getType(element.select("span").first().attr("class"));
                            String link = element.select("a").first().attr("href");
                            String[] semi = link.split("/");
                            String d = semi[3];
                            String e = semi[2];
                            String f = "";
                            String gens = "";
                            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("use_tags", false))
                                try {
                                    Document tags = Jsoup.connect("http://animeflv.net/" + c.trim().toLowerCase() + "/" + e + "/" + d).userAgent(BypassHolder.getUserAgent()).cookies(BypassHolder.getBasicCookieMap()).timeout(10000).get();
                                    for (Element g : tags.select("nav.Nvgnrs").first().select("a")) {
                                        gens += g.ownText().trim();
                                        gens += ", ";
                                    }
                                    if (!gens.equals(""))
                                        f = gens.substring(0, gens.lastIndexOf(","));
                                    Log.e("Dir DEBUG", "Tags: " + f);
                                } catch (NullPointerException nog) {
                                    Log.e("Dir DEBUG", "No Tags");
                                }
                            if (b.trim().equals(""))
                                b = Jsoup.connect("http://animeflv.net/" + c.trim().toLowerCase() + "/" + e + "/" + d).userAgent(BypassHolder.getUserAgent()).cookies(BypassHolder.getBasicCookieMap()).timeout(10000).get().select("meta[property='og:title']").first().attr("content").replace(" Capítulos Online", "").replace(" Ver ", "").trim();
                            JSONObject object = new JSONObject();
                            object.put("a", a);
                            object.put("b", b);
                            object.put("c", c);
                            object.put("d", d);
                            object.put("e", e);
                            object.put("f", f);
                            new_array.put(object);
                            progress++;
                            if (asyncInterface != null)
                                asyncInterface.onProgress(progress);
                        }
                    }
                    mergeLists(current, array, new_array, asyncInterface);
                    return null;
                } catch (Exception e) {
                    Log.e("Dir DEBUG", "Error loading dir", e);
                    CrashlyticsCore.getInstance().logException(e);
                    String dir = OfflineGetter.getDirectorio();
                    if (asyncInterface != null)
                        if (dir.equals("null")) {
                            asyncInterface.onError(e);
                        } else {
                            asyncInterface.onFinish(dir);
                        }
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    private static void mergeLists(JSONObject object, JSONArray old_list, JSONArray new_list, @Nullable BaseGetter.AsyncProgressInterface asyncInterface) throws JSONException {
        if (asyncInterface != null)
            asyncInterface.onProgress(-1);
        Log.e("Dir DEBUG", "In Dir: " + old_list.length() + " Added: " + new_list.length());
        if (new_list.length() > 0) {
            for (int i = 0; i < old_list.length(); i++) {
                new_list.put(old_list.getJSONObject(i));
            }
            object.put("lista", new_list);
        } else {
            object.put("lista", old_list);
        }
        OfflineGetter.backupJsonSync(object, OfflineGetter.directorio);
        if (asyncInterface != null)
            asyncInterface.onFinish(object.toString());
    }

    private static String getState(String className) {
        switch (className) {
            case "AnmStts":
                return "0000-00-00";
            case "AnmStts A":
                return "1";
            default:
                return "prox";
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

    private static String getType(String classname) {
        switch (classname) {
            case "Type tv":
                return "Anime";
            case "Type ova":
                return "OVA";
            default:
                return "Pelicula";
        }
    }

    private static String getCurrentHour() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mmaa", Locale.ENGLISH);
        return simpleDateFormat.format(new Date());
    }
}