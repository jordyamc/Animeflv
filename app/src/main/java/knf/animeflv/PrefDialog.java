package knf.animeflv;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
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
import knf.animeflv.Utils.UtilDialogPref;

/**
 * Created by Jordy on 04/09/2015.
 */
public class PrefDialog extends DialogFragment {

    MediaPlayer mp;
    Context context;

    public static PrefDialog create() {
        PrefDialog dialog = new PrefDialog();
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
        if (!mp.isPlaying()) {
            setMediaPlayer(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(UtilDialogPref.getKey(), UtilDialogPref.getDef())));
        }
        UtilDialogPref.setSelected(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(UtilDialogPref.getKey(), UtilDialogPref.getDef())));
        String[] array = new String[]{};
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(UtilDialogPref.getTitulo())
                .titleGravity(GravityEnum.CENTER)
                .customView(customView, false)
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
        recyclerView.setAdapter(new AdapterDialogPref(array, UtilDialogPref.getKey(), UtilDialogPref.getDef(), getActivity()));
        return dialog;
    }

    private void setMediaPlayer(int which) {
        if (!mp.isPlaying()) {
            if (which == 0) {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                mp = MediaPlayer.create(context, notification);
            }
            if (which == 1) {
                mp = MediaPlayer.create(context, R.raw.sound);
            }
            if (which == 2) {
                mp = MediaPlayer.create(context, R.raw.onii);
            }
            if (which == 3) {
                mp = MediaPlayer.create(context, R.raw.sam);
            }
            if (which == 4) {
                mp = MediaPlayer.create(context, R.raw.dango);
            }
            if (which == 5) {
                mp = MediaPlayer.create(context, R.raw.nico);
            }
            UtilDialogPref.setPlayer(mp);
        }
    }
}
