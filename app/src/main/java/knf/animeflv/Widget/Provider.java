package knf.animeflv.Widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import knf.animeflv.R;

/**
 * Created by Jordy on 09/05/2016.
 */
public class Provider extends AppWidgetProvider {
    public static String EXTRA_WORD =
            "com.commonsware.android.appwidget.lorem.WORD";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i : appWidgetIds) {
            Intent svcIntent = new Intent(context, Service.class);

            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, i);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews widget = new RemoteViews(context.getPackageName(),
                    R.layout.layout_widget);

            widget.setRemoteAdapter(i, R.id.main_list,
                    svcIntent);

            appWidgetManager.updateAppWidget(i, widget);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
