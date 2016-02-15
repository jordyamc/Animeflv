package knf.animeflv;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
public class LoginServer extends AsyncTask<String,String,String> {
    InputStream is;
    String _response;
    TaskType taskType;
    Context context;
    String email;
    String email_coded;
    String pass_coded;
    MaterialDialog materialDialog;
    callback call;
    public LoginServer(Context c,TaskType taskType,@Nullable String mail,@Nullable String email_c,@Nullable String pass_c,@Nullable MaterialDialog dialog){
        this.context=c;
        this.taskType=taskType;
        call = (callback) c;
        if (mail!=null)this.email=mail;
        if (email_c!=null)this.email_coded=email_c;
        if (pass_c!=null)this.pass_coded=pass_c;
        if (dialog!=null)this.materialDialog=dialog;
    }

    public interface callback {
        void response(String data, TaskType taskType);
    }
    @Override
    protected String doInBackground(String... params) {
        StringBuilder builder = new StringBuilder();
        HttpURLConnection c = null;
        try {
            URL u;
            if (params[0].startsWith(new Parser().getBaseUrl(TaskType.NORMAL, context))) {
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
            c.setRequestProperty( "User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4" );
            c.setUseCaches(false);
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
                _response="error";
            }else {
                _response = sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            _response="error";
        }
        return _response;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        String state=s.toLowerCase().trim();
        if (taskType==TaskType.NEW_USER) {
            if (s.toLowerCase().trim().equals("exito")) {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("login_email", email).apply();
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("login_email_coded", email_coded).apply();
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("login_pass_coded", pass_coded).apply();
            }
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            sharedPreferences.edit().putString("nCuenta_Status", state).apply();
            call.response("OK", taskType);
        }
        if (taskType==TaskType.GET_FAV){
            SharedPreferences defsharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            //defsharedPreferences.edit().putString("GET_Status", s.toLowerCase().trim()).apply();
            defsharedPreferences.edit().putString("GET_Status", state).apply();
            if (isJSONValid(s.trim())) {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("login_email", email).apply();
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("login_email_coded", email_coded).apply();
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("login_pass_coded", pass_coded).apply();
                new Parser().saveBackup(context);
                String favs = new Parser().getUserFavs(s.trim());
                String vistos = new Parser().getUserVistos(s.trim());
                SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
                sharedPreferences.edit().putString("favoritos", favs).apply();
                sharedPreferences.edit().putString("vistos", vistos).apply();
                defsharedPreferences.edit().putString("GET_Status", "exito").apply();
                materialDialog.dismiss();
                Toast.makeText(context, "Sesion Iniciada!!", Toast.LENGTH_SHORT).show();
                call.response("OK", taskType);
            }
        }
        if (taskType==TaskType.GET_FAV_SL){
            SharedPreferences defsharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            //defsharedPreferences.edit().putString("GET_Status", s.toLowerCase().trim()).apply();
            defsharedPreferences.edit().putString("GETSL_Status", state).apply();
            if (isJSONValid(s.trim())) {
                String favs = new Parser().getUserFavs(s.trim());
                String vistos = new Parser().getUserVistos(s.trim());
                SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
                sharedPreferences.edit().putString("favoritos", favs).apply();
                sharedPreferences.edit().putString("vistos", vistos).apply();
                defsharedPreferences.edit().putString("GETSL_Status", "exito").apply();
            }
        }
        if (taskType==TaskType.LIST_USERS){
            String format = s.replace("../user_favs/", "").replace(".txt", "");
            SharedPreferences defsharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            defsharedPreferences.edit().putString("lista", format).apply();
        }
        if (taskType==TaskType.cCorreo){
            SharedPreferences defsharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            //defsharedPreferences.edit().putString("GET_Status", s.toLowerCase().trim()).apply();
            defsharedPreferences.edit().putString("cCorreo_Status", state).apply();
            if (s.toLowerCase().trim().equals("exito")) {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("login_email", email).apply();
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("login_email_coded", email_coded).apply();
                new Parser().saveBackup(context);
                materialDialog.dismiss();
                Toast.makeText(context, "Email Cambiado!!", Toast.LENGTH_SHORT).show();
            }
        }
        if (taskType==TaskType.cPass){
            SharedPreferences defsharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            //defsharedPreferences.edit().putString("GET_Status", s.toLowerCase().trim()).apply();
            defsharedPreferences.edit().putString("cPass_Status", state).apply();
            if (s.toLowerCase().trim().equals("exito")) {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("login_pass_coded", pass_coded).apply();
                new Parser().saveBackup(context);
                materialDialog.dismiss();
                Toast.makeText(context, "Contraseña Cambiada!!", Toast.LENGTH_SHORT).show();
            }
        }
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
}
