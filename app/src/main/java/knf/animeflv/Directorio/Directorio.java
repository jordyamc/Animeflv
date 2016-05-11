package knf.animeflv.Directorio;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import knf.animeflv.AnimeCompare;
import knf.animeflv.Application;
import knf.animeflv.ColorsRes;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Recyclers.AdapterBusquedaNew;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.SearchUtils;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.eNums.SearchType;
import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 29/08/2015.
 */
public class Directorio extends AppCompatActivity {
    Toolbar toolbarS;
    Menu menuGlobal;
    EditText editText;
    RecyclerView recyclerView;
    FrameLayout frameLayout;
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
    FloatingActionButton nombre;
    FloatingActionButton genero;
    FloatingActionButton byid;
    FloatingActionMenu actionMenu;

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
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
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
        nombre = (FloatingActionButton) findViewById(R.id.search_menu_text);
        genero = (FloatingActionButton) findViewById(R.id.search_menu_generos);
        byid = (FloatingActionButton) findViewById(R.id.search_menu_id);
        actionMenu = (FloatingActionMenu) findViewById(R.id.search_menu);
        setSupportActionBar(toolbarS);
        actionMenu.getMenuIconView().setImageResource(R.drawable.ic_search_text);
        actionMenu.setClosedOnTouchOutside(true);
        actionMenu.setMenuButtonColorNormal(getColor());
        actionMenu.setMenuButtonColorPressed(getColor());
        nombre.setColorNormal(getColor());
        nombre.setColorPressed(getColor());
        genero.setColorNormal(getColor());
        genero.setColorPressed(getColor());
        byid.setColorNormal(getColor());
        byid.setColorPressed(getColor());
        SearchConstructor.SetSearch(SearchType.NOMBRE, null);
        actionMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, 0);
                }
            }
        });
        nombre.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toaster.toast("Buscar por Nombre");
                return false;
            }
        });
        genero.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toaster.toast("Buscar por Generos");
                return false;
            }
        });
        byid.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toaster.toast("Buscar por ID");
                return false;
            }
        });
        nombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (t_busqueda == 1) {
                    editText.setHint("Presiona Enter...");
                } else {
                    editText.setHint("Buscar...");
                }
                actionMenu.close(true);
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                SearchConstructor.SetSearch(SearchType.NOMBRE, null);
                actionMenu.getMenuIconView().setImageResource(R.drawable.ic_search_text);
                editText.setText(editText.getEditableText().toString());
            }
        });
        genero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMenu.close(false);
                actionMenu.getMenuIconView().setImageResource(R.drawable.ic_search_generos);
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                SearchConstructor.SetSearch(SearchType.GENEROS, SearchConstructor.getGenerosInt());
                editText.setText(editText.getEditableText().toString());
                editText.setHint("Generos: TODOS");
                new MaterialDialog.Builder(context)
                        .items(getGeneros())
                        .autoDismiss(false)
                        .cancelable(false)
                        .positiveText("OK")
                        .alwaysCallMultiChoiceCallback()
                        .itemsCallbackMultiChoice(SearchConstructor.getGenerosInt(), new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                if (Arrays.asList(which).contains(0)) {
                                    SearchConstructor.SetSearch(SearchType.GENEROS, which);
                                    dialog.setSelectedIndices(new Integer[]{0});
                                    editText.setHint("Generos: TODOS");
                                } else {
                                    SearchConstructor.SetSearch(SearchType.GENEROS, which);
                                    editText.setHint("Generos: " + which.length);
                                }
                                List<AnimeClass> animes = SearchUtils.Search(json, editText.getEditableText().toString());
                                Collections.sort(animes, new AnimeCompare());
                                AdapterBusquedaNew adapterBusqueda = new AdapterBusquedaNew(context, animes);
                                recyclerView.setAdapter(adapterBusqueda);
                                return true;
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                editText.setText(editText.getEditableText().toString());
                                dialog.dismiss();
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.showSoftInput(editText, 0);
                                editText.requestFocus();
                            }
                        }).build().show();
            }
        });
        byid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (t_busqueda == 1) {
                    editText.setHint("Presiona Enter...");
                } else {
                    editText.setHint("Buscar...");
                }
                actionMenu.close(true);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                SearchConstructor.SetSearch(SearchType.ID, null);
                actionMenu.getMenuIconView().setImageResource(R.drawable.ic_search_id);
                if (FileUtil.isNumber(editText.getEditableText().toString().trim())) {
                    editText.setText(editText.getEditableText().toString());
                } else {
                    editText.setText("");
                }
            }
        });
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
                            List<AnimeClass> animes = SearchUtils.Search(json, null);
                            Collections.sort(animes, new AnimeCompare());
                            AdapterBusquedaNew adapterBusqueda = new AdapterBusquedaNew(context, animes);
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
                            frameLayout.setVisibility(View.VISIBLE);
                        } else {
                            viewPager.setVisibility(View.GONE);
                            viewPagerTab.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            frameLayout.setVisibility(View.VISIBLE);
                        }
                        break;
                }
                return true;
            }
        });
        editText = (EditText) findViewById(R.id.et_busqueda);
        viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab2);
        editText.setEnabled(true);
        editText.setClickable(true);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("is_amoled", false)) {
            toolbarS.setBackgroundColor(getResources().getColor(android.R.color.black));
            toolbarS.getRootView().setBackgroundColor(getResources().getColor(R.color.negro));
            viewPagerTab.setBackgroundColor(getResources().getColor(android.R.color.black));
            viewPagerTab.setSelectedIndicatorColors(getResources().getColor(R.color.prim));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(getResources().getColor(R.color.negro));
                getWindow().setNavigationBarColor(getResources().getColor(R.color.negro));
            }
        }
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
                            List<AnimeClass> animes = SearchUtils.Search(json, s.toString());
                            Collections.sort(animes, new AnimeCompare());
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
                }, 150);
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
                    List<AnimeClass> animes = SearchUtils.Search(json, s.toString());
                    Collections.sort(animes, new AnimeCompare());
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
        frameLayout = (FrameLayout) findViewById(R.id.frame_dir);
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("ANIMES", Animes.class)
                .add("OVAS", Ovas.class)
                .add("PELICULAS", Peliculas.class)
                .create());
        viewPager = (ViewPager) findViewById(R.id.viewpager2);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
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
                frameLayout.setVisibility(View.VISIBLE);
            } else {
                viewPager.setVisibility(View.GONE);
                viewPagerTab.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                frameLayout.setVisibility(View.VISIBLE);
            }
        }
        List<AnimeClass> animes = SearchUtils.Search(json, null);
        Collections.sort(animes, new AnimeCompare());
        AdapterBusquedaNew adapterBusqueda = new AdapterBusquedaNew(context, animes);
        recyclerView.setAdapter(adapterBusqueda);
    }

    private String[] getGeneros() {
        String[] generos = {
                "Todos",
                "Accion",
                "Aventuras",
                "Carreras",
                "Comedia",
                "Cyberpunk",
                "Deportes",
                "Drama",
                "Ecchi",
                "Escolares",
                "Fantasía",
                "Ciencia Ficcion",
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
                "Recuentos de la vida",
                "Romance",
                "Seinen",
                "Shojo",
                "Shonen",
                "Sin Genero",
                "Sobrenatural",
                "Vampiros",
                "Yaoi",
                "Yuri"};
        return generos;
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
            Application application = (Application) getApplication();
            Tracker mTracker = application.getDefaultTracker();
            mTracker.setScreenName("Directorio");
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
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
            Application application = (Application) getApplication();
            Tracker mTracker = application.getDefaultTracker();
            mTracker.setScreenName("Busqueda");
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
            if (!isXLargeScreen(context)) {
                getMenuInflater().inflate(R.menu.menu_buscar_cancelar, menu);
            } else {
                getMenuInflater().inflate(R.menu.menu_buscar_cancelar_d, menu);
            }
        }
        return true;
    }

    private int getColor() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int accent = preferences.getInt("accentColor", ColorsRes.Naranja(context));
        int color = ColorsRes.Naranja(context);
        if (accent == ColorsRes.Rojo(context)) {
            color = ColorsRes.Rojo(context);
        }
        if (accent == ColorsRes.Naranja(context)) {
            color = ColorsRes.Naranja(context);
        }
        if (accent == ColorsRes.Gris(context)) {
            color = ColorsRes.Gris(context);
        }
        if (accent == ColorsRes.Verde(context)) {
            color = ColorsRes.Verde(context);
        }
        if (accent == ColorsRes.Rosa(context)) {
            color = ColorsRes.Rosa(context);
        }
        if (accent == ColorsRes.Morado(context)) {
            color = ColorsRes.Morado(context);
        }
        return color;
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
            frameLayout.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
            frameLayout.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            viewPagerTab.setVisibility(View.VISIBLE);
        }
    }
}
