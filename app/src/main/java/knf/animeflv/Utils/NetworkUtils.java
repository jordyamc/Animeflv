package knf.animeflv.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.format.Formatter;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.File;

import cz.msebera.android.httpclient.Header;
import knf.animeflv.ColorsRes;
import knf.animeflv.Utils.eNums.UpdateState;
import knf.animeflv.newMain;
import xdroid.toaster.Toaster;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by Jordy on 28/03/2016.
 */
public class NetworkUtils {
    private static boolean disM = false;
    private static int versionCode = 0;
    private static File descarga = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache", "Animeflv_Nver.apk");
    private static MaterialDialog dialog;
    private static String[] mensaje;
    private static Context context;

    public static void initial(Context con) {
        context = con;
        try {
            versionCode = con.getPackageManager().getPackageInfo(con.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            Toaster.toast("ERROR");
        }
    }

    public static String getIPAddress(Context context) {
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }

    public static boolean isNetworkAvailable() {
        try {
            Boolean net = false;
            int Tcon = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_conexion", "2"));
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            switch (Tcon) {
                case 0:
                    NetworkInfo Wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    net = Wifi.isConnectedOrConnecting();
                    break;
                case 1:
                    NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                    net = mobile.isConnectedOrConnecting();
                    break;
                case 2:
                    NetworkInfo WifiA = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    NetworkInfo mobileA = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                    net = WifiA.isConnectedOrConnecting() || mobileA.isConnectedOrConnecting();
                    break;
                case 3:
                    return connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
            }
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && net;
        } catch (Exception e) {
            return false;
        }
    }

    public static void checkVersion(final Context Tcontext) {
        Log.d("CheckVersion", "Start");
        if (UpdateUtil.getState() == UpdateState.NO_UPDATE && isNetworkAvailable()) {
            UpdateUtil.setState(UpdateState.START_UPDATE_CHECK);
            new checkAct(Tcontext).executeOnExecutor(ExecutorManager.getExecutor());
        }
    }

    public static void checkVersion(final Context Tcontext, boolean notify) {
        Log.d("CheckVersion", "Start");
        if (UpdateUtil.getState() == UpdateState.NO_UPDATE && isNetworkAvailable()) {
            UpdateUtil.setState(UpdateState.START_UPDATE_CHECK);
            new checkAct(Tcontext, notify).executeOnExecutor(ExecutorManager.getExecutor());
        }
    }

    public static void testUpdate(Context context) {
        context.startService(new Intent(context, UpdateService.class));
    }

    public static class checkAct extends AsyncTask<String, String, String> {
        Context Tcontext;
        String update_ver;
        boolean notify = false;

        private checkAct(Context tcontext) {
            Tcontext = tcontext;
            descarga = Keys.Dirs.getUpdateFile();
        }

        public checkAct(Context tcontext, boolean notify) {
            Tcontext = tcontext;
            descarga = Keys.Dirs.getUpdateFile();
            this.notify = notify;

        }

        @Override
        protected String doInBackground(String... params) {
            Looper.prepare();
            new SyncHttpClient().get(Keys.Url.VERSION_INT, null, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    UpdateUtil.setState(UpdateState.NO_UPDATE);
                    Log.e("Error", "CheckVersion", throwable);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String data) {
                    String vers = "";
                    if (!isNetworkAvailable() || data.trim().equals("error")) {
                        vers = Integer.toString(versionCode);
                    } else {
                        if (FileUtil.isNumber(data.trim())) {
                            vers = data;
                        } else {
                            vers = "0";
                            mensaje = data.split(":::");
                        }
                    }
                    update_ver = vers;
                    Log.e("Version", Integer.toString(versionCode) + " >> " + vers.trim());
                    if (versionCode >= Integer.parseInt(vers.trim())) {
                        UpdateUtil.isBeta = versionCode > Integer.parseInt(vers.trim());
                        if (Integer.parseInt(vers.trim()) == 0) {
                            checkMessage();
                        } else {
                            UpdateUtil.setState(UpdateState.NO_UPDATE);
                            Log.d("Version", "OK");
                            Tcontext.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("notVer", false).apply();
                            if (notify)
                                Toaster.toast("La app esta actualizada!!");
                        }
                    } else {
                        Log.d("Version", "Actualizar");
                        dialog = new MaterialDialog.Builder(Tcontext)
                                .title("Nueva Version " + vers.trim())
                                .content("Esta version (" + versionCode + ") es obsoleta, desea actualizar?")
                                .autoDismiss(false)
                                .cancelable(false)
                                .titleGravity(GravityEnum.CENTER)
                                .positiveText("Actualizar")
                                .negativeText("Cerrar")
                                .backgroundColor(ThemeUtils.isAmoled(Tcontext) ? ColorsRes.Prim(Tcontext) : ColorsRes.Blanco(Tcontext))
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                                        TrackingHelper.track(context, TrackingHelper.UPDATING + versionCode + " --> " + update_ver);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            if (context.getPackageManager().canRequestPackageInstalls()) {
                                                Tcontext.startService(new Intent(Tcontext, UpdateService.class));
                                                dialog.dismiss();
                                            } else {
                                                context.startActivity(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + context.getPackageName())));
                                            }
                                        } else {
                                            Tcontext.startService(new Intent(Tcontext, UpdateService.class));
                                            dialog.dismiss();
                                        }
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                }).build();
                        dialog.show();
                    }
                }
            });
            Looper.loop();
            return null;
        }

        private void checkMessage() {
            if (!disM) {
                /** Formato: dividido por ":::"
                 * String - Titulo
                 * String - Mensaje
                 * Boolean - autodismiss
                 * Boolean - Cancelable
                 * String - action: Salir/Cerrar
                 * String - ExtraAction: toast/toast&notshow/finish/dismiss/dismiss&notshow/none
                 * (Opcional) String - ToastMessage
                 */
                MaterialDialog dialog = new MaterialDialog.Builder(Tcontext)
                        .title(mensaje[0])
                        .content(mensaje[1])
                        .autoDismiss(Boolean.valueOf(mensaje[2].trim()))
                        .cancelable(Boolean.valueOf(mensaje[3].trim()))
                        .titleGravity(GravityEnum.CENTER)
                        .positiveText(mensaje[4])
                        .backgroundColor(ThemeUtils.isAmoled(Tcontext) ? ColorsRes.Prim(Tcontext) : ColorsRes.Blanco(Tcontext))
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                if (mensaje[4].trim().toLowerCase().equals("salir")) {
                                    System.exit(0);
                                } else if (mensaje[4].trim().toLowerCase().equals("cerrar")) {
                                    disM = true;
                                    dialog.dismiss();
                                } else {
                                    dialog.dismiss();
                                }
                                if (!mensaje[4].trim().toLowerCase().equals("salir") || !mensaje[4].trim().toLowerCase().equals("cerrar")) {
                                    if (mensaje[5].trim().equals("toast")) {
                                        Toaster.toast(mensaje[6].trim());
                                    }
                                    if (mensaje[5].trim().equals("toast&notshow")) {
                                        Toaster.toast(mensaje[6].trim());
                                        disM = true;
                                    }
                                    if (mensaje[5].trim().equals("finish")) {
                                        System.exit(0);
                                    }
                                    if (mensaje[5].trim().equals("dismiss")) {
                                        dialog.dismiss();
                                    }
                                    if (mensaje[5].trim().equals("dismiss&notshow")) {
                                        disM = true;
                                        dialog.dismiss();
                                    }
                                }
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                System.exit(0);
                            }
                        }).build();
                dialog.show();
            }
        }

        private void startInstall() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri uri = FileProvider.getUriForFile(context, "knf.animeflv.RequestsBackground", descarga);
                //Tcontext.grantUriPermission("com.android.packageinstaller", uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                        .setDataAndType(uri,
                                "application/vnd.android.package-archive");
                promptInstall.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                promptInstall.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                dialog.dismiss();
                ((newMain) Tcontext).finish();
                Tcontext.startActivity(promptInstall);
            } else {
                Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                        .setDataAndType(Uri.fromFile(descarga),
                                "application/vnd.android.package-archive");
                dialog.dismiss();
                ((newMain) Tcontext).finish();
                Tcontext.startActivity(promptInstall);
            }
        }
    }
}
