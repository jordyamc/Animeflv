package knf.animeflv;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import knf.animeflv.Utils.Logger;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 20/08/2015.
 */
public class BackDownload extends AppCompatActivity {
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web);
        web = (WebView) findViewById(R.id.wv_global);
        context = this;
        setupWeb();
        Bundle bundle = getIntent().getExtras();
        aid = bundle.getString("aid", "");
        num = bundle.getString("num", "");
        titulo = bundle.getString("titulo", "");
        eid = bundle.getString("eid", "");
        Parser parser = new Parser();
        new Check(this).execute(parser.getInicioUrl(TaskType.NORMAL, this) + "?url=" + Parser.getUrlCached(aid + "_" + num + "E", "000"));
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
                //Log.d("Descarga",url+" " + fileName);
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
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    //request.setTitle(fileName.substring(0, fileName.indexOf(".")));
                    request.setTitle(titulo);
                    request.setDescription("Capítulo " + num);
                    request.addRequestHeader("cookie", cookie);
                    request.addRequestHeader("User-Agent", web.getSettings().getUserAgentString());
                    request.addRequestHeader("Accept", "text/html, application/xhtml+xml, *" + "/" + "*");
                    request.addRequestHeader("Accept-Language", "en-US,en;q=0.7,he;q=0.3");
                    request.addRequestHeader("Referer", urlD);
                    request.setMimeType("video/mp4");
                    request.setDestinationInExternalPublicDir("Animeflv/download/" + url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("_")), fileName);
                    DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    long l = manager.enqueue(request);
                    getSharedPreferences("data", MODE_PRIVATE).edit().putString(eid, Long.toString(l)).apply();
                    String descargados = getSharedPreferences("data", MODE_PRIVATE).getString("eids_descarga", "");
                    String epID = getSharedPreferences("data", MODE_PRIVATE).getString("epIDS_descarga", "");
                    if (descargados.contains(eid)) {
                        getSharedPreferences("data", MODE_PRIVATE).edit().putString("eids_descarga", descargados.replace(eid + ":::", "")).apply();
                        getSharedPreferences("data", MODE_PRIVATE).edit().putString("epIDS_descarga", epID.replace(aid + "_" + num + ":::", "")).apply();
                    }
                    descargados = getSharedPreferences("data", MODE_PRIVATE).getString("eids_descarga", "");
                    getSharedPreferences("data", MODE_PRIVATE).edit().putString("eids_descarga", descargados + eid + ":::").apply();
                    String tits = getSharedPreferences("data", MODE_PRIVATE).getString("titulos_descarga", "");
                    epID = getSharedPreferences("data", MODE_PRIVATE).getString("epIDS_descarga", "");
                    getSharedPreferences("data", MODE_PRIVATE).edit().putString("titulos_descarga", tits + aid + ":::").apply();
                    getSharedPreferences("data", MODE_PRIVATE).edit().putString("epIDS_descarga", epID + aid + "_" + num + ":::").apply();
                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("visto" + aid + "_" + num, true).apply();
                    String vistos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", "");
                    if (!vistos.contains(eid.trim())) {
                        vistos = vistos + eid.trim() + ":::";
                        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("vistos", vistos).apply();
                    }
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
        d.dismiss();
        finish();
    }

    public void toast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }

    public void Descargar(String url) {
        File Dstorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + aid);
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!Dstorage.exists()) {
                Dstorage.mkdirs();
            }
        }
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            Log.d("DURL", url);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            //request.setTitle(fileName.substring(0, fileName.indexOf(".")));
            request.setTitle(titulo);
            request.setDescription("Capítulo " + num);
            request.setMimeType("video/mp4");
            request.setDestinationInExternalPublicDir("Animeflv/download/" + aid, aid + "_" + num + ".mp4");
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            long l = manager.enqueue(request);
            context.getSharedPreferences("data", MODE_PRIVATE).edit().putString(eid, Long.toString(l)).apply();
            String descargados = context.getSharedPreferences("data", MODE_PRIVATE).getString("eids_descarga", "");
            String epID = context.getSharedPreferences("data", MODE_PRIVATE).getString("epIDS_descarga", "");
            if (descargados.contains(eid)) {
                context.getSharedPreferences("data", MODE_PRIVATE).edit().putString("eids_descarga", descargados.replace(eid + ":::", "")).apply();
                context.getSharedPreferences("data", MODE_PRIVATE).edit().putString("epIDS_descarga", epID.replace(aid + "_" + num + ":::", "")).apply();
            }
            descargados = context.getSharedPreferences("data", MODE_PRIVATE).getString("eids_descarga", "");
            context.getSharedPreferences("data", MODE_PRIVATE).edit().putString("eids_descarga", descargados + eid + ":::").apply();
            String tits = context.getSharedPreferences("data", MODE_PRIVATE).getString("titulos_descarga", "");
            epID = context.getSharedPreferences("data", MODE_PRIVATE).getString("epIDS_descarga", "");
            context.getSharedPreferences("data", MODE_PRIVATE).edit().putString("titulos_descarga", tits + aid + ":::").apply();
            context.getSharedPreferences("data", MODE_PRIVATE).edit().putString("epIDS_descarga", epID + aid + "_" + num + ":::").apply();
            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("visto" + eid + "_" + num, true).apply();
            String vistos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", "");
            if (!vistos.contains(eid.trim())) {
                vistos = vistos + eid.trim() + ":::";
                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("vistos", vistos).apply();
            }
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            StringBuilder builder = new StringBuilder();
            HttpURLConnection c = null;
            try {
                URL u;
                if (params[0].startsWith(new Parser().getBaseUrl(TaskType.NORMAL, context)) || params[0].contains("getHtml.php")) {
                    if (params[0].endsWith(".php")) {
                        u = new URL(params[0] + "?certificate=" + getCertificateSHA1Fingerprint());
                    } else {
                        u = new URL(params[0] + "&certificate=" + getCertificateSHA1Fingerprint());
                    }
                } else {
                    u = new URL(params[0]);
                }
                c = (HttpURLConnection) u.openConnection();
                c.setRequestProperty("Content-length", "0");
                c.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
                c.setRequestProperty("Accept", "*/*");
                c.setUseCaches(false);
                c.setConnectTimeout(20000);
                c.setAllowUserInteraction(false);
                c.connect();
                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                //c.disconnect();
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                if (!c.getURL().toString().contains("fav-server"))
                    Log.d("Requests URL Normal", u.toString());
                if (c.getURL() != u) {
                    if (!c.getURL().toString().trim().startsWith("http://animeflv")) {
                        Log.d("Requests URL ERROR", c.getURL().toString());
                        _response = "error";
                    } else {
                        if (!c.getURL().toString().contains("fav-server"))
                            Log.d("Requests URL OK", c.getURL().toString());
                        if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            _response = sb.toString();
                        } else {
                            _response = "error";
                        }
                    }
                } else {
                    if (!c.getURL().toString().contains("fav-server"))
                        Log.d("Requests URL OK", c.getURL().toString());
                    if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        _response = sb.toString();
                    } else {
                        _response = "error";
                    }
                }
                //String fullPage = page.asXml();
            } catch (Exception e) {
                Logger.Error(getClass(), e);
                _response = "error";
            }
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

    public class Izanagi extends AsyncTask<String, String, String> {
        String _response;


        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection c = null;
            try {
                URL u = new URL(params[0]);
                c = (HttpURLConnection) u.openConnection();
                c.setRequestProperty("Content-length", "0");
                c.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
                c.setRequestProperty("Accept", "*/*");
                c.setInstanceFollowRedirects(true);
                c.setUseCaches(false);
                c.setConnectTimeout(10000);
                c.setAllowUserInteraction(false);
                c.connect();
                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                //c.disconnect();
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                _response = sb.toString();
                //String fullPage = page.asXml();
            } catch (Exception e) {
                Logger.Error(BackDownload.this.getClass(), e);
                _response = "error";
            }
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
}
