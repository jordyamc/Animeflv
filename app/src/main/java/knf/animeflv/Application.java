package knf.animeflv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.karumi.dexter.Dexter;

import es.munix.multidisplaycast.CastManager;
import io.branch.referral.Branch;
import io.fabric.sdk.android.Fabric;
import knf.animeflv.LoginActivity.DropboxManager;
import knf.animeflv.Utils.Logger;
import knf.animeflv.Utils.UtilsInit;


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
                if (!(e instanceof InternalError)) {
                    if (!(e instanceof OutOfMemoryError)) {
                        Logger.UncaughtError(e);
                        System.exit(0);
                    } else {
                        Toast.makeText(context, "Error en memoria!!!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(context, Splash.class));
                    }
                }
            }
        });
        Branch.getAutoInstance(this);
        Fabric.with(this, new Crashlytics(), new Answers());
        Crashlytics.setUserEmail(FavSyncro.getEmail(this));
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
