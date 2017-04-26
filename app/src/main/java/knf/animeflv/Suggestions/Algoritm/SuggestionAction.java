package knf.animeflv.Suggestions.Algoritm;

/**
 * Created by Jordy on 21/04/2017.
 */

public enum SuggestionAction {
    EXPLORE(1),
    PLAY(2),
    UNFAV(-2),
    UNFOLLOW(-2),
    FAV(3),
    FOLLOW(4);

    public int value;

    SuggestionAction(int value) {
        this.value = value;
    }
}
