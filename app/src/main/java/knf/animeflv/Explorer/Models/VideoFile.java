package knf.animeflv.Explorer.Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jordy on 01/06/2016.
 */

public class VideoFile {
    private File src;
    private File tmp = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/thumbs");

    public VideoFile(File src) {
        this.src = src;
    }

    public String getPath() {
        return src.getAbsolutePath();
    }

    public File getFile() {
        return src;
    }

    public String getEID() {
        return src.getName().replace(".mp4", "E");
    }

    public String getID() {
        return getEID().replace("E", "").split("_")[0];
    }

    public File getThumbImage() {
        File file = new File(tmp, src.getName().replace(".mp4", ".png"));
        if (file.exists()) {
            return file;
        } else {
            return saveBitmap(ThumbnailUtils.createVideoThumbnail(src.getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND), src);
        }
    }

    public String getTitle() {
        return "Capitulo " + getCapNumber();
    }

    public String getDuration(Context context) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, Uri.fromFile(src));
        long duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        String dur = String.format(Locale.getDefault(), "%d:%d:%d",
                TimeUnit.MILLISECONDS.toHours(duration),
                TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );
        String[] divided = dur.split(":");
        String hours = divided[0];
        String minutes = divided[1];
        String seconds = divided[2];
        String f = "";
        if (!hours.equals("0")) {
            f += hours + ":";
        }
        if (minutes.length() < 2) {
            f += "0" + minutes + ":";
        } else {
            f += minutes + ":";
        }
        if (seconds.length() < 2) {
            f += "0" + seconds;
        } else {
            f += seconds;
        }
        return f;
    }

    public String getCapNumber() {
        return getEID().replace("E", "").split("_")[1];
    }

    private File saveBitmap(Bitmap bitmap, File file) {
        if (!tmp.exists()) {
            tmp.mkdirs();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(tmp, file.getName().replace(".mp4", ".png")));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new File(tmp, file.getName().replace(".mp4", ".png"));
    }
}
