package knf.animeflv;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.karumi.dexter.Dexter;
import com.parse.Parse;
import com.parse.ParseObject;

import java.io.File;

import knf.animeflv.Emision.EmisionChecker;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.UtilsInit;

/**
 * Created by Jordy on 29/10/2015.
 */
public class Application extends android.app.Application {
    Context context;
    private Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        UtilsInit.init(this);
        EmisionChecker.Ginit(this);
        Dexter.initialize(getApplicationContext());
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Message.class);
        Parse.initialize(this, "SSAa2CfCYAzpY3uqFf7ZMy19RU6jCgnVr2IbM6zC", "kownbaCYyGG07ZtlQDM5TEVZBezLzR32dzJIdzcF");
        android.webkit.CookieSyncManager.createInstance(this);
        android.webkit.CookieManager.getInstance().setAcceptCookie(true);
        WebkitCookieManagerProxy coreCookieManager = new WebkitCookieManagerProxy(null, java.net.CookiePolicy.ACCEPT_ALL);
        java.net.CookieHandler.setDefault(coreCookieManager);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                Log.e("Uncaught", "Error", e);
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                FileUtil.writeToFile(e.getMessage() + "\n" + e.getCause(), new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache", "log.txt"));
                System.exit(0);
            }
        });
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
}
