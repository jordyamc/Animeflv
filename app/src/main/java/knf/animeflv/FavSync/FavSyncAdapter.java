package knf.animeflv.FavSync;

import android.content.Context;
import android.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.captain_miao.optroundcardview.OptRoundCardView;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.Favorites.FavObject;
import knf.animeflv.Favorites.FavotiteDB;
import knf.animeflv.R;
import knf.animeflv.Utils.CacheManager;
import knf.animeflv.Utils.DesignUtils;
import knf.animeflv.Utils.ThemeUtils;

public class FavSyncAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<FavObject> list = new ArrayList<>();
    private ThemeUtils.Theme theme;

    public FavSyncAdapter(Context context, int type, ListCountInterface countInterface) {
        this.context = context;
        this.list = FavSyncHelper.getResolved(type == 0 ? FavSyncHelper.local : FavSyncHelper.cloud);
        this.theme = ThemeUtils.Theme.create(context);
        countInterface.onListCount(list.size());
        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            default:
            case FavotiteDB.TYPE_SECTION:
                return new SectionViewHolder(inflater.inflate(DesignUtils.forcePhone(context) ? R.layout.item_anime_fav_header_force : R.layout.item_anime_fav_header, parent, false));
            case FavotiteDB.TYPE_FAV:
                return new FavViewHolder(inflater.inflate(DesignUtils.forcePhone(context) ? R.layout.item_anime_fav_force : R.layout.item_anime_fav, parent, false), context);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == -1)
            return getItemViewType(0);
        return list.get(position).isSection ? FavotiteDB.TYPE_SECTION : FavotiteDB.TYPE_FAV;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder h, final int position) {
        if (h.getItemViewType() == FavotiteDB.TYPE_SECTION) {
            final SectionViewHolder holder = (SectionViewHolder) h;
            holder.name.setText(list.get(position).name);
            holder.name.setTextColor(theme.textColor);
        } else if (h.getItemViewType() == FavotiteDB.TYPE_FAV) {
            final FavViewHolder holder = (FavViewHolder) h;
            CacheManager.mini(context, list.get(position).aid, holder.imageView);
            holder.name.setText(list.get(position).name);
            holder.name.setTextColor(theme.textColor);
            DesignUtils.setCardStyle(context, getItemCount(), getPosition(holder.getAdapterPosition(), position), holder.cardView, holder.separator, holder.imageView);
            holder.cardView.setCardBackgroundColor(theme.card_normal);
        }
    }

    private int getPosition(int holder, int pos) {
        return holder == -1 ? pos : holder;
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).id;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    interface ListCountInterface {
        void onListCount(int count);
    }

    public static class FavViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card)
        OptRoundCardView cardView;
        @BindView(R.id.img)
        RoundedImageView imageView;
        @BindView(R.id.title)
        TextView name;
        @BindView(R.id.separator_top)
        View separator;

        public FavViewHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("use_space", false))
                imageView.setPadding(0, 0, 0, 0);
            DesignUtils.setCardSpaceStyle(context, cardView);
        }
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.header_text)
        TextView name;
        @BindView(R.id.selectable)
        RelativeLayout selectable;
        @BindView(R.id.default_indicator)
        ImageView def_ind;

        public SectionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            def_ind.setVisibility(View.GONE);
        }
    }

}