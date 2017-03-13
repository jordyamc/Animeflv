package knf.animeflv.Explorer.Models;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.provider.DocumentFile;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import knf.animeflv.Explorer.DirectoryComparator;
import knf.animeflv.Explorer.VideoComparator;
import knf.animeflv.Parser;
import knf.animeflv.Utils.ExecutorManager;
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

    public static void createDirectoryListAsync(final Context context, @NonNull final AsyncDirectoryListener listener) {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        List<Directory> files = new ArrayList<>();
                        try {
                            for (DocumentFile file : getDirectoryFileAccess(context).listFiles()) {
                                if (file.isDirectory() && file.listFiles().length > 0) {
                                    files.add(new Directory(file, PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sd_down", false)));
                                }
                            }
                            Collections.sort(files, new DirectoryComparator());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        listener.onCreated(files);
                    } else {
                        List<Directory> files = new ArrayList<>();
                        if (!getDirectoryFile(context).exists()) {
                            getDirectoryFile(context).mkdirs();
                        }
                        for (File file : getDirectoryFile(context).listFiles(dirFilter)) {
                            files.add(new Directory(file));
                        }
                        Collections.sort(files, new DirectoryComparator());
                        listener.onCreated(files);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    List<Directory> files = new ArrayList<>();
                    listener.onCreated(files);
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static void createVideosListAsync(final File file, final AsyncFileListener listener) {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                String type = Parser.getTypeCached(file.getName());
                List<VideoFile> files = new ArrayList<>();
                try {
                    for (File video : file.listFiles()) {
                        if (!video.isDirectory()) {
                            files.add(new VideoFile(video, type));
                        }
                    }
                    Collections.sort(files, new VideoComparator());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                listener.onCreated(files);
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static File getDirectoryFile(Context context) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sd_down", false)) {
            return new File(FileUtil.init(context).getSDPath() + "/Animeflv/download");
        } else {
            return new File(Environment.getExternalStorageDirectory() + "/Animeflv/download");
        }
    }

    public static DocumentFile getDirectoryFileAccess(Context context) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sd_down", false)) {
            return FileUtil.init(context).getDownloadDirFromAccess();
        } else {
            return DocumentFile.fromFile(new File(Environment.getExternalStorageDirectory() + "/Animeflv/download"));
        }
    }

    public interface AsyncDirectoryListener {
        void onCreated(List<Directory> list);
    }

    public interface AsyncFileListener {
        void onCreated(List<VideoFile> list);
    }
}
