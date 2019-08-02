package knf.animeflv.Favorites;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import org.cryse.widget.persistentsearch.HomeButton;
import org.cryse.widget.persistentsearch.PersistentSearchView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.FavSync.FavSyncHelper;
import knf.animeflv.FavSync.SyncActivity;
import knf.animeflv.FavSyncro;
import knf.animeflv.LoginActivity.DropboxManager;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Utils.Keys;
import knf.animeflv.Utils.NetworkUtils;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.TrackingHelper;
import xdroid.toaster.Toaster;

public class FavoriteMain extends AppCompatActivity implements FavoriteAdapter.ListListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.edit)
    FloatingActionButton button;

    @BindView(R.id.searchview)
    PersistentSearchView searchView;

    @BindView(R.id.no_data)
    LinearLayout no_data;
    @BindView(R.id.img_no_data)
    ImageView img_no_data;
    @BindView(R.id.txt_no_data)
    TextView txt_no_data;

    private FavoriteAdapter adapter;

    private boolean cloud_updated = false;

    private boolean search_opened = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("force_phone", false) ? R.layout.favs_force : R.layout.favs);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Favoritos");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        searchView.hideSuggestions();
        ((EditText) searchView.findViewById(org.cryse.widget.persistentsearch.R.id.edittext_search)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setAdapter(new FavoriteAdapter(FavoriteMain.this, s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchView.setSearchListener(new PersistentSearchView.SearchListener() {
            @Override
            public void onSearchCleared() {

            }

            @Override
            public void onSearchTermChanged(String s) {

            }

            @Override
            public void onSearch(String s) {
                setAdapter(new FavoriteAdapter(FavoriteMain.this, s));
            }

            @Override
            public void onSearchEditOpened() {

            }

            @Override
            public void onSearchEditClosed() {

            }

            @Override
            public boolean onSearchEditBackPressed() {
                return false;
            }

            @Override
            public void onSearchExit() {
                search_opened = false;
                adapter.setSearching(false);
                setAdapter(new FavoriteAdapter(FavoriteMain.this));
            }
        });
        searchView.closeSearch();
        ThemeUtils.Theme theme = ThemeUtils.Theme.create(this);
        ThemeUtils.setNavigationColor(toolbar, theme.toolbarNavigation);
        toolbar.setTitleTextColor(theme.textColorToolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(theme.primaryDark);
            getWindow().setNavigationBarColor(theme.primary);
        }
        if (ThemeUtils.isTablet(this)) {
            findViewById(R.id.l_toolbar).setBackgroundColor(theme.tablet_toolbar);
            findViewById(R.id.cardMain).setBackgroundColor(theme.primary);
            toolbar.getRootView().setBackgroundColor(theme.tablet_background);
            toolbar.setTitleTextColor(ColorsRes.Blanco(this));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
        } else {
            toolbar.getRootView().setBackgroundColor(theme.background);
        }
        if (ThemeUtils.isTablet(this))
            if (theme.isDark) {
                ((HomeButton) searchView.findViewById(org.cryse.widget.persistentsearch.R.id.button_home)).setArrowDrawableColor(ColorsRes.Holo_Light(this));
                ((CardView) searchView.findViewById(org.cryse.widget.persistentsearch.R.id.cardview_search)).setCardBackgroundColor(ColorsRes.Blanco(this));
                ((EditText) searchView.findViewById(org.cryse.widget.persistentsearch.R.id.edittext_search)).setTextColor(ColorsRes.Negro(this));
                ((EditText) searchView.findViewById(org.cryse.widget.persistentsearch.R.id.edittext_search)).setHintTextColor(ColorsRes.SecondaryTextLight(this));
                ((ImageView) searchView.findViewById(org.cryse.widget.persistentsearch.R.id.button_mic)).setColorFilter(ColorsRes.Holo_Light(this));
            } else {
                ((HomeButton) searchView.findViewById(org.cryse.widget.persistentsearch.R.id.button_home)).setArrowDrawableColor(ColorsRes.Blanco(this));
                ((CardView) searchView.findViewById(org.cryse.widget.persistentsearch.R.id.cardview_search)).setCardBackgroundColor(ColorsRes.Dark(this));
                ((EditText) searchView.findViewById(org.cryse.widget.persistentsearch.R.id.edittext_search)).setTextColor(ColorsRes.Blanco(this));
                ((EditText) searchView.findViewById(org.cryse.widget.persistentsearch.R.id.edittext_search)).setHintTextColor(ColorsRes.SecondaryTextDark(this));
                ((ImageView) searchView.findViewById(org.cryse.widget.persistentsearch.R.id.button_mic)).setColorFilter(ColorsRes.Blanco(this));
            }
        toolbar.setBackgroundColor(theme.primary);
        button.setBackgroundColor(theme.accent);
        try {
            final MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .progress(true, 0)
                    .content("Actualizando al nuevo formato")
                    .cancelable(false)
                    .build();
            dialog.show();
            new FavotiteDB(this).updateOldData(new FavotiteDB.updateDataInterface() {
                @Override
                public void onFinish() {
                    startAsync();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAdapter(final FavoriteAdapter favoriteAdapter) {
        try {
            final ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
                private ThemeUtils.Theme theme = ThemeUtils.Theme.create(FavoriteMain.this);
                private boolean first = true;
                private boolean last = false;

                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    favoriteAdapter.onMoveItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                    return true;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                }

                @Override
                public int getDragDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                    if (viewHolder.getAdapterPosition() == 0 || adapter.list.get(viewHolder.getAdapterPosition()).isSection)
                        return 0;
                    return ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                }

                @Override
                public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
                    return target.getAdapterPosition() != 0;
                }

                @Override
                public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                    FavoriteAdapter.FavViewHolder holder = (FavoriteAdapter.FavViewHolder) viewHolder;
                    if (first) {
                        ViewPropertyAnimator animator = holder.cardView.animate();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.cardView.setCardElevation(8);
                            animator.start();
                        } else {
                            holder.cardView.setCardBackgroundColor(theme.accent);
                            holder.name.setTextColor(ColorsRes.Blanco(FavoriteMain.this));
                        }
                        first = false;
                    }
                    if (last) {
                        ViewPropertyAnimator animator = holder.cardView.animate();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.cardView.setCardElevation(1);
                            animator.start();
                        } else {
                            holder.cardView.setCardBackgroundColor(theme.card_normal);
                            holder.name.setTextColor(theme.textColor);
                        }
                        last = false;
                        first = true;
                    }
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }

                @Override
                public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                    super.clearView(recyclerView, viewHolder);
                    last = true;
                }
            });
            adapter = favoriteAdapter;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView.setLayoutManager(new LinearLayoutManager(FavoriteMain.this));
                    recyclerView.setAdapter(adapter);
                    if (ThemeUtils.isTablet(FavoriteMain.this)) {
                        recyclerView.setPadding(0, (int) Parser.toPx(FavoriteMain.this, 10), 0, Keys.getNavBarSize(FavoriteMain.this));
                        recyclerView.setClipToPadding(false);
                    }
                    try {
                        helper.attachToRecyclerView(recyclerView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (PreferenceManager.getDefaultSharedPreferences(FavoriteMain.this).getBoolean("section_favs", false)) {
                        if (search_opened) {
                            button.setVisibility(View.GONE);
                        } else {
                            button.setVisibility(View.VISIBLE);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (adapter.isEditing()) {
                                        button.setImageResource(R.drawable.move);
                                        adapter.setEditing(false);
                                    } else {
                                        button.setImageResource(R.drawable.check);
                                        adapter.setEditing(true);
                                    }
                                }
                            });
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startAsync() {
        setAdapter(new FavoriteAdapter(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("section_favs", false)) {
            if (ThemeUtils.isTablet(this)) {
                if (ThemeUtils.isAmoled(this)) {
                    getMenuInflater().inflate(R.menu.menu_fav_new_dark, menu);
                } else {
                    getMenuInflater().inflate(R.menu.menu_fav_new, menu);
                }
            } else {
                getMenuInflater().inflate(R.menu.menu_fav_new, menu);
            }
        } else {
            if (ThemeUtils.isTablet(this)) {
                if (ThemeUtils.isAmoled(this)) {
                    getMenuInflater().inflate(R.menu.menu_fav_old_dark, menu);
                } else {
                    getMenuInflater().inflate(R.menu.menu_fav_old, menu);
                }
            } else {
                getMenuInflater().inflate(R.menu.menu_fav_old, menu);
            }
        }
        if (!FavSyncro.isLogedIn(this) || !NetworkUtils.isNetworkAvailable())
            menu.removeItem(R.id.sync);
        ThemeUtils.setMenuColor(menu, ThemeUtils.Theme.get(this, ThemeUtils.Theme.KEY_TOOLBAR_NAVIGATION));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                MaterialDialog dialog = new MaterialDialog.Builder(this)
                        .title("Nombre")
                        .titleGravity(GravityEnum.CENTER)
                        .input("Nombre de sección", "", false, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                new FavotiteDB(FavoriteMain.this).addSection(new FavObject(input.toString())).close();
                                adapter.onListChanged();
                            }
                        })
                        .inputRange(3, 25)
                        .build();
                dialog.show();
                break;
            case R.id.sync:
                final MaterialDialog d = new MaterialDialog.Builder(this)
                        .content("Obteniendo favoritos...")
                        .progress(true, 0)
                        .cancelable(false)
                        .build();
                d.show();
                FavSyncHelper.recreate(this, new FavSyncHelper.SyncListener() {
                    @Override
                    public void onSync() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                d.dismiss();
                                if (FavSyncHelper.isSame) {
                                    Toaster.toast("Los favoritos son iguales!!!");
                                } else {
                                    startActivityForResult(new Intent(FavoriteMain.this, SyncActivity.class), 55447);
                                }
                            }
                        });
                    }
                });
                /*FavSyncro.updateLocal(this, new FavSyncro.UpdateCallback() {
                    @Override
                    public void onUpdate() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cloud_updated = true;
                                d.dismiss();
                                recreate();
                            }
                        });
                    }
                });*/
                break;
            case R.id.search:
                toogleSearch();
                break;
        }
        return true;
    }

    private void toogleSearch() {
        if (search_opened) {
            searchView.closeSearch();
            search_opened = false;
        } else {
            adapter.setSearching(true);
            searchView.openSearch();
            search_opened = true;
        }
    }

    @Override
    public void onListCreated(final List<FavObject> list, @Nullable final String search) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (list.size() == 0) {
                    no_data.setVisibility(View.VISIBLE);
                    img_no_data.setImageResource(ThemeUtils.getFlatImage(FavoriteMain.this));
                    if (search == null) {
                        txt_no_data.setText("No tienes animes favoritos");
                    } else {
                        txt_no_data.setText("No se encontró el anime: \"" + search + "\"");
                    }
                } else {
                    no_data.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (search_opened) {
            toogleSearch();
            setAdapter(new FavoriteAdapter(this));
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 55447) {
            recreate();
        }
    }

    @Override
    protected void onDestroy() {
        if (!cloud_updated)
            DropboxManager.updateFavs(getApplicationContext(), null);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TrackingHelper.track(this, TrackingHelper.FAVORITOS);
    }
}
