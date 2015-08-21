package knf.animeflv.info;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import knf.animeflv.Parser;
import knf.animeflv.Requests;
import knf.animeflv.TaskType;

/**
 * Created by Jordy on 20/08/2015.
 */
public class RelActInfo extends AppCompatActivity implements Requests.callback {
    String aidInfo;
    String ext_storage_state = Environment.getExternalStorageState();
    File mediaStorage = new File(Environment.getExternalStorageDirectory() + "/.Animeflv/cache");

    Parser parser=new Parser();
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getApplicationContext();
        Bundle bundle=getIntent().getExtras();
        setInfo(bundle.getString("aid",""));
    }
    public void setInfo(String aid){
        aidInfo=aid;
        SharedPreferences sharedPreferences=getSharedPreferences("data",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("aid",aidInfo);
        editor.commit();
        new Requests(this, TaskType.GET_INFO).execute("http://animeflv.net/api.php?accion=anime&aid=" + aid);
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
        }
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && net;
    }
    public void actCacheInfo(String json){
        Bundle bundleInfo=new Bundle();
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!mediaStorage.exists()) {
                mediaStorage.mkdirs();
            }
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/.Animeflv/cache/"+aidInfo+".txt");
        String file_loc = Environment.getExternalStorageDirectory() + "/.Animeflv/cache/"+aidInfo+".txt";
        if (isNetworkAvailable()) {
            if (!file.exists()) {
                Log.d("Archivo:", "No existe");
                try {file.createNewFile();} catch (IOException e) {Log.d("Archivo:", "Error al crear archivo");}
                writeToFile(json, file);
                bundleInfo.putString("aid",parser.getAID(json));
                Intent intent=new Intent(this,Info.class);
                intent.putExtras(bundleInfo);
                finish();
                startActivity(intent);
            } else {
                Log.d("Archivo", "Existe");
                String infile = getStringFromFile(file_loc);
                if (json.trim().equals(infile.trim())) {
                    bundleInfo.putString("aid",parser.getAID(json));
                    Intent intent = new Intent(this, Info.class);
                    intent.putExtras(bundleInfo);
                    finish();
                    startActivity(intent);
                }else {
                    writeToFile(json,file);
                    bundleInfo.putString("aid", parser.getAID(json));
                    Intent intent = new Intent(this, Info.class);
                    intent.putExtras(bundleInfo);
                    finish();
                    startActivity(intent);
                }
            }
        } else {
            if (file.exists()) {
                bundleInfo.putString("aid",parser.getAID(json));
                Intent intent = new Intent(this, Info.class);
                intent.putExtras(bundleInfo);
                finish();
                startActivity(intent);
            } else {
                toast("No hay datos guardados");
                finish();
            }
        }
    }
    public void toast(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }
    @Override
    public void sendtext1(String data,TaskType taskType){
        if (taskType==TaskType.GET_INFO){
            actCacheInfo(data);
        }
    }
}
