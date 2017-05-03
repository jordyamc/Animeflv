package knf.animeflv.CustomSettingsIntro.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import agency.tango.materialintroscreen.SlideFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.Keys;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

public class BackupFragment extends SlideFragment {
    public static boolean backupLoaded = false;
    @BindView(R.id.image_slide)
    ImageView image_slide;
    @BindView(R.id.txt_title_slide)
    TextView title;
    @BindView(R.id.txt_description_slide)
    TextView description;
    @BindView(R.id.restore)
    AppCompatButton button;
    private BackupCallback backupCallback;
    private Activity activity;

    public static BackupFragment get(Activity act, BackupCallback callback) {
        BackupFragment fragment = new BackupFragment();
        fragment.setCallBack(callback);
        fragment.setActivity(act);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slide_backup, container, false);
        ButterKnife.bind(this, view);
        image_slide.setImageResource(R.drawable.app_icon_intro);
        BackupFragment.backupLoaded = PreferenceManager.getDefaultSharedPreferences(getA()).getBoolean("f_backup_set", false);
        if (BackupFragment.backupLoaded) {
            setBackup();
        } else {
            setNoInfo();
        }
        if (Keys.Dirs.BACKUP_DATA.exists() && !BackupFragment.backupLoaded) {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    start();
                }
            });
        }
        return view;
    }

    public void setActivity(Activity a) {
        activity = a;
    }

    private Activity getA() {
        if (activity == null) {
            return getActivity();
        } else {
            return activity;
        }
    }

    private void start() {
        getA().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MaterialDialog.Builder(getA())
                        .title("Respaldo")
                        .content("Se ah encontrado un respaldo de la configuracion, ¿Desea restaurarlo?")
                        .positiveText("SI")
                        .negativeText("NO")
                        .autoDismiss(true)
                        .cancelable(true)
                        .backgroundColor(ThemeUtils.isAmoled(getA()) ? ColorsRes.Prim(getA()) : ColorsRes.Blanco(getA()))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                String save = FileUtil.getStringFromFile(Keys.Dirs.BACKUP_DATA.getPath());
                                if (new Parser().restoreBackup(save, getA()) != Parser.Response.OK) {
                                    Toaster.toast("Error al restaurar");
                                    Keys.Dirs.BACKUP_DATA.delete();
                                    new Parser().saveBackup(getA());
                                    BackupFragment.backupLoaded = true;
                                    setNoInfo();
                                } else {
                                    backupCallback.onBackup();
                                    setBackup();
                                }
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Keys.Dirs.BACKUP_DATA.delete();
                                new Parser().saveBackup(getA());
                                setNoInfo();
                            }
                        }).build().show();
            }
        });
    }

    private void setNoInfo() {
        PreferenceManager.getDefaultSharedPreferences(getA()).edit().putBoolean("f_backup_set", true).apply();
        getA().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                title.setText(getText(R.string.intro_title_1));
                description.setText(getText(R.string.intro_desc_1));
            }
        });
    }

    private void setBackup() {
        BackupFragment.backupLoaded = true;
        PreferenceManager.getDefaultSharedPreferences(getA()).edit().putBoolean("f_backup_set", true).apply();
        getA().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                title.setText(getText(R.string.intro_title_1_1));
                description.setText(getText(R.string.intro_desc_1_1));
                button.setVisibility(View.GONE);
            }
        });
    }

    private void setCallBack(BackupCallback back) {
        backupCallback = back;
    }

    @Override
    public int backgroundColor() {
        return R.color.intro_1;
    }

    @Override
    public int buttonsColor() {
        return R.color.intro_1_dark;
    }

    public interface BackupCallback {
        void onBackup();
    }
}
