package knf.animeflv.playerSources;

import android.view.MotionEvent;

/**
 * Created by Jordy on 30/06/2016.
 */

public interface VideoListeners{
    void onSingleTouch();
    void onDoubleTouch();
    void onHorizontalScroll(MotionEvent event, float delta);
    void onVerticalScroll(MotionEvent motionEvent, float delta, int direction);
}
