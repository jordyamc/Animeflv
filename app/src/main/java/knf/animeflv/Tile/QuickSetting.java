package knf.animeflv.Tile;

import android.annotation.TargetApi;
import android.content.Intent;
import android.service.quicksettings.TileService;

import knf.animeflv.Splash;

@TargetApi(24)
public class QuickSetting extends TileService {
    public QuickSetting() {
    }

    @Override
    public void onClick() {
        startActivity(new Intent(getApplicationContext(), Splash.class));
    }

}
