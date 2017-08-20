package knf.animeflv.JsonFactory.Objects;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Jordy on 17/08/2017.
 */

public class Server {
    public String name;
    public List<Option> options = new ArrayList<>();

    public Server(String name) {
        this.name = name;
    }

    public Server(String name, Option option) {
        this.name = name;
        addOption(option);
    }

    public static List<Server> filter(List<Server> servers) {
        List<String> names = new ArrayList<>();
        List<Server> filtered = new ArrayList<>();
        for (Server server : servers) {
            if (!names.contains(server.name)) {
                names.add(server.name);
                filtered.add(server);
            }
        }
        return filtered;
    }

    public static List<String> getNames(List<Server> servers) {
        List<String> names = new ArrayList<>();
        for (Server server : servers) {
            names.add(server.name);
        }
        return names;
    }

    public static int findPosition(List<Server> servers, String name) {
        int i = 0;
        for (Server server : servers) {
            if (server.name.equals(name))
                return i;
            i++;
        }
        return 0;
    }

    public static boolean existServer(List<Server> servers, int position) {
        String name = Names.getDownloadServers()[position - 1];
        for (Server server : servers) {
            if (server.name.equals(name))
                return true;
        }
        return false;
    }

    public static Server findServer(List<Server> servers, int position) {
        String name = Names.getDownloadServers()[position - 1];
        return servers.get(findPosition(servers, name));
    }

    public void addOption(Option option) {
        options.add(option);
    }

    public Option getOption() {
        return options.get(0);
    }

    public boolean haveOptions() {
        return options.size() > 1;
    }

    public static class Sorter implements Comparator<Server> {
        @Override
        public int compare(Server server, Server t1) {
            return server.name.compareToIgnoreCase(t1.name);
        }
    }

    public static class Names {
        public static final String IZANAGI = "Izanagi";
        public static final String MINHATECA = "Minhateca";
        public static final String YOTTA = "Yotta";
        public static final String HYPERION = "Hyperion";
        public static final String OKRU = "Okru";
        public static final String CLUP = "Clup";
        public static final String MP4UPLOAD = "Mp4Upload";
        public static final String YOURUPLOAD = "YourUpload";
        public static final String ZIPPYSHARE = "Zippyshare";
        public static final String _4SYNC = "4Sync";
        public static final String MEGA = "Mega";
        public static final String ANIMEFLV = "Animeflv";
        public static final String MARU = "Maru";

        public static String[] getDownloadServers() {
            return new String[]{
                    IZANAGI,
                    MINHATECA,
                    YOTTA,
                    HYPERION,
                    OKRU,
                    CLUP,
                    MP4UPLOAD,
                    YOURUPLOAD,
                    ZIPPYSHARE,
                    _4SYNC,
                    MEGA,
                    ANIMEFLV,
                    MARU
            };
        }
    }
}
