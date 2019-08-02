package knf.animeflv.LoginActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.dropbox.core.android.Auth;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.AutoEmision.AutoEmisionHelper;
import knf.animeflv.ColorsRes;
import knf.animeflv.FavSync.FavSyncHelper;
import knf.animeflv.FavSync.SyncActivity;
import knf.animeflv.FavSyncro;
import knf.animeflv.R;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 15/07/2017.
 */

public class LoginActivity extends AppCompatActivity {

    private static boolean LOADED = false;
    private static boolean FAVS_VISIBLE = false;
    private static boolean SEEN_VISIBLE = false;
    private static boolean EMISION_VISIBLE = false;
    private static boolean FAVS_SYNC = false;
    private static boolean SEEN_SYNC = false;
    private static boolean EMISION_SYNC = false;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.login)
    Button login;
    @BindView(R.id.sync_buttons)
    LinearLayout layout;
    @BindView(R.id.sync_favs)
    AppCompatButton s_favs;
    @BindView(R.id.sync_seen)
    AppCompatButton s_seen;
    @BindView(R.id.sync_emision)
    AppCompatButton s_emision;
    private boolean isLoging = false;
    private int COLOR_SUCCESS = Color.parseColor("#006600");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#E0E0E0"));
            getWindow().setNavigationBarColor(ColorsRes.Blanco(this));
        }
        Drawable drawable = getResources().getDrawable(R.drawable.clear);
        drawable.setColorFilter(ColorsRes.Holo_Light(this), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(drawable);
        toolbar.getRootView().setBackgroundColor(ColorsRes.Blanco(this));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        startUp();
        s_favs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog d = new MaterialDialog.Builder(LoginActivity.this)
                        .content("Obteniendo favoritos...")
                        .progress(true, 0)
                        .cancelable(false)
                        .build();
                d.show();
                FavSyncHelper.recreate(LoginActivity.this, new FavSyncHelper.SyncListener() {
                    @Override
                    public void onSync() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                d.dismiss();
                                if (FavSyncHelper.isSame) {
                                    Toaster.toast("Los favoritos son iguales!!!");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            FAVS_SYNC = true;
                                            s_favs.setTextColor(COLOR_SUCCESS);
                                            s_favs.setText("Favoritos sincronizados");
                                            s_favs.setOnClickListener(null);
                                        }
                                    });
                                } else {
                                    startActivityForResult(new Intent(LoginActivity.this, SyncActivity.class), 55447);
                                }
                            }
                        });
                    }
                });
            }
        });
        s_seen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog dialog = getSyncDialog();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.show();
                    }
                });
                FavSyncro.updateLocalSeen(LoginActivity.this, new FavSyncro.UpdateCallback() {
                    @Override
                    public void onUpdate() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SEEN_SYNC = true;
                                dialog.dismiss();
                                s_seen.setTextColor(COLOR_SUCCESS);
                                s_seen.setText("Capitulos vistos sincronizados");
                                s_seen.setOnClickListener(null);
                            }
                        });
                    }
                });
            }
        });
        s_emision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog dialog = getSyncDialog();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.show();
                    }
                });
                DropboxManager.downloadEmision(LoginActivity.this, new DropboxManager.DownloadCallback() {
                    @Override
                    public void onDownload(final JSONObject result, final boolean success) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (success) {
                                    EMISION_SYNC = true;
                                    AutoEmisionHelper.updateSavedList(LoginActivity.this, result);
                                    s_emision.setTextColor(COLOR_SUCCESS);
                                    s_emision.setText("Lista de emision sincronizada");
                                    s_emision.setOnClickListener(null);
                                } else {
                                    EMISION_SYNC = false;
                                    Toaster.toast("Error al sincronizar lista de emision");
                                }
                                dialog.dismiss();
                            }
                        });
                    }
                });
            }
        });
        if (LOADED) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (FAVS_VISIBLE) {
                        showFavs();
                        if (FAVS_SYNC) {
                            s_favs.setTextColor(COLOR_SUCCESS);
                            s_favs.setText("Favoritos sincronizados");
                            s_favs.setOnClickListener(null);
                        }
                    }
                    if (SEEN_VISIBLE) {
                        showSeen();
                        if (SEEN_SYNC) {
                            s_seen.setTextColor(COLOR_SUCCESS);
                            s_seen.setText("Capitulos vistos sincronizados");
                            s_seen.setOnClickListener(null);
                        }
                    }
                    if (EMISION_VISIBLE) {
                        showEmision();
                        if (EMISION_SYNC) {
                            s_emision.setTextColor(COLOR_SUCCESS);
                            s_emision.setText("Lista de emision sincronizada");
                            s_emision.setOnClickListener(null);
                        }
                    }
                }
            });
        }
        LOADED = true;
    }

    private void startUp() {
        if (DropboxManager.islogedIn()) {
            onLoginSuccess(false);
            DropboxManager.checkSources(new DropboxManager.LoginCalbackSources() {
                @Override
                public void onFavs(boolean isAvailable) {
                    if (isAvailable)
                        showFavs();
                }

                @Override
                public void onSeen(boolean isAvailable) {
                    if (isAvailable)
                        showSeen();
                }

                @Override
                public void onEmision(boolean isAvailable) {
                    if (isAvailable)
                        showEmision();
                }

                @Override
                public void onLogin(boolean loged) {

                }

                @Override
                public void onStartLogin() {

                }
            });
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new MaterialDialog.Builder(LoginActivity.this)
                            .content("¿Cerrar sesión de Dropbox?")
                            .theme(Theme.DARK)
                            .positiveText("cerrar")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    DropboxManager.logoff(LoginActivity.this);
                                    startUp();
                                }
                            }).build().show();
                }
            });
        } else {
            onLoginError(false);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DropboxManager.login(LoginActivity.this, new DropboxManager.LoginCalbackSources() {
                        @Override
                        public void onFavs(boolean isAvailable) {
                            if (isAvailable)
                                showFavs();
                        }

                        @Override
                        public void onSeen(boolean isAvailable) {
                            if (isAvailable)
                                showSeen();
                        }

                        @Override
                        public void onEmision(boolean isAvailable) {
                            if (isAvailable)
                                showEmision();
                        }

                        @Override
                        public void onLogin(final boolean loged) {
                            isLoging = false;
                            if (loged) {
                                onLoginSuccess(true);
                            } else {
                                onLoginError(true);
                            }
                        }

                        @Override
                        public void onStartLogin() {
                            isLoging = true;
                        }
                    });
                }
            });
        }
    }

    private void onLoginSuccess(final boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (show)
                    Toaster.toast("Sesión Iniciada!!");
                //login.setVisibility(View.GONE);
                login.setText("cerrar sesion");
                login.setBackgroundColor(ColorsRes.Rojo(LoginActivity.this));
                layout.setVisibility(View.VISIBLE);
            }
        });
    }

    private MaterialDialog getSyncDialog() {
        return new MaterialDialog.Builder(this)
                .content("Sincronizando...")
                .progress(true, 0)
                .build();
    }

    private void onLoginError(final boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (show)
                    Toaster.toast("Error al iniciar sesión!!!");
                //login.setVisibility(View.VISIBLE);
                login.setText("Entrar / Registrar");
                login.setBackgroundColor(Color.parseColor("#007ee5"));
                layout.setVisibility(View.GONE);
            }
        });
    }

    private void showFavs() {
        FAVS_VISIBLE = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                s_favs.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showSeen() {
        SEEN_VISIBLE = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                s_seen.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showEmision() {
        EMISION_VISIBLE = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                s_emision.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onResume() {
        if (isLoging) {
            isLoging = false;
            String token = Auth.getOAuth2Token();
            if (token != null) {
                DropboxManager.UpdateToken(token);
                PreferenceManager.getDefaultSharedPreferences(this).edit().putString(DropboxManager.KEY_DROPBOX, token).apply();
                onLoginSuccess(true);
                DropboxManager.checkSources(new DropboxManager.LoginCalbackSources() {
                    @Override
                    public void onFavs(boolean isAvailable) {
                        if (isAvailable)
                            showFavs();
                    }

                    @Override
                    public void onSeen(boolean isAvailable) {
                        if (isAvailable)
                            showSeen();
                    }

                    @Override
                    public void onEmision(boolean isAvailable) {
                        if (isAvailable)
                            showEmision();
                    }

                    @Override
                    public void onLogin(boolean loged) {

                    }

                    @Override
                    public void onStartLogin() {

                    }
                });
            }
        }
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 55447 && resultCode == Activity.RESULT_OK) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FAVS_SYNC = true;
                    s_favs.setTextColor(COLOR_SUCCESS);
                    s_favs.setText("Favoritos sincronizados");
                    s_favs.setOnClickListener(null);
                }
            });
        }
    }
}
