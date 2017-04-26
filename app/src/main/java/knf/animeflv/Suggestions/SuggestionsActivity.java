package knf.animeflv.Suggestions;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.R;
import knf.animeflv.Random.AnimeObject;
import knf.animeflv.Suggestions.Algoritm.SuggestionDB;
import knf.animeflv.Suggestions.Algoritm.SuggestionHelper;
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
        if (ThemeUtils.isAmoled(this)) {
            toolbar.setBackgroundColor(ColorsRes.Negro(this));
            toolbar.getRootView().setBackgroundColor(ColorsRes.Negro(this));
            advice.setTextColor(ColorsRes.SecondaryTextDark(this));
        } else {
            advice.setTextColor(ColorsRes.SecondaryTextLight(this));
        }
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
        }
        return true;
    }
}
