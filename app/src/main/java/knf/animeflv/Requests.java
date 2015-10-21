package knf.animeflv;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jordy on 12/08/2015.
 */
public class Requests extends AsyncTask<String,String,String> {
    InputStream is;
    String _response;
    callback call;
    TaskType taskType;

    public interface callback{
        public void sendtext1(String data,TaskType taskType);
    }
    public Requests(Context con, TaskType taskType){
        call=(callback) con;
        this.taskType=taskType;

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
            c.setRequestProperty( "User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4" );
            c.setUseCaches(true);
            c.setConnectTimeout(15000);
            c.setAllowUserInteraction(false);
            c.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
            //c.disconnect();
            StringBuilder sb = new StringBuilder();
            String line="";
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            Log.d("URL Normal", u.toString());
            if (c.getURL()!=u){
                if (!c.getURL().toString().contains("http://animeflv")) {
                    Log.d("URL ERROR", c.getURL().toString());
                    _response = "error";
                }else {
                    Log.d("URL OK",c.getURL().toString());
                    if (c.getResponseCode()==HttpURLConnection.HTTP_OK) {
                        _response = sb.toString();
                    }else{
                        _response="error";
                    }
                }
            }else {
                Log.d("URL OK",c.getURL().toString());
                if (c.getResponseCode()==HttpURLConnection.HTTP_OK) {
                    _response = sb.toString();
                }else{
                    _response="error";
                }
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
        call.sendtext1(s,taskType);
    }
}
