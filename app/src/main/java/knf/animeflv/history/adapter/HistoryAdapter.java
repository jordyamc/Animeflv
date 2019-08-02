package knf.animeflv.history.adapter;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.captain_miao.optroundcardview.OptRoundCardView;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.R;
import knf.animeflv.Utils.CacheManager;
import knf.animeflv.Utils.DesignUtils;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.info.Helper.InfoHelper;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    HistoryAdapterInterface historyInterface;
    private Activity activity;
    private JSONArray jsonArray;
    private ThemeUtils.Theme theme;

    public HistoryAdapter(Activity activity) {
        this.activity = activity;
        jsonArray = HistoryHelper.getHistoryArray(activity);
        historyInterface = (HistoryAdapterInterface) activity;
        theme = ThemeUtils.Theme.create(activity);
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(HistoryHelper.getAidFrom(jsonArray, position));
    }

    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(activity).
                inflate(R.layout.item_history, parent, false);
        return new HistoryAdapter.ViewHolder(itemView, activity);
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        DesignUtils.setCardStyle(activity, getItemCount(), getPosition(holder, position), holder.card, holder.separator, holder.img);
        holder.card.setCardBackgroundColor(theme.card_normal);
        holder.tv_tit.setTextColor(theme.textColor);
        holder.tv_current.setTextColor(theme.accent);
        holder.tv_tit.setText(HistoryHelper.getTitFrom(jsonArray, holder.getAdapterPosition()));
        holder.tv_current.setText(HistoryHelper.getLastFrom(jsonArray, holder.getAdapterPosition()));
        CacheManager.mini(activity, HistoryHelper.getAidFrom(jsonArray, holder.getAdapterPosition()), holder.img);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InfoHelper.open(
                        activity,
                        new InfoHelper.SharedItem(holder.img, "img"),
                        new InfoHelper.BundleItem("aid", HistoryHelper.getAidFrom(jsonArray, holder.getAdapterPosition())),
                        new InfoHelper.BundleItem("title", HistoryHelper.getTitFrom(jsonArray, holder.getAdapterPosition())),
                        new InfoHelper.BundleItem("position", HistoryHelper.getLastNumFrom(jsonArray, holder.getAdapterPosition()))
                );
            }
        });
    }

    private int getPosition(RecyclerView.ViewHolder holder, int position) {
        return holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition();
    }

    public void onRemoved(int position) {
        jsonArray = HistoryHelper.delFromList(jsonArray, position, activity);
        notifyItemRemoved(position);
        historyInterface.onDelete();
    }

    public interface HistoryAdapterInterface {
        void onDelete();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card)
        public OptRoundCardView card;
        @BindView(R.id.img)
        public RoundedImageView img;
        @BindView(R.id.tv_tit)
        public TextView tv_tit;
        @BindView(R.id.tv_current)
        public TextView tv_current;
        @BindView(R.id.separator_top)
        View separator;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setAlpha(1);
            itemView.setTranslationX(0);
            itemView.setTranslationY(0);
            if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("use_space", false))
                img.setPadding(0, 0, 0, 0);
            DesignUtils.setCardSpaceStyle(context, card);
        }
    }

}