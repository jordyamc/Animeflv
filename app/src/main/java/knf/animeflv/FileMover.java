package knf.animeflv;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.FileUtil;

public class FileMover {
    public static void PrepareMove(final Activity activity, final OnProgressListener listener) {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                Looper.prepare();
                List<File> files = getFileList();
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

    private static List<File> getFileList() {
        File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download");
        List<File> list = new ArrayList<>();
        for (File directory : file.listFiles()) {
            if (directory.isDirectory()) {
                for (File f : directory.listFiles()) {
                    if (f.isFile() && f.getName().endsWith(".mp4")) {
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
                moveNormal(file, listener);
            }
            prog++;
            Log.d("Moved", prog + " of " + files.size());
            listener.onStep(prog, files.size());
        }
    }

    /*private static void moveNormal(File file, OnProgressListener listener) {
        String name = file.getName();
        try {
            FileChannel inChannel = new FileInputStream(file).getChannel();
            FileChannel outChannel = new FileOutputStream(new File(FileUtil.getSDPath() + "/Animeflv/download/" + name.replace(".mp4", "").split("_")[0] + "/" + name)).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        } catch (Exception e) {
            Log.e("Move Error", file.getName() + " Error:" + e.getMessage());
            listener.onError(name);
        }
    }*/

    private static void moveNormal(File file, OnProgressListener listener) {
        String[] name = file.getName().replace(".mp4", "").split("_");
        String title = Parser.getTitleCached(name[0]) + " " + name[1];
        DocumentFile fileout = FileUtil.getFileFromAccess(file.getName().replace(".mp4", "E"));
        try {
            FileInputStream in = new FileInputStream(file);
            FileOutputStream out = new FileOutputStream(new File(FileUtil.getSDPath() + "/Animeflv/download/" + name[0] + "/" + file.getName()));
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
                file.delete();
        } catch (Exception e) {
            fileout.delete();
            Log.e("Move Error", file.getName() + " Error:" + e.getMessage());
            e.printStackTrace();
            listener.onError(file.getName());
        }
    }

    private static void moveAccess(Activity activity, File file, OnProgressListener listener) {
        String[] name = file.getName().replace(".mp4", "").split("_");
        String title = Parser.getTitleCached(name[0]) + " " + name[1];
        DocumentFile fileout = FileUtil.getFileFromAccess(file.getName().replace(".mp4", "E"));
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
                    file.delete();
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

    /*private static void moveAccess(Activity activity,File file, OnProgressListener listener) {
        try {
            FileChannel inChannel = new FileInputStream(file).getChannel();
            FileChannel outChannel = new FileOutputStream(FileUtil.getFileDescriptorFromAccess(activity,file).getFileDescriptor()).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
            if (file.exists()&&FileUtil.getFileFromAccess(file.getName().replace(".mp4","E")).exists())file.delete();
        } catch (Exception e){
            Log.e("Move Error",file.getName()+" Error:"+e.getMessage());
            listener.onError(file.getName());
        }
    }*/

    public interface OnProgressListener {
        void onStep(int progress, int total);

        void onSemiStep(String name, int progress);

        void onError(String name);
    }
}
