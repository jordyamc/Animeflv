package knf.animeflv.Changelog.Adapters;

import android.app.Activity;
import android.support.annotation.ColorInt;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.Changelog.ChangeLogObjects;
import knf.animeflv.ColorsRes;
import knf.animeflv.R;
import knf.animeflv.Utils.ThemeUtils;
import xdroid.toaster.Toaster;

public class LogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<ChangeLogObjects.Log> logs;

    public LogAdapter(Activity activity, List<ChangeLogObjects.Log> logs) {
        this.activity = activity;
        this.logs = logs;
    }

    @Override
    public int getItemViewType(int position) {
        return logs.get(position).haveExtras ? 1 : 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return new LogAdapter.ViewHolderNormal(LayoutInflater.from(activity).inflate(R.layout.item_changelog_log, parent, false));
            case 1:
                return new LogAdapter.ViewHolderExtras(LayoutInflater.from(activity).inflate(R.layout.item_changelog_log_extras, parent, false));
            default:
                return new LogAdapter.ViewHolderNormal(LayoutInflater.from(activity).inflate(R.layout.item_changelog_log, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder h, final int position) {
        if (getItemViewType(h.getAdapterPosition()) == 0) {
            final ViewHolderNormal holder = (ViewHolderNormal) h;
            if (ThemeUtils.isAmoled(activity)) {
                holder.name.setTextColor(ColorsRes.Blanco(activity));
            } else {
                holder.name.setTextColor(ColorsRes.Negro(activity));
            }
            holder.name.setText(logs.get(holder.getAdapterPosition()).description);
            holder.card.setBackgroundColor(getCardColor(logs.get(holder.getAdapterPosition()).type));
            holder.card.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toaster.toast(getDesc(logs.get(holder.getAdapterPosition()).type));
                    return true;
                }
            });
        } else {
            final ViewHolderExtras holder = (ViewHolderExtras) h;
            if (ThemeUtils.isAmoled(activity)) {
                holder.name.setTextColor(ColorsRes.Blanco(activity));
            } else {
                holder.name.setTextColor(ColorsRes.Negro(activity));
            }
            holder.name.setText(logs.get(holder.getAdapterPosition()).description);
            holder.card.setBackgroundColor(getCardColor(logs.get(holder.getAdapterPosition()).type));
            holder.card.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toaster.toast(getDesc(logs.get(holder.getAdapterPosition()).type));
                    return true;
                }
            });
            holder.extras.setLayoutManager(new LinearLayoutManager(activity));
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    holder.extras.setAdapter(new LogAdapter(activity, logs.get(holder.getAdapterPosition()).sublist));
                }
            });
        }
    }

    @ColorInt
    private int getCardColor(ChangeLogObjects.LogType type) {
        switch (type) {
            case CORREGIDO:
                return ColorsRes.LogCorregido(activity);
            case IMPORTANTE:
                return ColorsRes.LogImportante(activity);
            case NUEVO:
                return ColorsRes.LogNew(activity);
            case NUEVO_IMPORTANTE:
                return ColorsRes.LogNew(activity);
            case CAMBIO:
                return ColorsRes.LogCambio(activity);
            default:
                return ThemeUtils.isAmoled(activity) ? ColorsRes.Prim(activity) : ColorsRes.Blanco(activity);
        }
    }

    private String getDesc(ChangeLogObjects.LogType type) {
        switch (type) {
            case CORREGIDO:
                return "Correccion";
            case IMPORTANTE:
                return "Importante";
            case NUEVO:
                return "Nuevo";
            case NUEVO_IMPORTANTE:
                return "Nuevo";
            case CAMBIO:
                return "Cambio";
            default:
                return "Sin Categoria";
        }
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    public static class ViewHolderNormal extends RecyclerView.ViewHolder {
        @BindView(R.id.log_desc)
        TextView name;
        @BindView(R.id.cardMain)
        CardView card;

        public ViewHolderNormal(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class ViewHolderExtras extends RecyclerView.ViewHolder {
        @BindView(R.id.log_desc)
        TextView name;
        @BindView(R.id.cardMain)
        CardView card;
        @BindView(R.id.recycler_extras)
        RecyclerView extras;

        public ViewHolderExtras(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
