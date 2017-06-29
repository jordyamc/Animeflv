package knf.animeflv.Directorio;

import android.app.Activity;
import android.content.Context;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.Directorio.DB.DirectoryDB;
import knf.animeflv.Directorio.DB.DirectoryHelper;
import knf.animeflv.JsonFactory.BaseGetter;
import knf.animeflv.JsonFactory.JsonTypes.DIRECTORIO;
import knf.animeflv.R;
import knf.animeflv.Recyclers.AdapterBusquedaNew;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.Keys;
import knf.animeflv.Utils.NetworkUtils;
import knf.animeflv.Utils.SearchUtils;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.TrackingHelper;
import knf.animeflv.Utils.eNums.SearchType;
import xdroid.toaster.Toaster;

public class Directorio extends AppCompatActivity {
    @BindView(R.id.toolbar_search)
    Toolbar toolbarS;
    Menu menuGlobal;
    @BindView(R.id.et_busqueda)
    MaterialEditText editText;
    @BindView(R.id.rv_busqueda)
    RecyclerView recyclerView;
    @BindView(R.id.frame_dir)
    FrameLayout frameLayout;
    @BindView(R.id.load)
    ProgressBar loading;
    @BindView(R.id.load_progress)
    TextView load_prog;
    RelativeLayout linearLayout;
    Activity context;
    EditText.OnEditorActionListener listener;
    int t_busqueda;
    ViewPager viewPager;
    @BindView(R.id.viewpagertab2)
    SmartTabLayout viewPagerTab;
    @BindView(R.id.search_menu_text)
    FloatingActionButton nombre;
    @BindView(R.id.search_menu_generos)
    FloatingActionButton genero;
    @BindView(R.id.search_menu_id)
    FloatingActionButton byid;
    @BindView(R.id.search_menu)
    FloatingActionMenu actionMenu;
    List<Integer> lastSearch = Arrays.asList(new Integer[]{0});
    boolean loaded = false;
    private boolean prestarted = false;
    private boolean menuOpen = false;

    private int DELAY = 100;

