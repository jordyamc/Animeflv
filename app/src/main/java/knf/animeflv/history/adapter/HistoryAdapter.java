package knf.animeflv.history.adapter;

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

import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;

import org.json.JSONArray;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.R;
import knf.animeflv.Utils.CacheManager;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.info.Helper.InfoHelper;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> implements SwipeableItemAdapter<HistoryAdapter.ViewHolder> {
    HistoryAdapterInterface historyInterface;
    private Activity activity;
    private JSONArray jsonArray;
    private ThemeUtils.Theme theme;
    public HistoryAdapter(Activity activity) {
        this.activity = activity;
        jsonArray = HistoryHelper.getHistoryArray(activity);
        historyInterface = (HistoryAdapterInterface)activity;
        theme = ThemeUtils.Theme.create(activity);
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(HistoryHelper.getAidFrom(jsonArray,position));
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
        holder.card.setCardBackgroundColor(theme.card_normal);
        holder.tv_tit.setTextColor(theme.textColor);
        holder.tv_current.setTextColor(theme.accent);
        holder.tv_tit.setText(HistoryHelper.getTitFrom(jsonArray,holder.getAdapterPosition()));
        holder.tv_current.setText(HistoryHelper.getLastFrom(jsonArray,holder.getAdapterPosition()));
        new CacheManager().mini(activity,HistoryHelper.getAidFrom(jsonArray,holder.getAdapterPosition()),holder.img);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InfoHelper.open(
                        activity,
                        new InfoHelper.SharedItem(holder.img, "img"),
                        new InfoHelper.BundleItem("aid", HistoryHelper.getAidFrom(jsonArray,holder.getAdapterPosition())),
                        new InfoHelper.BundleItem("title", HistoryHelper.getTitFrom(jsonArray,holder.getAdapterPosition())),
                        new InfoHelper.BundleItem("position", HistoryHelper.getLastNumFrom(jsonArray, holder.getAdapterPosition()))
                );
            }
        });
    }

    @Override
    public SwipeResultAction onSwipeItem(ViewHolder holder, int position, int result) {
        if (result == Swipeable.RESULT_CANCELED) {
            return new SwipeResultActionDefault();
        } else {
            return new OnRemove(this, position, historyInterface);
        }
    }

    @Override
    public int onGetSwipeReactionType(ViewHolder holder, int position, int x, int y) {
        return Swipeable.REACTION_CAN_SWIPE_BOTH_H;
    }

    @Override
    public void onSetSwipeBackground(ViewHolder holder, int position, int type) {

    }

    interface Swipeable extends SwipeableItemConstants {
    }

    public interface HistoryAdapterInterface {
        void onDelete();
    }

    public static class ViewHolder extends AbstractSwipeableItemViewHolder {
        @BindView(R.id.card)
        public CardView card;
        @BindView(R.id.img)
        public ImageView img;
        @BindView(R.id.tv_tit)
        public TextView tv_tit;
        @BindView(R.id.tv_current)
        public TextView tv_current;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setAlpha(1);
            itemView.setTranslationX(0);
            itemView.setTranslationY(0);
            if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("use_space", false))
                img.setPadding(0, 0, 0, 0);
        }

        @Override
        public View getSwipeableContainerView() {
            return card;
        }
    }

    static class OnRemove extends SwipeResultActionRemoveItem {
        private HistoryAdapter adapter;
        private int position;
        private HistoryAdapterInterface adapterInterface;

        public OnRemove(HistoryAdapter adapter, int position, HistoryAdapterInterface adapterInterface) {
            this.adapter = adapter;
            this.position = position;
            this.adapterInterface = adapterInterface;
        }

        @Override
        protected void onPerformAction() {
            adapter.jsonArray = HistoryHelper.delFromList(adapter.jsonArray,position,adapter.activity);
            adapter.notifyItemRemoved(position);
            adapterInterface.onDelete();
        }
    }

}