package knf.animeflv;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import knf.animeflv.Utils.UtilNotBlocker;
import knf.animeflv.Utils.UtilSound;

/**
 * Created by Jordy on 11/08/2015.
 */
public class RequestsBackground extends AsyncTask<String, String, String> {
    InputStream is;
    String _response = "";
    String ext_storage_state;
    File mediaStorage;
    Context context;
    TaskType taskType;

    public RequestsBackground(Context cont, TaskType task) {
        context = cont;
        this.taskType = task;
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

    public static String getStringFromFile(String filePath) {
        String ret = "";
        try {
            File fl = new File(filePath);
            FileInputStream fin = new FileInputStream(fl);
            ret = convertStreamToString(fin);
            fin.close();
        } catch (IOException e) {
        } catch (Exception e) {
        }
        return ret;
    }

    public static String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1) h = "0" + h;
            if (l > 2) h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1)) str.append(':');
        }
        return str.toString();
    }

    private String getCertificateSHA1Fingerprint() {
        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();
        int flags = PackageManager.GET_SIGNATURES;
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(packageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Signature[] signatures = packageInfo.signatures;
        byte[] cert = signatures[0].toByteArray();
        InputStream input = new ByteArrayInputStream(cert);
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        X509Certificate c = null;
        try {
            c = (X509Certificate) cf.generateCertificate(input);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        String hexString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(c.getEncoded());
            hexString = byte2HexFormatted(publicKey);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return hexString;
    }

    @Override
    protected String doInBackground(String... params) {
        if (isNetworkAvailable()) {
            String url="";
            if (taskType==TaskType.NOT){
                url=new Parser().getInicioUrl(TaskType.NORMAL,context)+ "?certificate=" + getCertificateSHA1Fingerprint();
            }
            if (taskType==TaskType.VERSION){
                url="https://raw.githubusercontent.com/jordyamc/Animeflv/master/app/version.html";
            }
            SyncHttpClient client=new SyncHttpClient();
            client.setConnectTimeout(15000);
            client.setLoggingEnabled(false);
            client.get(url, null, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    makeDesicion("");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    makeDesicion(responseString);
                }
            });
        } else {
            Log.d("Conexion", "No hay internet");
        }
        return _response;
    }

    public void writeToFile(String body, File file) {
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
        Boolean net = false;
        int Tcon = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_conexion", "2"));
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        switch (Tcon) {
            case 0:
                NetworkInfo Wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                net = Wifi.isConnected();
                break;
            case 1:
                NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                net = mobile.isConnected();
                break;
            case 2:
                NetworkInfo WifiA = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mobileA = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                net = WifiA.isConnected() || mobileA.isConnected();
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

    private String getHour() {
        SimpleDateFormat sdf = new SimpleDateFormat("~hh:mmaa");
        Date d = new Date();
        return sdf.format(d);
    }

    private int getActualDayCode() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String day = sdf.format(d).toLowerCase();
        int code;
        switch (day.substring(0, 1)) {
            case "l":
                code = 1;
                break;
            case "m":
                if (day.substring(1, 2).equals("a")) {
                    code = 2;
                } else {
                    code = 3;
                }
                break;
            case "j":
                code = 4;
                break;
            case "v":
                code = 5;
                break;
            case "s":
                code = 6;
                break;
            case "d":
                code = 7;
                break;
            default:
                code = 0;
        }
        return code;
    }

    @Override
    protected void onPostExecute(final String s) {
        super.onPostExecute(s);

    }

    public void makeDesicion(String s){
        if (taskType == TaskType.NOT && new Parser().checkStatus(s.trim()) == 0) {
            ext_storage_state = Environment.getExternalStorageState();
            mediaStorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache");
            if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                if (!mediaStorage.exists()) {
                    mediaStorage.mkdirs();
                }
            }
            File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/inicio.txt");
            String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/inicio.txt";
            if (isNetworkAvailable() && !s.trim().equals("")) {
                Log.d("Conexion", "Hay internet");
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
                    if (isJSONValid(txt) && isJSONValid(s)) {
                        if (!jsonDesc[0].trim().equals(jsonArchivo[0].trim())) {
                            writeToFile(s, file);
                            String act = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("reload", "0");
                            if (act.trim().equals("0")) {
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("reload", "1").apply();
                            } else {
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("reload", "0").apply();
                            }
                            int num = 0;
                            Boolean isnot = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notificaciones", true);
                            Set<String> sts = context.getSharedPreferences("data", Context.MODE_PRIVATE).getStringSet("eidsNot", new HashSet<String>());
                            loop:
                            {
                                for (String st : jsonDesc) {
                                    if (!st.trim().equals(jsonArchivo[0].trim())) {
                                        List<String> indexs = Arrays.asList(jsonDesc);
                                        int index = indexs.indexOf(st);
                                        String favoritos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", "");
                                        Boolean comp = favoritos.startsWith(jsonAIDS[index] + ":::") || favoritos.contains(":::" + jsonAIDS[index] + ":::");
                                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt(jsonAIDS[index] + "onday", getActualDayCode()).apply();
                                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString(jsonAIDS[index] + "onhour", getHour()).apply();
                                        Log.d("Registrer", "Aid: " + jsonAIDS[index] + "   ----   Hour: " + getHour() + "   ----   Day: " + getActualDayCode());
                                        if (comp && desc && isnot) {
                                            Descargar(jsonAIDS[index], jsonNums[index], jsonTits[index], st);
                                        }
                                        num += 1;
                                        sts.add(st);
                                    } else {
                                        break loop;
                                    }
                                }
                            }
                            if (isnot && !UtilNotBlocker.isBlocked()) {
                                int nCaps = context.getSharedPreferences("data", Context.MODE_PRIVATE).getInt("nCaps", 0) + num;
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt("nCaps", nCaps).apply();
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putStringSet("eidsNot", sts).apply();
                                String mess = "";
                                List<String> eids = Arrays.asList(jsonDesc);
                                String[] tits = new Parser().parseTitulos(s);
                                String NotTit;
                                if (nCaps == 1) {
                                    mess = tits[0] + " " + eids.get(0).replace("E", "").split("_")[1];
                                    NotTit = "Nuevo capitulo disponible!";
                                } else {
                                    mess = "Hay " + Integer.toString(nCaps) + " nuevos capitulos disponibles!!!";
                                    NotTit = "AnimeFLV";
                                }
                                String temp = "";
                                List<String> tlist = new ArrayList<>();
                                tlist.addAll(sts);
                                for (String alone : tlist) {
                                    String[] data = alone.replace("E", "").split("_");
                                    //String tit = new Parser().getTitCached(data[0]);
                                    if (tlist.get(tlist.size() - 1).equals(alone)) {
                                        temp += tits[eids.indexOf(alone)] + " " + data[1];
                                    } else {
                                        temp += tits[eids.indexOf(alone)] + " " + data[1] + "\n";
                                    }
                                }
                                if (temp.endsWith("\n")) {
                                    temp = temp.substring(0, temp.length() - 2);
                                }
                                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                                bigTextStyle.setBigContentTitle("Animes:");
                                bigTextStyle.bigText(temp);
                                NotificationCompat.Builder mBuilder =
                                        new NotificationCompat.Builder(context)
                                                .setSmallIcon(R.drawable.ic_not_r)
                                                .setContentTitle(NotTit)
                                                .setContentText(mess);
                                mBuilder.setVibrate(new long[]{100, 200, 100, 500});
                                mBuilder.setStyle(bigTextStyle);
                                int not = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("sonido", "0"));
                                mBuilder.setSound(UtilSound.getSoundUri(not));
                                mBuilder.setAutoCancel(true);
                                mBuilder.setPriority(Notification.PRIORITY_MAX);
                                mBuilder.setLights(Color.argb(0, 255, 128, 0), 5000, 2000);
                                mBuilder.setGroup("animeflv_group");
                                Intent resultIntent = new Intent(context, newMain.class);
                                PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                mBuilder.setContentIntent(resultPendingIntent);
                                int mNotificationId = 6991;
                                NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                mNotifyMgr.cancel(mNotificationId);
                                mNotifyMgr.notify(mNotificationId, mBuilder.build());
                            } else {
                                if (UtilNotBlocker.isBlocked()) {
                                    Log.d("Not Service", "isBlocked");
                                    UtilNotBlocker.setBlocked(false);
                                }
                            }
                        } else {
                            Log.d("JSON", "Es igual");
                        }
                    } else {
                        Log.d("Error", "Borrar archivo");
                        new File(file_loc).delete();
                    }
                }
            } else {
                Log.d("Conexion", "No hay internet");
            }
        }
        if (taskType == TaskType.VERSION) {
            int versionCode = 0;
            try {
                versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            } catch (Exception e) {
                Log.d("ERROR", "Get Versioncode");
            }
            Log.d("Version", Integer.toString(versionCode) + " >> " + s.trim());
            String data = s.trim();
            if (data.trim().equals("")) {
                data = Integer.toString(versionCode);
            }
            if (versionCode >= Integer.parseInt(data.trim())) {
                Log.d("Version", "OK");
            } else {
                Boolean auto = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("autoUpdate", false);
                if (!auto) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
                    Boolean isnot = sharedPreferences.getBoolean("notVer", false);
                    if (isnot) {
                        Log.d("Version", "Not ya existe");
                    } else {
                        sharedPreferences.edit().putBoolean("notVer", true).apply();
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(context)
                                        .setSmallIcon(R.drawable.ic_not_r)
                                        .setContentTitle("AnimeFLV")
                                        .setContentText("Nueva Version Disponible!!!");
                        mBuilder.setVibrate(new long[]{100, 200, 100, 500});
                        int not = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("sonido", "0"));
                        mBuilder.setSound(UtilSound.getSoundUri(not));
                        mBuilder.setAutoCancel(true);
                        mBuilder.setPriority(Notification.PRIORITY_MAX);
                        mBuilder.setLights(Color.BLUE, 5000, 2000);
                        mBuilder.setGroup("animeflv_group");
                        Intent resultIntent = new Intent(context, newMain.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("act", "1");
                        resultIntent.putExtras(bundle);
                        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(resultPendingIntent);
                        int mNotificationId = 1964;
                        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotifyMgr.notify(mNotificationId, mBuilder.build());
                    }
                } else {
                    Log.d("Auto", "true");
                    final File descarga = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache", "Animeflv_Nver.apk");
                    int isDesc = context.getSharedPreferences("data", Context.MODE_PRIVATE).getInt("isDescDown", versionCode);
                    Boolean downloading = context.getSharedPreferences("data", Context.MODE_PRIVATE).getBoolean("isDescRun", false);
                    if (descarga.exists() && isDesc < Integer.parseInt(s.trim()) && !downloading) {
                        if (isDesc < Integer.parseInt(s.trim())) {
                            Log.d("Descarga", "Numero menor");
                            descarga.delete();
                            DescargarActualizacion(descarga, s.trim());
                        } else {
                            Log.d("Valores", String.valueOf(descarga.exists()) + " <--> " + String.valueOf(isDesc < versionCode) + " <--> " + String.valueOf(downloading));
                        }
                    } else {
                        if (isDesc < Integer.parseInt(s.trim())) {
                            if (!descarga.exists()) {
                                if (!downloading) {
                                    DescargarActualizacion(descarga, s.trim());
                                    Log.d("Descarga", "No en curso");
                                } else {
                                    int progreso = context.getSharedPreferences("data", Context.MODE_PRIVATE).getInt("ActProg", 0);
                                    Log.d("Descarga", "En Curso " + Integer.toString(progreso));
                                }
                            }
                        } else {
                            Log.d("Actualizacion", "ya descargada");
                        }
                    }
                }
            }
        }
    }

    public void DescargarActualizacion(final File descarga, final String s) {
        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("isDescRun", true).apply();
        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("notVer", true).apply();
        Uri download = Uri.parse("https://github.com/jordyamc/Animeflv/blob/master/app/app-release.apk?raw=true");
        final ThinDownloadManager downloadManager = new ThinDownloadManager();
        final DownloadRequest downloadRequest = new DownloadRequest(download)
                .setDestinationURI(Uri.fromFile(descarga))
                .setStatusListener(new DownloadStatusListenerV1() {
                    @Override
                    public void onDownloadComplete(DownloadRequest downloadRequest) {
                        Log.d("Actualizacion", "OK");
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("isDescRun", false).apply();
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt("isDescDown", Integer.parseInt(s.trim())).apply();

                        int not = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("sonido", "0"));
                        Uri ring = UtilSound.getSoundUri(not);
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(context)
                                        .setSmallIcon(R.drawable.ic_not_r)
                                        .setContentTitle("AnimeFLV")
                                        .setContentText("Instalar version " + s.trim());
                        mBuilder.setVibrate(new long[]{100, 200, 100, 500});
                        mBuilder.setSound(ring, AudioManager.STREAM_NOTIFICATION);
                        mBuilder.setAutoCancel(true);
                        mBuilder.setPriority(Notification.PRIORITY_MAX);
                        mBuilder.setLights(Color.BLUE, 5000, 2000);
                        mBuilder.setGroup("animeflv_group");
                        Intent resultIntent = new Intent(Intent.ACTION_VIEW)
                                .setDataAndType(Uri.fromFile(descarga),
                                        "application/vnd.android.package-archive");
                        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(resultPendingIntent);
                        int mNotificationId = (int) Math.round(Math.random());
                        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotifyMgr.notify(mNotificationId, mBuilder.build());
                    }

                    @Override
                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("notVer", false).apply();
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("isDescRun", false).apply();
                        Log.d("Actualizacion", "Fallada");
                    }

                    @Override
                    public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt("ActProg", progress).apply();
                    }
                });
        downloadManager.add(downloadRequest);
    }

    public void Descargar(String aid, String num, String titulo, String eid) {
        int not = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("sonido", "0"));
        Uri ring = UtilSound.getSoundUri(not);
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
        mBuilder.setGroup("animeflv_group");
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
