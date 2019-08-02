package knf.animeflv;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.documentfile.provider.DocumentFile;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import knf.animeflv.Changelog.ChangelogActivity;
import knf.animeflv.CustomSettingsIntro.CustomIntro;
import knf.animeflv.CustomViews.LauncherIconAdapter;
import knf.animeflv.Directorio.DB.DirectoryDB;
import knf.animeflv.Directorio.Directorio;
import knf.animeflv.Jobs.CheckJob;
import knf.animeflv.JsonFactory.SelfGetter;
import knf.animeflv.SDControl.SDManager;
import knf.animeflv.SDControl.SDResultContainer;
import knf.animeflv.SDControl.SDSearcher;
import knf.animeflv.Tutorial.TutorialActivity;
import knf.animeflv.Utils.CacheControl;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.Files.FileSearchResponse;
import knf.animeflv.Utils.FragmentExtras;
import knf.animeflv.Utils.Keys;
import knf.animeflv.Utils.Keys.Conf;
import knf.animeflv.Utils.Logger;
import knf.animeflv.Utils.NetworkUtils;
import knf.animeflv.Utils.SoundsLoader;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.UpdateUtil;
import knf.animeflv.Utils.UtilDialogPref;
import knf.animeflv.Utils.UtilSound;
import knf.animeflv.Utils.eNums.SDStatus;
import knf.animeflv.Utils.eNums.UpdateState;
import xdroid.toaster.Toaster;

//@SuppressWarnings("all")
public class Conf_fragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, FolderChooserDialog.FolderCallback {
    Activity context;
    MediaPlayer mp;
    private int REQUEST_CODE_LOGIN = 58458;
    private FragmentActivity myContext;

