package knf.animeflv;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by Jordy on 13/10/2015.
 */
public class Intro extends AppIntro2 {
    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(AppIntroFragment.newInstance("Bienvenido!!",
                "Esta es la app No Oficial de AnimeFLV",
                R.drawable.intro,
                Color.parseColor("#FAC720")));
        addSlide(AppIntroFragment.newInstance("Recientes",
                "Lista con los ultimos capitulos agregados",
                R.drawable.int1,
                Color.parseColor("#204CFA")));
        addSlide(AppIntroFragment.newInstance("Descarga y Streaming",
                "Descarga y Streaming disponible para todos los capitulos",
                R.drawable.int3,
                Color.parseColor("#19D13B")));
        addSlide(AppIntroFragment.newInstance("Informacion de Anime",
                "Informacion, Sinopsis y Relacionados de cada anime",
                R.drawable.int7,
                Color.parseColor("#F23207")));
        addSlide(AppIntroFragment.newInstance("Varias Secciones",
                "Favoritos, Directorio, Descargas, Sugerencias",
                R.drawable.int4,
                Color.parseColor("#9100B5")));
        addSlide(AppIntroFragment.newInstance("Favoritos",
                "Soporte para copia en la nube",
                R.drawable.int6,
                Color.parseColor("#00B5B2")));
        addSlide(AppIntroFragment.newInstance("Notificaciones",
                "Avisa cada vez que sale un capitulo nuevo",
                R.drawable.int8,
                Color.parseColor("#A3ADAD")));
        addSlide(AppIntroFragment.newInstance("Configuracion",
                "Muchas configuraciones a modificar",
                R.drawable.int5,
                Color.parseColor("#E8E117")));
        addSlide(AppIntroFragment.newInstance("Sin Publicidad",
                "100% libre de publicidad, una app de fans para fans ;)",
                R.drawable.block,
                Color.parseColor("#732100")));
        addSlide(AppIntroFragment.newInstance("Listo",
                "Has terminado la introduccion, puedes empezar a utilizar la app :)",
                R.drawable.listo,
                Color.parseColor("#3B3B3B")));
    }

    @Override
    public void onDonePressed() {
        getSharedPreferences("data",MODE_PRIVATE).edit().putBoolean("intro",true).apply();
        finish();
    }

    @Override
    public void onSlideChanged() {

    }

    @Override
    public void onNextPressed() {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
