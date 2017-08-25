package knf.animeflv.Recyclers;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.captain_miao.optroundcardview.OptRoundCardView;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.munix.multidisplaycast.CastControlsActivity;
import knf.animeflv.ColorsRes;
import knf.animeflv.DownloadManager.CookieConstructor;
import knf.animeflv.DownloadManager.ManageDownload;
import knf.animeflv.DownloadService.ServerHolder;
import knf.animeflv.Interfaces.MainRecyclerCallbacks;
import knf.animeflv.JsonFactory.DownloadGetter;
import knf.animeflv.Parser;
import knf.animeflv.PlayBack.CastPlayBackManager;
import knf.animeflv.R;
import knf.animeflv.Recientes.MainAnimeModel;
import knf.animeflv.Seen.SeenManager;
import knf.animeflv.StreamManager.StreamManager;
import knf.animeflv.TV.TvUtils;
import knf.animeflv.Utils.CacheManager;
import knf.animeflv.Utils.DesignUtils;
import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.Logger;
import knf.animeflv.Utils.MainStates;
import knf.animeflv.Utils.NetworkUtils;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.UpdateUtil;
import knf.animeflv.Utils.eNums.DownloadTask;
import knf.animeflv.Utils.eNums.UpdateState;
import knf.animeflv.info.Helper.InfoHelper;
import knf.animeflv.zippy.zippyHelper;
import xdroid.toaster.Toaster;


public class AdapterMainNoGIF extends RecyclerView.Adapter<AdapterMainNoGIF.ViewHolder> {

    private final int UPDATE_FREQUENCY = 3000;
    private Activity context;
    private List<MainAnimeModel> Animes = new ArrayList<>();
    private MainRecyclerCallbacks callbacks;
    private Handler handler;
    private ThemeUtils.Theme theme;

    public AdapterMainNoGIF(Activity context) {
        this.context = context;
        this.callbacks = (MainRecyclerCallbacks) context;
        this.theme = ThemeUtils.Theme.create(context);
    }

