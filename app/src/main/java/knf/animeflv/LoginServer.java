package knf.animeflv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
            URL u = new URL(params[0]);
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
            Log.d("GET State",state);
            SharedPreferences defsharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            //defsharedPreferences.edit().putString("GET_Status", s.toLowerCase().trim()).apply();
            defsharedPreferences.edit().putString("GET_Status", state).apply();
            if (s.toLowerCase().trim().contains(":::")||s.toLowerCase().trim().equals("")) {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("login_email", email).apply();
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("login_email_coded", email_coded).apply();
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("login_pass_coded", pass_coded).apply();
                if (!s.toLowerCase().trim().contains(":;:")) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString("favoritos", s.trim()).apply();
                    defsharedPreferences.edit().putString("GET_Status", "exito").apply();
                }else {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
                    String[] separar=s.trim().split(":;:");
                    sharedPreferences.edit().putString("favoritos", separar[0]).apply();
                    sharedPreferences.edit().putString("vistos", separar[1]).apply();
                    defsharedPreferences.edit().putString("GET_Status", "exito").apply();
                }
                materialDialog.dismiss();
                Toast.makeText(context, "Sesion Iniciada!!", Toast.LENGTH_SHORT).show();
                call.response("OK", taskType);
            }
        }
        if (taskType==TaskType.GET_FAV_SL){
            Log.d("GET State",state);
            SharedPreferences defsharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            //defsharedPreferences.edit().putString("GET_Status", s.toLowerCase().trim()).apply();
            defsharedPreferences.edit().putString("GETSL_Status", state).apply();
            if (s.toLowerCase().trim().contains(":::")||s.toLowerCase().trim().equals("")) {
                if (!s.toLowerCase().trim().contains(":;:")) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString("favoritos", s.trim()).apply();
                    defsharedPreferences.edit().putString("GETSL_Status", "exito").apply();
                }else {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
                    String[] separar=s.trim().split(":;:");
                    sharedPreferences.edit().putString("favoritos", separar[0]).apply();
                    sharedPreferences.edit().putString("vistos", separar[1]).apply();
                    defsharedPreferences.edit().putString("GETSL_Status", "exito").apply();
                }
            }
        }
        if (taskType==TaskType.LIST_USERS){
            String format=s.replace("./user_favs/","").replace(".txt","");
            SharedPreferences defsharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            defsharedPreferences.edit().putString("lista", format).apply();
        }
        if (taskType==TaskType.cCorreo){
            Log.d("cCorreo State",state);
            SharedPreferences defsharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            //defsharedPreferences.edit().putString("GET_Status", s.toLowerCase().trim()).apply();
            defsharedPreferences.edit().putString("cCorreo_Status", state).apply();
            if (s.toLowerCase().trim().equals("exito")) {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("login_email", email).apply();
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("login_email_coded", email_coded).apply();
                materialDialog.dismiss();
                Toast.makeText(context, "Email Cambiado!!", Toast.LENGTH_SHORT).show();
            }
        }
        if (taskType==TaskType.cPass){
            Log.d("cPass State",state);
            SharedPreferences defsharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            //defsharedPreferences.edit().putString("GET_Status", s.toLowerCase().trim()).apply();
            defsharedPreferences.edit().putString("cPass_Status", state).apply();
            if (s.toLowerCase().trim().equals("exito")) {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("login_pass_coded", pass_coded).apply();
                materialDialog.dismiss();
                Toast.makeText(context, "Contraseña Cambiada!!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
