package knf.animeflv.JsonFactory;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import knf.animeflv.Cloudflare.Bypass;
import knf.animeflv.Cloudflare.BypassHolder;
import knf.animeflv.Directorio.DB.DirectoryHelper;
import knf.animeflv.DownloadManager.CookieConstructor;
import knf.animeflv.DownloadManager.ManageDownload;
import knf.animeflv.JsonFactory.JsonTypes.DOWNLOAD;
import knf.animeflv.JsonFactory.Objects.Option;
import knf.animeflv.JsonFactory.Objects.Server;
import knf.animeflv.PlayBack.CastPlayBackManager;
import knf.animeflv.StreamManager.StreamManager;
import knf.animeflv.Suggestions.Algoritm.SuggestionAction;
import knf.animeflv.Suggestions.Algoritm.SuggestionHelper;
import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.MainStates;
import knf.animeflv.Utils.NetworkUtils;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

public class DownloadGetter {

    public static void search2(final Activity context, final String eid, final ActionsInterface actionsInterface) {
        final MaterialDialog progress = new MaterialDialog.Builder(context)
                .content("Obteniendo links...")
                .theme((ThemeUtils.isAmoled(context) || ThemeUtils.isTV(context)) ? Theme.DARK : Theme.LIGHT)
                .contentColor((ThemeUtils.isAmoled(context) && !ThemeUtils.isTV(context)) ? Color.WHITE : Color.BLACK)
                .widgetColor(ThemeUtils.getAcentColor(context))
                .progress(true, 0)
                .cancelable(false)
                .build();
        if (eid.contains("_") && eid.endsWith("E")) {
            if (actionsInterface instanceof ActionsInterfaceDeep)
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= 21)
                            MDTintHelper.setTint(progress.getProgressBar(), ThemeUtils.getAcentColor(context));
                        progress.show();
                    }
                });
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
                            if (actionsInterface instanceof ActionsInterfaceDeep)
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress.dismiss();
                                    }
                                });
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
                                if (actionsInterface instanceof ActionsInterfaceDeep)
                                    context.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progress.dismiss();
                                        }
                                    });
                                final MaterialDialog.Builder d = new MaterialDialog.Builder(context)
                                        .title(actionsInterface.isStream() ? "Streaming" : "Descarga")
                                        .titleColor(ThemeUtils.isAmoled(context) ? Color.WHITE : Color.BLACK)
                                        .titleGravity(GravityEnum.CENTER)
                                        .items(nombres)
                                        .autoDismiss(false)
                                        .theme((ThemeUtils.isAmoled(context) || ThemeUtils.isTV(context)) ? Theme.DARK : Theme.LIGHT)
                                        .choiceWidgetColor(ColorStateList.valueOf(ThemeUtils.getAcentColor(context)))
                                        .widgetColor(ThemeUtils.getAcentColor(context))
                                        .positiveColor(ThemeUtils.getAcentColor(context))
                                        .negativeColor(ThemeUtils.getAcentColor(context))
                                        .neutralColor(ThemeUtils.getAcentColor(context))
                                        .negativeText("cerrar")
                                        .positiveText("iniciar")
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
                                                materialDialog.dismiss();
                                                return true;
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
                                            dialog.dismiss();
                                        }
                                    });
                                } else if (!actionsInterface.isStream()) {
                                    d.neutralText("Tamaño");
                                    d.onNeutral(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull final MaterialDialog materialDialog, @NonNull final DialogAction dialogAction) {
                                            new AsyncTask<Void, Void, Void>() {
                                                @Override
                                                protected Void doInBackground(Void... voids) {
                                                    switch (nombres.get(materialDialog.getSelectedIndex()).toLowerCase()) {
                                                        case "zippyshare":
                                                        case "zippyshare fast":
                                                        case "openload":
                                                        case "mega":
                                                            Toaster.toast("Desconocido");
                                                            break;
                                                        default:
                                                            long size = Long.parseLong(SelfGetter.getSize(urls.get(materialDialog.getSelectedIndex())));
                                                            String formated = FileUtil.formatSize(size);
                                                            if (size == -1) {
                                                                Toaster.toast("Desconocido");
                                                            } else if (formated.endsWith(" B")) {
                                                                Toaster.toast("Error en archivo(evitar servidor)");
                                                            } else {
                                                                Toaster.toast(formated);
                                                            }
                                                            break;
                                                    }
                                                    return null;
                                                }
                                            }.executeOnExecutor(ExecutorManager.getExecutor());
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
                            try {
                                if (DirectoryHelper.get(context).getEpUrl(eid, "000").equals("null")) {
                                    Toaster.toast("Anime no encontrado en directorio!");
                                } else if (json.startsWith("error") && json.contains("503")) {
                                    Toaster.toast("No se pudo acceder a la pagina de Animeflv");
                                    Bypass.check(context, null);
                                } else if (json.startsWith("error") && json.contains("521")) {
                                    Toaster.toast("Pagina de Animeflv caida");
                                } else if (json.startsWith("error")) {
                                    Toaster.toast("Error en conexion: " + json);
                                } else if (e instanceof JSONException) {
                                    Toaster.toast("Error en json: " + e.getMessage());
                                    Crashlytics.logException(e);
                                } else {
                                    Toaster.toast("Error desconocido: " + e.getMessage());
                                    Crashlytics.logException(e);
                                }
                            } catch (Exception ex) {
                                Toaster.toast("Error desconocido: " + e.getMessage());
                                Crashlytics.logException(e);
                                Crashlytics.logException(ex);
                            }
                        } else {
                            if (progress.isShowing())
                                progress.dismiss();
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

    public static void search(final Activity context, final String eid, final ActionsInterface actionsInterface) {
        final MaterialDialog progress = new MaterialDialog.Builder(context)
                .content("Obteniendo links...")
                .theme((ThemeUtils.isAmoled(context) || ThemeUtils.isTV(context)) ? Theme.DARK : Theme.LIGHT)
                .contentColor((ThemeUtils.isAmoled(context) && !ThemeUtils.isTV(context)) ? Color.WHITE : Color.BLACK)
                .widgetColor(ThemeUtils.getAcentColor(context))
                .progress(true, 0)
                .cancelable(false)
                .build();
        if (eid.contains("_") && eid.endsWith("E")) {
            if (actionsInterface instanceof ActionsInterfaceDeep)
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= 21)
                            MDTintHelper.setTint(progress.getProgressBar(), ThemeUtils.getAcentColor(context));
                        progress.show();
                    }
                });
            BypassHolder.savedToLocal(context);
            BaseGetter.getJson(context, new DOWNLOAD(eid), new BaseGetter.AsyncDownloadInterface() {
                @Override
                public void onFinish(@NonNull final List<Server> servers) {
                    try {
                        Looper.prepare();
                    } catch (Exception e) {
                    }
                    try {
                        if (servers.size() > 0) {
                            if (actionsInterface instanceof ActionsInterfaceDeep)
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress.dismiss();
                                    }
                                });
                            String last = PreferenceManager.getDefaultSharedPreferences(context).getString(actionsInterface.isStream() ? "last_stream" : "last_download", "null");
                            final int last_pos = Server.findPosition(servers, last);
                            int pref = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("def_download_serv", "0"));
                            if (pref > 0 && Server.existServer(servers, pref - 1) && !(CastPlayBackManager.get(context).isDeviceConnected() && actionsInterface.isStream())) {
                                Server server = servers.get(pref - 1);
                                String des = server.name;
                                if (server.haveOptions()) {
                                    showOptions(context, eid, server.name, server.options, null, actionsInterface);
                                } else {
                                    final String ur = server.getOption().url;
                                    switch (des.toLowerCase()) {
                                        case "zippyshare":
                                            actionsInterface.onStartZippy(ur);
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
                                }
                            } else {
                                if (actionsInterface instanceof ActionsInterfaceDeep)
                                    context.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progress.dismiss();
                                        }
                                    });
                                final MaterialDialog.Builder d = new MaterialDialog.Builder(context)
                                        .title(actionsInterface.isStream() ? "Streaming" : "Descarga")
                                        .titleColor(ThemeUtils.isAmoled(context) ? Color.WHITE : Color.BLACK)
                                        .titleGravity(GravityEnum.CENTER)
                                        .items(Server.getNames(servers))
                                        .autoDismiss(false)
                                        .theme((ThemeUtils.isAmoled(context) || ThemeUtils.isTV(context)) ? Theme.DARK : Theme.LIGHT)
                                        .choiceWidgetColor(ColorStateList.valueOf(ThemeUtils.getAcentColor(context)))
                                        .widgetColor(ThemeUtils.getAcentColor(context))
                                        .positiveColor(ThemeUtils.getAcentColor(context))
                                        .negativeColor(ThemeUtils.getAcentColor(context))
                                        .neutralColor(ThemeUtils.getAcentColor(context))
                                        .negativeText("cerrar")
                                        .positiveText("iniciar")
                                        .itemsCallbackSingleChoice(last_pos, new MaterialDialog.ListCallbackSingleChoice() {
                                            @Override
                                            public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                                                if (servers.get(i).haveOptions()) {
                                                    showOptions(context, eid, servers.get(i).name, servers.get(i).options, materialDialog, actionsInterface);
                                                } else {
                                                    SuggestionHelper.register(context, eid.trim().split("_")[0], SuggestionAction.PLAY);
                                                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(actionsInterface.isStream() ? "last_stream" : "last_download", charSequence.toString()).apply();
                                                    final String ur = servers.get(i).getOption().url;
                                                    switch (charSequence.toString().toLowerCase()) {
                                                        case "zippyshare":
                                                            actionsInterface.onStartZippy(ur);
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
                                                }
                                                materialDialog.dismiss();
                                                return true;
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
                                            Server server = servers.get(dialog.getSelectedIndex());
                                            if (!server.haveOptions()) {
                                                final String url = server.getOption().url;
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
                                            } else {
                                                showOptions(context, eid, server.name, server.options, true, dialog, actionsInterface);
                                            }
                                            dialog.dismiss();
                                        }
                                    });
                                } else if (!actionsInterface.isStream()) {
                                    d.neutralText("Tamaño");
                                    d.onNeutral(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull final MaterialDialog materialDialog, @NonNull final DialogAction dialogAction) {
                                            new AsyncTask<Void, Void, Void>() {
                                                @Override
                                                protected Void doInBackground(Void... voids) {
                                                    Server server = servers.get(materialDialog.getSelectedIndex());
                                                    if (!server.haveOptions()) {
                                                        switch (server.name.toLowerCase()) {
                                                            case "zippyshare":
                                                            case "zippyshare fast":
                                                            case "openload":
                                                            case "mega":
                                                                Toaster.toast("Desconocido");
                                                                break;
                                                            default:
                                                                long size = Long.parseLong(SelfGetter.getSize(server.getOption().url));
                                                                String formated = FileUtil.formatSize(size);
                                                                if (size == -1) {
                                                                    Toaster.toast("Desconocido");
                                                                } else if (formated.endsWith(" B")) {
                                                                    Toaster.toast("Error en archivo(evitar servidor)");
                                                                } else {
                                                                    Toaster.toast(formated);
                                                                }
                                                                break;
                                                        }
                                                    } else {
                                                        Toaster.toast("Este servidor tiene mas de una opcion disponible");
                                                    }
                                                    return null;
                                                }
                                            }.executeOnExecutor(ExecutorManager.getExecutor());
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
                        Toaster.toast("Error desconocido: " + e.getMessage());
                        Crashlytics.logException(e);
                        if (!NetworkUtils.isNetworkAvailable()) {
                            if (progress.isShowing())
                                progress.dismiss();
                            Toaster.toast("No hay internet!!!");
                        }
                    }
                    try {
                        Looper.loop();
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onError(String json) {
                    if (NetworkUtils.isNetworkAvailable()) {
                        try {
                            if (DirectoryHelper.get(context).getEpUrl(eid, "000").equals("null")) {
                                Toaster.toast("Anime no encontrado en directorio!");
                            } else if (json.startsWith("error") && json.contains("503")) {
                                Toaster.toast("No se pudo acceder a la pagina de Animeflv");
                                Bypass.check(context, null);
                            } else if (json.startsWith("error") && json.contains("521")) {
                                Toaster.toast("Pagina de Animeflv caida");
                            } else if (json.startsWith("error")) {
                                Toaster.toast("Error en conexion: " + json);
                            } else {
                                Toaster.toast("Error desconocido: " + json);
                            }
                        } catch (Exception ex) {
                            Toaster.toast("Error desconocido: " + ex.getMessage());
                            Crashlytics.logException(ex);
                        }
                    }
                }
            });
        } else {
            IllegalStateException exception = new IllegalStateException("Eid don't have format: " + eid);
            actionsInterface.onLogError(exception);
            Crashlytics.logException(exception);
        }
    }

    private static void showOptions(final Activity context, final String eid, final String name, final List<Option> options, MaterialDialog dialog, final ActionsInterface actionsInterface) {
        showOptions(context, eid, name, options, false, dialog, actionsInterface);
    }

    private static void showOptions(final Activity context, final String eid, final String name, final List<Option> options, final boolean cast, final MaterialDialog dialog, final ActionsInterface actionsInterface) {
        MaterialDialog.Builder d = new MaterialDialog.Builder(context)
                .title(name)
                .titleGravity(GravityEnum.CENTER)
                .items(Option.getNames(options))
                .cancelable(true)
                .autoDismiss(false)
                .positiveText("iniciar")
                .negativeText("atras")
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, View view, final int i, CharSequence charSequence) {
                        if (cast) {
                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... voids) {
                                    CastPlayBackManager.get(context).play(options.get(i).url, eid);
                                    actionsInterface.onStartCasting();
                                    return null;
                                }
                            }.executeOnExecutor(ExecutorManager.getExecutor());
                        } else {
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(actionsInterface.isStream() ? "last_stream" : "last_download", name).apply();
                            startDownload(actionsInterface.isStream(), context, eid, options.get(i).url);
                            MainStates.setProcessing(false, null);
                            actionsInterface.onStartDownload();
                        }
                        SuggestionHelper.register(context, eid.trim().split("_")[0], SuggestionAction.PLAY);
                        materialDialog.dismiss();
                        return true;
                    }
                })
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        if (dialog != null)
                            dialog.show();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        materialDialog.dismiss();
                        if (dialog != null)
                            dialog.show();
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        if (materialDialog.getSelectedIndex() >= 0) {
                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... voids) {
                                    long size = Long.parseLong(SelfGetter.getSize(options.get(materialDialog.getSelectedIndex()).url));
                                    String formated = FileUtil.formatSize(size);
                                    if (size == -1) {
                                        Toaster.toast("Desconocido");
                                    } else if (formated.endsWith(" B")) {
                                        Toaster.toast("Error en archivo(evitar opcion)");
                                    } else {
                                        Toaster.toast(formated);
                                    }
                                    return null;
                                }
                            }.executeOnExecutor(ExecutorManager.getExecutor());
                        }
                    }
                });
        if (!actionsInterface.isStream())
            d.neutralText("tamaño");
        d.build().show();
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

    public interface ActionsInterfaceDeep extends ActionsInterface {

    }
}
