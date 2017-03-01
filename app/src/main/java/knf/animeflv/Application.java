package knf.animeflv;

import android.app.Activity;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.karumi.dexter.Dexter;

import es.munix.multidisplaycast.CastManager;
import knf.animeflv.LoginActivity.DropboxManager;
import knf.animeflv.Utils.Logger;
import knf.animeflv.Utils.UtilsInit;
import xdroid.toaster.Toaster;


public class Application extends MultiDexApplication {
    Context context;
    private Tracker mTracker;

    public static Application get(Activity activity) {
        return (Application) activity.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        UtilsInit.init(this);
        Dexter.initialize(getApplicationContext());
        android.webkit.CookieSyncManager.createInstance(this);
        android.webkit.CookieManager.getInstance().setAcceptCookie(true);
        WebkitCookieManagerProxy coreCookieManager = new WebkitCookieManagerProxy(null, java.net.CookiePolicy.ACCEPT_ALL);
        java.net.CookieHandler.setDefault(coreCookieManager);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                Toaster.toast(e.getMessage());
                Logger.UncaughtError(e);
                System.exit(0);
            }
        });
        CastManager.register(getApplicationContext());
        DropboxManager.init(this);
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.app_tracker);
        }
        return mTracker;
    }

}
