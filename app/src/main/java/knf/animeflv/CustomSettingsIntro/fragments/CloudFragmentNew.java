package knf.animeflv.CustomSettingsIntro.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dropbox.core.android.Auth;
import com.heinrichreimersoftware.materialintro.app.SlideFragment;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.AutoEmision.AutoEmisionHelper;
import knf.animeflv.FavSyncro;
import knf.animeflv.LoginActivity.DropboxManager;
import knf.animeflv.R;
import xdroid.toaster.Toaster;

public class CloudFragmentNew extends SlideFragment {

    private static boolean LOADED = false;
    private static boolean FAVS_VISIBLE = false;
    private static boolean SEEN_VISIBLE = false;
    private static boolean EMISION_VISIBLE = false;
    private static boolean FAVS_SYNC = false;
    private static boolean SEEN_SYNC = false;
    private static boolean EMISION_SYNC = false;
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

    public static CloudFragmentNew get() {
        return new CloudFragmentNew();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sound_fragment, container, false);
        ButterKnife.bind(this, view);
        if (DropboxManager.islogedIn()) {
            onLoginSuccess(false);
            FAVS_SYNC = true;
            getActivity().runOnUiThread(() -> {
                s_favs.setVisibility(View.VISIBLE);
                s_favs.setTextColor(COLOR_SUCCESS);
                s_favs.setText("Favoritos sincronizados");
            });
            DropboxManager.checkSources(new DropboxManager.LoginCalbackSources() {
                @Override
                public void onFavs(boolean isAvailable) {

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
        } else {
            login.setOnClickListener(v -> DropboxManager.login(getActivity(), new DropboxManager.LoginCalbackSources() {
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
                        onLoginError();
                    }
                }

                @Override
                public void onStartLogin() {
                    isLoging = true;
                }
            }));
            s_favs.setOnClickListener(v -> {
                final MaterialDialog dialog = getSyncDialog();
                getActivity().runOnUiThread(() -> dialog.show());
                FavSyncro.updateLocal(getActivity(), () -> getActivity().runOnUiThread(() -> {
                    FAVS_SYNC = true;
                    dialog.dismiss();
                    s_favs.setTextColor(COLOR_SUCCESS);
                    s_favs.setText("Favoritos sincronizados");
                    s_favs.setOnClickListener(null);
                }));
            });
        }
        s_seen.setOnClickListener(v -> {
            final MaterialDialog dialog = getSyncDialog();
            getActivity().runOnUiThread(() -> dialog.show());
            FavSyncro.updateLocalSeen(getActivity(), () -> getActivity().runOnUiThread(() -> {
                SEEN_SYNC = true;
                dialog.dismiss();
                s_seen.setTextColor(COLOR_SUCCESS);
                s_seen.setText("Capitulos vistos sincronizados");
                s_seen.setOnClickListener(null);
            }));
        });
        s_emision.setOnClickListener(v -> {
            final MaterialDialog dialog = getSyncDialog();
            getActivity().runOnUiThread(() -> dialog.show());
            DropboxManager.downloadEmision(getActivity(), (result, success) -> getActivity().runOnUiThread(() -> {
                if (success) {
                    EMISION_SYNC = true;
                    AutoEmisionHelper.updateSavedList(getActivity(), result);
                    s_emision.setTextColor(COLOR_SUCCESS);
                    s_emision.setText("Lista de emision sincronizada");
                    s_emision.setOnClickListener(null);
                } else {
                    EMISION_SYNC = false;
                    Toaster.toast("Error al sincronizar lista de emision");
                }
                dialog.dismiss();
            }));
        });
        if (LOADED) {
            getActivity().runOnUiThread(() -> {
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
            });
        }
        LOADED = true;
        return view;
    }

    private void onLoginSuccess(final boolean show) {
        getActivity().runOnUiThread(() -> {
            if (show)
                Toaster.toast("Sesión Iniciada!!");
            login.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);
        });
    }

    private MaterialDialog getSyncDialog() {
        return new MaterialDialog.Builder(getActivity())
                .content("Sincronizando...")
                .progress(true, 0)
                .build();
    }

    private void onLoginError() {
        getActivity().runOnUiThread(() -> {
            Toaster.toast("Error al iniciar sesión!!!");
            login.setVisibility(View.VISIBLE);
            layout.setVisibility(View.GONE);
        });
    }

    private void showFavs() {
        FAVS_VISIBLE = true;
        getActivity().runOnUiThread(() -> s_favs.setVisibility(View.VISIBLE));
    }

    private void showSeen() {
        SEEN_VISIBLE = true;
        getActivity().runOnUiThread(() -> s_seen.setVisibility(View.VISIBLE));
    }

    private void showEmision() {
        EMISION_VISIBLE = true;
        getActivity().runOnUiThread(() -> s_emision.setVisibility(View.VISIBLE));
    }

    @Override
    public void onResume() {
        if (isLoging) {
            isLoging = false;
            String token = Auth.getOAuth2Token();
            if (token != null) {
                DropboxManager.UpdateToken(token);
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString(DropboxManager.KEY_DROPBOX, token).apply();
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
}
