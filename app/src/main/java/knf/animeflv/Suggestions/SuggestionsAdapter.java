package knf.animeflv.Suggestions;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.R;
import knf.animeflv.Random.AnimeObject;
import knf.animeflv.Utils.CacheManager;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.info.Helper.InfoHelper;

public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsAdapter.ViewHolder> {

    private Activity activity;
    private List<AnimeObject> animes = new ArrayList<>();
    private ThemeUtils.Theme theme;

    public SuggestionsAdapter(Activity activity, List<AnimeObject> list) {
        this.activity = activity;
        this.animes = list;
        this.theme = ThemeUtils.Theme.create(activity);
    }

    @Override
    public SuggestionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(activity).
                inflate(R.layout.item_suggestion, parent, false);
        return new SuggestionsAdapter.ViewHolder(itemView, activity);
    }

    @Override
    public void onBindViewHolder(final SuggestionsAdapter.ViewHolder holder, final int position) {
        holder.card.setCardBackgroundColor(theme.card_normal);
        holder.tv_tit.setTextColor(theme.textColor);
        holder.tv_tipo.setTextColor(theme.accent);
        new CacheManager().mini(activity, animes.get(getPosition(holder, position)).aid, holder.iv_rel);
        holder.tv_tit.setText(animes.get(getPosition(holder, position)).title);
        holder.tv_tipo.setText(animes.get(getPosition(holder, position)).tid);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InfoHelper.open(
                        activity,
                        new InfoHelper.SharedItem(holder.iv_rel, "img"),
                        new InfoHelper.BundleItem("title", animes.get(getPosition(holder, position)).title),
                        new InfoHelper.BundleItem("aid", animes.get(getPosition(holder, position)).aid)
                );
            }
        });
    }

    private int getPosition(ViewHolder holder, int position) {
        return holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return animes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgCardInfoRel)
        public ImageView iv_rel;
        @BindView(R.id.tv_info_rel_tit)
        public TextView tv_tit;
        @BindView(R.id.tv_info_rel_tipo)
        public TextView tv_tipo;
        @BindView(R.id.cardRel)
        public CardView card;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("use_space", false))
                iv_rel.setPadding(0, 0, 0, 0);
        }
    }

}