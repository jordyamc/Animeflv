package knf.animeflv.Recyclers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import knf.animeflv.ColorsRes;
import knf.animeflv.DownloadManager.CookieConstructor;
import knf.animeflv.DownloadManager.ManageDownload;
import knf.animeflv.Interfaces.MainRecyclerCallbacks;
import knf.animeflv.JsonFactory.DownloadGetter;
import knf.animeflv.Parser;
import knf.animeflv.PlayBack.CastPlayBackManager;
import knf.animeflv.R;
import knf.animeflv.Recientes.MainAnimeModel;
import knf.animeflv.Seen.SeenManager;
import knf.animeflv.StreamManager.StreamManager;
import knf.animeflv.TaskType;
import knf.animeflv.Utils.CacheManager;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.Logger;
import knf.animeflv.Utils.MainStates;
import knf.animeflv.Utils.NetworkUtils;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.UpdateUtil;
import knf.animeflv.Utils.eNums.DownloadTask;
import knf.animeflv.Utils.eNums.UpdateState;
import knf.animeflv.info.Helper.InfoHelper;
import xdroid.toaster.Toaster;


public class AdapterMainNoGIF extends RecyclerView.Adapter<AdapterMainNoGIF.ViewHolder> {

    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
    private Activity context;
    private List<MainAnimeModel> Animes = new ArrayList<>();
    private MainRecyclerCallbacks callbacks;
    private Parser parser = new Parser();
    private Spinner sp;
    private MaterialDialog d;

    public AdapterMainNoGIF(Activity context) {
        this.context = context;
        this.callbacks = (MainRecyclerCallbacks) context;
    }

