package knf.animeflv.ServerReload.Adapter;

import org.json.JSONObject;

import knf.animeflv.Utils.FileUtil;

/**
 * Created by Jordy on 05/05/2016.
 */
public class RecObject {
    private String name;
    private String aid;
    private String normalUrl;
    private String bypassUrl;

    public RecObject(JSONObject object) {
        try {
            name = FileUtil.corregirTit(object.getString("titulo") + " " + object.getString("numero"));
            aid = object.getString("aid");
            normalUrl = object.getString("normal");
            bypassUrl = object.getString("bypass");
        } catch (Exception e) {
            name = "Error!";
            aid = "-1";
            normalUrl = "error";
            bypassUrl = "error";
        }

    }

    public String getName() {
        return name;
    }

    public String getAid() {
        return aid;
    }

    public String getUrl(Type type) {
        if (type == Type.NORMAL) {
            return normalUrl;
        } else {
            return bypassUrl;
        }
    }

    public enum Type {
        NORMAL(0),
        BYPASS(1);
        int value;

        Type(int value) {
            this.value = value;
        }
    }
}
