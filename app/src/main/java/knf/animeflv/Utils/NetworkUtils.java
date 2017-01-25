package knf.animeflv.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionButton;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;

import cz.msebera.android.httpclient.Header;
import knf.animeflv.ColorsRes;
import knf.animeflv.R;
import knf.animeflv.Utils.eNums.UpdateState;
import knf.animeflv.newMain;
import xdroid.toaster.Toaster;

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

    public static boolean isNetworkAvailable() {
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

    public static void checkVersion(final Context Tcontext, final FloatingActionButton button) {
        Log.d("CheckVersion", "Start");
        if (UpdateUtil.getState() == UpdateState.NO_UPDATE) {
            UpdateUtil.setState(UpdateState.START_UPDATE_CHECK);
            new checkAct(Tcontext, button).executeOnExecutor(ExecutorManager.getExecutor());
        }
    }

    private static class checkAct extends AsyncTask<String, String, String> {
        Context Tcontext;
        FloatingActionButton button;

        public checkAct(Context tcontext, FloatingActionButton button) {
            Tcontext = tcontext;
            this.button = button;
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
                    Log.d("Version", Integer.toString(versionCode) + " >> " + vers.trim());
                    if (versionCode >= Integer.parseInt(vers.trim())) {
                        if (Integer.parseInt(vers.trim()) == 0) {
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
                                                } else
                                                if (mensaje[4].trim().toLowerCase().equals("cerrar")) {
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
                        } else {
                            UpdateUtil.setState(UpdateState.NO_UPDATE);
                            Log.d("Version", "OK");
                            Tcontext.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("notVer", false).apply();
                        }
                    } else {
                        Log.d("Version", "Actualizar");
                        dialog = new MaterialDialog.Builder(Tcontext)
                                .title("Nueva Version " + vers.trim())
                                .content("Esta version (" + versionCode + ") es obsoleta, porfavor actualiza para continuar.")
                                .autoDismiss(false)
                                .cancelable(false)
                                .titleGravity(GravityEnum.CENTER)
                                .positiveText("Actualizar")
                                .negativeText("Salir")
                                .backgroundColor(ThemeUtils.isAmoled(Tcontext) ? ColorsRes.Prim(Tcontext) : ColorsRes.Blanco(Tcontext))
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(final MaterialDialog dialog) {
                                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                                        if (descarga.exists()) {
                                            descarga.delete();
                                        }
                                        UpdateUtil.setState(UpdateState.DOWNLOADING);
                                        ((newMain) Tcontext).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                button.setShowProgressBackground(true);
                                                button.show(true);
                                            }
                                        });
                                        final ThinDownloadManager downloadManager = new ThinDownloadManager();
                                        Uri download = Uri.parse(Keys.Url.UPDATE);
                                        final DownloadRequest downloadRequest = new DownloadRequest(download)
                                                .setDestinationURI(Uri.fromFile(descarga))
                                                .setStatusListener(new DownloadStatusListenerV1() {
                                                    @Override
                                                    public void onDownloadComplete(DownloadRequest downloadRequest) {
                                                        Toaster.toast("Presiona para instalar");
                                                        UpdateUtil.setState(UpdateState.WAITING_TO_UPDATE);
                                                        ((newMain) Tcontext).runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                button.setImageResource(R.drawable.ic_done);
                                                                button.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        startInstall();
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                                                        Toaster.toast(errorMessage);
                                                        dialog.dismiss();
                                                        System.exit(0);
                                                    }

                                                    @Override
                                                    public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, final int progress) {
                                                        ((newMain) Tcontext).runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                button.setProgress(progress, true);
                                                            }
                                                        });
                                                    }
                                                });
                                        downloadManager.add(downloadRequest);
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        dialog.dismiss();
                                        System.exit(0);
                                    }
                                }).build();
                        dialog.show();
                    }
                }
            });
            Looper.loop();
            return null;
        }

        private void startInstall() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri uri = FileProvider.getUriForFile(context, "knf.animeflv.RequestsBackground", descarga);
                Tcontext.grantUriPermission("com.android.packageinstaller", uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                        .setDataAndType(uri,
                                "application/vnd.android.package-archive");
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
