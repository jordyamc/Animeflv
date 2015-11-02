package knf.animeflv;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Jordy on 29/10/2015.
 */
public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Message.class);
        Parse.initialize(this, "SSAa2CfCYAzpY3uqFf7ZMy19RU6jCgnVr2IbM6zC", "kownbaCYyGG07ZtlQDM5TEVZBezLzR32dzJIdzcF");
    }
}
