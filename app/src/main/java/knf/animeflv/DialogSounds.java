package knf.animeflv;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import knf.animeflv.Recyclers.AdapterDialogPref;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.UtilDialogPref;
import knf.animeflv.Utils.UtilSound;

/**
 * Created by Jordy on 04/09/2015.
 */
public class DialogSounds extends DialogFragment {

    MediaPlayer mp;
    Context context;

    public static DialogSounds create() {
        DialogSounds dialog = new DialogSounds();
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
        final View customView;
        try {
            customView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_pref, null);
        } catch (InflateException e) {
            throw new IllegalStateException("This device does not support Web Views.");
        }
        context = getActivity();
        mp = UtilDialogPref.getPlayer();
        try {
            if (!UtilDialogPref.getPlayer().isPlaying()) {
                try {
                    int selected = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(UtilDialogPref.getKey(), UtilDialogPref.getDef()));
                    setMediaPlayer(selected);
                    UtilSound.setCurrentMediaPlayerInt(selected);
                } catch (Exception e) {
                    e.printStackTrace();
                    setMediaPlayer(0);
                    UtilSound.setCurrentMediaPlayerInt(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            UtilDialogPref.setSelected(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(UtilDialogPref.getKey(), UtilDialogPref.getDef())));
        } catch (Exception e) {
            UtilDialogPref.setSelected(-1);
        }
        String[] array = new String[]{};
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(UtilDialogPref.getTitulo())
                .titleGravity(GravityEnum.CENTER)
                .customView(customView, false)
                .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                .canceledOnTouchOutside(true)
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        UtilDialogPref.getPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {

                            }
                        });
                    }
                })
                .build();

        if (UtilDialogPref.getLista() != null) {
            array = UtilDialogPref.getLista();
        }
        RecyclerView recyclerView = (RecyclerView) dialog.getCustomView().findViewById(R.id.dialog_pref_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new AdapterDialogPref(array, getActivity()));
        return dialog;
    }

    private void setMediaPlayer(int which) {
        if (!UtilDialogPref.getPlayer().isPlaying()) {
            mp = UtilSound.getMediaPlayer(context,which);
            mp.setLooping(true);
            UtilDialogPref.setPlayer(mp);
        }
    }
}
