package knf.animeflv.Recyclers;

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
import knf.animeflv.Directorio.AnimeClass;
import knf.animeflv.Directorio.DB.DirectoryHelper;
import knf.animeflv.Directorio.Directorio;
import knf.animeflv.R;
import knf.animeflv.Utils.CacheManager;
import knf.animeflv.Utils.DesignUtils;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.info.Helper.InfoHelper;

/**
 * Created by Jordy on 22/08/2015.
 */
public class AdapterDirPeliculaNew extends RecyclerView.Adapter<AdapterDirPeliculaNew.ViewHolder> {

    List<AnimeClass> Animes;
    private Context context;
    private ThemeUtils.Theme theme;

    public AdapterDirPeliculaNew(Context context, List<AnimeClass> animes) {
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
    public AdapterDirPeliculaNew.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).
                inflate(DesignUtils.forcePhone(context) ? R.layout.item_anime_fav_force : R.layout.item_anime_fav, parent, false);
        return new AdapterDirPeliculaNew.ViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(final AdapterDirPeliculaNew.ViewHolder holder, final int position) {
        DesignUtils.setCardStyle(context, getItemCount(), getPosition(holder.getAdapterPosition(), position), holder.card, holder.separator, holder.iv_rel);
        holder.card.setCardBackgroundColor(theme.card_normal);
        holder.tv_tit.setTextColor(theme.textColor);
        CacheManager.mini(context, Animes.get(holder.getAdapterPosition()).getAid(), holder.iv_rel);
        holder.tv_tit.setText(Animes.get(holder.getAdapterPosition()).getNombre());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoHelper.open(
                        ((Directorio) context),
                        new InfoHelper.SharedItem(holder.iv_rel, "img"),
                        Intent.FLAG_ACTIVITY_NEW_TASK,
                        new InfoHelper.BundleItem("aid", Animes.get(holder.getAdapterPosition()).getAid()),
                        new InfoHelper.BundleItem("title", Animes.get(holder.getAdapterPosition()).getNombre()),
                        new InfoHelper.BundleItem("link", DirectoryHelper.get(context).getAnimeUrl(Animes.get(holder.getAdapterPosition()).getAid()))
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
