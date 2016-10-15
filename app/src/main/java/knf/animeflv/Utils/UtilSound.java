package knf.animeflv.Utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.cleveroad.audiowidget.AudioWidget;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import knf.animeflv.ColorsRes;
import knf.animeflv.R;
import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 07/04/2016.
 */
public class UtilSound {
    public static boolean isNotSoundShow = false;
    public static int NOT_SOUND_ID = 58984;
    private static Context context;
    private static AudioWidget widget;
    private static int currentMediaPlayerInt = 0;

    public static void init(Context context){
        UtilSound.context=context;
    }

    public static MediaPlayer getMediaPlayer(Context cont, int which) {
        UtilSound.currentMediaPlayerInt = which;
        switch (which){
            case 0:
                MediaPlayer defplayer = MediaPlayer.create(cont, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                if (defplayer!=null)defplayer.setLooping(true);
                setCurrentMediaPlayer(defplayer);
                return getCurrentMediaPlayer();
            default:
                try {
                    File file=new File(Environment.getExternalStorageDirectory()+"/Animeflv/cache/.sounds",getSoundsFileName(which));
                    if (file.exists()) {
                        MediaPlayer splayer = MediaPlayer.create(context, Uri.fromFile(file));
                        splayer.setLooping(true);
                        setCurrentMediaPlayer(splayer);
                        return getCurrentMediaPlayer();
                    }else {
                        Toaster.toast("Archivo no encontrado");
                        return getMediaPlayer(context,0);
                    }
                }catch (Exception e){
                    return getMediaPlayer(cont,0);
                }
        }
    }

    public static void toogleNotSound(int wich) {
        if (isNotSoundShow) {
            NotManager().cancel(NOT_SOUND_ID);
            UtilSound.isNotSoundShow = false;
        } else {
            NotManager().notify(NOT_SOUND_ID, getBuilder(wich).build());
            UtilSound.isNotSoundShow = true;
        }

    }

    public static void UpdateNotSound(int wich) {
        NotManager().notify(NOT_SOUND_ID, getBuilder(wich).build());
        UtilSound.isNotSoundShow = true;

    }

    private static NotificationCompat.Builder getBuilder(int wich) {
        Intent intent = new Intent(context, FastActivity.class);
        intent.putExtra("key", FastActivity.STOP_SOUND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent configurations = new Intent(context, FastActivity.class);
        configurations.putExtra("key", FastActivity.OPEN_CONF_SOUNDS);
        configurations.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return new NotificationCompat.Builder(context)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_not_r)
                .setContentTitle("AnimeFLV")
                .setContentText("Reproduciendo " + getSoundName(wich))
                .setContentIntent(PendingIntent.getActivity(context, (int) (System.currentTimeMillis() & 0xfffffff), configurations, PendingIntent.FLAG_CANCEL_CURRENT))
                .addAction(R.drawable.ic_stop, "Detener", PendingIntent.getActivity(context, (int) (System.currentTimeMillis() & 0xfffffff), intent, PendingIntent.FLAG_CANCEL_CURRENT));
    }

    public static NotificationManager NotManager() {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static int getCurrentMediaPlayerInt() {
        return currentMediaPlayerInt;
    }

    public static void setCurrentMediaPlayerInt(int currentMediaPlayerInt) {
        UtilSound.currentMediaPlayerInt = currentMediaPlayerInt;
    }

    public static int getSetDefMediaPlayerInt() {
        UtilSound.currentMediaPlayerInt = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(Keys.Conf.SOUNDS, "0"));
        return getCurrentMediaPlayerInt();
    }

    public static MediaPlayer getCurrentMediaPlayer() {
        return UtilDialogPref.getPlayer();
    }

    public static void setCurrentMediaPlayer(MediaPlayer currentMediaPlayer) {
        UtilDialogPref.setPlayer(currentMediaPlayer);
    }

    public static AudioWidget getAudioWidget() {
        if (widget == null) {
            Drawable stop = context.getResources().getDrawable(R.drawable.stop);
            widget = new AudioWidget.Builder(context)
                    .pauseDrawable(stop)
                    .darkColor(ThemeUtils.getAcentColor(context))
                    .lightColor(ColorsRes.Prim(context))
                    .expandWidgetColor(ThemeUtils.getAcentColor(context))
                    .crossOverlappedColor(ColorsRes.Prim(context))
                    .crossColor(ThemeUtils.getAcentColor(context))
                    .shadowColor(ColorsRes.Prim(context))
                    .build();
            widget.controller().start();
            widget.controller().albumCover(getAppIcon());
            widget.controller().onWidgetStateChangedListener(new AudioWidget.OnWidgetStateChangedListener() {
                @Override
                public void onWidgetStateChanged(@NonNull AudioWidget.State state) {
                    if (state == AudioWidget.State.REMOVED) {
                        if (getCurrentMediaPlayer().isPlaying()) {
                            getCurrentMediaPlayer().stop();
                        }
                    }
                }

                @Override
                public void onWidgetPositionChanged(int cx, int cy) {

                }
            });
            widget.controller().onControlsClickListener(new AudioWidget.OnControlsClickListener() {
                @Override
                public boolean onPlaylistClicked() {
                    return false;
                }

                @Override
                public void onPreviousClicked() {
                    int current = getCurrentMediaPlayerInt();
                    current--;
                    if (current < 0) {
                        current = getSoundsNameList().length - 1;
                    }
                    Toaster.toast(getSoundName(current));
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString("sonido", String.valueOf(current)).apply();
                    setCurrentMediaPlayerInt(current);
                    getCurrentMediaPlayer().stop();
                    setCurrentMediaPlayer(getMediaPlayer(context, current));
                    getCurrentMediaPlayer().start();
                }

                @Override
                public boolean onPlayPauseClicked() {
                    if (getCurrentMediaPlayer().isPlaying()) {
                        getCurrentMediaPlayer().stop();
                        getAudioWidget().hide();
                    }
                    return false;
                }

                @Override
                public void onNextClicked() {
                    int current = getCurrentMediaPlayerInt();
                    current++;
                    if (current > getSoundsNameList().length) {
                        current = 0;
                    }
                    Toaster.toast(getSoundName(current));
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString("sonido", String.valueOf(current)).apply();
                    setCurrentMediaPlayerInt(current);
                    getCurrentMediaPlayer().stop();
                    setCurrentMediaPlayer(getMediaPlayer(context, current));
                    getCurrentMediaPlayer().start();
                }

                @Override
                public void onAlbumClicked() {
                    Intent configurations = new Intent(context, FastActivity.class);
                    configurations.putExtra("key", FastActivity.OPEN_CONF_SOUNDS);
                    configurations.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(configurations);
                }

                @Override
                public void onPlaylistLongClicked() {

                }

                @Override
                public void onPreviousLongClicked() {

                }

                @Override
                public void onPlayPauseLongClicked() {

                }

                @Override
                public void onNextLongClicked() {

                }

                @Override
                public void onAlbumLongClicked() {
                    widget.collapse();
                }
            });
        }
        return widget;
    }

    public static Drawable getAppIcon() {
        return context.getResources().getDrawable(R.mipmap.ic_launcher);
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
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Uri uri = FileProvider.getUriForFile(context, "knf.animeflv.RequestsBackground", file);
                            context.grantUriPermission("com.android.systemui", uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            return uri;
                        } else {
                            return Uri.fromFile(file);
                        }
                    }else {
                        Log.d("Sound Uri","Not found");
                        return getSoundUri(0);
                    }
                }catch (Exception e){
                    e.printStackTrace();
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

    public static String getSoundName(int pos) {
        try {
            JSONObject nobject = new JSONObject(context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("SoundJson", ""));
            JSONArray array = nobject.getJSONArray("sounds");
            JSONObject object = array.getJSONObject(pos - 1);
            return object.getString("name");
        } catch (Exception e) {
            return "Por Defecto";
        }
    }
}
