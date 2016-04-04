package knf.animeflv.Utils;

import android.media.MediaPlayer;
import android.preference.Preference;

/**
 * Created by Jordy on 02/04/2016.
 */
public class UtilDialogPref {
    private static String[] lista;
    private static String key;
    private static String def;
    private static String titulo;
    private static MediaPlayer player;
    private static Preference preference;
    private static String[] custom;
    private static String pattern;
    private static int selected = -1;

    public static void init(String[] lista, String key, String def, String titulo, MediaPlayer player, Preference preference) {
        UtilDialogPref.lista = lista;
        UtilDialogPref.key = key;
        UtilDialogPref.def = def;
        UtilDialogPref.titulo = titulo;
        UtilDialogPref.player = player;
        UtilDialogPref.preference = preference;
    }

    public static void init(String[] lista, String key, String def, String titulo, Preference preference) {
        UtilDialogPref.lista = lista;
        UtilDialogPref.key = key;
        UtilDialogPref.def = def;
        UtilDialogPref.titulo = titulo;
        UtilDialogPref.preference = preference;
        UtilDialogPref.custom = null;
        UtilDialogPref.pattern = "%s";
    }

    public static void init(String[] lista, String[] custom, String pattern, String key, String def, String titulo, Preference preference) {
        UtilDialogPref.lista = lista;
        UtilDialogPref.key = key;
        UtilDialogPref.def = def;
        UtilDialogPref.titulo = titulo;
        UtilDialogPref.preference = preference;
        UtilDialogPref.custom = custom;
        UtilDialogPref.pattern = pattern;
    }

    public static void init(String[] lista, String pattern, String key, String def, String titulo, Preference preference) {
        UtilDialogPref.lista = lista;
        UtilDialogPref.key = key;
        UtilDialogPref.def = def;
        UtilDialogPref.titulo = titulo;
        UtilDialogPref.preference = preference;
        UtilDialogPref.custom = null;
        UtilDialogPref.pattern = pattern;
    }

    public static String[] getCustom() {
        return custom;
    }

    public static String getPattern() {
        return pattern;
    }

    public static Preference getPreference() {
        return preference;
    }

    public static MediaPlayer getPlayer() {
        return player;
    }

    public static void setPlayer(MediaPlayer player) {
        UtilDialogPref.player = player;
    }

    public static String[] getLista() {
        return lista;
    }

    public static String getKey() {
        return key;
    }

    public static String getDef() {
        return def;
    }

    public static String getTitulo() {
        return titulo;
    }

    public static int getSelected() {
        return selected;
    }

    public static void setSelected(int selected) {
        UtilDialogPref.selected = selected;
    }
}
