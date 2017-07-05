package knf.animeflv.Explorer.Models;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import knf.animeflv.Directorio.AnimeClass;
import knf.animeflv.Directorio.DB.DirectoryHelper;
import knf.animeflv.Explorer.DirectoryComparator;
import knf.animeflv.Explorer.VideoComparator;
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
                    List<Directory> files = new ArrayList<>();
                    List<String> aids = new ArrayList<>();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        try {
                            for (DocumentFile file : getDirectoryFileAccess(context).listFiles()) {
                                if (file.isDirectory() && file.listFiles().length > 0) {
                                    aids.add(file.getName());
                                    files.add(new Directory(file, PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sd_down", false)));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (!getDirectoryFile(context).exists()) {
                            getDirectoryFile(context).mkdirs();
                        }
                        for (File file : getDirectoryFile(context).listFiles(dirFilter)) {
                            if (file.isDirectory() && file.listFiles().length > 0) {
                                aids.add(file.getName());
                                files.add(new Directory(file));
                            }
                        }
                    }
                    Log.e("Files found", "Files: " + aids.size() + " list: " + files.size());
                    replaceNames(context, aids, files, listener);
                } catch (Exception e) {
                    e.printStackTrace();
                    List<Directory> files = new ArrayList<>();
                    listener.onCreated(files);
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    private static void replaceNames(Context context, List<String> names, List<Directory> directories, AsyncDirectoryListener listener) {
        List<Directory> n_dirs = new ArrayList<>();
        List<AnimeClass> list = DirectoryHelper.get(context).getAll();
        try {
            for (AnimeClass anime : list) {
                if (names.contains(anime.getAid())) {
                    Directory dir = getSelectedDirectory(anime.getAid(), directories);
                    if (dir != null) {
                        dir.title = anime.getNombre();
                        n_dirs.add(dir);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.sort(n_dirs, new DirectoryComparator());
        listener.onCreated(n_dirs);
    }

    private static Directory getSelectedDirectory(String id, List<Directory> directories) {
        for (Directory directory : directories) {
            if (directory.getID().equals(id))
                return directory;
        }
        return null;
    }

    public static void createVideosListAsync(final Context context, final File file, final AsyncFileListener listener) {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                String type = DirectoryHelper.get(context).getType(file.getName());
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

    public static File getRootSDFile(Context context) {
        return new File(FileUtil.init(context).getSDPath());
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
