package knf.animeflv.Explorer.Models;

import android.content.Context;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import knf.animeflv.Explorer.DirectoryComparator;
import knf.animeflv.Explorer.VideoComparator;
import knf.animeflv.Utils.FileUtil;

public class ModelFactory {
    private static FileFilter dirFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            if (pathname.isDirectory()) {
                if (pathname.list().length > 0) {
                    return true;
                }
            }
            return false;
        }
    };
    public static List<Directory> createDirectoryList(Context context) {
        List<Directory> files = new ArrayList<>();
        if (!getDirectoryFile(context).exists()) {
            getDirectoryFile(context).mkdirs();
        }
        for (File file : getDirectoryFile(context).listFiles(dirFilter)) {
            files.add(new Directory(file));
        }
        Collections.sort(files, new DirectoryComparator());
        return files;
    }

    public static List<VideoFile> createVideosList(File file) {
        List<VideoFile> files = new ArrayList<>();
        for (File video : file.listFiles()) {
            if (!video.isDirectory()) {
                files.add(new VideoFile(video));
            }
        }
        Collections.sort(files, new VideoComparator());
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
