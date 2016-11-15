package knf.animeflv;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.Logger;
import knf.animeflv.Utils.ThemeUtils;

public class MoveFiles extends AsyncTask<String,String,String> {
    MaterialDialog dialog;
    Context context;
    Context d_context;

    public MoveFiles(Context c, Context d_c) {
        this.context = c;
        this.d_context = d_c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new MaterialDialog.Builder(d_context)
                .title("Moviendo Animes...")
                .titleGravity(GravityEnum.CENTER)
                .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                .progress(false, count(), true)
                .cancelable(false)
                .build();
        dialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            File sourceLocation = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download");
            File targetLocation = new File(FileUtil.init(context).getSDPath() + "/Animeflv/download");
            if (sourceLocation.isDirectory()) {
                if (!targetLocation.exists() && !targetLocation.mkdirs()) {
                    throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
                }
                String[] children = sourceLocation.list();
                for (String i : children) {
                    File scan = new File(sourceLocation, i);
                    if (scan.isDirectory()) {
                        String[] archivos = scan.list();
                        for (String mp4 : archivos) {
                            File inSD = new File(targetLocation.getPath(), i);
                            if (!inSD.exists()) {
                                inSD.mkdir();
                            }
                            if (!new File(inSD.getPath(), mp4).exists()) {
                                //boolean success = new File(sourceLocation+"/"+i+"/"+mp4).renameTo(new File(getSD1()+"/Animeflv/download/"+i+"/"+mp4));
                                FileChannel inChannel = new FileInputStream(new File(sourceLocation + "/" + i + "/" + mp4)).getChannel();
                                FileChannel outChannel = new FileOutputStream(new File(FileUtil.init(context).getSDPath() + "/Animeflv/download/" + i + "/" + mp4)).getChannel();
                                try {
                                    inChannel.transferTo(0, inChannel.size(), outChannel);
                                } finally {
                                    if (inChannel != null)
                                        inChannel.close();
                                    if (outChannel != null)
                                        outChannel.close();
                                }
                                File file = new File(inSD.getPath(), mp4);
                                if (file.exists()) {
                                    if (!new File(scan.getPath(), mp4).exists()) {
                                        Log.d("Move ok", mp4);
                                        dialog.incrementProgress(1);
                                    } else {
                                        if (new File(scan.getPath(), mp4).delete()) {
                                            Log.d("Move ok", mp4);
                                            dialog.incrementProgress(1);
                                        } else {
                                            Log.d("Move error", mp4);
                                            throw new IOException("error");
                                        }
                                    }
                                } else {
                                    Log.d("Move error", mp4);
                                    throw new IOException("error");
                                }
                            } else {
                                if (new File(scan.getPath(), mp4).delete()) {
                                    Log.d("Move ok", mp4);
                                }
                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error " + e.getMessage();
        }
        return "";
    }

    private int count() {
        int count = 0;
        File f = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download");
        File[] files = f.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    count += file.list().length;
                }
            }
        }
        return count;
    }
    public String getSD1(){
        String sSDpath = null;
        File   fileCur = null;
        for (String sPathCur : Arrays.asList("MicroSD", "external_SD", "sdcard1", "ext_card", "external_sd", "ext_sd", "external", "extSdCard", "externalSdCard", "8E84-7E70")) {
            fileCur = new File( "/mnt/", sPathCur);
            if( fileCur.isDirectory() && fileCur.canWrite()) {
                sSDpath = fileCur.getAbsolutePath();
                break;
            }
            if( sSDpath == null)  {
                fileCur = new File( "/storage/", sPathCur);
                if( fileCur.isDirectory() && fileCur.canWrite())
                {
                    sSDpath = fileCur.getAbsolutePath();
                    break;
                }
            }
            if( sSDpath == null)  {
                fileCur = new File( "/storage/emulated", sPathCur);
                if( fileCur.isDirectory() && fileCur.canWrite())
                {
                    sSDpath = fileCur.getAbsolutePath();
                    Logger.Error(getClass(), new Throwable(sSDpath));
                    break;
                }
            }
        }
        return sSDpath;
    }

    public void MoveFiles() throws Exception {
        File sourceLocation=new File(Environment.getExternalStorageDirectory() + "/Animeflv/download");
        File targetLocation = new File(FileUtil.init(context).getSDPath() + "/Animeflv/download");
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists() && !targetLocation.mkdirs()) {
                throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
            }
            String[] children = sourceLocation.list();
            int prog = 0;
            dialog = new MaterialDialog.Builder(d_context)
                    .title("Moviendo Animes...")
                    .titleGravity(GravityEnum.CENTER)
                    .progress(false, children.length, true)
                    .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                    .cancelable(false)
                    .build();
            dialog.show();
            for (String i:children) {
                prog++;
                dialog.setProgress(prog);
                String tit = new Parser().getTitCached(i);
                File scan=new File(sourceLocation,i);
                if (scan.isDirectory()) {
                    String[] archivos = scan.list();
                    for (String mp4:archivos){
                        dialog.setContent(tit + " " + mp4.replace(i + "_", "").replace(".mp4", ""));
                        File inSD=new File(targetLocation.getPath(),i);
                        if (!inSD.exists()){
                            inSD.mkdir();
                        }
                        if (!new File(inSD.getPath(), mp4).exists()) {
                            //boolean success = new File(sourceLocation+"/"+i+"/"+mp4).renameTo(new File(getSD1()+"/Animeflv/download/"+i+"/"+mp4));
                            FileChannel inChannel = new FileInputStream(new File(sourceLocation+"/"+i+"/"+mp4)).getChannel();
                            FileChannel outChannel = new FileOutputStream(new File(FileUtil.init(context).getSDPath() + "/Animeflv/download/" + i + "/" + mp4)).getChannel();
                            try {
                                inChannel.transferTo(0, inChannel.size(), outChannel);
                            }
                            finally {
                                if (inChannel != null)
                                    inChannel.close();
                                if (outChannel != null)
                                    outChannel.close();
                            }
                            File file = new File(inSD.getPath(), mp4);
                            if (file.exists()) {
                                if (!new File(scan.getPath(), mp4).exists()) {
                                    Log.d("Move ok", mp4);
                                } else {
                                    if (new File(scan.getPath(), mp4).delete()) {
                                        Log.d("Move ok", mp4);
                                    } else {
                                        Log.d("Move error", mp4);
                                        throw new IOException("error");
                                    }
                                }
                            }else {
                                Log.d("Move error", mp4);
                                throw new IOException("error");
                            }
                        }else {
                            if (new File(scan.getPath(), mp4).delete()){
                                Log.d("Move ok", mp4);
                            }
                        }
                    }

                }
            }
        }
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            if (s.contains("Error")) {
                Toast.makeText(context, s, Toast.LENGTH_LONG).show();
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Movidos correctamente", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }
}
