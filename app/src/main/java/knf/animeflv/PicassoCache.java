package knf.animeflv;

import android.content.Context;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.net.CookieManager;
import java.net.CookiePolicy;

import knf.animeflv.Cloudflare.BypassHolder;
import knf.animeflv.Utils.PicassoDownloader;

public class PicassoCache {

    /**
     * Static Picasso Instance
     */
    private static Picasso picassoInstance = null;

    /**
     * PicassoCache Constructor
     *
     * @param context application Context
     */
    private PicassoCache (Context context) {
        OkHttpClient client = new OkHttpClient();
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        client.setCookieHandler(cookieManager);

        Downloader downloader;
        if (BypassHolder.isActive) {
            downloader = new PicassoDownloader(context);
        } else {
            downloader = new OkHttpDownloader(context, Integer.MAX_VALUE);
        }

        Picasso.Builder builder = new Picasso.Builder(context);
        //builder.loggingEnabled(true);
        builder.downloader(downloader);

        picassoInstance = builder.build();
    }

    /**
     * Get Singleton Picasso Instance
     *
     * @param context application Context
     * @return Picasso instance
     */
    public static Picasso getPicassoInstance (Context context) {

        if (picassoInstance == null) {

            new PicassoCache(context);
            return picassoInstance;
        }
        return picassoInstance;
    }


}
