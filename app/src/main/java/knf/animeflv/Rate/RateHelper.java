package knf.animeflv.Rate;

import androidx.annotation.Nullable;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import knf.animeflv.JsonFactory.ServerGetter;

/**
 * Created by Jordy on 22/04/2017.
 */

public class RateHelper {
    public static void rate(String aid, int stars, final RateResponse rateResponse) {
        RequestParams params = new RequestParams();
        params.put("type", "anime");
        params.put("rating", String.valueOf(stars));
        params.put("id", aid);
        ServerGetter.getClient().post("http://animeflv.net/api/animes/rate", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.getInt("success") == 1) {
                        rateResponse.onResponse(response.getString("rating_votes"), response.getDouble("rating"), true);
                    } else {
                        rateResponse.onResponse(null, -1f, false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    rateResponse.onResponse(null, -1f, false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                rateResponse.onResponse(null, -1f, false);
            }
        });
    }

    public interface RateResponse {
        void onResponse(@Nullable String votes, double rating, boolean success);
    }
}
