package knf.animeflv.Recyclers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;
import knf.animeflv.ColorsRes;
import knf.animeflv.DownloadManager.CookieConstructor;
import knf.animeflv.DownloadManager.ManageDownload;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.StreamManager.StreamManager;
import knf.animeflv.TaskType;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.MainStates;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.UpdateUtil;
import knf.animeflv.Utils.UrlUtils;
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
    Parser parser = new Parser();
    MaterialDialog dialog;
    MaterialDialog d;
    Boolean streaming = false;
    int posT;
    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
    private Context context;
    private int lastPosition = 0;

    public AdapterInfoCapsMaterial(Context context, List<String> capitulos, String aid, List<String> eid) {
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
        if (FileUtil.ExistAnime(eids.get(holder.getAdapterPosition()))) {
            showDelete(holder.ib_des);
            showPlay(holder.ib_ver);
        } else {
            showCloudPlay(holder.ib_ver);
            showDownload(holder.ib_des);
        }
        holder.tv_capitulo.setText(capitulo.get(position));
        Boolean vistos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getBoolean("visto" + id + "_" + item, false);
        holder.tv_capitulo.setTextColor(context.getResources().getColor(R.color.black));
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("is_amoled", false)) {
            holder.card.setCardBackgroundColor(Color.parseColor("#212121"));
            holder.tv_capitulo.setTextColor(context.getResources().getColor(R.color.blanco));
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
        if (MainStates.WaitContains(eids.get(holder.getAdapterPosition()))) {
            if (!FileUtil.ExistAnime(eids.get(holder.getAdapterPosition()))) {
                showCloudPlay(holder.ib_ver);
                holder.ib_des.setImageResource(R.drawable.ic_waiting);
            } else {
                showPlay(holder.ib_ver);
                showDelete(holder.ib_des);
                MainStates.delFromWaitList(eids.get(holder.getAdapterPosition()));
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
                            if (!MainStates.WaitContains(eids.get(holder.getAdapterPosition()))) {
                                if (!FileUtil.ExistAnime(eids.get(holder.getAdapterPosition()))) {
                                    showLoading(holder.ib_des);
                                    new CheckDown(holder.web, holder.ib_des, holder.ib_ver, holder.tv_capitulo, holder.getAdapterPosition(), new Parser().getUrlCached(id, item)).executeOnExecutor(threadPoolExecutor);
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
                                                    if (FileUtil.DeleteAnime(eids.get(holder.getAdapterPosition()))) {
                                                        showDownload(holder.ib_des);
                                                        showCloudPlay(holder.ib_ver);
                                                        ManageDownload.cancel(context, eids.get(holder.getAdapterPosition()));
                                                        Toast.makeText(context, "Archivo Eliminado", Toast.LENGTH_SHORT).show();
                                                    }
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
                                                MainStates.delFromWaitList(eids.get(holder.getAdapterPosition()));
                                                showLoading(holder.ib_des);
                                                new CheckDown(holder.web, holder.ib_des, holder.ib_ver, holder.tv_capitulo, holder.getAdapterPosition(), new Parser().getUrlCached(id, item)).executeOnExecutor(threadPoolExecutor);
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
                            if (!MainStates.WaitContains(eids.get(holder.getAdapterPosition()))) {
                                if (FileUtil.ExistAnime(eids.get(holder.getAdapterPosition()))) {
                                    holder.tv_capitulo.setTextColor(getColor());
                                    StreamManager.Play(context, eids.get(holder.getAdapterPosition()));
                                } else {
                                    showLoading(holder.ib_des);
                                    new CheckStream(holder.web, holder.tv_capitulo, holder.getAdapterPosition(), holder, new Parser().getUrlCached(id, item)).executeOnExecutor(threadPoolExecutor);
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
                                                MainStates.delFromWaitList(eids.get(holder.getAdapterPosition()));
                                                if (FileUtil.ExistAnime(eids.get(holder.getAdapterPosition()))) {
                                                    holder.tv_capitulo.setTextColor(getColor());
                                                    StreamManager.Play(context, eids.get(holder.getAdapterPosition()));
                                                } else {
                                                    showLoading(holder.ib_des);
                                                    new CheckStream(holder.web, holder.tv_capitulo, holder.getAdapterPosition(), holder, new Parser().getUrlCached(id, item)).executeOnExecutor(threadPoolExecutor);
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
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MainStates.isListing()) {
                    String item = capitulo.get(holder.getAdapterPosition()).replace("Capitulo ", "").trim();
                    Boolean vistos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getBoolean("visto" + id + "_" + item, false);
                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("cambio", true).apply();
                    if (!vistos) {
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("visto" + id + "_" + item, true).apply();
                        holder.tv_capitulo.setTextColor(getColor());
                    } else {
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("visto" + id + "_" + item, false).apply();
                        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("is_amoled", false)) {
                            holder.tv_capitulo.setTextColor(context.getResources().getColor(R.color.blanco));
                        } else {
                            holder.tv_capitulo.setTextColor(context.getResources().getColor(R.color.black));
                        }
                    }
                } else {
                    if (!FileUtil.ExistAnime(eids.get(holder.getAdapterPosition()))) {
                        if (MainStates.WaitContains(eids.get(holder.getAdapterPosition()))) {
                            MainStates.delFromWaitList(eids.get(holder.getAdapterPosition()));
                            showDownload(holder.ib_des);
                        } else {
                            MainStates.addToWaitList(eids.get(holder.getAdapterPosition()));
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

    private int getColor() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int accent = preferences.getInt("accentColor", ColorsRes.Naranja(context));
        int color = ColorsRes.Naranja(context);
        if (accent == ColorsRes.Rojo(context)) {
            color = ColorsRes.Rojo(context);
        }
        if (accent == ColorsRes.Naranja(context)) {
            color = ColorsRes.Naranja(context);
        }
        if (accent == ColorsRes.Gris(context)) {
            color = ColorsRes.Gris(context);
        }
        if (accent == ColorsRes.Verde(context)) {
            color = ColorsRes.Verde(context);
        }
        if (accent == ColorsRes.Rosa(context)) {
            color = ColorsRes.Rosa(context);
        }
        if (accent == ColorsRes.Morado(context)) {
            color = ColorsRes.Morado(context);
        }
        return color;
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
                            if (isMXinstalled()) {
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

    public boolean isMXinstalled() {
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = context.getPackageManager();
        packages = pm.getInstalledApplications(0);
        String pack = "null";
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals("com.mxtech.videoplayer.pro")) {
                pack = "com.mxtech.videoplayer.pro";
                break;
            }
            if (packageInfo.packageName.equals("com.mxtech.videoplayer.ad")) {
                pack = "com.mxtech.videoplayer.ad";
                break;
            }
        }
        return !pack.equals("null");
    }

    private String getCertificateSHA1Fingerprint() {
        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();
        int flags = PackageManager.GET_SIGNATURES;
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(packageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Signature[] signatures = packageInfo.signatures;
        byte[] cert = signatures[0].toByteArray();
        InputStream input = new ByteArrayInputStream(cert);
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        X509Certificate c = null;
        try {
            c = (X509Certificate) cf.generateCertificate(input);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        String hexString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(c.getEncoded());
            hexString = byte2HexFormatted(publicKey);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return hexString;
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

    public class CheckDown extends AsyncTask<String, String, String> {
        ImageButton des;
        ImageButton ver;
        WebView web;
        TextView cap;
        int pos;
        String _response;
        Spinner sp;
        String url;

        public CheckDown(WebView w, ImageButton ib_des, ImageButton ib_ver, TextView tv_capitulo, int position, String url) {
            this.des = ib_des;
            this.ver = ib_ver;
            this.cap = tv_capitulo;
            this.pos = position;
            this.web = w;
            this.url = url;
        }

        @Override
        protected String doInBackground(String... params) {
            new SyncHttpClient().get(new Parser().getInicioUrl(TaskType.NORMAL, context) + "?certificate=" + getCertificateSHA1Fingerprint() + "&url=" + url + "&newMain", null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    _response = response.toString();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    super.onSuccess(statusCode, headers, responseString);
                    _response = responseString;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    _response = "error";
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    _response = "error";
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    _response = "error";
                }
            });
            return _response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("error")) {
                dialog.dismiss();
                showDownload(des);
                Toast.makeText(context, "Error en servidor", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s.trim());
                    JSONArray jsonArray = jsonObject.getJSONArray("downloads");
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
                        d = new MaterialDialog.Builder(context)
                                .title("Opciones")
                                .titleGravity(GravityEnum.CENTER)
                                .customView(R.layout.dialog_down, false)
                                .cancelable(true)
                                .autoDismiss(false)
                                .positiveText("Descargar")
                                .negativeText("Cancelar")
                                .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction dialogAction) {
                                        String desc = nombres.get(sp.getSelectedItemPosition());
                                        final String ur = urls.get(sp.getSelectedItemPosition());
                                        Log.d("Descargar", "URL -> " + ur);
                                        switch (desc.toLowerCase()) {
                                            case "zippyshare":
                                                web.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        web.loadUrl(ur);
                                                    }
                                                });
                                                d.dismiss();
                                                break;
                                            case "mega":
                                                d.dismiss();
                                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ur)));
                                                showCloudPlay(ver);
                                                showDownload(des);
                                                break;
                                            default:
                                                ManageDownload.chooseDownDir(context, eids.get(pos), ur);
                                                showPlay(ver);
                                                showDelete(des);
                                                d.dismiss();
                                                break;
                                        }
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                        materialDialog.dismiss();
                                        showDownload(des);
                                    }
                                })
                                .cancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        showDownload(des);
                                        d.dismiss();
                                    }
                                })
                                .build();
                        sp = (Spinner) d.getCustomView().findViewById(R.id.spinner_down);
                        sp.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, nombres));
                        d.show();
                    } else {
                        Toaster.toast("No hay links!!! Intenta mas tarde!!!");
                        showDownload(des);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error en JSON", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class CheckStream extends AsyncTask<String, String, String> {
        WebView web;
        TextView cap;
        int pos;
        String _response;
        Spinner sp;
        AdapterInfoCapsMaterial.ViewHolder holder;
        String url;

        private CheckStream(WebView w, TextView tv_capitulo, int position, AdapterInfoCapsMaterial.ViewHolder holder, String url) {
            this.cap = tv_capitulo;
            this.pos = position;
            this.web = w;
            this.holder = holder;
            this.url = url;
        }

        @Override
        protected String doInBackground(String... params) {
            new SyncHttpClient().get(new Parser().getInicioUrl(TaskType.NORMAL, context) + "?certificate=" + getCertificateSHA1Fingerprint() + "&url=" + url + "&newMain", null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    _response = response.toString();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    super.onSuccess(statusCode, headers, responseString);
                    _response = responseString;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    _response = "error";
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    _response = "error";
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    _response = "error";
                }
            });
            return _response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("error")) {
                dialog.dismiss();
                Toast.makeText(context, "Error en servidor", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s.trim());
                    JSONArray jsonArray = jsonObject.getJSONArray("downloads");
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
                        d = new MaterialDialog.Builder(context)
                                .title("Opciones")
                                .titleGravity(GravityEnum.CENTER)
                                .customView(R.layout.dialog_down, false)
                                .cancelable(true)
                                .autoDismiss(false)
                                .positiveText("Reproducir")
                                .negativeText("Cancelar")
                                .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction dialogAction) {
                                        String desc = nombres.get(sp.getSelectedItemPosition());
                                        final String ur = urls.get(sp.getSelectedItemPosition());
                                        Log.d("Streaming", "URL -> " + ur);
                                        switch (desc.toLowerCase()) {
                                            case "zippyshare":
                                                streaming = true;
                                                posT = pos;
                                                web.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        web.loadUrl(ur);
                                                    }
                                                });
                                                d.dismiss();
                                                break;
                                            case "mega":
                                                d.dismiss();
                                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ur)));
                                                showDownload(holder.ib_des);
                                                showCloudPlay(holder.ib_ver);
                                                holder.tv_capitulo.setTextColor(ThemeUtils.getAcentColor(context));
                                                break;
                                            default:
                                                StreamManager.Stream(context, eids.get(holder.getAdapterPosition()), ur);
                                                holder.tv_capitulo.setTextColor(ThemeUtils.getAcentColor(context));
                                                showDownload(holder.ib_des);
                                                showCloudPlay(holder.ib_ver);
                                                d.dismiss();
                                                break;
                                        }
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                        showDownload(holder.ib_des);
                                        materialDialog.dismiss();
                                    }
                                })
                                .cancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        showDownload(holder.ib_des);
                                        d.dismiss();
                                    }
                                })
                                .build();
                        sp = (Spinner) d.getCustomView().findViewById(R.id.spinner_down);
                        sp.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, nombres));
                        d.show();
                    } else {
                        Toaster.toast("No hay links!!! Intenta mas tarde!!!");
                        showDownload(holder.ib_des);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error en JSON", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
