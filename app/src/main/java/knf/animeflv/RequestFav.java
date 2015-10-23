package knf.animeflv;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
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
            File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + i + ".txt");
            if (!file.exists()) {
                try {
                    Log.d("aid", i);
                    u = new URL("http://animeflv.com/api.php?accion=anime&aid=" + i);
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
                    if (c.getURL()==u){
                        writeToFile(sb.toString(),file);
                        list.add(parser.getTit(sb.toString()));
                    }else {
                        if (c.getURL().toString().trim().startsWith("http://animeflv")){
                            writeToFile(sb.toString(),file);
                            list.add(parser.getTit(sb.toString()));
                        }
                    }
                } catch (Exception e) {
                    Log.e("log_tag", "Error in http connection " + e.toString());
                    File file1 = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + i + ".txt");
                    String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + i + ".txt";
                    if (file1.exists()) {
                        list.add(parser.getTit(getStringFromFile(file_loc)));
                    }
                    //list.add("");
                }
            }else {
                String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + i + ".txt";
                if (file.exists()) {
                    list.add(parser.getTit(getStringFromFile(file_loc)));
                }
            }
        }
        String[] favoritos=new String[list.size()];
        list.toArray(favoritos);
        StringBuilder builder = new StringBuilder();
        for(String i : favoritos) {
            builder.append(":::" + i);
        }
        _response=builder.toString();
        return _response;
    }
    public  void writeToFile(String body,File file){
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(body.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getStringFromFile (String filePath) {
        String ret="";
        try {
            File fl = new File(filePath);
            FileInputStream fin = new FileInputStream(fl);
            ret = convertStreamToString(fin);
            fin.close();
        }catch (IOException e){}catch (Exception e){}
        return ret;
    }
    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        call.favCall(s, taskType);
    }
}
