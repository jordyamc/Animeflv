package knf.animeflv.Changelog;

import androidx.annotation.Nullable;

import java.util.List;

/**
 * Created by Jordy on 24/10/2016.
 */

public class ChangeLogObjects {
    public enum LogType {
        IMPORTANTE(0),
        CAMBIO(1),
        NUEVO(2),
        CORREGIDO(3),
        NUEVO_IMPORTANTE(4),
        NORMAL(5);
        int value;

        LogType(int value) {
            this.value = value;
        }

    }

    public static class Version {
        public String name;
        public List<Log> logs;

        public Version(String name, List<Log> logs) {
            this.name = name;
            this.logs = logs;
        }
    }

    public static class Log {
        public LogType type;
        public String description;
        public List<Log> sublist;
        public boolean haveExtras = false;

        public Log(LogType type, String description, @Nullable List<Log> sublist) {
            this.type = type;
            this.description = description;
            if (sublist != null) {
                this.sublist = sublist;
                this.haveExtras = true;
            }
        }
    }
}
