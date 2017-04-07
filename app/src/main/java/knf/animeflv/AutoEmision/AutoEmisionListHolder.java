package knf.animeflv.AutoEmision;

import java.util.ArrayList;
import java.util.List;

import knf.animeflv.JsonFactory.OfflineGetter;
import knf.animeflv.Recientes.MainAnimeModel;
import knf.animeflv.Recientes.MainOrganizer;

/**
 * Created by Jordy on 09/01/2017.
 */

public class AutoEmisionListHolder {
    private static List<EmObj> list1;
    private static List<EmObj> list2;
    private static List<EmObj> list3;
    private static List<EmObj> list4;
    private static List<EmObj> list5;
    private static List<EmObj> list6;
    private static List<EmObj> list7;


    private static List<MainAnimeModel> episodes;

    public static void setList(int day, List<EmObj> list) {
        switch (day) {
            case 1:
                list1 = list;
                break;
            case 2:
                list2 = list;
                break;
            case 3:
                list3 = list;
                break;
            case 4:
                list4 = list;
                break;
            case 5:
                list5 = list;
                break;
            case 6:
                list6 = list;
                break;
            case 7:
                list7 = list;
                break;
            default:
                break;
        }
    }

    public static List<EmObj> getList(int day) {
        switch (day) {
            case 1:
                return list1;
            case 2:
                return list2;
            case 3:
                return list3;
            case 4:
                return list4;
            case 5:
                return list5;
            case 6:
                return list6;
            case 7:
                return list7;
            default:
                return new ArrayList<>();
        }
    }

    public static List<List<EmObj>> getAllLists() {
        List<List<EmObj>> lists = new ArrayList<>();
        lists.add(list1);
        lists.add(list2);
        lists.add(list3);
        lists.add(list4);
        lists.add(list5);
        lists.add(list6);
        lists.add(list7);
        return lists;
    }

    public static void invalidateLists() {
        list1 = null;
        list2 = null;
        list3 = null;
        list4 = null;
        list5 = null;
        list6 = null;
        list7 = null;
    }

    public static void deleteFromList(String aid, int day) {
        List<EmObj> list = getList(day);
        if (list != null) {
            int count = 0;
            boolean exist = false;
            for (EmObj obj : list) {
                if (obj.getAid().equals(aid)) {
                    exist = true;
                    break;
                }
                count++;
            }
            if (exist) {
                list.remove(count);
                setList(day, list);
            }
        }
    }

    public static void deleteFromList(List<EmObj> list, String aid, int day) {
        if (list != null) {
            int count = 0;
            boolean exist = false;
            for (EmObj obj : list) {
                if (obj.getAid().equals(aid)) {
                    exist = true;
                    break;
                }
                count++;
            }
            if (exist)
                list.remove(count);
        }
    }

    public static List<MainAnimeModel> getEpisodes() {
        return episodes;
    }

    public static void reloadEpisodes() {
        try {
            episodes = MainOrganizer.init(OfflineGetter.getInicio()).list();
        } catch (Exception e) {
            e.printStackTrace();
            episodes = new ArrayList<>();
        }
    }
}
