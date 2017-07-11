package knf.animeflv.Widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import knf.animeflv.AutoEmision.AutoEmisionActivity;
import knf.animeflv.AutoEmision.AutoEmisionHelper;
import knf.animeflv.AutoEmision.EmObj;
import knf.animeflv.R;
import knf.animeflv.Utils.ThemeUtils;

/**
 * Created by Jordy on 09/05/2016.
 */
public class ListProvider implements RemoteViewsFactory {
    private ArrayList<ListItem> listItemList = new ArrayList<ListItem>();
    private Context context = null;

    public ListProvider(Context context, Intent intent) {
        this.context = context;
        populateListItem();
    }

    private void populateListItem() {
        listItemList = new ArrayList<>();
        List<EmObj> list = AutoEmisionHelper.getDirectListDay(context, getActualDayCode());
        for (EmObj obj : list) {
            ListItem listItem = new ListItem();
            listItem.title = obj.getTitle();
            listItemList.add(listItem);
        }

    }

    @Override
    public int getCount() {
        return listItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     *Similar to getView of Adapter where instead of View
     *we return RemoteViews
     *
     */
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.list_row);
        ListItem listItem = listItemList.get(position);
        remoteView.setTextViewText(R.id.heading, listItem.title);
        Intent clickIntent = new Intent(context, AutoEmisionActivity.class);
        remoteView.setOnClickFillInIntent(R.id.linear, clickIntent);
        ThemeUtils.Theme theme = ThemeUtils.Theme.create(context);
        remoteView.setInt(R.id.linear, "setBackgroundColor", theme.card_normal);
        remoteView.setTextColor(R.id.heading, theme.textColorCard);
        return remoteView;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        populateListItem();
    }

    @Override
    public void onDestroy() {
    }

    private int getActualDayCode() {
        switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
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
            default:
                return 1;
        }
    }

}