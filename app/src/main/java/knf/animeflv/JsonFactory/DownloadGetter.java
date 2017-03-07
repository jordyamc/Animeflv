package knf.animeflv.JsonFactory;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import knf.animeflv.Cloudflare.Bypass;
import knf.animeflv.Cloudflare.BypassHolder;
import knf.animeflv.ColorsRes;
import knf.animeflv.DownloadManager.CookieConstructor;
import knf.animeflv.DownloadManager.ManageDownload;
import knf.animeflv.JsonFactory.JsonTypes.DOWNLOAD;
import knf.animeflv.Parser;
import knf.animeflv.PlayBack.CastPlayBackManager;
import knf.animeflv.R;
import knf.animeflv.StreamManager.StreamManager;
import knf.animeflv.Utils.MainStates;
import knf.animeflv.Utils.NetworkUtils;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

public class DownloadGetter {
    private static Spinner sp;

    public static void search(final Activity context, final String eid, final ActionsInterface actionsInterface) {
        if (eid.contains("_") && eid.endsWith("E")) {
            BypassHolder.savedToLocal(context);
            BaseGetter.getJson(context, new DOWNLOAD(eid), new BaseGetter.AsyncInterface() {
                @Override
                public void onFinish(String json) {
                    try {
                        Looper.prepare();
                    } catch (Exception e) {
                    }
                    try {
                        JSONArray jsonArray = new JSONObject(json).getJSONArray("downloads");
                        final List<String> nombres = new ArrayList<>();
                        final List<String> urls = new ArrayList<>();
                        final List<String> datas = new ArrayList<>();
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String u = object.getString("url");
                                if (!u.trim().equals("null")) {
                                    nombres.add(object.getString("name"));
                                    urls.add(u);
                                    try {
                                        datas.add(object.getString("data"));
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (nombres.size() != 0) {
                            final MaterialDialog.Builder d = new MaterialDialog.Builder(context)
                                    .title(actionsInterface.isStream() ? "Streaming" : "Descarga")
                                    .titleGravity(GravityEnum.CENTER)
                                    .customView(R.layout.dialog_down, false)
                                    .cancelable(true)
                                    .autoDismiss(false)
                                    .positiveText(actionsInterface.isStream() ? "Reproducir" : "Descargar")
                                    .negativeText("Cancelar")
                                    .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            String des = nombres.get(sp.getSelectedItemPosition());
                                            final String ur = urls.get(sp.getSelectedItemPosition());
                                            switch (des.toLowerCase()) {
                                                case "zippyshare":
                                                    actionsInterface.onStartZippy(ur);
                                                    dialog.dismiss();
                                                    break;
                                                case "zippyshare fast":
                                                    dialog.dismiss();
                                                    startDownload(actionsInterface.isStream(), context, eid, ur, new CookieConstructor(datas.get(0)));
                                                    actionsInterface.onStartDownload();
                                                    break;
                                                case "mega":
                                                    dialog.dismiss();
                                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ur)));
                                                    MainStates.setProcessing(false, null);
                                                    actionsInterface.onStartDownload();
                                                    break;
                                                default:
                                                    startDownload(actionsInterface.isStream(), context, eid, ur);
                                                    MainStates.setProcessing(false, null);
                                                    actionsInterface.onStartDownload();
                                                    dialog.dismiss();
                                                    break;
                                            }
                                        }
                                    })
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            MainStates.setProcessing(false, null);
                                            actionsInterface.onCancelDownload();
                                            dialog.dismiss();
                                        }
                                    })
                                    .cancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            dialog.dismiss();
                                            MainStates.setProcessing(false, null);
                                            actionsInterface.onCancelDownload();
                                        }
                                    });
                            if (CastPlayBackManager.get(context).isDeviceConnected() && actionsInterface.isStream()) {
                                d.neutralText("CAST");
                                d.onNeutral(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        String url = urls.get(sp.getSelectedItemPosition());
                                        if (!url.toLowerCase().contains("zippyshare")) {
                                            CastPlayBackManager.get(context).play(url, eid);
                                            dialog.dismiss();
                                        } else {
                                            Toaster.toast("No se puede reproducir desde Zippyshare!!!");
                                            dialog.dismiss();
                                        }
                                        actionsInterface.onStartCasting();
                                    }
                                });
                            }
                            MaterialDialog builded = d.build();
                            sp = (Spinner) builded.getCustomView().findViewById(R.id.spinner_down);
                            sp.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, nombres));
                            sp.setBackgroundColor((ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context)));
                            builded.show();
                        } else {
                            Toaster.toast("No hay links!!! Intenta mas tarde!!!");
                            MainStates.setProcessing(false, null);
                            actionsInterface.onCancelDownload();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainStates.setProcessing(false, null);
                        actionsInterface.onCancelDownload();
                        actionsInterface.onLogError(e);
                        if (NetworkUtils.isNetworkAvailable()) {
                            if (Parser.getUrlCached(eid, "000").equals("null")) {
                                Toaster.toast("Anime no encontrado en directorio!");
                            } else if (json.startsWith("error") && json.contains("503")) {
                                Toaster.toast("No se pudo acceder a la pagina de Animeflv");
                                Bypass.check(context, null);
                            } else {
                                Toaster.toast("Error en json");
                            }
                        } else {
                            Toaster.toast("No hay internet!!!");
                        }
                    }
                    Looper.loop();
                }
            });
        } else {
            IllegalStateException exception = new IllegalStateException("Eid don't have format: " + eid);
            actionsInterface.onLogError(exception);
            Crashlytics.logException(exception);
        }
    }

    private static void startDownload(boolean isStreaming, Activity context, String eid, String url) {
        if (isStreaming) {
            StreamManager.Stream(context, eid, url);
        } else {
            ManageDownload.chooseDownDir(context, eid, url);
        }
    }

    private static void startDownload(boolean isStreaming, Activity context, String eid, String url, CookieConstructor cookieConstructor) {
        if (isStreaming) {
            StreamManager.Stream(context, eid, url);
        } else {
            ManageDownload.chooseDownDir(context, eid, url, cookieConstructor);
        }
    }

    public interface ActionsInterface {
        boolean isStream();

        void onStartDownload();

        void onStartZippy(String u);

        void onCancelDownload();

        void onStartCasting();

        void onLogError(Exception e);
    }
}
