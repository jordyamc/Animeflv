package knf.animeflv.JsonFactory.Objects;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnimeInfo {
    public String aid;
    public String title;
    public String sid;
    public HashMap<String, String> epMap;

    public AnimeInfo(String code) {
        //Matcher matcher = Pattern.compile("\"(.*)\",\"(.*)\",\"(.*)\",\"(.*)\"").matcher(code);
        Matcher matcher = Pattern.compile("\"([^\",/<>]*)\"").matcher(code);
        int i = 0;
        while (matcher.find()) {
            switch (i) {
                case 0:
                    this.aid = matcher.group(1);
                    break;
                case 1:
                    this.title = matcher.group(1);
                    break;
                case 2:
                    this.sid = matcher.group(1);
                    break;
            }
            i++;
        }
        this.epMap = getEpListMap(code);
    }

    private HashMap<String, String> getEpListMap(String code) {
        HashMap<String, String> map = new LinkedHashMap<>();
        Matcher matcher = Pattern.compile("\\[(\\d+),(\\d+)\\]").matcher(code);
        while (matcher.find()) {
            map.put(matcher.group(1), matcher.group(2));
        }
        return map;
    }
}
