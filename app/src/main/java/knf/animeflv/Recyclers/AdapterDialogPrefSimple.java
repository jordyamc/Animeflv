package knf.animeflv.Recyclers;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import knf.animeflv.ColorsRes;
import knf.animeflv.R;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.UtilDialogPref;

/**
 * Created by Jordy on 17/08/2015.
 */
public class AdapterDialogPrefSimple extends RecyclerView.Adapter<AdapterDialogPrefSimple.ViewHolder> {

    private String[] lista;

    private Context context;
    private Preference preference;
    private MaterialDialog dialog;

    public AdapterDialogPrefSimple(String[] lista, Context context, MaterialDialog dialog) {
        this.lista = lista;
        this.context = context;
        this.preference = UtilDialogPref.getPreference();
        this.dialog = dialog;
    }

    @Override
    public AdapterDialogPrefSimple.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).
                inflate(R.layout.item_dialog_conf_choose, parent, false);
        return new AdapterDialogPrefSimple.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AdapterDialogPrefSimple.ViewHolder holder, final int position) {
        holder.playing.setColorFilter(ThemeUtils.getAcentColor(context));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_checked},
                            new int[]{android.R.attr.state_checked}
                    },
                    new int[]{

                            ThemeUtils.getAcentColor(context)
                            , ThemeUtils.getAcentColor(context),
                    }
            );
            holder.button.setSupportButtonTintList(colorStateList);
        }
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("is_amoled", false)) {
            holder.text.setTextColor(ColorsRes.Holo_Dark(context));
        } else {
            holder.text.setTextColor(ColorsRes.Holo_Light(context));
        }
        holder.playing.setVisibility(View.GONE);
        if (holder.getAdapterPosition() == UtilDialogPref.getSelected()) {
            holder.button.setChecked(true);
        } else {
            holder.button.setChecked(false);
        }
        holder.text.setText(lista[holder.getAdapterPosition()]);
        holder.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() != UtilDialogPref.getSelected()) {
                    checkRadios(holder.getAdapterPosition());
                }
            }
        });
    }

    private void checkRadios(int selected) {
        if (UtilDialogPref.getCustom() == null) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(UtilDialogPref.getKey(), String.valueOf(selected)).apply();
        } else {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(UtilDialogPref.getKey(), UtilDialogPref.getCustom()[selected]).apply();
        }
        preference.setSummary(UtilDialogPref.getPattern().replace("%s", lista[selected]));
        dialog.dismiss();
    }


    @Override
    public int getItemCount() {
        return lista.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public AppCompatRadioButton button;
        public RelativeLayout back;
        public TextView text;
        public ImageView playing;

        public ViewHolder(View itemView) {
            super(itemView);
            this.button = (AppCompatRadioButton) itemView.findViewById(R.id.select);
            this.back = (RelativeLayout) itemView.findViewById(R.id.Relative_dialog);
            this.text = (TextView) itemView.findViewById(R.id.tv_dialog_pref);
            this.playing = (ImageView) itemView.findViewById(R.id.playing);
        }
    }

}