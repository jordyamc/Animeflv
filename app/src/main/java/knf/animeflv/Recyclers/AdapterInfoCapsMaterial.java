package knf.animeflv.Recyclers;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import knf.animeflv.ColorsRes;
import knf.animeflv.DownloadManager.CookieConstructor;
import knf.animeflv.DownloadManager.ManageDownload;
import knf.animeflv.JsonFactory.DownloadGetter;
import knf.animeflv.PlayBack.CastPlayBackManager;
import knf.animeflv.R;
import knf.animeflv.StreamManager.StreamManager;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.Logger;
import knf.animeflv.Utils.MainStates;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.UpdateUtil;
import knf.animeflv.Utils.UrlUtils;
import knf.animeflv.Utils.eNums.DownloadTask;
import knf.animeflv.Utils.eNums.UpdateState;
import knf.animeflv.info.InfoNewMaterial;
import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 08/08/2015.
 */
public class AdapterInfoCapsMaterial extends RecyclerView.Adapter<AdapterInfoCapsMaterial.ViewHolder> {

    List<String> capitulo;
    String id;
    List<String> eids;
    String ext_storage_state = Environment.getExternalStorageState();
    MaterialDialog d;
    Boolean streaming = false;
    int posT;
    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
    private Activity context;

    public AdapterInfoCapsMaterial(Activity context, List<String> capitulos, String aid, List<String> eid) {
        this.capitulo = capitulos;
        this.context = context;
        this.id = aid;
        this.eids = eid;
    }

