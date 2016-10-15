package knf.animeflv.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import knf.animeflv.Parser;

/**
 * Created by Jordy on 15/07/2016.
 */

public class RandomHelper {
    public static int getRandomNumber() {
        return new Random().nextInt(Parser.getLastAidCached() - 1) + 1;
    }

    public static List<AnimeObject> getList(int number) {
        List<AnimeObject> list = new ArrayList<>();
        for (int i = 0; i <= number; i++) {
            list.add(new AnimeObject(String.valueOf(getRandomNumber())));
        }
        return list;
    }
}
