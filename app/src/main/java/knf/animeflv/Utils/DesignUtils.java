package knf.animeflv.Utils;

import android.content.Context;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.cardview.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.captain_miao.optroundcardview.OptRoundCardView;
import com.makeramen.roundedimageview.RoundedImageView;

import knf.animeflv.Parser;

/**
 * Created by Jordy on 22/06/2017.
 */

public class DesignUtils {
    @UiThread
    public static void setCardSpaceStyle(Context context, OptRoundCardView cardView) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getString("list_style", "0").equals("1")) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                cardView.setCardElevation(0);
                cardView.setPreventCornerOverlap(false);
                cardView.setUseCompatPadding(true);
                lp.setMargins((int) Parser.toPx(context, 10), (int) Parser.toPx(context, -8), (int) Parser.toPx(context, 10), (int) Parser.toPx(context, -8));
                cardView.setLayoutParams(lp);
            } else {
                lp.setMargins((int) Parser.toPx(context, 10), 0, (int) Parser.toPx(context, 10), 0);
                cardView.setLayoutParams(lp);
            }
            cardView.requestLayout();
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            cardView.setCardElevation(0);
            cardView.setPreventCornerOverlap(false);
            cardView.setUseCompatPadding(true);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins((int) Parser.toPx(context, 10), (int) Parser.toPx(context, -5), (int) Parser.toPx(context, 10), (int) Parser.toPx(context, -5));
            cardView.setLayoutParams(lp);
        }
    }

    @UiThread
    public static void setCardSpaceStyle(Context context, CardView cardView) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getString("list_style", "0").equals("1")) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                cardView.setCardElevation(0);
                cardView.setPreventCornerOverlap(false);
                cardView.setUseCompatPadding(true);
                lp.setMargins((int) Parser.toPx(context, 10), (int) Parser.toPx(context, -8), (int) Parser.toPx(context, 10), (int) Parser.toPx(context, -8));
                cardView.setLayoutParams(lp);
            } else {
                lp.setMargins((int) Parser.toPx(context, 10), 0, (int) Parser.toPx(context, 10), 0);
                cardView.setLayoutParams(lp);
            }
            cardView.requestLayout();
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            cardView.setCardElevation(0);
            cardView.setPreventCornerOverlap(false);
            cardView.setUseCompatPadding(true);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins((int) Parser.toPx(context, 10), (int) Parser.toPx(context, -5), (int) Parser.toPx(context, 10), (int) Parser.toPx(context, -5));
            cardView.setLayoutParams(lp);
        }
    }

    @UiThread
    public static void setCardStyle(Context context, int total, int position, OptRoundCardView cardView, @Nullable View separator, RoundedImageView roundedImageView) {
        boolean use_space = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("use_space", false);
        if (PreferenceManager.getDefaultSharedPreferences(context).getString("list_style", "0").equals("1")) {
            if (total == 1) {
                cardView.showCorner(true, true, true, true);
                if (separator != null)
                    separator.setVisibility(View.GONE);
                if (!use_space && roundedImageView != null)
                    roundedImageView.setCornerRadius(10, 0, 10, 0);
            } else if (position == 0) {
                cardView.showCorner(true, true, false, false);
                if (separator != null)
                    separator.setVisibility(View.GONE);
                if (!use_space && roundedImageView != null)
                    roundedImageView.setCornerRadius(10, 0, 0, 0);
            } else if (position == total - 1) {
                cardView.showCorner(false, false, true, true);
                if (separator != null)
                    separator.setVisibility(View.VISIBLE);
                if (!use_space && roundedImageView != null)
                    roundedImageView.setCornerRadius(0, 0, 10, 0);
            } else {
                cardView.showCorner(false, false, false, false);
                if (separator != null)
                    separator.setVisibility(View.VISIBLE);
                if (!use_space && roundedImageView != null)
                    roundedImageView.setCornerRadius(0, 0, 0, 0);
            }
        } else {
            cardView.showCorner(true, true, true, true);
            if (separator != null)
                separator.setVisibility(View.GONE);
            if (!use_space && roundedImageView != null)
                roundedImageView.setCornerRadius(10, 0, 10, 0);
        }
        if (roundedImageView != null)
            roundedImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    public static void setCardStyle(Context context, int total, int position, @Nullable View separator) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getString("list_style", "0").equals("1")) {
            if (total == 1) {
                if (separator != null)
                    separator.setVisibility(View.GONE);
            } else if (position == 0) {
                if (separator != null)
                    separator.setVisibility(View.GONE);
            }
        } else {
            if (separator != null)
                separator.setVisibility(View.GONE);
        }
    }

    public static boolean forcePhone(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("force_phone", false);
    }
}