    public static String formatSize(long v) {
        if (v < 1024) return v + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format(Locale.US, "%.1f %sB", (double) v / (1L << (z * 10)), " KMGTPE".charAt(z));
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static long getFileSize(final File file) {
        if (file == null || !file.exists())
            return 0;
        if (!file.isDirectory())
            return file.length();
        final List<File> dirs = new LinkedList<File>();
        dirs.add(file);
        long result = 0;
        while (!dirs.isEmpty()) {
            final File dir = dirs.remove(0);
            if (!dir.exists())
                continue;
            final File[] listFiles = dir.listFiles();
            if (listFiles == null || listFiles.length == 0)
                continue;
            for (final File child : listFiles) {
                result += child.length();
                if (child.isDirectory())
                    dirs.add(child);
            }
        }
        return result;
    }

    //FIXME:TEST AREA
    private void doTests() throws Exception {
        getPreferenceScreen().findPreference("open_intro").setOnPreferenceClickListener(preference -> {
            context.startActivity(new Intent(context, Intronew.class));
            return false;
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(ThemeUtils.Theme.create(getActivity()).background);
        return view;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
        context = getActivity();

        //Test Area
        try {
            doTests();
        } catch (NullPointerException n) {
            //null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //<----------

        Boolean activado = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("notificaciones", true);
        if (!activado) {
            getPreferenceScreen().findPreference("tiempo").setEnabled(false);
            getPreferenceScreen().findPreference("sonido").setEnabled(false);
            getPreferenceScreen().findPreference(Conf.INDICADOR_SONIDOS).setEnabled(false);
            getPreferenceScreen().findPreference(Conf.RECHARGE_SOUNDS).setEnabled(false);
        } else {
            getPreferenceScreen().findPreference("tiempo").setEnabled(true);
            getPreferenceScreen().findPreference("sonido").setEnabled(true);
            getPreferenceScreen().findPreference(Conf.INDICADOR_SONIDOS).setEnabled(true);
            getPreferenceScreen().findPreference(Conf.RECHARGE_SOUNDS).setEnabled(true);
        }
        if (UtilDialogPref.getPlayer() != null) {
            mp = UtilDialogPref.getPlayer();
        } else {
            mp = UtilSound.getMediaPlayer(context, UtilSound.getSetDefMediaPlayerInt());
        }
        final File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download");
        long dirsize = getFileSize(file);
        String vidsize = formatSize(dirsize);

        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("b_video", vidsize).apply();
        getPreferenceScreen().findPreference("b_cache").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getActivity().finish();
                getActivity().startActivity(new Intent(getActivity(), CacheControl.class));
                return false;
            }
        });

        getPreferenceScreen().findPreference("r_sounds").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (NetworkUtils.isNetworkAvailable()) {
                    Toaster.toast("Se paciente...");
                    cleanSounds();
                } else {
                    Toaster.toast("Se necesita internet para esto...");
                }
                return false;
            }
        });
        getPreferenceScreen().findPreference("update_check_now").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (UpdateUtil.getState() != UpdateState.DOWNLOADING) {
                    NetworkUtils.checkVersion(context, true);
                } else {
                    Toaster.toast("La actualizacion se esta descargando");
                }
                return false;
            }
        });
        getPreferenceScreen().findPreference("b_video").setSummary("Espacio usado: " + vidsize);
        getPreferenceScreen().findPreference("b_video").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new MaterialDialog.Builder(myContext)
                        .title("ELIMINAR")
                        .titleGravity(GravityEnum.CENTER)
                        .content("Desea eliminar TODOS los animes descargados?")
                        .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                        .positiveText("SI")
                        .negativeText("CANCELAR")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                deleteDownload(file);
                                DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                                String[] eids = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("teids", "").split(":::");
                                for (String s : eids) {
                                    if (!s.trim().equals("")) {
                                        long l = Long.parseLong(context.getSharedPreferences("data", Context.MODE_PRIVATE).getString(s, "0"));
                                        manager.remove(l);
                                    }
                                }
                                String si = formatSize(getFileSize(file));
                                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("b_video", si).commit();
                                getPreferenceScreen().findPreference("b_video").setSummary("Espacio usado: " + si);
                            }
                        }).build().show();

                return false;
            }
        });
        getPreferenceScreen().findPreference("b_log").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                /*ChangelogDialog.create()
                        .show(myContext.getSupportFragmentManager(), "changelog");*/
                startActivity(new Intent(getActivity(), ChangelogActivity.class));
                return false;
            }
        });
        getPreferenceScreen().findPreference("b_vistos").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String vistos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", "");
                String[] array = vistos.split(":::");
                for (String s : array) {
                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean(s, false).apply();
                }
                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("vistos", "").apply();
                Toast.makeText(getActivity(), "Historial Borrado!!", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        getPreferenceScreen().findPreference("b_move").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (FileUtil.init(context).getSDPath() != null) {
                    if (count() > 0) {
                        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                                .title("Moviendo Animes...")
                                .titleGravity(GravityEnum.CENTER)
                                .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                                .progress(false, count(), true)
                                .cancelable(false)
                                .build();
                        dialog.show();
                        FileMover.PrepareMove(context, new FileMover.OnProgressListener() {
                            @Override
                            public void onStep(int progress, int total) {
                                if (progress < total) {
                                    dialog.setMaxProgress(total);
                                    dialog.setProgress(progress);
                                } else {
                                    dialog.dismiss();
                                }
                            }

                            @Override
                            public void onSemiStep(String name, int progress) {

                            }

                            @Override
                            public void onError(String name) {
                                Toaster.toast("Error al Mover " + name);
                            }
                        });
                        //new MoveFiles(context, myContext).execute();
                    } else {
                        Toast.makeText(context, "No hay archivos para mover", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, "No se detecta tarjeta SD", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
        getPreferenceScreen().findPreference("sonido").setSummary(getStringfromArray(UtilSound.getSoundsNameList(), "sonido", "0"));
        getPreferenceScreen().findPreference("sonido").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (UtilDialogPref.getPlayer() != null) {
                    mp = UtilDialogPref.getPlayer();
                }
                getPreferenceScreen().findPreference("sonido").setSummary(getStringfromArray(UtilSound.getSoundsNameList(), "sonido", "0"));
                UtilDialogPref.init(UtilSound.getSoundsNameList(), "sonido", "0", "Sonido", mp, getPreferenceScreen().findPreference("sonido"));
                DialogSounds.create().show(myContext.getSupportFragmentManager(), "Pref");
                return false;
            }
        });
        getPreferenceScreen().findPreference("tiempo").setSummary("Revisar actualizaciones cada " + getStringfromResourse(R.array.minutos, "tiempo", "60000"));
        getPreferenceScreen().findPreference("tiempo").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UtilDialogPref.init(getResources().getStringArray(R.array.minutos), getResources().getStringArray(R.array.min_val), "Revisar actualizaciones cada %s", "tiempo", "60000", "Revisar cada", getPreferenceScreen().findPreference("tiempo"));
                PrefDialogSimple.create().show(myContext.getSupportFragmentManager(), "PrefSimple");
                return false;
            }
        });
        getPreferenceScreen().findPreference(Conf.INDICADOR_SONIDOS).setSummary(getStringfromResourse(R.array.sound_ind, Conf.INDICADOR_SONIDOS, "0"));
        getPreferenceScreen().findPreference(Conf.INDICADOR_SONIDOS).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UtilDialogPref.init(getResources().getStringArray(R.array.sound_ind), Conf.INDICADOR_SONIDOS, "0", "Tipo", getPreferenceScreen().findPreference(Conf.INDICADOR_SONIDOS));
                PrefDialogSimple.create().show(myContext.getSupportFragmentManager(), "PrefSimple");
                return false;
            }
        });
        getPreferenceScreen().findPreference("t_conexion").setSummary(getStringfromResourse(R.array.tipos, "t_conexion", "2"));
        getPreferenceScreen().findPreference("t_conexion").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UtilDialogPref.init(getResources().getStringArray(R.array.tipos), "t_conexion", "2", "Usar Conexion", getPreferenceScreen().findPreference("t_conexion"));
                PrefDialogSimple.create().show(myContext.getSupportFragmentManager(), "PrefSimple");
                return false;
            }
        });
        getPreferenceScreen().findPreference("bypass_time").setSummary("Esperar " + getStringfromResourse(R.array.time_bypass, "bypass_time", "30000") + " para cancelar bypass");
        getPreferenceScreen().findPreference("bypass_time").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UtilDialogPref.init(getResources().getStringArray(R.array.time_bypass), getResources().getStringArray(R.array.time_val_bypass), "Esperar %s para cancelar bypass", "bypass_time", "30000", "Tiempo para cancelar", getPreferenceScreen().findPreference("bypass_time"));
                PrefDialogSimple.create().show(myContext.getSupportFragmentManager(), "PrefSimple");
                return false;
            }
        });
        getPreferenceScreen().findPreference("t_busqueda").setSummary(getStringfromResourse(R.array.busqueda, "t_busqueda", "0"));
        getPreferenceScreen().findPreference("t_busqueda").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UtilDialogPref.init(getResources().getStringArray(R.array.busqueda), "t_busqueda", "0", "Tipo de busqueda", getPreferenceScreen().findPreference("t_busqueda"));
                PrefDialogSimple.create().show(myContext.getSupportFragmentManager(), "PrefSimple");
                return false;
            }
        });
        getPreferenceScreen().findPreference("ord_busqueda").setSummary(getStringfromResourse(R.array.busqueda_sort, "ord_busqueda", "0"));
        getPreferenceScreen().findPreference("ord_busqueda").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UtilDialogPref.init(getResources().getStringArray(R.array.busqueda_sort), "ord_busqueda", "0", "Orden de busqueda", getPreferenceScreen().findPreference("ord_busqueda"));
                PrefDialogSimple.create().show(myContext.getSupportFragmentManager(), "PrefSimple");
                return false;
            }
        });
        getPreferenceScreen().findPreference("t_video").setSummary("Reproductor: " + getStringfromResourse(R.array.players, "t_video", "0"));
        getPreferenceScreen().findPreference("t_video").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UtilDialogPref.init(getResources().getStringArray(R.array.players), "Reproductor: %s", "t_video", "0", "Reproductor", getPreferenceScreen().findPreference("t_video"));
                PrefDialogSimple.create().show(myContext.getSupportFragmentManager(), "PrefSimple");
                return false;
            }
        });
        getPreferenceScreen().findPreference("t_streaming").setSummary("Reproductor: " + getStringfromResourse(R.array.players, "t_streaming", "0"));
        getPreferenceScreen().findPreference("t_streaming").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UtilDialogPref.init(getResources().getStringArray(R.array.players), "Reproductor: %s", "t_streaming", "0", "Reproductor", getPreferenceScreen().findPreference("t_streaming"));
                PrefDialogSimple.create().show(myContext.getSupportFragmentManager(), "PrefSimple");
                return false;
            }
        });
        getPreferenceScreen().findPreference("t_player").setSummary("Tipo: " + getStringfromResourse(R.array.players_type, "t_player", "0"));
        getPreferenceScreen().findPreference("t_player").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UtilDialogPref.init(getResources().getStringArray(R.array.players_type), "Tipo: %s", "t_player", "0", "Selecciona", getPreferenceScreen().findPreference("t_player"));
                PrefDialogSimple.create().show(myContext.getSupportFragmentManager(), "PrefSimple");
                return false;
            }
        });
        getPreferenceScreen().findPreference("sort_fav").setSummary(getStringfromResourse(R.array.sort_type, "sort_fav", "0"));
        getPreferenceScreen().findPreference("sort_fav").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UtilDialogPref.init(getResources().getStringArray(R.array.sort_type), "%s", "sort_fav", "0", "Selecciona", getPreferenceScreen().findPreference("sort_fav"));
                PrefDialogSimple.create().show(myContext.getSupportFragmentManager(), "PrefSimple");
                return false;
            }
        });
        getPreferenceScreen().findPreference("def_download_serv").setSummary(getStringfromResourse(SelfGetter.getDownloadServersOptions(), "def_download_serv", "0"));
        getPreferenceScreen().findPreference("def_download_serv").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UtilDialogPref.init(SelfGetter.getDownloadServersOptions(), "%s", "def_download_serv", "0", "Selecciona", getPreferenceScreen().findPreference("def_download_serv"));
                PrefDialogSimple.create().show(myContext.getSupportFragmentManager(), "PrefSimple");
                return false;
            }
        });
        getPreferenceScreen().findPreference("sug_order").setSummary(getStringfromResourse(R.array.sug_order, "sug_order", "0"));
        getPreferenceScreen().findPreference("sug_order").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UtilDialogPref.init(getResources().getStringArray(R.array.sug_order), "%s", "sug_order", "0", "Selecciona", getPreferenceScreen().findPreference("sug_order"));
                PrefDialogSimple.create().show(myContext.getSupportFragmentManager(), "PrefSimple");
                return false;
            }
        });
        getPreferenceScreen().findPreference("list_style").setSummary(getStringfromResourse(R.array.list_styles, "list_style", "0"));
        getPreferenceScreen().findPreference("list_style").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UtilDialogPref.init(getResources().getStringArray(R.array.list_styles), "%s", "list_style", "0", "Selecciona", getPreferenceScreen().findPreference("list_style"));
                PrefDialogSimple.create().show(myContext.getSupportFragmentManager(), "PrefSimple");
                return false;
            }
        });
        getPreferenceScreen().findPreference("Rpath").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("SDPath", null).commit();
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Keys.Extra.EXTERNAL_SD_ACCESS_URI, null).commit();
                getActivity().recreate();
                return false;
            }
        });
        getPreferenceScreen().findPreference("open_tutorial").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                context.startActivity(new Intent(context, TutorialActivity.class));
                return false;
            }
        });
        getPreferenceScreen().findPreference(Conf.SD_ACCESS).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                openAccessFramework();
                return false;
            }
        });
        getPreferenceScreen().findPreference("SDpath").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                openAccessFramework();
                return false;
            }
        });
        getPreferenceScreen().findPreference("launcher_icon").setSummary(PreferenceManager.getDefaultSharedPreferences(context).getString("launcher_icon", "Normal"));
        getPreferenceScreen().findPreference("launcher_icon").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                final LauncherIconAdapter adapter = new LauncherIconAdapter(new LauncherIconAdapter.Callback() {
                    @Override
                    public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item) {
                        String name = item.getContent().toString();
                        getPreferenceScreen().findPreference("launcher_icon").setSummary(name);
                        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("launcher_icon", name).apply();
                        disableIcons();
                        switch (index) {
                            case 0:
                                context.getPackageManager().setComponentEnabledSetting(
                                        new ComponentName(context.getPackageName(), context.getPackageName() + ".SplashNormal"),
                                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                                break;
                            case 1:
                                context.getPackageManager().setComponentEnabledSetting(
                                        new ComponentName(context.getPackageName(), context.getPackageName() + ".SplashVariant"),
                                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                                break;
                            case 2:
                                context.getPackageManager().setComponentEnabledSetting(
                                        new ComponentName(context.getPackageName(), context.getPackageName() + ".SplashHamnster"),
                                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                                break;
                            case 3:
                                context.getPackageManager().setComponentEnabledSetting(
                                        new ComponentName(context.getPackageName(), context.getPackageName() + ".SplashText"),
                                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                                break;
                            case 4:
                                context.getPackageManager().setComponentEnabledSetting(
                                        new ComponentName(context.getPackageName(), context.getPackageName() + ".SplashWeb"),
                                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                                break;
                        }
                        Toaster.toast("Puede tomar unos segundos en cambiar el icono");
                        dialog.dismiss();
                    }
                });
                adapter.add(new MaterialSimpleListItem.Builder(context)
                        .content("Normal")
                        .icon(R.mipmap.ic_launcher).build());
                adapter.add(new MaterialSimpleListItem.Builder(context)
                        .content("Variante")
                        .icon(R.mipmap.ic_launcher_variant).build());
                adapter.add(new MaterialSimpleListItem.Builder(context)
                        .content("Hamnster")
                        .icon(R.mipmap.ic_launcher_hamnster).build());
                adapter.add(new MaterialSimpleListItem.Builder(context)
                        .content("Texto")
                        .icon(R.mipmap.ic_launcher_text).build());
                adapter.add(new MaterialSimpleListItem.Builder(context)
                        .content("Web")
                        .icon(R.mipmap.ic_launcher_web).build());
                new MaterialDialog.Builder(context)
                        .title("Icono de la app")
                        .adapter(adapter, null)
                        .show();
                return false;
            }
        });

        if (FileUtil.init(context).getSDPath() == null) {
            getPreferenceScreen().findPreference("b_move").setEnabled(false);
            getPreferenceScreen().findPreference("sd_down").setEnabled(false);
            getPreferenceScreen().findPreference("b_move").setSummary("Tarjeta SD no encontrada");
            final FileSearchResponse response = FileUtil.init(context).searchforSD();
            if (response.existSD()) {
                if (response.isOnlyOne()) {
                    if (!response.getUniqueName().contains("_noWrite_")) {
                        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("SDPath", response.getUniqueName()).apply();
                        getPreferenceScreen().findPreference("SDpath").setTitle("Cambiar SD");
                        getPreferenceScreen().findPreference("SDpath").setSummary(FileUtil.init(context).getSDPath());
                        getPreferenceScreen().findPreference("SDpath").setEnabled(true);
                        getActivity().recreate();
                    } else {
                        Toaster.toast("Se requiere autorizacion manual para escribir en " + response.getUniqueName().replace("_noWrite_", ""));
                        getPreferenceScreen().findPreference("SDpath").setTitle("No se puede escribir en SD");
                        getPreferenceScreen().findPreference("SDpath").setSummary("Solicitar Permiso");
                        getPreferenceScreen().findPreference("SDpath").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                            @Override
                            public boolean onPreferenceClick(Preference preference) {
                                openAccessFramework();
                                return false;
                            }
                        });
                        PreferenceCategory sd = (PreferenceCategory) getPreferenceScreen().findPreference("catSD");
                        sd.setEnabled(false);
                        /*getPreferenceScreen().removePreference(sd);
                        getPreferenceScreen().findPreference("").setEnabled(false);*/
                    }
                } else {
                    if (response.list().size() > 1) {
                        getPreferenceScreen().findPreference("SDpath").setTitle("Multiples SD Encontrados");
                        getPreferenceScreen().findPreference("SDpath").setSummary("Precione para seleccionar");
                        getPreferenceScreen().findPreference("SDpath").setEnabled(true);
                        getPreferenceScreen().findPreference("SDpath").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                            @Override
                            public boolean onPreferenceClick(Preference preference) {
                                openAccessFramework();
                                return false;
                            }
                        });
                    }
                }
            } else {
                getPreferenceScreen().findPreference("SDpath").setTitle("SD No Encontrada");
                getPreferenceScreen().findPreference("SDpath").setSummary("Seleccionar manualmente");
                getPreferenceScreen().findPreference("SDpath").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        new MaterialDialog.Builder(myContext)
                                .title("Direccion")
                                .titleGravity(GravityEnum.CENTER)
                                .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                                .items(new String[]{"/mnt", "/storage"})
                                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                        Intent i = new Intent(context, FilePickerActivity.class);
                                        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                                        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                                        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
                                        i.putExtra(FilePickerActivity.EXTRA_START_PATH, text);
                                        startActivityForResult(i, 6991);
                                        return false;
                                    }
                                }).build().show();
                        return false;
                    }
                });
                PreferenceCategory sd = (PreferenceCategory) getPreferenceScreen().findPreference("catSD");
                getPreferenceScreen().removePreference(sd);
            }
        } else {
            if (FileUtil.init(context).getSDPath().contains("_noWrite_")) {
                Toaster.toast("Se requiere autorizacion manual para escribir en " + FileUtil.init(context).getSDPath().replace("_noWrite_", ""));
                getPreferenceScreen().findPreference("SDpath").setTitle("No se puede escribir en SD");
                getPreferenceScreen().findPreference("SDpath").setSummary("Solicitar Permiso");
                getPreferenceScreen().findPreference("SDpath").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        openAccessFramework();
                        return false;
                    }
                });
                PreferenceCategory sd = (PreferenceCategory) getPreferenceScreen().findPreference("catSD");
                getPreferenceScreen().removePreference(sd);
            } else {
                getPreferenceScreen().findPreference("sd_down").setEnabled(true);
                getPreferenceScreen().findPreference("b_move").setEnabled(true);
                getPreferenceScreen().findPreference("SDpath").setTitle("Cambiar SD");
                getPreferenceScreen().findPreference("SDpath").setSummary(FileUtil.init(context).getSDPath());
                getPreferenceScreen().findPreference("SDpath").setEnabled(true);

            }
        }
        openIfExtra();
    }

    @TargetApi(21)
    private void openAccessFramework() {
        startActivityForResult(new Intent(context, SDManager.class), SDManager.REQUEST_CODE);
    }

    private void openIfExtra() {
        int bundle = FragmentExtras.KEY;
        if (bundle != -1) {
            switch (bundle) {
                case Configuracion.OPEN_SOUNDS:
                    if (UtilDialogPref.getPlayer() != null) {
                        mp = UtilDialogPref.getPlayer();
                    }
                    UtilDialogPref.init(UtilSound.getSoundsNameList(), "sonido", "0", "Sonido", mp, getPreferenceScreen().findPreference("sonido"));
                    DialogSounds.create().show(myContext.getSupportFragmentManager(), "Pref");
                    break;
                case Configuracion.GET_WRITE_PERMISSIONS:
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    startActivityForResult(intent, 15890);
                    break;
            }
            FragmentExtras.KEY = -1;
        } else {
            Log.d("Conf", "No Extras");
        }
    }

    private void disableIcons() {
        context.getPackageManager().setComponentEnabledSetting(
                new ComponentName(context.getPackageName(), context.getPackageName() + ".SplashNormal"),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        context.getPackageManager().setComponentEnabledSetting(
                new ComponentName(context.getPackageName(), context.getPackageName() + ".SplashVariant"),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        context.getPackageManager().setComponentEnabledSetting(
                new ComponentName(context.getPackageName(), context.getPackageName() + ".SplashHamnster"),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        context.getPackageManager().setComponentEnabledSetting(
                new ComponentName(context.getPackageName(), context.getPackageName() + ".SplashText"),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        context.getPackageManager().setComponentEnabledSetting(
                new ComponentName(context.getPackageName(), context.getPackageName() + ".SplashWeb"),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    private String getStringfromResourse(@ArrayRes int array, String key, String def) {
        return getStringfromResourse(getResources().getStringArray(array), key, def);
    }

    private String getStringfromResourse(String[] array, String key, String def) {
        try {
            return array[Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(myContext).getString(key, def))];
        } catch (IndexOutOfBoundsException e) {
            if (key.equals("tiempo")) {
                return array[Arrays.asList(getResources().getStringArray(R.array.min_val)).indexOf(PreferenceManager.getDefaultSharedPreferences(myContext).getString(key, def))];
            } else if (key.equals("bypass_time")) {
                return array[Arrays.asList(getResources().getStringArray(R.array.time_val_bypass)).indexOf(PreferenceManager.getDefaultSharedPreferences(myContext).getString(key, def))];
            } else {
                Logger.Error(getClass(), e);
                PreferenceManager.getDefaultSharedPreferences(myContext).edit().putString(key, def).apply();
                Toaster.toast("Error en " + key + ", opcion reiniciada");
                return array[0];
            }
        }
    }

    private String getStringfromArray(String[] array, String key, String def) {
        try {
            return array[Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(myContext).getString(key, def))];
        } catch (ArrayIndexOutOfBoundsException e) {
            Logger.Error(getClass(), e);
            PreferenceManager.getDefaultSharedPreferences(myContext).edit().putString(key, def).apply();
            Toaster.toast("Error en " + key + ", opcion reiniciada");
            return "Cargando...";
        }
    }

    private boolean excludechar(String input) {
        List<String> exclude = FileUtil.getExcludeDirList();
        for (String i : exclude) {
            if (input.contains("/" + i)) {
                return true;
            }
        }
        return false;
    }

    private int count() {
        int count = 0;
        File f = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download");
        File[] files = f.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    if (file.list() != null) {
                        count += file.list().length;
                    }
                }
            }
        }
        return count;
    }

    public void deleteDownload(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                deleteDir(new File(dir, children[i]));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        new Parser().saveBackup(context);
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        Log.d("Preference", key);
        switch (key) {
            case "notificaciones":
                Boolean activado = sharedPreferences.getBoolean(key, true);
                if (!activado) {
                    getPreferenceScreen().findPreference("tiempo").setEnabled(false);
                    getPreferenceScreen().findPreference("sonido").setEnabled(false);
                    getPreferenceScreen().findPreference(Conf.INDICADOR_SONIDOS).setEnabled(false);
                    getPreferenceScreen().findPreference(Conf.RECHARGE_SOUNDS).setEnabled(false);
                    new Alarm().CancelAlarm(context);
                    CheckJob.stopShedule();
                } else {
                    getPreferenceScreen().findPreference("tiempo").setEnabled(true);
                    getPreferenceScreen().findPreference("sonido").setEnabled(true);
                    getPreferenceScreen().findPreference(Conf.INDICADOR_SONIDOS).setEnabled(true);
                    getPreferenceScreen().findPreference(Conf.RECHARGE_SOUNDS).setEnabled(true);
                    new Alarm().SetAlarm(context);
                    CheckJob.shedule(context);
                }
                break;
            case "tiempo":
                int tiempo = Integer.parseInt(sharedPreferences.getString(key, "60000"));
                new Alarm().CancelAlarm(context);
                new Alarm().SetAlarm(context, tiempo);
                CheckJob.reshedule(context);
                break;
            case "sd_down":
                if (sharedPreferences.getBoolean(key, false)) {
                    if (FileUtil.init(context).getSDPath() == null) {
                        getPreferenceScreen().findPreference(key).setEnabled(false);
                        sharedPreferences.edit().putBoolean(key, false).apply();
                    }
                }
                break;
            case "betaSounds":
                SoundsLoader.start(context);
                if (UtilDialogPref.getPlayer() != null) {
                    if (UtilDialogPref.getPlayer().isPlaying()) {
                        UtilDialogPref.getPlayer().stop();
                    }
                }
                break;
            case Conf.INDICADOR_SONIDOS:
                if (sharedPreferences.getString(key, "0").equals("0")) {
                    if (UtilSound.isNotSoundShow) {
                        UtilSound.NotManager().cancel(UtilSound.NOT_SOUND_ID);
                        UtilSound.isNotSoundShow = false;
                    }
                }
                if (sharedPreferences.getString(key, "0").equals("1")) {
                    UtilSound.toogleNotSound(UtilSound.getCurrentMediaPlayerInt());
                }
                if (sharedPreferences.getString(key, "0").equals("2")) {
                    if (UtilSound.isNotSoundShow) {
                        UtilSound.NotManager().cancel(UtilSound.NOT_SOUND_ID);
                        UtilSound.isNotSoundShow = false;
                    }
                }
                break;
            case "use_tags":
                if (sharedPreferences.getBoolean(key, false))
                    new MaterialDialog.Builder(getActivity())
                            .content("Activar esta opcion requiere recrear el directorio, por los nuevos cambios de la pagina se tardara mas tiempo de lo normal (hasta 10 minutos dependiendo de la velocidad del internet)")
                            .positiveText("ACTIVAR")
                            .negativeText("CANCELAR")
                            .canceledOnTouchOutside(true)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    Keys.Dirs.CACHE_DIRECTORIO.delete();
                                    new DirectoryDB(getActivity()).reset();
                                    getActivity().startActivity(new Intent(getActivity(), Directorio.class));
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    sharedPreferences.edit().putBoolean(key, false).apply();
                                    ((com.lb.material_preferences_library.custom_preferences.SwitchPreference) getPreferenceScreen().findPreference(key)).setChecked(false);
                                }
                            })
                            .cancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    sharedPreferences.edit().putBoolean(key, false).apply();
                                    ((com.lb.material_preferences_library.custom_preferences.SwitchPreference) getPreferenceScreen().findPreference(key)).setChecked(false);
                                }
                            }).build().show();
                break;
            case "resaltar":
                if (sharedPreferences.getBoolean(key, true)) {
                    getPreferenceScreen().findPreference("color_favs").setEnabled(true);
                    getPreferenceScreen().findPreference("color_new").setEnabled(true);
                    getPreferenceScreen().findPreference("color_default").setEnabled(true);
                } else {
                    getPreferenceScreen().findPreference("color_favs").setEnabled(false);
                    getPreferenceScreen().findPreference("color_new").setEnabled(false);
                    getPreferenceScreen().findPreference("color_default").setEnabled(false);
                }
                break;
            case "list_style":
                getActivity().setResult(9988);
                break;
        }
    }

    private void cleanSounds() {
        File dir = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/.sounds");
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                file.delete();
            }
            SoundsLoader.start(context);
        }
    }

    @Override
    public void onFolderSelection(@NonNull FolderChooserDialog dialog, @NonNull File folder) {
        Toast.makeText(myContext, folder.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }


    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);

    }

    @TargetApi(19)
    private void takePermission(Intent data) {
        Uri treeUri = data.getData();
        final int takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        try {
            context.getContentResolver().takePersistableUriPermission(treeUri, takeFlags);
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString(Keys.Extra.EXTERNAL_SD_ACCESS_URI, treeUri.toString()).apply();
            DocumentFile pickedDir = DocumentFile.fromTreeUri(context, treeUri);
            DocumentFile newFile = pickedDir.createFile("text/plain", "Prueba");
            OutputStream out = context.getContentResolver().openOutputStream(newFile.getUri());
            out.write("Prueba".getBytes());
            out.close();
            newFile.delete();
        } catch (SecurityException e) {
            e.printStackTrace();
            Toaster.toast(data.getDataString() + " Directorio no encontrado o invalido");
        } catch (Exception e) {
            e.printStackTrace();
            Toaster.toast("Error al obtener permiso");
        }
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 6991 && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if (excludechar(uri.toString())) {
                Toaster.toast("Directorio no valido");
            } else {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("SDPath", uri.toString().substring(uri.toString().lastIndexOf("/") + 1).trim()).commit();
                if (FileUtil.init(context).getSDPath() != null) {
                    getActivity().recreate();
                } else {
                    Toaster.toast(uri.toString().substring(uri.toString().lastIndexOf("/") + 1) + " Directorio no encontrado o invalido");
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString("SDPath", "null").apply();
                }
            }
        }
        if (requestCode == 15889) {
            if (resultCode == Activity.RESULT_OK) {
                takePermission(data);
            }
        }
        if (requestCode == 15890) {
            if (resultCode == Activity.RESULT_OK) {
                if (FileUtil.checkIfSDCardRoot(data.getData()) == SDStatus.OK) {
                    takePermission(data);
                } else {
                    getActivity().setResult(-1, new Intent().putExtra("error", FileUtil.checkIfSDCardRoot(data.getData()).getErrorMessage()));
                }
                getActivity().finish();
            }
        }
        if (requestCode == SDManager.REQUEST_CODE && SDResultContainer.getResult() == SDSearcher.SD_SELECTED) {
            try {
                getPreferenceScreen().findPreference("sd_down").setEnabled(true);
                getPreferenceScreen().findPreference("b_move").setEnabled(true);
                getPreferenceScreen().findPreference("SDpath").setTitle("Cambiar SD");
                getPreferenceScreen().findPreference("SDpath").setSummary(FileUtil.init(context).getSDPath());
                getPreferenceScreen().findPreference("SDpath").setEnabled(true);
                PreferenceCategory sd = (PreferenceCategory) getPreferenceScreen().findPreference("catSD");
                sd.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onFolderChooserDismissed(@NonNull FolderChooserDialog folderChooserDialog) {

    }
}
