package knf.animeflv.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import java.util.Calendar;

import knf.animeflv.AutoEmision.AutoEmisionActivity;
import knf.animeflv.AutoEmision.AutoEmisionHelper;
import knf.animeflv.R;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.info.InfoFragments;

public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i : appWidgetIds) {
            RemoteViews remoteViews = updateWidgetListView(context,
                    i);
            appWidgetManager.updateAppWidget(i, remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.emision_widget);
        Intent svcIntent = new Intent(context, WidgetService.class);
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(R.id.words,
                svcIntent);
        Intent clickIntent = new Intent(context, AutoEmisionActivity.class);
        remoteViews.setTextViewText(R.id.title_day, getActualDay());
        remoteViews.setTextViewText(R.id.title_count, String.valueOf(AutoEmisionHelper.getDirectListDay(context, getActualDayCode()).size()));
        remoteViews.setOnClickPendingIntent(R.id.back_layout, PendingIntent.getActivity(context, 555, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        remoteViews.setPendingIntentTemplate(R.id.words, PendingIntent.getActivity(context, appWidgetId, new Intent(context, InfoFragments.class), PendingIntent.FLAG_UPDATE_CURRENT));
        ThemeUtils.Theme theme = ThemeUtils.Theme.create(context);
        remoteViews.setInt(R.id.back_layout, "setBackgroundColor", theme.primary);
        remoteViews.setTextColor(R.id.title_day, theme.textColorToolbar);
        remoteViews.setTextColor(R.id.title_count, theme.textColorToolbar);
        remoteViews.setEmptyView(R.id.main_list, R.id.empty);
        return remoteViews;
    }

    private String getActualDay() {
        switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                return "LUNES";
            case Calendar.TUESDAY:
                return "MARTES";
            case Calendar.WEDNESDAY:
                return "MIERCOLES";
            case Calendar.THURSDAY:
                return "JUEVES";
            case Calendar.FRIDAY:
                return "VIERNES";
            case Calendar.SATURDAY:
                return "SABADO";
            case Calendar.SUNDAY:
                return "DOMINGO";
            default:
                return "DESCONOCIDO(LUNES POR DEFECTO)";
        }
    }

    private int getActualDayCode() {
        switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            default:
            case Calendar.MONDAY:
                return 1;
            case Calendar.TUESDAY:
                return 2;
            case Calendar.WEDNESDAY:
                return 3;
            case Calendar.THURSDAY:
                return 4;
            case Calendar.FRIDAY:
                return 5;
            case Calendar.SATURDAY:
                return 6;
            case Calendar.SUNDAY:
                return 7;
        }
    }

}
