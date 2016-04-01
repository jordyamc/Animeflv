package knf.animeflv.WaitList.Costructor;

import java.util.Comparator;

/**
 * Created by Jordy on 31/03/2016.
 */
public class aidComparator implements Comparator<String> {
    @Override
    public int compare(String lhs, String rhs) {
        return Integer.parseInt(lhs) - Integer.parseInt(rhs);
    }
}
