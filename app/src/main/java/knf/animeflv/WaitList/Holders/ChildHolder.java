package knf.animeflv.WaitList.Holders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.captain_miao.optroundcardview.OptRoundCardView;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder;

import knf.animeflv.R;
import knf.animeflv.Utils.DesignUtils;

public class ChildHolder extends AbstractDraggableSwipeableItemViewHolder implements ExpandableItemViewHolder {
    public OptRoundCardView card;
    public TextView cap;
    public ImageButton delete;
    public ImageButton download;
    public View separator;
    int mExpandStateFlags;
    View mContainer;

    public ChildHolder(@NonNull View itemView, Context context) {
        super(itemView);
        this.card = itemView.findViewById(R.id.card_wait_child);
        this.cap = itemView.findViewById(R.id.tv_wait_cap);
        this.delete = itemView.findViewById(R.id.ib_wait_child_del);
        this.download = itemView.findViewById(R.id.ib_wait_download);
        this.separator = itemView.findViewById(R.id.separator_top);
        DesignUtils.setCardSpaceStyle(context, card);
    }

    @Override
    public int getExpandStateFlags() {
        return mExpandStateFlags;
    }

    @Override
    public void setExpandStateFlags(int flag) {
        mExpandStateFlags = flag;
    }

    @Override
    public View getSwipeableContainerView() {
        return mContainer;
    }
}
