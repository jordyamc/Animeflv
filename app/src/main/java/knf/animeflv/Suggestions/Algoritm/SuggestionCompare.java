package knf.animeflv.Suggestions.Algoritm;

import java.util.Comparator;

/**
 * Created by Jordy on 21/04/2017.
 */

public class SuggestionCompare implements Comparator<SuggestionDB.Suggestion> {
    @Override
    public int compare(SuggestionDB.Suggestion o1, SuggestionDB.Suggestion o2) {
        return o2.count - o1.count;
    }
}
