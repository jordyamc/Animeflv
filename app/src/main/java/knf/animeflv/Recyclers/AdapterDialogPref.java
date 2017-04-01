package knf.animeflv.Recyclers;

import android.content.Context;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import knf.animeflv.ColorsRes;
import knf.animeflv.R;
import knf.animeflv.Utils.Keys;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.Utils.UtilDialogPref;
import knf.animeflv.Utils.UtilSound;

/**
 * Created by Jordy on 17/08/2015.
 */
public class AdapterDialogPref extends RecyclerView.Adapter<AdapterDialogPref.ViewHolder> {

    private String[] lista;
    private Context context;
    private Preference preference;

    public AdapterDialogPref(String[] lista, Context context) {
        this.lista = lista;
        this.context = context;
        this.preference = UtilDialogPref.getPreference();
    }

    @Override
    public AdapterDialogPref.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).
                inflate(R.layout.item_dialog_conf_choose, parent, false);
        return new AdapterDialogPref.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AdapterDialogPref.ViewHolder holder, final int position) {
        try {
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
            if (holder.getAdapterPosition() == UtilDialogPref.getSelected()) {
                holder.button.setChecked(true);
                holder.playing.setVisibility(View.VISIBLE);
                if (UtilDialogPref.getPlayer().isPlaying()) {
                    holder.playing.setImageResource(R.drawable.ic_stop);
                    UtilDialogPref.getPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            holder.playing.setImageResource(R.drawable.ic_play);
                        }
                    });
                } else {
                    holder.playing.setImageResource(R.drawable.ic_play);
                }
            } else {
                holder.button.setChecked(false);
                holder.playing.setVisibility(View.GONE);
            }
            holder.text.setText(lista[holder.getAdapterPosition()]);
            holder.back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.getAdapterPosition() == UtilDialogPref.getSelected()) {
                        if (UtilDialogPref.getPlayer().isPlaying()) {
                            UtilDialogPref.getPlayer().stop();
                            tooglebyPref(holder.getAdapterPosition());
                            holder.playing.setImageResource(R.drawable.ic_play);
                        } else {
                            setMediaPlayer(holder.getAdapterPosition());
                            UtilDialogPref.getPlayer().start();
                            tooglebyPref(holder.getAdapterPosition());
                            holder.playing.setImageResource(R.drawable.ic_stop);
                        }
                    } else {
                        try {
                            if (UtilDialogPref.getPlayer().isPlaying()) {
                                UtilDialogPref.getPlayer().stop();
                            }
                            setMediaPlayer(holder.getAdapterPosition());
                            UtilDialogPref.getPlayer().start();
                            updatebyPref(holder.getAdapterPosition());
                            checkRadios(holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    UtilSound.setCurrentMediaPlayerInt(holder.getAdapterPosition());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tooglebyPref(int pos) {
        int pref = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(Keys.Conf.INDICADOR_SONIDOS, "0"));
        switch (pref) {
            case 1:
                UtilSound.toogleNotSound(pos);
                break;
            case 2:
                showHideWidget();
                break;
        }
    }

    private void updatebyPref(int pos) {
        int pref = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(Keys.Conf.INDICADOR_SONIDOS, "0"));
        switch (pref) {
            case 1:
                if (!UtilSound.isNotSoundShow) {
                    UtilSound.toogleNotSound(pos);
                } else {
                    UtilSound.UpdateNotSound(pos);
                }
                break;
            case 2:
                if (!UtilSound.getAudioWidget().isShown()) {
                    UtilSound.getAudioWidget().show(100, 100);
                }
                break;
        }
    }

    private void showHideWidget() {
        if (!UtilSound.getCurrentMediaPlayer().isPlaying()) {
            UtilSound.getAudioWidget().hide();
            UtilSound.getAudioWidget().controller().stop();
        } else {
            UtilSound.getAudioWidget().show(100, 100);
            UtilSound.getAudioWidget().controller().start();
        }
    }

    private void checkRadios(int selected) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(UtilDialogPref.getKey(), String.valueOf(selected)).apply();
        preference.setSummary(lista[selected]);
        UtilDialogPref.setSelected(selected);
        notifyDataSetChanged();
    }

    private void setMediaPlayer(int which) {
        try {
            Log.d("AdapterDialog", "Reset Player");
            if (!UtilDialogPref.getPlayer().isPlaying()) {
                UtilDialogPref.setPlayer(UtilSound.getMediaPlayer(context, which));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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