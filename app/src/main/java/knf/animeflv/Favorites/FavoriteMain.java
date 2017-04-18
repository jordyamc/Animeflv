package knf.animeflv.Favorites;

import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.FavSyncro;
import knf.animeflv.LoginActivity.DropboxManager;
import knf.animeflv.R;
import knf.animeflv.Utils.NetworkUtils;
import knf.animeflv.Utils.ThemeUtils;

public class FavoriteMain extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.edit)
    FloatingActionButton button;

    private RecyclerViewDragDropManager dragDropManager;
    private RecyclerView.Adapter wraped;
    private FavoriteAdapter adapter;

    private boolean cloud_updated = false;

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
        if (ThemeUtils.isAmoled(this)) {
            if (ThemeUtils.isTablet(this)) {
                findViewById(R.id.l_toolbar).setBackgroundColor(ColorsRes.Negro(this));
                findViewById(R.id.cardMain).setBackgroundColor(ColorsRes.Negro(this));
                toolbar.getRootView().setBackgroundColor(ColorsRes.Prim(this));
            } else {
                toolbar.getRootView().setBackgroundColor(ColorsRes.Negro(this));
            }
            toolbar.setBackgroundColor(ColorsRes.Negro(this));
        }
        button.setBackgroundColor(ThemeUtils.getAcentColor(this));
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

    private void startAsync() {
        dragDropManager = new RecyclerViewDragDropManager();
        dragDropManager.setInitiateOnLongPress(true);
        dragDropManager.setInitiateOnMove(false);
        dragDropManager.setDraggingItemShadowDrawable(
                (NinePatchDrawable) ContextCompat.getDrawable(this, R.drawable.material_shadow_z3));

        adapter = new FavoriteAdapter(this);
        wraped = dragDropManager.createWrappedAdapter(adapter);
        final GeneralItemAnimator animator = new DraggableItemAnimator();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recyclerView.setLayoutManager(new LinearLayoutManager(FavoriteMain.this));
                recyclerView.setAdapter(wraped);
                recyclerView.setItemAnimator(animator);

                if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
                    recyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) ContextCompat.getDrawable(FavoriteMain.this, R.drawable.material_shadow_z1)));
                }
                dragDropManager.attachRecyclerView(recyclerView);
                if (PreferenceManager.getDefaultSharedPreferences(FavoriteMain.this).getBoolean("section_favs", false)) {
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
        });
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                MaterialDialog dialog = new MaterialDialog.Builder(this)
                        .title("Nombre")
                        .titleGravity(GravityEnum.CENTER)
                        .input("Nombre de seccion", "", false, new MaterialDialog.InputCallback() {
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
                        .content("Actualizando Favoritos...")
                        .progress(true, 0)
                        .cancelable(false)
                        .build();
                d.show();
                FavSyncro.updateLocal(this, new FavSyncro.UpdateCallback() {
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
                });
                break;
            /*case R.id.recreate:
                new MaterialDialog.Builder(this)
                        .title("Recrear Base de datos")
                        .titleGravity(GravityEnum.CENTER)
                        .content("Borrar todas las secciones y recrear la base de datos (puede tardar unos segundos)?")
                        .positiveText("continuar")
                        .negativeText("cancelar")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("data_revised", false).apply();
                                recreate();
                            }
                        }).build().show();
                break;*/
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        if (!cloud_updated)
            DropboxManager.updateFavs(getApplicationContext(), null);
        super.onDestroy();
    }
}
