package knf.animeflv.Suggestions.Algoritm;

import java.util.Comparator;

import knf.animeflv.Random.AnimeObject;

public class SuggestionNameCompare implements Comparator<AnimeObject> {
    @Override
    public int compare(AnimeObject o1, AnimeObject o2) {
        return o1.title.compareToIgnoreCase(o2.title);
    }
}