    public static String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1) h = "0" + h;
            if (l > 2) h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1)) str.append(':');
        }
        return str.toString();
    }

    @Override
    public AdapterInfoCapsMaterial.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).
                inflate(R.layout.item_anime_descarga, parent, false);
        return new AdapterInfoCapsMaterial.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AdapterInfoCapsMaterial.ViewHolder holder, int position) {
        SetUpWeb(holder.web, holder);
        final String item = capitulo.get(position).replace("Capitulo ", "").trim();
        if (FileUtil.init(context).ExistAnime(eids.get(holder.getAdapterPosition()))) {
            showDelete(holder.ib_des);
            showPlay(holder.ib_ver);
        } else {
            if (ManageDownload.isDownloading(context, eids.get(holder.getAdapterPosition()))) {
                showDelete(holder.ib_des);
                showPlay(holder.ib_ver);
            } else {
                showCloudPlay(holder.ib_ver);
                showDownload(holder.ib_des);
            }
        }
        holder.tv_capitulo.setText(capitulo.get(position));
        Boolean vistos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getBoolean("visto" + id + "_" + item, false);
        holder.tv_capitulo.setTextColor(context.getResources().getColor(R.color.black));
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("is_amoled", false)) {
            holder.card.setCardBackgroundColor(ColorsRes.Prim(context));
            holder.tv_capitulo.setTextColor(ColorsRes.Blanco(context));
            holder.ib_ver.setColorFilter(ColorsRes.Holo_Dark(context));
            holder.ib_des.setColorFilter(null);
            holder.ib_des.setColorFilter(ColorsRes.Holo_Dark(context));
        } else {
            holder.ib_ver.setColorFilter(ColorsRes.Holo_Light(context));
            holder.ib_des.setColorFilter(null);
            holder.ib_des.setColorFilter(ColorsRes.Holo_Light(context));
        }
        if (vistos) {
            holder.tv_capitulo.setTextColor(getColor());
        }
        if (MainStates.init(context).WaitContains(eids.get(holder.getAdapterPosition()))) {
            if (!FileUtil.init(context).ExistAnime(eids.get(holder.getAdapterPosition()))) {
                if (CastPlayBackManager.get(context).getCastingEid().equals(eids.get(holder.getAdapterPosition()))) {
                    showDownload(holder.ib_des);
                    showCastPlay(holder.ib_ver);
                } else {
                    showCloudPlay(holder.ib_ver);
                    holder.ib_des.setImageResource(R.drawable.ic_waiting);
                }
            } else {
                showPlay(holder.ib_ver);
                showDelete(holder.ib_des);
                MainStates.init(context).delFromWaitList(eids.get(holder.getAdapterPosition()));
            }
        }
        holder.ib_des.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MainStates.isListing()) {
                    if (UpdateUtil.getState() == UpdateState.WAITING_TO_UPDATE) {
                        Toaster.toast("Actualizacion descargada, instalar para continuar");
                    } else {
                        if (!MainStates.isProcessing()) {
                            if (!MainStates.init(context).WaitContains(eids.get(holder.getAdapterPosition()))) {
                                if (!FileUtil.init(context).ExistAnime(eids.get(holder.getAdapterPosition())) && !ManageDownload.isDownloading(context, eids.get(holder.getAdapterPosition()))) {
                                    showLoading(holder.ib_des);
                                    searchDownload(holder);
                                } else {
                                    final String item = capitulo.get(holder.getAdapterPosition()).replace("Capitulo ", "").trim();
                                    MaterialDialog borrar = new MaterialDialog.Builder(context)
                                            .title("Eliminar")
                                            .titleGravity(GravityEnum.CENTER)
                                            .content("Desea eliminar el capitulo " + item + "?")
                                            .positiveText("Eliminar")
                                            .negativeText("Cancelar")
                                            .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                                    FileUtil.init(context).DeleteAnime(eids.get(holder.getAdapterPosition()));
                                                    showDownload(holder.ib_des);
                                                    showCloudPlay(holder.ib_ver);
                                                    ManageDownload.cancel(context, eids.get(holder.getAdapterPosition()));
                                                    Toast.makeText(context, "Archivo Eliminado", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                                    materialDialog.dismiss();
                                                }
                                            })
                                            .build();
                                    borrar.show();
                                }
                            } else {
                                String[] data = eids.get(holder.getAdapterPosition()).replace("E", "").split("_");
                                String aid = data[0];
                                String num = data[1];
                                new MaterialDialog.Builder(context)
                                        .content(
                                                "El capitulo " + num +
                                                        " de " + UrlUtils.getTitCached(aid) +
                                                        " se encuentra en lista de espera, si continua, sera removido de la lista, desea continuar?")
                                        .autoDismiss(true)
                                        .positiveText("Continuar")
                                        .negativeText("Cancelar")
                                        .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                MainStates.init(context).delFromWaitList(eids.get(holder.getAdapterPosition()));
                                                showLoading(holder.ib_des);
                                                searchDownload(holder);
                                            }
                                        })
                                        .build().show();
                            }
                        } else {
                            toast("Procesando...");
                        }
                    }
                }
            }
        });
        holder.ib_ver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MainStates.isListing()) {
                    if (UpdateUtil.getState() == UpdateState.WAITING_TO_UPDATE) {
                        Toaster.toast("Actualizacion descargada, instalar para continuar");
                    } else {
                        if (!MainStates.isProcessing()) {
                            if (!MainStates.init(context).WaitContains(eids.get(holder.getAdapterPosition()))) {
                                if (FileUtil.init(context).ExistAnime(eids.get(holder.getAdapterPosition())) && !ManageDownload.isDownloading(context, eids.get(holder.getAdapterPosition()))) {
                                    holder.tv_capitulo.setTextColor(getColor());
                                    StreamManager.Play(context, eids.get(holder.getAdapterPosition()));
                                } else {
                                    showLoading(holder.ib_des);
                                    searchStream(holder);
                                }
                            } else {
                                String[] data = eids.get(holder.getAdapterPosition()).replace("E", "").split("_");
                                String aid = data[0];
                                String num = data[1];
                                new MaterialDialog.Builder(context)
                                        .content(
                                                "El capitulo " + num +
                                                        " de " + UrlUtils.getTitCached(aid) +
                                                        " se encuentra en lista de espera, si continua, sera removido de la lista, desea continuar?")
                                        .autoDismiss(true)
                                        .positiveText("Continuar")
                                        .negativeText("Cancelar")
                                        .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                MainStates.init(context).delFromWaitList(eids.get(holder.getAdapterPosition()));
                                                if (FileUtil.init(context).ExistAnime(eids.get(holder.getAdapterPosition()))) {
                                                    holder.tv_capitulo.setTextColor(getColor());
                                                    StreamManager.Play(context, eids.get(holder.getAdapterPosition()));
                                                } else {
                                                    showLoading(holder.ib_des);
                                                    searchStream(holder);
                                                }
                                            }
                                        })
                                        .build().show();
                            }
                        }
                    }
                }
            }
        });
        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!MainStates.isListing()) {
                    if (!FileUtil.init(context).ExistAnime(eids.get(holder.getAdapterPosition()))) {
                        if (MainStates.init(context).WaitContains(eids.get(holder.getAdapterPosition()))) {
                            MainStates.init(context).delFromWaitList(eids.get(holder.getAdapterPosition()));
                            showDownload(holder.ib_des);
                        } else {
                            MainStates.init(context).addToWaitList(eids.get(holder.getAdapterPosition()));
                            holder.ib_des.setImageResource(R.drawable.ic_waiting);
                        }
                    }
                }
                return true;
            }
        });
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MainStates.isListing()) {
                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("cambio", true).apply();
                    if (!FileUtil.init(context).isInSeen(eids.get(holder.getAdapterPosition()))) {
                        FileUtil.init(context).setSeenState(eids.get(holder.getAdapterPosition()), true);
                        holder.tv_capitulo.setTextColor(getColor());
                    } else {
                        FileUtil.init(context).setSeenState(eids.get(holder.getAdapterPosition()), false);
                        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("is_amoled", false)) {
                            holder.tv_capitulo.setTextColor(context.getResources().getColor(R.color.blanco));
                        } else {
                            holder.tv_capitulo.setTextColor(context.getResources().getColor(R.color.black));
                        }
                    }
                } else {
                    if (!FileUtil.init(context).ExistAnime(eids.get(holder.getAdapterPosition()))) {
                        if (MainStates.init(context).WaitContains(eids.get(holder.getAdapterPosition()))) {
                            MainStates.init(context).delFromWaitList(eids.get(holder.getAdapterPosition()));
                            showDownload(holder.ib_des);
                        } else {
                            MainStates.init(context).addToWaitList(eids.get(holder.getAdapterPosition()));
                            holder.ib_des.setImageResource(R.drawable.ic_waiting);
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return capitulo.size();
    }

    private void showLoading(final ImageButton button) {
        MainStates.setProcessing(true, null);
        ((InfoNewMaterial) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.setImageResource(R.drawable.ic_warning);
                button.setEnabled(false);
            }
        });
    }

    private void showDownload(final ImageButton button) {
        MainStates.setProcessing(false, null);
        ((InfoNewMaterial) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.setImageResource(R.drawable.ic_get_r);
                button.setEnabled(true);
            }
        });
    }

    private void showDelete(final ImageButton button) {
        MainStates.setProcessing(false, null);
        ((InfoNewMaterial) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.setImageResource(R.drawable.ic_borrar_r);
                button.setEnabled(true);
            }
        });
    }

    private void showCastPlay(final ImageButton button) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.setImageResource(R.drawable.cast_c);
            }
        });
    }

    private void showCloudPlay(final ImageButton button) {
        ((InfoNewMaterial) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.setImageResource(R.drawable.ic_cloud_play);
            }
        });
    }

    private void showPlay(final ImageButton button) {
        ((InfoNewMaterial) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.setImageResource(R.drawable.ic_play);
            }
        });
    }

    @ColorInt
    private int getColor() {
        return ThemeUtils.getAcentColor(context);
    }

    private void searchDownload(final ViewHolder holder) {
        DownloadGetter.search(context, eids.get(holder.getAdapterPosition()), new DownloadGetter.ActionsInterface() {
            @Override
            public boolean isStream() {
                return false;
            }

            @Override
            public void onStartDownload() {
                showDelete(holder.ib_des);
                showPlay(holder.ib_ver);
            }

            @Override
            public void onStartZippy(final String url) {
                MainStates.setZippyState(DownloadTask.DESCARGA, url, holder.ib_des, holder.ib_ver, holder.getAdapterPosition());
                holder.web.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.web.loadUrl(url);
                    }
                });
            }

            @Override
            public void onCancelDownload() {
                showDownload(holder.ib_des);
            }

            @Override
            public void onStartCasting() {

            }

            @Override
            public void onLogError(Exception e) {
                Logger.Error(AdapterInfoCapsMaterial.this.getClass(), e);
            }
        });
    }

    private void searchStream(final ViewHolder holder) {
        DownloadGetter.search(context, eids.get(holder.getAdapterPosition()), new DownloadGetter.ActionsInterface() {
            @Override
            public boolean isStream() {
                return true;
            }

            @Override
            public void onStartDownload() {
                MainStates.setProcessing(false, null);
                showDownload(holder.ib_des);
            }

            @Override
            public void onStartZippy(final String url) {
                MainStates.setZippyState(DownloadTask.STREAMING, url, holder.ib_des, holder.ib_ver, holder.getAdapterPosition());
                holder.web.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.web.loadUrl(url);
                    }
                });
            }

            @Override
            public void onCancelDownload() {
                MainStates.setProcessing(false, null);
                showDownload(holder.ib_des);
            }

            @Override
            public void onStartCasting() {
                MainStates.setProcessing(false, null);
                showDownload(holder.ib_des);
                holder.ib_ver.setImageResource(es.munix.multidisplaycast.R.drawable.cast_on);
            }

            @Override
            public void onLogError(Exception e) {
                Logger.Error(AdapterInfoCapsMaterial.this.getClass(), e);
            }
        });
    }

    public void onStartList() {
        MainStates.setListing(true);
    }

    public void onStopList() {
        MainStates.setListing(false);
        notifyDataSetChanged();
    }

    public void SetUpWeb(final WebView web, final AdapterInfoCapsMaterial.ViewHolder holder) {
        web.getSettings().setJavaScriptEnabled(true);
        CookieSyncManager.createInstance(context);
        CookieSyncManager.getInstance().startSync();
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.contains("zippyshare.com") || url.contains("blank")) {
                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("urlD", url).apply();
                    web.loadUrl("javascript:("
                            + "function(){var l=document.getElementById('dlbutton');" + "var f=document.createEvent('HTMLEvents');" + "f.initEvent('click',true,true);" + "l.dispatchEvent(f);}"
                            + ")()");
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        web.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                String fileName = url.substring(url.lastIndexOf("/") + 1);
                web.loadUrl("about:blank");
                if (!streaming) {
                    File Dstorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("_")));
                    if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                        if (!Dstorage.exists()) {
                            Dstorage.mkdirs();
                        }
                    }
                    File archivo = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("_")) + "/" + fileName);
                    if (!archivo.exists()) {
                        String item = capitulo.get(holder.getAdapterPosition()).replace("Capitulo ", "").trim();
                        String urlD = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("urlD", null);
                        CookieManager cookieManager = CookieManager.getInstance();
                        String cookie = cookieManager.getCookie(url.substring(0, url.indexOf("/", 8)));
                        CookieConstructor constructor = new CookieConstructor(cookie, web.getSettings().getUserAgentString(), urlD);
                        ManageDownload.chooseDownDir(context, eids.get(holder.getAdapterPosition()), url, constructor);
                        showPlay(holder.ib_ver);
                        showDelete(holder.ib_des);
                        Boolean vistos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getBoolean("visto" + id + "_" + item, false);
                        if (!vistos) {
                            holder.tv_capitulo.setTextColor(getColor());
                        }
                    } else {
                        Toast.makeText(context, "El archivo ya existe", Toast.LENGTH_SHORT).show();
                    }
                    d.dismiss();

                } else {
                    int type = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_streaming", "0"));
                    String urlD = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("urlD", null);
                    CookieManager cookieManager = CookieManager.getInstance();
                    String cookie = cookieManager.getCookie(url.substring(0, url.indexOf("/", 8)));
                    streaming = false;
                    web.loadUrl("about:blank");
                    showCloudPlay(holder.ib_ver);
                    showDownload(holder.ib_des);
                    CookieConstructor constructor = new CookieConstructor(cookie, web.getSettings().getUserAgentString(), urlD);
                    if (type == 1) {
                        StreamManager.mx(context).Stream(eids.get(holder.getAdapterPosition()), url, constructor);
                        holder.tv_capitulo.setTextColor(ThemeUtils.getAcentColor(context));
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            StreamManager.internal(context).Stream(eids.get(holder.getAdapterPosition()), url, constructor);
                            holder.tv_capitulo.setTextColor(ThemeUtils.getAcentColor(context));
                        } else {
                            if (FileUtil.init(context).isMXinstalled()) {
                                toast("Version de android por debajo de lo requerido, reproduciendo en MXPlayer");
                                StreamManager.mx(context).Stream(eids.get(holder.getAdapterPosition()), url, constructor);
                                holder.tv_capitulo.setTextColor(ThemeUtils.getAcentColor(context));
                            } else {
                                toast("No hay reproductor adecuado disponible");
                            }
                        }
                    }
                }
            }
        });
    }

    public void toast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_capitulo;
        public ImageButton ib_ver;
        public ImageButton ib_des;
        public CardView card;
        public RecyclerView recyclerView;
        public WebView web;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tv_capitulo = (TextView) itemView.findViewById(R.id.tv_cardD_capitulo);
            this.ib_ver = (ImageButton) itemView.findViewById(R.id.ib_ver_rv);
            this.ib_des = (ImageButton) itemView.findViewById(R.id.ib_descargar_rv);
            this.card = (CardView) itemView.findViewById(R.id.card_descargas_info);
            this.web = (WebView) itemView.findViewById(R.id.wv_anime_zippy);
        }
    }

}
