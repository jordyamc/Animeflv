package knf.animeflv.info;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Transition;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import knf.animeflv.LoginServer;
import knf.animeflv.Main;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Requests;
import knf.animeflv.TaskType;

/**
 * Created by Jordy on 12/08/2015.
 */
public class Info extends AppCompatActivity implements Requests.callback, LoginServer.callback {
    Parser parser=new Parser();
    Toolbar toolbar;
    String ext_storage_state = Environment.getExternalStorageState();
    File mediaStorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache");
    Boolean favBoolean=false;
    Menu Amenu;
    String aid;
    String titulo="";
    String id="";
    MaterialDialog dialog;
    Spinner spinner;
    Context context;
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
        context = this;
        getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("cambio", false).apply();
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
        toolbar=(Toolbar) findViewById(R.id.info_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.blanco), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SharedPreferences sharedPreferences=getSharedPreferences("data", Context.MODE_PRIVATE);
        aid=sharedPreferences.getString("aid", "");
        Log.d("Base Aid",aid);
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!mediaStorage.exists()) {
                mediaStorage.mkdirs();
            }
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/"+aid+".txt");
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/"+aid+".txt";
        Bundle bundleR = getIntent().getExtras();
        String link = bundleR.getString("link", "");
        if (file.exists()) {
            Log.d("Archivo", "Existe");
            String infile = getStringFromFile(file_loc);
            SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
            editor.putString("titInfo",parser.getTit(infile)).commit();
            getSupportActionBar().setTitle(parser.getTit(infile));
            titulo=parser.getTit(infile);
            id=parser.getAID(infile);
        }else {
            String servidor = PreferenceManager.getDefaultSharedPreferences(this).getString("servidor", "http://animeflv.net/api.php?accion=");
            new Requests(this, TaskType.GET_INFO).execute(new Parser().getInicioUrl(TaskType.NORMAL, this) + "?url=" + link);
        }
        Bundle bundle=new Bundle();
        bundle.putString("aid", getIntent().getExtras().getString("aid", "1"));
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("INFORMACION", AnimeInfo.class,bundle)
                .add("EPISODIOS", InfoCap.class,bundle)
                .create());
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager1);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab1);
        viewPagerTab.setViewPager(viewPager);
        final String email_coded=PreferenceManager.getDefaultSharedPreferences(this).getString("login_email_coded", "null");
        final String pass_coded=PreferenceManager.getDefaultSharedPreferences(this).getString("login_pass_coded", "null");
        String Svistos=getSharedPreferences("data",Context.MODE_PRIVATE).getString("vistos","");
        String favoritos=getSharedPreferences("data", MODE_PRIVATE).getString("favoritos", "");
        if (!email_coded.equals("null")&&!email_coded.equals("null")) {
            //new LoginServer(this, TaskType.UPDATE, null, null, null, null).execute("http://animeflvapp.x10.mx/fav-server.php?tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + favoritos+":;:"+Svistos);
        }
    }
    public void toast(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }
    public String getJson() {
        String json="";
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!mediaStorage.exists()) {
                mediaStorage.mkdirs();
            }
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + id + ".txt");
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/" + id + ".txt";
        if (file.exists()) {
            Log.d("Archivo", "Existe");
            json = getStringFromFile(file_loc);
        }
        return json;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Amenu=menu;
        SharedPreferences sharedPreferences=getSharedPreferences("data",MODE_PRIVATE);
        String fav=sharedPreferences.getString("favoritos", "");
        String[] favoritos={};
        favoritos=fav.split(":::");
        String tit=getSharedPreferences("data", MODE_PRIVATE).getString("titInfo","");
        Boolean isfav=false;
        for (String favo:favoritos){
            if (!favo.equals("")) {
                if (Integer.parseInt(favo) == Integer.parseInt(aid)) {
                    getMenuInflater().inflate(R.menu.menu_fav_si, menu);
                    isfav=true;
                    break;
                }
            }
        }
        if (isfav){
            Amenu.clear();
            getMenuInflater().inflate(R.menu.menu_fav_si, menu);
        }else {
            Amenu.clear();
            getMenuInflater().inflate(R.menu.menu_fav_no, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String email_coded=PreferenceManager.getDefaultSharedPreferences(this).getString("login_email_coded", "null");
        String pass_coded=PreferenceManager.getDefaultSharedPreferences(this).getString("login_pass_coded", "null");
        switch (item.getItemId()){
            case R.id.favorito_si:
                SharedPreferences sharedPreferences=getSharedPreferences("data",MODE_PRIVATE);
                String fav=sharedPreferences.getString("favoritos", "");
                String[] favoritos={};
                favoritos=fav.split(":::");
                List<String> list=new ArrayList<String>();
                for (String i:favoritos){
                    if (!i.equals("")) {
                        if (Integer.parseInt(i) != Integer.parseInt(aid)) {
                            list.add(i);
                        }
                    }
                }
                favoritos=new String[list.size()];
                list.toArray(favoritos);
                StringBuilder builder = new StringBuilder();
                for(String i : favoritos)
                {
                    builder.append(":::" + i);
                }
                toast("Favorito Eliminado");
                getSharedPreferences("data",MODE_PRIVATE).edit().putString("favoritos",builder.toString()).commit();
                String vistos=getSharedPreferences("data",MODE_PRIVATE).getString("vistos","");
                if (!email_coded.equals("null")&&!email_coded.equals("null")) {
                    new LoginServer(this, TaskType.UPDATE, null, null, null, null).execute(parser.getBaseUrl(TaskType.NORMAL, context) + "fav-server.php?tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + builder.toString() + ":;:" + vistos);
                    new LoginServer(this, TaskType.UPDATE, null, null, null, null).execute(parser.getBaseUrl(TaskType.SECUNDARIA, context) + "fav-server.php?tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + builder.toString() + ":;:" + vistos);
                }
                Amenu.clear();
                getMenuInflater().inflate(R.menu.menu_fav_no,Amenu);
                break;
            case R.id.favorito_no:
                String[] favoritosNo={getSharedPreferences("data",MODE_PRIVATE).getString("favoritos","")};
                String titNo=getSharedPreferences("data",MODE_PRIVATE).getString("aid","");
                List<String> Listno = new ArrayList<String>(Arrays.asList(favoritosNo));
                Listno.add(aid);
                favoritos=new String[Listno.size()];
                Listno.toArray(favoritos);
                StringBuilder builderNo = new StringBuilder();
                for(String i : favoritos)
                {
                    builderNo.append(":::" + i);
                }
                toast("Favorito Agregado");
                getSharedPreferences("data",MODE_PRIVATE).edit().putString("favoritos",builderNo.toString()).commit();
                String vistos1=getSharedPreferences("data",MODE_PRIVATE).getString("vistos","");
                if (!email_coded.equals("null")&&!email_coded.equals("null")) {
                    new LoginServer(this, TaskType.UPDATE, null, null, null, null).execute(parser.getBaseUrl(TaskType.NORMAL, context) + "fav-server.php?tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + builderNo.toString() + ":;:" + vistos1);
                    new LoginServer(this, TaskType.UPDATE, null, null, null, null).execute(parser.getBaseUrl(TaskType.SECUNDARIA, context) + "fav-server.php?tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + builderNo.toString() + ":;:" + vistos1);
                }
                Amenu.clear();
                getMenuInflater().inflate(R.menu.menu_fav_si, Amenu);
                break;
            case R.id.comentarios:
                dialog=new MaterialDialog.Builder(this)
                        .title("COMENTARIOS")
                        .titleGravity(GravityEnum.CENTER)
                        .customView(R.layout.comentarios,false)
                        .positiveText("SALIR")
                        .build();
                spinner=(Spinner)dialog.getCustomView().findViewById(R.id.comentarios_box_cap);
                final List<String> caps=parser.parseNumerobyEID(getJson());
                String[] array=new String[caps.size()];
                caps.toArray(array);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,array );
                spinner.setAdapter(arrayAdapter);
                webView=(WebView)dialog.getCustomView().findViewById(R.id.comentarios_box);
                webView.getSettings().setJavaScriptEnabled(true);
                String newUA= "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0";
                webView.getSettings().setUserAgentString(newUA);
                webView.loadUrl("https://www.facebook.com/plugins/comments.php?api_key=133687500123077&channel_url=http%3A%2F%2Fstatic.ak.facebook.com%2Fconnect%2Fxd_arbiter%2Fjb3BUxkAISL.js%3Fversion%3D41%23cb%3Dfbb6634b4%26domain%3Danimeflv.com%26origin%3Dhttp%253A%252F%252Fanimeflv.com%252Ff1449cd23c%26relation%3Dparent.parent&href=http%3A%2F%2Fanimeflv.net%2Fver%2F" + getUrl(titulo, caps.get(0).substring(caps.get(0).lastIndexOf(" ") + 1)) + "&locale=es_LA&numposts=15&sdk=joey&version=v2.3&width=1000");
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        webView.loadUrl("https://www.facebook.com/plugins/comments.php?api_key=133687500123077&channel_url=http%3A%2F%2Fstatic.ak.facebook.com%2Fconnect%2Fxd_arbiter%2Fjb3BUxkAISL.js%3Fversion%3D41%23cb%3Dfbb6634b4%26domain%3Danimeflv.com%26origin%3Dhttp%253A%252F%252Fanimeflv.com%252Ff1449cd23c%26relation%3Dparent.parent&href=http%3A%2F%2Fanimeflv.net%2Fver%2F" + getUrl(titulo, caps.get(position).substring(caps.get(position).lastIndexOf(" ") + 1)) + "&locale=es_LA&numposts=15&sdk=joey&version=v2.3&width=1000");
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                dialog.show();
                break;
        }
        return true;
    }

    @Override
    public void sendtext1(String data,TaskType taskType) {
        SharedPreferences.Editor editor=getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putString("titInfo", parser.getTit(data)).apply();
        getSupportActionBar().setTitle(parser.getTit(data));
        titulo=parser.getTit(data);
        id=parser.getAID(data);
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
    public static String getStringFromFile (String filePath) {
        String ret="";
        try {
            File fl = new File(filePath);
            FileInputStream fin = new FileInputStream(fl);
            ret = convertStreamToString(fin);
            fin.close();
        } catch (IOException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
    public String getUrl(String titulo,String capitulo) {
        String ftitulo = "";
        String atitulo = titulo.toLowerCase();
        atitulo = atitulo.replace("*", "-");
        atitulo = atitulo.replace(":", "");
        atitulo = atitulo.replace(",", "");
        atitulo = atitulo.replace(" \u2606 ", "-");
        atitulo = atitulo.replace("\u2606", "-");
        atitulo = atitulo.replace("  ", "-");
        atitulo = atitulo.replace("@", "a");
        atitulo = atitulo.replace("/", "-");
        atitulo = atitulo.replace(".", "");
        atitulo = atitulo.replace("\"", "");
        atitulo = atitulo.replace("♥", "-");
        for (int x = 0; x < atitulo.length(); x++) {
            if (atitulo.charAt(x) != ' ') {
                ftitulo += atitulo.charAt(x);
            } else {
                if (atitulo.charAt(x) == ' ') {
                    ftitulo += "-";
                }
            }
        }
        ftitulo = ftitulo.replace("!!!", "-3");
        ftitulo = ftitulo.replace("!", "");
        ftitulo = ftitulo.replace("°", "");
        ftitulo = ftitulo.replace("&deg;", "");
        ftitulo = ftitulo.replace("(", "");
        ftitulo = ftitulo.replace(")", "");
        ftitulo = ftitulo.replace("2nd-season", "2");
        ftitulo = ftitulo.replace("'", "");
        if (ftitulo.trim().equals("gintama")) {
            ftitulo = ftitulo + "-2015";
        }
        if (ftitulo.trim().equals("miss-monochrome-the-animation-2")) {
            ftitulo = "miss-monochrome-the-animation-2nd-season";
        }
        if (ftitulo.trim().equals("ore-ga-ojousama-gakkou-ni-shomin-sample-toshite-gets-sareta-ken")) {
            ftitulo = "ore-ga-ojousama-gakkou-ni-shomin-sample-toshite-gets-sareta-";
        }
        if (ftitulo.trim().equals("diabolik-lovers-moreblood")) {
            ftitulo = "diabolik-lovers-more-blood";
        }
        String link = ftitulo + "-" + capitulo + ".html";
        return link;
    }

    @Override
    public void response(String data, TaskType taskType) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        final String email_coded=PreferenceManager.getDefaultSharedPreferences(this).getString("login_email_coded", "null");
        final String pass_coded=PreferenceManager.getDefaultSharedPreferences(this).getString("login_pass_coded", "null");
        String Svistos=getSharedPreferences("data",Context.MODE_PRIVATE).getString("vistos","");
        String favoritos=getSharedPreferences("data", MODE_PRIVATE).getString("favoritos", "");
        Boolean cambio = getSharedPreferences("data", MODE_PRIVATE).getBoolean("cambio", false);
        if (!email_coded.equals("null") && !email_coded.equals("null") && cambio) {
            new LoginServer(this, TaskType.UPDATE, null, null, null, null).execute(parser.getBaseUrl(TaskType.NORMAL, context) + "fav-server.php?tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + favoritos + ":;:" + Svistos);
            new LoginServer(this, TaskType.UPDATE, null, null, null, null).execute(parser.getBaseUrl(TaskType.SECUNDARIA, context) + "fav-server.php?tipo=refresh&email_coded=" + email_coded + "&pass_coded=" + pass_coded + "&new_favs=" + favoritos + ":;:" + Svistos);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
