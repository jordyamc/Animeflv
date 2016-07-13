package knf.animeflv.StreamManager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.io.File;
import java.util.List;

import knf.animeflv.Parser;
import xdroid.toaster.Toaster;

public class ExternalStream {
    Context context;

    public ExternalStream(Context context) {
        this.context = context;
    }

    public void Stream(String eid, String url) {
        try {
            String aid = eid.replace("E", "").substring(0, eid.lastIndexOf("_"));
            String numero = eid.replace("E", "").substring(eid.lastIndexOf("_") + 1);
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).setDataAndType(Uri.parse(url),"video/mp4").putExtra("title", new Parser().getTitCached(aid) + " " + numero));
            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("visto" + eid.replace("E", ""), true).apply();
            String vistosad = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", "");
            if (!vistosad.contains(eid)) {
                vistosad = vistosad + eid + ":::";
                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("vistos", vistosad).apply();
            }
        }catch (Exception e){
            e.printStackTrace();
            Toaster.toast("No hay Aplicaciones dispnibles para reproducir el video!!!");
        }
    }

    public void Play(String eid, File file) {
        if (isMXinstalled()) {
            new MXStream(context).Play(eid, file);
        } else {
            String aid = eid.replace("E", "").substring(0, eid.lastIndexOf("_"));
            String numero = eid.replace("E", "").substring(eid.lastIndexOf("_") + 1);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file));
            intent.setDataAndType(Uri.fromFile(file), "video/mp4");
            intent.putExtra("title", new Parser().getTitCached(aid) + " " + numero);
            context.startActivity(intent);
            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("visto" + aid + "_" + numero, true).apply();
            String vistosad = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", "");
            if (!vistosad.contains(eid)) {
                vistosad = vistosad + eid + ":::";
                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("vistos", vistosad).apply();
            }
        }
    }

    private boolean isMXinstalled() {
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = context.getPackageManager();
        packages = pm.getInstalledApplications(0);
        String pack = "null";
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals("com.mxtech.videoplayer.pro")) {
                pack = "com.mxtech.videoplayer.pro";
                break;
            }
            if (packageInfo.packageName.equals("com.mxtech.videoplayer.ad")) {
                pack = "com.mxtech.videoplayer.ad";
                break;
            }
        }
        return !pack.equals("null");
    }
}
