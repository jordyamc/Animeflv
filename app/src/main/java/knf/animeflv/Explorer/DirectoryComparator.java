package knf.animeflv.Explorer;

import java.util.Comparator;

import knf.animeflv.Explorer.Models.Directory;

public class DirectoryComparator implements Comparator<Directory> {
    @Override
    public int compare(Directory lhs, Directory rhs) {
        return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
    }
}
