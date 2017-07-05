package knf.animeflv.Utils;

import android.content.Context;
import android.net.Uri;

import com.squareup.picasso.UrlConnectionDownloader;

import java.io.IOException;
import java.net.HttpURLConnection;

import knf.animeflv.Cloudflare.BypassHolder;

/**
 * Created by Jordy on 04/03/2017.
 */

public class PicassoDownloader extends UrlConnectionDownloader {
    public PicassoDownloader(Context context) {
        super(context);
    }

    @Override
    protected HttpURLConnection openConnection(Uri path) throws IOException {
        HttpURLConnection conn = super.openConnection(path);
        conn.setRequestProperty("Cookie", BypassHolder.cookieKeyDuid + "=" + BypassHolder.valueDuid + "; " + BypassHolder.cookieKeyClearance + "=" + BypassHolder.valueClearance);
        conn.setRequestProperty("User-Agent", BypassHolder.getUserAgent());
        conn.setInstanceFollowRedirects(true);
        return conn;
    }
}
