package knf.animeflv.Suggestions;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.JsonFactory.BaseGetter;
import knf.animeflv.JsonFactory.SelfGetter;
import knf.animeflv.R;
import knf.animeflv.Random.AnimeObject;
import knf.animeflv.Suggestions.Algoritm.SuggestionDB;
import knf.animeflv.Suggestions.Algoritm.SuggestionHelper;
import knf.animeflv.Utils.Keys;
import knf.animeflv.Utils.SearchUtils;
import knf.animeflv.Utils.ThemeUtils;

/**
 * Created by Jordy on 21/04/2017.
 */

public class SuggestionsActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.advice)
    TextView advice;

    List<SuggestionDB.Suggestion> suggestionsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_suggestions);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sugeridos");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ThemeUtils.Theme theme = ThemeUtils.Theme.create(this);
        toolbar.setBackgroundColor(theme.primary);
        toolbar.getRootView().setBackgroundColor(theme.background);
        toolbar.setTitleTextColor(theme.textColorToolbar);
        ThemeUtils.setNavigationColor(toolbar, theme.toolbarNavigation);
        advice.setTextColor(theme.secondaryTextColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(theme.primaryDark);
            getWindow().setNavigationBarColor(theme.primary);
        }
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("use_tags", false)) {
            final MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .progress(true, 0)
                    .content("Recreando directorio...\n\nAgregados: 0")
                    .cancelable(false)
                    .build();
            new MaterialDialog.Builder(this)
                    .content("Para recibir sugerencias, se necesita la búsqueda por géneros activada. Ten presente que se tendrá que elaborar de nuevo el directorio de animes, esta operación puede tomar hasta diez minutos")
                    .positiveText("Activar")
                    .negativeText("Cancelar")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog d, @NonNull DialogAction which) {
                            d.dismiss();
                            dialog.show();
                            PreferenceManager.getDefaultSharedPreferences(SuggestionsActivity.this).edit().putBoolean("use_tags", true).apply();
                            Keys.Dirs.CACHE_DIRECTORIO.delete();
                            SelfGetter.getDir(SuggestionsActivity.this, new BaseGetter.AsyncProgressInterface() {
                                @Override
                                public void onFinish(String json) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                            startList();
                                        }
                                    });
                                }

                                @Override
                                public void onProgress(final int progress) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.setContent("Recreando directorio...\n\nAgregados: " + progress);
                                        }
                                    });
                                }

                                @Override
                                public void onError(Throwable throwable) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                            recyclerView.setVisibility(View.GONE);
                                            advice.setVisibility(View.VISIBLE);
                                            advice.setText("Error al recrear directorio :(");
                                        }
                                    });
                                }
                            });
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog d, @NonNull DialogAction which) {
                            d.dismiss();
                            finish();
                        }
                    }).build().show();
        } else {
            startList();
        }
    }

    private void startList() {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content("Creando lista de sugerencias...")
                .progress(true, 0)
                .cancelable(false)
                .build();
        dialog.show();
        SuggestionHelper.getSuggestions(this, new SuggestionHelper.SuggestionCreate() {
            @Override
            public void onListCreated(final List<AnimeObject> animes, List<SuggestionDB.Suggestion> suggestions) {
                suggestionsList = suggestions;
                supportInvalidateOptionsMenu();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SuggestionsAdapter adapter = new SuggestionsAdapter(SuggestionsActivity.this, animes);
                        //recyclerView.setLayoutManager(new StickyHeaderLayoutManager());
                        recyclerView.setLayoutManager(new LinearLayoutManager(SuggestionsActivity.this));
                        recyclerView.setAdapter(adapter);
                        dialog.dismiss();
                    }
                });
            }

            @Override
            public void onError(int code) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                });
                switch (code) {
                    case SuggestionHelper.DIRECTORY_ERROR_CODE:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setVisibility(View.GONE);
                                advice.setVisibility(View.VISIBLE);
                                advice.setText("Error al leer directorio :(");
                            }
                        });
                        break;
                    case SuggestionHelper.NO_INFO_CODE:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setVisibility(View.GONE);
                                advice.setVisibility(View.VISIBLE);
                            }
                        });
                        break;
                    default:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setVisibility(View.GONE);
                                advice.setVisibility(View.VISIBLE);
                                advice.setText("Error desconocido al crear lista de sugerencias :(");
                            }
                        });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (suggestionsList != null) {
            getMenuInflater().inflate(R.menu.menu_suggestions, menu);
            ThemeUtils.setMenuColor(menu, ThemeUtils.Theme.get(this, ThemeUtils.Theme.KEY_TOOLBAR_NAVIGATION));
        }
        return true;
    }

    private Integer[] getExcluded() {
        String[] genres = SearchUtils.getGenerosOnly();
        List<String> excluded = SuggestionHelper.getExcluded(this);
        List<Integer> integers = new ArrayList<>();
        for (int i = 0; i < genres.length; i++) {
            if (excluded.contains(genres[i]))
                integers.add(i);
        }
        return integers.toArray(new Integer[0]);
    }

    private void saveExcluded(Integer[] excluded) {
        String[] genres = SearchUtils.getGenerosOnly();
        List<String> ex = new ArrayList<>();
        for (int item : excluded) {
            ex.add(genres[item]);
        }
        SuggestionHelper.saveExcluded(this, TextUtils.join(";", ex.toArray(new String[0])));
        startList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.status:
                StringBuilder builder = new StringBuilder();
                for (SuggestionDB.Suggestion suggestion : suggestionsList) {
                    builder.append(suggestion.name);
                    builder.append(": ");
                    builder.append(suggestion.count);
                    builder.append("\n\n");
                }
                String status = builder.toString().trim();
                new MaterialDialog.Builder(this)
                        .content(status)
                        .positiveText("OK")
                        .neutralText("blacklist")
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                new MaterialDialog.Builder(SuggestionsActivity.this)
                                        .title("Blacklist")
                                        .titleGravity(GravityEnum.CENTER)
                                        .items(SearchUtils.getGenerosOnly())
                                        .itemsCallbackMultiChoice(getExcluded(), new MaterialDialog.ListCallbackMultiChoice() {
                                            @Override
                                            public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                                                saveExcluded(integers);
                                                return true;
                                            }
                                        })
                                        .positiveText("aceptar")
                                        .negativeText("cancelar").build().show();
                            }
                        })
                        .build().show();
                break;
            case R.id.re_make:
                new MaterialDialog.Builder(this)
                        .content("¿Desea resetear las estadisticas de los generos?")
                        .positiveText("resetear")
                        .negativeText("cancelar")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                SuggestionHelper.clear(SuggestionsActivity.this);
                                recreate();
                            }
                        }).build().show();
                break;
        }
        return true;
    }
}
