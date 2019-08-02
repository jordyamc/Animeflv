package knf.animeflv.Recyclers;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.github.captain_miao.optroundcardview.OptRoundCardView;

import java.io.File;
import java.util.List;

import es.munix.multidisplaycast.CastControlsActivity;
import knf.animeflv.ColorsRes;
import knf.animeflv.DownloadManager.CookieConstructor;
import knf.animeflv.DownloadManager.ManageDownload;
import knf.animeflv.DownloadService.ServerHolder;
import knf.animeflv.JsonFactory.DownloadGetter;
import knf.animeflv.Parser;
import knf.animeflv.PlayBack.CastPlayBackManager;
import knf.animeflv.R;
import knf.animeflv.Seen.SeenManager;
import knf.animeflv.StreamManager.StreamManager;
import knf.animeflv.Utils.DesignUtils;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.Logger;
import knf.animeflv.Utils.MainStates;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.UpdateUtil;
import knf.animeflv.Utils.eNums.DownloadTask;
import knf.animeflv.Utils.eNums.UpdateState;
import knf.animeflv.zippy.zippyHelper;
import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 08/08/2015.
 */
public class AdapterInfoCapsMaterial extends RecyclerView.Adapter<AdapterInfoCapsMaterial.ViewHolder> implements SectionTitleProvider {

    private List<String> capitulo;
    private String id;
    private List<String> eids;
    private String ext_storage_state = Environment.getExternalStorageState();
    private Boolean streaming = false;
    private Activity context;
    private ThemeUtils.Theme theme;

    private Handler handler = new Handler();

    public AdapterInfoCapsMaterial(Activity context, List<String> capitulos, String aid, List<String> eid) {
        this.capitulo = capitulos;
        this.context = context;
        this.id = aid;
        this.eids = eid;
        this.theme = ThemeUtils.Theme.create(context);
    }

