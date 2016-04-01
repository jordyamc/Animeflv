package knf.animeflv.WaitList.Holders;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder;

import knf.animeflv.R;

/**
 * Created by Jordy on 31/03/2016.
 */
public class GroupHolder extends AbstractDraggableSwipeableItemViewHolder implements ExpandableItemViewHolder {
    public CardView card;
    public ImageView image;
    public TextView titulo;
    public ImageButton start;
    public ImageButton delete;
    int mExpandStateFlags;
    View mContainer;

    public GroupHolder(@NonNull View itemView) {
        super(itemView);
        this.card = (CardView) itemView.findViewById(R.id.card_wait_main);
        this.image = (ImageView) itemView.findViewById(R.id.img_wait_group);
        this.titulo = (TextView) itemView.findViewById(R.id.tv_wait_Tit);
        this.start = (ImageButton) itemView.findViewById(R.id.ib_wait_start);
        this.delete = (ImageButton) itemView.findViewById(R.id.ib_wait_group_delete);
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
