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
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.afollestad.materialdialogs.internal.MDButton;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.crashlytics.android.Crashlytics;

import java.util.List;

import knf.animeflv.Cloudflare.Bypass;
import knf.animeflv.Cloudflare.BypassHolder;
import knf.animeflv.Directorio.DB.DirectoryHelper;
import knf.animeflv.DownloadManager.CookieConstructor;
import knf.animeflv.DownloadManager.ManageDownload;
import knf.animeflv.JsonFactory.JsonTypes.DOWNLOAD;
import knf.animeflv.JsonFactory.Objects.Option;
import knf.animeflv.JsonFactory.Objects.VideoServer;
import knf.animeflv.PlayBack.CastPlayBackManager;
import knf.animeflv.StreamManager.StreamManager;
import knf.animeflv.Suggestions.Algoritm.SuggestionAction;
import knf.animeflv.Suggestions.Algoritm.SuggestionHelper;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.MainStates;
import knf.animeflv.Utils.NetworkUtils;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.VideoServers.Server;
import xdroid.toaster.Toaster;

public class DownloadGetter {

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
                public void onFinish(@NonNull final List<Server> videoServers) {
                    try {
                        Looper.prepare();
                    } catch (Exception e) {
                    }
                    try {
                        if (videoServers.size() > 0) {
                            if (actionsInterface instanceof ActionsInterfaceDeep)
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress.dismiss();
                                    }
                                });
                            final int pref = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("def_download_serv", "0"));
                            if (pref > 0 &&
                                    PreferenceManager.getDefaultSharedPreferences(context).getBoolean("no_skip_check", true) &&
                                    Server.existServer(videoServers, pref) &&
                                    !(CastPlayBackManager.get(context).isDeviceConnected() &&
                                            actionsInterface.isStream())) {
                                Server videoServer = Server.findServer(videoServers, pref);
                                String des = videoServer.getLink().name;
                                if (videoServer.getLink().haveOptions()) {
                                    showOptions(context, eid, videoServer.getLink().name, videoServer.getLink().options, videoServers, 0, actionsInterface);
                                } else {
                                    final String ur = videoServer.getLink().getOption().url;
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
                                String last = PreferenceManager.getDefaultSharedPreferences(context).getString(actionsInterface.isStream() ? "last_stream" : "last_download", "null");
                                final int last_pos = Server.findPosition(videoServers, last);
                                if (actionsInterface instanceof ActionsInterfaceDeep)
                                    context.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progress.dismiss();
                                        }
                                    });
                                showDownload(context, eid, last_pos, videoServers, actionsInterface);
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

    private static void showDownload(final Activity context, final String eid, final int last_pos, final List<Server> videoServers, final ActionsInterface actionsInterface) {
        final MaterialDialog.Builder d = new MaterialDialog.Builder(context)
                .title(actionsInterface.isStream() ? "Streaming" : "Descarga")
                .titleColor(ThemeUtils.isAmoled(context) ? Color.WHITE : Color.BLACK)
                .titleGravity(GravityEnum.CENTER)
                .items(Server.getNames(videoServers))
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
                    public boolean onSelection(final MaterialDialog materialDialog, View view, final int i, final CharSequence charSequence) {
                        final MaterialDialog progress = new MaterialDialog.Builder(context)
                                .content("Obteniendo link...")
                                .progress(true, 0)
                                .cancelable(false)
                                .build();
                        materialDialog.hide();
                        progress.show();
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                VideoServer videoServer = videoServers.get(i).getLink();
                                if (videoServer != null) {
                                    progress.dismiss();
                                    if (videoServers.get(i).getLink().haveOptions()) {
                                        showOptions(context, eid, videoServer.name, videoServer.options, videoServers, i, actionsInterface);
                                    } else {
                                        materialDialog.dismiss();
                                        SuggestionHelper.register(context, eid.trim().split("_")[0], SuggestionAction.PLAY);
                                        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(actionsInterface.isStream() ? "last_stream" : "last_download", charSequence.toString()).apply();
                                        final String ur = videoServer.getOption().url;
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
                                } else {
                                    Toaster.toast("Servidor invalido, intente otro");
                                    progress.dismiss();
                                    context.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!materialDialog.isShowing())
                                                materialDialog.show();
                                        }
                                    });
                                }
                            }
                        });
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
                public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                    final MaterialDialog progress = new MaterialDialog.Builder(context)
                            .content("Obteniendo link...")
                            .progress(true, 0)
                            .cancelable(false)
                            .build();
                    dialog.dismiss();
                    progress.show();
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            SuggestionHelper.register(context, eid.trim().split("_")[0], SuggestionAction.PLAY);
                            VideoServer videoServer = videoServers.get(dialog.getSelectedIndex()).getLink();
                            if (videoServer != null) {
                                progress.dismiss();
                                if (!videoServer.haveOptions()) {
                                    final String url = videoServer.getOption().url;
                                    if (url.toLowerCase().contains("zippyshare")) {
                                        Toaster.toast("No se puede reproducir desde Zippyshare!!!");
                                        MainStates.setProcessing(false, null);
                                        actionsInterface.onCancelDownload();
                                    } else {
                                        AsyncTask.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                CastPlayBackManager.get(context).play(url, eid);
                                                actionsInterface.onStartCasting();
                                            }
                                        });
                                    }
                                } else {
                                    showOptions(context, eid, videoServer.name, videoServer.options, true, videoServers, dialog.getSelectedIndex(), actionsInterface);
                                }
                            } else {
                                Toaster.toast("Servidor invalido, intente otro");
                                progress.dismiss();
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showDownload(context, eid, last_pos, videoServers, actionsInterface);
                                    }
                                });
                            }
                        }
                    });
                }
            });
        } else if (!actionsInterface.isStream()) {
            d.neutralText("Tamaño");
            d.onNeutral(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull final MaterialDialog materialDialog, @NonNull final DialogAction dialogAction) {
                    final MDButton button = materialDialog.getActionButton(DialogAction.NEUTRAL);
                    button.setEnabled(false);
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            VideoServer videoServer = videoServers.get(materialDialog.getSelectedIndex()).getLink();
                            if (videoServer != null) {
                                if (!videoServer.haveOptions()) {
                                    switch (videoServer.name.toLowerCase()) {
                                        case "zippyshare":
                                        case "zippyshare fast":
                                        case "openload":
                                        case "mega":
                                            Toaster.toast("Desconocido");
                                            break;
                                        default:
                                            long size = Long.parseLong(SelfGetter.getSize(videoServer.getOption().url));
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
                            } else {
                                Toaster.toast("Servidor invalido, intente otro");
                            }
                            button.post(new Runnable() {
                                @Override
                                public void run() {
                                    button.setEnabled(true);
                                }
                            });
                        }
                    });
                }
            });
        }
        d.build().show();
    }

    private static void showOptions(final Activity context, final String eid, final String name, final List<Option> options, List<Server> videoServers, int pos, final ActionsInterface actionsInterface) {
        showOptions(context, eid, name, options, false, videoServers, pos, actionsInterface);
    }

    private static void showOptions(final Activity context, final String eid, final String name, final List<Option> options, final boolean cast, final List<Server> videoServers, final int pos, final ActionsInterface actionsInterface) {
        final MaterialDialog.Builder d = new MaterialDialog.Builder(context)
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
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    CastPlayBackManager.get(context).play(options.get(i).url, eid);
                                    actionsInterface.onStartCasting();
                                }
                            });
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
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showDownload(context, eid, pos, videoServers, actionsInterface);
                            }
                        });

                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        materialDialog.dismiss();
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showDownload(context, eid, pos, videoServers, actionsInterface);
                            }
                        });
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        if (materialDialog.getSelectedIndex() >= 0) {
                            final MDButton button = materialDialog.getActionButton(DialogAction.NEUTRAL);
                            button.setEnabled(false);
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    long size = Long.parseLong(SelfGetter.getSize(options.get(materialDialog.getSelectedIndex()).url));
                                    String formated = FileUtil.formatSize(size);
                                    if (size == -1) {
                                        Toaster.toast("Desconocido");
                                    } else if (formated.endsWith(" B")) {
                                        Toaster.toast("Error en archivo(evitar opcion)");
                                    } else {
                                        Toaster.toast(formated);
                                    }
                                    button.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            button.setEnabled(true);
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
        if (!actionsInterface.isStream())
            d.neutralText("tamaño");
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                d.build().show();
            }
        });
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
