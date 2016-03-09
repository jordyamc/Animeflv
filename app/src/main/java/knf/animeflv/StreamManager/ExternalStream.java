package knf.animeflv.StreamManager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

import knf.animeflv.Parser;

/**
 * Created by Jordy on 04/03/2016.
 */
public class ExternalStream {
    Context context;
    Parser parser = new Parser();

    public ExternalStream(Context context) {
        this.context = context;
    }

    public void Stream(String eid, String url) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("visto" + eid.replace("E", ""), true).apply();
        String vistosad = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", "");
        if (!vistosad.contains(eid)) {
            vistosad = vistosad + eid + ":::";
            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("vistos", vistosad).apply();
        }
    }

    public void Play(String eid, File file) {
        String aid = eid.replace("E", "").substring(0, eid.lastIndexOf("_"));
        String numero = eid.replace("E", "").substring(eid.lastIndexOf("_") + 1);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file));
        intent.setDataAndType(Uri.fromFile(file), "video/mp4");
        context.startActivity(intent);
        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("visto" + aid + "_" + numero, true).apply();
        String vistosad = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", "");
        if (!vistosad.contains(eid)) {
            vistosad = vistosad + eid + ":::";
            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("vistos", vistosad).apply();
        }
    }
}
