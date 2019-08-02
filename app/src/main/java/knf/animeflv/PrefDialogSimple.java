package knf.animeflv;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Arrays;
import java.util.List;

import knf.animeflv.Recyclers.AdapterDialogPrefSimple;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.UtilDialogPref;

/**
 * Created by Jordy on 04/09/2015.
 */
public class PrefDialogSimple extends DialogFragment {

    Context context;

    public static PrefDialogSimple create() {
        PrefDialogSimple dialog = new PrefDialogSimple();
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
        if (UtilDialogPref.getCustom() == null) {
            UtilDialogPref.setSelected(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(UtilDialogPref.getKey(), UtilDialogPref.getDef())));
        } else {
            List<String> custom = Arrays.asList(UtilDialogPref.getCustom());
            UtilDialogPref.setSelected(custom.indexOf(PreferenceManager.getDefaultSharedPreferences(context).getString(UtilDialogPref.getKey(), UtilDialogPref.getDef())));
        }
        String[] array = new String[]{};
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(UtilDialogPref.getTitulo())
                .titleGravity(GravityEnum.CENTER)
                .customView(customView, false)
                .canceledOnTouchOutside(true)
                .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                .build();

        if (UtilDialogPref.getLista() != null) {
            array = UtilDialogPref.getLista();
        }
        RecyclerView recyclerView = (RecyclerView) dialog.getCustomView().findViewById(R.id.dialog_pref_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new AdapterDialogPrefSimple(array, getActivity(), dialog));
        return dialog;
    }
}
