package knf.animeflv;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.evernote.android.job.JobManager;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import es.munix.multidisplaycast.CastManager;
import io.branch.referral.Branch;
import io.fabric.sdk.android.Fabric;
import knf.animeflv.DownloadService.SSLCertificateHandler;
import knf.animeflv.Jobs.JobsCreator;
import knf.animeflv.LoginActivity.DropboxManager;
import knf.animeflv.Utils.Logger;
import knf.animeflv.Utils.UtilsInit;

import static knf.animeflv.BackgroundChecker.startBackground.CHANNEL_ANIMES;
import static knf.animeflv.BackgroundChecker.startBackground.CHANNEL_ANIMES_DESC;
import static knf.animeflv.BackgroundChecker.startBackground.CHANNEL_CAST;
import static knf.animeflv.BackgroundChecker.startBackground.CHANNEL_CAST_DESC;
import static knf.animeflv.BackgroundChecker.startBackground.CHANNEL_COM_DOWNLOAD;
import static knf.animeflv.BackgroundChecker.startBackground.CHANNEL_COM_DOWNLOAD_DESC;
import static knf.animeflv.BackgroundChecker.startBackground.CHANNEL_CURR_DOWNLOAD;
import static knf.animeflv.BackgroundChecker.startBackground.CHANNEL_CURR_DOWNLOAD_DESC;
import static knf.animeflv.BackgroundChecker.startBackground.CHANNEL_ERROR;
import static knf.animeflv.BackgroundChecker.startBackground.CHANNEL_ERROR_DESC;
import static knf.animeflv.BackgroundChecker.startBackground.CHANNEL_UPDATES;
import static knf.animeflv.BackgroundChecker.startBackground.CHANNEL_UPDATES_DESC;
import static knf.animeflv.BackgroundChecker.startBackground.CHANNEL_UPDATES_RUNNING;
import static knf.animeflv.BackgroundChecker.startBackground.CHANNEL_UPDATES_RUNNING_DESC;


public class Application extends MultiDexApplication {
    public static Context context;
    private Tracker mTracker;

    public static Application get(Activity activity) {
        return (Application) activity.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        SSLCertificateHandler.nuke();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            initChannels();
        UtilsInit.init(this);
        JobManager.create(this).addJobCreator(new JobsCreator());
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

    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(this);
        super.attachBaseContext(base);
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.app_tracker);
        }
        return mTracker;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void initChannels() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createChannel(manager, CHANNEL_ANIMES, CHANNEL_ANIMES_DESC, "Animes", NotificationManager.IMPORTANCE_MAX, Color.argb(0, 255, 128, 0), true, true);
        createChannel(manager, CHANNEL_UPDATES, CHANNEL_UPDATES_DESC, "Actualizaciones", NotificationManager.IMPORTANCE_MAX, Color.BLUE, true);
        createChannel(manager, CHANNEL_UPDATES_RUNNING, CHANNEL_UPDATES_RUNNING_DESC, "Descarga de actualizacion", NotificationManager.IMPORTANCE_LOW, Color.BLUE, false);
        createChannel(manager, CHANNEL_CURR_DOWNLOAD, CHANNEL_CURR_DOWNLOAD_DESC, "Descargas en progreso", NotificationManager.IMPORTANCE_DEFAULT, -1, false);
        createChannel(manager, CHANNEL_COM_DOWNLOAD, CHANNEL_COM_DOWNLOAD_DESC, "Descargas terminadas", NotificationManager.IMPORTANCE_DEFAULT, Color.parseColor("#8BC34A"), false);
        createChannel(manager, CHANNEL_ERROR, CHANNEL_ERROR_DESC, "Errores", NotificationManager.IMPORTANCE_MAX, Color.RED, false);
        createChannel(manager, CHANNEL_CAST, CHANNEL_CAST_DESC, "Notificacion de CAST", NotificationManager.IMPORTANCE_DEFAULT, Color.GREEN, false);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel(NotificationManager manager, String id, String desc, String name, int importance, int ligths, boolean vibration) {
        createChannel(manager, id, desc, name, importance, ligths, vibration, false);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel(NotificationManager manager, String id, String desc, String name, int importance, int ligths, boolean vibration, boolean badge) {
        try {
            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(desc);
            channel.enableLights(true);
            if (ligths != -1)
                channel.setLightColor(ligths);
            if (vibration) {
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 200, 100, 500});
            }
            if (badge)
                channel.setShowBadge(true);
            manager.createNotificationChannel(channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
