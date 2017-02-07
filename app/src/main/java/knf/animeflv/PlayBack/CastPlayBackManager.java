package knf.animeflv.PlayBack;

import android.app.Activity;
import android.view.Menu;

import com.connectsdk.device.DevicePicker;

import es.munix.multidisplaycast.CastControlsActivity;
import es.munix.multidisplaycast.CastManager;
import es.munix.multidisplaycast.interfaces.CastListener;
import es.munix.multidisplaycast.interfaces.PlayStatusListener;
import knf.animeflv.Parser;
import knf.animeflv.Seen.SeenManager;
import knf.animeflv.TaskType;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

public class CastPlayBackManager implements CastListener, PlayStatusListener {
    private static final String TAG = "CastPlayBackManager";
    private static CastPlayBackManager manager;
    private Activity activity;
    private boolean isConnected = false;
    private PlayBackInterface playBackInterface;
    private String castingEid = "null";

    public CastPlayBackManager(Activity activity) {
        this.activity = activity;
        CastManager.getInstance().setTheme(ThemeUtils.isAmoled(activity) ? DevicePicker.Theme.DARK : DevicePicker.Theme.LIGTH);
        CastManager.getInstance().setDiscoveryManager();
        CastManager.getInstance().setCastListener(TAG, this);
        CastManager.getInstance().setPlayStatusListener(TAG, this);
    }

    public static CastPlayBackManager get(Activity activity) {
        if (manager == null)
            manager = new CastPlayBackManager(activity);
        return manager;
    }

    public void setPlayBackListener(PlayBackInterface playBackListener) {
        this.playBackInterface = playBackListener;
    }

    public void registrerMenu(Menu menu, int id) {
        CastManager.getInstance().registerForActivity(activity, menu, id);
    }

    public void destroyManager() {
        CastManager.getInstance().unsetCastListener(TAG);
        CastManager.getInstance().unsetPlayStatusListener(TAG);
        CastManager.getInstance().onDestroy();
    }

    public boolean isDeviceConnected() {
        return isConnected;
    }

    public void play(String url, String eid) {
        if (isDeviceConnected()) {
            castingEid = eid;
            SeenManager.get(activity).setSeenStateUpload(eid, true);
            String[] semi = eid.replace("E", "").split("_");
            String aid = semi[0];
            String num = semi[1];
            CastManager.getInstance().playMedia(url, "video/mp4", new Parser().getTitCached(aid), "Capítulo " + num, getPreviewUrl());
        } else {
            isConnected = false;
            Toaster.toast("No hay dispositivos conectados");
        }
    }

    public void stop() {
        castingEid = "null";
        CastManager.getInstance().stop();
    }

    public String getCastingEid() {
        return castingEid;
    }

    public boolean isCasting(String eid) {
        return getCastingEid().equals(eid);
    }

    private String getPreviewUrl() {
        String base = new Parser().getBaseUrl(TaskType.NORMAL, activity).replace("api2.", "") + "/images/";
        return base + (ThemeUtils.isAmoled(activity) ? "preview_dark.png" : "preview_light.png");
    }

    @Override
    public void isConnected() {
        isConnected = true;
    }

    @Override
    public void isDisconnected() {
        isConnected = false;
    }

    @Override
    public void onPlayStatusChanged(int i) {
        switch (i) {
            case STATUS_START_PLAYING:
                if (playBackInterface != null)
                    playBackInterface.onPlay();
                CastManager.getInstance().startControlsActivity(activity, CastControlsActivity.class);
                break;
            case STATUS_FINISHED:
            case STATUS_STOPPED:
                castingEid = "null";
                if (playBackInterface != null)
                    playBackInterface.onStop();
                break;

            case STATUS_PAUSED:
                if (playBackInterface != null)
                    playBackInterface.onPause();
                break;

            case STATUS_NOT_SUPPORT_LISTENER:
                Toaster.toast("No soportado!!");
                if (playBackInterface != null)
                    playBackInterface.onExit();
                break;
        }
    }

    @Override
    public void onPositionChanged(long l) {

    }

    @Override
    public void onTotalDurationObtained(long l) {

    }

    @Override
    public void onSuccessSeek() {

    }

    public interface PlayBackInterface {
        void onPlay();

        void onPause();

        void onStop();

        void onExit();
    }
}
