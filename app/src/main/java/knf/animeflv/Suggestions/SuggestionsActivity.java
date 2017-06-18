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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

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
                    .content("Para crear sugerencias se necesita la busqueda por generos activada, se recreara el directorio (puede tardar hasta 10 minutos)")
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
        if (suggestionsList != null)
            getMenuInflater().inflate(R.menu.menu_suggestions, menu);
        return true;
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
