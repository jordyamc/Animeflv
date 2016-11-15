package knf.animeflv.Utils.Files;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jordy on 22/03/2016.
 */
public class FileSearchResponse {
    private List<String> sdNames = new ArrayList<>();
    private List<String> sdDirs = new ArrayList<>();

    public FileSearchResponse(List<String> sdNames, List<String> sdDirs) {
        this.sdNames = sdNames;
        this.sdDirs = sdDirs;
    }

    public List<String> list() {
        return sdNames;
    }

    public List<String> listDisrs() {
        return sdDirs;
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
