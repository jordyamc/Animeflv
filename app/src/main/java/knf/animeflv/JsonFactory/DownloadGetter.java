package knf.animeflv.JsonFactory;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import knf.animeflv.ColorsRes;
import knf.animeflv.DownloadManager.ManageDownload;
import knf.animeflv.JsonFactory.JsonTypes.DOWNLOAD;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.StreamManager.StreamManager;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.MainStates;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

public class DownloadGetter {
    private static Spinner sp;

    public static void search(final Activity context, final String eid, final ActionsInterface actionsInterface) {
        BaseGetter.getJson(context, new DOWNLOAD(new Parser().getUrlCached(eid), eid), new BaseGetter.AsyncInterface() {
            @Override
            public void onFinish(String json) {
                try {
                    Looper.prepare();
                } catch (Exception e) {
                }
                ;
                try {
                    JSONArray jsonArray = new JSONObject(json).getJSONArray("downloads");
                    final List<String> nombres = new ArrayList<>();
                    final List<String> urls = new ArrayList<>();
                    try {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String u = object.getString("url");
                            if (!u.trim().equals("null")) {
                                nombres.add(object.getString("name"));
                                urls.add(u);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (nombres.size() != 0) {
                        final MaterialDialog d = new MaterialDialog.Builder(context)
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
                                            case "mega":
                                                dialog.dismiss();
                                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ur)));
                                                MainStates.setProcessing(false, null);
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
                                })
                                .build();
                        sp = (Spinner) d.getCustomView().findViewById(R.id.spinner_down);
                        sp.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, nombres));
                        sp.setBackgroundColor((ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context)));
                        d.show();
                    } else {
                        Toaster.toast("No hay links!!! Intenta mas tarde!!!");
                        MainStates.setProcessing(false, null);
                        actionsInterface.onCancelDownload();
                    }
                } catch (Exception e) {
                    MainStates.setProcessing(false, null);
                    actionsInterface.onCancelDownload();
                    actionsInterface.onLogError(e);
                    Parser parser = new Parser();
                    FileUtil.writeToFile(e.getMessage() + "   " + parser.getUrlCached(eid) + "\n" + e.getCause(), new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache", "log.txt"));
                    if (!parser.getUrlCached(eid).equals("null")) {
                        Toaster.toast("Error en JSON");
                    } else {
                        Toaster.toast("Anime no encontrado en directorio!");
                    }
                }
                Looper.loop();
            }
        });
    }

    private static void startDownload(boolean isStreaming, Activity context, String eid, String url) {
        if (isStreaming) {
            StreamManager.Stream(context, eid, url);
        } else {
            ManageDownload.chooseDownDir(context, eid, url);
        }
    }

    public interface ActionsInterface {
        boolean isStream();

        void onStartDownload();

        void onStartZippy(String u);

        void onCancelDownload();

        void onLogError(Exception e);
    }
}
