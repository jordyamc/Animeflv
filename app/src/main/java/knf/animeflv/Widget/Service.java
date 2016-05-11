package knf.animeflv.Widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Jordy on 09/05/2016.
 */
public class Service extends RemoteViewsService {
    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new Factory(this.getApplicationContext(),
                intent));
    }
}