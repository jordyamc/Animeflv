package knf.animeflv;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import knf.animeflv.DownloadManager.CookieConstructor;
import knf.animeflv.DownloadManager.ManageDownload;
import knf.animeflv.JsonFactory.DownloadGetter;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.MainStates;
import knf.animeflv.Utils.eNums.DownloadTask;
import knf.animeflv.zippy.zippyHelper;
import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 20/08/2015.
 */
public class BackDownload extends AppCompatActivity {
    String aid;
    String num;
    String titulo;
    String eid;

    Parser parser = new Parser();
    Context context;
    MaterialDialog d;

    WebView web;

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
        if (!FileUtil.init(this).ExistAnime(eid)) {
            DownloadGetter.search(this, eid, new DownloadGetter.ActionsInterface() {
                @Override
                public boolean isStream() {
                    return false;
                }

                @Override
                public void onStartDownload() {
                    finish();
                }

                @Override
                public void onStartZippy(final String u) {
                    zippyHelper.calculate(u, new zippyHelper.OnZippyResult() {
                        @Override
                        public void onSuccess(zippyHelper.zippyObject object) {
                            ManageDownload.chooseDownDir(BackDownload.this, eid, object.download_url, object.cookieConstructor);
                        }

                        @Override
                        public void onError() {
                            Toaster.toast("Error al obtener link, reintentando en modo nativo");
                            MainStates.setZippyState(DownloadTask.DESCARGA, u, null, null, 0);
                            web.post(new Runnable() {
                                @Override
                                public void run() {
                                    web.loadUrl(u);
                                }
                            });
                        }
                    });
                }

                @Override
                public void onCancelDownload() {
                    finish();
                }

                @Override
                public void onStartCasting() {

                }

                @Override
                public void onLogError(Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            Toaster.toast("Este capitulo ya esta descargado");
            finish();
        }
    }

    public void setupWeb() {
        web.getSettings().setJavaScriptEnabled(true);
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().startSync();
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
                String urlD = MainStates.getUrlZippy();
                CookieManager cookieManager = CookieManager.getInstance();
                String cookie = cookieManager.getCookie(url.substring(0, url.indexOf("/", 8)));
                CookieConstructor constructor = new CookieConstructor(cookie, web.getSettings().getUserAgentString(), urlD);
                ManageDownload.chooseDownDir(BackDownload.this, eid, url, constructor);
                web.loadUrl(Parser.getNormalUrl(context));
            }
        });
        web.loadUrl(Parser.getNormalUrl(context));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            d.dismiss();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }
}
