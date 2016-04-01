package knf.animeflv.Utils.Files;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jordy on 22/03/2016.
 */
public class FileSearchResponse {
    private List<String> sdNames = new ArrayList<>();

    public FileSearchResponse(List<String> sdNames) {
        this.sdNames = sdNames;
    }

    public List<String> list() {
        return sdNames;
    }

    public boolean existSD() {
        return sdNames.size() != 0;
    }

    public boolean isOnlyOne() {
        return sdNames.size() == 1;
    }

    public String getUniqueName() {
        if (sdNames.size() == 1) {
            return sdNames.get(0);
        } else {
            return null;
        }
    }
}
