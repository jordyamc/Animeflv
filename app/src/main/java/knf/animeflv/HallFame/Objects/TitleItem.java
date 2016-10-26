package knf.animeflv.HallFame.Objects;

/**
 * Created by Jordy on 25/10/2016.
 */

public class TitleItem extends ListItem {
    public TitleItem(String name) {
        this.name = name;
    }

    @Override
    public boolean isTitle() {
        return true;
    }
}
