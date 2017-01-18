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
    private final File tmp = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/thumbs");
    private File src;
    private String eid;
    private String id;
    private File thumb;
    private String f_num;

    public VideoFile(File src) {
        this.src = src;
        this.eid = src.getName().replace(".mp4", "E");
        this.id = getEID().replace("E", "").split("_")[0];
        File file = new File(tmp, src.getName().replace(".mp4", ".png"));
        if (file.exists()) {
            this.thumb = file;
        } else {
            this.thumb = saveBitmap(ThumbnailUtils.createVideoThumbnail(src.getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND), src);
        }
        this.f_num = getEID().replace("E", "").split("_")[1];
    }

    public String getPath() {
        return src.getAbsolutePath();
    }

    public File getFile() {
        return src;
    }

    public String getEID() {
        return eid;
    }

    public String getID() {
        return id;
    }

    public File getThumbImage() {
        return thumb;
    }

    public File recreateThumbImage() {
        File file = new File(tmp, src.getName().replace(".mp4", ".png"));
        if (file.exists()) {
            file.delete();
        }
        return saveBitmap(ThumbnailUtils.createVideoThumbnail(src.getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND), src);
    }

    public String getTitle() {
        return "Capitulo " + getCapNumber();
    }

    public String getDuration(Context context) {
        try {
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
        } catch (Exception e) {
            return "";
        }
    }

    public String getCapNumber() {
        return f_num;
    }

    private File saveBitmap(Bitmap bitmap, File file) {
        File thumbs = new File(tmp, ".nomedia");
        if (!tmp.exists()) {
            tmp.mkdirs();
        }
        if (!thumbs.exists())
            try {
                thumbs.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
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
