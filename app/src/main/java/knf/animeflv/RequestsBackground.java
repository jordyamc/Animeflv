package knf.animeflv;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jordy on 11/08/2015.
 */
public class RequestsBackground extends AsyncTask<String,String,String> {
    InputStream is;
    String _response;
    String ext_storage_state;
    File mediaStorage;
    Context context;
    public RequestsBackground(Context cont){
        context=cont;
    }

        @Override
    protected String doInBackground(String... params) {
        StringBuilder builder = new StringBuilder();
        HttpURLConnection c = null;
        try {
            URL u = new URL(params[0]);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line="";
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            _response = sb.toString();
            is = c.getInputStream();
        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }
        return _response;
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

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        ext_storage_state = Environment.getExternalStorageState();
        mediaStorage = new File(Environment.getExternalStorageDirectory() + "/.Animeflv/cache");
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!mediaStorage.exists()) {
                mediaStorage.mkdirs();
            }
        }
        File file=new File(Environment.getExternalStorageDirectory() + "/.Animeflv/cache/inicio.txt");
        String file_loc=Environment.getExternalStorageDirectory()+ "/.Animeflv/cache/inicio.txt";
        if (!file.exists()){
            Log.d("Archivo:", "No existe");
            try {file.createNewFile();}catch (IOException e){Log.d("Archivo:", "Error al crear archivo");}
            writeToFile(s,file);
        }else {
            String txt=getStringFromFile(file_loc);
            String[] jsonDesc=new Parser().parseEID(s);
            String[] jsonArchivo=new Parser().parseEID(txt);
            if (!jsonDesc[0].trim().equals(jsonArchivo[0].trim())){
                Log.d("Notificacion:","Crear");
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_not_r)
                                .setContentTitle("AnimeFLV")
                                .setContentText("Nuevos capitulos disponibles!!!");
                mBuilder.setVibrate(new long[]{100, 200, 100, 500});
                Intent resultIntent = new Intent(context, Main.class);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(context,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
                int mNotificationId = 001;
                NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                mNotifyMgr.notify(mNotificationId, mBuilder.build());
            }else {
                Log.d("JSON","Es igual");
            }
        }
    }
}
