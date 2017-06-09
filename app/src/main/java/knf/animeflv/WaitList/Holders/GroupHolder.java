package knf.animeflv.WaitList.Holders;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.R;

/**
 * Created by Jordy on 31/03/2016.
 */
public class GroupHolder extends AbstractDraggableSwipeableItemViewHolder implements ExpandableItemViewHolder {
    @BindView(R.id.card_wait_main)
    public CardView card;
    @BindView(R.id.img_wait_group)
    public ImageView image;
    @BindView(R.id.tv_wait_Tit)
    public TextView titulo;
    @BindView(R.id.ib_wait_start)
    public ImageButton start;
    @BindView(R.id.ib_wait_group_delete)
    public ImageButton delete;
    public LinearLayout mContainer;
    int mExpandStateFlags;

    public GroupHolder(@NonNull View itemView, Context context) {
        super(itemView);
        //this.mContainer = (LinearLayout) itemView.findViewById(R.id.root);
        ButterKnife.bind(this, itemView);
        if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("use_space", false))
            image.setPadding(0, 0, 0, 0);
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
