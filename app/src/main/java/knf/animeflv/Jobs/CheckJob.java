package knf.animeflv.Jobs;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import knf.animeflv.BackgroundChecker.startBackground;
import knf.animeflv.Cloudflare.Bypass;
import knf.animeflv.R;
import knf.animeflv.Utils.FastActivity;

public class CheckJob extends Job {

    public static final String TAG = "check_background";

    private static long getTime(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? 900000 : Long.parseLong(PreferenceManager.getDefaultSharedPreferences(context).getString("tiempo", "60000"));
    }

    public static void shedule(Context context) {
        if (!JobManager.instance().getAllJobRequestsForTag(TAG).isEmpty() || Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        new JobRequest.Builder(TAG)
                .setPeriodic(getTime(context), JobRequest.MIN_FLEX)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setUpdateCurrent(true)
                .build().schedule();
    }

    public static void reshedule(Context context) {
        if (!JobManager.instance().getAllJobRequestsForTag(TAG).isEmpty() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopShedule();
            shedule(context);
        }
    }

    public static void stopShedule() {
        JobManager.instance().cancelAllForTag(TAG);
    }

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Boolean not = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("notificaciones", true);
        if (not) {
            Bypass.runJsoupTest(getContext(), new Bypass.onTestResult() {
                @Override
                public void onResult(boolean needBypass) {
                    if (needBypass && checkBypass(getContext())) {
                        Intent intent = new Intent(getContext(), FastActivity.class);
                        intent.putExtra("key", FastActivity.RECREATE_BYPASS);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), startBackground.CHANNEL_UPDATES)
                                .setContentTitle("Bypass de cloudflare caducado")
                                .setContentText("Click para recrear bypass")
                                .setSmallIcon(R.drawable.ic_not_r)
                                .setAutoCancel(true)
                                .setOnlyAlertOnce(true)
                                .setColor(Color.YELLOW)
                                .setContentIntent(PendingIntent.getActivity(getContext(), (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT));
                        ((NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE)).notify(55898, builder.build());
                    } else {
                        startService(getContext());
                    }
                }
            });
        } else {
            Log.e("Service", "Servicio Desactivado");
        }
        return Result.SUCCESS;
    }

    private boolean checkBypass(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("bypass_check", false);
    }

    private void startService(Context context) {
        Log.e("Service", "Servicio Iniciado");
        startBackground.compareNots(context);
        try {
            startBackground.checkUpdate(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
