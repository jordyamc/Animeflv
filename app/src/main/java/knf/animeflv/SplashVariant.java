package knf.animeflv;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
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
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;
import knf.animeflv.Cloudflare.Bypass;
import knf.animeflv.Cloudflare.BypassHolder;
import knf.animeflv.CustomSettingsIntro.CustomIntro;
import knf.animeflv.Jobs.CheckJob;
import knf.animeflv.TV.TVMain;
import knf.animeflv.Utils.NetworkUtils;
import knf.animeflv.Utils.OnlineDataHelper;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

public class SplashVariant extends AwesomeSplash {
    final Handler handler = new Handler();
    Context context;
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Toaster.toast("Error al activar Bypass");
            proceed();
        }
    };

    public static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.setSplashTheme(this, getSplashColor());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initSplash(ConfigSplash configSplash) {
        context = this;
        new Alarm().SetAlarm(this);
        CheckJob.shedule(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, getSplashColor()));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, getSplashColor()));
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            sendBroadcast(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
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

    }

    @ColorRes
    public int getSplashColor() {
        if (getSharedPreferences("data", MODE_PRIVATE).getBoolean("isDown", false))
            return R.color.negro;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String fecha = dateFormat.format(calendar.getTime());
        String trim = fecha.substring(0, fecha.lastIndexOf("-"));
        switch (trim) {
            case "24-12":
                return R.color.navidad;
            case "31-12":
                return R.color.anuevo;
            case "01-01":
                return R.color.anuevo;
            case "14-02":
                return R.color.amor;
            default:
                if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("is_amoled", false)) {
                    return R.color.prim;
                } else {
                    return R.color.nmain;
                }
        }
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
        if (NetworkUtils.isNetworkAvailable()) {
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            asyncHttpClient.setTimeout(2000);
            asyncHttpClient.setResponseTimeout(2000);
            asyncHttpClient.setConnectTimeout(2000);
            asyncHttpClient.get("https://raw.githubusercontent.com/jordyamc/Animeflv/master/app/online_data.json", null, new JsonHttpResponseHandler() {

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
        } else {
            checkPermission();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (!isXLargeScreen(getApplicationContext())) {
            return;
        }
    }

    public void actlinks(String s) {
        if (!s.equals("error")) {
            try {
                JSONObject json = new JSONObject(s.trim());
                OnlineDataHelper.update(this, json);
                JSONObject jsonObject = json.getJSONObject("links");
                SharedPreferences.Editor preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
                preferences.putString("dir_normal", jsonObject.getString("normal")).apply();
                preferences.putString("dir_base", jsonObject.getString("base")).apply();
                preferences.putString("dir_base_back", jsonObject.getString("base_back")).apply();
                preferences.putString("dir_inicio", jsonObject.getString("inicio")).apply();
                preferences.putString("dir_inicio_back", jsonObject.getString("inicio_back")).apply();
                preferences.putString("dir_directorio", jsonObject.getString("directorio")).apply();
                preferences.putString("dir_directorio_back", jsonObject.getString("directorio_back")).apply();
                checkPermission();
            } catch (Exception e) {
                e.printStackTrace();
                checkPermission();
            }
        } else {
            checkPermission();
        }
    }

    private void proceed() {
        if (getSharedPreferences("data", MODE_PRIVATE).getBoolean("intro", false)) {
            finish();
            if (ThemeUtils.isTV(this) && PreferenceManager.getDefaultSharedPreferences(this).getBoolean("tv_layout", false)) {
                startActivity(new Intent(this, TVMain.class));
            } else {
                startActivity(new Intent(this, newMain.class));
            }
        } else {
            finish();
            startActivity(new Intent(context, CustomIntro.class));
        }
    }

    private void checkCloudflare() {
        if (NetworkUtils.isNetworkAvailable()) {
            Bypass.runJsoupTest(new Bypass.onTestResult() {
                @Override
                public void onResult(boolean needBypass) {
                    if (needBypass) {
                        Toaster.toast("Activando bypass de Cloudflare");
                        handler.postDelayed(runnable, Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(SplashVariant.this).getString("bypass_time", "30000")));
                        Bypass.check(SplashVariant.this, new Bypass.onBypassCheck() {
                            @Override
                            public void onFinish() {
                                handler.removeCallbacks(runnable);
                                PicassoCache.recreate(getApplicationContext());
                                proceed();
                            }
                        });
                    } else {
                        BypassHolder.clear(SplashVariant.this);
                        proceed();
                    }
                }
            });
        } else {
            proceed();
        }
    }

    @TargetApi(23)
    public void checkPermission() {
        final String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Dexter.withActivity(this).withPermission(permission).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                    checkCloudflare();
                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                    if (!permissionDeniedResponse.isPermanentlyDenied()) {
                        String titulo = "Leer/Escribir archivos";
                        String desc = "Este permiso es necesario para descargar los animes, asi como para funcionar sin conexion";
                        new MaterialDialog.Builder(context)
                                .title(titulo)
                                .content(desc)
                                .positiveText("ACTIVAR")
                                .cancelable(false)
                                .autoDismiss(true)
                                .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        checkPermission();
                                    }
                                })
                                .build().show();
                    } else {
                        Toaster.toast("El permiso es necesario, por favor activalo");
                        finish();
                        Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        i.addCategory(Intent.CATEGORY_DEFAULT);
                        i.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(i);
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    String titulo = "Leer/Escribir archivos";
                    String desc = "Este permiso es necesario para descargar los animes, asi como para funcionar sin conexion";
                    try {
                        new MaterialDialog.Builder(context)
                                .title(titulo)
                                .content(desc)
                                .positiveText("ACTIVAR")
                                .cancelable(false)
                                .autoDismiss(true)
                                .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        finish();
                                        Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        i.addCategory(Intent.CATEGORY_DEFAULT);
                                        i.setData(Uri.parse("package:" + getPackageName()));
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivityForResult(i, 5548);
                                    }
                                })
                                .build().show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).check();
        } else {
            checkCloudflare();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Branch branch = Branch.getInstance();

        branch.initSession(new Branch.BranchUniversalReferralInitListener() {
            @Override
            public void onInitFinished(BranchUniversalObject branchUniversalObject, LinkProperties linkProperties, BranchError error) {
                if (error != null) {
                    Log.i("MyApp", error.getMessage());
                }
            }
        }, this.getIntent().getData(), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }
}