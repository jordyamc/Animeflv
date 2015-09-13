package knf.animeflv;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jordy on 22/08/2015.
 */
public class RequestFav extends AsyncTask<String,String,String> {
    InputStream is;
    String _response;
    callback call;
    TaskType taskType;
    Parser parser=new Parser();
    StringBuilder builder = new StringBuilder();
    HttpURLConnection c = null;
    URL u;
    public interface callback{
        public void favCall(String data,TaskType taskType);
    }
    public RequestFav(Context con, TaskType taskType){
        call=(callback) con;
        this.taskType=taskType;

    }
    @Override
    protected String doInBackground(String... params) {
        List<String> list=new ArrayList<String>();
        for (String i:params) {
            try {
                Log.d("aid", i);
                u = new URL("http://animeflv.net/api.php?accion=anime&aid="+i);
                c = (HttpURLConnection) u.openConnection();
                c.setRequestProperty("Content-length", "0");
                c.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4");
                c.setUseCaches(false);
                c.setAllowUserInteraction(false);
                c.setConnectTimeout(15000);
                c.connect();
                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                //c.disconnect();
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                list.add(parser.getTit(sb.toString()));
            } catch (Exception e) {
                Log.e("log_tag", "Error in http connection " + e.toString());
                list.add("");
            }
        }
        String[] favoritos=new String[list.size()];
        list.toArray(favoritos);
        StringBuilder builder = new StringBuilder();
        for(String i : favoritos) {
            builder.append(":::" + i);
        }
        Log.d("URL Normal", u.toString());
        if (c.getURL()!=u){
            Log.d("URL Actual", c.getURL().toString());
            _response="";
        }else {
            Log.d("URL Actual",c.getURL().toString());
            _response=builder.toString();
        }
        return _response;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        call.favCall(s, taskType);
    }
}
