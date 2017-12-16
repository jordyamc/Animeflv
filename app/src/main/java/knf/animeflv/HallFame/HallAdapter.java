package knf.animeflv.HallFame;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.HallFame.Objects.ListItem;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.TaskType;
import knf.animeflv.Utils.CacheManager;
import knf.animeflv.Utils.ThemeUtils;

public class HallAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<ListItem> items;

    public HallAdapter(Activity activity, List<ListItem> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).isTitle() ? 0 : 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return new HallAdapter.ViewHolderTitle(LayoutInflater.from(activity).inflate(R.layout.item_hall_title, parent, false));
            case 1:
                return new HallAdapter.ViewHolderItem(LayoutInflater.from(activity).inflate(R.layout.item_hall_person, parent, false), activity);
            default:
                return new HallAdapter.ViewHolderTitle(LayoutInflater.from(activity).inflate(R.layout.item_hall_person, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder h, final int position) {
        if (getItemViewType(h.getAdapterPosition()) == 0) {
            ViewHolderTitle holder = (ViewHolderTitle) h;
            if (ThemeUtils.isAmoled(activity)) {
                holder.name.setTextColor(ColorsRes.SecondaryTextDark(activity));
            } else {
                holder.name.setTextColor(ColorsRes.SecondaryTextLight(activity));
            }
            holder.name.setText(items.get(holder.getAdapterPosition()).name);
        } else {
            ViewHolderItem holder = (ViewHolderItem) h;
            holder.description.setTextColor(ThemeUtils.getAcentColor(activity));
            if (ThemeUtils.isAmoled(activity)) {
                holder.name.setTextColor(ColorsRes.Blanco(activity));
                holder.card.setBackgroundColor(ColorsRes.Prim(activity));
            } else {
                holder.name.setTextColor(ColorsRes.Negro(activity));
                holder.card.setBackgroundColor(ColorsRes.Blanco(activity));
            }
            CacheManager.hallMini(activity, items.get(holder.getAdapterPosition()).id, getImageLink(holder.getAdapterPosition()), holder.imageView);
            holder.name.setText(items.get(holder.getAdapterPosition()).name);
            holder.description.setText(items.get(holder.getAdapterPosition()).description);
        }
    }

    private String getImageLink(int position) {
        return new Parser().getBaseUrl(TaskType.NORMAL, activity) + "hall-of-fame.php?id=" + items.get(position).id + "&image=mini";
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolderTitle extends RecyclerView.ViewHolder {
        @BindView(R.id.name)
        TextView name;

        public ViewHolderTitle(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class ViewHolderItem extends RecyclerView.ViewHolder {
        @BindView(R.id.img)
        CircularImageView imageView;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.card)
        CardView card;

        public ViewHolderItem(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("use_space", false))
                itemView.setPadding(0, 0, 0, 0);
        }
    }
}
