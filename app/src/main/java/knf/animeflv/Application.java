package knf.animeflv;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.karumi.dexter.Dexter;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import es.munix.multidisplaycast.CastManager;
import io.branch.referral.Branch;
import io.fabric.sdk.android.Fabric;
import knf.animeflv.DownloadService.SSLCertificateHandler;
import knf.animeflv.LoginActivity.DropboxManager;
import knf.animeflv.Utils.FastActivity;
import knf.animeflv.Utils.Logger;
import knf.animeflv.Utils.UtilsInit;
import xdroid.toaster.Toaster;


public class Application extends MultiDexApplication {
    private static NotificationResponse handler;
    Context context;
    private Tracker mTracker;

    public static Application get(Activity activity) {
        return (Application) activity.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        SSLCertificateHandler.nuke();
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
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationOpenedHandler(getNotificationHandler())
                .init();
        String email = FavSyncro.getEmail(context);
        if (!email.equals("Animeflv"))
            OneSignal.syncHashedEmail(email);
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.app_tracker);
        }
        return mTracker;
    }

    public NotificationResponse getNotificationHandler() {
        if (handler == null)
            handler = new NotificationResponse();
        return handler;
    }

    public class NotificationResponse implements OneSignal.NotificationOpenedHandler {
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            try {
                JSONObject data = result.notification.payload.additionalData;
                if (data == null) {
                    Log.e("Notification Received", "No data");
                    Intent intent = new Intent(getApplicationContext(), Splash.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                } else {
                    Log.e("Notification Received", "Data: [Action: " + data.getString("action") + "]");
                    switch (data.getString("action")) {
                        case "DIALOG":
                            Bundle bundle = new Bundle();
                            bundle.putInt("key", FastActivity.SHOW_DIALOG);
                            bundle.putString("content", data.getString("text"));
                            Intent intent = new Intent(getApplicationContext(), FastActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtras(bundle);
                            getApplicationContext().startActivity(intent);
                            break;
                        case "MAIN":
                            Intent splash = new Intent(getApplicationContext(), Splash.class);
                            splash.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplicationContext().startActivity(splash);
                            break;
                        case "WEB":
                            Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse(data.getString("url")));
                            web.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplicationContext().startActivity(web);
                            break;
                    }
                }
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                Toaster.toast("No se encontro ninguna aplicacion disponible");
            } catch (Exception e) {
                e.printStackTrace();
                CrashlyticsCore.getInstance().logException(e);
            }
        }
    }

}
