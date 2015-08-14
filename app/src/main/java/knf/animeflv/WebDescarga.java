package knf.animeflv;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by Jordy on 10/08/2015.
 */
public class WebDescarga extends AppCompatActivity implements Requests.callback {
    WebView webView;
    Toolbar toolbar;
    Bundle bundle;
    int inicio=0;

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
        webView.getSettings().setLoadsImagesAutomatically(false);
        webView.getSettings().setBlockNetworkLoads(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.addJavascriptInterface(new JavaScriptInterface(getApplicationContext()), "HtmlViewer");
        webView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                new Main().toast("Descarga Iniciada");
                String fileName = url.substring(url.lastIndexOf("/") + 1);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDescription("Cecyt 3");
                request.setTitle(fileName);
                // in order for this if to run, you must use the android 3.2 to compile your app
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

                // get download service and enqueue file
                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);
            }
        });
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return false;
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
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:window.HtmlViewer.showHTMLD1" + "(document.getElementById('descargas_box').getElementsByTagName('a')[1].href);");
                webView.loadUrl("javascript:window.HtmlViewer.showHTMLD2" + "(document.getElementById('dlbutton').href);");
                webView.loadUrl("javascript:(function(){" + "l=document.getElementById('skiplink');" + "e=document.createEvent('HTMLEvents');" + "e.initEvent('click',true,true);" + "l.dispatchEvent(e);" + "})()");
            }
        });
        new Requests(this,TaskType.GET_HTML1).execute(bundle.getString("url"));
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
            Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(html));
            startActivity(intent);
            finish();
        }
        @JavascriptInterface
        public void showHTMLD2(String html) {

        }}

}
