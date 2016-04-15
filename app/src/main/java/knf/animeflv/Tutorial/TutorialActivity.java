package knf.animeflv.Tutorial;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.R;
import knf.animeflv.Utils.ThemeUtils;

/**
 * Created by Jordy on 11/04/2016.
 */
public class TutorialActivity extends AppCompatActivity {
    Toolbar toolbar;
    @Bind(R.id.card_tut_1)
    CardView card1;
    @Bind(R.id.card_tut_2)
    CardView card2;
    @Bind(R.id.card_tut_3)
    CardView card3;
    @Bind(R.id.img_tut_1)
    ImageView image1;
    @Bind(R.id.img_tut_2)
    ImageView image2;
    @Bind(R.id.img_tut_3)
    ImageView image3;
    @Bind(R.id.ib_tut_1_1)
    ImageButton buttonv1;
    @Bind(R.id.ib_tut_2_1)
    ImageButton buttonv2;
    @Bind(R.id.ib_tut_3_1)
    ImageButton buttonv3;
    @Bind(R.id.ib_tut_1_2)
    ImageButton buttond1;
    @Bind(R.id.ib_tut_2_2)
    ImageButton buttond2;
    @Bind(R.id.ib_tut_3_2)
    ImageButton buttond3;
    @Bind(R.id.tv_tut_tit_1)
    TextView txt1;
    @Bind(R.id.tv_tut_tit_2)
    TextView txt2;
    @Bind(R.id.tv_tut_tit_3)
    TextView txt3;
    @Bind(R.id.tv_tut_cap_1)
    TextView txtCap1;
    @Bind(R.id.tv_tut_cap_2)
    TextView txtCap2;
    @Bind(R.id.tv_tut_cap_3)
    TextView txtCap3;
    @Bind(R.id.titulo1)
    TextView tit1;
    @Bind(R.id.titulo2)
    TextView tit2;
    @Bind(R.id.titulo3)
    TextView tit3;
    @Bind(R.id.titulo4)
    TextView tit4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial);
        toolbar = (Toolbar) findViewById(R.id.toolbar_tuto);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tutorial");
        ButterKnife.bind(this);
        setUpViews();
    }

    private void setUpViews() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("is_amoled", false)) {
            card1.setCardBackgroundColor(ColorsRes.Prim(this));
            buttond1.setColorFilter(ColorsRes.Blanco(this));
            buttond2.setColorFilter(ColorsRes.Blanco(this));
            buttond3.setColorFilter(ColorsRes.Blanco(this));
            buttonv1.setColorFilter(ColorsRes.Blanco(this));
            buttonv2.setColorFilter(ColorsRes.Blanco(this));
            buttonv3.setColorFilter(ColorsRes.Blanco(this));
            txt1.setTextColor(ColorsRes.Blanco(this));
            txt2.setTextColor(ColorsRes.Blanco(this));
            txt3.setTextColor(ColorsRes.Blanco(this));
        } else {
            buttond1.setColorFilter(ColorsRes.Holo_Light(this));
            buttond2.setColorFilter(ColorsRes.Holo_Light(this));
            buttond3.setColorFilter(ColorsRes.Holo_Light(this));
            buttonv1.setColorFilter(ColorsRes.Holo_Light(this));
            buttonv2.setColorFilter(ColorsRes.Holo_Light(this));
            buttonv3.setColorFilter(ColorsRes.Holo_Light(this));
        }
        txtCap1.setTextColor(ThemeUtils.getAcentColor(this));
        txtCap2.setTextColor(ThemeUtils.getAcentColor(this));
        txtCap3.setTextColor(ThemeUtils.getAcentColor(this));
        tit1.setTextColor(ThemeUtils.getAcentColor(this));
        tit2.setTextColor(ThemeUtils.getAcentColor(this));
        tit3.setTextColor(ThemeUtils.getAcentColor(this));
        tit4.setTextColor(ThemeUtils.getAcentColor(this));
        card2.setCardBackgroundColor(Color.argb(100, 253, 250, 93));
        card3.setCardBackgroundColor(Color.argb(100, 26, 206, 246));

    }

    private void setTheme() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int accent = preferences.getInt("accentColor", ColorsRes.Naranja(this));
        if (preferences.getBoolean("is_amoled", false)) {
            if (accent == ColorsRes.Rojo(this)) {
                setTheme(R.style.AppThemeDarkNoRojo);
            }
            if (accent == ColorsRes.Naranja(this)) {
                setTheme(R.style.AppThemeDarkNoNaranja);
            }
            if (accent == ColorsRes.Gris(this)) {
                setTheme(R.style.AppThemeDarkNoGris);
            }
            if (accent == ColorsRes.Verde(this)) {
                setTheme(R.style.AppThemeDarkNoVerde);
            }
            if (accent == ColorsRes.Rosa(this)) {
                setTheme(R.style.AppThemeDarkNoRosa);
            }
            if (accent == ColorsRes.Morado(this)) {
                setTheme(R.style.AppThemeDarkNoMorado);
            }
        } else {
            if (accent == ColorsRes.Rojo(this)) {
                setTheme(R.style.AppThemeNoRojo);
            }
            if (accent == ColorsRes.Naranja(this)) {
                setTheme(R.style.AppThemeNoNaranja);
            }
            if (accent == ColorsRes.Gris(this)) {
                setTheme(R.style.AppThemeNoGris);
            }
            if (accent == ColorsRes.Verde(this)) {
                setTheme(R.style.AppThemeNoVerde);
            }
            if (accent == ColorsRes.Rosa(this)) {
                setTheme(R.style.AppThemeNoRosa);
            }
            if (accent == ColorsRes.Morado(this)) {
                setTheme(R.style.AppThemeNoMorado);
            }
        }
    }
}
