package knf.animeflv.Changelog.Adapters;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class VersionAdapter extends RecyclerView.Adapter<VersionAdapter.ViewHolder> {

    private Activity activity;
    private List<ChangeLogObjects.Version> versions;

    public VersionAdapter(Activity activity, List<ChangeLogObjects.Version> versions) {
        this.activity = activity;
        this.versions = versions;
    }

    @Override
    public VersionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(activity).
                inflate(R.layout.item_changelog_version, parent, false);
        return new VersionAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final VersionAdapter.ViewHolder holder, final int position) {
        if (ThemeUtils.isAmoled(activity)) {
            holder.card.setCardBackgroundColor(ColorsRes.Prim(activity));
            holder.name.setTextColor(ColorsRes.SecondaryTextDark(activity));
        } else {
            holder.card.setCardBackgroundColor(ColorsRes.Blanco(activity));
            holder.name.setTextColor(ColorsRes.SecondaryTextLight(activity));
        }
        String name = versions.get(holder.getAdapterPosition()).name;
        Log.e("Version", "Name: " + name);
        holder.name.setText(name);
        holder.logs.setLayoutManager(new LinearLayoutManager(activity));
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                holder.logs.setAdapter(new LogAdapter(activity, versions.get(holder.getAdapterPosition()).logs));
            }
        });
    }

    @Override
    public int getItemCount() {
        return versions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cardMain)
        CardView card;
        @BindView(R.id.version_name)
        TextView name;
        @BindView(R.id.recycler_logs)
        RecyclerView logs;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
