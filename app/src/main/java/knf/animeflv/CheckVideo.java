package knf.animeflv;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jordy on 12/08/2015.
 */
public class CheckVideo extends AsyncTask<String,String,String> {
    InputStream is;
    String _response;
    callback call;
    TaskType taskType;
    int position;

    public interface callback{
        void sendtext1(String data, TaskType taskType, int position);
    }
    public CheckVideo(Context con, TaskType taskType, int pos){
        call=(callback) con;
        this.taskType=taskType;
        this.position=pos;

    }
    @Override
    protected String doInBackground(String... params) {
        StringBuilder builder = new StringBuilder();
        HttpURLConnection c = null;
        try {
            Log.d("URL",params[0]);
            URL u = new URL(params[0]);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestProperty("Content-length", "0");
            c.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4");
            c.setUseCaches(true);
            c.setConnectTimeout(5000);
            c.setAllowUserInteraction(false);
            c.connect();
            //c.disconnect();
            Log.d("URL Normal", u.toString());
            if (c.getResponseCode()!=HttpURLConnection.HTTP_NOT_FOUND){
                if (c.getURL()!=u){
                    Log.d("URL ERROR", c.getURL().toString());
                    _response="error";
                }else {
                    if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        Log.d("URL OK", c.getURL().toString());
                        _response = "ok";
                    } else {
                        Log.d("URL ERROR", c.getURL().toString());
                        _response = "error";
                    }
                }
            }else {
                _response = "error";
            }
            //String fullPage = page.asXml();
        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
            _response="error";
        }
        return _response;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        call.sendtext1(s,taskType,position);
    }
}
