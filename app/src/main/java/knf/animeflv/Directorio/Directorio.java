package knf.animeflv.Directorio;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.List;

import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Recyclers.AdapterBusqueda;
import knf.animeflv.Requests;
import knf.animeflv.TaskType;
import knf.animeflv.info.AnimeInfo;
import knf.animeflv.info.Info;
import knf.animeflv.info.InfoCap;

/**
 * Created by Jordy on 29/08/2015.
 */
public class Directorio extends AppCompatActivity {
    Toolbar toolbarS;
    Menu menuGlobal;
    EditText editText;
    RecyclerView recyclerView;
    LinearLayout linearLayout;
    String ext_storage_state = Environment.getExternalStorageState();
    File mediaStorage = new File(Environment.getExternalStorageDirectory() + "/.Animeflv/cache");
    Parser parser=new Parser();
    String json="";
    Context context;
    EditText.OnEditorActionListener listener;
    int t_busqueda;
    ViewPager viewPager;
    SmartTabLayout viewPagerTab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.directorio);
        json=getJson();
        context=this;
        final Bundle bundle=getIntent().getExtras();
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
        t_busqueda= Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_busqueda","0").trim());
        toolbarS=(Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbarS);
        if (!isXLargeScreen(context)) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            toolbarS.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bundle == null) {
                        if (editText.getVisibility() == View.GONE) {
                            finish();
                        } else {
                            cancelar();
                        }
                    } else {
                        finish();
                    }
                }
            });
        }else {
            Toolbar ltoolbar=(Toolbar)findViewById(R.id.toolbar_l);
            ltoolbar.setNavigationIcon(R.drawable.ic_back_r);
            ltoolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bundle == null) {
                        if (editText.getVisibility() == View.GONE) {
                            finish();
                        } else {
                            cancelar();
                        }
                    } else {
                        finish();
                    }
                }
            });
        }
        toolbarS.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.buscar_borrar:
                        if (editText.getText().length() > 0) {
                            editText.setText("");
                            menuGlobal.clear();
                            if (!isXLargeScreen(context)) {
                                getMenuInflater().inflate(R.menu.menu_buscar_cancelar, menuGlobal);
                            }else {
                                getMenuInflater().inflate(R.menu.menu_buscar_cancelar_d, menuGlobal);
                            }
                            List<String> titulos = parser.DirTitulosBusqueda(json, null);
                            List<String> tipos = parser.DirTiposBusqueda(json, null);
                            List<String> index = parser.DirIndexBusqueda(json, null);
                            List<String> titOrd = parser.DirTitulosBusqueda(json, null);
                            Collections.sort(titOrd, String.CASE_INSENSITIVE_ORDER);
                            List<String> indexOrd = new ArrayList<String>();
                            List<String> tiposOrd = new ArrayList<String>();
                            List<String> links = new ArrayList<String>();
                            for (String si : titOrd) {
                                String indexn = index.get(titulos.indexOf(si));
                                indexOrd.add(indexn);
                            }
                            for (String so : titOrd) {
                                String tipon = tipos.get(titulos.indexOf(so));
                                tiposOrd.add(tipon);
                            }
                            for (String st : indexOrd) {
                                String link = "http://cdn.animeflv.net/img/portada/thumb_80/" + st + ".jpg";
                                links.add(link);
                            }
                            AdapterBusqueda adapterBusqueda = new AdapterBusqueda(context, titOrd, tiposOrd, links, indexOrd);
                            recyclerView.setAdapter(adapterBusqueda);
                        }
                        break;
                    case R.id.buscar_cancelar:
                        if (bundle==null) {
                            cancelar();
                        }else {
                            finish();
                        }
                        break;
                    case R.id.search:
                        getSupportActionBar().setTitle("");
                        editText.setVisibility(View.VISIBLE);
                        editText.setText("");
                        editText.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(editText, 0);
                        menuGlobal.clear();
                        if (!isXLargeScreen(context)) {
                            getMenuInflater().inflate(R.menu.menu_buscar_cancelar, menuGlobal);
                        }else {
                            getMenuInflater().inflate(R.menu.menu_buscar_cancelar_d, menuGlobal);
                        }
                        if (!isXLargeScreen(context)) {
                            linearLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }else {
                            viewPager.setVisibility(View.GONE);
                            viewPagerTab.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        break;
                }
                return true;
            }
        });
        editText=(EditText) findViewById(R.id.et_busqueda);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(final Editable s) {
                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (t_busqueda==0) {
                            if (s.length() > 0) {
                                menuGlobal.clear();
                                if (!isXLargeScreen(context)) {
                                    getMenuInflater().inflate(R.menu.menu_buscar_borrar, menuGlobal);
                                }else {
                                    getMenuInflater().inflate(R.menu.menu_buscar_borrar_d, menuGlobal);
                                }
                            } else {
                                menuGlobal.clear();
                                if (!isXLargeScreen(context)) {
                                    getMenuInflater().inflate(R.menu.menu_buscar_cancelar, menuGlobal);
                                }else {
                                    getMenuInflater().inflate(R.menu.menu_buscar_cancelar_d, menuGlobal);
                                }
                            }
                            List<String> titulos = parser.DirTitulosBusqueda(json, s.toString());
                            List<String> tipos = parser.DirTiposBusqueda(json, s.toString());
                            List<String> index = parser.DirIndexBusqueda(json, s.toString());
                            List<String> titOrd = parser.DirTitulosBusqueda(json, s.toString());
                            Collections.sort(titOrd, String.CASE_INSENSITIVE_ORDER);
                            List<String> indexOrd = new ArrayList<String>();
                            List<String> tiposOrd = new ArrayList<String>();
                            List<String> links = new ArrayList<String>();
                            for (String si : titOrd) {
                                String indexn = index.get(titulos.indexOf(si));
                                indexOrd.add(indexn);
                            }
                            for (String so : titOrd) {
                                String tipon = tipos.get(titulos.indexOf(so));
                                tiposOrd.add(tipon);
                            }
                            for (String st : indexOrd) {
                                String link = "http://cdn.animeflv.net/img/portada/thumb_80/" + st + ".jpg";
                                links.add(link);
                            }
                            AdapterBusqueda adapterBusqueda = new AdapterBusqueda(context, titOrd, tiposOrd, links, indexOrd);
                            recyclerView.setAdapter(adapterBusqueda);
                        }else {
                            if (s.length() > 0) {
                                menuGlobal.clear();
                                if (!isXLargeScreen(context)) {
                                    getMenuInflater().inflate(R.menu.menu_buscar_borrar, menuGlobal);
                                }else {
                                    getMenuInflater().inflate(R.menu.menu_buscar_borrar_d, menuGlobal);
                                }
                            } else {
                                menuGlobal.clear();
                                if (!isXLargeScreen(context)) {
                                    getMenuInflater().inflate(R.menu.menu_buscar_cancelar, menuGlobal);
                                }else {
                                    getMenuInflater().inflate(R.menu.menu_buscar_cancelar_d, menuGlobal);
                                }
                            }
                        }
                    }
                },1000);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        editText.post(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
            }
        });
        listener=new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (t_busqueda==1) {
                        Editable s=editText.getEditableText();
                        if (s.length() > 0) {
                            menuGlobal.clear();
                            if (!isXLargeScreen(context)) {
                                getMenuInflater().inflate(R.menu.menu_buscar_borrar, menuGlobal);
                            }else {
                                getMenuInflater().inflate(R.menu.menu_buscar_borrar_d, menuGlobal);
                            }
                        } else {
                            menuGlobal.clear();
                            if (!isXLargeScreen(context)) {
                                getMenuInflater().inflate(R.menu.menu_buscar_cancelar, menuGlobal);
                            }else {
                                getMenuInflater().inflate(R.menu.menu_buscar_cancelar_d, menuGlobal);
                            }
                        }
                        List<String> titulos = parser.DirTitulosBusqueda(json, s.toString());
                        List<String> tipos = parser.DirTiposBusqueda(json, s.toString());
                        List<String> index = parser.DirIndexBusqueda(json, s.toString());
                        List<String> titOrd = parser.DirTitulosBusqueda(json, s.toString());
                        Collections.sort(titOrd, String.CASE_INSENSITIVE_ORDER);
                        List<String> indexOrd = new ArrayList<String>();
                        List<String> tiposOrd = new ArrayList<String>();
                        List<String> links = new ArrayList<String>();
                        for (String si : titOrd) {
                            String indexn = index.get(titulos.indexOf(si));
                            indexOrd.add(indexn);
                        }
                        for (String so : titOrd) {
                            String tipon = tipos.get(titulos.indexOf(so));
                            tiposOrd.add(tipon);
                        }
                        for (String st : indexOrd) {
                            String link = "http://cdn.animeflv.net/img/portada/thumb_80/" + st + ".jpg";
                            links.add(link);
                        }
                        AdapterBusqueda adapterBusqueda = new AdapterBusqueda(context, titOrd, tiposOrd, links, indexOrd);
                        recyclerView.setAdapter(adapterBusqueda);
                }
                return true;
            }
        };
        editText.setOnEditorActionListener(listener);
        if (t_busqueda==1){
            editText.setHint("Presiona Enter...");
        }
        linearLayout=(LinearLayout) findViewById(R.id.LY_dir);
        recyclerView=(RecyclerView)findViewById(R.id.rv_busqueda);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("ANIMES", Animes.class)
                .add("OVAS", Ovas.class)
                .add("PELICULAS", Peliculas.class)
                .create());
        viewPager = (ViewPager) findViewById(R.id.viewpager2);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
        viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab2);
        viewPagerTab.setViewPager(viewPager);
        if (bundle!=null){
            if (!isXLargeScreen(context)) {
                linearLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }else {
                viewPager.setVisibility(View.GONE);
                viewPagerTab.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
        List<String>titulos=parser.DirTitulosBusqueda(json, null);
        List<String>tipos=parser.DirTiposBusqueda(json, null);
        List<String>index=parser.DirIndexBusqueda(json, null);
        List<String> titOrd= parser.DirTitulosBusqueda(json, null);
        Collections.sort(titOrd, String.CASE_INSENSITIVE_ORDER);
        List<String> indexOrd=new ArrayList<String>();
        List<String> tiposOrd=new ArrayList<String>();
        List<String>links=new ArrayList<String>();
        for (String s:titOrd){
            String indexn=index.get(titulos.indexOf(s));
            indexOrd.add(indexn);
        }
        for (String so:titOrd){
            String tipon=tipos.get(titulos.indexOf(so));
            tiposOrd.add(tipon);
        }
        for (String st:indexOrd){
            String link="http://cdn.animeflv.net/img/portada/thumb_80/"+st+".jpg";
            links.add(link);
        }
        AdapterBusqueda adapterBusqueda=new AdapterBusqueda(this,titOrd,tiposOrd,links,indexOrd);
        recyclerView.setAdapter(adapterBusqueda);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuGlobal=menu;
        Bundle bundle=getIntent().getExtras();
        if (bundle==null){
            editText.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            menuGlobal.clear();
            if (!isXLargeScreen(context)) {
                getMenuInflater().inflate(R.menu.menu_main, menuGlobal);
            }else {
                getMenuInflater().inflate(R.menu.menu_main_dark, menuGlobal);
            }
            getSupportActionBar().setTitle("Directorio");
        }else {
            if (!isXLargeScreen(context)) {
                getMenuInflater().inflate(R.menu.menu_buscar_cancelar, menu);
            }else {
                getMenuInflater().inflate(R.menu.menu_buscar_cancelar_d, menu);
            }
        }
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, 0);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public String getJson() {
        String json = "";
        if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!mediaStorage.exists()) {
                mediaStorage.mkdirs();
            }
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/.Animeflv/cache/directorio.txt");
        String file_loc = Environment.getExternalStorageDirectory() + "/.Animeflv/cache/directorio.txt";
        if (file.exists()) {
            Log.d("Archivo", "Existe");
            json = getStringFromFile(file_loc);
        }
        return json;
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
        }catch (IOException e){}catch (Exception e){}
        return ret;
    }
    public static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }
    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (!isXLargeScreen(getApplicationContext()) ) {
            return;
        }
    }
    public void cancelar() {
        if (editText.getText().length() >= 0) {
            editText.setText("");
            editText.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            menuGlobal.clear();
            if (!isXLargeScreen(context)) {
                getMenuInflater().inflate(R.menu.menu_main, menuGlobal);
            }else {
                getMenuInflater().inflate(R.menu.menu_main_dark, menuGlobal);
            }
            getSupportActionBar().setTitle("Directorio");
            if (!isXLargeScreen(context)) {
                recyclerView.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
            }else {
                recyclerView.setVisibility(View.GONE);
                viewPager.setVisibility(View.VISIBLE);
                viewPagerTab.setVisibility(View.VISIBLE);
            }
            List<String> titulos = parser.DirTitulosBusqueda(json, null);
            List<String> tipos = parser.DirTiposBusqueda(json, null);
            List<String> index = parser.DirIndexBusqueda(json, null);
            List<String> titOrd = parser.DirTitulosBusqueda(json, null);
            Collections.sort(titOrd, String.CASE_INSENSITIVE_ORDER);
            List<String> indexOrd = new ArrayList<String>();
            List<String> tiposOrd = new ArrayList<String>();
            List<String> links = new ArrayList<String>();
            for (String si : titOrd) {
                String indexn = index.get(titulos.indexOf(si));
                indexOrd.add(indexn);
            }
            for (String so : titOrd) {
                String tipon = tipos.get(titulos.indexOf(so));
                tiposOrd.add(tipon);
            }
            for (String st : indexOrd) {
                String link = "http://cdn.animeflv.net/img/portada/thumb_80/" + st + ".jpg";
                links.add(link);
            }
            AdapterBusqueda adapterBusqueda = new AdapterBusqueda(context, titOrd, tiposOrd, links, indexOrd);
            recyclerView.setAdapter(adapterBusqueda);
        }
    }
}
