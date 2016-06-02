package knf.animeflv;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import knf.animeflv.DownloadManager.CookieConstructor;
import knf.animeflv.DownloadManager.ManageDownload;
import knf.animeflv.StreamManager.StreamManager;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 20/08/2015.
 */
public class BackDownloadDeep extends AppCompatActivity {
    String aid;
    String num;
    String titulo;
    String eid;
    String ext_storage_state = Environment.getExternalStorageState();
    File mediaStorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache");

    Parser parser = new Parser();
    Context context;
    String link;
    MaterialDialog d;
    Spinner sp;

    WebView web;
    WebView zippy;

    MaterialDialog options;

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
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int accent = preferences.getInt("accentColor", ColorsRes.Naranja(this));
        if (preferences.getBoolean("is_amoled", false)) {
            if (accent == ColorsRes.Rojo(this)) {
                setTheme(R.style.TranslucentDarkRojo);
            }
            if (accent == ColorsRes.Naranja(this)) {
                setTheme(R.style.TranslucentDarkNaranja);
            }
            if (accent == ColorsRes.Gris(this)) {
                setTheme(R.style.TranslucentDarkGris);
            }
            if (accent == ColorsRes.Verde(this)) {
                setTheme(R.style.TranslucentDarkVerde);
            }
            if (accent == ColorsRes.Rosa(this)) {
                setTheme(R.style.TranslucentDarkRosa);
            }
            if (accent == ColorsRes.Morado(this)) {
                setTheme(R.style.TranslucentDarkMorado);
            }
        } else {
            if (accent == ColorsRes.Rojo(this)) {
                setTheme(R.style.TranslucentRojo);
            }
            if (accent == ColorsRes.Naranja(this)) {
                setTheme(R.style.TranslucentNaranja);
            }
            if (accent == ColorsRes.Gris(this)) {
                setTheme(R.style.TranslucentGris);
            }
            if (accent == ColorsRes.Verde(this)) {
                setTheme(R.style.TranslucentVerde);
            }
            if (accent == ColorsRes.Rosa(this)) {
                setTheme(R.style.TranslucentRosa);
            }
            if (accent == ColorsRes.Morado(this)) {
                setTheme(R.style.TranslucentMorado);
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web);
        web = (WebView) findViewById(R.id.wv_global);
        zippy = (WebView) findViewById(R.id.wv_zippy);
        context = this;
        setupWeb();
        String url = getIntent().getDataString().replace("http://animeflv.net/ver/", "").replace(".html", "");
        String cortado = url.substring(0, url.lastIndexOf("-"));
        aid = parser.getAidCached(cortado);
        num = url.substring(url.lastIndexOf("-") + 1);
        titulo = parser.getTitCached(aid);
        eid = aid + "_" + num + "E";
        options = new MaterialDialog.Builder(this)
                .title("OPCIONES")
                .titleGravity(GravityEnum.CENTER)
                .content("Deseas descargar el capitulo " + num + " de " + titulo + "?")
                .positiveText("DESCARGAR")
                .negativeText("Streaming")
                .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                .cancelable(true)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (!animeExist()) {
                            new Check(context).execute(parser.getInicioUrl(TaskType.NORMAL, context) + "?url=" + parser.getUrlCached(aid, num));
                        } else {
                            options.dismiss();
                            Toaster.toast("El anime ya existe o se esta descargando");
                            finish();
                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (!animeExist()) {
                            new CheckStream(context).execute(parser.getInicioUrl(TaskType.NORMAL, context) + "?url=" + parser.getUrlCached(aid, num));
                        } else {
                            Toaster.toast("El anime ya existe, reproduciendo");
                            int type = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_video", "0"));
                            File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + aid, eid.replace("E", "") + ".mp4");
                            File sd = new File(FileUtil.getSDPath() + "/Animeflv/download/" + aid, eid.replace("E", "") + ".mp4");
                            switch (type) {
                                case 0:
                                    if (file.exists()) {
                                        StreamManager.internal(context).Play(eid, file);
                                    } else {
                                        if (sd.exists()) {
                                            StreamManager.internal(context).Play(eid, sd);
                                        }
                                    }
                                    break;
                                case 1:
                                    if (file.exists()) {
                                        StreamManager.external(context).Play(eid, file);
                                    } else {
                                        if (sd.exists()) {
                                            StreamManager.external(context).Play(eid, sd);
                                        }
                                    }
                                    break;
                            }
                            options.dismiss();
                            finish();
                        }
                    }
                }).build();
        options.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                options.dismiss();
                finish();
            }
        });
        options.show();
    }

    public boolean animeExist() {
        return new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + aid, eid.replace("E", "") + ".mp4").exists() || new File(FileUtil.getSDPath() + "/Animeflv/download/" + aid, eid.replace("E", "") + ".mp4").exists();
    }

    public void setupWeb() {
        web.getSettings().setJavaScriptEnabled(true);
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().startSync();
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                //web.loadUrl("javascript:"+"var num=e();"+"window.HtmlViewer.showHTMLD2(e());");
                if (!url.contains("api.php") && !url.contains("getHtml.php")) {
                    if (!url.contains("animeflv")) {
                        if (url.contains("zippyshare.com") || url.contains("blank")) {
                            web.loadUrl("javascript:("
                                    + "function(){var l=document.getElementById('dlbutton');" + "var f=document.createEvent('HTMLEvents');" + "f.initEvent('click',true,true);" + "l.dispatchEvent(f);}"
                                    + ")()");
                        } else {
                            if (!url.contains("izanagi.php")) {
                                if (url.contains("amazona")) {
                                    Log.d("Download", "Amazona");
                                    Descargar(url);
                                } else {
                                    toast("Error al descargar");
                                }
                            }
                        }
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //web.loadUrl("javascript:window.HtmlViewer.showHTMLD2(document.getElementsByTagName('body')[0].innerHTML);");
                            }
                        }, 10000);
                    }
                } else {
                    if (view.getUrl().contains("api.php?accion=anime")) {
                        web.loadUrl("javascript:window.HtmlViewer.HTMLInfo(document.getElementsByTagName('body')[0].innerHTML);");
                        CookieManager cookieManager = CookieManager.getInstance();
                        String cookies = cookieManager.getCookie(url);
                        getSharedPreferences("data", MODE_PRIVATE).edit().putString("cookies", cookies).apply();
                        //web.loadUrl("about:blank");
                    } else {
                        if (view.getUrl().contains("api.php?accion=inicio")) {
                            web.loadUrl("javascript:window.HtmlViewer.showHTMLD2(document.getElementsByTagName('body')[0].innerHTML);");
                            CookieManager cookieManager = CookieManager.getInstance();
                            String cookies = cookieManager.getCookie(url);
                            getSharedPreferences("data", MODE_PRIVATE).edit().putString("cookies", cookies).apply();
                            //web.loadUrl("about:blank");
                        } else {
                            if (!view.getUrl().contains("izanagi.php")) {
                                web.loadUrl("javascript:window.HtmlViewer.showHTMLD2(document.getElementsByTagName('body')[0].innerHTML);");
                            }
                        }
                    }
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
                File Dstorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("_")));
                if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                    if (!Dstorage.exists()) {
                        Dstorage.mkdirs();
                    }
                }
                File archivo = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("_")) + "/" + fileName);
                if (!archivo.exists()) {
                    String urlD = getSharedPreferences("data", MODE_PRIVATE).getString("urlD", null);
                    CookieManager cookieManager = CookieManager.getInstance();
                    String cookie = cookieManager.getCookie(url.substring(0, url.indexOf("/", 8)));
                    CookieConstructor constructor = new CookieConstructor(cookie, web.getSettings().getUserAgentString(), urlD);
                    Descargar(url, constructor);
                    web.loadUrl("about:blank");
                } else {
                    web.loadUrl("about:blank");
                }
            }
        });
        zippy.getSettings().setJavaScriptEnabled(true);
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().startSync();
        zippy.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                //web.loadUrl("javascript:"+"var num=e();"+"window.HtmlViewer.showHTMLD2(e());");
                if (!url.contains("api.php") && !url.contains("getHtml.php")) {
                    if (!url.contains("animeflv")) {
                        if (url.contains("zippyshare.com") || url.contains("blank")) {
                            zippy.loadUrl("javascript:("
                                    + "function(){var l=document.getElementById('dlbutton');" + "var f=document.createEvent('HTMLEvents');" + "f.initEvent('click',true,true);" + "l.dispatchEvent(f);}"
                                    + ")()");
                        } else {
                            if (!url.contains("izanagi.php")) {
                                if (url.contains("amazona")) {
                                    Log.d("Download", "Amazona");
                                    Descargar(url);
                                } else {
                                    toast("Error al descargar");
                                }
                            }
                        }
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //web.loadUrl("javascript:window.HtmlViewer.showHTMLD2(document.getElementsByTagName('body')[0].innerHTML);");
                            }
                        }, 10000);
                    }
                } else {
                    if (view.getUrl().contains("api.php?accion=anime")) {
                        zippy.loadUrl("javascript:window.HtmlViewer.HTMLInfo(document.getElementsByTagName('body')[0].innerHTML);");
                        CookieManager cookieManager = CookieManager.getInstance();
                        String cookies = cookieManager.getCookie(url);
                        getSharedPreferences("data", MODE_PRIVATE).edit().putString("cookies", cookies).apply();
                        //web.loadUrl("about:blank");
                    } else {
                        if (view.getUrl().contains("api.php?accion=inicio")) {
                            zippy.loadUrl("javascript:window.HtmlViewer.showHTMLD2(document.getElementsByTagName('body')[0].innerHTML);");
                            CookieManager cookieManager = CookieManager.getInstance();
                            String cookies = cookieManager.getCookie(url);
                            getSharedPreferences("data", MODE_PRIVATE).edit().putString("cookies", cookies).apply();
                            //web.loadUrl("about:blank");
                        } else {
                            if (!view.getUrl().contains("izanagi.php")) {
                                zippy.loadUrl("javascript:window.HtmlViewer.showHTMLD2(document.getElementsByTagName('body')[0].innerHTML);");
                            }
                        }
                    }
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        zippy.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                String fileName = url.substring(url.lastIndexOf("/") + 1);
                File Dstorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("_")));
                if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                    if (!Dstorage.exists()) {
                        Dstorage.mkdirs();
                    }
                }
                File archivo = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("_")) + "/" + fileName);
                if (!archivo.exists()) {
                    String urlD = getSharedPreferences("data", MODE_PRIVATE).getString("urlD", null);
                    CookieManager cookieManager = CookieManager.getInstance();
                    String cookie = cookieManager.getCookie(url.substring(0, url.indexOf("/", 8)));
                    CookieConstructor constructor = new CookieConstructor(cookie, web.getSettings().getUserAgentString(), urlD);
                    Stream(url, constructor);
                    web.loadUrl("about:blank");
                } else {
                    web.loadUrl("about:blank");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void toast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }

    public void Minimize() {
        if (d.isShowing()) {
            d.dismiss();
        }
        finish();
    }

    public void Descargar(String url) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sd_down", false)) {
            ManageDownload.external(context).startDownload(eid, url);
        } else {
            ManageDownload.internal(context).startDownload(eid, url);
        }
        Minimize();
    }

    public void Descargar(String url, CookieConstructor constructor) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sd_down", false)) {
            ManageDownload.external(context).startDownload(eid, url, constructor);
        } else {
            ManageDownload.internal(context).startDownload(eid, url, constructor);
        }
        Minimize();
    }

    public void Stream(String url) {
        int type = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_streaming", "0"));
        switch (type) {
            case 0:
                StreamManager.internal(context).Stream(eid, url);
                break;
            case 1:
                Intent i = (new Intent(Intent.ACTION_VIEW, Uri.parse(url)).setType("application/mp4"));
                PackageManager pm = context.getPackageManager();
                final ResolveInfo mInfo = pm.resolveActivity(i, 0);
                String id = mInfo.activityInfo.applicationInfo.processName;
                if (id.startsWith("com.mxtech.videoplayer")) {
                    StreamManager.mx(context).Stream(eid, url);
                } else {
                    StreamManager.external(context).Stream(eid, url);

                }
                break;
        }
        Minimize();
    }

    public void Stream(String url, CookieConstructor constructor) {
        int type = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_streaming", "0"));
        switch (type) {
            case 0:
                StreamManager.internal(context).Stream(eid, url, constructor);
                break;
            case 1:
                Intent i = (new Intent(Intent.ACTION_VIEW, Uri.parse(url)).setType("application/mp4"));
                PackageManager pm = context.getPackageManager();
                final ResolveInfo mInfo = pm.resolveActivity(i, 0);
                String id = mInfo.activityInfo.applicationInfo.processName;
                if (id.startsWith("com.mxtech.videoplayer")) {
                    StreamManager.mx(context).Stream(eid, url, constructor);
                } else {
                    StreamManager.external(context).Stream(eid, url);

                }
                break;
        }
        Minimize();
    }

    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                ex1.printStackTrace();
                return false;
            }
        }
        return true;
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

    public class Check extends AsyncTask<String, String, String> {
        Context context;
        String _response;

        public Check(Context c) {
            this.context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String u;
            if (params[0].startsWith(new Parser().getBaseUrl(TaskType.NORMAL, context)) || params[0].contains("getHtml.php")) {
                if (params[0].endsWith(".php")) {
                    u = params[0] + "?certificate=" + getCertificateSHA1Fingerprint();
                } else {
                    u = params[0] + "&certificate=" + getCertificateSHA1Fingerprint();
                }
            } else {
                u = params[0];
            }
            new SyncHttpClient().get(u, null, new JsonHttpResponseHandler() {
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
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    _response = "error";
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    _response = "error";
                }
            });
            return _response;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            if (isJSONValid(data.trim())) {
                try {
                    JSONObject jsonObject = new JSONObject(data.trim());
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
                    d = new MaterialDialog.Builder(context)
                            .title("Opciones")
                            .titleGravity(GravityEnum.CENTER)
                            .customView(R.layout.dialog_down, false)
                            .cancelable(true)
                            .autoDismiss(false)
                            .positiveText("Descargar")
                            .negativeText("Cancelar")
                            .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    String des = nombres.get(sp.getSelectedItemPosition());
                                    final String ur = urls.get(sp.getSelectedItemPosition());
                                    Log.d("Descargar", "URL -> " + ur);
                                    switch (des.toLowerCase()) {
                                        case "izanagi":
                                            new Izanagi().execute(ur);
                                            d.dismiss();
                                            break;
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
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ur)));
                                            break;
                                        default:
                                            Descargar(ur);
                                            d.dismiss();
                                            break;
                                    }
                                }

                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    super.onNegative(dialog);
                                    d.dismiss();
                                    finish();
                                }
                            })
                            .cancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    d.dismiss();
                                    finish();
                                }
                            })
                            .build();
                    sp = (Spinner) d.getCustomView().findViewById(R.id.spinner_down);
                    sp.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, nombres));
                    sp.setBackgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context));
                    d.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Data", data);
                    Toaster.toast("Error en JSON");
                }
            } else {
                Log.d("Data", data);
                Toaster.toast("Error en JSON");
            }
        }
    }

    public class CheckStream extends AsyncTask<String, String, String> {
        Context context;
        String _response;

        public CheckStream(Context c) {
            this.context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String u;
            if (params[0].startsWith(new Parser().getBaseUrl(TaskType.NORMAL, context)) || params[0].contains("getHtml.php")) {
                if (params[0].endsWith(".php")) {
                    u = params[0] + "?certificate=" + getCertificateSHA1Fingerprint();
                } else {
                    u = params[0] + "&certificate=" + getCertificateSHA1Fingerprint();
                }
            } else {
                u = params[0];
            }
            new SyncHttpClient().get(u, null, new JsonHttpResponseHandler() {
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
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    _response = "error";
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    _response = "error";
                }
            });
            return _response;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            if (isJSONValid(data.trim())) {
                try {
                    JSONObject jsonObject = new JSONObject(data.trim());
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
                    d = new MaterialDialog.Builder(context)
                            .title("Opciones")
                            .titleGravity(GravityEnum.CENTER)
                            .customView(R.layout.dialog_down, false)
                            .cancelable(true)
                            .autoDismiss(false)
                            .positiveText("Ver")
                            .negativeText("Cancelar")
                            .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    String des = nombres.get(sp.getSelectedItemPosition());
                                    final String ur = urls.get(sp.getSelectedItemPosition());
                                    Log.d("Descargar", "URL -> " + ur);
                                    switch (des.toLowerCase()) {
                                        case "izanagi":
                                            new IzanagiStream().execute(ur);
                                            d.dismiss();
                                            break;
                                        case "zippyshare":
                                            zippy.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    zippy.loadUrl(ur);
                                                }
                                            });
                                            d.dismiss();
                                            break;
                                        case "mega":
                                            d.dismiss();
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ur)));
                                            break;
                                        default:
                                            Stream(ur);
                                            d.dismiss();
                                            break;
                                    }
                                }

                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    super.onNegative(dialog);
                                    d.dismiss();
                                    finish();
                                }
                            })
                            .cancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    d.dismiss();
                                    finish();
                                }
                            })
                            .build();
                    sp = (Spinner) d.getCustomView().findViewById(R.id.spinner_down);
                    sp.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, nombres));
                    sp.setBackgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context));
                    d.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Data", data);
                    Toaster.toast("Error en JSON");
                }
            } else {
                Log.d("Data", data);
                Toaster.toast("Error en JSON");
            }
        }
    }

    public class Izanagi extends AsyncTask<String, String, String> {
        String _response;

        @Override
        protected String doInBackground(String... params) {
            new SyncHttpClient().get(params[0], null, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    throwable.printStackTrace();
                    _response = "error";
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    _response = responseString;
                }
            });
            return _response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!s.trim().equals("error")) {
                String furl = s.substring(s.indexOf("URL=") + 4, s.lastIndexOf("\">"));
                Descargar(furl);
            } else {
                toast("Error al descargar");
                finish();
            }
        }
    }

    public class IzanagiStream extends AsyncTask<String, String, String> {
        String _response;

        @Override
        protected String doInBackground(String... params) {
            new SyncHttpClient().get(params[0], null, new JsonHttpResponseHandler() {
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
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    _response = "error";
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    _response = "error";
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    _response = "error";
                }
            });
            return _response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!s.trim().equals("error")) {
                String furl = s.substring(s.indexOf("URL=") + 4, s.lastIndexOf("\">"));
                Stream(furl);
            } else {
                toast("Error al descargar");
                finish();
            }
        }
    }
}
