package knf.animeflv.Favorites;

import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.ColorsRes;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Utils.CacheManager;
import knf.animeflv.Utils.ThemeUtils;
import knf.animeflv.info.Helper.InfoHelper;

public class FavoriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DraggableItemAdapter<RecyclerView.ViewHolder> {
    private Activity context;
    private List<FavObject> list = new ArrayList<>();
    private boolean moving = false;
    private boolean sectionsEnabled = false;
    private boolean editing = false;
    private boolean searching = false;

    public FavoriteAdapter(Activity context) {
        this.context = context;
        this.sectionsEnabled = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("section_favs", false);
        this.list = new FavotiteDB(context).getAllSectionsExtended(true, sectionsEnabled);
        setHasStableIds(true);
    }

    public FavoriteAdapter(Activity context, String search) {
        this.context = context;
        this.sectionsEnabled = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("section_favs", false);
        this.list = new FavotiteDB(context).getAllSectionsExtended(true, sectionsEnabled, search);
        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            default:
            case FavotiteDB.TYPE_SECTION:
                return new SectionViewHolder(inflater.inflate(R.layout.item_anime_fav_header, parent, false));
            case FavotiteDB.TYPE_FAV:
                return new FavViewHolder(inflater.inflate(R.layout.item_anime_fav, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).isSection ? FavotiteDB.TYPE_SECTION : FavotiteDB.TYPE_FAV;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder h, final int position) {
        if (h.getItemViewType() == FavotiteDB.TYPE_SECTION) {
            final SectionViewHolder holder = (SectionViewHolder) h;
            holder.name.setText(list.get(position).name);
            if (ThemeUtils.isAmoled(context)) {
                holder.name.setTextColor(ColorsRes.SecondaryTextDark(context));
                holder.def_ind.setColorFilter(ColorsRes.Holo_Dark(context));
            } else {
                holder.name.setTextColor(ColorsRes.SecondaryTextLight(context));
                holder.def_ind.setColorFilter(ColorsRes.Blanco(context));
            }
            if (list.get(position).name.equals(FavoriteHelper.getDefaultSectionName(context))) {
                holder.def_ind.setImageResource(R.drawable.ic_fav_lleno);
            } else {
                holder.def_ind.setImageResource(R.drawable.ic_fav_vacio);
            }
            holder.def_ind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FavoriteHelper.setDefaultSection(context, list.get(position).name);
                    onListChanged();
                }
            });
            if (!list.get(position).name.equals(FavotiteDB.NO_SECTION))
                holder.selectable.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (!moving)
                            new MaterialDialog.Builder(context)
                                    .title("Editar seccion")
                                    .titleGravity(GravityEnum.CENTER)
                                    .autoDismiss(false)
                                    .input("Nombre de seccion", list.get(position).name, false, new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(@NonNull final MaterialDialog dialog, CharSequence input) {
                                            Log.e("Edit Section", "OnInput: " + input.toString());
                                            if (!input.toString().equals(list.get(position).name)) {
                                                new FavotiteDB(context).changeSectionName(list.get(position).name, input.toString(), new FavotiteDB.updateDataInterface() {
                                                    @Override
                                                    public void onFinish() {
                                                        dialog.dismiss();
                                                        onListChanged();
                                                    }
                                                });
                                            } else {
                                                dialog.dismiss();
                                            }
                                        }
                                    })
                                    .inputRange(3, 25)
                                    .positiveText("aceptar")
                                    .negativeText("cancelar")
                                    .neutralText("eliminar")
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            dialog.dismiss();
                                            final MaterialDialog d = new MaterialDialog.Builder(context)
                                                    .progress(true, 0)
                                                    .content("Eliminando...")
                                                    .cancelable(false)
                                                    .build();
                                            context.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    d.show();
                                                }
                                            });
                                            new FavotiteDB(context).deleteSection(list.get(position).name, new FavotiteDB.updateDataInterface() {
                                                @Override
                                                public void onFinish() {
                                                    context.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            d.dismiss();
                                                            onListChanged();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }).build().show();
                        return true;
                    }
                });
        } else if (h.getItemViewType() == FavotiteDB.TYPE_FAV) {
            final FavViewHolder holder = (FavViewHolder) h;
            new CacheManager().mini(context, list.get(position).aid, holder.imageView);
            holder.name.setText(list.get(position).name);
            if (ThemeUtils.isAmoled(context)) {
                holder.name.setTextColor(ColorsRes.SecondaryTextDark(context));
            } else {
                holder.name.setTextColor(ColorsRes.SecondaryTextLight(context));
            }
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!editing) {
                        InfoHelper.open(
                                context,
                                new InfoHelper.SharedItem(holder.imageView, "img"),
                                Intent.FLAG_ACTIVITY_NEW_TASK,
                                new InfoHelper.BundleItem("aid", list.get(position).aid),
                                new InfoHelper.BundleItem("title", list.get(position).name),
                                new InfoHelper.BundleItem("link", Parser.getUrlAnimeCached(list.get(position).aid))
                        );
                    } else {
                        final List<String> sectionNames = new FavotiteDB(context).getSectionListName();
                        if (!moving)
                            new MaterialDialog.Builder(context)
                                    .title("Seccion")
                                    .titleGravity(GravityEnum.CENTER)
                                    .items(sectionNames)
                                    .itemsCallbackSingleChoice(sectionNames.indexOf(list.get(position).section), new MaterialDialog.ListCallbackSingleChoice() {
                                        @Override
                                        public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                            if (which != sectionNames.indexOf(list.get(position).section)) {
                                                final MaterialDialog d = new MaterialDialog.Builder(context)
                                                        .content("Moviendo...")
                                                        .progress(true, 0)
                                                        .cancelable(false)
                                                        .build();
                                                d.show();
                                                FavObject section = new FavotiteDB(context).getSection(sectionNames.get(which));
                                                FavObject current = list.get(position);
                                                new FavotiteDB(context).changeFavSection(current.aid, current.order, section.sectionList.size(), current.section, sectionNames.get(which), new FavotiteDB.updateDataInterface() {
                                                    @Override
                                                    public void onFinish() {
                                                        d.dismiss();
                                                        onListChanged();
                                                    }
                                                });

                                            }
                                            return false;
                                        }
                                    }).build().show();
                    }
                }
            });
            final int dragState = holder.getDragStateFlags();
            if (((dragState & FavoriteAdapter.Draggable.STATE_FLAG_IS_UPDATED) != 0)) {
                if ((dragState & FavoriteAdapter.Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
                    holder.cardView.setCardBackgroundColor(ThemeUtils.getAcentColor(context));
                    holder.name.setTextColor(ColorsRes.Blanco(context));
                } else {
                    holder.cardView.setCardBackgroundColor(ThemeUtils.isAmoled(context) ? ColorsRes.Prim(context) : ColorsRes.Blanco(context));
                }
            }
        }
    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean e) {
        editing = e;
    }

    public void setSearching(boolean s) {
        searching = s;
    }

    @Override
    public boolean onCheckCanStartDrag(RecyclerView.ViewHolder holder, int position, int x, int y) {
        return (!list.get(holder.getAdapterPosition()).isSection) && !moving && sectionsEnabled && !searching;
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(RecyclerView.ViewHolder holder, int position) {
        return new ItemDraggableRange(1, list.size() - 1);
    }

    @Override
    public void onMoveItem(final int fromPosition, final int toPosition) {
        if (fromPosition != toPosition && !list.get(fromPosition).isSection) {
            moving = true;
            FavObject object = list.get(fromPosition);
            FavObject current = list.get(toPosition);
            if (current.isSection) {
                Log.e("FavotiteDB", "Current is Section");
                if (fromPosition < toPosition) {
                    current = new FavObject(current.name, "-1", current.name, 0);
                } else {
                    current = list.get(toPosition - 1);
                    if (current.isSection) {
                        current = new FavObject(current.name, "-1", current.name, 0);
                    } else {
                        current = new FavObject(current.name, "-1", current.section, current.order + 1);
                    }
                }
            } else {
                Log.e("FavotiteDB", "Current is Favorite");
                if (fromPosition < toPosition) {
                    if (!object.section.equals(current.section)) {
                        current.order = current.order + 1;
                    }
                }
            }
            list.remove(fromPosition);
            list.add(toPosition, object);
            notifyItemMoved(fromPosition, toPosition);
            new FavotiteDB(context).changeFavSection(object.aid, object.order, current.order, object.section, current.section, new FavotiteDB.updateDataInterface() {
                @Override
                public void onFinish() {
                    Log.e("FavoriteDB", "Finish Moving");
                    moving = false;
                    onListChanged();
                }
            });
        }
    }


    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return dropPosition != 0;
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).id;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void onListChanged() {
        list = new FavotiteDB(context).getAllSectionsExtended(true, sectionsEnabled);
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    private interface Draggable extends DraggableItemConstants {
    }

    public static class FavViewHolder extends AbstractDraggableItemViewHolder {
        @BindView(R.id.card)
        CardView cardView;
        @BindView(R.id.img)
        ImageView imageView;
        @BindView(R.id.title)
        TextView name;

        public FavViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class SectionViewHolder extends AbstractDraggableItemViewHolder {
        @BindView(R.id.header_text)
        TextView name;
        @BindView(R.id.selectable)
        RelativeLayout selectable;
        @BindView(R.id.default_indicator)
        ImageView def_ind;

        public SectionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}