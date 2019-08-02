package knf.animeflv.SplashActivity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import knf.animeflv.Utils.ThemeUtils;

/**
 * Created by Jordy on 01/09/2016.
 */

public class ParticleSplash extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);


    }
}
