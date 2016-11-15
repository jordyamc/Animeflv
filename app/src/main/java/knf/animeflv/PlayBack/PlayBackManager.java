package knf.animeflv.PlayBack;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.media.MediaControlIntent;
import android.support.v7.media.MediaItemStatus;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.support.v7.media.MediaSessionStatus;
import android.support.v7.media.RemotePlaybackClient;
import android.util.Log;

public class PlayBackManager {
    private static PlayBackManager manager;
    private RemotePlaybackClient remotePlaybackClient;
    private MediaRouter.Callback mediaRouterCallback;
    private MediaRouteSelector selector;
    private MediaRouter mediaRouter;
    private Activity activity;
    private boolean isCastSelected = false;

    public PlayBackManager(Activity activity) {
        this.activity = activity;
        selector = new MediaRouteSelector.Builder()
                .addControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)
                .build();
        mediaRouter = MediaRouter.getInstance(activity);
        mediaRouter.addCallback(getSelector(), getMediaRouterCallback(), MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN);
    }

    public static PlayBackManager get(Activity activity) {
        if (manager == null)
            manager = new PlayBackManager(activity);
        return manager;
    }

    public static PlayBackManager get() {
        return manager;
    }

    public MediaRouteSelector getSelector() {
        return selector;
    }

    private void updatePlayback(MediaRouter.RouteInfo routeInfo) {
        remotePlaybackClient = new RemotePlaybackClient(activity, routeInfo);
    }

    public void addCallbacks() {
        mediaRouter.addCallback(getSelector(), getMediaRouterCallback(), MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN);
    }

    public void removeCallbacks() {
        mediaRouter.removeCallback(getMediaRouterCallback());
    }

    public boolean isCastSelected() {
        return isCastSelected;
    }

    public void play(String url) {
        if (remotePlaybackClient != null && isCastSelected)
            remotePlaybackClient.play(Uri.parse(url), "video/mp4", null, 0, null, new RemotePlaybackClient.ItemActionCallback() {
                @Override
                public void onResult(Bundle data, String sessionId, MediaSessionStatus sessionStatus, String itemId, MediaItemStatus itemStatus) {
                    Log.e("Remote Play", "Success");
                }

                @Override
                public void onError(String error, int code, Bundle data) {
                    Log.e("Remote Play", "Error code: " + code + "-" + error);
                }
            });
    }

    private MediaRouter.Callback getMediaRouterCallback() {
        if (mediaRouterCallback == null)
            mediaRouterCallback = new MediaRouter.Callback() {
                @Override
                public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo route) {
                    isCastSelected = true;
                    updatePlayback(route);
                }

                @Override
                public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo route, int reason) {
                    isCastSelected = false;
                    updatePlayback(route);
                }

                @Override
                public void onRouteRemoved(MediaRouter router, MediaRouter.RouteInfo route) {
                    isCastSelected = false;
                    updatePlayback(route);
                }

                @Override
                public void onRouteChanged(MediaRouter router, MediaRouter.RouteInfo route) {
                    isCastSelected = true;
                    updatePlayback(route);
                }
            };
        return mediaRouterCallback;
    }
}
