package knf.animeflv.Widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Jordy on 09/05/2016.
 */
public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new ListProvider(this.getApplicationContext(),
                intent));
    }
}