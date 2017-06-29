package knf.animeflv.Random;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import knf.animeflv.Directorio.DB.DirectoryHelper;

/**
 * Created by Jordy on 15/07/2016.
 */

public class RandomHelper {
    public static int getRandomNumber(Context context) {
        try {
            return new Random().nextInt(DirectoryHelper.get(context).lastAid() - 1) + 1;
        } catch (Exception e) {
            return new Random().nextInt(2800) + 1;
        }
    }

    public static List<AnimeObject> getList(Context context, int number) {
        List<AnimeObject> list = new ArrayList<>();
        for (int i = 0; i <= number; i++) {
            list.add(new AnimeObject(context, String.valueOf(getRandomNumber(context))));
        }
        return list;
    }
}
