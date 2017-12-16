package knf.animeflv.Suggestions;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.captain_miao.optroundcardview.OptRoundCardView;
import com.makeramen.roundedimageview.RoundedImageView;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.R;
import knf.animeflv.Random.AnimeObject;
import knf.animeflv.Utils.CacheManager;
import knf.animeflv.Utils.DesignUtils;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.info.Helper.InfoHelper;

public class SuggestionsAdapterSticky extends SectioningAdapter {

    private Activity activity;
    private List<AnimeObject> animes = new ArrayList<>();
    private ThemeUtils.Theme theme;

    public SuggestionsAdapterSticky(Activity activity, List<AnimeObject> list) {
        this.activity = activity;
        this.animes = list;
        this.theme = ThemeUtils.Theme.create(activity);
    }

    @Override
    public int getNumberOfSections() {
        return animes.size();
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        return animes.get(sectionIndex).objects.size();
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        return true;
    }

    @Override
    public boolean doesSectionHaveFooter(int sectionIndex) {
        return false;
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemUserType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion, parent, false), activity);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerUserType) {
        return new Header(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion_header, parent, false));
    }

    @Override
    public void onBindItemViewHolder(ItemViewHolder viewHolder, final int sectionIndex, final int itemIndex, int itemUserType) {
        final SuggestionsAdapterSticky.ViewHolder holder = (ViewHolder) viewHolder;
        DesignUtils.setCardStyle(activity, getNumberOfItemsInSection(sectionIndex), itemIndex, holder.card, holder.separator, holder.iv_rel);
        holder.card.setCardBackgroundColor(theme.card_normal);
        holder.tv_tit.setTextColor(theme.textColor);
        holder.tv_tipo.setTextColor(theme.accent);
        CacheManager.mini(activity, animes.get(sectionIndex).objects.get(itemIndex).aid, holder.iv_rel);
        holder.tv_tit.setText(animes.get(sectionIndex).objects.get(itemIndex).title);
        holder.tv_tipo.setText(animes.get(sectionIndex).objects.get(itemIndex).tid);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InfoHelper.open(
                        activity,
                        new InfoHelper.SharedItem(holder.iv_rel, "img"),
                        new InfoHelper.BundleItem("title", animes.get(sectionIndex).objects.get(itemIndex).title),
                        new InfoHelper.BundleItem("aid", animes.get(sectionIndex).objects.get(itemIndex).aid)
                );
            }
        });
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder viewHolder, int sectionIndex, int headerUserType) {
        Header header = (Header) viewHolder;
        header.header.setTextColor(theme.textColorToolbar);
        header.layout.setBackgroundColor(theme.indicatorColor);
        header.header.setText(animes.get(sectionIndex).title);
    }

    @Override
    public GhostHeaderViewHolder onCreateGhostHeaderViewHolder(ViewGroup parent) {
        return super.onCreateGhostHeaderViewHolder(parent);
    }

    public static class ViewHolder extends SectioningAdapter.ItemViewHolder {
        @BindView(R.id.imgCardInfoRel)
        public RoundedImageView iv_rel;
        @BindView(R.id.tv_info_rel_tit)
        public TextView tv_tit;
        @BindView(R.id.tv_info_rel_tipo)
        public TextView tv_tipo;
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

    public static class Header extends SectioningAdapter.HeaderViewHolder {
        @BindView(R.id.header_text)
        public TextView header;
        @BindView(R.id.selectable)
        public RelativeLayout layout;

        public Header(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}