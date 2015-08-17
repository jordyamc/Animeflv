package knf.animeflv;

import android.app.Application;

import com.github.mmin18.layoutcast.LayoutCast;

/**
 * Created by Jordy on 17/08/2015.
 */
public class mApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            LayoutCast.init(this);
        }
    }
}
