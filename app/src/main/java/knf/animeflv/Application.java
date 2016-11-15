package knf.animeflv;

import android.content.Context;

import com.karumi.dexter.Dexter;

import es.munix.multidisplaycast.CastManager;
import knf.animeflv.Emision.EmisionChecker;
import knf.animeflv.Utils.Logger;
import knf.animeflv.Utils.UtilsInit;
import xdroid.toaster.Toaster;


public class Application extends android.app.Application {
    Context context;

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
        CastManager.register(getApplicationContext());
    }
}
