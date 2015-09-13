package knf.animeflv;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Jordy on 10/08/2015.
 */
public class WebDescarga extends AppCompatActivity implements Requests.callback {
    WebView webView;
    Toolbar toolbar;
    Bundle bundle;
    int inicio=0;
    boolean descargando=false;
    ProgressDialog progress;
    String ext_storage_state = Environment.getExternalStorageState();
    boolean doubleBackToExitPressedOnce = false;
    boolean closed=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web);
        if (!isXLargeScreen(getApplicationContext())) { //set phones to portrait;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.dark));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.prim));
        }
        bundle = getIntent().getExtras();
        webView = (WebView) findViewById(R.id.wv_global);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.addJavascriptInterface(new JavaScriptInterface(getApplicationContext()), "HtmlViewer");
        webView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                String fileName = url.substring(url.lastIndexOf("/") + 1);
                File Dstorage = new File(Environment.getExternalStorageDirectory() + "/.Animeflv/download/" + url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("_")));
                if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                    if (!Dstorage.exists()) {
                        Dstorage.mkdirs();
                    }
                }
                File archivo = new File(Dstorage, fileName);
                if (!archivo.exists() && !descargando && !closed) {
                    descargando = true;
                    String urlD = getSharedPreferences("data", MODE_PRIVATE).getString("urlD", null);
                    CookieManager cookieManager = CookieManager.getInstance();
                    String cookie = cookieManager.getCookie(url.substring(0, url.indexOf("/", 8)));
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setTitle(fileName.substring(0, fileName.indexOf(".")));
                    request.setDescription("Animeflv");
                    request.addRequestHeader("cookie", cookie);
                    request.addRequestHeader("User-Agent", webView.getSettings().getUserAgentString());
                    request.addRequestHeader("Accept", "text/html, application/xhtml+xml, *" + "/" + "*");
                    request.addRequestHeader("Accept-Language", "en-US,en;q=0.7,he;q=0.3");
                    request.addRequestHeader("Referer", urlD);
                    request.setMimeType("video/mp4");
                    request.setDestinationInExternalPublicDir(".Animeflv/download/" + url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("_")), fileName);
                    DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    manager.enqueue(request);
                    getSharedPreferences("data", MODE_PRIVATE).edit().putInt("sov", 0).apply();
                    progress.dismiss();
                    finish();
                    closed=true;
                } else {
                    if (!closed) {
                        toastS("El archivo ya existe");
                        progress.dismiss();
                        finish();
                        closed=true;
                    }
                }
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                int sov = getSharedPreferences("data", MODE_PRIVATE).getInt("sov", 0);
                if (sov == 1) {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onReceivedError(WebView view, int errorCod, String description, String failingUrl) {
                /*Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(failingUrl));
                finish();
                startActivity(intent);*/
                toastL("Error: " + Integer.toString(errorCod));
                finish();
                closed=true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:" +
                        "var json=JSON.stringify(videos);" +
                        "window.HtmlViewer.showHTMLD1(json);");
                webView.loadUrl("javascript:(function(){var l=document.getElementById('dlbutton');" + "var f=document.createEvent('HTMLEvents');" + "f.initEvent('click',true,true);" + "l.dispatchEvent(f);" + "})()");
            }
        });
        progress = ProgressDialog.show(this, "Obteniendo Link",
                "Por favor espere...", true, true, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                        toastS("Descarga cancelada");
                        finish();
                        closed=true;
                    }
                });
        new Requests(this,TaskType.GET_HTML1).execute(bundle.getString("url"));
        Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!closed) {
                    progress.dismiss();
                    toastS("Error al descargar");
                    finish();
                    closed=true;
                }
            }
        },30000);
    }
    public void toastS(String text){Toast.makeText(this,text,Toast.LENGTH_SHORT).show();}
    public void toastL(String text){Toast.makeText(this,text,Toast.LENGTH_LONG).show();}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_wv,menu);
        return false;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();
        if(id==R.id.atras){
            if (webView.canGoBack()){
                webView.goBack();
            }else {
                toastS("No puedes ir atras");
            }
        }
        if (id==R.id.adelante){
            if (webView.canGoForward()){
                webView.goForward();
            }else {
                toastS("No puedes ir adelante");
            }
        }
        return super.onOptionsItemSelected(item);
    }
    public static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }
    @Override
    public void onConfigurationChanged (Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        if (!isXLargeScreen(getApplicationContext()) ) {
            return;
        }
    }
    @Override
    public void sendtext1(String data,TaskType taskType){
        webView.loadUrl("about:blank");
        webView.loadData(data, "text/html", "UTF-8");
        descargando=false;
    }
    class JavaScriptInterface {
        private Context ctx;
        JavaScriptInterface(Context ctx) {
            this.ctx = ctx;}
        @JavascriptInterface
        public void showHTML(String html) {

        }
        @JavascriptInterface
        public void showHTMLD1(String html) {
            String replace=html.replace("\\/","/");
            String cortado=replace.substring(replace.indexOf("&proxy.link=")+12);
            cortado=cortado.substring(0,cortado.indexOf("file.html")+9).trim();
            getSharedPreferences("data", MODE_PRIVATE).edit().putString("urlD", cortado).apply();
            getSharedPreferences("data", MODE_PRIVATE).edit().putInt("sov",1).apply();
            final String finalstring=cortado;
            webView.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(finalstring);
                }
            });
        }
        @JavascriptInterface
        public void showHTMLD2(String html) {

        }}

}
