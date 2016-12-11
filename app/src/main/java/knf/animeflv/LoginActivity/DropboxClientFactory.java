package knf.animeflv.LoginActivity;

import android.util.Log;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.http.OkHttp3Requestor;
import com.dropbox.core.v2.DbxClientV2;

/**
 * Created by Jordy on 07/12/2016.
 */

public class DropboxClientFactory {

    private static DbxClientV2 sDbxClient;

    public static void init(String accessToken) {
        if (sDbxClient == null) {
            try {
                DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder("dropbox_app")
                        .withHttpRequestor(OkHttp3Requestor.INSTANCE)
                        .build();

                sDbxClient = new DbxClientV2(requestConfig, accessToken);
                Log.e("Dropbox", "Init Success");
            } catch (Exception e) {
                Log.e("Dropbox", "Init Error", e);
            }
        }
    }

    public static DbxClientV2 getClient() {
        if (sDbxClient == null) {
            return null;
        }
        return sDbxClient;
    }
}