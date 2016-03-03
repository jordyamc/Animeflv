package knf.animeflv;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.daimajia.androidanimations.library.Techniques;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 05/12/2015.
 */
public class Splash extends AwesomeSplash {
    Context context;

    @Override
    public void initSplash(ConfigSplash configSplash) {

            /* you don't have to override every property */
        context = this;
        if (!isXLargeScreen(getApplicationContext())) { //set phones to portrait;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        if (isNetworkAvailable()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                getWindow().setNavigationBarColor(getResources().getColor(getSplashColor()));
                getWindow().setStatusBarColor(getResources().getColor(getSplashColor()));
            }
            //Customize Circular Reveal
            configSplash.setBackgroundColor(getSplashColor()); //any color you want form colors.xml
            configSplash.setAnimCircularRevealDuration(500); //int ms
            configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);  //or Flags.REVEAL_LEFT
            configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM); //or Flags.REVEAL_TOP

            //Customize Logo
            configSplash.setLogoSplash(getSplashImage()); //or any other drawable
            configSplash.setAnimLogoSplashDuration(500); //int ms
            configSplash.setAnimLogoSplashTechnique(Techniques.Bounce); //choose one form Techniques (ref: https://github.com/daimajia/AndroidViewAnimations)


            //Customize Title
            configSplash.setTitleSplash(getSplashText());
            configSplash.setTitleTextColor(R.color.blanco);
            configSplash.setTitleTextSize(30f); //float value
            configSplash.setAnimTitleDuration(750);
            configSplash.setAnimTitleTechnique(Techniques.FlipInX);
        } else {
            finish();
            startActivity(new Intent(context, Main.class));
        }
        //getSplashImage();
    }

    public int getSplashColor() {
        int splash;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String fecha = dateFormat.format(calendar.getTime());
        String trim = fecha.substring(0, fecha.lastIndexOf("-"));
        switch (trim) {
            case "24-12":
                splash = R.color.navidad;
                break;
            case "31-12":
                splash = R.color.anuevo;
                break;
            case "01-01":
                splash = R.color.anuevo;
                break;
            case "14-02":
                splash = R.color.amor;
                break;
            default:
                splash = R.color.nmain;
                break;
        }
        if (getSharedPreferences("data", MODE_PRIVATE).getBoolean("isDown", false))
            splash = R.color.negro;
        return splash;
    }

    public int getSplashImage() {
        int splash;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String fecha = dateFormat.format(calendar.getTime());
        String trim = fecha.substring(0, fecha.lastIndexOf("-"));
        switch (trim) {
            case "24-12":
                splash = R.drawable.splash_navidad;
                break;
            case "31-12":
                splash = R.drawable.splash_new_year;
                break;
            case "01-01":
                splash = R.drawable.splash_new_year;
                break;
            case "14-02":
                splash = R.drawable.pepe_corazon;
                break;
            default:
                splash = R.drawable.splash;
                break;
        }
        if (getSharedPreferences("data", MODE_PRIVATE).getBoolean("isDown", false))
            splash = R.drawable.no_ahora_porfavor;
        return splash;
    }

    public String getSplashText() {
        String text;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String fecha = dateFormat.format(calendar.getTime());
        String trim = fecha.substring(0, fecha.lastIndexOf("-"));
        switch (trim) {
            case "24-12":
                text = "Feliz Navidad!!!";
                break;
            case "31-12":
                text = "Feliz Año Nuevo!!!";
                break;
            case "01-01":
                text = "Feliz Año Nuevo!!!";
                break;
            case "14-02":
                text = "( ͡° ͜ʖ ͡°)";
                break;
            default:
                text = "AnimeFLV App";
                break;
        }
        if (getSharedPreferences("data", MODE_PRIVATE).getBoolean("isDown", false))
            text = "Server Fallando";
        return text;
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

    @Override
    public void animationsFinished() {
        new back(context, TaskType.ACT_LIKNS).execute("https://raw.githubusercontent.com/jordyamc/Animeflv/master/app/links.html");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (!isXLargeScreen(getApplicationContext())) {
            return;
        }
    }

    public static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private boolean isNetworkAvailable() {
        Boolean net = false;
        int Tcon = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_conexion", "0"));
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
    public class back extends AsyncTask<String, String, String> {
        Context context;
        TaskType taskType;
        String _response = "";

        public back(Context c, TaskType t) {
            this.context = c;
            this.taskType = t;
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder builder = new StringBuilder();
            HttpURLConnection c = null;
            String link1 = "";
            String link2 = "";
            if (taskType == TaskType.ACT_LIKNS) {
                try {
                    String cookies = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("cookies", "");
                    URL u = new URL(params[0]);
                    c = (HttpURLConnection) u.openConnection();
                    c.setRequestProperty("Content-length", "0");
                    c.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
                    c.setRequestProperty("Accept", "*/*");
                    c.setRequestProperty("Cookie", cookies.trim().substring(0, cookies.indexOf(";") + 1));
                    c.setUseCaches(false);
                    c.setConnectTimeout(3000);
                    c.setAllowUserInteraction(false);
                    c.connect();
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = "";
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    if (!c.getURL().toString().contains("fav-server"))
                        if (c.getURL() != u) {
                            if (!c.getURL().toString().trim().startsWith("http://animeflv")) {
                                _response = "error";
                            } else {
                                if (!c.getURL().toString().contains("fav-server"))
                                    if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                        _response = sb.toString();
                                    } else {
                                        _response = "error";
                                    }
                            }
                        } else {
                            if (!c.getURL().toString().contains("fav-server"))
                                if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                    _response = sb.toString();
                                } else {
                                    _response = "error";
                                }
                        }
                } catch (Exception e) {
                    _response = "error";
                }
            } else {
                for (int i = 0; i < 2; i++) {
                    try {
                        String cookies = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("cookies", "");
                        URL u = new URL(params[i]);
                        c = (HttpURLConnection) u.openConnection();
                        c.setRequestProperty("Content-length", "0");
                        c.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
                        c.setRequestProperty("Accept", "*/*");
                        c.setRequestProperty("Cookie", cookies.trim().substring(0, cookies.indexOf(";") + 1));
                        c.setUseCaches(false);
                        c.setConnectTimeout(3000);
                        c.setAllowUserInteraction(false);
                        c.connect();
                        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line = "";
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        br.close();
                        if (c.getURL() != u) {
                            switch (i) {
                                case 0:
                                    link1 = "error";
                                    break;
                                case 1:
                                    link2 = "error";
                                    break;
                            }
                        } else {
                            if (isJSONValid(sb.toString()) && c.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                switch (i) {
                                    case 0:
                                        link1 = "ok";
                                        break;
                                    case 1:
                                        link2 = "ok";
                                        break;
                                }
                            } else {
                                switch (i) {
                                    case 0:
                                        link1 = "error";
                                        break;
                                    case 1:
                                        link2 = "error";
                                        break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        _response = "error";
                    }
                }
                if (!_response.equals("error"))
                    _response = link1 + "<-->" + link2;
            }
            return _response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (taskType == TaskType.ACT_LIKNS) {
                if (!s.equals("error")) {
                    Boolean isDebuging = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("debug", false);
                    try {
                        JSONObject jsonObject = new JSONObject(s.trim());
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("dir_base", jsonObject.getString("base")).apply();
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("dir_base_back", jsonObject.getString("base_back")).apply();
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("dir_inicio", jsonObject.getString("inicio")).apply();
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("dir_inicio_back", jsonObject.getString("inicio_back")).apply();
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("dir_directorio", jsonObject.getString("directorio")).apply();
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("dir_directorio_back", jsonObject.getString("directorio_back")).apply();
                        if (isDebuging) {
                            new back(context, TaskType.EVALUAR).execute(jsonObject.getString("inicio"), jsonObject.getString("inicio_back"));
                        } else {
                            finish();
                            startActivity(new Intent(context, Main.class));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        finish();
                        startActivity(new Intent(context, Main.class));
                    }
                } else {
                    finish();
                    startActivity(new Intent(context, Main.class));
                }
            }
            if (taskType == TaskType.EVALUAR) {
                if (s.contains("<-->")) {
                    Log.d("EVALUAR", s);
                    switch (s) {
                        case "ok<-->ok":
                            Toaster.toast("Todos los servidores funcionando");
                            break;
                        case "ok<-->error":
                            Toaster.toast("Servidor principal funcionando");
                            break;
                        case "error<-->ok":
                            Toaster.toast("Servidor de respaldo funcionando");
                            break;
                        case "error<-->error":
                            Toaster.toast("Sin servidores disponibles");
                            break;
                    }
                    finish();
                    startActivity(new Intent(context, Main.class));
                } else {
                    Log.d("EVALUAR", "ERROR");
                    finish();
                    startActivity(new Intent(context, Main.class));
                }
            }
        }
    }
}