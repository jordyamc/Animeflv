package knf.animeflv.Explorer.Models;

import android.content.Context;
import android.os.Environment;
import android.support.v4.provider.DocumentFile;

import java.io.File;

import knf.animeflv.Parser;
import knf.animeflv.TaskType;
import knf.animeflv.Utils.FileUtil;


public class Directory {
    private File src;
    private String title;
    private DocumentFile srcAccess;
    private boolean inSD;

    public Directory(File src) {
        this.src = src;
        this.title = new Parser().getTitCached(getID());
    }

    public Directory(DocumentFile src, boolean inSD) {
        this.srcAccess = src;
        this.title = new Parser().getTitCached(getID());
        this.inSD = inSD;
    }

    public String getPath() {
        return src.getAbsolutePath();
    }

    public File getFile() {
        if (src != null) {
            return src;
        } else {
            if (inSD) {
                return new File(FileUtil.getSDPath() + "/Animeflv/download/" + srcAccess.getName());
            } else {
                return new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + srcAccess.getName());
            }
        }
    }

    public String getID() {
        if (src != null) {
            return src.getName();
        } else {
            return srcAccess.getName();
        }
    }

    public String getImageUrl(Context context) {
        return new Parser().getBaseUrl(TaskType.NORMAL, context) + "imagen.php?certificate=" + new Parser().getCertificateSHA1Fingerprint(context) + "&thumb=http://cdn.animeflv.net/img/portada/thumb_80/" + getID() + ".jpg";
    }

    public String getTitle() {
        return title;
    }

    public String getFilesNumber() {
        if (src != null) {
            return String.valueOf(src.list().length) + " archivos";
        } else {
            return String.valueOf(srcAccess.listFiles().length) + " archivos";
        }
    }
}
