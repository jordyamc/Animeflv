package knf.animeflv.Explorer.Models;

import android.content.Context;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import knf.animeflv.Utils.FileUtil;

public class ModelFactory {
    public static List<Directory> createDirectoryList(Context context) {
        List<Directory> files = new ArrayList<>();
        if (!getDirectoryFile(context).exists()) {
            getDirectoryFile(context).mkdirs();
        }
        for (File file : getDirectoryFile(context).listFiles()) {
            if (file.isDirectory()) {
                if (file.listFiles().length > 0) {
                    files.add(new Directory(file));
                }
            }
        }
        return files;
    }

    public static List<VideoFile> createVideosList(File file) {
        List<VideoFile> files = new ArrayList<>();
        for (File video : file.listFiles()) {
            if (!video.isDirectory()) {
                files.add(new VideoFile(video));
            }
        }
        return files;
    }

    public static File getDirectoryFile(Context context) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sd_down", false)) {
            return new File(FileUtil.getSDPath() + "/Animeflv/download");
        } else {
            return new File(Environment.getExternalStorageDirectory() + "/Animeflv/download");
        }
    }
}
