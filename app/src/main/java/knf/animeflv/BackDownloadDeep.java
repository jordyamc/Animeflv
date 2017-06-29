package knf.animeflv;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import knf.animeflv.Directorio.DB.DirectoryHelper;
import knf.animeflv.DownloadManager.CookieConstructor;
import knf.animeflv.DownloadManager.ManageDownload;
import knf.animeflv.JsonFactory.DownloadGetter;
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
    Activity context;
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
        String full = getIntent().getDataString();
        String url = full.substring(full.lastIndexOf("/") + 1).replace(".html", "");
        final String cortado = url.substring(0, url.lastIndexOf("-"));
        aid = DirectoryHelper.get(this).getAid(cortado);
        num = url.substring(url.lastIndexOf("-") + 1);
        titulo = DirectoryHelper.get(this).getTitle(aid);
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
                        if (!animeExist() && !ManageDownload.isDownloading(BackDownloadDeep.this, eid)) {
                            DownloadGetter.search(BackDownloadDeep.this, eid, new DownloadGetter.ActionsInterface() {
                                @Override
                                public boolean isStream() {
                                    return false;
                                }

                                @Override
                                public void onStartDownload() {
                                    Minimize();
                                }

                                @Override
                                public void onStartZippy(final String url) {
                                    web.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            web.loadUrl(url);
                                        }
                                    });
                                }

                                @Override
                                public void onCancelDownload() {
                                    Minimize();
                                }

                                @Override
                                public void onStartCasting() {
                                    Minimize();
                                }

                                @Override
                                public void onLogError(Exception e) {
                                    e.printStackTrace();
                                }
                            });
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
                            DownloadGetter.search(BackDownloadDeep.this, eid, new DownloadGetter.ActionsInterface() {
                                @Override
                                public boolean isStream() {
                                    return true;
                                }

                                @Override
                                public void onStartDownload() {
                                    Minimize();
                                }

                                @Override
                                public void onStartZippy(final String url) {
                                    web.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            web.loadUrl(url);
                                        }
                                    });
                                }

                                @Override
                                public void onCancelDownload() {
                                    Minimize();
                                }

                                @Override
                                public void onStartCasting() {
                                    Minimize();
                                }

                                @Override
                                public void onLogError(Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            Toaster.toast("El anime ya existe, reproduciendo");
                            StreamManager.Play(BackDownloadDeep.this, eid);
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
        return FileUtil.init(this).ExistAnime(eid);
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
                                    ManageDownload.chooseDownDir(BackDownloadDeep.this, eid, url);
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
                    ManageDownload.chooseDownDir(BackDownloadDeep.this, eid, url, constructor);
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
                                    ManageDownload.chooseDownDir(BackDownloadDeep.this, eid, url);
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
                    StreamManager.Stream(BackDownloadDeep.this, eid, url);
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
        if (options.isShowing()) {
            options.dismiss();
        }
        finish();
    }
}
