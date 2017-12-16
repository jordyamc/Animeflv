package knf.animeflv.Recyclers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.captain_miao.optroundcardview.OptRoundCardView;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.Directorio.DB.DirectoryHelper;
import knf.animeflv.JsonFactory.OfflineGetter;
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
        DesignUtils.setCardStyle(context, getItemCount(), position, holder.card, holder.separator, holder.iv_rel);
        holder.card.setCardBackgroundColor(theme.card_normal);
        holder.tv_tit.setTextColor(theme.textColor);
        holder.tv_tipo.setTextColor(theme.accent);
        CacheManager.mini(context, aids[position], holder.iv_rel);
        holder.tv_tit.setText(titulosCard.get(position));
        holder.tv_tipo.setText(tiposCard.get(position));
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = FileUtil.getStringFromFile(OfflineGetter.directorio);
                String link = DirectoryHelper.get(context).getAnimeUrl(aids[position]);
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
        @BindView(R.id.imgCardInfoRel)
        public RoundedImageView iv_rel;
        @BindView(R.id.tv_info_rel_tit)
        public TextView tv_tit;
        @BindView(R.id.tv_info_rel_tipo)
        public TextView tv_tipo;
        @BindView(R.id.cardRel)
        public OptRoundCardView card;
        @BindView(R.id.separator_top)
        public View separator;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("use_space", false))
                iv_rel.setPadding(0, 0, 0, 0);
            DesignUtils.setCardSpaceStyle(context, card);
        }
    }

}