package knf.animeflv;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 * Created by Jordy on 15/10/2015.
 */
public class Move extends Activity {
    ProgressDialog dialog;
    int num=1;
    int cuenta=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        count(Environment.getExternalStorageDirectory() + "/Animeflv/download");
            dialog = ProgressDialog.show(this, "", "Moviendo video 1 de " + cuenta, true, false);
            try {
                MoveFiles();
            } catch (IOException e) {
                e.printStackTrace();
                dialog.dismiss();
                Toast.makeText(this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
    }
    public String getSD1(){
        String sSDpath = null;
        File fileCur = null;
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
                    Log.e("path", sSDpath);
                    break;
                }
            }
        }
        return sSDpath;
    }
    public void count(String path){
        File sourceLocation=new File(path);
        File[] files = sourceLocation.listFiles();
        if (files != null)
            for (File i:files) {
                if (i.isDirectory()) {
                    count(i.getAbsolutePath());
                }else {
                    cuenta++;
                    Log.d("MP4",i.getAbsolutePath());
                }
            }
    }
    public void MoveFiles() throws IOException {
        File sourceLocation = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download");
        File targetLocation = new File(getSD1() + "/Animeflv/download");
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
                            FileChannel outChannel = new FileOutputStream(new File(getSD1() + "/Animeflv/download/" + i + "/" + mp4)).getChannel();
                            try {
                                Toast.makeText(this,"Moviendo video "+num,Toast.LENGTH_SHORT).show();
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
                                    num++;
                                    dialog.setMessage("Moviendo video"+num+"de "+cuenta);
                                } else {
                                    if (new File(scan.getPath(), mp4).delete()) {
                                        Log.d("Move ok", mp4);
                                        num++;
                                        dialog.setMessage("Moviendo video"+num+"de "+cuenta);
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
                                num++;
                                dialog.setMessage("Moviendo video"+num+"de "+cuenta);
                            }
                        }
                    }
                    dialog.dismiss();
                    finish();
                }
            }
        }
    }
}
