package knf.animeflv;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.dark));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.nmain));
            getWindow().setStatusBarColor(getResources().getColor(R.color.nmain));
        }
        //Customize Circular Reveal
        configSplash.setBackgroundColor(R.color.nmain); //any color you want form colors.xml
        configSplash.setAnimCircularRevealDuration(500); //int ms
        configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);  //or Flags.REVEAL_LEFT
        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM); //or Flags.REVEAL_TOP

        //Customize Logo
        configSplash.setLogoSplash(R.drawable.splash); //or any other drawable
        configSplash.setAnimLogoSplashDuration(500); //int ms
        configSplash.setAnimLogoSplashTechnique(Techniques.Bounce); //choose one form Techniques (ref: https://github.com/daimajia/AndroidViewAnimations)


        //Customize Title
        configSplash.setTitleSplash("AnimeFLV App");
        configSplash.setTitleTextColor(R.color.blanco);
        configSplash.setTitleTextSize(30f); //float value
        configSplash.setAnimTitleDuration(750);
        configSplash.setAnimTitleTechnique(Techniques.FlipInX);
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
                            if (isJSONValid(sb.toString())) {
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
                    try {
                        JSONObject jsonObject = new JSONObject(s.trim());
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("dir_base", jsonObject.getString("base")).apply();
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("dir_base_back", jsonObject.getString("base_back")).apply();
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("dir_inicio", jsonObject.getString("inicio")).apply();
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("dir_inicio_back", jsonObject.getString("inicio_back")).apply();
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("dir_directorio", jsonObject.getString("directorio")).apply();
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("dir_directorio_back", jsonObject.getString("directorio_back")).apply();
                        new back(context, TaskType.EVALUAR).execute(jsonObject.getString("inicio"), jsonObject.getString("inicio_back"));
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