    @Override
    public AdapterInfoCapsMaterial.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).
                inflate(R.layout.item_anime_descarga, parent, false);
        return new AdapterInfoCapsMaterial.ViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(final AdapterInfoCapsMaterial.ViewHolder holder, final int position) {
        DesignUtils.setCardStyle(context, getItemCount(), getPosition(holder, position), holder.card, holder.separator, null);
        SetUpWeb(holder.web, holder);
        if (FileUtil.init(context).ExistAnime(eids.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()))) {
            showDelete(holder.ib_des);
            showPlay(holder.ib_ver);
        } else {
            if (ManageDownload.isDownloading(context, eids.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()))) {
                showDelete(holder.ib_des);
                showPlay(holder.ib_ver);
            } else if (CastPlayBackManager.get(context).getCastingEid().equals(eids.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()))) {
                showDownload(holder.ib_des);
                showCastPlay(holder.ib_ver);
            } else {
                showCloudPlay(holder.ib_ver);
                showDownload(holder.ib_des);
            }
        }
        if (CastPlayBackManager.get(context).isCasting(eids.get(getPosition(holder, position))))
            showCastPlay(holder.ib_ver);
        holder.tv_capitulo.setText(capitulo.get(position));
        holder.tv_capitulo.setTextColor(theme.textColorNormal);
        holder.card.setCardBackgroundColor(theme.card_normal);
        holder.ib_des.setColorFilter(null);
        holder.ib_ver.setColorFilter(null);
        holder.ib_ver.setColorFilter(theme.iconFilter);
        holder.ib_des.setColorFilter(theme.iconFilter);
        if (SeenManager.get(context).isSeen(eids.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()))) {
            holder.tv_capitulo.setTextColor(getColor());
        }

        holder.ib_des.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MainStates.isListing()) {
                    if (UpdateUtil.getState() == UpdateState.WAITING_TO_UPDATE) {
                        Toaster.toast("Actualizacion descargada, instalar para continuar");
                    } else {
                        if (!MainStates.isProcessing()) {
                            if (!FileUtil.init(context).ExistAnime(eids.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition())) && !ManageDownload.isDownloading(context, eids.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()))) {
                                showLoading(holder.ib_des);
                                searchDownload(holder);
                            } else {
                                final String item = capitulo.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()).replace("Capítulo ", "").trim();
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
                                                FileUtil.init(context).DeleteAnime(eids.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()));
                                                showDownload(holder.ib_des);
                                                showCloudPlay(holder.ib_ver);
                                                ManageDownload.cancel(context, eids.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()));
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
                            if (CastPlayBackManager.get(context).isCasting(eids.get(getPosition(holder, position)))) {
                                CastControlsActivity.open(context, theme.accent);
                            } else if (FileUtil.init(context).ExistAnime(eids.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()))) {
                                holder.tv_capitulo.setTextColor(getColor());
                                StreamManager.Play(context, eids.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()));
                            } else if (ManageDownload.isDownloading(context, eids.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()))) {
                                Toaster.toast("Descarga en proceso");
                            } else {
                                showLoading(holder.ib_des);
                                searchStream(holder);
                            }
                        }
                    }
                }
            }
        });
        holder.ib_ver.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String eid = eids.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition());
                if (FileUtil.init(context).ExistAnime(eid) && CastPlayBackManager.get(context).isDeviceConnected() && !CastPlayBackManager.get(context).isCasting(eids.get(getPosition(holder, position)))) {
                    showDelete(holder.ib_des);
                    showCastPlay(holder.ib_ver);
                    ServerHolder.getInstance().startServer(context, FileUtil.init(context).getSimpleFile(eid), eid);
                    MainStates.setProcessing(false, null);
                    refresh();
                    return true;
                } else {
                    return false;
                }
            }
        });
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!FileUtil.init(context).isInSeen(eids.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()))) {
                        FileUtil.init(context).setSeenState(eids.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()), true);
                        holder.tv_capitulo.setTextColor(getColor());
                    } else {
                        FileUtil.init(context).setSeenState(eids.get(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition()), false);
                        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("is_amoled", false)) {
                            holder.tv_capitulo.setTextColor(context.getResources().getColor(R.color.blanco));
                        } else {
                            holder.tv_capitulo.setTextColor(context.getResources().getColor(R.color.black));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void refresh() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    private int getPosition(RecyclerView.ViewHolder holder, int position) {
        return holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return capitulo.size();
    }

    private void showLoading(final ImageButton button) {
        MainStates.setProcessing(true, null);
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.setImageResource(R.drawable.ic_warning);
                button.setEnabled(false);
            }
        });
    }

    private void showDownload(final ImageButton button) {
        MainStates.setProcessing(false, null);
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.setImageResource(R.drawable.ic_get_r);
                button.setEnabled(true);
            }
        });
    }

    private void showDelete(final ImageButton button) {
        MainStates.setProcessing(false, null);
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

    @ColorInt
    private int getColor() {
        return theme.accent;
    }

    private void searchDownload(final ViewHolder holder) {
        try {
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
                    zippyHelper.calculate(url, new zippyHelper.OnZippyResult() {
                        @Override
                        public void onSuccess(zippyHelper.zippyObject object) {
                            MainStates.setProcessing(false, null);
                            showDelete(holder.ib_des);
                            showPlay(holder.ib_ver);
                            ManageDownload.chooseDownDir(context, eids.get(holder.getAdapterPosition()), object.download_url, object.cookieConstructor);
                        }

                        @Override
                        public void onError() {
                            Toaster.toast("Error al obtener link, reintentando en modo nativo");
                            MainStates.setZippyState(DownloadTask.DESCARGA, url, holder.ib_des, holder.ib_ver, holder.getAdapterPosition());
                            holder.web.post(new Runnable() {
                                @Override
                                public void run() {
                                    holder.web.loadUrl(url);
                                }
                            });
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    MainStates.setProcessing(false, null);
                                    showDownload(holder.ib_des);
                                }
                            }, 5000);
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

                }

                @Override
                public void onLogError(Exception e) {
                    Logger.Error(AdapterInfoCapsMaterial.this.getClass(), e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            MainStates.setProcessing(false, null);
            showDownload(holder.ib_des);
        }
    }

    private void searchStream(final ViewHolder holder) {
        try {
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
                    zippyHelper.calculate(url, new zippyHelper.OnZippyResult() {
                        @Override
                        public void onSuccess(zippyHelper.zippyObject object) {
                            MainStates.setProcessing(false, null);
                            showDownload(holder.ib_des);
                            int type = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_streaming", "0"));
                            if (type == 1) {
                                StreamManager.mx(context).Stream(eids.get(holder.getAdapterPosition()), object.download_url, object.cookieConstructor);
                            } else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    StreamManager.internal(context).Stream(eids.get(holder.getAdapterPosition()), object.download_url, object.cookieConstructor);
                                } else {
                                    if (FileUtil.init(context).isMXinstalled()) {
                                        Toaster.toast("Version de android por debajo de lo requerido, reproduciendo en MXPlayer");
                                        StreamManager.mx(context).Stream(eids.get(holder.getAdapterPosition()), object.download_url, object.cookieConstructor);
                                    } else {
                                        Toaster.toast("No hay reproductor adecuado disponible");
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError() {
                            Toaster.toast("Error al obtener link, reintentando en modo nativo");
                            MainStates.setZippyState(DownloadTask.STREAMING, url, holder.ib_des, holder.ib_ver, holder.getAdapterPosition());
                            holder.web.post(new Runnable() {
                                @Override
                                public void run() {
                                    holder.web.loadUrl(url);
                                }
                            });
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
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showDownload(holder.ib_des);
                            showCastPlay(holder.ib_ver);
                        }
                    });
                }

                @Override
                public void onLogError(Exception e) {
                    Logger.Error(AdapterInfoCapsMaterial.this.getClass(), e);
                }
            });
        } catch (Exception e) {
            MainStates.setProcessing(false, null);
            showDownload(holder.ib_des);
        }
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
        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                return true;
            }
        });
        web.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                handler.removeCallbacks(null);
                try {
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
                            try {
                                String item = capitulo.get(holder.getAdapterPosition()).replace("Capítulo ", "").trim();
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
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toaster.toast("Error al comenzar descarga");
                            }
                        } else {
                            Toast.makeText(context, "El archivo ya existe", Toast.LENGTH_SHORT).show();
                        }

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
                } catch (Exception e) {
                    e.printStackTrace();
                    Toaster.toast("Error al reproducir link");
                    showCloudPlay(holder.ib_ver);
                    showDownload(holder.ib_des);
                }
            }
        });
        web.loadUrl(Parser.getNormalUrl(context));
    }

    public void toast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getSectionTitle(int i) {
        String cap = capitulo.get(i).trim();
        return cap.substring(cap.lastIndexOf(" ") + 1);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_capitulo;
        public ImageButton ib_ver;
        public ImageButton ib_des;
        public OptRoundCardView card;
        public RecyclerView recyclerView;
        public WebView web;
        public View separator;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            this.tv_capitulo = itemView.findViewById(R.id.tv_cardD_capitulo);
            this.ib_ver = itemView.findViewById(R.id.ib_ver_rv);
            this.ib_des = itemView.findViewById(R.id.ib_descargar_rv);
            this.card = itemView.findViewById(R.id.card_descargas_info);
            this.web = itemView.findViewById(R.id.wv_anime_zippy);
            this.separator = itemView.findViewById(R.id.separator_top);
            DesignUtils.setCardSpaceStyle(context, card);
        }
    }

}
