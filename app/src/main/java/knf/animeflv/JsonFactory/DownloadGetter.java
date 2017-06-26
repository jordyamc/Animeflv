package knf.animeflv.JsonFactory;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

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
import knf.animeflv.StreamManager.StreamManager;
import knf.animeflv.Suggestions.Algoritm.SuggestionAction;
import knf.animeflv.Suggestions.Algoritm.SuggestionHelper;
import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.MainStates;
import knf.animeflv.Utils.NetworkUtils;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

public class DownloadGetter {

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
                            Log.e("Default Server", "No default");
                        }
                        if (nombres.size() != 0) {
                            String last = PreferenceManager.getDefaultSharedPreferences(context).getString(actionsInterface.isStream() ? "last_download" : "last_download", "null");
                            final int last_pos = nombres.contains(last) ? nombres.indexOf(last) : 0;
                            int pref = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("def_download", "0"));
                            if (pref > 0 && !jsonArray.getJSONObject(pref - 1).getString("url").equals("null") && !(CastPlayBackManager.get(context).isDeviceConnected() && actionsInterface.isStream())) {
                                JSONObject current = jsonArray.getJSONObject(pref - 1);
                                String des = current.getString("name");
                                final String ur = current.getString("url");
                                switch (des.toLowerCase()) {
                                    case "zippyshare":
                                        actionsInterface.onStartZippy(ur);
                                        break;
                                    case "zippyshare fast":
                                        startDownload(actionsInterface.isStream(), context, eid, ur, new CookieConstructor(datas.get(0)));
                                        actionsInterface.onStartDownload();
                                        break;
                                    case "openload":
                                        OpenLoadGetter.get(context, ur, new OpenLoadGetter.OpenLoadInterface() {
                                            @Override
                                            public void onSuccess(String url_final) {
                                                startDownload(actionsInterface.isStream(), context, eid, url_final);
                                                actionsInterface.onStartDownload();
                                            }

                                            @Override
                                            public void onError(String error) {
                                                Toaster.toast(error);
                                                actionsInterface.onCancelDownload();
                                            }
                                        });
                                        break;
                                    case "mega":
                                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ur)));
                                        MainStates.setProcessing(false, null);
                                        actionsInterface.onStartDownload();
                                        break;
                                    default:
                                        startDownload(actionsInterface.isStream(), context, eid, ur);
                                        MainStates.setProcessing(false, null);
                                        actionsInterface.onStartDownload();
                                        break;
                                }
                                SuggestionHelper.register(context, eid.trim().split("_")[0], SuggestionAction.PLAY);
                            } else {
                                final MaterialDialog.Builder d = new MaterialDialog.Builder(context)
                                        .title(actionsInterface.isStream() ? "Streaming" : "Descarga")
                                        .titleGravity(GravityEnum.CENTER)
                                        .items(nombres)
                                        .autoDismiss(true)
                                        .positiveText(actionsInterface.isStream() ? "Reproducir" : "Descargar")
                                        .negativeText("Cancelar")
                                        .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                                        .itemsCallbackSingleChoice(last_pos, new MaterialDialog.ListCallbackSingleChoice() {
                                            @Override
                                            public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                                                SuggestionHelper.register(context, eid.trim().split("_")[0], SuggestionAction.PLAY);
                                                PreferenceManager.getDefaultSharedPreferences(context).edit().putString(actionsInterface.isStream() ? "last_download" : "last_download", charSequence.toString()).apply();
                                                final String ur = urls.get(i);
                                                switch (charSequence.toString().toLowerCase()) {
                                                    case "zippyshare":
                                                        actionsInterface.onStartZippy(ur);
                                                        break;
                                                    case "zippyshare fast":
                                                        startDownload(actionsInterface.isStream(), context, eid, ur, new CookieConstructor(datas.get(0)));
                                                        actionsInterface.onStartDownload();
                                                        break;
                                                    case "openload":
                                                        OpenLoadGetter.get(context, ur, new OpenLoadGetter.OpenLoadInterface() {
                                                            @Override
                                                            public void onSuccess(String url_final) {
                                                                startDownload(actionsInterface.isStream(), context, eid, url_final);
                                                                actionsInterface.onStartDownload();
                                                            }

                                                            @Override
                                                            public void onError(String error) {
                                                                Toaster.toast(error);
                                                                actionsInterface.onCancelDownload();
                                                            }
                                                        });
                                                        break;
                                                    case "mega":
                                                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ur)));
                                                        MainStates.setProcessing(false, null);
                                                        actionsInterface.onStartDownload();
                                                        break;
                                                    default:
                                                        startDownload(actionsInterface.isStream(), context, eid, ur);
                                                        MainStates.setProcessing(false, null);
                                                        actionsInterface.onStartDownload();
                                                        break;
                                                }
                                                return true;
                                            }
                                        })
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                MainStates.setProcessing(false, null);
                                                actionsInterface.onCancelDownload();
                                            }
                                        })
                                        .cancelListener(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialog) {
                                                MainStates.setProcessing(false, null);
                                                actionsInterface.onCancelDownload();
                                            }
                                        });
                                if (CastPlayBackManager.get(context).isDeviceConnected() && actionsInterface.isStream()) {
                                    d.neutralText("CAST");
                                    d.onNeutral(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            SuggestionHelper.register(context, eid.trim().split("_")[0], SuggestionAction.PLAY);
                                            final String url = urls.get(dialog.getSelectedIndex());
                                            if (url.toLowerCase().contains("zippyshare")) {
                                                Toaster.toast("No se puede reproducir desde Zippyshare!!!");
                                                MainStates.setProcessing(false, null);
                                                actionsInterface.onCancelDownload();
                                            } else {
                                                new AsyncTask<Void, Void, Void>() {
                                                    @Override
                                                    protected Void doInBackground(Void... voids) {
                                                        CastPlayBackManager.get(context).play(url, eid);
                                                        actionsInterface.onStartCasting();
                                                        return null;
                                                    }
                                                }.executeOnExecutor(ExecutorManager.getExecutor());
                                            }
                                        }
                                    });
                                }
                                d.build().show();
                            }
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
                            } else if (json.startsWith("error") && json.contains("521")) {
                                Toaster.toast("Pagina de Animeflv caida");
                            } else {
                                Toaster.toast("Error en json");
                            }
                        } else {
                            Toaster.toast("No hay internet!!!");
                        }
                    }
                    try {
                        Looper.loop();
                    } catch (Exception e) {
                    }
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
        } else if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("use_ext_downloader", false)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(url), "video/mp4");
            context.startActivity(intent);
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
