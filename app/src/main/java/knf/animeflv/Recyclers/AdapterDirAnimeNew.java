package knf.animeflv.Recyclers;

import android.app.Activity;
import android.content.Context;
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
import knf.animeflv.Directorio.AnimeClass;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Utils.CacheManager;
import knf.animeflv.Utils.DesignUtils;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.info.Helper.InfoHelper;

/**
 * Created by Jordy on 22/08/2015.
 */
public class AdapterDirAnimeNew extends RecyclerView.Adapter<AdapterDirAnimeNew.ViewHolder> {

    List<AnimeClass> Animes;
    private Activity context;
    private ThemeUtils.Theme theme;
    public AdapterDirAnimeNew(Activity context, List<AnimeClass> animes) {
        this.context = context;
        this.Animes = animes;
        this.theme = ThemeUtils.Theme.create(context);
    }

    public static String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1) h = "0" + h;
            if (l > 2) h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1)) str.append(':');
        }
        return str.toString();
    }

    @Override
    public AdapterDirAnimeNew.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).
                inflate(DesignUtils.forcePhone(context) ? R.layout.item_anime_fav_force : R.layout.item_anime_fav, parent, false);
        return new AdapterDirAnimeNew.ViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(final AdapterDirAnimeNew.ViewHolder holder, final int position) {
        DesignUtils.setCardStyle(context, getItemCount(), getPosition(holder.getAdapterPosition(), position), holder.card, holder.separator, holder.iv_rel);
        holder.card.setCardBackgroundColor(theme.card_normal);
        holder.tv_tit.setTextColor(theme.textColor);
        new CacheManager().mini(context,Animes.get(holder.getAdapterPosition()).getAid(),holder.iv_rel);
        holder.tv_tit.setText(Animes.get(holder.getAdapterPosition()).getNombre());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoHelper.open(
                        context,
                        new InfoHelper.SharedItem(holder.iv_rel, "img"),
                        new InfoHelper.BundleItem("aid", Animes.get(holder.getAdapterPosition()).getAid()),
                        new InfoHelper.BundleItem("title", Animes.get(holder.getAdapterPosition()).getNombre()),
                        new InfoHelper.BundleItem("link", Parser.getUrlAnimeCached(Animes.get(holder.getAdapterPosition()).getAid()))
                );
            }
        });
    }

    private int getPosition(int holder, int pos) {
        return holder == -1 ? pos : holder;
    }

    @Override
    public int getItemCount() {
        return Animes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img)
        public RoundedImageView iv_rel;
        @BindView(R.id.title)
        public TextView tv_tit;
        @BindView(R.id.card)
        public OptRoundCardView card;
        @BindView(R.id.separator_top)
        View separator;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("use_space", false))
                iv_rel.setPadding(0, 0, 0, 0);
            DesignUtils.setCardSpaceStyle(context, card);
        }
    }
}
