package knf.animeflv.WaitList;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableDraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableSwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;

import java.util.List;

import knf.animeflv.ColorsRes;
import knf.animeflv.Interfaces.WaitDownloadCallback;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.TaskType;
import knf.animeflv.Utils.CacheManager;
import knf.animeflv.Utils.MainStates;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.WaitList.Costructor.WaitManager;
import knf.animeflv.WaitList.Holders.ChildHolder;
import knf.animeflv.WaitList.Holders.GroupHolder;
import knf.animeflv.info.Helper.InfoHelper;

/**
 * Created by Jordy on 31/03/2016.
 */
public class AdapterWait extends AbstractExpandableItemAdapter<GroupHolder, ChildHolder>
        implements ExpandableDraggableItemAdapter<GroupHolder, ChildHolder>,
        ExpandableSwipeableItemAdapter<GroupHolder, ChildHolder> {
    private List<WaitDBHelper.WaitObject> animes;
    private List<List<Integer>> animesCapList;
    private RecyclerViewExpandableItemManager manager;
    private RecyclerViewSwipeManager swipeManager;
    private Activity context;
    private Parser parser = new Parser();
    private WaitDownloadCallback callback;
    private ThemeUtils.Theme theme;

    AdapterWait(Activity context, RecyclerViewExpandableItemManager manager, RecyclerViewSwipeManager swipeManager) {
        setHasStableIds(true);
        this.manager = manager;
        this.swipeManager = swipeManager;
        this.context = context;
        animes = WaitManager.getAnimesList();
        animesCapList = WaitManager.getNumerosList();
        callback = (WaitDownloadCallback) context;
        theme = ThemeUtils.Theme.create(context);
    }

    @Override
    public int getGroupCount() {
        return animes.size();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return animesCapList.get(groupPosition).size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        // This method need to return unique value within all group items.
        return Integer.parseInt(animes.get(groupPosition).aid);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // This method need to return unique value within the group.
        return ((Integer.parseInt(animes.get(groupPosition).aid) * animesCapList.get(groupPosition).get(childPosition)) / 2) + 11;
    }

    @Override
    public GroupHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wait_group, parent, false);
        return new GroupHolder(v, context);
    }

    @Override
    public ChildHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wait_child, parent, false);
        return new ChildHolder(v);
    }

    @Override
    public void onBindGroupViewHolder(final GroupHolder holder, final int groupPosition, int viewType) {
        holder.card.setCardBackgroundColor(theme.card_normal);
        holder.titulo.setTextColor(theme.textColor);
        holder.delete.setColorFilter(theme.iconFilter);
        holder.start.setColorFilter(theme.iconFilter);
        new CacheManager().mini(context, animes.get(groupPosition).aid, holder.image);
        holder.titulo.setText(parser.getTitCached(animes.get(groupPosition).aid));
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoHelper.open(
                        context,
                        new InfoHelper.SharedItem(holder.image, "img"),
                        Intent.FLAG_ACTIVITY_NEW_TASK,
                        new InfoHelper.BundleItem("aid", animes.get(groupPosition).aid),
                        new InfoHelper.BundleItem("title", parser.getTitCached(animes.get(groupPosition).aid))
                );
            }
        });
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manager.isGroupExpanded(groupPosition)) {
                    manager.collapseGroup(groupPosition);
                } else {
                    manager.collapseAll();
                    manager.expandGroup(groupPosition);
                }

            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (manager.isGroupExpanded(groupPosition)) {
                        manager.collapseGroup(groupPosition);
                    }
                    animesCapList.remove(groupPosition);
                    notifyItemRemoved(groupPosition);
                    //holder.card.setVisibility(View.GONE);
                    new WaitDBHelper(context).removeList(animes.get(groupPosition).aid);
                    MainStates.init(context).delFromGlobalWaitList(animes.get(groupPosition).aid);
                    WaitManager.Refresh();
                    animes = WaitManager.getAnimesList();
                    animesCapList = WaitManager.getNumerosList();
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        holder.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(context)
                        .content("Desea empezar a descargar los " + animesCapList.get(groupPosition).size() + " capitulos?")
                        .positiveText("Descargar")
                        .negativeText("Cancelar")
                        .backgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                callback.onAllCapsDownload(animes.get(groupPosition).aid, animesCapList.get(groupPosition));
                            }
                        }).build().show();
            }
        });
    }

    private String getUrlImg(String aid) {
        return parser.getBaseUrl(TaskType.NORMAL, context) + "imagen.php?thumb=http://cdn.animeflv.net/img/portada/thumb_80/" + aid + ".jpg&certificate=" + parser.getCertificateSHA1Fingerprint(context);
    }

    @Override
    public int getGroupItemViewType(int groupPosition) {
        return 0;
    }

    @Override
    public int getChildItemViewType(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public void onBindChildViewHolder(final ChildHolder holder, final int groupPosition, final int childPosition, int viewType) {
        holder.card.setCardBackgroundColor(theme.card_normal);
        holder.cap.setTextColor(theme.textColor);
        holder.delete.setColorFilter(theme.iconFilter);
        holder.download.setColorFilter(theme.iconFilter);
        holder.cap.setText(Parser.getCap(animes.get(groupPosition).type, String.valueOf(animesCapList.get(groupPosition).get(childPosition)), animesCapList.get(groupPosition).size() > 1));
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.card.setVisibility(View.GONE);
                MainStates.init(context).delFromWaitList(animes.get(groupPosition) + "_" + animesCapList.get(groupPosition).get(childPosition) + "E");
                WaitManager.Refresh();
                animesCapList = WaitManager.getNumerosList();
                notifyDataSetChanged();
            }
        });
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onSingleCapDownload(animes.get(groupPosition).aid, animesCapList.get(groupPosition).get(childPosition));
            }
        });
    }

    public void notifyResume() {
        WaitManager.Refresh();
        animes = WaitManager.getAnimesList();
        animesCapList = WaitManager.getNumerosList();
        notifyDataSetChanged();
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(GroupHolder holder, int groupPosition, int x, int y, boolean expand) {
        return false;
    }

    @Override
    public boolean onCheckGroupCanStartDrag(GroupHolder holder, int groupPosition, int x, int y) {
        return true;
    }

    @Override
    public boolean onCheckChildCanStartDrag(ChildHolder holder, int groupPosition, int childPosition, int x, int y) {
        return false;
    }

    @Override
    public void onMoveGroupItem(int fromGroupPosition, int toGroupPosition) {
        animes.remove(fromGroupPosition);
        animes.add(toGroupPosition, animes.get(fromGroupPosition));
        MainStates.init(context).UpdateWaitList("GlobalWaiting", animes);
        WaitManager.Refresh();
        animesCapList = WaitManager.getNumerosList();
        notifyDataSetChanged();
    }

    @Override
    public void onMoveChildItem(int fromGroupPosition, int fromChildPosition, int toGroupPosition, int toChildPosition) {

    }

    @Override
    public ItemDraggableRange onGetChildItemDraggableRange(ChildHolder holder, int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public ItemDraggableRange onGetGroupItemDraggableRange(GroupHolder holder, int groupPosition) {
        return null;
    }

    @Override
    public SwipeResultAction onSwipeGroupItem(GroupHolder holder, int groupPosition, int result) {
        animes.remove(groupPosition);
        MainStates.init(context).UpdateWaitList("GlobalWaiting", animes);
        WaitManager.Refresh();
        animesCapList = WaitManager.getNumerosList();
        notifyDataSetChanged();
        return null;
    }

    @Override
    public SwipeResultAction onSwipeChildItem(ChildHolder holder, int groupPosition, int childPosition, int result) {
        return null;
    }

    @Override
    public int onGetGroupItemSwipeReactionType(GroupHolder holder, int groupPosition, int x, int y) {
        return Swipeable.REACTION_CAN_SWIPE_BOTH_H;
    }

    @Override
    public int onGetChildItemSwipeReactionType(ChildHolder holder, int groupPosition, int childPosition, int x, int y) {
        return 0;
    }

    @Override
    public void onSetGroupItemSwipeBackground(GroupHolder holder, int groupPosition, int type) {

    }

    @Override
    public void onSetChildItemSwipeBackground(ChildHolder holder, int groupPosition, int childPosition, int type) {

    }

    @Override
    public boolean onCheckGroupCanDrop(int draggingGroupPosition, int dropGroupPosition) {
        return false;
    }

    @Override
    public boolean onCheckChildCanDrop(int draggingGroupPosition, int draggingChildPosition, int dropGroupPosition, int dropChildPosition) {
        return false;
    }

    private interface Expandable extends ExpandableItemConstants {
    }

    private interface Draggable extends DraggableItemConstants {
    }

    private interface Swipeable extends SwipeableItemConstants {
    }

    public interface EventListener {
        void onGroupItemRemoved(int groupPosition);

        void onChildItemRemoved(int groupPosition, int childPosition);

        void onGroupItemPinned(int groupPosition);

        void onChildItemPinned(int groupPosition, int childPosition);

        void onItemViewClicked(View v, boolean pinned);
    }

}
