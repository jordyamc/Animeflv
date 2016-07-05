package knf.animeflv.WaitList;

import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceManager;
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
    List<String> animes;
    List<List<Float>> animesCapList;
    RecyclerViewExpandableItemManager manager;
    RecyclerViewSwipeManager swipeManager;
    Activity context;
    Parser parser = new Parser();
    WaitDownloadCallback callback;

    public AdapterWait(Activity context, RecyclerViewExpandableItemManager manager, RecyclerViewSwipeManager swipeManager) {
        setHasStableIds(true);
        this.manager = manager;
        this.swipeManager = swipeManager;
        this.context = context;
        animes = WaitManager.getAnimesList();
        animesCapList = WaitManager.getNumerosList();
        callback = (WaitDownloadCallback) context;
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
        return animes.get(groupPosition).hashCode();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // This method need to return unique value within the group.
        return Math.round(animesCapList.get(groupPosition).get(childPosition) * Float.parseFloat(animes.get(groupPosition)));
    }

    @Override
    public GroupHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wait_group, parent, false);
        return new GroupHolder(v);
    }

    @Override
    public ChildHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wait_child, parent, false);
        return new ChildHolder(v);
    }

    @Override
    public void onBindGroupViewHolder(final GroupHolder holder, final int groupPosition, int viewType) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("is_amoled", false)) {
            holder.card.setCardBackgroundColor(context.getResources().getColor(R.color.prim));
            holder.titulo.setTextColor(context.getResources().getColor(R.color.blanco));
            holder.delete.setColorFilter(ColorsRes.Holo_Dark(context));
            holder.start.setColorFilter(ColorsRes.Holo_Dark(context));
        }
        //PicassoCache.getPicassoInstance(context).load(Uri.parse(getUrlImg(animes.get(groupPosition)))).error(R.drawable.ic_block_r).into(holder.image);
        new CacheManager().mini(context,animes.get(groupPosition),holder.image);
        holder.titulo.setText(parser.getTitCached(animes.get(groupPosition)));
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoHelper.open(
                        ((WaitActivity) context),
                        new InfoHelper.SharedItem(holder.image, "img"),
                        Intent.FLAG_ACTIVITY_NEW_TASK,
                        new InfoHelper.BundleItem("aid", animes.get(groupPosition)),
                        new InfoHelper.BundleItem("title", parser.getTitCached(animes.get(groupPosition)))
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
                if (manager.isGroupExpanded(groupPosition)) {
                    manager.collapseGroup(groupPosition);
                }
                holder.card.setVisibility(View.GONE);
                MainStates.delFromGlobalWaitList(animes.get(groupPosition));
                WaitManager.Refresh();
                animes = WaitManager.getAnimesList();
                animesCapList = WaitManager.getNumerosList();
                notifyDataSetChanged();
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
                                callback.onAllCapsDownload(animes.get(groupPosition), animesCapList.get(groupPosition));
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
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("is_amoled", false)) {
            holder.card.setCardBackgroundColor(context.getResources().getColor(R.color.prim));
            holder.cap.setTextColor(context.getResources().getColor(R.color.blanco));
            holder.delete.setColorFilter(ColorsRes.Holo_Dark(context));
            holder.download.setColorFilter(ColorsRes.Holo_Dark(context));
        }
        String text = "Capitulo " + animesCapList.get(groupPosition).get(childPosition);
        holder.cap.setText(text.replace(".0", ""));
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.card.setVisibility(View.GONE);
                MainStates.delFromWaitList(animes.get(groupPosition) + "_" + animesCapList.get(groupPosition).get(childPosition) + "E");
                WaitManager.Refresh();
                animesCapList = WaitManager.getNumerosList();
                notifyDataSetChanged();
            }
        });
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onSingleCapDownload(animes.get(groupPosition), animesCapList.get(groupPosition).get(childPosition));
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
        String tmp = animes.get(fromGroupPosition);
        animes.remove(fromGroupPosition);
        animes.add(toGroupPosition, tmp);
        MainStates.UpdateWaitList("GlobalWaiting", animes);
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
        MainStates.UpdateWaitList("GlobalWaiting", animes);
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
