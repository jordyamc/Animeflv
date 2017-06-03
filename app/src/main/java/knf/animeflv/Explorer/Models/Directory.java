package knf.animeflv.Explorer.Models;

import android.content.Context;
import android.os.Environment;
import android.support.v4.provider.DocumentFile;

import java.io.File;

import knf.animeflv.Parser;
import knf.animeflv.TaskType;
import knf.animeflv.Utils.FileUtil;


public class Directory {
    public String title;
    private File src;
    private DocumentFile srcAccess;
    private boolean inSD;
    private String filenumber;
    private String id;

    public Directory(File src) {
        this.src = src;
        if (src != null) {
            this.filenumber = String.valueOf(src.list().length) + " archivos";
            this.id = src.getName();
        } else {
            this.filenumber = String.valueOf(srcAccess.listFiles().length) + " archivos";
            this.id = srcAccess.getName();
        }
        //this.title = new Parser().getTitCached(getID());
    }

    public Directory(DocumentFile srce, boolean inSD) {
        this.srcAccess = srce;
        this.inSD = inSD;
        if (src != null) {
            this.filenumber = String.valueOf(src.list().length) + " archivos";
            this.id = src.getName();
        } else {
            this.filenumber = String.valueOf(srcAccess.listFiles().length) + " archivos";
            this.id = srcAccess.getName();
        }
        //this.title = new Parser().getTitCached(getID());
    }

    public String getPath() {
        return src.getAbsolutePath();
    }

    public File getFile(Context context) {
        if (src != null) {
            return src;
        } else {
            if (inSD) {
                return new File(FileUtil.init(context).getSDPath() + "/Animeflv/download/" + srcAccess.getName());
            } else {
                return new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + srcAccess.getName());
            }
        }
    }

    public String getID() {
        return id;
    }

    public String getImageUrl(Context context) {
        return new Parser().getBaseUrl(TaskType.NORMAL, context) + "imagen.php?certificate=" + new Parser().getCertificateSHA1Fingerprint(context) + "&thumb=http://cdn.animeflv.net/img/portada/thumb_80/" + getID() + ".jpg";
    }

    public String getTitle() {
        return title;
    }


    public String getFilesNumber() {
        return filenumber;
    }
}
