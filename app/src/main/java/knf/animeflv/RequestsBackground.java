package knf.animeflv;

import android.app.DownloadManager;
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
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Jordy on 11/08/2015.
 */
public class RequestsBackground extends AsyncTask<String,String,String> {
    InputStream is;
    String _response="";
    String ext_storage_state;
    File mediaStorage;
    Context context;
    TaskType taskType;
    public RequestsBackground(Context cont,TaskType task){
        context=cont;
        this.taskType=task;
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
                    c.setConnectTimeout(15000);
                    c.connect();
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = "";
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    Log.d("Back URL Normal", u.toString());
                    if (c.getURL()!=u){
                        if (!c.getURL().toString().trim().startsWith("http://animeflv")) {
                            Log.d("Back URL ERROR", c.getURL().toString());
                            _response = "";
                        }else {
                            Log.d("Back URL OK",c.getURL().toString());
                            _response = sb.toString();
                        }
                    }else {
                        Log.d("Back URL OK",c.getURL().toString());
                        _response = sb.toString();
                    }
                    is = c.getInputStream();
                } catch (Exception e) {
                    Log.e("log_tag", "Error in http connection " + e.toString());
                    _response="";
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
        int Tcon=Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_conexion", "0"));
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
                break;
        }
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && net;
    }
    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (taskType==TaskType.NOT){
        ext_storage_state = Environment.getExternalStorageState();
        mediaStorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache");
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!mediaStorage.exists()) {
                mediaStorage.mkdirs();
            }
        }
        File file=new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/inicio.txt");
        String file_loc=Environment.getExternalStorageDirectory()+ "/Animeflv/cache/inicio.txt";
        if (isNetworkAvailable()&&!s.trim().equals("")) {
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
                String[] jsonAIDS = new Parser().parseAID(s);
                String[] jsonNums = new Parser().parsenumeros(s);
                String[] jsonTits = new Parser().parseTitulos(s);
                String[] jsonDesc = new Parser().parseEID(s);
                String[] jsonArchivo = new Parser().parseEID(txt);
                Boolean desc = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("autoDesc", false);
                if (isJSONValid(txt) && isJSONValid(s)){
                    if (!jsonDesc[0].trim().equals(jsonArchivo[0].trim())) {
                        writeToFile(s, file);
                        int not = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("sonido", "0"));
                        if (not == 0) {
                            Log.d("Notificacion:", "Crear Sonido Def");
                            String act = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("reload", "0");
                            Log.d("Registrer", act);
                            if (act.trim().equals("0")) {
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("reload", "1").apply();
                                Log.d("Registrer to", "1");
                            } else {
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("reload", "0").apply();
                                Log.d("Registrer to", "0");
                            }
                            int num = 0;
                            loop:
                            {
                                for (String st : jsonDesc) {
                                    if (!st.trim().equals(jsonArchivo[0].trim())) {
                                        List<String> indexs = Arrays.asList(jsonDesc);
                                        int index = indexs.indexOf(st);
                                        String favoritos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", "");
                                        Boolean comp = favoritos.startsWith(jsonAIDS[index] + ":::") || favoritos.contains(":::" + jsonAIDS[index] + ":::");
                                        if (comp && desc) {
                                            Descargar(jsonAIDS[index], jsonNums[index], jsonTits[index], st);
                                        }
                                        num += 1;
                                    } else {
                                        break loop;
                                    }
                                }
                            }
                            int nCaps = context.getSharedPreferences("data", Context.MODE_PRIVATE).getInt("nCaps", 0) + num;
                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt("nCaps", nCaps).apply();
                            String mess = "";
                            if (nCaps == 1) {
                                mess = "Hay " + Integer.toString(nCaps) + " nuevo capitulo disponible!!!";
                            } else {
                                mess = "Hay " + Integer.toString(nCaps) + " nuevos capitulos disponibles!!!";
                            }
                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(context)
                                            .setSmallIcon(R.drawable.ic_not_r)
                                            .setContentTitle("AnimeFLV")
                                            .setContentText(mess);
                            mBuilder.setVibrate(new long[]{100, 200, 100, 500});
                            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                            mBuilder.setAutoCancel(true);
                            mBuilder.setPriority(Notification.PRIORITY_MAX);
                            mBuilder.setLights(Color.BLUE, 5000, 2000);
                            Intent resultIntent = new Intent(context, Main.class);
                            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.setContentIntent(resultPendingIntent);
                            int mNotificationId = 6991;
                            NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            mNotifyMgr.cancel(mNotificationId);
                            mNotifyMgr.notify(mNotificationId, mBuilder.build());
                        }
                        if (not == 1) {
                            Log.d("Notificacion:", "Crear Sonido Especial");
                            String act = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("reload", "0");
                            Log.d("Registrer", act);
                            if (act.equals("0")) {
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("reload", "1").apply();
                                Log.d("Registrer to", "1");
                            } else {
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("reload", "0").apply();
                                Log.d("Registrer to", "0");
                            }
                            int num = 0;
                            loop:
                            {
                                for (String st : jsonDesc) {
                                    if (!st.trim().equals(jsonArchivo[0].trim())) {
                                        List<String> indexs = Arrays.asList(jsonDesc);
                                        int index = indexs.indexOf(st);
                                        String favoritos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", "");
                                        Boolean comp = favoritos.startsWith(jsonAIDS[index] + ":::") || favoritos.contains(":::" + jsonAIDS[index] + ":::");
                                        if (comp && desc) {
                                            Descargar(jsonAIDS[index], jsonNums[index], jsonTits[index], st);
                                        }
                                        num += 1;
                                    } else {
                                        break loop;
                                    }
                                }
                            }
                            int nCaps = context.getSharedPreferences("data", Context.MODE_PRIVATE).getInt("nCaps", 0) + num;
                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt("nCaps", nCaps).apply();
                            String mess = "";
                            if (nCaps == 1) {
                                mess = "Hay " + Integer.toString(nCaps) + " nuevo capitulo disponible!!!";
                            } else {
                                mess = "Hay " + Integer.toString(nCaps) + " nuevos capitulos disponibles!!!";
                            }
                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(context)
                                            .setSmallIcon(R.drawable.ic_not_r)
                                            .setContentTitle("AnimeFLV")
                                            .setContentText(mess);
                            mBuilder.setVibrate(new long[]{100, 200, 100, 500});
                            mBuilder.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/raw/sound"), AudioManager.STREAM_NOTIFICATION);
                            mBuilder.setAutoCancel(true);
                            mBuilder.setPriority(Notification.PRIORITY_MAX);
                            mBuilder.setLights(Color.BLUE, 5000, 2000);
                            Intent resultIntent = new Intent(context, Main.class);
                            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.setContentIntent(resultPendingIntent);
                            int mNotificationId = 6991;
                            NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            mNotifyMgr.cancel(mNotificationId);
                            mNotifyMgr.notify(mNotificationId, mBuilder.build());
                        }
                        if (not == 2) {
                            Log.d("Notificacion:", "Crear Sonido Onii-chan");
                            String act = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("reload", "0");
                            Log.d("Registrer", act);
                            if (act.equals("0")) {
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("reload", "1").apply();
                                Log.d("Registrer to", "1");
                            } else {
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("reload", "0").apply();
                                Log.d("Registrer to", "0");
                            }
                            int num = 0;
                            loop:
                            {
                                for (String st : jsonDesc) {
                                    if (!st.trim().equals(jsonArchivo[0].trim())) {
                                        List<String> indexs = Arrays.asList(jsonDesc);
                                        int index = indexs.indexOf(st);
                                        String favoritos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", "");
                                        Boolean comp = favoritos.startsWith(jsonAIDS[index] + ":::") || favoritos.contains(":::" + jsonAIDS[index] + ":::");
                                        if (comp && desc) {
                                            Descargar(jsonAIDS[index], jsonNums[index], jsonTits[index], st);
                                        }
                                        num += 1;
                                    } else {
                                        break loop;
                                    }
                                }
                            }
                            int nCaps = context.getSharedPreferences("data", Context.MODE_PRIVATE).getInt("nCaps", 0) + num;
                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt("nCaps", nCaps).apply();
                            String mess = "";
                            if (nCaps == 1) {
                                mess = "Hay " + Integer.toString(nCaps) + " nuevo capitulo disponible!!!";
                            } else {
                                mess = "Hay " + Integer.toString(nCaps) + " nuevos capitulos disponibles!!!";
                            }
                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(context)
                                            .setSmallIcon(R.drawable.ic_not_r)
                                            .setContentTitle("AnimeFLV")
                                            .setContentText(mess);
                            mBuilder.setVibrate(new long[]{100, 200, 100, 500});
                            mBuilder.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/raw/onii"), AudioManager.STREAM_NOTIFICATION);
                            mBuilder.setAutoCancel(true);
                            mBuilder.setPriority(Notification.PRIORITY_MAX);
                            mBuilder.setLights(Color.BLUE, 5000, 2000);
                            Intent resultIntent = new Intent(context, Main.class);
                            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.setContentIntent(resultPendingIntent);
                            int mNotificationId = 6991;
                            NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            mNotifyMgr.cancel(mNotificationId);
                            mNotifyMgr.notify(mNotificationId, mBuilder.build());
                        }
                        if (not == 3) {
                            Log.d("Notificacion:", "Crear Sonido Sam");
                            String act = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("reload", "0");
                            Log.d("Registrer", act);
                            if (act.equals("0")) {
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("reload", "1").apply();
                                Log.d("Registrer to", "1");
                            } else {
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("reload", "0").apply();
                                Log.d("Registrer to", "0");
                            }
                            int num = 0;
                            loop:
                            {
                                for (String st : jsonDesc) {
                                    if (!st.trim().equals(jsonArchivo[0].trim())) {
                                        List<String> indexs = Arrays.asList(jsonDesc);
                                        int index = indexs.indexOf(st);
                                        String favoritos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", "");
                                        Boolean comp = favoritos.startsWith(jsonAIDS[index] + ":::") || favoritos.contains(":::" + jsonAIDS[index] + ":::");
                                        if (comp && desc) {
                                            Descargar(jsonAIDS[index], jsonNums[index], jsonTits[index], st);
                                        }
                                        num += 1;
                                    } else {
                                        break loop;
                                    }
                                }
                            }
                            int nCaps = context.getSharedPreferences("data", Context.MODE_PRIVATE).getInt("nCaps", 0) + num;
                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt("nCaps", nCaps).apply();
                            String mess = "";
                            if (nCaps == 1) {
                                mess = "Hay " + Integer.toString(nCaps) + " nuevo capitulo disponible!!!";
                            } else {
                                mess = "Hay " + Integer.toString(nCaps) + " nuevos capitulos disponibles!!!";
                            }
                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(context)
                                            .setSmallIcon(R.drawable.ic_not_r)
                                            .setContentTitle("AnimeFLV")
                                            .setContentText(mess);
                            mBuilder.setVibrate(new long[]{100, 200, 100, 500});
                            mBuilder.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/raw/sam"), AudioManager.STREAM_NOTIFICATION);
                            mBuilder.setAutoCancel(true);
                            mBuilder.setPriority(Notification.PRIORITY_MAX);
                            mBuilder.setLights(Color.BLUE, 5000, 2000);
                            Intent resultIntent = new Intent(context, Main.class);
                            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.setContentIntent(resultPendingIntent);
                            int mNotificationId = 6991;
                            NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            mNotifyMgr.cancel(mNotificationId);
                            mNotifyMgr.notify(mNotificationId, mBuilder.build());
                        }
                        if (not == 4) {
                            Log.d("Notificacion:", "Crear Sonido Dango");
                            String act = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("reload", "0");
                            Log.d("Registrer", act);
                            if (act.equals("0")) {
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("reload", "1").apply();
                                Log.d("Registrer to", "1");
                            } else {
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("reload", "0").apply();
                                Log.d("Registrer to", "0");
                            }
                            int num = 0;
                            loop:
                            {
                                for (String st : jsonDesc) {
                                    if (!st.trim().equals(jsonArchivo[0].trim())) {
                                        List<String> indexs = Arrays.asList(jsonDesc);
                                        int index = indexs.indexOf(st);
                                        String favoritos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", "");
                                        Boolean comp = favoritos.startsWith(jsonAIDS[index] + ":::") || favoritos.contains(":::" + jsonAIDS[index] + ":::");
                                        if (comp && desc) {
                                            Descargar(jsonAIDS[index], jsonNums[index], jsonTits[index], st);
                                        }
                                        num += 1;
                                    } else {
                                        break loop;
                                    }
                                }
                            }
                            int nCaps = context.getSharedPreferences("data", Context.MODE_PRIVATE).getInt("nCaps", 0) + num;
                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt("nCaps", nCaps).apply();
                            String mess = "";
                            if (nCaps == 1) {
                                mess = "Hay " + Integer.toString(nCaps) + " nuevo capitulo disponible!!!";
                            } else {
                                mess = "Hay " + Integer.toString(nCaps) + " nuevos capitulos disponibles!!!";
                            }
                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(context)
                                            .setSmallIcon(R.drawable.ic_not_r)
                                            .setContentTitle("AnimeFLV")
                                            .setContentText(mess);
                            mBuilder.setVibrate(new long[]{100, 200, 100, 500});
                            mBuilder.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/raw/dango"), AudioManager.STREAM_NOTIFICATION);
                            mBuilder.setAutoCancel(true);
                            mBuilder.setPriority(Notification.PRIORITY_MAX);
                            mBuilder.setLights(Color.BLUE, 5000, 2000);
                            Intent resultIntent = new Intent(context, Main.class);
                            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.setContentIntent(resultPendingIntent);
                            int mNotificationId = 6991;
                            NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            mNotifyMgr.cancel(mNotificationId);
                            mNotifyMgr.notify(mNotificationId, mBuilder.build());
                        }
                    } else {
                        Log.d("JSON", "Es igual");
                    }
                }else {
                    Log.d("Error","Borrar archivo");
                    new File(file_loc).delete();
                }
            }
        }else {Log.d("Conexion","No hay internet");}}
        if (taskType==TaskType.VERSION){
            int versionCode=0;
            try {versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;} catch (Exception e) {Log.d("ERROR","Get Versioncode");}
            Log.d("Version", Integer.toString(versionCode)+ " >> "+s.trim());
            String data=s.trim();
            if (data.trim().equals("")){data=Integer.toString(versionCode);}
            if (versionCode>=Integer.parseInt(data.trim())){
                Log.d("Version", "OK");
            }else {
                SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
                Boolean isnot = sharedPreferences.getBoolean("notVer", false);
                if (isnot) {
                    Log.d("Version", "Not ya existe");
                } else {
                    sharedPreferences.edit().putBoolean("notVer", true).apply();
                    int not = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("sonido", "0"));
                    if (not == 0) {
                        Log.d("Notificacion:", "Crear Sonido 0");
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(context)
                                        .setSmallIcon(R.drawable.ic_not_r)
                                        .setContentTitle("AnimeFLV")
                                        .setContentText("Nueva Version Disponible!!!");
                        mBuilder.setVibrate(new long[]{100, 200, 100, 500});
                        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                        mBuilder.setAutoCancel(true);
                        mBuilder.setPriority(Notification.PRIORITY_MAX);
                        mBuilder.setLights(Color.BLUE, 5000, 2000);
                        Intent resultIntent = new Intent(context, Main.class);
                        Bundle bundle=new Bundle();
                        bundle.putString("act","1");
                        resultIntent.putExtras(bundle);
                        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(resultPendingIntent);
                        int mNotificationId = 1964;
                        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotifyMgr.notify(mNotificationId, mBuilder.build());
                    }
                    if (not==1){
                        Log.d("Notificacion:", "Crear Sonido Especial");
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(context)
                                        .setSmallIcon(R.drawable.ic_not_r)
                                        .setContentTitle("AnimeFLV")
                                        .setContentText("Nueva Version Disponible!!!");
                        mBuilder.setVibrate(new long[]{100, 200, 100, 500});
                        mBuilder.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/raw/sound"), AudioManager.STREAM_NOTIFICATION);
                        mBuilder.setAutoCancel(true);
                        mBuilder.setPriority(Notification.PRIORITY_MAX);
                        mBuilder.setLights(Color.BLUE, 5000, 2000);
                        Intent resultIntent = new Intent(context, Main.class);
                        Bundle bundle=new Bundle();
                        bundle.putString("act", "1");
                        resultIntent.putExtras(bundle);
                        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(resultPendingIntent);
                        int mNotificationId = 1964;
                        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotifyMgr.notify(mNotificationId, mBuilder.build());
                    }
                    if (not==2){
                        Log.d("Notificacion:", "Crear Sonido Onii-chan");
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(context)
                                        .setSmallIcon(R.drawable.ic_not_r)
                                        .setContentTitle("AnimeFLV")
                                        .setContentText("Nueva Version Disponible!!!");
                        mBuilder.setVibrate(new long[]{100, 200, 100, 500});
                        mBuilder.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/raw/onii"), AudioManager.STREAM_NOTIFICATION);
                        mBuilder.setAutoCancel(true);
                        mBuilder.setPriority(Notification.PRIORITY_MAX);
                        mBuilder.setLights(Color.BLUE, 5000, 2000);
                        Intent resultIntent = new Intent(context, Main.class);
                        Bundle bundle=new Bundle();
                        bundle.putString("act", "1");
                        resultIntent.putExtras(bundle);
                        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(resultPendingIntent);
                        int mNotificationId = 1964;
                        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotifyMgr.notify(mNotificationId, mBuilder.build());
                    }
                    if (not==3){
                        Log.d("Notificacion:", "Crear Sonido Sam");
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(context)
                                        .setSmallIcon(R.drawable.ic_not_r)
                                        .setContentTitle("AnimeFLV")
                                        .setContentText("Nueva Version Disponible!!!");
                        mBuilder.setVibrate(new long[]{100, 200, 100, 500});
                        mBuilder.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/raw/sam"), AudioManager.STREAM_NOTIFICATION);
                        mBuilder.setAutoCancel(true);
                        mBuilder.setPriority(Notification.PRIORITY_MAX);
                        mBuilder.setLights(Color.BLUE, 5000, 2000);
                        Intent resultIntent = new Intent(context, Main.class);
                        Bundle bundle=new Bundle();
                        bundle.putString("act", "1");
                        resultIntent.putExtras(bundle);
                        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(resultPendingIntent);
                        int mNotificationId = 1964;
                        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotifyMgr.notify(mNotificationId, mBuilder.build());
                    }
                    if (not == 4) {
                        Log.d("Notificacion:", "Crear Sonido Sam");
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(context)
                                        .setSmallIcon(R.drawable.ic_not_r)
                                        .setContentTitle("AnimeFLV")
                                        .setContentText("Nueva Version Disponible!!!");
                        mBuilder.setVibrate(new long[]{100, 200, 100, 500});
                        mBuilder.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/raw/dango"), AudioManager.STREAM_NOTIFICATION);
                        mBuilder.setAutoCancel(true);
                        mBuilder.setPriority(Notification.PRIORITY_MAX);
                        mBuilder.setLights(Color.BLUE, 5000, 2000);
                        Intent resultIntent = new Intent(context, Main.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("act", "1");
                        resultIntent.putExtras(bundle);
                        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(resultPendingIntent);
                        int mNotificationId = 1964;
                        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotifyMgr.notify(mNotificationId, mBuilder.build());
                    }
                }
            }
        }
    }

    public void Descargar(String aid, String num, String titulo, String eid) {
        int not = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("sonido", "0"));
        Uri ring = Uri.parse("");
        switch (not) {
            case 0:
                ring = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                break;
            case 1:
                ring = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/raw/sound");
                break;
            case 2:
                ring = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/raw/onii");
                break;
            case 3:
                ring = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/raw/sam");
                break;
            case 4:
                ring = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/raw/dango");
                break;
        }
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_not_r)
                        .setContentTitle(titulo)
                        .setContentText("Descargar capitulo " + num);
        mBuilder.setVibrate(new long[]{100, 200, 100, 500});
        mBuilder.setSound(ring, AudioManager.STREAM_NOTIFICATION);
        mBuilder.setAutoCancel(true);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setLights(Color.BLUE, 5000, 2000);
        Intent resultIntent = new Intent(context, BackDownload.class);
        Bundle bundle = new Bundle();
        bundle.putString("aid", aid);
        bundle.putString("num", num);
        bundle.putString("titulo", titulo);
        bundle.putString("eid", eid);
        resultIntent.putExtras(bundle);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        int mNotificationId = (int) Math.round(Math.random());
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
