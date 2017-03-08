package knf.animeflv.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import knf.animeflv.Configuracion;

public class FastActivity extends AppCompatActivity {
    public static final int STOP_SOUND = 1;
    public static final int OPEN_CONF_SOUNDS = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                switch (bundle.getInt("key")) {
                    case STOP_SOUND:
                        UtilSound.getCurrentMediaPlayer().stop();
                        if (UtilSound.isNotSoundShow) {
                            UtilSound.toogleNotSound(-1);
                        }
                        break;
                    case OPEN_CONF_SOUNDS:
                        FragmentExtras.KEY = Configuracion.OPEN_SOUNDS;
                        startActivity(new Intent(this, Configuracion.class));
                        break;
                }
                finish();
            } else {
                finish();
            }
        } catch (Exception e) {
            finish();
        }
    }
}
