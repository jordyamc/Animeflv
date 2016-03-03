package knf.animeflv;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 * Created by Jordy on 12/08/2015.
 */
public class MoveFiles extends AsyncTask<String,String,String> {
    ProgressDialog dialog;
    Context context;
    public MoveFiles(Context c){
        context=c;
    }
    @Override
    protected String doInBackground(String... params) {
        try {
            MoveFiles();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error " + e.getMessage();
        }
        return "";
    }
    public String getSD(){
        String sd = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (android.os.Build.DEVICE.contains("samsung") || android.os.Build.MANUFACTURER.contains("samsung")) {
            File f = new File(Environment.getExternalStorageDirectory().getParent() + "/extSdCard");
            if (f.exists() && f.isDirectory()) {
                sd = Environment.getExternalStorageDirectory().getParent() + "/extSdCard";
            } else {
                f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/external_sd");
                if (f.exists() && f.isDirectory()) {
                    sd = Environment.getExternalStorageDirectory().getAbsolutePath() + "/external_sd";
                } else {
                    sd = "NoSD";
                }
            }
        }
        return sd;
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
                    Log.e("path",sSDpath);
                    break;
                }
            }
        }
        return sSDpath;
    }
    public void MoveFiles() throws IOException {
        File sourceLocation=new File(Environment.getExternalStorageDirectory() + "/Animeflv/download");
        File targetLocation=new File(getSD1() + "/Animeflv/download");
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists() && !targetLocation.mkdirs()) {
                throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
            }
            String[] children = sourceLocation.list();
            for (String i:children) {
                File scan=new File(sourceLocation,i);
                if (scan.isDirectory()) {
                    String[] archivos = scan.list();
                    for (String mp4:archivos){
                        File inSD=new File(targetLocation.getPath(),i);
                        if (!inSD.exists()){
                            inSD.mkdir();
                        }
                        if (!new File(inSD.getPath(), mp4).exists()) {
                            //boolean success = new File(sourceLocation+"/"+i+"/"+mp4).renameTo(new File(getSD1()+"/Animeflv/download/"+i+"/"+mp4));
                            FileChannel inChannel = new FileInputStream(new File(sourceLocation+"/"+i+"/"+mp4)).getChannel();
                            FileChannel outChannel = new FileOutputStream(new File(getSD1()+"/Animeflv/download/"+i+"/"+mp4)).getChannel();
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
        if (s.contains("Error")){
            Toast.makeText(context,s , Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(context,"Movidos correctamente",Toast.LENGTH_SHORT).show();
        }

    }
}
