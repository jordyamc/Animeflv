package knf.animeflv.Changelog;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import knf.animeflv.Utils.ExecutorManager;

/**
 * Created by Jordy on 24/10/2016.
 */

public class ChangeLogOrganizer {
    public static void organize(final Activity activity, final ChangelogListListener listListener) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    StringBuilder buf = new StringBuilder();
                    InputStream json = activity.getAssets().open("log.html");
                    BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
                    String str;
                    while ((str = in.readLine()) != null)
                        buf.append(str);
                    in.close();
                    Document document = Jsoup.parse(buf.toString());
                    List<String> versionsname = new ArrayList<String>();
                    for (Element element : document.getElementsByTag("h2")) {
                        versionsname.add(element.text());
                    }
                    Elements logs = document.select("ul[type='circle']");
                    List<ChangeLogObjects.Version> versions = new ArrayList<ChangeLogObjects.Version>();
                    for (int i = 0; i < logs.size(); i++) {
                        Elements el = logs.get(i).select("ul[type='circle'] > li");
                        List<ChangeLogObjects.Log> ls = new ArrayList<ChangeLogObjects.Log>();
                        for (Element lie : el) {
                            ls.add(new ChangeLogObjects.Log(getType(lie), lie.ownText(), getSublist(lie)));
                        }
                        versions.add(new ChangeLogObjects.Version(versionsname.get(i), ls));
                    }
                    listListener.onListCreated(versions);
                } catch (Exception e) {
                    Log.e("LogOrganizer", "Error", e);
                    listListener.onListFailed();
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    @Nullable
    private static List<ChangeLogObjects.Log> getSublist(Element element) {
        Elements elements = element.select("li > ul");
        if (elements != null && elements.size() > 0) {
            List<ChangeLogObjects.Log> logs = new ArrayList<>();
            for (Element el : elements.first().select("ul[type='disc'] > li")) {
                logs.add(new ChangeLogObjects.Log(getType(el), el.ownText(), null));
            }
            return logs;
        } else {
            return null;
        }
    }

    private static ChangeLogObjects.LogType getType(Element element) {
        if (element.getElementsByClass("cambio").size() > 0) {
            return ChangeLogObjects.LogType.CAMBIO;
        } else if (element.getElementsByClass("new").size() > 0) {
            return ChangeLogObjects.LogType.NUEVO;
        } else if (element.getElementsByClass("importante").size() > 0) {
            return ChangeLogObjects.LogType.IMPORTANTE;
        } else if (element.getElementsByClass("corregido").size() > 0) {
            return ChangeLogObjects.LogType.CORREGIDO;
        } else if (element.getElementsByClass("newImp").size() > 0) {
            return ChangeLogObjects.LogType.NUEVO_IMPORTANTE;
        } else {
            return ChangeLogObjects.LogType.NORMAL;
        }
    }

    public interface ChangelogListListener {
        void onListCreated(List<ChangeLogObjects.Version> versions);

        void onListFailed();
    }
}
