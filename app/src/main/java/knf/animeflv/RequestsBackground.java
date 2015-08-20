package knf.animeflv;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
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
    String _response="";
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
            if (isNetworkAvailable()) {
                try {
                    URL u = new URL(params[0]);
                    c = (HttpURLConnection) u.openConnection();
                    c.setRequestProperty("Content-length", "0");
                    c.setUseCaches(false);
                    c.setAllowUserInteraction(false);
                    c.connect();
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = "";
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    _response = sb.toString();
                    is = c.getInputStream();
                } catch (Exception e) {
                    Log.e("log_tag", "Error in http connection " + e.toString());
                }
            }else {Log.d("Conexion","No hay internet");}
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
    private boolean isNetworkAvailable() {
        Boolean net=false;
        int Tcon=Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_conexion","0"));
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        switch (Tcon){
            case 0:
                NetworkInfo Wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                net=Wifi.isConnected();
                break;
            case 1:
                NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                net=mobile.isConnected();
                break;
            case 2:
                NetworkInfo WifiA = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mobileA = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                net=WifiA.isConnected()||mobileA.isConnected();
        }
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && net;
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
        if (isNetworkAvailable()) {
            Log.d("Conexion","Hay internet");
            if (!file.exists()) {
                Log.d("Archivo:", "No existe");
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    Log.d("Archivo:", "Error al crear archivo");
                }
                writeToFile(s, file);
            } else {
                String txt = getStringFromFile(file_loc);
                String[] jsonDesc = new Parser().parseEID(s);
                String[] jsonArchivo = new Parser().parseEID(txt);
                if (!jsonDesc[0].trim().equals(jsonArchivo[0].trim())) {
                    writeToFile(s, file);
                    int not=Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("sonido","0"));
                    if (not==0) {
                        Log.d("Notificacion:", "Crear Sonido 0");
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(context)
                                        .setSmallIcon(R.drawable.ic_not_r)
                                        .setContentTitle("AnimeFLV")
                                        .setContentText("Nuevos capitulos disponibles!!!");
                        mBuilder.setVibrate(new long[]{100, 200, 100, 500});
                        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                        mBuilder.setAutoCancel(true);
                        mBuilder.setLights(Color.BLUE, 5000, 2000);
                        Intent resultIntent = new Intent(context, Main.class);
                        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(resultPendingIntent);
                        int mNotificationId = 6991;
                        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                        mNotifyMgr.notify(mNotificationId, mBuilder.build());
                    }else {
                        Log.d("Notificacion:", "Crear Sonido Especial");
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(context)
                                        .setSmallIcon(R.drawable.ic_not_r)
                                        .setContentTitle("AnimeFLV")
                                        .setContentText("Nuevos capitulos disponibles!!!");
                        mBuilder.setVibrate(new long[]{100, 200, 100, 500});
                        mBuilder.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/raw/sound"), AudioManager.STREAM_NOTIFICATION);
                        mBuilder.setAutoCancel(true);
                        mBuilder.setLights(Color.BLUE, 5000, 2000);
                        Intent resultIntent = new Intent(context, Main.class);
                        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(resultPendingIntent);
                        int mNotificationId = 6991;
                        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                        mNotifyMgr.notify(mNotificationId, mBuilder.build());
                    }
                } else {
                    Log.d("JSON", "Es igual");
                }
            }
        }else {Log.d("Conexion","No hay internet");}
    }
}
