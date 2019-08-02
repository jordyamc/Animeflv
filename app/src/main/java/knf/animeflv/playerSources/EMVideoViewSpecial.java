package knf.animeflv.playerSources;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.devbrackets.android.exomedia.ui.widget.EMVideoView;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;

/**
 * Created by Jordy on 30/06/2016.
 */

public class EMVideoViewSpecial extends EMVideoView {

    public EMVideoViewSpecial(Context context) {
        super(context);
    }

    public EMVideoViewSpecial(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EMVideoViewSpecial(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EMVideoViewSpecial(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setControls(@Nullable VideoControls controls) {
        if (videoControls != null && videoControls != controls) {
            removeView(videoControls);
        }

        if (controls != null) {
            videoControls = controls;
            controls.setVideoView(this);
            addView(controls);
        }
    }
}
