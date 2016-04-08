package knf.animeflv.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import knf.animeflv.R;
import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 07/04/2016.
 */
public class UtilSound {
    private static Context context;

    public static void init(Context context){
        UtilSound.context=context;
    }

    public static MediaPlayer getMediaPlayer(Context cont, int which) {
        switch (which){
            case 0:
                return MediaPlayer.create(cont, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            default:
                try {
                    File file=new File(Environment.getExternalStorageDirectory()+"/Animeflv/cache/.sounds",getSoundsFileName(which));
                    if (file.exists()) {
                        return MediaPlayer.create(context, Uri.fromFile(file));
                    }else {
                        Toaster.toast("Archivo no encontrado");
                        return getMediaPlayer(context,0);
                    }
                }catch (Exception e){
                    return getMediaPlayer(cont,0);
                }
        }
    }

    public static Uri getSoundUri(int not) {
        switch (not) {
            case 0:
                return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            default:
                try {
                    File file=new File(Environment.getExternalStorageDirectory()+"/Animeflv/cache/.sounds",getSoundsFileName(not));
                    if (file.exists()) {
                        file.setReadable(true,false);
                        return Uri.fromFile(file);
                    }else {
                        Log.d("Sound Uri","Not found");
                        return getSoundUri(0);
                    }
                }catch (Exception e){
                    return getSoundUri(0);
                }
        }
    }

    public static String[] getSoundsNameList() {
        List<String> list=new ArrayList<>();
        try {
            JSONObject nobject=new JSONObject(context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("SoundJson",""));
            JSONArray array = nobject.getJSONArray("sounds");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                list.add(object.getString("name"));
            }
            list.add(0,"Por Defecto");
            String[] tmp=new String[list.size()];
            list.toArray(tmp);
            return tmp;
        }catch (Exception e){
            return new String[]{};
        }
    }
    public static String getSoundsFileName(int pos) {
        try {
            JSONObject nobject=new JSONObject(context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("SoundJson",""));
            JSONArray array = nobject.getJSONArray("sounds");
            JSONObject object = array.getJSONObject(pos-1);
            return object.getString("file");
        }catch (Exception e){
            return "null";
        }
    }
}
