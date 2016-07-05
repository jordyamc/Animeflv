package knf.animeflv;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;

import com.daimajia.androidanimations.library.Techniques;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Jordy on 05/12/2015.
 */
public class Splash extends AwesomeSplash {
    Context context;

    public static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    public void initSplash(ConfigSplash configSplash) {
        context = this;
        new Alarm().SetAlarm(this);
        //EmisionChecker.Refresh();
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
            startActivity(new Intent(context, newMain.class));
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
                if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("is_amoled", false)) {
                    splash = R.color.prim;
                } else {
                    splash = R.color.nmain;
                }
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
                if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("is_amoled", false)) {
                    splash = R.drawable.dark_umaru;
                } else {
                    splash = R.drawable.splash;
                }
                break;
        }
        if (getSharedPreferences("data", MODE_PRIVATE).getBoolean("isDown", false))
            splash = R.drawable.no_ahora_porfavor;
        return splash;
    }

    public String getSplashText() {
        String text;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
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
        //new back(context, TaskType.ACT_LIKNS).execute("https://raw.githubusercontent.com/jordyamc/Animeflv/master/app/links.html");
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setConnectTimeout(2000);
        asyncHttpClient.get("https://raw.githubusercontent.com/jordyamc/Animeflv/master/app/links.html", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                actlinks(response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                actlinks(response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                actlinks(responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                actlinks("error");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                actlinks("error");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                actlinks("error");
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (!isXLargeScreen(getApplicationContext())) {
            return;
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

    public void actlinks(String s) {
        if (!s.equals("error")) {
            try {
                JSONObject jsonObject = new JSONObject(s.trim());
                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("dir_base", jsonObject.getString("base")).apply();
                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("dir_base_back", jsonObject.getString("base_back")).apply();
                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("dir_inicio", jsonObject.getString("inicio")).apply();
                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("dir_inicio_back", jsonObject.getString("inicio_back")).apply();
                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("dir_directorio", jsonObject.getString("directorio")).apply();
                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("dir_directorio_back", jsonObject.getString("directorio_back")).apply();
                finish();
                startActivity(new Intent(context, newMain.class));
            } catch (Exception e) {
                e.printStackTrace();
                finish();
                startActivity(new Intent(context, newMain.class));
            }
        } else {
            finish();
            startActivity(new Intent(context, newMain.class));
        }
    }
}