    public AdapterMainNoGIF(Activity context, List<MainAnimeModel> data) {
        this.context = context;
        this.callbacks = (MainRecyclerCallbacks) context;
        this.Animes = data;
        this.theme = ThemeUtils.Theme.create(context);
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
                inflate(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("force_phone", false) ? R.layout.item_main_ng_force : R.layout.item_main_ng, parent, false);
        return new AdapterMainNoGIF.ViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(final AdapterMainNoGIF.ViewHolder holder, final int position) {
        holder.card.setCardBackgroundColor(theme.card_normal);
        holder.tv_tit.setTextColor(theme.textColorCard);
        holder.tv_num.setTextColor(theme.accent);
        holder.ib_ver.setColorFilter(theme.iconFilter);
        holder.ib_des.setColorFilter(theme.iconFilter);
        TvUtils.setFocusable(context, holder.ib_ver, holder.ib_des);
        DesignUtils.setCardStyle(context, getItemCount(), getPosition(holder.getAdapterPosition(), position), holder.card, holder.separator, holder.iv_main);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            holder.progressBar.getProgressDrawable().setColorFilter(theme.accent, PorterDuff.Mode.SRC_ATOP);
        MainAnimeModel.Type type = Animes.get(getPosition(holder.getAdapterPosition(), position)).getType();
        Boolean resaltar = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("resaltar", true);
        if (resaltar && type != MainAnimeModel.Type.NORMAL) {
            holder.card.setCardBackgroundColor(type == MainAnimeModel.Type.FAV ? theme.card_fav : theme.card_new);
        } else {
            holder.card.setCardBackgroundColor(theme.card_normal);
        }
        setUpWeb(holder.webView);
        new CacheManager().mini(context, Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getAid(), holder.iv_main);
        holder.tv_tit.setText(Animes.get(position).getTitulo());
        holder.tv_num.setText(getCap(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()));
        if (FileUtil.init(context).ExistAnime(Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid())
                || ManageDownload.isDownloading(context, Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid())) {
            showPlay(holder.ib_ver);
            showDelete(holder.ib_des);
            if (ManageDownload.getDownloadSate(context, Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid()) == DownloadManager.STATUS_PAUSED) {
                showDownload(holder.ib_des, holder.getAdapterPosition());
                showCloudPlay(holder.ib_ver);
            }
        } else {
            showDownload(holder.ib_des, holder.getAdapterPosition());
            if (CastPlayBackManager.get(context).isCasting(Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid())) {
                showCastPlay(holder.ib_ver);
            } else {
                showCloudPlay(holder.ib_ver);
            }
        }
        if (MainStates.isProcessing()) {
            if (MainStates.getProcessingEid().equals(Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid())) {
                showLoading(holder.ib_des);
            }
        }
        if (MainStates.init(context).WaitContains(Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid())) {
            if (!FileUtil.init(context).ExistAnime(Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid())) {
                showCloudPlay(holder.ib_ver);
                holder.ib_des.setImageResource(R.drawable.ic_waiting);
            } else {
                showPlay(holder.ib_ver);
                showDelete(holder.ib_des);
                MainStates.init(context).delFromWaitList(Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid());
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
                                    new InfoHelper.BundleItem("aid", Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getAid()),
                                    new InfoHelper.BundleItem("title", Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getTitulo())
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
                if (MainStates.init(context).WaitContains(Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid())) {
                    MainStates.init(context).delFromWaitList(Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid());
                    holder.ib_des.setImageResource(R.drawable.ic_get_r);
                    callbacks.onDelFromList();
                } else {
                    if (!FileUtil.init(context).ExistAnime(Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid()) && !ManageDownload.isDownloading(context, Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid())) {
                        MainStates.init(context).addToWaitList(Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid());
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
                    if (!FileUtil.init(context).ExistAnime(Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid()) && !ManageDownload.isDownloading(context, Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid())
                            || ManageDownload.getDownloadSate(context, Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid()) == DownloadManager.STATUS_PAUSED) {
                        if (!MainStates.isProcessing()) {
                            if (MainStates.init(context).WaitContains(Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid())) {
                                final int pos = holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition();
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
                                                MainStates.setProcessing(true, Animes.get(pos).getEid());
                                                showLoading(holder.ib_des);
                                                searchDownload(holder, position);

                                            }
                                        })
                                        .build().show();
                            } else {
                                MainStates.setProcessing(true, Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid());
                                showLoading(holder.ib_des);
                                searchDownload(holder, position);
                            }
                        } else {
                            Toaster.toast("Procesando");
                        }
                    } else {
                        MaterialDialog borrar = new MaterialDialog.Builder(context)
                                .title("Eliminar")
                                .titleGravity(GravityEnum.CENTER)
                                .content("Desea eliminar el " + getCap(Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getNumero()).toLowerCase() + " de " + Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getTitulo() + "?")
                                .positiveText("Eliminar")
                                .negativeText("Cancelar")
                                .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        try {
                                            if (FileUtil.init(context).DeleteAnime(Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid())) {
                                                ManageDownload.cancel(context, Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid());
                                                showDownload(holder.ib_des, holder.getAdapterPosition());
                                                showCloudPlay(holder.ib_ver);
                                                Toaster.toast("Archivo Eliminado");
                                                cancelDownload(holder);
                                            } else {
                                                if (!FileUtil.init(context).ExistAnime(Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid())) {
                                                    if (ManageDownload.isDownloading(context, Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid())) {
                                                        ManageDownload.cancel(context, Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid());
                                                    }
                                                    showDownload(holder.ib_des, holder.getAdapterPosition());
                                                    showCloudPlay(holder.ib_ver);
                                                    Toaster.toast("Archivo Eliminado");
                                                    cancelDownload(holder);
                                                } else {
                                                    Toaster.toast("Error al Eliminar");
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Toaster.toast("Error al Eliminar");
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
                try {
                    if (UpdateUtil.getState() == UpdateState.WAITING_TO_UPDATE) {
                        Toaster.toast("Actualizacion descargada, instalar para continuar");
                    } else {
                        if (CastPlayBackManager.get(context).isCasting(Animes.get(getPosition(holder.getAdapterPosition(), position)).getEid())) {
                            context.startActivity(new Intent(context, CastControlsActivity.class));
                        } else if (FileUtil.init(context).ExistAnime(Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid())) {
                            StreamManager.Play(context, Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid());
                        } else {
                            if (ManageDownload.isDownloading(context, Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid())) {
                                Toaster.toast("Descarga en proceso");
                            } else {
                                if (NetworkUtils.isNetworkAvailable()) {
                                    if (!MainStates.isProcessing()) {
                                        if (MainStates.init(context).WaitContains(Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid())) {
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
                                                            MainStates.setProcessing(true, Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid());
                                                            showLoading(holder.ib_des);
                                                            searchStream(holder, position);
                                                        }
                                                    })
                                                    .build().show();
                                        } else {
                                            MainStates.setProcessing(true, Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid());
                                            showLoading(holder.ib_des);
                                            searchStream(holder, position);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        holder.ib_ver.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String eid = Animes.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).getEid();
                if (FileUtil.init(context).ExistAnime(eid) && CastPlayBackManager.get(context).isDeviceConnected() && !CastPlayBackManager.get(context).isCasting(eid)) {
                    showDelete(holder.ib_des);
                    showCastPlay(holder.ib_ver);
                    ServerHolder.getInstance().startServer(context, FileUtil.init(context).getSimpleFile(eid), eid);
                    MainStates.setProcessing(false, null);
                    refresh();
                }
                return true;
            }
        });
        if (ManageDownload.isDownloading(context, Animes.get(holder.getAdapterPosition()).getEid()) && showProgress()) {
            startDownload(holder);
        } else {
            holder.progressBar.setVisibility(View.GONE);
        }
    }

    private void refresh() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    private boolean showProgress() {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("prog_rec", true);
    }


    private void cancelDownload(final AdapterMainNoGIF.ViewHolder holder) {
        getHandler().removeCallbacks(null);
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                holder.progressBar.setVisibility(View.GONE);
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
    }

    private void startDownload(final AdapterMainNoGIF.ViewHolder holder) {
        if (showProgress()) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    holder.progressBar.setVisibility(View.VISIBLE);
                }
            });
            getHandler().post(getProgressRunnable(holder));
        }
    }

    private Handler getHandler() {
        if (handler == null) handler = new Handler();
        return handler;
    }

    private Runnable getProgressRunnable(final AdapterMainNoGIF.ViewHolder holder) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                switch (ManageDownload.getDownloadSate(context, Animes.get(holder.getAdapterPosition()).getEid())) {
                                    case DownloadManager.STATUS_RUNNING:
                                        final int progress = ManageDownload.getProgress(context, Animes.get(holder.getAdapterPosition()).getEid());
                                        if (progress != -1) {
                                            context.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    holder.progressBar.setIndeterminate(false);
                                                    holder.progressBar.setVisibility(View.VISIBLE);
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                        holder.progressBar.setProgress(progress, true);
                                                    } else {
                                                        holder.progressBar.setProgress(progress);
                                                    }
                                                }
                                            });
                                        } else {
                                            context.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    holder.progressBar.setVisibility(View.VISIBLE);
                                                    holder.progressBar.setIndeterminate(true);
                                                }
                                            });
                                        }
                                        setHandler(holder, UPDATE_FREQUENCY);
                                        break;
                                    case DownloadManager.STATUS_FAILED:
                                        context.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                holder.progressBar.setVisibility(View.GONE);
                                                notifyDataSetChanged();
                                            }
                                        });
                                        break;
                                    case DownloadManager.STATUS_SUCCESSFUL:
                                        context.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                holder.progressBar.setVisibility(View.GONE);
                                            }
                                        });
                                        break;
                                    case -1:
                                        setHandler(holder, UPDATE_FREQUENCY);
                                        break;
                                    default:
                                        context.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                holder.progressBar.setVisibility(View.VISIBLE);
                                                holder.progressBar.setIndeterminate(true);
                                            }
                                        });
                                        setHandler(holder, UPDATE_FREQUENCY);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }.executeOnExecutor(ExecutorManager.getExecutor());
                } catch (Exception e) {

                }
            }
        };
    }

    private void setHandler(AdapterMainNoGIF.ViewHolder holder, int time) {
        getHandler().postDelayed(getProgressRunnable(holder), time);
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
                    web.loadUrl("javascript:(" +
                            "function(){" +
                            "var down=document.getElementById('dlbutton').href;" +
                            "location.replace(down);" +
                            "})()");
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
        web.loadUrl(Parser.getNormalUrl(context));
    }

    public void setData(List<MainAnimeModel> data) {
        Animes = new ArrayList<>();
        Animes.addAll(data);
        notifyDataSetChanged();
    }

    private void searchDownload(final ViewHolder holder, final int position) {
        try {
            DownloadGetter.search(context, Animes.get(getPosition(holder.getAdapterPosition(), position)).getEid(), new DownloadGetter.ActionsInterface() {
                @Override
                public boolean isStream() {
                    return false;
                }

                @Override
                public void onStartDownload() {
                    MainStates.setProcessing(false, null);
                    showDelete(holder.ib_des);
                    showPlay(holder.ib_ver);
                    startDownload(holder);
                }

                @Override
                public void onStartZippy(final String url) {
                    zippyHelper.calculate(url, new zippyHelper.OnZippyResult() {
                        @Override
                        public void onSuccess(zippyHelper.zippyObject object) {
                            MainStates.setProcessing(false, null);
                            showDelete(holder.ib_des);
                            showPlay(holder.ib_ver);
                            ManageDownload.chooseDownDir(context, Animes.get(getPosition(holder.getAdapterPosition(), position)).getEid(), object.download_url, object.cookieConstructor);
                            startDownload(holder);
                        }

                        @Override
                        public void onError() {
                            Toaster.toast("Error al obtener link, reintentando en modo nativo");
                            MainStates.setZippyState(DownloadTask.DESCARGA, url, holder.ib_des, holder.ib_ver, getPosition(holder.getAdapterPosition(), position));
                            holder.webView.post(new Runnable() {
                                @Override
                                public void run() {
                                    holder.webView.loadUrl(url);
                                }
                            });
                        }
                    });
                }

                @Override
                public void onCancelDownload() {
                    MainStates.setProcessing(false, null);
                    showDownload(holder.ib_des, getPosition(holder.getAdapterPosition(), position));
                }

                @Override
                public void onStartCasting() {

                }

                @Override
                public void onLogError(Exception e) {
                    Logger.Error(AdapterMainNoGIF.this.getClass(), e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toaster.toast("Error inesperado, intente de nuevo!");
            MainStates.setProcessing(false, null);
            showDownload(holder.ib_des, getPosition(holder.getAdapterPosition(), position));
        }
    }

    private void searchStream(final ViewHolder holder, final int position) {
        try {
            DownloadGetter.search(context, Animes.get(getPosition(holder.getAdapterPosition(), position)).getEid(), new DownloadGetter.ActionsInterface() {
                @Override
                public boolean isStream() {
                    return true;
                }

                @Override
                public void onStartDownload() {
                    MainStates.setProcessing(false, null);
                    showDownload(holder.ib_des, getPosition(holder.getAdapterPosition(), position));
                }

                @Override
                public void onStartZippy(final String url) {
                    zippyHelper.calculate(url, new zippyHelper.OnZippyResult() {
                        @Override
                        public void onSuccess(zippyHelper.zippyObject object) {
                            MainStates.setProcessing(false, null);
                            showDownload(holder.ib_des, getPosition(holder.getAdapterPosition(), position));
                            int type = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_streaming", "0"));
                            if (type == 1) {
                                StreamManager.mx(context).Stream(Animes.get(getPosition(holder.getAdapterPosition(), position)).getEid(), object.download_url, object.cookieConstructor);
                            } else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    StreamManager.internal(context).Stream(Animes.get(getPosition(holder.getAdapterPosition(), position)).getEid(), object.download_url, object.cookieConstructor);
                                } else {
                                    if (FileUtil.init(context).isMXinstalled()) {
                                        Toaster.toast("Version de android por debajo de lo requerido, reproduciendo en MXPlayer");
                                        StreamManager.mx(context).Stream(Animes.get(getPosition(holder.getAdapterPosition(), position)).getEid(), object.download_url, object.cookieConstructor);
                                    } else {
                                        Toaster.toast("No hay reproductor adecuado disponible");
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError() {
                            Toaster.toast("Error al obtener link, reintentando en modo nativo");
                            MainStates.setZippyState(DownloadTask.STREAMING, url, holder.ib_des, holder.ib_ver, getPosition(holder.getAdapterPosition(), position));
                            holder.webView.post(new Runnable() {
                                @Override
                                public void run() {
                                    holder.webView.loadUrl(url);
                                }
                            });
                        }
                    });
                }

                @Override
                public void onCancelDownload() {
                    MainStates.setProcessing(false, null);
                    showDownload(holder.ib_des, getPosition(holder.getAdapterPosition(), position));
                    showCloudPlay(holder.ib_ver);
                }

                @Override
                public void onStartCasting() {
                    MainStates.setProcessing(false, null);
                    showDownload(holder.ib_des, getPosition(holder.getAdapterPosition(), position));
                    showCastPlay(holder.ib_ver);
                    resetIcon();
                }

                @Override
                public void onLogError(Exception e) {
                    Logger.Error(AdapterMainNoGIF.this.getClass(), e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toaster.toast("Error inesperado, intente de nuevo!");
            MainStates.setProcessing(false, null);
            showDownload(holder.ib_des, getPosition(holder.getAdapterPosition(), position));
            showCloudPlay(holder.ib_ver);
        }
    }

    private void resetIcon() {
        String eid = CastPlayBackManager.get(context).getCastingEid();
        int pos = AnimesContainsEid(eid);
        if (pos != -1) {
            notifyItemChanged(pos);
        }
    }

    private int AnimesContainsEid(String eid) {
        for (int i = 0; i < Animes.size(); i++) {
            if (Animes.get(i).getEid().equals(eid))
                return i;
        }
        return -1;
    }

    private int getPosition(int holder, int pos) {
        return holder == -1 ? pos : holder;
    }

    @Override
    public int getItemCount() {
        return Animes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_main)
        public RoundedImageView iv_main;
        @BindView(R.id.tv_main_Tit)
        public TextView tv_tit;
        @BindView(R.id.tv_main_Cap)
        public TextView tv_num;
        @BindView(R.id.card_main)
        public OptRoundCardView card;
        @BindView(R.id.ib_main_ver)
        public ImageButton ib_ver;
        @BindView(R.id.ib_main_descargar)
        public ImageButton ib_des;
        @BindView(R.id.wv_main)
        public WebView webView;
        @BindView(R.id.progress)
        ProgressBar progressBar;
        @BindView(R.id.separator_top)
        View separator;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("use_space", false))
                iv_main.setPadding(0, 0, 0, 0);
            DesignUtils.setCardSpaceStyle(context, card);
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