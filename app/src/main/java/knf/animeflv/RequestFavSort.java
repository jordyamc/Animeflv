package knf.animeflv;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import knf.animeflv.Directorio.AnimeClass;

/**
 * Created by Jordy on 22/08/2015.
 */
public class RequestFavSort extends AsyncTask<String, List<AnimeClass>, List<AnimeClass>> {
    InputStream is;
    List<AnimeClass> _response;
    callback call;
    TaskType taskType;
    Parser parser = new Parser();
    StringBuilder builder = new StringBuilder();
    HttpURLConnection c = null;
    URL u;
    Context context;
    Boolean running;
    int prog = 0;

    public interface callback {
        void favCallSort(List<AnimeClass> list, TaskType taskType);
    }

    public RequestFavSort(Context con, TaskType taskType) {
        call = (callback) con;
        this.context = con;
        this.taskType = taskType;
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

    @Override
    protected List<AnimeClass> doInBackground(String... params) {
        String file_ = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
        String dir = getStringFromFile(file_);
        List<AnimeClass> list = new ArrayList<AnimeClass>();
        for (String i : params) {
            File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + i + ".txt");
            if (!file.exists() || !isJSONValid(getStringFromFile(file.getPath()))) {
                try {
                    //Log.d("aid", i);
                    u = new URL(new Parser().getInicioUrl(TaskType.NORMAL, context) + "?url=" + parser.getUrlFavs(dir, i) + "&certificate=" + getCertificateSHA1Fingerprint());
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
                    if (c.getURL() == u) {
                        writeToFile(sb.toString(), file);
                        list.add(new AnimeClass(sb.toString()));
                    } else {
                        if (c.getURL().toString().trim().startsWith("http://animeflv")) {
                            writeToFile(sb.toString(), file);
                            list.add(new AnimeClass(sb.toString()));
                        }
                    }
                } catch (Exception e) {
                    Log.e("log_tag", "Error in http connection " + e.toString());
                    File file1 = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + i + ".txt");
                    String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + i + ".txt";
                    if (file1.exists()) {
                        list.add(new AnimeClass(getStringFromFile(file_loc)));
                    }
                    //list.add("");
                }
            } else {
                String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + i + ".txt";
                if (file.exists()) {
                    list.add(new AnimeClass(getStringFromFile(file_loc)));
                }
            }
        }
        return list;
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

    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
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
    protected void onPostExecute(List<AnimeClass> s) {
        super.onPostExecute(s);
        call.favCallSort(s, taskType);
    }
}
