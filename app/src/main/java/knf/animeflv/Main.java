package knf.animeflv;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;

public class Main extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    String accion;

    WebView web;
    WebView web_Links;

    Parser parser;
    Toolbar toolbar;

    Context context;

    ScrollView scrollView;

    ImageView imgCard1;
    ImageView imgCard2;
    ImageView imgCard3;
    ImageView imgCard4;
    ImageView imgCard5;
    ImageView imgCard6;
    ImageView imgCard7;
    ImageView imgCard8;
    ImageView imgCard9;
    ImageView imgCard10;
    ImageView imgCard11;
    ImageView imgCard12;
    ImageView imgCard13;
    ImageView imgCard14;
    ImageView imgCard15;
    ImageView imgCard16;
    ImageView imgCard17;
    ImageView imgCard18;
    ImageView imgCard19;
    ImageView imgCard20;

    TextView txtTitulo1;
    TextView txtTitulo2;
    TextView txtTitulo3;
    TextView txtTitulo4;
    TextView txtTitulo5;
    TextView txtTitulo6;
    TextView txtTitulo7;
    TextView txtTitulo8;
    TextView txtTitulo9;
    TextView txtTitulo10;
    TextView txtTitulo11;
    TextView txtTitulo12;
    TextView txtTitulo13;
    TextView txtTitulo14;
    TextView txtTitulo15;
    TextView txtTitulo16;
    TextView txtTitulo17;
    TextView txtTitulo18;
    TextView txtTitulo19;
    TextView txtTitulo20;

    TextView txtCapitulo1;
    TextView txtCapitulo2;
    TextView txtCapitulo3;
    TextView txtCapitulo4;
    TextView txtCapitulo5;
    TextView txtCapitulo6;
    TextView txtCapitulo7;
    TextView txtCapitulo8;
    TextView txtCapitulo9;
    TextView txtCapitulo10;
    TextView txtCapitulo11;
    TextView txtCapitulo12;
    TextView txtCapitulo13;
    TextView txtCapitulo14;
    TextView txtCapitulo15;
    TextView txtCapitulo16;
    TextView txtCapitulo17;
    TextView txtCapitulo18;
    TextView txtCapitulo19;
    TextView txtCapitulo20;

    SwipeRefreshLayout mswipe;

    int first=0;
    String[] eids;
    String[] aids;
    String[] numeros;
    String[] titulos;
    String inicio="http://animeflv.net/api.php?accion=inicio";
    String json="{}";

    Alarm alarm = new Alarm();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anime_inicio);
        alarm.SetAlarm(this);
        first=1;
        if (!isXLargeScreen(getApplicationContext())) { //set phones to portrait;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Recientes");
        context=getApplicationContext();
        parser=new Parser();
        setLoad();
        mswipe.setOnRefreshListener(this);
        getJson();
        //RecyclerView rvAnimes = (RecyclerView) findViewById(R.id.rv_CardDownload);
        //RecyclerAdapter adapter = new RecyclerAdapter(this, getAnimes());
        //rvAnimes.setAdapter(adapter);
        //rvAnimes.setLayoutManager(new LinearLayoutManager(this));
    }

    public void toast(String texto){
        Toast.makeText(this,texto,Toast.LENGTH_LONG).show();
    }
    public void onVerclicked(View view){
        toast("Ver click");
    }
    public void onDesClicked(View view){
        Bundle bundle=new Bundle();
        Intent intent=new Intent(this,WebDescarga.class);
        switch (view.getId()){
            case R.id.ib_descargar_cardD1:
                String url1=getUrl(titulos[0], numeros[0]);
                bundle.putString("url",url1);
                bundle.putString("titulo", titulos[0]);
                bundle.putString("aid",aids[0]);
                bundle.putString("enum", numeros[0]);
                break;
            case R.id.ib_descargar_cardD2:
                String url2=getUrl(titulos[1], numeros[1]);
                bundle.putString("url",url2);
                bundle.putString("titulo",titulos[1]);
                bundle.putString("aid", aids[1]);
                bundle.putString("enum", numeros[1]);
                break;
            case R.id.ib_descargar_cardD3:
                String url3=getUrl(titulos[2], numeros[2]);
                bundle.putString("url",url3);
                bundle.putString("titulo",titulos[2]);
                bundle.putString("aid",aids[2]);
                bundle.putString("enum",numeros[2]);
                break;
            case R.id.ib_descargar_cardD4:
                String url4=getUrl(titulos[3], numeros[3]);
                bundle.putString("url",url4);
                bundle.putString("titulo",titulos[3]);
                bundle.putString("aid",aids[3]);
                bundle.putString("enum",numeros[3]);
                break;
            case R.id.ib_descargar_cardD5:
                String url5=getUrl(titulos[4], numeros[4]);
                bundle.putString("url",url5);
                bundle.putString("titulo",titulos[4]);
                bundle.putString("aid",aids[4]);
                bundle.putString("enum",numeros[4]);
                break;
            case R.id.ib_descargar_cardD6:
                String url6=getUrl(titulos[5], numeros[5]);
                bundle.putString("url",url6);
                bundle.putString("titulo",titulos[5]);
                bundle.putString("aid",aids[5]);
                bundle.putString("enum",numeros[5]);
                break;
            case R.id.ib_descargar_cardD7:
                String url7=getUrl(titulos[6], numeros[6]);
                bundle.putString("url",url7);
                bundle.putString("titulo",titulos[6]);
                bundle.putString("aid",aids[6]);
                bundle.putString("enum",numeros[6]);
                break;
            case R.id.ib_descargar_cardD8:
                String url8=getUrl(titulos[7], numeros[7]);
                bundle.putString("url",url8);
                bundle.putString("titulo",titulos[7]);
                bundle.putString("aid",aids[7]);
                bundle.putString("enum",numeros[7]);
                break;
            case R.id.ib_descargar_cardD9:
                String url9=getUrl(titulos[8], numeros[8]);
                bundle.putString("url",url9);
                bundle.putString("titulo",titulos[8]);
                bundle.putString("aid",aids[8]);
                bundle.putString("enum",numeros[8]);
                break;
            case R.id.ib_descargar_cardD10:
                String url10=getUrl(titulos[9], numeros[9]);
                bundle.putString("url",url10);
                bundle.putString("titulo",titulos[9]);
                bundle.putString("aid",aids[9]);
                bundle.putString("enum",numeros[9]);
                break;
            case R.id.ib_descargar_cardD11:
                String url11=getUrl(titulos[10], numeros[10]);
                bundle.putString("url",url11);
                bundle.putString("titulo",titulos[10]);
                bundle.putString("aid",aids[10]);
                bundle.putString("enum",numeros[10]);
                break;
            case R.id.ib_descargar_cardD12:
                String url12=getUrl(titulos[11], numeros[11]);
                bundle.putString("url",url12);
                bundle.putString("titulo",titulos[11]);
                bundle.putString("aid",aids[11]);
                bundle.putString("enum",numeros[11]);
                break;
            case R.id.ib_descargar_cardD13:
                String url13=getUrl(titulos[12], numeros[12]);
                bundle.putString("url",url13);
                bundle.putString("titulo",titulos[12]);
                bundle.putString("aid",aids[12]);
                bundle.putString("enum",numeros[12]);
                break;
            case R.id.ib_descargar_cardD14:
                String url14=getUrl(titulos[13], numeros[13]);
                bundle.putString("url",url14);
                bundle.putString("titulo",titulos[13]);
                bundle.putString("aid",aids[13]);
                bundle.putString("enum",numeros[13]);
                break;
            case R.id.ib_descargar_cardD15:
                String url15=getUrl(titulos[14], numeros[14]);
                bundle.putString("url",url15);
                bundle.putString("titulo",titulos[14]);
                bundle.putString("aid",aids[14]);
                bundle.putString("enum",numeros[14]);
                break;
            case R.id.ib_descargar_cardD16:
                String url16=getUrl(titulos[15], numeros[15]);
                bundle.putString("url",url16);
                bundle.putString("titulo",titulos[15]);
                bundle.putString("aid",aids[15]);
                bundle.putString("enum",numeros[15]);
                break;
            case R.id.ib_descargar_cardD17:
                String url17=getUrl(titulos[16], numeros[16]);
                bundle.putString("url",url17);
                bundle.putString("titulo",titulos[16]);
                bundle.putString("aid",aids[16]);
                bundle.putString("enum",numeros[16]);
                break;
            case R.id.ib_descargar_cardD18:
                String url18=getUrl(titulos[17], numeros[17]);
                bundle.putString("url",url18);
                bundle.putString("titulo",titulos[17]);
                bundle.putString("aid",aids[17]);
                bundle.putString("enum",numeros[17]);
                break;
            case R.id.ib_descargar_cardD19:
                String url19=getUrl(titulos[18], numeros[18]);
                bundle.putString("url",url19);
                bundle.putString("titulo",titulos[18]);
                bundle.putString("aid",aids[18]);
                bundle.putString("enum",numeros[18]);
                break;
            case R.id.ib_descargar_cardD20:
                String url20=getUrl(titulos[19], numeros[19]);
                bundle.putString("url",url20);
                bundle.putString("titulo",titulos[19]);
                bundle.putString("aid",aids[19]);
                bundle.putString("enum",numeros[19]);
                break;
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }
    public void onCardClicked(View view){ toast("Card Click"); }
    public void setLoad(){
        scrollView=(ScrollView) findViewById(R.id.sv_inicio);
        mswipe=(SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        imgCard1=(ImageView) findViewById(R.id.imgCardD1);
        imgCard2=(ImageView) findViewById(R.id.imgCardD2);
        imgCard3=(ImageView) findViewById(R.id.imgCardD3);
        imgCard4=(ImageView) findViewById(R.id.imgCardD4);
        imgCard5=(ImageView) findViewById(R.id.imgCardD5);
        imgCard6=(ImageView) findViewById(R.id.imgCardD6);
        imgCard7=(ImageView) findViewById(R.id.imgCardD7);
        imgCard8=(ImageView) findViewById(R.id.imgCardD8);
        imgCard9=(ImageView) findViewById(R.id.imgCardD9);
        imgCard10=(ImageView) findViewById(R.id.imgCardD10);
        imgCard11=(ImageView) findViewById(R.id.imgCardD11);
        imgCard12=(ImageView) findViewById(R.id.imgCardD12);
        imgCard13=(ImageView) findViewById(R.id.imgCardD13);
        imgCard14=(ImageView) findViewById(R.id.imgCardD14);
        imgCard15=(ImageView) findViewById(R.id.imgCardD15);
        imgCard16=(ImageView) findViewById(R.id.imgCardD16);
        imgCard17=(ImageView) findViewById(R.id.imgCardD17);
        imgCard18=(ImageView) findViewById(R.id.imgCardD18);
        imgCard19=(ImageView) findViewById(R.id.imgCardD19);
        imgCard20=(ImageView) findViewById(R.id.imgCardD20);

        txtTitulo1=(TextView) findViewById(R.id.tv_cardD_titulo1);
        txtTitulo2=(TextView) findViewById(R.id.tv_cardD_titulo2);
        txtTitulo3=(TextView) findViewById(R.id.tv_cardD_titulo3);
        txtTitulo4=(TextView) findViewById(R.id.tv_cardD_titulo4);
        txtTitulo5=(TextView) findViewById(R.id.tv_cardD_titulo5);
        txtTitulo6=(TextView) findViewById(R.id.tv_cardD_titulo6);
        txtTitulo7=(TextView) findViewById(R.id.tv_cardD_titulo7);
        txtTitulo8=(TextView) findViewById(R.id.tv_cardD_titulo8);
        txtTitulo9=(TextView) findViewById(R.id.tv_cardD_titulo9);
        txtTitulo10=(TextView) findViewById(R.id.tv_cardD_titulo10);
        txtTitulo11=(TextView) findViewById(R.id.tv_cardD_titulo11);
        txtTitulo12=(TextView) findViewById(R.id.tv_cardD_titulo12);
        txtTitulo13=(TextView) findViewById(R.id.tv_cardD_titulo13);
        txtTitulo14=(TextView) findViewById(R.id.tv_cardD_titulo14);
        txtTitulo15=(TextView) findViewById(R.id.tv_cardD_titulo15);
        txtTitulo16=(TextView) findViewById(R.id.tv_cardD_titulo16);
        txtTitulo17=(TextView) findViewById(R.id.tv_cardD_titulo17);
        txtTitulo18=(TextView) findViewById(R.id.tv_cardD_titulo18);
        txtTitulo19=(TextView) findViewById(R.id.tv_cardD_titulo19);
        txtTitulo20=(TextView) findViewById(R.id.tv_cardD_titulo20);

        txtCapitulo1=(TextView) findViewById(R.id.tv_cardD_capitulo1);
        txtCapitulo2=(TextView) findViewById(R.id.tv_cardD_capitulo2);
        txtCapitulo3=(TextView) findViewById(R.id.tv_cardD_capitulo3);
        txtCapitulo4=(TextView) findViewById(R.id.tv_cardD_capitulo4);
        txtCapitulo5=(TextView) findViewById(R.id.tv_cardD_capitulo5);
        txtCapitulo6=(TextView) findViewById(R.id.tv_cardD_capitulo6);
        txtCapitulo7=(TextView) findViewById(R.id.tv_cardD_capitulo7);
        txtCapitulo8=(TextView) findViewById(R.id.tv_cardD_capitulo8);
        txtCapitulo9=(TextView) findViewById(R.id.tv_cardD_capitulo9);
        txtCapitulo10=(TextView) findViewById(R.id.tv_cardD_capitulo10);
        txtCapitulo11=(TextView) findViewById(R.id.tv_cardD_capitulo11);
        txtCapitulo12=(TextView) findViewById(R.id.tv_cardD_capitulo12);
        txtCapitulo13=(TextView) findViewById(R.id.tv_cardD_capitulo13);
        txtCapitulo14=(TextView) findViewById(R.id.tv_cardD_capitulo14);
        txtCapitulo15=(TextView) findViewById(R.id.tv_cardD_capitulo15);
        txtCapitulo16=(TextView) findViewById(R.id.tv_cardD_capitulo16);
        txtCapitulo17=(TextView) findViewById(R.id.tv_cardD_capitulo17);
        txtCapitulo18=(TextView) findViewById(R.id.tv_cardD_capitulo18);
        txtCapitulo19=(TextView) findViewById(R.id.tv_cardD_capitulo19);
        txtCapitulo20=(TextView) findViewById(R.id.tv_cardD_capitulo20);

        web=(WebView) findViewById(R.id.wv_inicio);
        web.getSettings().setJavaScriptEnabled(true);
        web.addJavascriptInterface(new JavaScriptInterface(context), "HtmlViewer");
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                web.loadUrl("javascript:window.HtmlViewer.showHTML" +
                        "('<html>'+document.getElementsByTagName('body')[0].innerHTML+'</html>');");
            }
        });
        web_Links=(WebView) findViewById(R.id.wv_inicio2);
        web_Links.getSettings().setJavaScriptEnabled(true);
        web_Links.getSettings().setLoadsImagesAutomatically(false);
        web_Links.getSettings().setBlockNetworkLoads(true);
        web_Links.addJavascriptInterface(new JavaScriptInterface(context), "HtmlViewer");
        web_Links.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                web_Links.loadUrl("javascript:window.HtmlViewer.showHTMLD1" + "(document.getElementById('descargas_box').getElementsByTagName('a')[1].href);");
                web_Links.loadUrl("javascript:window.HtmlViewer.showHTMLD2" + "(document.getElementById('dlbutton').href);");
                web_Links.loadUrl("javascript:(function(){" +"l=document.getElementById('skiplink');" +"e=document.createEvent('HTMLEvents');" +"e.initEvent('click',true,true);" +"l.dispatchEvent(e);" +"})()");
            }
        });
    }
    public void loadImg(String[] list){
        final Context context=getApplicationContext();
        final String[] url=list;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Picasso.with(context).load(url[0]).error(R.drawable.ic_block_r).into(imgCard1);
                Picasso.with(context).load(url[1]).error(R.drawable.ic_block_r).into(imgCard2);
                Picasso.with(context).load(url[2]).error(R.drawable.ic_block_r).into(imgCard3);
                Picasso.with(context).load(url[3]).error(R.drawable.ic_block_r).into(imgCard4);
                Picasso.with(context).load(url[4]).error(R.drawable.ic_block_r).into(imgCard5);
                Picasso.with(context).load(url[5]).error(R.drawable.ic_block_r).into(imgCard6);
                Picasso.with(context).load(url[6]).error(R.drawable.ic_block_r).into(imgCard7);
                Picasso.with(context).load(url[7]).error(R.drawable.ic_block_r).into(imgCard8);
                Picasso.with(context).load(url[8]).error(R.drawable.ic_block_r).into(imgCard9);
                Picasso.with(context).load(url[9]).error(R.drawable.ic_block_r).into(imgCard10);
                Picasso.with(context).load(url[10]).error(R.drawable.ic_block_r).into(imgCard11);
                Picasso.with(context).load(url[11]).error(R.drawable.ic_block_r).into(imgCard12);
                Picasso.with(context).load(url[12]).error(R.drawable.ic_block_r).into(imgCard13);
                Picasso.with(context).load(url[13]).error(R.drawable.ic_block_r).into(imgCard14);
                Picasso.with(context).load(url[14]).error(R.drawable.ic_block_r).into(imgCard15);
                Picasso.with(context).load(url[15]).error(R.drawable.ic_block_r).into(imgCard16);
                Picasso.with(context).load(url[16]).error(R.drawable.ic_block_r).into(imgCard17);
                Picasso.with(context).load(url[17]).error(R.drawable.ic_block_r).into(imgCard18);
                Picasso.with(context).load(url[18]).error(R.drawable.ic_block_r).into(imgCard19);
                Picasso.with(context).load(url[19]).error(R.drawable.ic_block_r).into(imgCard20);
            }
        });
    }
    public void loadTitulos(String[] list){
        final String[] titulo=list;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtTitulo1.setText(titulo[0]);
                txtTitulo2.setText(titulo[1]);
                txtTitulo3.setText(titulo[2]);
                txtTitulo4.setText(titulo[3]);
                txtTitulo5.setText(titulo[4]);
                txtTitulo6.setText(titulo[5]);
                txtTitulo7.setText(titulo[6]);
                txtTitulo8.setText(titulo[7]);
                txtTitulo9.setText(titulo[8]);
                txtTitulo10.setText(titulo[9]);
                txtTitulo11.setText(titulo[10]);
                txtTitulo12.setText(titulo[11]);
                txtTitulo13.setText(titulo[12]);
                txtTitulo14.setText(titulo[13]);
                txtTitulo15.setText(titulo[14]);
                txtTitulo16.setText(titulo[15]);
                txtTitulo17.setText(titulo[16]);
                txtTitulo18.setText(titulo[17]);
                txtTitulo19.setText(titulo[18]);
                txtTitulo20.setText(titulo[19]);
            }
        });
    }
    public void loadCapitulos(String[] list){
        final String[] capitulo=list;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtCapitulo1.setText(capitulo[0]);
                txtCapitulo2.setText(capitulo[1]);
                txtCapitulo3.setText(capitulo[2]);
                txtCapitulo4.setText(capitulo[3]);
                txtCapitulo5.setText(capitulo[4]);
                txtCapitulo6.setText(capitulo[5]);
                txtCapitulo7.setText(capitulo[6]);
                txtCapitulo8.setText(capitulo[7]);
                txtCapitulo9.setText(capitulo[8]);
                txtCapitulo10.setText(capitulo[9]);
                txtCapitulo11.setText(capitulo[10]);
                txtCapitulo12.setText(capitulo[11]);
                txtCapitulo13.setText(capitulo[12]);
                txtCapitulo14.setText(capitulo[13]);
                txtCapitulo15.setText(capitulo[14]);
                txtCapitulo16.setText(capitulo[15]);
                txtCapitulo17.setText(capitulo[16]);
                txtCapitulo18.setText(capitulo[17]);
                txtCapitulo19.setText(capitulo[18]);
                txtCapitulo20.setText(capitulo[19]);
            }
        });
    }
    public void getJson(){
        web.loadUrl(inicio);
    }
    public void getlinks(String json){
        loadImg(parser.parseLinks(json));
    }
    public void gettitulos(String json){
        loadTitulos(parser.parseTitulos(json));
    }
    public void getCapitulos(String json){
        loadCapitulos(parser.parseCapitulos(json));
    }
    public void isFirst(){
        if (first==1){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    scrollView.setVisibility(View.VISIBLE);
                }});
            first=0;
        }
    }
    public void getData(String json){
        getlinks(json);
        gettitulos(json);
        getCapitulos(json);
        titulos=parser.parseTitulos(json);
        eids=parser.parseEID(json);
        aids=parser.parseAID(json);
        numeros=parser.parsenumeros(json);
        isFirst();
    }

    public String getUrl(String titulo,String capitulo){
        String ftitulo="";
        String atitulo=titulo.toLowerCase();
        for (int x=0; x < atitulo.length(); x++) {
            if (atitulo.charAt(x) != ' ') {
                ftitulo += atitulo.charAt(x);
            }else {
                if (atitulo.charAt(x) == ' ') {
                    ftitulo += "-";
                }
            }
        }
        ftitulo=ftitulo.replace("!","");
        String link="http://animeflv.net/ver/"+ftitulo+"-"+capitulo+".html";
        return link;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onRefresh() {
        web.loadUrl(inicio);
        if (mswipe.isRefreshing()){
            mswipe.setRefreshing(false);
        }
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
    class JavaScriptInterface {
        private Context ctx;
        JavaScriptInterface(Context ctx) {
            this.ctx = ctx;}
        @JavascriptInterface
        public void showHTML(String html) {
            String s_html_i=html.substring(21);
            String s_html_f="{"+s_html_i.substring(0,s_html_i.length()-7);
            json = s_html_f;
            getData(s_html_f);
        }
        @JavascriptInterface
        public void showHTMLD1(String html) {
            showHTML(html);
        }
        @JavascriptInterface
        public void showHTMLD2(String html) {
            showHTML(html);
        }}
}
