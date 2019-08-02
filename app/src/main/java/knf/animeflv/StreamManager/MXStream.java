package knf.animeflv.StreamManager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import knf.animeflv.Directorio.DB.DirectoryHelper;
import knf.animeflv.DownloadManager.CookieConstructor;
import knf.animeflv.Parser;
import knf.animeflv.Seen.SeenManager;
import knf.animeflv.history.adapter.HistoryHelper;

/**
 * Created by Jordy on 04/03/2016.
 */
public class MXStream {
    Context context;
    Parser parser = new Parser();

    public MXStream(Context context) {
        this.context = context;
    }

    public void Stream(String eid, String url) {
        String aid = eid.replace("E", "").substring(0, eid.lastIndexOf("_"));
        String numero = eid.replace("E", "").substring(eid.lastIndexOf("_") + 1);
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
        switch (pack) {
            case "com.mxtech.videoplayer.pro":
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri videoUri = Uri.parse(url);
                intent.setDataAndType(videoUri, "video/mp4");
                intent.setPackage("com.mxtech.videoplayer.pro");
                intent.putExtra("title", DirectoryHelper.get(context).getTitle(aid) + " " + numero);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                SeenManager.get(context).setSeenState(eid, true);
                break;
            case "com.mxtech.videoplayer.ad":
                Intent intentad = new Intent(Intent.ACTION_VIEW);
                Uri videoUriad = Uri.parse(url);
                intentad.setDataAndType(videoUriad, "video/mp4");
                intentad.setPackage("com.mxtech.videoplayer.ad");
                intentad.putExtra("title", DirectoryHelper.get(context).getTitle(aid) + " " + numero);
                intentad.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentad);
                SeenManager.get(context).setSeenState(eid, true);
                break;
            default:
                Toast.makeText(context, "MX player no instalado", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void Stream(String eid, String url, CookieConstructor constructor) {
        String aid = eid.replace("E", "").substring(0, eid.lastIndexOf("_"));
        String numero = eid.replace("E", "").substring(eid.lastIndexOf("_") + 1);
        HistoryHelper.addToList(context, aid, DirectoryHelper.get(context).getTitle(aid), numero);
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
        switch (pack) {
            case "com.mxtech.videoplayer.pro":
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri videoUri = Uri.parse(url);
                intent.setDataAndType(videoUri, "application/mp4");
                intent.setPackage("com.mxtech.videoplayer.pro");
                intent.putExtra("title", DirectoryHelper.get(context).getTitle(aid) + " " + numero);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                String[] headers = {"cookie", constructor.getCookie(), "User-Agent", constructor.getUseAgent(), "Accept", "text/html, application/xhtml+xml, *" + "/" + "*", "Accept-Language", "en-US,en;q=0.7,he;q=0.3", "Referer", constructor.getReferer()};
                intent.putExtra("headers", headers);
                context.startActivity(intent);
                SeenManager.get(context).setSeenState(eid, true);
                break;
            case "com.mxtech.videoplayer.ad":
                Intent intentad = new Intent(Intent.ACTION_VIEW);
                Uri videoUriad = Uri.parse(url);
                intentad.setDataAndType(videoUriad, "application/mp4");
                intentad.setPackage("com.mxtech.videoplayer.ad");
                intentad.putExtra("title", DirectoryHelper.get(context).getTitle(aid) + " " + numero);
                intentad.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                String[] headers1 = {"cookie", constructor.getCookie(), "User-Agent", constructor.getUseAgent(), "Accept", "text/html, application/xhtml+xml, *" + "/" + "*", "Accept-Language", "en-US,en;q=0.7,he;q=0.3", "Referer", constructor.getReferer()};
                intentad.putExtra("headers", headers1);
                context.startActivity(intentad);
                SeenManager.get(context).setSeenState(eid, true);
                break;
            default:
                Toast.makeText(context, "MX player no instalado", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void Play(String eid, File file) {
        String aid = eid.replace("E", "").substring(0, eid.lastIndexOf("_"));
        String numero = eid.replace("E", "").substring(eid.lastIndexOf("_") + 1);
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
        if (!pack.equals("null")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri videoUri = getUrifromFile(file);
            intent.setDataAndType(videoUri, "video/mp4");
            intent.setPackage(pack);
            intent.putExtra("title", DirectoryHelper.get(context).getTitle(aid) + " " + numero);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
            SeenManager.get(context).setSeenState(eid, true);
        } else {
            Toast.makeText(context, "MX player no instalado", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri getUrifromFile(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, context.getPackageName() + ".RequestsBackground", file);
        } else {
            return Uri.fromFile(file);
        }
    }
}