    public AdapterMainNoGIF(Activity context, List<MainAnimeModel> data) {
        this.context = context;
        this.callbacks = (MainRecyclerCallbacks) context;
        this.Animes = data;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile(String filePath) {
        String ret = "";
        try {
            File fl = new File(filePath);
            FileInputStream fin = new FileInputStream(fl);
            ret = convertStreamToString(fin);
            fin.close();
        } catch (IOException e) {
        } catch (Exception e) {
        }
        return ret;
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
    public AdapterMainNoGIF.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).
                inflate(R.layout.item_main_ng, parent, false);
        return new AdapterMainNoGIF.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AdapterMainNoGIF.ViewHolder holder, final int position) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("is_amoled", false)) {
            holder.card.setCardBackgroundColor(context.getResources().getColor(R.color.prim));
            holder.tv_tit.setTextColor(context.getResources().getColor(R.color.blanco));
            holder.ib_des.setColorFilter(ColorsRes.Holo_Dark(context));
            holder.ib_ver.setColorFilter(ColorsRes.Holo_Dark(context));
        } else {
            holder.card.setCardBackgroundColor(ColorsRes.Blanco(context));
            holder.ib_des.setColorFilter(ColorsRes.Holo_Light(context));
        }
        Boolean resaltar = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("resaltar", true);
        if (getCap(holder.getAdapterPosition()).equals("Capítulo 1") || getCap(holder.getAdapterPosition()).equals("Preestreno") || getCap(holder.getAdapterPosition()).contains("OVA") || getCap(holder.getAdapterPosition()).contains("Pelicula")) {
            if (resaltar)
                holder.card.setCardBackgroundColor(Color.argb(100, 253, 250, 93));
        }
        final String favoritos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("favoritos", "");
        final Boolean comp = favoritos.startsWith(Animes.get(position).getAid() + ":::") || favoritos.contains(":::" + Animes.get(position).getAid() + ":::") || favoritos.endsWith(":::" + Animes.get(position).getAid());
        if (comp) {
            if (resaltar)
                holder.card.setCardBackgroundColor(Color.argb(100, 26, 206, 246));
        }
        setUpWeb(holder.webView);
        holder.tv_num.setTextColor(ThemeUtils.getAcentColor(context));
        new CacheManager().mini(context, Animes.get(holder.getAdapterPosition()).getAid(), holder.iv_main);
        holder.tv_tit.setText(Animes.get(position).getTitulo());
        holder.tv_num.setText(getCap(holder.getAdapterPosition()));
        if (FileUtil.init(context).ExistAnime(Animes.get(holder.getAdapterPosition()).getEid())) {
            showPlay(holder.ib_ver);
            showDelete(holder.ib_des);
        } else {
            if (ManageDownload.isDownloading(context, Animes.get(holder.getAdapterPosition()).getEid())) {
                showPlay(holder.ib_ver);
                showDelete(holder.ib_des);
            } else {
                showDownload(holder.ib_des, holder.getAdapterPosition());
                if (CastPlayBackManager.get(context).getCastingEid().equals(Animes.get(holder.getAdapterPosition()).getEid())) {
                    showCastPlay(holder.ib_ver);
                } else {
                    showCloudPlay(holder.ib_ver);
                }
            }
        }
        if (MainStates.isProcessing()) {
            if (MainStates.getProcessingEid().equals(Animes.get(holder.getAdapterPosition()).getEid())) {
                showLoading(holder.ib_des);
            }
        }
        if (MainStates.init(context).WaitContains(Animes.get(holder.getAdapterPosition()).getEid())) {
            if (!FileUtil.init(context).ExistAnime(Animes.get(holder.getAdapterPosition()).getEid())) {
                showCloudPlay(holder.ib_ver);
                holder.ib_des.setImageResource(R.drawable.ic_waiting);
            } else {
                showPlay(holder.ib_ver);
                showDelete(holder.ib_des);
                MainStates.init(context).delFromWaitList(Animes.get(holder.getAdapterPosition()).getEid());
            }
        }
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UpdateUtil.getState() == UpdateState.WAITING_TO_UPDATE) {
                    Toaster.toast("Actualizacion descargada, instalar para continuar");
                } else {
                    try {
                        if (!MainStates.isListing()) {
                            InfoHelper.open(
                                    context,
                                    new InfoHelper.SharedItem(holder.iv_main, "img"),
                                    new InfoHelper.BundleItem("aid", Animes.get(holder.getAdapterPosition()).getAid()),
                                    new InfoHelper.BundleItem("title", Animes.get(holder.getAdapterPosition()).getTitulo())
                            );
                        } else {
                            MainStates.setListing(false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MainStates.setListing(true);
                if (MainStates.init(context).WaitContains(Animes.get(holder.getAdapterPosition()).getEid())) {
                    MainStates.init(context).delFromWaitList(Animes.get(holder.getAdapterPosition()).getEid());
                    holder.ib_des.setImageResource(R.drawable.ic_get_r);
                    callbacks.onDelFromList();
                } else {
                    if (!FileUtil.init(context).ExistAnime(Animes.get(holder.getAdapterPosition()).getEid()) && !ManageDownload.isDownloading(context, Animes.get(holder.getAdapterPosition()).getEid())) {
                        MainStates.init(context).addToWaitList(Animes.get(holder.getAdapterPosition()).getEid());
                        holder.ib_des.setImageResource(R.drawable.ic_waiting);
                        callbacks.onPutInList();
                    } else {
                        MainStates.setListing(false);
                    }
                }
                return false;
            }
        });
        holder.ib_des.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UpdateUtil.getState() == UpdateState.WAITING_TO_UPDATE) {
                    Toaster.toast("Actualizacion descargada, instalar para continuar");
                } else {
                    if (!FileUtil.init(context).ExistAnime(Animes.get(holder.getAdapterPosition()).getEid()) && !ManageDownload.isDownloading(context, Animes.get(holder.getAdapterPosition()).getEid())) {
                        if (!MainStates.isProcessing()) {
                            if (MainStates.init(context).WaitContains(Animes.get(holder.getAdapterPosition()).getEid())) {
                                final int pos = holder.getAdapterPosition();
                                new MaterialDialog.Builder(context)
                                        .content(
                                                "El " + getCap(Animes.get(pos).getNumero()).toLowerCase() +
                                                        " de " + Animes.get(pos).getTitulo() +
                                                        " se encuentra en lista de espera, si continua, sera removido de la lista, desea continuar?")
                                        .autoDismiss(true)
                                        .positiveText("Continuar")
                                        .negativeText("Cancelar")
                                        .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                MainStates.init(context).delFromWaitList(Animes.get(pos).getEid());
                                                MainStates.setProcessing(true, Animes.get(holder.getAdapterPosition()).getEid());
                                                showLoading(holder.ib_des);
                                                searchDownload(holder);

                                            }
                                        })
                                        .build().show();
                            } else {
                                MainStates.setProcessing(true, Animes.get(holder.getAdapterPosition()).getEid());
                                showLoading(holder.ib_des);
                                searchDownload(holder);
                            }
                        } else {
                            Toaster.toast("Procesando");
                        }
                    } else {
                        MaterialDialog borrar = new MaterialDialog.Builder(context)
                                .title("Eliminar")
                                .titleGravity(GravityEnum.CENTER)
                                .content("Desea eliminar el " + getCap(Animes.get(holder.getAdapterPosition()).getNumero()).toLowerCase() + " de " + Animes.get(holder.getAdapterPosition()).getTitulo() + "?")
                                .positiveText("Eliminar")
                                .negativeText("Cancelar")
                                .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        if (FileUtil.init(context).DeleteAnime(Animes.get(holder.getAdapterPosition()).getEid())) {
                                            ManageDownload.cancel(context, Animes.get(holder.getAdapterPosition()).getEid());
                                            showDownload(holder.ib_des, holder.getAdapterPosition());
                                            showCloudPlay(holder.ib_ver);
                                            Toaster.toast("Archivo Eliminado");
                                        } else {
                                            if (!FileUtil.init(context).ExistAnime(Animes.get(holder.getAdapterPosition()).getEid())) {
                                                if (ManageDownload.isDownloading(context, Animes.get(holder.getAdapterPosition()).getEid())) {
                                                    ManageDownload.cancel(context, Animes.get(holder.getAdapterPosition()).getEid());
                                                }
                                                showDownload(holder.ib_des, holder.getAdapterPosition());
                                                showCloudPlay(holder.ib_ver);
                                                Toaster.toast("Archivo Eliminado");
                                            } else {
                                                Toaster.toast("Error al Eliminar");
                                            }
                                        }
                                    }
                                })
                                .build();
                        borrar.show();
                    }
                }
            }
        });
        holder.ib_ver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UpdateUtil.getState() == UpdateState.WAITING_TO_UPDATE) {
                    Toaster.toast("Actualizacion descargada, instalar para continuar");
                } else {
                    if (FileUtil.init(context).ExistAnime(Animes.get(holder.getAdapterPosition()).getEid())) {
                        /*if (CastPlayBackManager.get(context).isDeviceConnected()){
                            String e=Animes.get(holder.getAdapterPosition()).getEid();
                            CastPlayBackManager.get(context).play(ServerManager.get().startStream(context,FileUtil.init(context).getFile(e)),e);
                        }else {
                            StreamManager.Play(context, Animes.get(holder.getAdapterPosition()).getEid());
                        }*/
                        StreamManager.Play(context, Animes.get(holder.getAdapterPosition()).getEid());
                    } else {
                        if (ManageDownload.isDownloading(context, Animes.get(holder.getAdapterPosition()).getEid())) {
                            Toaster.toast("Descarga en proceso");
                        } else {
                            if (NetworkUtils.isNetworkAvailable()) {
                                if (!MainStates.isProcessing()) {
                                    if (MainStates.init(context).WaitContains(Animes.get(holder.getAdapterPosition()).getEid())) {
                                        final int pos = holder.getAdapterPosition();
                                        new MaterialDialog.Builder(context)
                                                .content(
                                                        "El " + getCap(Animes.get(pos).getNumero()).toLowerCase() +
                                                                " de " + Animes.get(pos).getTitulo() +
                                                                " se encuentra en lista de espera, si continua, sera removido de la lista, desea continuar?")
                                                .autoDismiss(true)
                                                .positiveText("Continuar")
                                                .negativeText("Cancelar")
                                                .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        MainStates.init(context).delFromWaitList(Animes.get(pos).getEid());
                                                        MainStates.setProcessing(true, Animes.get(holder.getAdapterPosition()).getEid());
                                                        showLoading(holder.ib_des);
                                                        searchStream(holder);
                                                    }
                                                })
                                                .build().show();
                                    } else {
                                        MainStates.setProcessing(true, Animes.get(holder.getAdapterPosition()).getEid());
                                        showLoading(holder.ib_des);
                                        searchStream(holder);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }


    private String getCap(String numero) {
        if (numero.equals("0")) {
            return "Preestreno";
        } else {
            return "Capítulo " + numero;
        }
    }

    private void showLoading(final ImageButton button) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.setImageResource(R.drawable.ic_warning);
                button.setEnabled(false);
            }
        });
    }

    private void showDownload(final ImageButton button, final int position) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (SeenManager.get(context).isSeen(Animes.get(position).getEid())) {
                        button.setImageResource(R.drawable.listo);
                        button.setEnabled(true);
                    } else {
                        button.setImageResource(R.drawable.ic_get_r);
                        button.setEnabled(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showDelete(final ImageButton button) {
        context.runOnUiThread(new Runnable() {
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
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.setImageResource(R.drawable.ic_cloud_play);
            }
        });
    }

    private void showPlay(final ImageButton button) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.setImageResource(R.drawable.ic_play);
            }
        });
    }

    private String getCap(int position) {
        MainAnimeModel model = Animes.get(position);
        String res = "";
        switch (model.getTipo()) {
            case "Anime":
                res = "Capítulo " + model.getNumero();
                break;
            case "OVA":
                res = "OVA " + model.getNumero();
                break;
            case "Pelicula":
                res = "Pelicula";
                break;
        }
        return res;
    }

    private void setUpWeb(final WebView web) {
        web.getSettings().setJavaScriptEnabled(true);
        web.addJavascriptInterface(new JavaScriptInterface(context), "HtmlViewer");
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.contains("zippyshare.com") || url.contains("blank")) {
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
                String eid = fileName.replace(".mp4", "") + "E";
                if (MainStates.getDowloadTask() == DownloadTask.DESCARGA) {
                    if (!FileUtil.init(context).ExistAnime(eid) && MainStates.isProcessing()) {
                        showDelete(MainStates.getGifDownButton());
                        showPlay(MainStates.getDownStateButton());
                        String urlD = MainStates.getUrlZippy();
                        CookieManager cookieManager = CookieManager.getInstance();
                        String cookie = cookieManager.getCookie(url.substring(0, url.indexOf("/", 8)));
                        CookieConstructor constructor = new CookieConstructor(cookie, web.getSettings().getUserAgentString(), urlD);
                        ManageDownload.chooseDownDir(context, eid, url, constructor);
                        web.loadUrl("about:blank");
                    } else {
                        showDelete(MainStates.getGifDownButton());
                        showPlay(MainStates.getDownStateButton());
                        web.loadUrl("about:blank");
                    }
                }
                if (MainStates.getDowloadTask() == DownloadTask.STREAMING) {
                    int type = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_streaming", "0"));
                    String urlD = MainStates.getUrlZippy();
                    CookieManager cookieManager = CookieManager.getInstance();
                    String cookie = cookieManager.getCookie(url.substring(0, url.indexOf("/", 8)));
                    CookieConstructor constructor = new CookieConstructor(cookie, web.getSettings().getUserAgentString(), urlD);
                    showDownload(MainStates.getGifDownButton(), MainStates.getPosition());
                    web.loadUrl("about:blank");
                    if (type == 1) {
                        StreamManager.mx(context).Stream(eid, url, constructor);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            StreamManager.internal(context).Stream(eid, url, constructor);
                        } else {
                            if (FileUtil.init(context).isMXinstalled()) {
                                Toaster.toast("Version de android por debajo de lo requerido, reproduciendo en MXPlayer");
                                StreamManager.mx(context).Stream(eid, url, constructor);
                            } else {
                                Toaster.toast("No hay reproductor adecuado disponible");
                            }
                        }
                    }
                }
                MainStates.setProcessing(false, null);
            }
        });
        web.loadUrl(parser.getBaseUrl(TaskType.NORMAL, context).replace("api2.", ""));
    }

    public void setData(List<MainAnimeModel> data) {
        Animes = new ArrayList<>();
        Animes.addAll(data);
        notifyDataSetChanged();
    }

    private void searchDownload(final AdapterMainNoGIF.ViewHolder holder) {
        DownloadGetter.search(context, Animes.get(holder.getAdapterPosition()).getEid(), new knf.animeflv.JsonFactory.DownloadGetter.ActionsInterface() {
            @Override
            public boolean isStream() {
                return false;
            }

            @Override
            public void onStartDownload() {
                MainStates.setProcessing(false, null);
                showDelete(holder.ib_des);
                showPlay(holder.ib_ver);
            }

            @Override
            public void onStartZippy(final String url) {
                MainStates.setZippyState(DownloadTask.DESCARGA, url, holder.ib_des, holder.ib_ver, holder.getAdapterPosition());
                holder.webView.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.webView.loadUrl(url);
                    }
                });
            }

            @Override
            public void onCancelDownload() {
                MainStates.setProcessing(false, null);
                showDownload(holder.ib_des, holder.getAdapterPosition());
            }

            @Override
            public void onStartCasting() {

            }

            @Override
            public void onLogError(Exception e) {
                Logger.Error(AdapterMainNoGIF.this.getClass(), e);
            }
        });
    }

    private void searchStream(final AdapterMainNoGIF.ViewHolder holder) {
        DownloadGetter.search(context, Animes.get(holder.getAdapterPosition()).getEid(), new knf.animeflv.JsonFactory.DownloadGetter.ActionsInterface() {
            @Override
            public boolean isStream() {
                return true;
            }

            @Override
            public void onStartDownload() {
                MainStates.setProcessing(false, null);
                showDownload(holder.ib_des, holder.getAdapterPosition());
            }

            @Override
            public void onStartZippy(final String url) {
                MainStates.setZippyState(DownloadTask.STREAMING, url, holder.ib_des, holder.ib_ver, holder.getAdapterPosition());
                holder.webView.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.webView.loadUrl(url);
                    }
                });
            }

            @Override
            public void onCancelDownload() {
                MainStates.setProcessing(false, null);
                showDownload(holder.ib_des, holder.getAdapterPosition());
                showCloudPlay(holder.ib_ver);
            }

            @Override
            public void onStartCasting() {
                MainStates.setProcessing(false, null);
                showDownload(holder.ib_des, holder.getAdapterPosition());
                showCastPlay(holder.ib_ver);
            }

            @Override
            public void onLogError(Exception e) {
                Logger.Error(AdapterMainNoGIF.this.getClass(), e);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Animes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_main;
        public TextView tv_tit;
        public TextView tv_num;
        public CardView card;
        public ImageButton ib_ver;
        public ImageButton ib_des;
        public WebView webView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.iv_main = (ImageView) itemView.findViewById(R.id.img_main);
            this.tv_tit = (TextView) itemView.findViewById(R.id.tv_main_Tit);
            this.tv_num = (TextView) itemView.findViewById(R.id.tv_main_Cap);
            this.card = (CardView) itemView.findViewById(R.id.card_main);
            this.ib_ver = (ImageButton) itemView.findViewById(R.id.ib_main_ver);
            this.ib_des = (ImageButton) itemView.findViewById(R.id.ib_main_descargar);
            this.webView = (WebView) itemView.findViewById(R.id.wv_main);
        }
    }

    private class JavaScriptInterface {
        private Context ctx;

        JavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }

        @JavascriptInterface
        public void showHTML(String html) {
            String s_html_i = html.substring(21);
            String s_html_f = "{" + s_html_i.substring(0, s_html_i.length() - 7);
        }
    }

}