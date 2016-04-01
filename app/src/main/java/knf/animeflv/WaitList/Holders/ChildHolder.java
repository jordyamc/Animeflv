package knf.animeflv.WaitList.Holders;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder;

import knf.animeflv.R;

public class ChildHolder extends AbstractDraggableSwipeableItemViewHolder implements ExpandableItemViewHolder {
    public CardView card;
    public TextView cap;
    public ImageButton delete;
    public ImageButton download;
    int mExpandStateFlags;
    View mContainer;

    public ChildHolder(@NonNull View itemView) {
        super(itemView);
        this.card = (CardView) itemView.findViewById(R.id.card_wait_child);
        this.cap = (TextView) itemView.findViewById(R.id.tv_wait_cap);
        this.delete = (ImageButton) itemView.findViewById(R.id.ib_wait_child_del);
        this.download = (ImageButton) itemView.findViewById(R.id.ib_wait_download);
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
