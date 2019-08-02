package knf.animeflv.ServerReload.Adapter;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Jordy on 05/05/2016.
 */
public class CustomRecycler extends RecyclerView {
    private boolean verticleScrollingEnabled = true;

    public CustomRecycler(Context context) {
        super(context);
    }

    public CustomRecycler(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecycler(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void enableVersticleScroll(boolean enabled) {
        verticleScrollingEnabled = enabled;
    }

    public boolean isVerticleScrollingEnabled() {
        return verticleScrollingEnabled;
    }

    @Override
    public int computeVerticalScrollRange() {

        if (isVerticleScrollingEnabled())
            return super.computeVerticalScrollRange();
        return 0;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (isVerticleScrollingEnabled())
            return super.onInterceptTouchEvent(e);
        return false;

    }
}
