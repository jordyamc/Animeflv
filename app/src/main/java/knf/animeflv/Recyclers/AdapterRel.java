package knf.animeflv.Recyclers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import knf.animeflv.JsonFactory.OfflineGetter;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Utils.CacheManager;
import knf.animeflv.Utils.DesignUtils;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.info.Helper.InfoHelper;

public class AdapterRel extends RecyclerView.Adapter<AdapterRel.ViewHolder> {

    private List<String> titulosCard;
    private List<String> tiposCard;
    private String[] url;
    private String[] aids;
    private Activity context;
    private ThemeUtils.Theme theme;
    public AdapterRel(Activity context, List<String> titulos, List<String> tipos, String[] urls, String[] aid) {
        this.context = context;
        this.titulosCard = titulos;
        this.tiposCard = tipos;
        this.url = urls;
        this.aids=aid;
        this.theme = ThemeUtils.Theme.create(context);
    }

    @Override
    public AdapterRel.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).
                inflate(R.layout.item_anime_rel, parent, false);
        return new AdapterRel.ViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(final AdapterRel.ViewHolder holder, final int position) {
        holder.card.setCardBackgroundColor(theme.card_normal);
        holder.tv_tit.setTextColor(theme.textColor);
        holder.tv_tipo.setTextColor(theme.accent);
        new CacheManager().mini(context,aids[position],holder.iv_rel);
        holder.tv_tit.setText(titulosCard.get(position));
        holder.tv_tipo.setText(tiposCard.get(position));
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = FileUtil.getStringFromFile(OfflineGetter.directorio);
                String link = new Parser().getUrlFavs(json, aids[position]);
                InfoHelper.open(
                        context,
                        new InfoHelper.SharedItem(holder.iv_rel, "img"),
                        Intent.FLAG_ACTIVITY_NEW_TASK,
                        new InfoHelper.BundleItem("aid", aids[position]),
                        new InfoHelper.BundleItem("link", link),
                        new InfoHelper.BundleItem("title", titulosCard.get(position))
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return titulosCard.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_rel;
        public TextView tv_tit;
        public TextView tv_tipo;
        public CardView card;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            this.iv_rel = (ImageView) itemView.findViewById(R.id.imgCardInfoRel);
            this.tv_tit = (TextView) itemView.findViewById(R.id.tv_info_rel_tit);
            this.tv_tipo = (TextView) itemView.findViewById(R.id.tv_info_rel_tipo);
            this.card = (CardView) itemView.findViewById(R.id.cardRel);
            if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("use_space", false))
                iv_rel.setPadding(0, 0, 0, 0);
            DesignUtils.setCardSpaceStyle(context, card);
        }
    }

}