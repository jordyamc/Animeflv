package knf.animeflv.info.Helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import knf.animeflv.info.InfoFragments;

/**
 * Created by Jordy on 09/06/2016.
 */

public class InfoHelper {

    public static void open(Activity activity, SharedItem sharedItem, BundleItem... items) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedItem.view.setTransitionName("img");
            ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sharedItem.view, sharedItem.tag);
            Bundle bundleInfo = new Bundle();
            for (BundleItem item : items) {
                bundleInfo.putString(item.key, item.value);
            }
            Intent intent = new Intent(activity, InfoFragments.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtras(bundleInfo);
            activity.startActivity(intent, compat.toBundle());
        } else {
            Bundle bundleInfo = new Bundle();
            for (BundleItem item : items) {
                bundleInfo.putString(item.key, item.value);
            }
            Intent intent = new Intent(activity, InfoFragments.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtras(bundleInfo);
            activity.startActivity(intent);
        }
    }

    public static Intent get(Context context, BundleItem... items) {
        Bundle bundleInfo = new Bundle();
        for (BundleItem item : items) {
            bundleInfo.putString(item.key, item.value);
        }
        Intent intent = new Intent(context, InfoFragments.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(bundleInfo);
        return intent;
    }

    public static void openResult(Activity activity, SharedItem sharedItem, BundleItem... items) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedItem.view.setTransitionName("img");
            ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sharedItem.view, sharedItem.tag);
            Bundle bundleInfo = new Bundle();
            for (BundleItem item : items) {
                bundleInfo.putString(item.key, item.value);
            }
            Intent intent = new Intent(activity, InfoFragments.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtras(bundleInfo);
            activity.startActivityForResult(intent, 55774);
        } else {
            Bundle bundleInfo = new Bundle();
            for (BundleItem item : items) {
                bundleInfo.putString(item.key, item.value);
            }
            Intent intent = new Intent(activity, InfoFragments.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtras(bundleInfo);
            activity.startActivityForResult(intent, 55774);
        }
    }

    public static void open(Activity activity, SharedItem sharedItem, int flag, BundleItem... items) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedItem.view.setTransitionName("img");
            ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sharedItem.view, sharedItem.tag);
            Bundle bundleInfo = new Bundle();
            for (BundleItem item : items) {
                bundleInfo.putString(item.key, item.value);
            }
            Intent intent = new Intent(activity, InfoFragments.class);
            intent.addFlags(flag);
            intent.putExtras(bundleInfo);
            activity.startActivity(intent, compat.toBundle());
        } else {
            Bundle bundleInfo = new Bundle();
            for (BundleItem item : items) {
                bundleInfo.putString(item.key, item.value);
            }
            Intent intent = new Intent(activity, InfoFragments.class);
            intent.addFlags(flag);
            intent.putExtras(bundleInfo);
            activity.startActivity(intent);
        }
    }

    public static class BundleItem {
        public static final String KEY_AID = "aid";
        public static final String KEY_TITLE = "title";
        public String key;
        public String value;

        public BundleItem(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    public static class SharedItem {
        public String tag;
        public View view;

        public SharedItem(View view, String tag) {
            this.tag = tag;
            this.view = view;
        }
    }
}
