package knf.animeflv.StreamManager;

import android.content.Context;

/**
 * Created by Jordy on 04/03/2016.
 */
public class StreamManager {
    public static InternalStream internal(Context context) {
        return new InternalStream(context);
    }

    public static ExternalStream external(Context context) {
        return new ExternalStream(context);
    }

    public static MXStream mx(Context context) {
        return new MXStream(context);
    }
}
