package knf.animeflv.CustomViews;

import android.content.Context;
import android.support.annotation.Dimension;
import android.support.annotation.Px;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by Jordy on 08/10/2016.
 */

public class CustomFrame extends FrameLayout {

    public CustomFrame(Context context) {
        super(context);
    }

    public CustomFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomFrame(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public float getXFraction() {
        return getX() / getWidth(); // TODO: guard divide-by-zero
    }

    @Dimension
    public void setXFraction(float xFraction) {
        // TODO: cache width
        @Px
        int px = (int) ((getWidth() > 0) ? (xFraction * getWidth()) : -9999);
        setX(px);
    }
}
