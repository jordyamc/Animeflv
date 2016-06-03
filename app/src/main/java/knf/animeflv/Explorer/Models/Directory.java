package knf.animeflv.Explorer.Models;

import android.content.Context;

import java.io.File;

import knf.animeflv.Parser;
import knf.animeflv.TaskType;


public class Directory {
    private File src;
    private String title;

    public Directory(File src) {
        this.src = src;
        this.title = new Parser().getTitCached(getID());
    }

    public String getPath() {
        return src.getAbsolutePath();
    }

    public File getFile() {
        return src;
    }

    public String getID() {
        return src.getName();
    }

    public String getImageUrl(Context context) {
        return new Parser().getBaseUrl(TaskType.NORMAL, context) + "imagen.php?certificate=" + new Parser().getCertificateSHA1Fingerprint(context) + "&thumb=http://cdn.animeflv.net/img/portada/thumb_80/" + getID() + ".jpg";
    }

    public String getTitle() {
        return title;
    }

    public String getFilesNumber() {
        return String.valueOf(src.list().length) + " archivos";
    }
}
