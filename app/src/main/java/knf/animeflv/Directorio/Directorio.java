package knf.animeflv.Directorio;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
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
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import knf.animeflv.AnimeCompare;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Recyclers.AdapterBusqueda;
import knf.animeflv.Recyclers.AdapterBusquedaNew;

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
    File mediaStorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache");
    Parser parser = new Parser();
    String json = "";
    Context context;
    EditText.OnEditorActionListener listener;
    int t_busqueda;
    ViewPager viewPager;
    SmartTabLayout viewPagerTab;
    Spinner generosS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpAnimations();
        setContentView(R.layout.directorio);
        json = getJson();
        context = this;
        generosS = (Spinner) findViewById(R.id.spinner_generos);
        final Bundle bundle = getIntent().getExtras();
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
        t_busqueda = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_busqueda", "0").trim());
        toolbarS = (Toolbar) findViewById(R.id.toolbar_search);
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
        } else {
            Toolbar ltoolbar = (Toolbar) findViewById(R.id.toolbar_l);
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
                            } else {
                                getMenuInflater().inflate(R.menu.menu_buscar_cancelar_d, menuGlobal);
                            }
                            List<String> titulos = parser.DirTitulosBusquedaA(json, null);
                            List<String> tipos = parser.DirTiposBusquedaA(json, null);
                            List<String> index = parser.DirIndexBusquedaA(json, null);
                            List<String> titOrd = parser.DirTitulosBusquedaA(json, null);
                            Collections.sort(titOrd, String.CASE_INSENSITIVE_ORDER);
                            List<String> indexOrd = new ArrayList<String>(titOrd.size());
                            List<String> tiposOrd = new ArrayList<String>(titOrd.size());
                            List<String> links = new ArrayList<String>(titOrd.size());
                            for (String si : titOrd) {
                                int i = titulos.indexOf(si);
                                String indexn = index.get(i);
                                indexOrd.add(indexn);
                                String tipon = tipos.get(i);
                                tiposOrd.add(tipon);
                                String link = "http://cdn.animeflv.net/img/portada/thumb_80/" + indexn + ".jpg";
                                links.add(link);
                            }
                            AdapterBusqueda adapterBusqueda = new AdapterBusqueda(context, titOrd, tiposOrd, links, indexOrd);
                            recyclerView.setAdapter(adapterBusqueda);
                        }
                        break;
                    case R.id.buscar_cancelar:
                        if (bundle == null) {
                            cancelar();
                        } else {
                            finish();
                        }
                        break;
                    case R.id.search:
                        getSupportActionBar().setTitle("");
                        editText.setVisibility(View.VISIBLE);
                        generosS.setVisibility(View.GONE);
                        editText.setText("");
                        editText.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(editText, 0);
                        menuGlobal.clear();
                        if (!isXLargeScreen(context)) {
                            getMenuInflater().inflate(R.menu.menu_buscar_cancelar, menuGlobal);
                        } else {
                            getMenuInflater().inflate(R.menu.menu_buscar_cancelar_d, menuGlobal);
                        }
                        if (!isXLargeScreen(context)) {
                            linearLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            viewPager.setVisibility(View.GONE);
                            viewPagerTab.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        break;
                }
                return true;
            }
        });
        editText = (EditText) findViewById(R.id.et_busqueda);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(final Editable s) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (t_busqueda == 0) {
                            if (s.length() > 0) {
                                menuGlobal.clear();
                                if (!isXLargeScreen(context)) {
                                    getMenuInflater().inflate(R.menu.menu_buscar_borrar, menuGlobal);
                                } else {
                                    getMenuInflater().inflate(R.menu.menu_buscar_borrar_d, menuGlobal);
                                }
                            } else {
                                if (editText.getVisibility() == View.VISIBLE) {
                                    menuGlobal.clear();
                                    if (!isXLargeScreen(context)) {
                                        getMenuInflater().inflate(R.menu.menu_buscar_cancelar, menuGlobal);
                                    } else {
                                        getMenuInflater().inflate(R.menu.menu_buscar_cancelar_d, menuGlobal);
                                    }
                                }
                            }
                            List<AnimeClass> animes = parser.DirAllAnimes(json, s.toString());
                            Collections.sort(animes, new AnimeCompare());
                            /*List<String> titulos = parser.DirTitulosBusquedaA(json, s.toString());
                            List<String> tipos = parser.DirTiposBusquedaA(json, s.toString());
                            List<String> index = parser.DirIndexBusquedaA(json, s.toString());
                            List<String> titOrd = parser.DirTitulosBusquedaA(json, s.toString());
                            Collections.sort(titOrd, String.CASE_INSENSITIVE_ORDER);
                            List<String> indexOrd = new ArrayList<String>(titOrd.size());
                            List<String> tiposOrd = new ArrayList<String>(titOrd.size());
                            List<String> links = new ArrayList<String>(titOrd.size());
                            for (String si : titOrd) {
                                int i = titulos.indexOf(si);
                                String indexn = index.get(i);
                                indexOrd.add(indexn);
                                String tipon = tipos.get(i);
                                tiposOrd.add(tipon);
                                String link = "http://cdn.animeflv.net/img/portada/thumb_80/" + indexn + ".jpg";
                                links.add(link);
                            }
                            AdapterBusqueda adapterBusqueda = new AdapterBusqueda(context, titOrd, tiposOrd, links, indexOrd);*/
                            AdapterBusquedaNew adapterBusqueda = new AdapterBusquedaNew(context, animes);
                            recyclerView.setAdapter(adapterBusqueda);
                        } else {
                            if (s.length() > 0) {
                                menuGlobal.clear();
                                if (!isXLargeScreen(context)) {
                                    getMenuInflater().inflate(R.menu.menu_buscar_borrar, menuGlobal);
                                } else {
                                    getMenuInflater().inflate(R.menu.menu_buscar_borrar_d, menuGlobal);
                                }
                            } else {
                                menuGlobal.clear();
                                if (!isXLargeScreen(context)) {
                                    getMenuInflater().inflate(R.menu.menu_buscar_cancelar, menuGlobal);
                                } else {
                                    getMenuInflater().inflate(R.menu.menu_buscar_cancelar_d, menuGlobal);
                                }
                            }
                        }
                    }
                }, 500);
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
        listener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (t_busqueda == 1) {
                    Editable s = editText.getEditableText();
                    if (s.length() > 0) {
                        menuGlobal.clear();
                        if (!isXLargeScreen(context)) {
                            getMenuInflater().inflate(R.menu.menu_buscar_borrar, menuGlobal);
                        } else {
                            getMenuInflater().inflate(R.menu.menu_buscar_borrar_d, menuGlobal);
                        }
                    } else {
                        menuGlobal.clear();
                        if (!isXLargeScreen(context)) {
                            getMenuInflater().inflate(R.menu.menu_buscar_cancelar, menuGlobal);
                        } else {
                            getMenuInflater().inflate(R.menu.menu_buscar_cancelar_d, menuGlobal);
                        }
                    }
                    List<AnimeClass> animes = parser.DirAllAnimes(json, s.toString());
                    Collections.sort(animes, new AnimeCompare());
                    /*List<String> titulos = parser.DirTitulosBusquedaA(json, s.toString());
                    List<String> tipos = parser.DirTiposBusquedaA(json, s.toString());
                    List<String> index = parser.DirIndexBusquedaA(json, s.toString());
                    List<String> titOrd = parser.DirTitulosBusquedaA(json, s.toString());
                    Collections.sort(titOrd, String.CASE_INSENSITIVE_ORDER);
                    List<String> indexOrd = new ArrayList<String>(titOrd.size());
                    List<String> tiposOrd = new ArrayList<String>(titOrd.size());
                    List<String> links = new ArrayList<String>(titOrd.size());
                    for (String si : titOrd) {
                        int i = titulos.indexOf(si);
                        String indexn = index.get(i);
                        indexOrd.add(indexn);
                        String tipon = tipos.get(i);
                        tiposOrd.add(tipon);
                        String link = "http://cdn.animeflv.net/img/portada/thumb_80/" + indexn + ".jpg";
                        links.add(link);
                    }*/
                    //AdapterBusqueda adapterBusqueda = new AdapterBusqueda(context, titOrd, tiposOrd, links, indexOrd);
                    AdapterBusquedaNew adapterBusqueda = new AdapterBusquedaNew(context, animes);
                    recyclerView.setAdapter(adapterBusqueda);
                }
                return true;
            }
        };
        editText.setOnEditorActionListener(listener);
        if (t_busqueda == 1) {
            editText.setHint("Presiona Enter...");
        }
        getSharedPreferences("data", MODE_PRIVATE).edit().putInt("genero", 0).apply();
        linearLayout = (LinearLayout) findViewById(R.id.LY_dir);
        recyclerView = (RecyclerView) findViewById(R.id.rv_busqueda);
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
        String[] generos = {
                "Todos",
                "Acción",
                "Aventuras",
                "Carreras",
                "Comedia",
                "Cyberpunk",
                "Deportes",
                "Drama",
                "Ecchi",
                "Escolares",
                "Fantasía",
                "Ciencia Ficción",
                "Gore",
                "Harem",
                "Horror",
                "Josei",
                "Lucha",
                "Magia",
                "Mecha",
                "Militar",
                "Misterio",
                "Música",
                "Parodias",
                "Psicologico",
                "Recuerdos de la vida",
                "Romance",
                "Seinen",
                "Shojo",
                "Shonen",
                "Vampiros",
                "Yaoi",
                "Yuri",
                "Sobrenatural"};
        if (isXLargeScreen(context)) {
            ArrayAdapter<String> spinerA = new ArrayAdapter<String>(context, R.layout.spinner_generos_blanco, generos);
            generosS.setAdapter(spinerA);
        } else {
            ArrayAdapter<String> spinerA = new ArrayAdapter<String>(context, R.layout.spinner_generos_normal, generos);
            generosS.setAdapter(spinerA);
        }
        generosS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                getSharedPreferences("data", MODE_PRIVATE).edit().putInt("genero", position).apply();
                FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                        getSupportFragmentManager(), FragmentPagerItems.with(context)
                        .add("ANIMES", Animes.class)
                        .add("OVAS", Ovas.class)
                        .add("PELICULAS", Peliculas.class)
                        .create());
                viewPager.setAdapter(adapter);
            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        if (bundle != null) {
            if (!isXLargeScreen(context)) {
                try {
                    linearLayout.setVisibility(View.GONE);
                } catch (NullPointerException e) {
                    Toast.makeText(context, "Esta vista se hizo para tablets, por favor desactiva el modo landscape", Toast.LENGTH_SHORT).show();
                    finish();
                }
                recyclerView.setVisibility(View.VISIBLE);
            } else {
                viewPager.setVisibility(View.GONE);
                viewPagerTab.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
        List<AnimeClass> animes = parser.DirAllAnimes(json, null);
        Collections.sort(animes, new AnimeCompare());
        /*List<String> titulos = parser.DirTitulosBusquedaA(json, null);
        List<String> tipos = parser.DirTiposBusquedaA(json, null);
        List<String> index = parser.DirIndexBusquedaA(json, null);
        List<String> titOrd = parser.DirTitulosBusquedaA(json, null);
        Collections.sort(titOrd, String.CASE_INSENSITIVE_ORDER);
        List<String> indexOrd = new ArrayList<String>(titOrd.size());
        List<String> tiposOrd = new ArrayList<String>(titOrd.size());
        List<String> links = new ArrayList<String>(titOrd.size());
        for (String s : titOrd) {
            int i = titulos.indexOf(s);
            String indexn = index.get(i);
            indexOrd.add(indexn);
            String link = "http://cdn.animeflv.net/img/portada/thumb_80/" + indexn + ".jpg";
            links.add(link);
            String tipon = tipos.get(i);
            tiposOrd.add(tipon);
        }
        /*for (String st:indexOrd){
            String link="http://cdn.animeflv.net/img/portada/thumb_80/"+st+".jpg";
            links.add(link);
        }
        AdapterBusqueda adapterBusqueda = new AdapterBusqueda(this, titOrd, tiposOrd, links, indexOrd);*/
        AdapterBusquedaNew adapterBusqueda = new AdapterBusquedaNew(context, animes);
        recyclerView.setAdapter(adapterBusqueda);
    }

    @TargetApi(21)
    public void setUpAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

            Fade fade = new Fade();
            fade.setDuration(1000);
            getWindow().setEnterTransition(fade);

            Slide slide = new Slide();
            slide.setDuration(1000);
            getWindow().setReturnTransition(slide);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuGlobal = menu;
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            editText.setVisibility(View.GONE);
            //generosS.setVisibility(View.VISIBLE);
            generosS.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            menuGlobal.clear();
            if (!isXLargeScreen(context)) {
                getMenuInflater().inflate(R.menu.menu_main, menuGlobal);
            } else {
                getMenuInflater().inflate(R.menu.menu_main_dark, menuGlobal);
            }
            getSupportActionBar().setTitle("Directorio");
        } else {
            if (!isXLargeScreen(context)) {
                getMenuInflater().inflate(R.menu.menu_buscar_cancelar, menu);
            } else {
                getMenuInflater().inflate(R.menu.menu_buscar_cancelar_d, menu);
            }
        }
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
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
        File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt");
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
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

    public static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (!isXLargeScreen(getApplicationContext())) {
            return;
        }
    }

    public void cancelar() {
        editText.setText("");
        editText.setVisibility(View.GONE);
        //generosS.setVisibility(View.VISIBLE);
        generosS.setVisibility(View.GONE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        menuGlobal.clear();
        if (!isXLargeScreen(context)) {
            getMenuInflater().inflate(R.menu.menu_main, menuGlobal);
            invalidateOptionsMenu();
        } else {
            getMenuInflater().inflate(R.menu.menu_main_dark, menuGlobal);
            invalidateOptionsMenu();
        }
        getSupportActionBar().setTitle("Directorio");
        if (!isXLargeScreen(context)) {
            recyclerView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            viewPagerTab.setVisibility(View.VISIBLE);
        }
    }
}
