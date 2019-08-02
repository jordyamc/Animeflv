package knf.animeflv.Recyclers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
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
import knf.animeflv.R;
import knf.animeflv.Utils.CacheManager;
import knf.animeflv.Utils.DesignUtils;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.info.Helper.InfoHelper;

/**
 * Created by Jordy on 17/08/2015.
 */
public class AdapterBusquedaNew extends RecyclerView.Adapter<AdapterBusquedaNew.ViewHolder> {

    List<AnimeClass> Animes;
    private Activity context;
    private ThemeUtils.Theme theme;

    public AdapterBusquedaNew(Activity context, List<AnimeClass> animes) {
        this.context = context;
        this.Animes = animes;
        this.theme = ThemeUtils.Theme.create(context);
    }

    @Override
    public AdapterBusquedaNew.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).
                inflate(DesignUtils.forcePhone(context) ? R.layout.item_anime_search_force : R.layout.item_anime_search, parent, false);
        return new AdapterBusquedaNew.ViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(final AdapterBusquedaNew.ViewHolder holder, final int position) {
        restartViews(holder);
        holder.card.setCardBackgroundColor(theme.card_normal);
        holder.tv_tit.setTextColor(theme.textColor);
        holder.tv_noC.setTextColor(theme.textColor);
        holder.tv_tipo.setTextColor(theme.accent);
        DesignUtils.setCardStyle(context, getItemCount(), getPosition(holder.getAdapterPosition(), position), holder.card, holder.separator, holder.iv_rel);
        CacheManager.mini(context, Animes.get(holder.getAdapterPosition()).getAid(), holder.iv_rel);
        holder.tv_tit.setText(Animes.get(holder.getAdapterPosition()).getNombre());
        holder.tv_tipo.setText(Animes.get(holder.getAdapterPosition()).getTipo());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Animes.get(holder.getAdapterPosition()).getTipo().equals("none")) {
                    SharedPreferences.Editor sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
                    sharedPreferences.putString("aid", Animes.get(holder.getAdapterPosition()).getAid()).apply();
                    InfoHelper.open(
                            context,
                            new InfoHelper.SharedItem(holder.iv_rel, "img"),
                            Intent.FLAG_ACTIVITY_NEW_TASK,
                            new InfoHelper.BundleItem("aid", Animes.get(holder.getAdapterPosition()).getAid()),
                            new InfoHelper.BundleItem("title", Animes.get(holder.getAdapterPosition()).getNombre()),
                            new InfoHelper.BundleItem("link", DirectoryHelper.get(context).getAnimeUrl(Animes.get(holder.getAdapterPosition()).getAid()))
                    );
                }
            }
        });
    }

    public void restartViews(AdapterBusquedaNew.ViewHolder holder) {
        if (Animes.get(holder.getAdapterPosition()).getTipo().equals("none")) {
            holder.tv_noC.setText("Sin resultados");
        }
        if (Animes.get(holder.getAdapterPosition()).getTipo().equals("_aid_")) {
            holder.tv_noC.setText("Escribe un ID");
        }
        if (Animes.get(holder.getAdapterPosition()).getTipo().equals("_NoNum_")) {
            holder.tv_noC.setText(Animes.get(holder.getAdapterPosition()).getNombre() + " no es un ID");
        }
        String tipo = Animes.get(holder.getAdapterPosition()).getTipo();
        if (tipo.equals("none") || tipo.equals("_aid_") || tipo.equals("_NoNum_")) {
            holder.iv_rel.setVisibility(View.GONE);
            holder.tv_tipo.setVisibility(View.GONE);
            holder.tv_tit.setVisibility(View.GONE);
            holder.tv_noC.setVisibility(View.VISIBLE);
        } else {
            holder.iv_rel.setVisibility(View.VISIBLE);
            holder.tv_tipo.setVisibility(View.VISIBLE);
            holder.tv_tit.setVisibility(View.VISIBLE);
            holder.tv_noC.setVisibility(View.GONE);
        }
    }

    private int getPosition(int holder, int pos) {
        return holder == -1 ? pos : holder;
    }

    @Override
    public int getItemCount() {
        return Animes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgCardInfoRel)
        public RoundedImageView iv_rel;
        @BindView(R.id.tv_info_rel_tit)
        public TextView tv_tit;
        @BindView(R.id.tv_info_rel_tipo)
        public TextView tv_tipo;
        @BindView(R.id.tv_b_noC)
        public TextView tv_noC;
        @BindView(R.id.cardRel)
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