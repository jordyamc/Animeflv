package knf.animeflv.StreamManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.io.File;

import knf.animeflv.DownloadManager.CookieConstructor;
import knf.animeflv.Parser;
import knf.animeflv.Utils.FileUtil;

/**
 * Created by Jordy on 04/03/2016.
 */
public class InternalStream {
    Context context;
    Parser parser = new Parser();

    public InternalStream(Context context) {
        this.context = context;
    }

    public void Stream(String eid, String url) {
        String aid = eid.replace("E", "").substring(0, eid.lastIndexOf("_"));
        String numero = eid.replace("E", "").substring(eid.lastIndexOf("_") + 1);
        Intent interno = parser.getPrefIntPlayer(context);
        interno.putExtra("url", url);
        interno.putExtra("title", parser.getTitCached(aid) + " " + numero);
        interno.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(interno);
        FileUtil.setSeenState(eid, true);
    }

    public void Stream(String eid, String url, CookieConstructor constructor) {
        String aid = eid.replace("E", "").substring(0, eid.lastIndexOf("_"));
        String numero = eid.replace("E", "").substring(eid.lastIndexOf("_") + 1);
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("title", parser.getTitCached(aid) + " " + numero);
        bundle.putString("ops", "cookie:::" + constructor.getCookie() + ";;;" + "User-Agent:::" + constructor.getUseAgent() + ";;;" + "Accept:::text/html, application/xhtml+xml, */*;;;" + "Accept-Language:::en-US,en;q=0.7,he;q=0.3;;;" + "Referer:::" + constructor.getReferer());
        Intent intent = parser.getPrefIntPlayer(context);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        FileUtil.setSeenState(eid, true);
    }

    public void Play(String eid, File file) {
        String aid = eid.replace("E", "").substring(0, eid.lastIndexOf("_"));
        String numero = eid.replace("E", "").substring(eid.lastIndexOf("_") + 1);
        Intent interno = parser.getPrefIntPlayer(context);
        interno.putExtra("file", file.getAbsolutePath());
        interno.putExtra("title", parser.getTitCached(aid) + " " + numero);
        interno.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(interno);
        FileUtil.setSeenState(eid, true);
    }
}
