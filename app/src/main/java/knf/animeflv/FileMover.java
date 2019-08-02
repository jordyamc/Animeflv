package knf.animeflv;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import androidx.documentfile.provider.DocumentFile;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import knf.animeflv.Directorio.DB.DirectoryHelper;
import knf.animeflv.DownloadManager.ManageDownload;
import knf.animeflv.StreamManager.StreamManager;
import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.FileUtil;
import xdroid.toaster.Toaster;

public class FileMover {
    public static void PrepareToPlay(final Activity activity, final String eid) {
        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .progress(true, 0)
                .content("Preparando archivo...")
                .cancelable(false)
                .build();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.show();
                    }
                });
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                checktmp();
                File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/tmp", eid.replace("E", "") + ".mp4");
                if (!file.exists()) {
                    DocumentFile documentFile = FileUtil.init(activity).getFileFromAccess(eid);
                    try {
                        file.createNewFile();
                        InputStream in = FileUtil.getInputStreamFromAccess(activity, documentFile);
                        OutputStream out = new FileOutputStream(file);
                        int r;
                        byte[] b = new byte[1024 * 4];
                        while ((r = in.read(b, 0, b.length)) != -1) {
                            out.write(b, 0, r);
                            out.flush();
                        }
                        out.close();
                        in.close();
                        StreamManager.Play(activity, eid, file);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toaster.toast("Error preparando archivo");
                    }
                } else {
                    StreamManager.Play(activity, eid, file);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                });
                super.onPostExecute(aVoid);
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    private static void checktmp() {
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/tmp");
            if (!file.exists())
                file.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cleantmp() {
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/tmp");
            if (!file.exists()) {
                file.mkdirs();
            } else {
                for (File f : file.listFiles()) {
                    if (f.isFile())
                        f.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void PrepareMove(final Activity activity, final OnProgressListener listener) {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                Looper.prepare();
                List<File> files = getFileList(activity);
                if (files.size() == 0) {
                    listener.onStep(0, 0);
                } else {
                    listener.onStep(0, files.size());
                    StartMove(activity, files, listener);
                }
                Looper.loop();
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    private static List<File> getFileList(Activity activity) {
        File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download");
        List<File> list = new ArrayList<>();
        for (File directory : file.listFiles()) {
            if (directory.isDirectory()) {
                for (File f : directory.listFiles()) {
                    if (f.isFile() && f.getName().endsWith(".mp4") && !ManageDownload.isDownloading(activity, f.getName().replace(".mp4", "E"))) {
                        list.add(f);
                    }
                }
            }
        }
        return list;
    }

    private static void StartMove(Activity activity, List<File> files, OnProgressListener listener) {
        int prog = 0;
        for (File file : files) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                moveAccess(activity, file, listener);
            } else {
                moveNormal(activity, file, listener);
            }
            prog++;
            Log.d("Moved", prog + " of " + files.size());
            listener.onStep(prog, files.size());
        }
    }

    private static void moveNormal(Context context, File file, OnProgressListener listener) {
        String[] name = file.getName().replace(".mp4", "").split("_");
        String title = DirectoryHelper.get(context).getTitle(name[0]) + " " + name[1];
        DocumentFile fileout = FileUtil.init(context).getFileFromAccess(file.getName().replace(".mp4", "E"));
        try {
            FileInputStream in = new FileInputStream(file);
            FileOutputStream out = new FileOutputStream(new File(FileUtil.init(context).getSDPath() + "/Animeflv/download/" + name[0] + "/" + file.getName()));
            long totalBytes = in.available();
            // Start progress meter bar
            int r;
            long bytesRead = 0;

            // You may increase buffer size to improve IO speed
            byte[] b = new byte[1024 * 4];
            while ((r = in.read(b, 0, b.length)) != -1) {
                out.write(b, 0, r);
                out.flush();
                bytesRead += r;
                listener.onSemiStep(title, (int) (bytesRead * 100 / totalBytes));
            }
            out.close();
            in.close();
            if (file.exists() && fileout.exists())
                if (file.delete()) {
                    try {
                        file.getParentFile().delete();
                    } catch (Exception e) {
                    }
                }
        } catch (Exception e) {
            if (fileout != null)
                fileout.delete();
            Log.e("Move Error", file.getName() + " Error:" + e.getMessage());
            e.printStackTrace();
            listener.onError(file.getName());
        }
    }

    private static void moveAccess(Activity activity, File file, OnProgressListener listener) {
        String[] name = file.getName().replace(".mp4", "").split("_");
        String title = DirectoryHelper.get(activity).getTitle(name[0]) + " " + name[1];
        DocumentFile fileout = FileUtil.init(activity).getFileFromAccess(file.getName().replace(".mp4", "E"));
        try {
            InputStream in = new FileInputStream(file);
            OutputStream out = FileUtil.getOutputStreamFromAccess(activity, file);
            long totalBytes = in.available();
            // Start progress meter bar
            int r;
            long bytesRead = 0;
            // You may increase buffer size to improve IO speed
            byte[] b = new byte[1024 * 4];
            while ((r = in.read(b, 0, b.length)) != -1) {
                out.write(b, 0, r);
                out.flush();
                bytesRead += r;
                listener.onSemiStep(title, (int) (bytesRead * 100 / totalBytes));
            }
            out.close();
            in.close();
            try {
                if (file.exists() && fileout.exists())
                    if (file.delete()) {
                        try {
                            file.getParentFile().delete();
                        } catch (Exception e) {
                        }
                    }
            } catch (Exception e) {
                if (file.exists()) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            try {
                fileout.delete();
            } catch (Exception x) {
            }
            Log.e("Move Error", file.getName() + " Error:" + e.getMessage());
            e.printStackTrace();
            listener.onError(file.getName());
        }
    }

    public interface OnProgressListener {
        void onStep(int progress, int total);

        void onSemiStep(String name, int progress);

        void onError(String name);
    }
}
