package knf.animeflv.TV;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.AestheticActivity;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import knf.animeflv.ColorsRes;
import knf.animeflv.Configuracion;
import knf.animeflv.Directorio.AnimeClass;
import knf.animeflv.Directorio.DB.DirectoryHelper;
import knf.animeflv.JsonFactory.BaseGetter;
import knf.animeflv.JsonFactory.SelfGetter;
import knf.animeflv.R;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

public class TVMain extends AestheticActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    @BindView(R.id.theme)
    ImageButton theme;
    @BindView(R.id.settings)
    ImageButton settings;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tv_main);
        ButterKnife.bind(this);
        if (Aesthetic.isFirstTime())
            Aesthetic.get().isDark(ThemeUtils.Theme.create(this).isDark).colorAccent(ThemeUtils.Theme.create(this).accent).apply();
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        theme.setImageDrawable(new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_theme_light_dark)
                .color(ColorsRes.Holo_Light(this))
                .sizeDp(24));
        settings.setImageDrawable(new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_settings)
                .color(ColorsRes.Holo_Light(this))
                .sizeDp(24));

        if (!DirectoryHelper.get(this).isDirectoryValid()) {
            blockToUpdateDB();
        }
    }

    private void blockToUpdateDB() {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .progress(true, 0)
                .theme(ThemeUtils.isAmoled(this) ? Theme.DARK : Theme.LIGHT)
                .content("Creando directorio...\n\nAgregados: 0")
                .cancelable(false)
                .build();
        dialog.show();
        SelfGetter.getDirDB(this, new BaseGetter.AsyncProgressDBInterface() {
            @Override
            public void onFinish(List<AnimeClass> list) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onProgress(final int progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dialog.setContent("Creando directorio...\n\nAgregados: " + progress);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dialog.dismiss();
                            Toaster.toast("Error al crear directorio!!!");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }

    @OnClick({R.id.theme, R.id.settings})
    public void onOptionClick(ImageButton button) {
        switch (button.getId()) {
            case R.id.theme:
                return;
            case R.id.settings:
                startActivity(new Intent(this, Configuracion.class));
        }
    }

    @OnFocusChange({R.id.theme, R.id.settings})
    public void onOptionFocus(ImageButton button) {
        button.clearColorFilter();
        if (button.isFocused())
            button.setColorFilter(ThemeUtils.Theme.create(this).accent);
    }

}
