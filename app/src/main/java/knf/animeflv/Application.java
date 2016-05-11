package knf.animeflv;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.karumi.dexter.Dexter;

import knf.animeflv.Emision.EmisionChecker;
import knf.animeflv.Utils.Logger;
import knf.animeflv.Utils.UtilsInit;
import xdroid.toaster.Toaster;


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
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