    public static boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("force_phone", false) ? R.layout.directorio_force : R.layout.directorio);
        context = this;
        ButterKnife.bind(this);
        setSupportActionBar(toolbarS);
        ThemeUtils.Theme theme = ThemeUtils.Theme.create(this);
        if (!isXLargeScreen(getApplicationContext())) { //set phones to portrait;
            try {
                linearLayout = findViewById(R.id.LY_dir);
                linearLayout.setBackgroundColor(theme.background);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (isXLargeScreen(this)) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        toolbarS.setBackgroundColor(theme.primary);
        toolbarS.setTitleTextColor(theme.textColorToolbar);
        ThemeUtils.setNavigationColor(toolbarS, theme.toolbarNavigation);
        editText.setTextColor(theme.textColor);
        editText.setHintTextColor(theme.secondaryTextColor);
        editText.setBackgroundColor(ColorsRes.Transparent(this));
        load_prog.setTextColor(theme.secondaryTextColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(theme.primaryDark);
            if (!isXLargeScreen(this)) {
                getWindow().setNavigationBarColor(theme.primary);
            } else {
                getWindow().setNavigationBarColor(ColorsRes.Transparent(this));
            }
        }
        viewPagerTab.setSelectedIndicatorColors(theme.indicatorColor);
        viewPagerTab.setDefaultTabTextColor(theme.textColorToolbar);
        try {
            if (!isXLargeScreen(this)) {
                toolbarS.getRootView().setBackgroundColor(theme.background);
                viewPagerTab.setBackgroundColor(theme.primary);
            } else {
                actionMenu.setPadding(0, 0, 0, Keys.getNavBarSize(this));
                viewPagerTab.setBackgroundColor(theme.tablet_toolbar);
                toolbarS.getRootView().setBackgroundColor(theme.tablet_background);
                if (ThemeUtils.isTablet(this))
                    findViewById(R.id.cardMain).setBackgroundColor(theme.primary);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        final int size = DirectoryHelper.get(this).getAll().size();
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .progress(true, 0)
                .content("Creando directorio...\n\nAgregados: 0")
                .cancelable(false)
                .build();
        if (DirectoryHelper.get(this).isDirectoryValid()) {
            prestarted = true;
            toolbarS.setVisibility(View.VISIBLE);
            viewPagerTab.setVisibility(View.VISIBLE);
            init();
        } else {
            Toaster.toast("Creando directorio");
            dialog.show();
        }
        BaseGetter.getJson(this, new DIRECTORIO(), new BaseGetter.AsyncProgressDBInterface() {
            @Override
            public void onFinish(List<AnimeClass> list) {
                if (list.size() > 0) {
                    if (list.size() != size) {
                        if (prestarted) {
                            initAsync();
                        } else {
                            prestarted = true;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    toolbarS.setVisibility(View.VISIBLE);
                                    viewPagerTab.setVisibility(View.VISIBLE);
                                    loading.setVisibility(View.GONE);
                                    load_prog.setVisibility(View.GONE);
                                    dialog.dismiss();
                                    Toaster.toast("Directorio creado!");
                                    recreate();
                                }
                            });
                        }
                    }
                    loaded = true;
                    supportInvalidateOptionsMenu();
                } else {
                    if (!NetworkUtils.isNetworkAvailable()) {
                        Toaster.toast("Error al leer directorio de cache");
                    } else {
                        Toaster.toast("Error al abrir el directorio");
                    }
                    finish();
                }
            }

            @Override
            public void onProgress(final int progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String prog_text = progress >= 0 ? ("Animes agregados: " + progress) : "Creando lista...";
                        load_prog.setText(prog_text);
                        dialog.setContent("Creando directorio...\n\nAgregados: " + progress);
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                });
                if (throwable instanceof SocketTimeoutException) {
                    Toaster.toastLong("Error de red: Internet muy lento");
                    finish();
                } else {
                    Toaster.toast("Error al cargar directorio");
                    Toaster.toastLong(Log.getStackTraceString(throwable));
                    finish();
                }
            }
        });
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("use_tags", false))
            genero.hideButtonInMenu(true);
    }

    private void initAsync() {
        Log.e("InitAsync", "Prestarted: " + prestarted + " Loaded: " + loaded);
        final Bundle bundle = getIntent().getExtras();
        genero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMenu.close(false);
                actionMenu.getMenuIconView().setImageResource(R.drawable.ic_search_generos);
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
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
                                List<Integer> current = Arrays.asList(which);
                                if (!lastSearch.contains(0) && current.contains(0)) {
                                    lastSearch = Arrays.asList(new Integer[]{0});
                                    SearchConstructor.SetSearch(SearchType.GENEROS, which);
                                    dialog.setSelectedIndices(new Integer[]{0});
                                    editText.setHint("Generos: TODOS");
                                } else {
                                    List<Integer> l = new LinkedList<Integer>(Arrays.asList(which));
                                    if (l.contains(0)) {
                                        l.remove(0);
                                    }
                                    lastSearch = l;
                                    Integer[] nArray = new Integer[l.size()];
                                    l.toArray(nArray);
                                    dialog.setSelectedIndices(nArray);
                                    SearchConstructor.SetSearch(SearchType.GENEROS, nArray);
                                    editText.setHint("Generos: " + which.length);
                                }
                                List<AnimeClass> animes = SearchUtils.Search(Directorio.this, editText.getEditableText().toString());
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
                            ThemeUtils.setMenuColor(menuGlobal, ThemeUtils.Theme.get(Directorio.this, ThemeUtils.Theme.KEY_TOOLBAR_NAVIGATION));
                            List<AnimeClass> animes = SearchUtils.Search(Directorio.this, null);
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
                        ThemeUtils.setMenuColor(menuGlobal, ThemeUtils.Theme.get(Directorio.this, ThemeUtils.Theme.KEY_TOOLBAR_NAVIGATION));
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
                                ThemeUtils.setMenuColor(menuGlobal, ThemeUtils.Theme.get(Directorio.this, ThemeUtils.Theme.KEY_TOOLBAR_NAVIGATION));
                            } else {
                                if (editText.getVisibility() == View.VISIBLE) {
                                    menuGlobal.clear();
                                    if (!isXLargeScreen(context)) {
                                        getMenuInflater().inflate(R.menu.menu_buscar_cancelar, menuGlobal);
                                    } else {
                                        getMenuInflater().inflate(R.menu.menu_buscar_cancelar_d, menuGlobal);
                                    }
                                    ThemeUtils.setMenuColor(menuGlobal, ThemeUtils.Theme.get(Directorio.this, ThemeUtils.Theme.KEY_TOOLBAR_NAVIGATION));
                                }
                            }
                            List<AnimeClass> animes = SearchUtils.Search(Directorio.this, s.toString());
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
                            ThemeUtils.setMenuColor(menuGlobal, ThemeUtils.Theme.get(Directorio.this, ThemeUtils.Theme.KEY_TOOLBAR_NAVIGATION));
                        }
                    }
                }, DELAY);
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
                    ThemeUtils.setMenuColor(menuGlobal, ThemeUtils.Theme.get(Directorio.this, ThemeUtils.Theme.KEY_TOOLBAR_NAVIGATION));
                    List<AnimeClass> animes = SearchUtils.Search(Directorio.this, s.toString());
                    AdapterBusquedaNew adapterBusqueda = new AdapterBusquedaNew(context, animes);
                    recyclerView.setAdapter(adapterBusqueda);
                }
                return true;
            }
        };
        editText.setOnEditorActionListener(listener);
        List<AnimeClass> animes = SearchUtils.Search(Directorio.this, editText.getText().toString());
        final AdapterBusquedaNew adapterBusqueda = new AdapterBusquedaNew(context, animes);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (bundle == null) {
                        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                                getSupportFragmentManager(), FragmentPagerItems.with(Directorio.this)
                                .add("ANIMES", Animes.class)
                                .add("OVAS", Ovas.class)
                                .add("PELICULAS", Peliculas.class)
                                .create());
                        viewPager.setOffscreenPageLimit(3);
                        viewPager.setAdapter(adapter);
                        viewPagerTab.setViewPager(viewPager);
                        /*try {
                            ((Animes)adapter.getItem(0)).setDirectory(Directorio.this);
                            ((Ovas)adapter.getItem(1)).setDirectory(Directorio.this);
                            ((Peliculas)adapter.getItem(2)).setDirectory(Directorio.this);
                        }catch (Exception e){
                            Log.e("InitAsyncPages","Error",e);
                            //null
                        }*/
                    }
                    recyclerView.setAdapter(adapterBusqueda);
                    Toaster.toast("Directorio Actualizado");
                } catch (Exception e) {
                    Toaster.toast("Error en directorio, recargando...");
                    recreate();
                }
            }
        });
    }

    private void init() {
        Log.e("InitSync", "Prestarted: " + prestarted + " Loaded: " + loaded);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
                        menuOpen = opened;
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
                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
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
                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
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
                                        List<Integer> current = Arrays.asList(which);
                                        if (!lastSearch.contains(0) && current.contains(0)) {
                                            lastSearch = Arrays.asList(new Integer[]{0});
                                            SearchConstructor.SetSearch(SearchType.GENEROS, which);
                                            dialog.setSelectedIndices(new Integer[]{0});
                                            editText.setHint("Generos: TODOS");
                                        } else {
                                            List<Integer> l = new LinkedList<Integer>(Arrays.asList(which));
                                            if (l.contains(0)) {
                                                l.remove(0);
                                            }
                                            lastSearch = l;
                                            Integer[] nArray = new Integer[l.size()];
                                            l.toArray(nArray);
                                            dialog.setSelectedIndices(nArray);
                                            SearchConstructor.SetSearch(SearchType.GENEROS, nArray);
                                            editText.setHint("Generos: " + which.length);
                                        }
                                        List<AnimeClass> animes = SearchUtils.Search(Directorio.this, editText.getText().toString());
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
                    getSupportActionBar().setDisplayShowHomeEnabled(true);
                    ThemeUtils.setNavigationColor(toolbarS, ThemeUtils.Theme.get(Directorio.this, ThemeUtils.Theme.KEY_TOOLBAR_NAVIGATION));
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
                    try {
                        Toolbar ltoolbar = (Toolbar) findViewById(R.id.toolbar_l);
                        ThemeUtils.Theme theme = ThemeUtils.Theme.create(Directorio.this);
                        ltoolbar.setBackgroundColor(theme.tablet_toolbar);
                        ((Toolbar) findViewById(R.id.extra_toolbar)).setBackgroundColor(theme.tablet_toolbar);
                        ThemeUtils.setStatusBarPadding(Directorio.this, ltoolbar);
                        ltoolbar.setNavigationIcon(R.drawable.ic_back_r);
                        ThemeUtils.setNavigationColor(ltoolbar, ThemeUtils.Theme.get(Directorio.this, ThemeUtils.Theme.KEY_TOOLBAR_NAVIGATION));
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                toolbarS.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.re_make:
                                new MaterialDialog.Builder(Directorio.this)
                                        .content("Se recreara el directorio, esto puede tardar unos minutos")
                                        .positiveText("continuar")
                                        .negativeText("cancelar")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                try {
                                                    Keys.Dirs.CACHE_DIRECTORIO.delete();
                                                    new DirectoryDB(Directorio.this).reset();
                                                    recreate();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    recreate();
                                                }

                                            }
                                        }).build().show();
                                break;
                            case R.id.buscar_borrar:
                                if (editText.getText().length() > 0) {
                                    editText.setText("");
                                    menuGlobal.clear();
                                    if (!isXLargeScreen(context)) {
                                        getMenuInflater().inflate(R.menu.menu_buscar_cancelar, menuGlobal);
                                    } else {
                                        getMenuInflater().inflate(R.menu.menu_buscar_cancelar_d, menuGlobal);
                                    }
                                    List<AnimeClass> animes = SearchUtils.Search(Directorio.this, null);
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
                                try {
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
                                    ThemeUtils.setMenuColor(menuGlobal, ThemeUtils.Theme.get(Directorio.this, ThemeUtils.Theme.KEY_TOOLBAR_NAVIGATION));
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
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                        return true;
                    }
                });
                editText.setEnabled(true);
                editText.setClickable(true);
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void afterTextChanged(final Editable s) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (t_busqueda == 0) {
                                    if (s.length() > 0) {
                                        if (menuGlobal != null) {
                                            menuGlobal.clear();
                                            if (!isXLargeScreen(context)) {
                                                getMenuInflater().inflate(R.menu.menu_buscar_borrar, menuGlobal);
                                            } else {
                                                getMenuInflater().inflate(R.menu.menu_buscar_borrar_d, menuGlobal);
                                            }
                                            ThemeUtils.setMenuColor(menuGlobal, ThemeUtils.Theme.get(Directorio.this, ThemeUtils.Theme.KEY_TOOLBAR_NAVIGATION));
                                        }
                                    } else {
                                        if (editText.getVisibility() == View.VISIBLE) {
                                            if (menuGlobal != null) {
                                                menuGlobal.clear();
                                                if (!isXLargeScreen(context)) {
                                                    getMenuInflater().inflate(R.menu.menu_buscar_cancelar, menuGlobal);
                                                } else {
                                                    getMenuInflater().inflate(R.menu.menu_buscar_cancelar_d, menuGlobal);
                                                }
                                                ThemeUtils.setMenuColor(menuGlobal, ThemeUtils.Theme.get(Directorio.this, ThemeUtils.Theme.KEY_TOOLBAR_NAVIGATION));
                                            }
                                        }
                                    }
                                    List<AnimeClass> animes = SearchUtils.Search(Directorio.this, s.toString());
                                    AdapterBusquedaNew adapterBusqueda = new AdapterBusquedaNew(context, animes);
                                    recyclerView.setAdapter(adapterBusqueda);
                                } else {
                                    if (s.length() > 0) {
                                        if (menuGlobal != null) {
                                            menuGlobal.clear();
                                            if (!isXLargeScreen(context)) {
                                                getMenuInflater().inflate(R.menu.menu_buscar_borrar, menuGlobal);
                                            } else {
                                                getMenuInflater().inflate(R.menu.menu_buscar_borrar_d, menuGlobal);
                                            }
                                            ThemeUtils.setMenuColor(menuGlobal, ThemeUtils.Theme.get(Directorio.this, ThemeUtils.Theme.KEY_TOOLBAR_NAVIGATION));
                                        }
                                    } else {
                                        if (menuGlobal != null) {
                                            menuGlobal.clear();
                                            if (!isXLargeScreen(context)) {
                                                getMenuInflater().inflate(R.menu.menu_buscar_cancelar, menuGlobal);
                                            } else {
                                                getMenuInflater().inflate(R.menu.menu_buscar_cancelar_d, menuGlobal);
                                            }
                                            ThemeUtils.setMenuColor(menuGlobal, ThemeUtils.Theme.get(Directorio.this, ThemeUtils.Theme.KEY_TOOLBAR_NAVIGATION));
                                        }
                                    }
                                }
                            }
                        }, DELAY);
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
                            ThemeUtils.setMenuColor(menuGlobal, ThemeUtils.Theme.get(Directorio.this, ThemeUtils.Theme.KEY_TOOLBAR_NAVIGATION));
                            List<AnimeClass> animes = SearchUtils.Search(Directorio.this, s.toString());
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
                recyclerView.setLayoutManager(new LinearLayoutManager(Directorio.this));

                FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                        getSupportFragmentManager(), FragmentPagerItems.with(Directorio.this)
                        .add("ANIMES", Animes.class)
                        .add("OVAS", Ovas.class)
                        .add("PELICULAS", Peliculas.class)
                        .create());
                viewPager = (ViewPager) findViewById(R.id.viewpager2);
                viewPager.setOffscreenPageLimit(3);
                viewPager.setAdapter(adapter);
                viewPagerTab.setViewPager(viewPager);
                /*try {
                    ((Animes)adapter.getItem(0)).setDirectory(Directorio.this);
                    ((Ovas)adapter.getItem(1)).setDirectory(Directorio.this);
                    ((Peliculas)adapter.getItem(2)).setDirectory(Directorio.this);
                }catch (Exception e){
                    Log.e("InitPages","Error",e);
                    //null
                }*/
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
                List<AnimeClass> animes = SearchUtils.Search(Directorio.this, null);
                final AdapterBusquedaNew adapterBusqueda = new AdapterBusquedaNew(context, animes);
                recyclerView.setAdapter(adapterBusqueda);
            }
        });
    }

    private String[] getGeneros() {
        return SearchUtils.getGeneros();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuGlobal = menu;
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            editText.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            menuGlobal.clear();
            if (isXLargeScreen(this)) {
                if (ThemeUtils.isAmoled(this)) {
                    getMenuInflater().inflate(R.menu.menu_dir_dark, menuGlobal);
                } else {
                    getMenuInflater().inflate(R.menu.menu_dir, menuGlobal);
                }
            } else {
                getMenuInflater().inflate(R.menu.menu_dir, menuGlobal);
            }
            getSupportActionBar().setTitle("Directorio");
            if (!loaded) {
                menu.findItem(R.id.carg).setActionView(((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.prog_lay, null));
            } else {
                menu.removeItem(R.id.carg);
            }
        } else {
            if (!isXLargeScreen(context)) {
                getMenuInflater().inflate(R.menu.menu_buscar_cancelar, menu);
            } else {
                getMenuInflater().inflate(R.menu.menu_buscar_cancelar_d, menu);
            }
        }
        ThemeUtils.setMenuColor(menu, ThemeUtils.Theme.get(this, ThemeUtils.Theme.KEY_TOOLBAR_NAVIGATION));
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
        if (menuOpen) {
            actionMenu.close(true);
        } else if (loaded)
            super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (!isXLargeScreen(getApplicationContext())) {
            return;
        }
    }

    public void cancelar() {
        try {
            editText.setText("");
            editText.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            menuGlobal.clear();
            if (!isXLargeScreen(context)) {
                getMenuInflater().inflate(R.menu.menu_main, menuGlobal);
            } else {
                getMenuInflater().inflate(R.menu.menu_main_dark, menuGlobal);
            }
            supportInvalidateOptionsMenu();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        TrackingHelper.track(this, TrackingHelper.DIRECTORIO);
    }
}
