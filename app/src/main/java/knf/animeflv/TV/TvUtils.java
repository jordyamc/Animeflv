package knf.animeflv.TV;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;

import knf.animeflv.Utils.ThemeUtils;

/**
 * Created by Jordy on 07/08/2017.
 */

public class TvUtils {
    public static void setFocusable(final Context context, final ImageButton... buttons) {
        if (ThemeUtils.isTV(context)) {
            for (final ImageButton button : buttons) {
                button.setFocusable(true);
                button.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        ThemeUtils.Theme theme = ThemeUtils.Theme.create(context);
                        button.clearColorFilter();
                        if (b) {
                            button.setColorFilter(theme.accent);
                        } else {
                            button.setColorFilter(theme.iconFilter);
                        }
                    }
                });
            }
        }
    }
}
