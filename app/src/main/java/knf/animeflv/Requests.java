package knf.animeflv;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
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

/**
 * Created by Jordy on 12/08/2015.
 */
public class Requests extends AsyncTask<String,String,String> {
    InputStream is;
    String _response;
    callback call;
    TaskType taskType;
    Context context;

    public interface callback{
        void sendtext1(String data, TaskType taskType);
    }
    public Requests(Context con, TaskType taskType){
        context = con;
        call=(callback) con;
        this.taskType=taskType;

    }
    @Override
    protected String doInBackground(String... params) {
        StringBuilder builder = new StringBuilder();
        HttpURLConnection c = null;
        try {
            String cookies = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("cookies", "");
            URL u;
            if (params[0].startsWith(new Parser().getBaseUrl(TaskType.NORMAL, context)) || params[0].contains("getHtml.php")) {
                if (params[0].endsWith(".php")) {
                    u = new URL(params[0] + "?certificate=" + getCertificateSHA1Fingerprint());
                } else {
                    u = new URL(params[0] + "&certificate=" + getCertificateSHA1Fingerprint());
                }
            } else {
                u = new URL(params[0]);
            }
            c = (HttpURLConnection) u.openConnection();
            c.setRequestProperty("Content-length", "0");
            c.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            c.setRequestProperty("Accept", "*/*");
            c.setRequestProperty("Cookie", cookies.trim().substring(0, cookies.indexOf(";") + 1));
            c.setUseCaches(false);
            c.setConnectTimeout(20000);
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
            if (!c.getURL().toString().contains("fav-server"))
                Log.d("Requests URL Normal", u.toString());
            if (c.getURL()!=u){
                if (!c.getURL().toString().trim().startsWith("http://animeflv")) {
                    Log.d("Requests URL ERROR", c.getURL().toString());
                    _response = "error";
                }else {
                    if (!c.getURL().toString().contains("fav-server"))
                        Log.d("Requests URL OK", c.getURL().toString());
                    if (c.getResponseCode()==HttpURLConnection.HTTP_OK) {
                        _response = sb.toString();
                    }else{
                        _response="error";
                    }
                }
            }else {
                if (!c.getURL().toString().contains("fav-server"))
                    Log.d("Requests URL OK", c.getURL().toString());
                if (c.getResponseCode()==HttpURLConnection.HTTP_OK) {
                    _response = sb.toString();
                }else{
                    _response="error";
                }
            }
            //String fullPage = page.asXml();
        } catch (Exception e) {
            Log.e("Requests", "Error in http connection " + e.toString());
            _response="error";
        }
        return _response;
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
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        call.sendtext1(s,taskType);
    }
}
