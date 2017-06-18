package knf.animeflv.Recyclers;

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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.Directorio.AnimeClass;
import knf.animeflv.R;
import knf.animeflv.Utils.CacheManager;
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
                inflate(R.layout.item_anime_fav, parent, false);
        return new AdapterDirAnimeNew.ViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(final AdapterDirAnimeNew.ViewHolder holder, final int position) {
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
                        new InfoHelper.BundleItem("title", Animes.get(holder.getAdapterPosition()).getNombre())
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return Animes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img)
        public ImageView iv_rel;
        @BindView(R.id.title)
        public TextView tv_tit;
        @BindView(R.id.card)
        public CardView card;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("use_space", false))
                iv_rel.setPadding(0, 0, 0, 0);
        }
    }
}
