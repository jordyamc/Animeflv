package knf.animeflv.Explorer;

import java.util.Comparator;

import knf.animeflv.Explorer.Models.VideoFile;

public class VideoComparator implements Comparator<VideoFile> {
    @Override
    public int compare(VideoFile lhs, VideoFile rhs) {
        return Integer.parseInt(lhs.getCapNumber()) - Integer.parseInt(rhs.getCapNumber());
    }
}
