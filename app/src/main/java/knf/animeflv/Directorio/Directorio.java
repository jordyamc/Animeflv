package knf.animeflv.Directorio;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
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
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import knf.animeflv.AnimeSorter;
import knf.animeflv.Application;
import knf.animeflv.ColorsRes;
import knf.animeflv.JsonFactory.BaseGetter;
import knf.animeflv.JsonFactory.JsonTypes.DIRECTORIO;
import knf.animeflv.JsonFactory.OfflineGetter;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Recyclers.AdapterBusquedaNew;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.SearchUtils;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.eNums.SearchType;
import xdroid.toaster.Toaster;

public class Directorio extends AppCompatActivity {
    @Bind(R.id.toolbar_search)
    Toolbar toolbarS;
    Menu menuGlobal;
    @Bind(R.id.et_busqueda)
    MaterialEditText editText;
    @Bind(R.id.rv_busqueda)
    RecyclerView recyclerView;
    @Bind(R.id.frame_dir)
    FrameLayout frameLayout;
    RelativeLayout linearLayout;
    Activity context;
    EditText.OnEditorActionListener listener;
    int t_busqueda;
    ViewPager viewPager;
    @Bind(R.id.viewpagertab2)
    SmartTabLayout viewPagerTab;
    @Bind(R.id.search_menu_text)
    FloatingActionButton nombre;
    @Bind(R.id.search_menu_generos)
    FloatingActionButton genero;
    @Bind(R.id.search_menu_id)
    FloatingActionButton byid;
    @Bind(R.id.search_menu)
    FloatingActionMenu actionMenu;
    boolean loaded = false;

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
        //setUpAnimations();
        setContentView(R.layout.directorio);
        context = this;
        ButterKnife.bind(this);
        setSupportActionBar(toolbarS);
        if (!isXLargeScreen(getApplicationContext())) { //set phones to portrait;
            linearLayout = (RelativeLayout) findViewById(R.id.LY_dir);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (isXLargeScreen(this)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    getWindow().setFlags(
                            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                }
            }
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (ThemeUtils.isAmoled(this)) {
            toolbarS.setBackgroundColor(getResources().getColor(android.R.color.black));
            editText.setTextColor(ColorsRes.Blanco(this));
            editText.setHintTextColor(ColorsRes.SecondaryTextDark(this));
            editText.setHighlightColor(ColorsRes.Negro(this));
            if (!isXLargeScreen(this)) {
                toolbarS.getRootView().setBackgroundColor(getResources().getColor(R.color.negro));
                viewPagerTab.setBackgroundColor(getResources().getColor(android.R.color.black));
                viewPagerTab.setSelectedIndicatorColors(getResources().getColor(R.color.prim));
            } else {
                toolbarS.getRootView().setBackgroundColor(ColorsRes.Prim(this));
                findViewById(R.id.cardMain).setBackgroundColor(ColorsRes.Negro(this));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (!isXLargeScreen(this)) {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.negro));
                    getWindow().setNavigationBarColor(getResources().getColor(R.color.negro));
                } else {
                    getWindow().setStatusBarColor(ColorsRes.Prim(this));
                    getWindow().setNavigationBarColor(ColorsRes.Transparent(this));
                }
            }
        } else {
            editText.setTextColor(ColorsRes.Negro(this));
            editText.setHintTextColor(ColorsRes.SecondaryTextLight(this));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (!isXLargeScreen(this)) {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.dark));
                    getWindow().setNavigationBarColor(getResources().getColor(R.color.prim));
                } else {
                    getWindow().setStatusBarColor(ColorsRes.Dark(this));
                    getWindow().setNavigationBarColor(ColorsRes.Transparent(this));
                }
            }
        }
        final String j = OfflineGetter.getDirectorio();
        if (!j.equals("null")) {
            toolbarS.setVisibility(View.VISIBLE);
            viewPagerTab.setVisibility(View.VISIBLE);
            init(j);
        }
        BaseGetter.getJson(this, new DIRECTORIO(), new BaseGetter.AsyncInterface() {
            @Override
            public void onFinish(String json) {
                if (!json.equals("null")) {
                    if (!json.equals(j)) {
                        if (new Parser().checkStatus(json) == new Parser().checkStatus(j))
                            initAsync(json);
                    }
                    loaded = true;
                    invalidateOptionsMenu();
                } else {
                    Toaster.toast("Error al abrir el directorio");
                    finish();
                }
            }
        });
    }

    private void initAsync(final String json) {
        final Bundle bundle = getIntent().getExtras();
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
                        .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
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
                                List<AnimeClass> animes = AnimeSorter.sort(Directorio.this, SearchUtils.Search(json, editText.getEditableText().toString()));
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
                            List<AnimeClass> animes = AnimeSorter.sort(Directorio.this, SearchUtils.Search(json, null));
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
                            List<AnimeClass> animes = AnimeSorter.sort(Directorio.this, SearchUtils.Search(json, s.toString()));
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
                    List<AnimeClass> animes = AnimeSorter.sort(Directorio.this, SearchUtils.Search(json, s.toString()));
                    AdapterBusquedaNew adapterBusqueda = new AdapterBusquedaNew(context, animes);
                    recyclerView.setAdapter(adapterBusqueda);
                }
                return true;
            }
        };
        editText.setOnEditorActionListener(listener);
        if (bundle == null) {
            Bundle b = new Bundle();
            b.putString("json", json);
            FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                    getSupportFragmentManager(), FragmentPagerItems.with(this)
                    .add("ANIMES", Animes.class, b)
                    .add("OVAS", Ovas.class, b)
                    .add("PELICULAS", Peliculas.class, b)
                    .create());
            viewPager.setOffscreenPageLimit(3);
            viewPager.setAdapter(adapter);
            viewPagerTab.setViewPager(viewPager);
        }
        List<AnimeClass> animes = AnimeSorter.sort(Directorio.this, SearchUtils.Search(json, editText.getText().toString()));
        AdapterBusquedaNew adapterBusqueda = new AdapterBusquedaNew(context, animes);
        recyclerView.setAdapter(adapterBusqueda);
        Toaster.toast("Directorio Actualizado");
    }

    private void init(final String json) {
        final Bundle bundle = getIntent().getExtras();
        t_busqueda = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_busqueda", "0").trim());
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
                        .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
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
                                List<AnimeClass> animes = AnimeSorter.sort(Directorio.this, SearchUtils.Search(json, editText.getEditableText().toString()));
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
            ThemeUtils.setStatusBarPadding(this, ltoolbar);
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
                            List<AnimeClass> animes = AnimeSorter.sort(Directorio.this, SearchUtils.Search(json, null));
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
        editText.setEnabled(true);
        editText.setClickable(true);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
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
                            List<AnimeClass> animes = AnimeSorter.sort(Directorio.this, SearchUtils.Search(json, s.toString()));
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
                    List<AnimeClass> animes = AnimeSorter.sort(Directorio.this, SearchUtils.Search(json, s.toString()));
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
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Bundle b = new Bundle();
        b.putString("json", json);
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("ANIMES", Animes.class, b)
                .add("OVAS", Ovas.class, b)
                .add("PELICULAS", Peliculas.class, b)
                .create());
        viewPager = (ViewPager) findViewById(R.id.viewpager2);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);
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
        List<AnimeClass> animes = AnimeSorter.sort(Directorio.this, SearchUtils.Search(json, null));
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
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            menuGlobal.clear();
            if (!isXLargeScreen(context)) {
                getMenuInflater().inflate(R.menu.menu_main, menuGlobal);
            } else {
                getMenuInflater().inflate(R.menu.menu_main_dark, menuGlobal);
            }
            getSupportActionBar().setTitle("Directorio");
            if (!loaded) {
                menu.findItem(R.id.carg).setActionView(((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.prog_lay, null));
            } else {
                menu.removeItem(R.id.carg);
            }
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
        return ThemeUtils.getAcentColor(this);
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
